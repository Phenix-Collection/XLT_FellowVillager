package com.xianglin.fellowvillager.app.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

/**
 * Created by xl on 2016/1/19.
 */
public class BitmapUtils {
    /**
     * 获取图片旋转的角度
     * @param path
     * @return
     * @throws Exception
     */
    public static int readPictureDegree(String path) throws Exception {
        int degree = 0;
        if (TextUtils.isEmpty(path)) {
            return degree;
        }
        ExifInterface exifInterface = new ExifInterface(path);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            degree = 90;
        }
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            degree = 180;
        }
        else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            degree = 270;
        }
        return degree;
    }

    /**
     * 旋转图片角度
     * @param degree
     * @param bitmap
     * @return
     */
    public static Bitmap rotatingBitmap(int degree, Bitmap bitmap) {
        if(bitmap == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return rotateBitmap;
    }
}
