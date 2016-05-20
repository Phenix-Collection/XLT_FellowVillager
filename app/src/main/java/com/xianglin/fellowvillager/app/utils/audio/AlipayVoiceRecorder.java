package com.xianglin.fellowvillager.app.utils.audio;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder.OutputFormat;
import android.net.Uri;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.xianglin.fellowvillager.app.utils.audio.AudioRegulatorManager.AudioRegulator;

import java.io.File;

/**
 * 
 * @author liangzi.dlz
 *
 */
public class AlipayVoiceRecorder extends VoiceRecorder implements SensorEventListener {

    private static final String TAG                 = "AlipayVoiceRecorder";
    private Context             mContext;



    /** 用于管理与适配播放过程中扬声器的打开与关闭*/
    private AudioRegulator      mAudioRegulator;
    /** 传感觉器管理，用于手机移动到耳朵旁边时,扬声器模式自动切换为话筒模式*/
    private SensorManager       mSensorManager;
    /** 距离传感器*/
    private Sensor              mProximity;
    /** 监听电话状态*/
    private TelephonyManager    telephony;
    /** 是否在距离范围内（是否已经激活近脸模式）*/
    private boolean             isDistanceInRange   = false;
    /** 距离阈值*/
    private static final float  PROXIMITY_THRESHOLD = 5.0f;
    /** 是否有电话接入*/
    private boolean             hasPhoneCall        = false;
    /** 部分手机传感器的注册耗时，故在子线程注册*/
    private Thread              sensorRegisterThread;
    private Thread              sensorUnregisterThread;
    /** 用于录音时监听是否到达最大时限*/
    private Handler mHandler;
    /** 保存录音文件对象*/
    private File                voiceFile           = null;


    public interface MaxRecordDurationListener {
        public void onMaxRecordDurationCompleted();
    }

    public interface MinRecordDurationListener {
        public void onMinRecordDurationCompleted();
    }

    public AlipayVoiceRecorder(Context context) {
        Log.d(TAG, "AlipayVoiceRecorder onCreate");
        this.mContext = context;
        this.mAudioRegulator = AudioRegulatorManager.newAudioRegulator(context);
        initParameter();
        mHandler = new Handler();
    }

    private void initParameter() {
        this.mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        // 取得距離感應器
        this.mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    }


    /**
     * 开启手机的扬声器模式
     */
    public void  turnSpeakerphoneOn(){
        mAudioRegulator.turnSpeakerphoneOn();
    }

