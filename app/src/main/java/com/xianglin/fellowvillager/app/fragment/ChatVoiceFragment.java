package com.xianglin.fellowvillager.app.fragment;

import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity;
import com.xianglin.fellowvillager.app.chat.controller.SendMsgController;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.DialogManager;
import com.xianglin.fellowvillager.app.utils.FileSizeUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.SoundUtil;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.File;


/**
 */
@EFragment(R.layout.fragment_chat_menu_voice)
public class ChatVoiceFragment extends BaseFragment {

    @ViewById(R.id.voice_time)
    TextView voice_time;//录音时长

    @ViewById(R.id.voice_delete_tip)
    TextView voice_delete_tip;

    @ViewById(R.id.voice_line)
    ImageView voice_line;

    @ViewById(R.id.voice_delete)
    ImageView voice_delete;

    @ViewById(R.id.voice_recorder)
    ImageView voice_recorder;

    @ViewById(R.id.voice_hold)
    TextView voice_hold;

    String TAG = "ChatVoiceFragment";

    private long startRecord, endRecord;
    private boolean isCancelRecord = false;
    private Handler mHandler = new Handler();
    private DialogManager dialogManager;
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = SoundUtil.getInstance().getAmplitude();
            endRecord = System.currentTimeMillis();
            if (endRecord - startRecord > 60000) {
                tip("语音时间过长，为您自动发送");
                SoundUtil.getInstance().stopRecord();
                int voiceLength = (int) (endRecord - startRecord) / 1000;
                sendVoice(FileUtils.VOICE_CACHE_PATH, SoundUtil.getInstance().getVoiceName(), voiceLength, false);
                mHandler.removeCallbacks(mPollTask);
            } else {
                if(recorderState==SoundUtil.RECORDER_SUCCESS)
                    voice_time.setText(
                        getString(
                                R.string.two_words,
                                String.valueOf((int) (endRecord - startRecord) / 1000),
                                "\""
                        )
                );
                dialogManager.updateVoiceLevel((int) amp);
                mHandler.postDelayed(mPollTask, 50);
            }
        }
    };
    Rect mTrect = new Rect();

    int recorderState;
    int audioRecorderOP=-1;
    long fileSize;
    String voicePath;
    @AfterViews
    void init() {
        //voice_delete.getHitRect(Trect);
        //voice_delete.getLocalVisibleRect(Trect);
//        voice_delete.getLocationOnScreen

        dialogManager = new DialogManager(mContext);

        voice_recorder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        LogCatLog.e(TAG, "录音开始");
                        //int permission=ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO);
                        //LogCatLog.e("Test", "录音开始 permission="+permission);
                        //ToastUtils.toastForShort(v.getContext(), "开始录音");
                        voice_recorder.setImageResource(R.drawable.chat_voice_btn_focus);
                        voice_hold.setVisibility(View.INVISIBLE);
                        voice_line.setVisibility(View.VISIBLE);
                        voice_time.setVisibility(View.VISIBLE);
                        voice_delete.setVisibility(View.VISIBLE);
                        if (mBaseActivity instanceof ChatMainActivity) {
                            ((ChatMainActivity) mBaseActivity).hideInput4Voice();
                        }
                        Utils.muteAudioFocus(mBaseActivity, true);

                        startRecord = System.currentTimeMillis();
                        recorderState = SoundUtil.getInstance().startRecord(
                                mContext,
                                FileUtils.VOICE_CACHE_PATH);
                        SoundUtil.getInstance().stopPlayer();//录音时关闭语音播放
                        voicePath=FileUtils.VOICE_CACHE_PATH + SoundUtil.getInstance().getVoiceName();
                        audioRecorderOP = DeviceInfoUtil.checkOp(mContext, 27);
                        LogCatLog.e(TAG,"voicePath=" + voicePath+",audioRecorderOP="+audioRecorderOP+",recorderState="+recorderState);
                        if(audioRecorderOP == 1||recorderState==SoundUtil.RECORDER_FORBID
                                ||!FileUtils.getInstance().isExists(
                                voicePath)
                                ){
                            recorderState=SoundUtil.RECORDER_FORBID;
                            dialogManager.showRecordingDialog();
                            dialogManager.recordForbid();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialogManager.dimissDialog();
                                }
                            }, 500);
                            SoundUtil.getInstance().releaseRecord();
                            return true;
                        }else if(recorderState==SoundUtil.RECORDER_FAILED){
                            dialogManager.showRecordingDialog();
                            dialogManager.recordFail();
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialogManager.dimissDialog();
                                }
                            }, 500);
                            SoundUtil.getInstance().releaseRecord();
                            return true;
                        }else{
                            mHandler.postDelayed(mPollTask, 200);
                        }


                        break;
                    case MotionEvent.ACTION_MOVE:


                        voice_delete.getGlobalVisibleRect(mTrect);
