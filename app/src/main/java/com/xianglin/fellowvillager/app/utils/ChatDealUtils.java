package com.xianglin.fellowvillager.app.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.Gravity;
import android.widget.Toast;

import com.xianglin.fellowvillager.app.R;

import java.io.File;

/**
 * 类描述：
 * 创建人：chengshengli
 * 创建时间：2015/10/28 15:59  15 59
 * 修改人：chengshengli
 * 修改时间：2015/10/28 15:59  15 59
 * 修改备注：
 */
public class ChatDealUtils {

    /**
     * 从图库获取图片
     */
    public static void selectPicFromLocal(Activity activity,int REQUEST_CODE_LOCAL) {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        activity.startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }

    /**
     * 选择文件
     */
    public static void selectFileFromLocal(Activity activity,int REQUEST_CODE_SELECT_FILE) {
        Intent intent = null;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        activity.startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }
    public static  File cameraFile;
    /**
     * 照相获取图片
     */
    public static void selectPicFromCamera(Activity activity,int REQUEST_CODE_CAMERA) {
        if (!Utils.isExitsSdcard()) {
            String st = activity.getResources().getString(
                    R.string.sd_card_does_not_exist);
            Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
            return;
        }

       cameraFile = new File(FileUtils.IMG_SAVE_PATH, "pic"
                + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        activity.startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                        MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * 根据图库图片uri获取路径
     *
     * @param selectedImage
     */
    public static String getPicByUri(Activity activity,Uri selectedImage) {
        // String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.getContentResolver().query(selectedImage, null, null,
                null, null);
        String st8 = activity.getResources().getString(R.string.cant_find_pictures);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex("_data");
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || picturePath.equals("null")) {
                Toast toast = Toast.makeText(activity, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return null;
            }
            return picturePath;
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(activity, st8, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return null;
            }
            return file.getAbsolutePath();
        }

    }




}
