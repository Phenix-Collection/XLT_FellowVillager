package com.xianglin.fellowvillager.app.chat.model;

/**
 * 语音播放状态
 * Created by zhanglisan on 16/4/1.
 */
public enum VoicePlayStatus {
    /**未播放*/
    UN_PLAY(0),
    /**播放过*/
    PLAYED(1),
    /**播放完毕*/
    PLAY_DONE(2);

    private int value = 0;

    VoicePlayStatus(int value) {
        this.value = value;
    }

    public static VoicePlayStatus valueOf(int value) {
        switch (value) {
            case 0:
                return UN_PLAY;
            case 1:
                return PLAYED;
            case 2:
                return PLAY_DONE;
            default:
                return UN_PLAY;
        }
    }

    public int value() {
        return this.value;
    }
}
