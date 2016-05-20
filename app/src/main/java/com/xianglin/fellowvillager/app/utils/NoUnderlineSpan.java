package com.xianglin.fellowvillager.app.utils;

import android.text.TextPaint;
import android.text.style.UnderlineSpan;

/**
 *
 *无下划线Span
 * @author chengshengli
 * @version v 1.0.0 2015/12/5 17:56 XLXZ Exp $
 */
public class NoUnderlineSpan extends UnderlineSpan {
    private int resColor;
    private boolean showUnderLine;
    public NoUnderlineSpan( int resColor,boolean showUnderLine){
        this.resColor=resColor;
        this.showUnderLine=showUnderLine;
    }


    @Override
    public void updateDrawState(TextPaint ds) {
        //ds.setColor(ds.linkColor);
        //ds.setUnderlineText(false);
        ds.setColor(resColor);
        ds.setUnderlineText(showUnderLine);
    }
}
