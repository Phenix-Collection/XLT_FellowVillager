package com.xianglin.fellowvillager.app.utils.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class VoiceRecorder implements OnCompletionListener, OnErrorListener {
    
    private static final String TAG = "VoiceRecorder";

    static final String SAMPLE_PREFIX = "recording";
    static final String SAMPLE_PATH_KEY = "sample_path";
    static final String SAMPLE_LENGTH_KEY = "sample_length";

    public static final int IDLE_STATE = 0;
    public static final int RECORDING_STATE = 1;
    public static final int PLAYING_STATE = 2;
    public static final int PAUSE_STATE = 3;

    int mState = IDLE_STATE;

    public static final int NO_ERROR = 0;
    public static final int SDCARD_ACCESS_ERROR = 1;
    public static final int INTERNAL_ERROR = 2;
    public static final int IN_CALL_RECORD_ERROR = 3;

    private StateChangedListener mStateChangedListener = null;
    private VolumnChangedListener mVolumnChangedListener = null;
    private VoicePlayCompletionListener mVoicePlayCompletionListener = null;
    private VoiceProgressListener mVoiceProgressListener = null;

    private long mSampleStart = 0; // time at which latest record or play operation started
    private int mSampleLength = 0; // length of current sample

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private final Handler mHandler = new Handler();
    private Timer mTimer;    
    private TimerTask mTimerTask; 
    public  int EVERY_TIME = 150;
    
    private final int DEFAULT_PALY_MODE = -100;

    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    public VoiceRecorder() {
        Log.d(TAG, "VoiceRecorder onCreate");
    }

    public int getMaxAmplitude() {
        if (mState != RECORDING_STATE)
            return 0;
        return mRecorder.getMaxAmplitude();
    }
    /**
     * 监听录音，播放的状态,状态分两类
     * onStateChanged: IDLE_STATE;RECORDING_STATE;PLAYING_STATE;PAUSE_STATE
     * onError: NO_ERROR;SDCARD_ACCESS_ERROR;INTERNAL_ERROR;IN_CALL_RECORD_ERROR
     * @param listener
     */
    public void setStateChangedListener(StateChangedListener listener) {
        mStateChangedListener = listener;
    }

    /**
     * 监听录音音量的变化
     * @param mVolumnChangedListener
     */
    public void setmVolumnChangedListener(VolumnChangedListener mVolumnChangedListener) {
        this.mVolumnChangedListener = mVolumnChangedListener;
    }

    /**
     * 监听播放结束
     * @param listener
     */
    public void setmVoicePlayCompletionListener(VoicePlayCompletionListener listener) {
        mVoicePlayCompletionListener = listener;
    }
    
    /**
     * 监听播放进度
     * @param listener
     */
    public void setmVoiceProgressListener(VoiceProgressListener listener) {
    	mVoiceProgressListener = listener;
    }

    /**
     * 当前的状态
     * @return
     */
    public int state() {
        return mState;
    }

    public int progress() {

        if (mState == RECORDING_STATE || mState == PLAYING_STATE) {
            return (int) ((System.currentTimeMillis() - mSampleStart) / 1000);
        }

        return 0;
    }

    public int sampleLength() {
        return mSampleLength;
    }

    /**
     * Resets the recorder state. If a sample was recorded, the file is left on
     * disk and will be reused for a new recording.
     */
    public void clear() {
        stop();

        mSampleLength = 0;

        signalStateChanged(IDLE_STATE);
    }

    public void startRecording(int outputfileformat, File audioFile, Context context) {
        try{
            stop();

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(outputfileformat);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(audioFile.getAbsolutePath());

            // Handle IOException
            try {
                mRecorder.prepare();
            } catch (IOException exception) {
                Log.e(TAG, exception.getMessage());
                setError(INTERNAL_ERROR);
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
                return;
            }
            // Handle RuntimeException if the recording couldn't start
            try {
                mRecorder.start();

                startVolumnListener();

            } catch (RuntimeException exception) {
                Log.e(TAG, exception.getMessage());
                AudioManager audioMngr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                boolean isInCall = ((audioMngr.getMode() == AudioManager.MODE_IN_CALL));
                if (isInCall) {
                    setError(IN_CALL_RECORD_ERROR);
                } else {
                    setError(INTERNAL_ERROR);
                }
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
                return;
            }
            mSampleStart = System.currentTimeMillis();
            setState(RECORDING_STATE);
        } catch(RuntimeException e){
            Log.e(TAG, e.getMessage());
            setError(INTERNAL_ERROR);
        }
    }

    public void stopRecording() {
        if (mRecorder == null)
            return;

		try {
			mRecorder.stop();
		} catch (RuntimeException exception) {
			Log.e(TAG, exception.getMessage());
		}
        mRecorder.release();
        mRecorder = null;

        mSampleLength = (int) ((System.currentTimeMillis() - mSampleStart) / 1000);
        setState(IDLE_STATE);
    }

    public void play(Uri uri, OnPreparedListener preparedListener, OnCompletionListener listener) {
        doPlay(null, uri, null, preparedListener, listener, DEFAULT_PALY_MODE);
    }

    public void play(File audioFile, OnPreparedListener preparedListener, OnCompletionListener listener) {
        doPlay(audioFile, null, null, preparedListener, listener, DEFAULT_PALY_MODE);
    }
    
    public void play(FileDescriptor fileDes, OnPreparedListener preparedListener, OnCompletionListener listener) {
        doPlay(null, null, fileDes, preparedListener, listener, DEFAULT_PALY_MODE);
    }
    
    public void play(FileDescriptor fileDes, OnPreparedListener preparedListener, OnCompletionListener listener, int streamtype) {
        doPlay(null, null, fileDes, preparedListener, listener, streamtype);
    }
    
    private void doPlay(File audioFile, Uri uri, FileDescriptor fileDes, OnPreparedListener preparedListener,
                        final OnCompletionListener completionListener,int streamtype) {

        stop();

        mPlayer = new MediaPlayer();
        if (DEFAULT_PALY_MODE != streamtype)
            mPlayer.setAudioStreamType(streamtype);
        try {
            
            mPlayer.reset();

            if (audioFile != null) {
                //解决非sdcard存储区不能播放的问题
                FileInputStream fis = new FileInputStream(audioFile);
            	if(fis.getFD() != null){
            		mPlayer.setDataSource(fis.getFD());
            	}else{
            		mPlayer.setDataSource(audioFile.getAbsolutePath());
            	}
                fis.close();
            } else if (uri != null) {
                try{
                	FileInputStream fis = new FileInputStream(uri.getPath());
                	if(fis.getFD() != null){
                		mPlayer.setDataSource(fis.getFD());
                	}else{
                		mPlayer.setDataSource(uri.getPath());
                	}
                    fis.close();
                }catch(Throwable tr){
                	 mPlayer.setDataSource(uri.getPath());
                }
            } else if(fileDes != null) {
                mPlayer.setDataSource(fileDes);
            }else {
                return;
            }

            if (preparedListener != null) {

                mPlayer.setOnPreparedListener(preparedListener);
            }

            if (completionListener != null) {
                mPlayer.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {

                        VoiceRecorder.this.onCompletion(mp);
                        completionListener.onCompletion(mp);
                        stopProgress();
                    }
                });
            } else {
                mPlayer.setOnCompletionListener(this);
            }

            mPlayer.setOnErrorListener(this);

            mPlayer.prepare();
            timerShowPragress();
            mPlayer.start();
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage());
            setError(INTERNAL_ERROR);
            mPlayer = null;
            return;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            setError(SDCARD_ACCESS_ERROR);
            mPlayer = null;
            return;
        } catch (IllegalStateException e){
            Log.e(TAG, e.getMessage());
            setError(INTERNAL_ERROR);
            mPlayer=null;
            return;
        }

        mSampleStart = System.currentTimeMillis();
        setState(PLAYING_STATE);
    }

    public void stopPlay() {
        if (mPlayer == null) // we were not in playback
            return;
        mPlayer.stop();
        mPlayer.release();
        stopProgress();
        mPlayer = null;
        setState(IDLE_STATE);
    }

    public void pausePlay() {
        if (mPlayer == null) // we were not in playback
            return;
        mPlayer.pause();
        stopProgress();
        setState(PAUSE_STATE);
    }
    
    public void resumePlay() {
        if (mPlayer == null) // we were not in playback
            return;
        mPlayer.start();
        timerShowPragress();
        setState(PLAYING_STATE);
    }

    public void stop() {
        stopRecording();
        stopPlay();
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        stop();
        setError(SDCARD_ACCESS_ERROR);
        return true;
    }

    public void onCompletion(MediaPlayer mp) {
        stop();
        if (this.mVoicePlayCompletionListener != null) {
            this.mVoicePlayCompletionListener.onVoicePlayCompleted();
        }
    }

    private void setState(int state) {
        if (state == mState)
            return;

        mState = state;
        signalStateChanged(mState);
    }
    
    private void timerShowPragress(){
    	mTimer = new Timer();    
	    mTimerTask = new TimerTask() {    
	       @Override    
	       public void run() {     
	        	showPlayPragress();
	       }    
	    };   
	    mTimer.schedule(mTimerTask, 0, EVERY_TIME);
   }
    
    private void showPlayPragress(){
    	try{
	    	 if(mVoiceProgressListener != null){
	    		 if(mPlayer != null && mPlayer.isPlaying() && mPlayer.getDuration() > 0 && mPlayer.getCurrentPosition() > 0){
	    			 mVoiceProgressListener.onVoiceProgress(mPlayer.getDuration(), mPlayer.getCurrentPosition());
	    		 }
	         }
    	}catch(Exception e){
    		Log.e("VoiceRecorde", "show play pragress error" + e.getMessage());
    	}
    }
    
    public void stopProgress(){
    	stopTimer();
    }
    
    private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
	}
    /**
     */
    private void updateMicStatus() {

        if (this.mRecorder != null && this.mVolumnChangedListener != null) {

            int volumnLevle = 49 * this.mRecorder.getMaxAmplitude() / 32768;

            this.mVolumnChangedListener.onVolumnChanged(volumnLevle);

            if (this.state() == RECORDING_STATE) {

                startVolumnListener();
            }
        }
    }

    private void startVolumnListener() {

        this.mHandler.postDelayed(mUpdateMicStatusTimer, 100);
    }

    private void signalStateChanged(int state) {

        if (mStateChangedListener != null) {
            mStateChangedListener.onStateChanged(state);
        }
    }

    private void setError(int error) {

        if (mStateChangedListener != null) {

            mStateChangedListener.onError(error);
        }
    }

    public interface StateChangedListener {

        public void onStateChanged(int state);

        public void onError(int error);
    }

    public interface VolumnChangedListener {

        public void onVolumnChanged(int levle);
    }

    //TODO：优化播放器
    public interface VoicePlayCompletionListener {
        public void onVoicePlayCompleted();
    }

 	public interface VoiceProgressListener {
        public void onVoiceProgress(int duration, int currentPosition);
    }

}
