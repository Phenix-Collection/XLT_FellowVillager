package com.xianglin.fellowvillager.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.webp.libwebp;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * webp 工具类
 * png、jpg、jpeg 图片转 webp
 * 各类文件写入
 *
 * @author james
 */
public class WebPUtil {

    private Context mContext;// 当前上下文
    private LoadWebpImage image; //图片加载对象
    private volatile static WebPUtil webPUtil; // webpUtil 工具类应用

    private static final String TAG = WebPUtil.class.getSimpleName();

    public WebPUtil(Context mContext) {
        if (mContext == null)
            throw new NullPointerException("当前上下文不能为空！！！");
        this.mContext = mContext;
        this.image = new LoadWebpImage();
    }

    /**
     * 初始化上下文
     *
     * @param mContext
     * @return
     */
    public synchronized static WebPUtil with(Context mContext) {
        if (webPUtil != null) {
            return webPUtil;
        }
        webPUtil = new WebPUtil(mContext);
        return webPUtil;
    }

    /********************** 三大格式[png/jpg/jpeg]图片转换webp Bitmap **********************************/

    /**
     * 普通图片 转 webpBitmap
     *
     * @param filePath 文件路径
     * @return
     */
    public LoadWebpImage imageFileToWebpBitMap(String filePath) {
        if (filePath == null)
            throw new NullPointerException(
                    "普通图片转webpBitmap 失败， 需要转换的文件路径不能为null");
        try {
            Bitmap bitmap = image.imageFileTowebBitmap(filePath);
            image.setBitmap(bitmap);
            return image;
        } catch (Exception e) {
            LogCatLog.d(TAG, "普通图片 转 webpBitmap 失败！");
        }
        return null;

    }

    /**
     * 普通图片 转 webpBitmap
     *
     * @param file 文件对象
     * @return
     */
    public LoadWebpImage imageFileToWebpBitMap(File file) {
        if (file == null)
            throw new NullPointerException(
                    "普通图片转webpBitmap 失败， 需要转换的文件对象不能为null");
        try {
            return imageFileToWebpBitMap(file.getAbsolutePath());
        } catch (Exception e) {
            LogCatLog.d(TAG, "普通图片 转 webpBitmap 失败！");
        }
        return null;
    }

    /**
     * 普通图片bitmap 转 webpBitmap
     *
     * @param bitmap
     * @return
     */
    public LoadWebpImage imageFileToWebpBitMap(Bitmap bitmap) {
        if (bitmap == null)
            throw new NullPointerException(
                    "普通图片转webpBitmap 失败， 需要转换的Bitmap对象不能为null");
        try {
            byte[] encoded = image.imageFileTowebBitmap(bitmap);
            image.setBitmap(image.webpToBitmap(encoded));
            return image;
        } catch (Exception e) {
            LogCatLog.d(TAG, "普通图片bitmap 转 webpBitmap 失败！");
        }
        return null;
    }

    /************************* webp 转 bitmap ******************************************/

    /**
     * webp文件路径 转 bitmap
     *
     * @param filePath
     * @return
     */
    public LoadWebpImage webpFileToWebpBitMap(String filePath) {
        try {
            byte[] encoded = image.getBytes(filePath);
            image.setBitmap(image.webpToBitmap(encoded));
            return image;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "webp文件路径 转 bitmap 失败 " + e.getMessage());
        }

