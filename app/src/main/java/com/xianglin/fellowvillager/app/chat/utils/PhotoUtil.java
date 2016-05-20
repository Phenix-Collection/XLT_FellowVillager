package com.xianglin.fellowvillager.app.chat.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.xianglin.fellowvillager.app.chat.model.PhotoModel;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.fragment.ChatPictureFragment;
import com.xianglin.fellowvillager.app.utils.BitmapUtils;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.api.BackgroundExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天页面本地图片处理工具类
 * Created by zhanglisan on 16/3/31.
 */
public class PhotoUtil {

    private static final String TAG = "PhotoUtil";

    public static void initBitmap(final Context context) {

        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {

            @Override
            public void execute() {
                int windowHieght = Utils.dipToPixel(context, 235) * 5 / 6;
                List<PhotoModel> list = getSystemPhotoList(context);
                if (list != null && (BorrowConstants.pathList == null || BorrowConstants.pathList.size() == 0)) {
                    for (PhotoModel model : list) {
                        if (!TextUtils.isEmpty(model.getOriginalPath())) {
                            if (model.getOriginalPath().equals("add") || model.getOriginalPath().equals("more")) {
                                BorrowConstants.pathList.add(model);
                                continue;
                            }
                        }

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        //设置为true,表示解析Bitmap对象，该对象不占内存
                        options.inJustDecodeBounds = true;
                        String path = model.getOriginalPath();
                        BitmapFactory.decodeFile(path, options);


                        Bitmap bitmap = null;
                        try {
                            int bitmapWidth = options.outWidth;
                            int bitmapHeight = options.outHeight;
                            int degree = BitmapUtils.readPictureDegree(path);
                            switch (degree) {
                                case 90:
                                case 270:
                                    int num = bitmapHeight;
                                    bitmapHeight = bitmapWidth;
                                    bitmapWidth = num;
                                    break;
                                default:
                                    break;
                            }
                            if (bitmapHeight != 0) {
                                bitmap = BitmapUtils.rotatingBitmap(
                                        degree,
                                        getSmallBitmap(
                                                path,
                                                windowHieght / 3,
                                                bitmapWidth * Utils.dipToPixel(context, 235) * 5 / bitmapHeight / 6 / 3
                                        )
                                );
                            }

                        } catch (Exception e) {
                            LogCatLog.e(TAG, "bitmap is null", e);
                        }
                        if (bitmap != null) {
                            model.setmBitmap(bitmap);
                            BorrowConstants.pathList.add(model);
                        }
                    }
                }
            }
        });


    }


    /**
     * 获取本地图片路径
     */
    public static List<PhotoModel> getSystemPhotoList(Context context) {
        List<PhotoModel> result = new ArrayList<PhotoModel>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        ContentResolver contentResolver = context.getContentResolver();
        //用Coursor遍历本地相册下的图片路径
        Cursor cursor = contentResolver.query(uri, null, null, null, MediaStore.Images.Media._ID + " DESC");

        if (cursor.getCount() <= ChatPictureFragment.SHOW_IMAGE_NUM)
            ChatPictureFragment.SHOW_IMAGE_NUM = cursor.getCount();
        while (cursor.moveToNext()) {
            if (cursor.getPosition() == ChatPictureFragment.SHOW_IMAGE_NUM) {
                break;
            }
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(index); // 文件地址
            File file = new File(path);
            if (file.exists()) {
                PhotoModel cameraPhotoModel = new PhotoModel();
                cameraPhotoModel.setChecked(true);
                cameraPhotoModel.setOriginalPath(path);
                result.add(cameraPhotoModel);
            }
        }
        PhotoModel addPhoto = new PhotoModel();
        PhotoModel morePhoto = new PhotoModel();
        addPhoto.setOriginalPath("add");
        morePhoto.setOriginalPath("more");
        result.add(0, addPhoto);
        if (result.size() == 11)
            result.add(morePhoto);
        return result;
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath, int width, int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        try {
            int num = BitmapUtils.readPictureDegree(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeFile(filePath, options);
    }
}
