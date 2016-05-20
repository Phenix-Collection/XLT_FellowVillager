package com.xianglin.fellowvillager.app.utils.audio;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * 声音调节器的管理
 * @author haiying.zhanghy@alibaba-inc.com
 */
public class AudioRegulatorManager {
    
    private static final String TAG = "AudioRegulatorManager";

	final static int apiLevel = android.os.Build.VERSION.SDK_INT;
	final static String deviceModel = android.os.Build.MODEL;
	/**
	 * 创建一个声音调节器
	 * @param context
	 * @return
	 */
	public static AudioRegulator newAudioRegulator(Context context){
		//System.out.println("deviceModel: " + deviceModel);
		AudioRegulator regulator = new AudioRegulator(context);
		LineModeSetter setter;

		//setter = new MB860ModeSetter();

		if("MB860".equals(deviceModel)) { // Motorola MB860
			setter = new MB860ModeSetter();
		}else if ("M040".equals(deviceModel)){
			setter = new MX2ModeSetter();
		}else if(isSamsungDevice()){ //三星的设备
			setter = new SamsungModeSetter();
		}else{
			setter = new DefaultModeSetter();
		}
		regulator.setLineModeSetter(setter);
		return regulator;
	}
	
	/**
	 * 默认的声音调节器：针对所有非特殊的手机
	 * @author haiying.zhanghy@alibaba-inc.com
	 *
	 */
	public static class AudioRegulator {
		
		private AudioManager audioManager; //扬声器模式与耳机模式的动态切换
		private LineModeSetter mSetter;
		private int audioMode;
		
		public AudioRegulator(Context context){
			this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if(audioManager != null){
				this.audioMode = audioManager.getMode();
			}
		}
		
		public void setLineModeSetter(LineModeSetter setter){
			this.mSetter = setter;
		}

		/**
		 * 开启手机的扬声器模式
		 */
		public void turnSpeakerphoneOn() {
			//不需要使用扬声器设备的情况下直接返回
			if(!isSpeakerphoneAvailable()){
				Log.d("TAG","turnSpeakerphoneOn不需要使用扬声器设备的情况下直接返回");
				return;
			}
			if(this.mSetter != null){
				this.mSetter.setSpeakerLineMode(this.audioManager);
			}
			this.audioManager.setSpeakerphoneOn(true);//打开扬声器
		}
		/**
		 * 关闭手机的扬声器模式，保持听筒模式
		 */
		public void turnEarPhone() {
			//不需要使用扬声器设备的情况下直接返回
			if(!isSpeakerphoneAvailable()){
				Log.d("TAG","turnEarPhone不需要使用扬声器设备的情况下直接返回");
				return;
			}
			 if(this.mSetter != null){
				this.mSetter.setEarphoneLineMode(this.audioManager);
			}
			this.audioManager.setSpeakerphoneOn(false);//关闭扬声器
		}
		
		/**
		 * 判断扬声器设备是否可以用：也就是如果声音的播放是通过耳机或者是蓝牙之类的设备播放，则不对扬声器做任何操作
		 * @return
		 */
		public boolean isSpeakerphoneAvailable(){
			if(audioManager == null){
				return false;
			}
			
			boolean result = this.audioManager.isWiredHeadsetOn() || audioManager.isBluetoothA2dpOn() || audioManager.isBluetoothScoOn();
			if(result){
				return false;
			}
			return true;
		}
		
		/**
		 * 扬声器是否开启
		 * @return
		 */
		public boolean isSpeakerphoneOn(){
			if(audioManager == null){
				return false;
			}
			return this.audioManager.isSpeakerphoneOn();
		}

		/**
		 * 手机接近脸部的扬声器的状态处理：默认应该是关闭扬声器，开启话筒模式；
		 * 如果手机已经插入耳机，则不进行任何特殊处理
		 */
		public void closeToTheFace() {
			if(isSpeakerphoneAvailable()){
				//靠近脸部的时候，播放模式切换为话筒模式
				turnEarPhone();
			}
		}

		/**
		 * 离开脸部的处理：手机在插入耳机的情况下，不进行任何处理；
		 * 未插入耳机的情况下，根据传入的扬声器是否开启与否来决定是打开扬声器还是关闭扬声器
		 */
		public void stayAwayFromFace(boolean isTurnOnSpeaker) {
			if(isSpeakerphoneAvailable()){
				//离开脸部的时候，根据用户本身的选择开启扬声器或者是进入到话筒模式
				if(isTurnOnSpeaker){
					this.turnSpeakerphoneOn();
				}else{
					this.turnEarPhone();
				}
			}
		}

