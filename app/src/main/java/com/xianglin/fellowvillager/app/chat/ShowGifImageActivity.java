package com.xianglin.fellowvillager.app.chat;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.mobile.common.info.DeviceInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;

/**
 * 显示Gif大图页面
 * Created by zhanglisan on 2016/2/22.
 */
@Fullscreen
@EActivity(R.layout.activity_show_gif_image)
public class ShowGifImageActivity extends BaseActivity {

    @ViewById(R.id.gifImage)
    SimpleDraweeView touchImageView;
    @Extra
    String imgPath;

    @AfterViews
    void initView(){
        if(imgPath.contains(".")){ // 全路径

            ImageUtils.setImageOrDrawee(
                    touchImageView,
                    imgPath,
                    null,
                    DeviceInfo.getInstance().getmScreenWidth(),
                    DeviceInfo.getInstance().getmScreenHeight()
            );
        }else{ // 非全路径
            String path = ImageUtils.getLocalImagePath(
                    FileUtils.IMG_SAVE_PATH,
                    imgPath
            ).replace("file://", "");
            ImageUtils.setImageOrDrawee(
                    touchImageView,
                    path,
                    null,
                    DeviceInfo.getInstance().getmScreenWidth(),
                    DeviceInfo.getInstance().getmScreenHeight()
            );
        }
    }

    @Click(R.id.gifImage)
    void imgClick(){
        finish();
    }
}
