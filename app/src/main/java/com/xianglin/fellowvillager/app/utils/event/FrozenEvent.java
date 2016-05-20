package com.xianglin.fellowvillager.app.utils.event;

import com.xianglin.mobile.common.logging.LogCatLog;

/**
 * Created by yangjibin on 16/3/15.
 */
public class FrozenEvent {

    private boolean isFrozen;
    private String frozenFigureId;

    public FrozenEvent(String mFigureId, boolean mStatus) {
        frozenFigureId = mFigureId;
        isFrozen = mStatus;
    }

    public boolean isFrozen(){
        return isFrozen;
    }

    public String getFigureId(){
        return frozenFigureId;
    }
}