		/**
		 * 将扬声器的开启与关闭状态设置到最初的状态
		 */
		public void resetSpeakerToOriginal() {
			if(audioManager == null){
				return;
			}
			//退出页面后，还原页面上设置的声音内容
			this.audioManager.setMode(AudioManager.MODE_NORMAL);
			this.audioManager.setSpeakerphoneOn(false);
		}
	}
	
	public static interface LineModeSetter{
		
		public void setSpeakerLineMode(AudioManager audioManager);
		
		public void setEarphoneLineMode(AudioManager audioManager);
	}
	
	/**
	 * 是否是三星的设备
	 * @return
	 */
	public final static boolean isSamsungDevice(){
		String brand = android.os.Build.BRAND;
		if(brand != null){
			return "samsung".equalsIgnoreCase(brand);
		}
		return false;
	}
	
	//设置听筒模式
	private static void setAudioManagerModeIncall(AudioManager audioManager){
		if(audioManager == null){
			return;
		}
		if (audioManager.getMode() != AudioManager.MODE_IN_CALL) {
			audioManager.setMode(AudioManager.MODE_IN_CALL);
		}
	}
	
	//扬声器
	private static void setAudioManagerModeNormal(AudioManager audioManager){
		if(audioManager == null){
			return;
		}
		if (audioManager.getMode() == AudioManager.MODE_IN_CALL) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
		}
	}
	
	public static class DefaultModeSetter implements LineModeSetter{
		@Override
		public void setSpeakerLineMode(AudioManager audioManager) {
			if ((3 == apiLevel) || (4 == apiLevel)) {
				audioManager.setMode(AudioManager.MODE_NORMAL);
			}else{
				if(isLowerApi()){
					audioManager.setMode(AudioManager.MODE_IN_CALL);
				} else{
					setAudioManagerModeNormal(audioManager);
				}
			}
		}

		@Override
		public void setEarphoneLineMode(AudioManager audioManager) {
			if ((3 == apiLevel) || (4 == apiLevel)) {
				audioManager.setMode(AudioManager.MODE_IN_CALL);
			}else{
				if(isLowerApi()){
					audioManager.setMode(AudioManager.MODE_NORMAL);
				} else{
					setAudioManagerModeIncall(audioManager);
				}
			}
		}
	}
	
	public final static boolean isLowerApi(){
		return apiLevel >= 5 && apiLevel <= 7;
	}
	/**
	 * 三星手机的设置
	 * @author haiying.zhanghy@alibaba-inc.com
	 *
	 */
	public static class SamsungModeSetter implements LineModeSetter{
		
		@Override
		public void setSpeakerLineMode(AudioManager audioManager) {
			if(isLowerApi()){
				audioManager.setMode(AudioManager.MODE_IN_CALL);
			} else{
				//其他设备以及三星2.2之上的设备则不做任何语音线路的调整
				setAudioManagerModeNormal(audioManager);
			}
		}

		@Override
		public void setEarphoneLineMode(AudioManager audioManager) {
			if(isLowerApi()){
				audioManager.setMode(AudioManager.MODE_NORMAL);
			} else{
				//其他设备以及三星2.2之上的设备则不做任何语音线路的调整
				setAudioManagerModeIncall(audioManager);
			}
		}
		
	} 
	
	/**
	 * 魅族mx2的声音适配
	 * @author haiying.zhanghy@alibaba-inc.com
	 *
	 */
	public static class MX2ModeSetter implements LineModeSetter{

		@Override
		public void setSpeakerLineMode(AudioManager audioManager) {
			setAudioManagerModeNormal(audioManager);
		}

		@Override
		public void setEarphoneLineMode(AudioManager audioManager) {
			setAudioManagerModeIncall(audioManager);
		}
		
	}
	
	/**
	 * Motorola MB860
	 * @author haiying.zhanghy@alibaba-inc.com
	 *
	 */
	public static class MB860ModeSetter implements LineModeSetter{

		@Override
		public void setSpeakerLineMode(AudioManager audioManager) {
			audioManager.setMode(AudioManager.MODE_NORMAL);
		}

		@Override
		public void setEarphoneLineMode(AudioManager audioManager) {
			if(apiLevel>=11){
				audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
  			 }
		}
	}
}
