package com.xianglin.fellowvillager.app.chat;

import android.content.Context;
import android.widget.AbsListView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;

/**
 * 相册选择页item
 * Created by zhanglisan on 16/1/25.
 */
public class GalleryPhotoItem extends PhotoItem {

    /** 相册中每行item数量 */
    private static final int HORIZENTAL_NUM = 3;

    public GalleryPhotoItem(Context context, onPhotoItemCheckedListener listener) {
        super(context, listener);

        int horizentalSpace = context.getResources().getDimensionPixelSize(
                R.dimen.sticky_item_horizontalSpacing
        );
        int itemWidth = (DeviceInfoUtil.getWidth(context) - (horizentalSpace * (HORIZENTAL_NUM - 1)))
                / HORIZENTAL_NUM;
        AbsListView.LayoutParams itemLayoutParams = new AbsListView.LayoutParams(itemWidth, itemWidth);
        setLayoutParams(itemLayoutParams);
    }
}
