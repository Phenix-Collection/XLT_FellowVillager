package com.xianglin.fellowvillager.app.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.xianglin.appserv.common.service.facade.model.UserFigureDTO;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.zxing.camera.CameraManager;
import com.zxing.decoding.CaptureActivityHandler;
import com.zxing.decoding.InactivityTimer;
import com.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Vector;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class CaptureActivity extends BaseActivity implements Callback {

    private static final long VIBRATE_DURATION = 200L;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private Button cancelScanButton;
    private TopView mTopView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        cancelScanButton = (Button) this.findViewById(R.id.btn_cancel_scan);
        hasSurface = false;
        mTopView = (TopView) findViewById(R.id.topview);
        mTopView.initCommonTop(R.string.activity_sweep_title);
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        // quit the scan view
        cancelScanButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CaptureActivity.this.finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        // FIXME

        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(CaptureActivity.this, "二维码扫描失败，请重试", Toast.LENGTH_SHORT).show();
        } else {
            if (resultString.startsWith("http://") || resultString.startsWith("https://")) {
                Intent resultIntent = new Intent(this, WebviewActivity_.class);
                resultIntent.putExtra("url", resultString);
                startActivity(resultIntent);
            } else if (resultString.startsWith("xl://")) {
                resultString = resultString.substring(5);
                //&前面是0-加好友; &前面是1-加群
                if (resultString.split("&")[0].equals("0")) {
                    getUserFigureByToken(resultString.split("&")[1]);
                } else {
                }
            } else {
                Toast.makeText(CaptureActivity.this, "非法链接", Toast.LENGTH_SHORT).show();
            }
            CaptureActivity.this.finish();
        }
    }

    /**
     * 根据二维码获取联系人详情
     */
    void getUserFigureByToken(final String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SyncApi.getInstance().getUserFigureByToken(token,
                        CaptureActivity.this, new SyncApi.CallBack<UserFigureDTO>() {
                            @Override
                            public void success(UserFigureDTO mode) {
                                UserDetailBeforeChatActivity_
                                        .intent(CaptureActivity.this)
                                        .extra("serializable", mode)
                                        .extra("gowhere", BorrowConstants.QRCODE_SCAN)
                                        .toChatXlId(mode.getUserId())
                                        .toChatId(mode.getFigureId())
                                        .titleName(mode.getNickName())
                                        .gender(mode.getGender())
                                        .headerImgId(mode.getAvatarUrl())
                                        .toChatName(mode.getNickName())
                                        .chatType(BorrowConstants.CHATTYPE_SINGLE)
                                        .start();
                            }

                            @Override
                            public void failed(String errTip, int errCode) {
                                tip(errTip);
                            }
                        }
                );
            }
        }).start();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tip("请开启相机权限!");
                }
            });
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

}