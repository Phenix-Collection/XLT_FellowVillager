package com.xianglin.fellowvillager.app.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

import com.xianglin.mobile.common.logging.LogCatLog;

import java.io.File;


/**
 * @desc:声音工具类，包括录音，播放录音等
 */
public class SoundUtil {
    private static final double EMA_FILTER = 0.6;
    private static SoundUtil INSTANCE;
    private static MediaRecorder mMediaRecorder;
    private double mEMA = 0.0;
    private MediaPlayer mMediaPlayer;
    private String voicePath,voiceName;
    private int recorderStatus=-1;
    public static  final int RECORDER_BEGIN=0;
    public static  final int RECORDER_END=1;
    private String TAG=SoundUtil.class.getSimpleName();
    private SoundUtil() {
    }

    public int getRecorderStatus(){
        return recorderStatus;
    }

    public String getVoiceName(){
        return voiceName;
    }

    public static SoundUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (SoundUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SoundUtil();
                }
            }
        }

        return INSTANCE;
    }

    /**
     * 初始化
     */
    public void initMedia() throws Exception {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder
                    .setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.setAudioEncodingBitRate(64);
            mMediaRecorder.setAudioSamplingRate(8000);
        }
    }


    public static final int RECORDER_SUCCESS=0;//录音成功
    public static final int RECORDER_FAILED=1;//录音失败
    public static final int RECORDER_FORBID=2;////录音禁止
    /**
     * 开始录音
     * 
     * @param path
     *            声音存储的路径
     */
    public int startRecord(Context context,String path) {
        int reccord_state=RECORDER_SUCCESS;
        try {
            initMedia();
        } catch (Exception e1) {
            e1.printStackTrace();
            Toast.makeText(context, "麦克风不可用",Toast.LENGTH_SHORT).show();
        }
        //StringBuilder sb = getFilePath(context, name);
        voicePath=path;
        voiceName=System.currentTimeMillis() + ".amr";
        LogCatLog.e(TAG, "录音路径:" + path + voiceName);
        recorderStatus=RECORDER_BEGIN;
        try {
            mMediaRecorder.setOutputFile(path+voiceName);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mEMA = 0.0;
            reccord_state=RECORDER_SUCCESS;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            reccord_state=RECORDER_FAILED;
        }catch (RuntimeException e){
            reccord_state=RECORDER_FORBID;
            e.printStackTrace();
            //FileUtils.getInstance().deleteFile(path + name);
        }catch (Throwable e){
            reccord_state=RECORDER_FORBID;
        e.printStackTrace();
        //FileUtils.getInstance().deleteFile(path + name);
        }
        return reccord_state;
    }

    // public StringBuilder getFilePath(Context context, String name) {
    // StringBuilder sb = new StringBuilder();
    // sb.append("/storage/emulated/0");
    // sb.append("/");
    // sb.append(name);
    // return sb;
    // }

    public StringBuilder getFilePath(Context context, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(getDiskFielsDir(context));
        sb.append(File.separator);
        sb.append(name);
        return sb;
    }


    /**
     * 停止录音
     */
    public void stopRecord() throws IllegalStateException {
        try {

            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        }catch (Exception e){
            //FileUtils.getInstance().deleteFile(voicePath+voiceName);
        }
        recorderStatus=RECORDER_END;
        if(voiceCompletionListener!=null)voiceCompletionListener.complete();
    }

    public void releaseRecord(){
        try {

            if (mMediaRecorder != null) {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        }catch (Exception e){
            //FileUtils.getInstance().deleteFile(voicePath+voiceName);
        }
    }

    /**
     * 获得缓存路径
     * 
     * @param name
     * @return
     */
    // public String getDiskCacheDir(Context context) {
    // String cachePath;
    // if (Environment.MEDIA_MOUNTED.equals(Environment
    // .getExternalStorageState())
    // || !Environment.isExternalStorageRemovable()) {
    // cachePath = context.getExternalCacheDir().getPath();
    // } else {
    // cachePath = context.getCacheDir().getPath();
    // }
    // return cachePath;
    // }
    /**
     * 获取录音地址
     * 
     * @param context
     * @return
     */
    public String getDiskFielsDir(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            File file=new File(FileUtils.VOICE_CACHE_PATH);
            if(file.exists()){
                file.mkdirs();
            }
            cachePath=FileUtils.VOICE_CACHE_PATH;
//            cachePath = context.getExternalFilesDir(
//                    Environment.DIRECTORY_MUSIC).getPath();
        } else {
            cachePath = context.getFilesDir().getPath();
        }
        return cachePath;
    }

    /**
     * 获得缓存路径
     * 
     * @return
     */
    // public String getDiskFielsDir(Context context) {
    // String cachePath;
    // if (Environment.MEDIA_MOUNTED.equals(Environment
    // .getExternalStorageState())
    // || !Environment.isExternalStorageRemovable()) {
    // File file = null;
    // if (file == null) {
    // File musicFile = context.getExternalFilesDir(
    // Environment.DIRECTORY_MUSIC);
    // if(musicFile==null){
    //
    // }
    // String path = context.getExternalFilesDir(
    // Environment.DIRECTORY_MUSIC).getPath();
    // file = new File(path);
    // }
    // if (!file.exists()) {
    // file.mkdirs();
    // }
    // cachePath = file.getPath();
    // } else {
    // cachePath = context.getFilesDir().getPath();
    // }
    // return cachePath;
    // }

    public double getAmplitude() {
        if (mMediaRecorder != null)
            return (mMediaRecorder.getMaxAmplitude() / 2700.0);
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    public interface  VoiceCompletionListener{
        void complete();
    }
    public  VoiceCompletionListener voiceCompletionListener;
    public void setVoiceCompletionListener(VoiceCompletionListener voiceCompletionListener){
        this.voiceCompletionListener=voiceCompletionListener;
    }

    /**
     * @Description
     * @param filePath
     */
    public void playRecorder(Context context, String filePath) {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }

        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.reset();
            File file = new File(filePath);
            if (file.exists()) {
                mMediaPlayer.setDataSource(filePath);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                mMediaPlayer
                        .setOnCompletionListener(new OnCompletionListener() {
                            public void onCompletion(MediaPlayer mp) {
                                LogCatLog.e(TAG, "播放方程");
                                mMediaPlayer.release();
                                mMediaPlayer = null;
                                if(voiceCompletionListener!=null)voiceCompletionListener.complete();
                            }
                        });
                mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        if(voiceCompletionListener!=null)voiceCompletionListener.complete();
                        return false;
                    }
                });
            } else {
                // 不存在语音文件
                LogCatLog.e(TAG, "不存在语音文件");
            }

        } catch (Exception e) {
            e.printStackTrace();
            if(voiceCompletionListener!=null)voiceCompletionListener.complete();
        }

    }

    public  boolean isPlaying(){
        if(mMediaPlayer==null)
            return false;
        return mMediaPlayer.isPlaying();

    }

    /**
     * 语音播放停止
     */
    public void stopPlayer(){
        if(mMediaPlayer==null){
            if(voiceCompletionListener!=null)voiceCompletionListener.complete();
            return;
        }
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            if(voiceCompletionListener!=null)voiceCompletionListener.complete();
        }
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
}