//                        if (event.getY() < 0) {
//                            dialogManager.wantToCancel();
//                            isCancelRecord = true;
//                        } else {
//                            dialogManager.recording();
//                            isCancelRecord = false;
//                        }
                        if (mTrect.contains(x, y)) {
                            voice_delete.setImageResource(R.drawable.chat_voice_delete_focus);
                            voice_delete_tip.setVisibility(View.VISIBLE);
                            voice_time.setVisibility(View.GONE);
                        } else {
                            voice_delete.setImageResource(R.drawable.chat_voice_delete_default);
                            voice_delete_tip.setVisibility(View.GONE);
                            voice_time.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        LogCatLog.e(TAG, "录音结束");
                        voice_recorder.setImageResource(R.drawable.chat_voice_btn_default);
                        voice_line.setVisibility(View.INVISIBLE);
                        voice_delete.setVisibility(View.INVISIBLE);
                        voice_time.setVisibility(View.INVISIBLE);
                        voice_delete_tip.setVisibility(View.INVISIBLE);
                        voice_time.setText("0\"");
                        voice_hold.setVisibility(View.VISIBLE);
                        voice_delete.setImageResource(R.drawable.chat_voice_delete_default);
                        if (mBaseActivity instanceof ChatMainActivity) {
                            ((ChatMainActivity) mBaseActivity).showInput4Voice();
                        }

                        endRecord = System.currentTimeMillis();
                        //SoundUtil.getInstance().stopRecord();
                        Utils.muteAudioFocus(mContext, false);
                        mHandler.removeCallbacks(mPollTask);

                        //LogCatLog.e(TAG, "endRecord" + endRecord + ",x=" + x + ",y=" + y
                        //       + ",rectX=" + mTrect.centerX() + ",rectY=" + mTrect.centerY());
                        if (mTrect.contains(x, y)) {
                            isCancelRecord = true;
                        } else {
                            isCancelRecord = false;
                        }

                        if(recorderState==SoundUtil.RECORDER_SUCCESS){

                            if (endRecord - startRecord < 1000) {
                                dialogManager.showRecordingDialog();
                                dialogManager.tooShort();
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialogManager.dimissDialog();
                                    }
                                }, 1500);
                                SoundUtil.getInstance().releaseRecord();

                            } else if (endRecord - startRecord > 60000) {
                                LogCatLog.e(TAG, "录音时长超时-松开");
                            } else {
                                SoundUtil.getInstance().stopRecord();
                                fileSize= FileSizeUtil.getAutoFileOrFilesSize(voicePath);
                                LogCatLog.e(TAG,"recorderSize="+fileSize);
                                if (!isCancelRecord
                                        && FileUtils.getInstance().isExists(
                                        voicePath
                                )&&fileSize>0&&audioRecorderOP!=1) {
                                    final int voiceLength = (int) (endRecord - startRecord) / 1000;
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            sendVoice(FileUtils.VOICE_CACHE_PATH,
                                                    SoundUtil.getInstance().getVoiceName(), voiceLength, false);
                                        }
                                    }, 100);

                                }
                            }
                        }



                        break;
                    case MotionEvent.ACTION_CANCEL:
                        // 处理第一次获取录音权限时弹出权限对话框导致的录音失败问题
                        LogCatLog.e(TAG, "录音取消");
                        isCancelRecord = true;
                        SoundUtil.getInstance().stopRecord();
                        mHandler.removeCallbacks(mPollTask);
                        voice_line.setVisibility(View.INVISIBLE);
                        voice_delete.setVisibility(View.INVISIBLE);
                        voice_time.setVisibility(View.INVISIBLE);
                        voice_delete_tip.setVisibility(View.INVISIBLE);
                        voice_time.setText("0\"");
                        voice_hold.setVisibility(View.VISIBLE);
                        voice_delete.setImageResource(R.drawable.chat_voice_delete_default);
                        voice_recorder.setImageResource(R.drawable.chat_voice_btn_default);
                        if (mBaseActivity instanceof ChatMainActivity) {
                            ((ChatMainActivity) mBaseActivity).showInput4Voice();
                        }
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 发送语音
     *
     * @param filePath
     * @param fileName
     * @param length
     * @param isResend
     */
    private void sendVoice(final String filePath, final String fileName, final int length, boolean isResend) {
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        try {
            final String msgkey = System.currentTimeMillis() + "";
            LogCatLog.e(TAG, "filePath=" + filePath + fileName);
            MessageBean bean=MessageBean.createVoiceSendMessage(filePath,fileName,length);
            if (mBaseActivity instanceof ChatMainActivity) {
                SendMsgController.getInstance().sendChatVoice(bean, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
