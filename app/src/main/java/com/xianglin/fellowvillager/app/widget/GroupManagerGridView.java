package com.xianglin.fellowvillager.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 项目名称：乡邻小站
 * 类描述：
 * 创建人：何正纬
 * 创建时间：2015/11/27 9:51
 * 修改人：hezhengwei
 * 修改时间：2015/11/27 9:51
 * 修改备注：
 */
public class GroupManagerGridView extends GridView{

    public GroupManagerGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GroupManagerGridView(Context context) {
        super(context);
    }

    public GroupManagerGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}






