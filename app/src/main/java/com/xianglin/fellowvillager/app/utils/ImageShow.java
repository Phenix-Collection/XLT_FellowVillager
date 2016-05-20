package com.xianglin.fellowvillager.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.mobile.common.filenetwork.listener.FileMessageListener;
import com.xianglin.mobile.common.filenetwork.model.FileTask;
import com.xianglin.xlappcore.common.service.facade.vo.UserLocationVo;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * This is an example of
 * Javadoc
 *
 * @author james
 * @version 0.1, 2015-12-25
 */
public class ImageShow {

    private Map<String, ImageView> maps;

    public ImageShow(Map<String, ImageView> maps) {
        this.maps = maps;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://下载成功
//                    messageU.loadImage(imgView,"file://"+FileUtils.IMG_CACHE_HEADIMAGE_PATH + fileTask.fileName,
//                            activity.getResources().getDrawable(R.drawable.head));
                    try {
                        InputStream inputStream = new FileInputStream(new File(
                                FileUtils.IMG_CACHE_HEADIMAGE_PATH + msg.getData().getString("fileName")));
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        String fileId = msg.getData().getString("fileId");
                        ImageView imageView = maps.get(fileId);
                        if(imageView!=null)imageView.setImageBitmap(bitmap);
                    } catch (Exception e) {

                    }
                    break;
                case 2://下载失败
                    String fileId = msg.getData().getString("fileId");
                    ImageView imageView = maps.get(fileId);
                    if(imageView!=null)imageView.setImageResource(R.drawable.head);
                    break;
            }
        }
    };

    public void downloadImageHeader(Context context, List<UserLocationVo> list, final ImageView imgView, final int position) {

        maps.put(list.get(position).getImgId() + "", imgView);
        final String imageid=list.get(position).getImgId() + "";
        FileUtils.downloadFile(context, PersonSharePreference.getUserID(),imageid
                , FileUtils.IMG_CACHE_HEADIMAGE_PATH, new FileMessageListener<FileTask>() {
                    @Override
                    public void success(int statusCode, FileTask fileTask) {
                       String id= (String)imgView.getTag();
                        if(imageid.equals(id)) {
                            return;
                        }

                        imgView.setTag(imageid);

                        Message message = new Message();
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("fileName", fileTask.fileName);
                        bundle.putString("fileId", fileTask.fileID);
                        message.setData(bundle);
                        handler.sendMessage(message);
                    }

                    @Override
                    public void handleing(int statusCode, FileTask fileTask) {

                    }

                    @Override
                    public void failure(int statusCode, FileTask fileTask) {
                        Message message = new Message();
                        message.obj = imgView;
                        message.what = 2;
                        handler.sendMessage(message);
                    }
                });
    }
}