        return null;
    }

    /**
     * webp 文件对象 转 webp bitmap
     *
     * @param file
     * @return
     */
    public LoadWebpImage webpFileToWebpBitMap(File file) {
        if (file == null)
            throw new NullPointerException(
                    "webp 文件对象 转 webp bitmap 失败， 需要转换的File 对象不能为null");
        try {
            return webpFileToWebpBitMap(file.getAbsoluteFile());
        } catch (Exception e) {
            LogCatLog.d(TAG, "webp 文件对象 转 webp bitmap 失败！");
        }
        return null;
    }

    /**
     * webp bitmap 对象 显示 imageview;
     *
     * @param bitmap
     * @return
     */
    public LoadWebpImage webpFileToWebpBitMap(Bitmap bitmap) {
        if (bitmap == null)
            throw new NullPointerException(
                    "webp 文件对象 转 webp bitmap 失败， 需要转换的Bitmap 对象不能为null");
        try {
            image.setBitmap(bitmap);
            return image;
        } catch (Exception e) {
            LogCatLog.d(TAG, "webp bitmap 对象 显示 imageview 失败！");
        }
        return null;
    }

    /************************* 三个格式[png/jpg/jpeg]图片 转换为webp文件 ******************************************/

    /**
     * 普通图片转webp 图片
     *
     * @param fromImagePath 需要转的 文件路径
     * @param toImagePath   写入 文件路径
     * @return true:success false:error
     */
    public boolean imageToWebp(String fromImagePath, String toImagePath) {
        if (fromImagePath == null && toImagePath == null)
            throw new NullPointerException(
                    "普通图片转webp 图片， fromImagePath 或 toImagePath不能为null");
        try {
            return image.imageFileTowebpFile(fromImagePath, toImagePath);
        } catch (Exception e) {
            LogCatLog.d(TAG, "普通图片转webp 图片 失败！");
        }
        return false;
    }

    /**
     * 普通图片转webp图片
     *
     * @param fromFile 需要转的 文件对象
     * @param toFile   写入 文件对象
     * @return true:success false:error
     */
    public boolean imageToWebp(File fromFile, File toFile) {
        if (fromFile == null && fromFile == null)
            throw new NullPointerException(
                    "普通图片转webp 图片， fromFile 或 fromFile不能为null");
        try {
            return imageToWebp(fromFile.getAbsolutePath(), toFile.getAbsolutePath());
        } catch (Exception e) {
            LogCatLog.d(TAG, "普通图片转webp图片 失败！");
        }
        return false;
    }

    /**
     * 普通图片bitmap 转 webp 文件
     *
     * @param bitmap 需要转的bitmap对象
     * @param toFile 写入文件对象
     * @return
     */
    public boolean imageToWebp(Bitmap bitmap, File toFile) {
        if (bitmap == null && toFile == null)
            throw new NullPointerException(
                    "普通图片转webp 图片， bitmap 或 toFile不能为null");
        try {
            return image.writeFileFromByteArray(toFile.getAbsolutePath(),
                    image.imageFileTowebBitmap(bitmap));
        } catch (Exception e) {
            LogCatLog.d(TAG, "普通图片bitmap 转 webp 文件失败！");
        }
        return false;
    }

    /************************* webp图片 转换 三个格式[png/jpg/jpeg] ******************************************/


    /**
     *
     * @param fromWebpFilePath webp 图片
     * @param toImageFilePath png 图片
     * @return
     */
    public boolean webpToImage(String fromWebpFilePath, String toImageFilePath) {
        if(fromWebpFilePath == null || toImageFilePath == null){
            throw new NullPointerException("需要转webp路径或普通格式图片 不能为null");
        }

        Bitmap bitmap = webpFileToBitMap(fromWebpFilePath);
        LoadWebpImage.saveBitmap2file(bitmap, toImageFilePath);

        return true;
    }

    /**
     * webp文件路径 转 bitmap
     *
     * @param filePath
     * @return
     */
    public Bitmap webpFileToBitMap(String filePath) {

        try {
            byte[] encoded = image.getBytes(filePath);
            return image.webpToBitmap(encoded);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "webp文件路径 转 bitmap 失败 " + e.getMessage());
        }

        return null;
    }



    public static Bitmap webpToBitmap(byte[] encoded) {

        int[] width = new int[] { 0 };
        int[] height = new int[] { 0 };
        byte[] decoded = libwebp.WebPDecodeARGB(encoded, encoded.length, width,
                height);

        int[] pixels = new int[decoded.length / 4];
        ByteBuffer.wrap(decoded).asIntBuffer().get(pixels);

        return Bitmap.createBitmap(pixels, width[0], height[0],
                Bitmap.Config.ARGB_8888);

    }

    public static byte[] streamToBytes(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = in.read(buffer)) >= 0) {
                out.write(buffer, 0, len);
                out.flush();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out.toByteArray();
    }
}
