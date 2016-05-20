package com.xianglin.fellowvillager.app.chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.chat.model.PhotoModel;
import com.xianglin.fellowvillager.app.chat.utils.Util;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.ImageUtils;

/**
 * @author Aizaz AZ
 */

public class PhotoItem extends LinearLayout
        implements OnCheckedChangeListener, OnLongClickListener, View.OnClickListener {

    private RelativeLayout itemLayout;
    private SimpleDraweeView ivPhoto;
    private RelativeLayout cbLayout;
    private CheckBox cbPhoto;
    private onPhotoItemCheckedListener listener;
    private PhotoModel photo;
    private boolean isCheckAll;
    private onItemClickListener l;
    private int position;
    private PhotoSelectorActivity context;
    private PhotoItem(Context context) {
        super(context);
        if(context instanceof PhotoSelectorActivity){
            this.context= (PhotoSelectorActivity) context;
        }
    }

    public PhotoItem(final Context context, onPhotoItemCheckedListener listener) {
        this(context);
        LayoutInflater.from(context).inflate(R.layout.layout_photoitem, this,
                true);
        this.listener = listener;

        setOnLongClickListener(this);
        itemLayout = (RelativeLayout) findViewById(R.id.gallery_item_layout);
        ivPhoto = (SimpleDraweeView) findViewById(R.id.iv_photo_lpsi);
        cbLayout = (RelativeLayout) findViewById(R.id.cb_layout);
        cbPhoto = (CheckBox) findViewById(R.id.cb_photo_lpsi);

        cbPhoto.setOnCheckedChangeListener(this); // CheckBox选中状态改变监听器
        itemLayout.setOnClickListener(this);
        cbLayout.setOnClickListener(this);
//        ivPhoto.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View paramView) {
//
//
//                if (PhotoItem.this.context != null && PhotoItem.this.context.selected != null && PhotoItem.this.context.selected.size() >= 9) {
//                    if(!cbPhoto.isChecked())BaseActivity.tip("最多选择9张");
//                    cbPhoto.setChecked(false);
//                    return;
//                }
//                if (photo != null && photo.getOriginalPath().equals("default")) {
//                    Util.selectPicFromCamera(context);
//                    return;
//                }
//                if (cbPhoto.isChecked()) {
//                    cbPhoto.setChecked(false);
//                } else {
//                    cbPhoto.setChecked(true);
//                }
//            }
//        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked && PhotoItem.this.context != null && PhotoItem.this.context.selected != null && PhotoItem.this.context.selected.size() >= 9) {
            if(context instanceof BaseActivity) {
                context.tip("最多选择9张");
            }
            cbPhoto.setChecked(false);
            return;
        }
        if (!isCheckAll) {
            listener.onCheckedChanged(photo, buttonView, isChecked); // 调用主界面回调函数
        }
        // 让图片变暗或者变亮
        if (isChecked) {
            setDrawingable();
            ivPhoto.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        } else {
            ivPhoto.clearColorFilter();
        }
        photo.setChecked(isChecked);
    }

    /**
     * 设置路径下的图片对应的缩略图
     */
    public void setImageDrawable(final PhotoModel photo) {
        this.photo = photo;
        final float picSizeInDp = 118f;
        final int thumbSize = DeviceInfoUtil.dip2px(picSizeInDp);
        if (photo.getOriginalPath().equals("default")) {
            ImageLoader.getInstance().displayImage(
                    "drawable://" + R.drawable.camera_icon, ivPhoto,ImageUtils.getOpetionPhotoSelect());
            cbPhoto.setVisibility(View.INVISIBLE);
        } else {
            /*用fresco加载本地图片*/
            Uri uri = Uri.parse("file://" + photo.getOriginalPath());
            ImageRequest request = ImageRequestBuilder
                    .newBuilderWithSource(uri)
                    .setResizeOptions(
                            new ResizeOptions(
                                    thumbSize,
                                    thumbSize
                            )
                    )
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(ivPhoto.getController())
                    .build();

            ivPhoto.setController(controller);

            cbPhoto.setVisibility(View.VISIBLE);
        }
    }

    private void setDrawingable() {
        ivPhoto.setDrawingCacheEnabled(true);
        ivPhoto.buildDrawingCache();
    }

    @Override
    public void setSelected(boolean selected) {
        if (photo == null) {
            return;
        }
        isCheckAll = true;
        cbPhoto.setChecked(selected);
        isCheckAll = false;
    }

    public void setOnClickListener(onItemClickListener l, int position) {
        this.l = l;
        this.position = position;
    }

     @Override
     public void
     onClick(View v) {
         switch (v.getId()) {
             case R.id.gallery_item_layout: {
                 if (l != null) {
                     l.onItemClick(position);
                 }
             }
             break;
             case R.id.cb_layout: {
                 if (PhotoItem.this.context != null && PhotoItem.this.context.selected != null && PhotoItem.this.context.selected.size() >= 9) {
                    if(!cbPhoto.isChecked()) {
                        if (context instanceof BaseActivity)
                        context.tip("最多选择9张");
                    }
                    cbPhoto.setChecked(false);
                    return;
                }
                if (photo != null && photo.getOriginalPath().equals("default")) {
                    Util.selectPicFromCamera(context);
                    return;
                }
                 if (cbPhoto.isChecked()) {
                    cbPhoto.setChecked(false);
                } else {
                    cbPhoto.setChecked(true);
                }
             }
             break;
         }

     }

    /**
     * 图片Item选中事件监听器
     */
    public  interface onPhotoItemCheckedListener {
        void onCheckedChanged(
                PhotoModel photoModel,
                CompoundButton buttonView,
                boolean isChecked
        );
    }

    /**
     * 图片点击事件
     */
    public interface onItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public boolean onLongClick(View v) {
        if (l != null)
            l.onItemClick(position);
        return true;
    }

}