    /**
     * 关闭手机的扬声器模式，保持听筒模式
     */
    public void turnEarPhone() {
        mAudioRegulator.turnEarPhone();
    }
    /**
     * 前台注册事件监听,业务方可在onResume()中调用
     */
    public void onForeground() {
        //注册距离传感器监听
        sensorRegisterThread = new Thread() {
            public void run() {
                if (mSensorManager != null && mProximity != null) {
                    try {
                        mSensorManager.registerListener(AlipayVoiceRecorder.this, mProximity,
                            SensorManager.SENSOR_DELAY_NORMAL);
                        Log.d(TAG, "alipayvoicerecorder oninit");
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        };
        sensorRegisterThread.setName("sensorRegisterThread");
        sensorRegisterThread.start();
        //监听通话状态
        if (telephony != null) {
            telephony.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

    }

    /**
     * 后台取消事件监听并停止录音、播放,业务方可在onPause()中调用
     */
    public void onBackgound() {
        //业务压后台后，停止录音和播放
        stop();
        voiceFile=null;
        //目的在于：离开当前页面的时候，在非拨打电话的模式下，要将语音的输出模式恢复为之前的模式
        if (!hasPhoneCall) { // 当没有有电话呼入时,恢复speaker的默认设置
            Log.d(TAG, "has no phone call");
            this.mAudioRegulator.resetSpeakerToOriginal();
        } else {
            Log.d(TAG, "has phone call");
        }
        sensorUnregisterThread = new Thread() {
            public void run() {
                if (mSensorManager != null) {
                    try {
                        if (sensorRegisterThread != null) {
                            sensorRegisterThread.join();
                        }
                    } catch (InterruptedException e) {
                        Log.w(TAG, e);
                        e.printStackTrace();
                    }
                    try {
                        mSensorManager.unregisterListener(AlipayVoiceRecorder.this);
                        Log.d(TAG, "alipayvoicerecorder ondestroy");
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        };
        sensorUnregisterThread.setName("sensorUnregisterThread");
        sensorUnregisterThread.start();
        //取消通话监听
        if (telephony != null) {
            telephony.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        cancelTimer();
    }

    //来电监听设置标志位
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

                                                       @Override
                                                       public void onCallStateChanged(int state,
                                                                                      String incomingNumber) {
                                                           Log.d(TAG, "Call state changed: "
                                                                            + state);
                                                           switch (state) {
                                                               case TelephonyManager.CALL_STATE_RINGING:
                                                                   hasPhoneCall = true;
                                                                   break;
                                                               case TelephonyManager.CALL_STATE_IDLE:
                                                                   hasPhoneCall = false;
                                                                   break;
                                                               case TelephonyManager.CALL_STATE_OFFHOOK:
                                                                   hasPhoneCall = true;
                                                                   break;
                                                           }
                                                           super.onCallStateChanged(state,
                                                               incomingNumber);
                                                       }
                                                   };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //监听距离传感器变化来设置播音的模式：扬声器or听筒
    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.d(TAG, "on sensor changed");

        float distance = event.values[0];

        isDistanceInRange = (distance >= 0.0 && distance < PROXIMITY_THRESHOLD && distance < event.sensor
            .getMaximumRange());

        // 如果在录音时，直接返回
        if (VoiceRecorder.RECORDING_STATE == state())
            return;

        if (isDistanceInRange) {
            //将语音输出模式调整为接近脸部的模式
            Log.d(TAG, "close to face");
            mAudioRegulator.closeToTheFace();
        } else {
            Log.d(TAG, "stay away from face");
            mAudioRegulator.stayAwayFromFace(true);
        }
    }

    /**
     * 获取音频文件时长
     * @param context
     * @param f 音频文件对象
     * @return 音频时长，单位秒 返回-1时文件不存在
     */
    public int getVoiceFileDuration(Context context, File f) {
        if (null != f && f.exists()) {
            MediaPlayer player = MediaPlayer.create(context, Uri.fromFile(f));
            int duration = (int) (player.getDuration() / 1000 + 0.5f);
            if (null != player) {
                player.release();
            }
            return duration;
        }
        return -1;
    }

    private void setTimer(long duration, final MaxRecordDurationListener listener) {
    	cancelTimer();
    	maxRecordDurationNotifier.setListener(listener);
    	mHandler.postDelayed(maxRecordDurationNotifier, duration);
    }

    private void cancelTimer() {
    	maxRecordDurationNotifier.setListener(null);
        mHandler.removeCallbacks(maxRecordDurationNotifier);
    }
    
    private MaxRecordDurationNotifier maxRecordDurationNotifier = new MaxRecordDurationNotifier();
    
    /**
     * 超时提示使用postDelay方式实现
     * @author dongxinyu.dxy
     *
     */
    private class MaxRecordDurationNotifier implements Runnable {

    	private MaxRecordDurationListener mListener;
    	
    	void setListener(MaxRecordDurationListener listener) {
    		mListener = listener;
    	}
    	
		@Override
		public void run() {
            Log.d(TAG, "max record duration completed");
            stopRecording();
            if (mListener != null) {
            	mListener.onMaxRecordDurationCompleted();
            }
        }
    	
    }

    /**
     * 开始录音
     * @param context
     * @param audioFile amr文件
     * @param duration 最长录音时间 小于等于0时，最长时间,单位秒
     * @param listener 最长录音时间完成的回调
     */
    public void startRecordingWithDuration(Context context, File audioFile, int duration,
                                     MaxRecordDurationListener listener) {
        if(null==audioFile){
            return;
        }
        voiceFile=audioFile;
        startRecording(OutputFormat.RAW_AMR, audioFile, context);
        if (duration > 0) {
            
            setTimer(duration * 1000L, listener);
        }
    }

    /**
     * 停止录音
     * @param listener 录音时间过短回调
     * @return 录音时长
     */
    public int stopRecordingWithDuration(MinRecordDurationListener listener) {
        stopRecording();
        if (sampleLength() < 1) {
            listener.onMinRecordDurationCompleted();
            try {
                if (null != voiceFile && voiceFile.exists()) {
                    voiceFile.delete();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        voiceFile=null;
        cancelTimer();
        return sampleLength();
    }

    /**
     * 取消录音，删除录音文件
     * 
     */
    public void cancelRecordingWithDuration() {
        stopRecording();
        try {
            if (null != voiceFile && voiceFile.exists()) {
                voiceFile.delete();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        voiceFile=null;
        cancelTimer();
    }

//    /**
//     * 播放本地音频文件
//     * @param audioFile amr文件
//     * @param preparedListener 播放前的回调
//     * @param listener 完成播放的回调
//     */
//    public void alipayPlay(File audioFile, OnPreparedListener preparedListener,
//                           OnCompletionListener listener) {
//        play(audioFile, preparedListener, listener);
//    }
//
//    /**
//     * 播放uri音频文件
//     * @param uri
//     * @param preparedListener 播放前的回调
//     * @param listener 完成播放的回调
//     */
//    public void alipayPlay(Uri uri, OnPreparedListener preparedListener,
//                           OnCompletionListener listener) {
//        play(uri, preparedListener, listener);
//    }
//
//    /**
//     * 停止播放
//     */
//    public void alipayStopPlay() {
//        stopPlay();
//    }
}
