package com.xianglin.fellowvillager.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.utils.ImageCache;
import com.xianglin.mobile.common.filenetwork.listener.FileMessageListener;
import com.xianglin.mobile.common.filenetwork.model.AddressManager;
import com.xianglin.mobile.common.filenetwork.model.FileTask;
import com.xianglin.mobile.common.info.DeviceInfo;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片处理工具
 *
 * @author Ryan.Tang
 */
public final class ImageUtils {

    private static final String TAG  = ImageUtils.class.getSimpleName();
    /**
     * Drawable转Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                : Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Bitmap转Drawable
     *
     * @param bitmap
     * @return
     */
    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return new BitmapDrawable(bitmap);
    }

    /**
     * 输输流对象转Bitmap
     *
     * @param inputStream
     * @return
     * @throws Exception
     */
    public static Bitmap inputStreamToBitmap(InputStream inputStream)
            throws Exception {
        return BitmapFactory.decodeStream(inputStream);
    }

    /**
     * 字节字节数组转Bitmap
     *
     * @param byteArray
     * @return
     */
    public static Bitmap byteToBitmap(byte[] byteArray) {
        if (byteArray.length != 0) {
            return BitmapFactory
                    .decodeByteArray(byteArray, 0, byteArray.length);
        } else {
            return null;
        }
    }

    /**
     * 字节数组转Drawable对象
     *
     * @param byteArray
     * @return
     */
    public static Drawable byteToDrawable(byte[] byteArray) {
        ByteArrayInputStream ins = null;
        if (byteArray != null) {
            ins = new ByteArrayInputStream(byteArray);
        }
        return Drawable.createFromStream(ins, null);
    }

    /**
     * Bitmap转字节数组
     *
     * @param bm
     * @return
     */
    public static byte[] bitmapToBytes(Bitmap bm) {
        byte[] bytes = null;
        if (bm != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            bytes = baos.toByteArray();
        }
        return bytes;
    }

    /**
     * Drawable转字节数组
     *
     * @param drawable
     * @return
     */
    public static byte[] drawableToBytes(Drawable drawable) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        byte[] bytes = bitmapToBytes(bitmap);
        ;
        return bytes;
    }

    /**
     * Create reflection images
     *
     * @param bitmap
     * @return
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
                h / 2, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
                Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * 图片角圆化
     *
     * @param bitmap
     * @param roundPx 5 10
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        //默认是ARGB_8888，表示24bit颜色和透明通道，但一般用不上
        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 位图圆角处理
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {

        try {

            Bitmap targetBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Config.ARGB_8888);

            // 得到画布
            Canvas canvas = new Canvas(targetBitmap);

            // 创建画笔
            Paint paint = new Paint();
            paint.setAntiAlias(true);

            // 值越大角度越明显
            float roundPx = 10;
            float roundPy = 10;

            Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            RectF rectF = new RectF(rect);

            // 绘制
            canvas.drawARGB(0, 0, 0, 0);
            canvas.drawRoundRect(rectF, roundPx, roundPy, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return targetBitmap;

        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 重新指定图片大小
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap newbmp;
        try {
            // if (h >= w) {
            // if (height <= h) {
            Matrix matrix = new Matrix();
            float scaleHeight = ((float) height / h);
            matrix.postScale(scaleHeight, scaleHeight);
            newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
            return newbmp;
            // }
            // } else {
            // if (width <= w) {
            // Matrix matrix = new Matrix();
            // float scaleWidth = ((float) width / w);
            // matrix.postScale(scaleWidth, scaleWidth);
            // newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix,
            // true);
            // return newbmp;
            // }
            // }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 重新指定Drawable大小
     *
     * @param drawable
     * @param w
     * @param h
     * @return
     */
    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        matrix.postScale(sx, sy);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return new BitmapDrawable(newbmp);
    }

    /**
     * 根据文件路径+图片名称获得Bitmap对象
     *
     * @param photoName
     * @return
     */
    public static Bitmap getPhotoFromSDCard(String path, String photoName) {
        Bitmap photoBitmap = BitmapFactory.decodeFile(path + "/" + photoName
                + ".png");
        if (photoBitmap == null) {
            return null;
        } else {
            return photoBitmap;
        }
    }

    /**
     * 检查SD卡是否挂载
     *
     * @return
     */
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * 根据给定文件路径+文件名判断SD卡是否存在
     *
     * @param photoName
     * @return
     */
    public static boolean findPhotoFromSDCard(String path, String photoName) {
        boolean flag = false;
        if (checkSDCardAvailable()) {
            File dir = new File(path);
            if (dir.exists()) {
                File folders = new File(path);
                File photoFile[] = folders.listFiles();
                for (int i = 0; i < photoFile.length; i++) {
                    String fileName = photoFile[i].getName().split("\\.")[0];
                    if (fileName.equals(photoName)) {
                        flag = true;
                    }
                }
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * 将位图存到指定空间
     *
     * @param photoBitmap
     * @param photoName
     * @param path
     */
    public static void savePhotoToSDCard(Bitmap photoBitmap, String path,
                                         String photoName) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File photoFile = new File(path, photoName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(photoFile);
            if (photoBitmap != null) {
                if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
                        fileOutputStream)) {
                    fileOutputStream.flush();
                }
            }
        } catch (FileNotFoundException e) {
            photoFile.delete();
            e.printStackTrace();
        } catch (IOException e) {
            photoFile.delete();
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从SD卡中删除指定目录下文件
     *
     * @param path file:///sdcard/temp.jpg
     */
    public static void deleteAllPhoto(String path) {
        if (checkSDCardAvailable()) {
            File file = new File(path);
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    f.delete();
                }
            } else {
                file.delete();
            }
        }
    }

    public static void deletePhotoAtPathAndName(String path, String fileName) {
        if (checkSDCardAvailable()) {
            File folder = new File(path);
            File[] files = folder.listFiles();
            for (int i = 0; i < files.length; i++) {
                System.out.println(files[i].getName());
                if (files[i].getName().equals(fileName)) {
                    files[i].delete();
                }
            }
        }
    }

    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        return getRoundedCornerBitmap(bitmap, Integer.MAX_VALUE);
    }

    public static Bitmap getRoundedBitmap(Bitmap bitmap, float radius) {
        return getRoundedCornerBitmap(bitmap, radius);
    }

    /**
     * 将图片存入缓存目录
     */
    public static void save2Cache(Context context, String cover, Bitmap bitmap) {
        File file = new File(context.getCacheDir(), cover + ".png");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            if (bitmap != null) {
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                        fileOutputStream)) {
                    fileOutputStream.flush();
                }
            }
        } catch (FileNotFoundException e) {
            file.delete();
            e.printStackTrace();
        } catch (IOException e) {
            file.delete();
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
//		final int height = options.outHeight;
//		final int width = options.outWidth;
//		int inSampleSize = 1;
//		if (height > reqHeight || width > reqWidth) {
//			// 计算出实际宽高和目标宽高的比率
//			final int heightRatio = Math.round((float) height / (float) reqHeight);
//			final int widthRatio = Math.round((float) width / (float) reqWidth);
//			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
//			// 一定都会大于等于目标的宽和高。
//			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
//		}
//		return inSampleSize;
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static Bitmap decodeBitmapFromResource(Resources res, int resId,
                                                  int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 从path从取出图片，判断图片的宽高<br>
     * 1.如果图片宽度小于高度时<br>
     * 当图片高度大于屏幕高度，图片高度设置为400px，宽度等比缩为对应宽度<br>
     * 当图片高度小于屏幕高度，图片高度设置为250px，宽度等比缩为对应宽度<br>
     * 2.如果图片宽度大于高度时<br>
     * 当图片宽度大于屏幕宽度，图片宽度设置为400px，高度等比缩为对应高度<br>
     * 当图片宽度小于屏幕宽度，图片宽度设置为250px，高度等比缩为对应高度
     *
     * @param path
     * @return
     */
    public static Bitmap decodeThumbnailsBitmap(String path) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // 调用上面定义的方法计算inSampleSize值
        int width = options.outWidth;
        int height = options.outHeight;
        int reqWidth, reqHeight;
        int screenWidth = DeviceInfo.getInstance().getScreenWidth();
        int sreenHeight = DeviceInfo.getInstance().getScreenHeight();
        if (width == 0 || height == 0) {
            return decodeBitmapFromResource(XLApplication.getInstance().getResources(), R.drawable.head, 100, 100);
        }
        if (width > height) {
            if (width >= 2560 && screenWidth >= 1440) {
                reqWidth = 560;
            } else if (width >= 1920 && screenWidth >= 1080) {
                reqWidth = 480;
            } else if (width >= 1280 && screenWidth >= 720) {
                reqWidth = 400;
            } else {
                reqWidth = 250;
            }
            reqHeight = reqWidth * height / width;
        } else {
            if (height >= 2560) {
                reqHeight = 560;
            } else if (height >= 1920) {
                reqHeight = 480;
            } else if (height >= 1280) {
                reqHeight = 400;
            } else {
                reqHeight = 250;
            }
            reqWidth = reqHeight * width / height;
        }

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        LogCatLog.e("Test", "outWidth=" + width + ",outHeight=" + height
                + ",screenWidth=" + DeviceInfo.getInstance().getScreenWidth()
                + ",screenHeight=" + DeviceInfo.getInstance().getScreenHeight()
                + ",reqWidth=" + reqWidth + ",reqHeight=" + reqHeight
                + ",inSampleSize=" + options.inSampleSize);

        // 使用获取到的inSampleSize值再次解析图片
        int targetDensity = DeviceInfo.getInstance().getDencity();
        double xSScale = ((double) options.outWidth) / ((double) reqWidth);
        double ySScale = ((double) options.outHeight) / ((double) reqHeight);
        double startScale = xSScale > ySScale ? xSScale : ySScale;
        if (width < reqWidth || height < reqHeight) {
            reqWidth = width;
            reqHeight = height;
        } else {
            options.inScaled = true;
            options.inDensity = (int) (targetDensity * startScale / options.inSampleSize);
            options.inTargetDensity = targetDensity;
        }
        options.inJustDecodeBounds = false;
        Bitmap bmp=null;
        try{

            bmp = BitmapFactory.decodeFile(path, options);
        }catch (Exception e){

        }
//		if(width<reqWidth||height<reqHeight){
//			reqWidth=width;
//			reqHeight=height;
//		}else{
//			bmp=resizeImage(bmp, reqWidth, reqHeight);
//		}
        return bmp;
    }

    public static Bitmap decodeBitmapFromSDCard(String path,
                                                int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // 调用上面定义的方法计算inSampleSize值
        int width = options.outWidth;
        int height = options.outHeight;

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//		LogCatLog.e("Test","outWidth="+width+",outHeight="+height
//				+",screenWidth="+ DeviceInfo.getInstance().getScreenWidth()
//				+",screenHeight="+DeviceInfo.getInstance().getScreenHeight()
//				+",reqWidth="+reqWidth+",reqHeight="+reqHeight
//				+",inSampleSize="+options.inSampleSize);
        // 使用获取到的inSampleSize值再次解析图片
        int targetDensity = DeviceInfo.getInstance().getDencity();
        double xSScale = ((double) options.outWidth) / ((double) reqWidth);
        double ySScale = ((double) options.outHeight) / ((double) reqHeight);
        double startScale = xSScale > ySScale ? xSScale : ySScale;
        if (width < reqWidth || height < reqHeight) {
            reqWidth = width;
            reqHeight = height;
        } else {
            options.inScaled = true;
            options.inDensity = (int) (targetDensity * startScale / options.inSampleSize);
            options.inTargetDensity = targetDensity;
        }
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
//		if(width<reqWidth||height<reqHeight){
//			reqWidth=width;
//			reqHeight=height;
//		}else{
//			bmp=resizeImage(bmp, reqWidth, reqHeight);
//		}
        return bmp;
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // if you want to rotate the Bitmap
        // matrix.postRotate(45);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }


    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        if (context == null || uri == null) return null;

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static void loadImage(final ImageView imageView, String path, final Drawable ResFailedImg) {

        ImageLoader.getInstance().displayImage(path, imageView, getOpetion());
//		ImageLoader.getInstance().loadImage(path, new SimpleImageLoadingListener() {
//			@Override
//			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//				imageView.setImageBitmap(loadedImage);
//			}
//
//			@Override
//			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//				imageView.setImageDrawable(ResFailedImg);
//			}
//		});
    }

    public static void loadImage(final ImageView imageView, String path, final int ResImgId) {

        ImageLoader.getInstance().displayImage(path, imageView, getOpetions(ResImgId));

    }


    /**
     * ImageLoader的加载图片参数  测试
     *
     * @return
     */
    @Deprecated
    public static DisplayImageOptions getOpetion() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //.showImageOnLoading(R.drawable.head)
                .showImageForEmptyUri(R.drawable.head)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        return options;
    }

    @Deprecated
    public static DisplayImageOptions getOpetions(int defaultResId) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(defaultResId)
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Config.RGB_565)
                .build();
        return options;
    }

    /**
     * ImageLoader的加载图片参数  测试
     *
     * @return
     */
    @Deprecated
    public static DisplayImageOptions getOpetionPhotoSelect() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_image)
                .showImageForEmptyUri(R.drawable.default_image)
                .showImageOnFail(R.drawable.ic_picture_loadfailed)
                .bitmapConfig(Config.RGB_565) // default
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        return options;
    }

    /**
     * ImageLoader的加载图片参数  测试
     *
     * @return
     */

    public static DisplayImageOptions getDefOpetion() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.btn_bg_gray)
                .bitmapConfig(Config.RGB_565) // default
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        return options;
    }


    public static boolean isLocalImg(String imgId) {
        imgId = AddressManager.addressManager.env + "_" + imgId;
        if (FileUtils.getInstance().isExists(FileUtils.IMG_CACHE_HEADIMAGE_PATH + imgId + ".jpg") ||
                FileUtils.getInstance().isExists(FileUtils.IMG_CACHE_HEADIMAGE_PATH + imgId + ".png")
                || FileUtils.getInstance().isExists(FileUtils.IMG_CACHE_HEADIMAGE_PATH + imgId + ".webp")
                || FileUtils.getInstance().isExists(FileUtils.IMG_CACHE_HEADIMAGE_PATH + imgId + ".gif")) {
            return true;
        }
        return false;
    }

    public static boolean isLocalImg(String path, String imgId) {
        imgId = AddressManager.addressManager.env + "_" + imgId;
        if (FileUtils.getInstance().isExists(path + imgId + ".jpg") ||
                FileUtils.getInstance().isExists(path + imgId + ".png")
                || FileUtils.getInstance().isExists(path + imgId + ".webp")
                || FileUtils.getInstance().isExists(path + imgId + ".gif")) {
            return true;
        }
        return false;
    }

    public static void showLocalImg(Context context, ImageView imageView, String imgId) {


        String localImgId = AddressManager.addressManager.env + "_" + imgId;
        if (FileUtils.getInstance().isExists(FileUtils.IMG_CACHE_HEADIMAGE_PATH + localImgId + ".webp")) {
            imageView.setTag(imgId);
            ImageUtils.loadImage(imageView, "file://" + FileUtils.IMG_CACHE_HEADIMAGE_PATH + localImgId + ".webp",
                    context.getResources().getDrawable(R.drawable.head));

        } else if (FileUtils.getInstance().isExists(FileUtils.IMG_CACHE_HEADIMAGE_PATH + localImgId + ".jpg")) {
            imageView.setTag(imgId);
            ImageUtils.loadImage(imageView, "file://" + FileUtils.IMG_CACHE_HEADIMAGE_PATH + localImgId + ".jpg",
                    context.getResources().getDrawable(R.drawable.head));
        } else if (FileUtils.getInstance().isExists(FileUtils.IMG_CACHE_HEADIMAGE_PATH + localImgId + ".png")) {
            imageView.setTag(imgId);
            ImageUtils.loadImage(imageView, "file://" + FileUtils.IMG_CACHE_HEADIMAGE_PATH + localImgId + ".png",
                    context.getResources().getDrawable(R.drawable.head));
        } else {
            imageView.setImageResource(R.drawable.head);
        }
    }

    public static void showUrlImage(final ImageView imageView,final String url){
        if (ImageCache.getInstance().get(url) != null) {

            imageView.setImageBitmap(ImageCache.getInstance().get(url));

        } else {
            ImageLoader.getInstance().displayImage(url, imageView, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    ImageCache.getInstance().put(url,loadedImage);
                    imageView.setImageBitmap(ImageCache.getInstance().get(url));
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

        }
    }


    public static String getLocalImagePath(String imgId) {
        String path = "";
        String localImgId = AddressManager.addressManager.env + "_" + imgId;
        if (FileUtils.getInstance().isExists(FileUtils.IMG_CACHE_HEADIMAGE_PATH + localImgId + ".webp")) {
            path = "file://" + FileUtils.IMG_CACHE_HEADIMAGE_PATH + localImgId + ".webp";

        } else if (FileUtils.getInstance().isExists(FileUtils.IMG_CACHE_HEADIMAGE_PATH + localImgId + ".jpg")) {
            path = "file://" + FileUtils.IMG_CACHE_HEADIMAGE_PATH + localImgId + ".jpg";
        } else if (FileUtils.getInstance().isExists(FileUtils.IMG_CACHE_HEADIMAGE_PATH + localImgId + ".png")) {
            path = "file://" + FileUtils.IMG_CACHE_HEADIMAGE_PATH + localImgId + ".png";
        }
        return path;
    }

    public static String getLocalImagePath(String savaPath, String imgId) {
        String path = "";
        String localImgId = AddressManager.addressManager.env + "_" + imgId;
        if (FileUtils.getInstance().isExists(savaPath + localImgId + ".webp")) {
            path = "file://" + savaPath + localImgId + ".webp";

        } else if (FileUtils.getInstance().isExists(savaPath + localImgId + ".jpg")) {
            path = "file://" + savaPath + localImgId + ".jpg";
        } else if (FileUtils.getInstance().isExists(savaPath + localImgId + ".png")) {
            path = "file://" + savaPath + localImgId + ".png";
        } else if (FileUtils.getInstance().isExists(savaPath + localImgId + ".gif")) {
            path = "file://" + savaPath + localImgId + ".gif";
        }
        return path;
    }

    public static void showLocalImg(Context context, ImageView imageView, String path, String imgId) {
        imgId = AddressManager.addressManager.env + "_" + imgId;
        if (FileUtils.getInstance().isExists(path + imgId + ".webp")) {
            ImageUtils.loadImage(imageView, "file://" + path + imgId + ".webp",
                    R.drawable.ic_picture_loadfailed);
        } else if (FileUtils.getInstance().isExists(path + imgId + ".jpg")) {
            ImageUtils.loadImage(imageView, "file://" + path + imgId + ".jpg",
                    R.drawable.ic_picture_loadfailed);
        } else if (FileUtils.getInstance().isExists(path + imgId + ".png")) {
            ImageUtils.loadImage(imageView, "file://" + path + imgId + ".png",
                    R.drawable.ic_picture_loadfailed);
        } else {
            imageView.setImageResource(R.drawable.ic_picture_loadfailed);
        }
    }


    /**
     * 通用显示图片方法
     * @param activity
     * @param imgView
     * @param savePath
     * @param imageId
     * @param drawableId
     */
    public static void showCommonImage(
            Activity activity,
            ImageView imgView,
            String savePath,
            String imageId,
            int drawableId
    ) {
        imgView.setPadding(0, 0, 0, 0);
        if ((PersonSharePreference.getUserID() + "").equals(imageId)) {//本人头像
            setImageOrDrawee(
                    imgView,
                    FileUtils.IMG_CACHE_HEADIMAGE_PATH + PersonSharePreference.getUserID() + ".webp",
                    null
            );
        } else if (TextUtils.isEmpty(imageId) || imageId.equals("null")||"0".equals(imageId)) {//图像不存在
            imgView.setImageResource(drawableId);
        } else if (ImageCache.getInstance().get(imageId) != null) {
            imgView.setImageBitmap(ImageCache.getInstance().get(imageId));
        } else if (imageId.contains(".")) {//全路径
               setImageOrDrawee(imgView, imageId, imageId);
        } else if (ImageUtils.isLocalImg(savePath, imageId)) {//本地存在
            setImageOrDrawee(imgView, getLocalImagePath(savePath, imageId), imageId);
        } else {

            ImageUtils.downloadImage(activity, imgView, savePath, imageId);
        }
    }

    /**
     * 根据控件类型(ImageView或SimpleDraweeView)设置图片
     * @param imageView 控件
     * @param filePath 图片路径
     * @param imgCacheKey 添加图片缓存的key, 为null时不存入到缓存中
     * @param imgWidth 显示图片宽度
     * @param imgHeight 显示图片高度
     */
    public static void setImageOrDrawee(
            ImageView imageView,
            String filePath,
            String imgCacheKey,
            int imgWidth,
            int imgHeight
    ) {
        if (imageView == null) {
            return;
        }

        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        if (!(imageView instanceof SimpleDraweeView)) {
            Bitmap bitmap = ImageUtils.decodeThumbnailsBitmap(filePath.replace("file://",""));
            imageView.setImageBitmap(bitmap);
            if (imgCacheKey != null) {
                ImageCache.getInstance().put(imgCacheKey, bitmap);
            }
            return;
        }
        /*设置SimpleDraweeView的图片显示*/
        Uri uri = Uri.parse("file://" + filePath);
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(
                        new ResizeOptions(
                                imgWidth,
                                imgHeight
                        )
                )
                .build();
        DraweeController controller;
        if (filePath.endsWith(".gif") || filePath.endsWith(".GIF")) {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(((SimpleDraweeView) imageView).getController())
                    .setAutoPlayAnimations(true)
                    .build();
        } else {
            controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(((SimpleDraweeView) imageView).getController())
                    .build();
        }
        ((SimpleDraweeView) imageView).setController(controller);

    }

    /**
     * 根据控件类型(ImageView或SimpleDraweeView)设置图片
     * @param imageView 控件
     * @param filePath 图片路径
     * @param imgCacheKey 添加图片缓存的key, 为null时不存入到缓存中
     */
    public static void setImageOrDrawee(
            ImageView imageView,
            String filePath,
            String imgCacheKey
    ) {
        if (imageView == null) {
            return;
        }
        setImageOrDrawee(
                imageView,
                filePath,
                imgCacheKey,
                imageView.getLayoutParams().width,
                imageView.getLayoutParams().height
        );
    }


    public static Bitmap getBitmapById(String savePath, String imageId) {
        String path = getLocalImagePath(savePath, imageId).replace("file://", "");
        return decodeThumbnailsBitmap(path);
    }


    /**
     * @param activity
     * @param imgView
     * @param downloadSavePath 下载保存路径
     * @param imgId            要下载的图片ID
     */
    public static void downloadImage(final Activity activity, final ImageView imgView, final String downloadSavePath, final String imgId) {
        long xlId = PersonSharePreference.getUserID();

        FileUtils.downloadFile(activity, xlId, imgId, downloadSavePath, new FileMessageListener<FileTask>() {
            @Override
            public void success(int statusCode, final FileTask fileTask) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogCatLog.d(TAG,"file path=="+fileTask.filePath);
                        /*如果是SimpleDraweeView，用fresco加载图片*/
                        setImageOrDrawee(
                                imgView,
                                downloadSavePath + fileTask.fileName,
                                imgId
                        );
                        imgView.setPadding(0,0,0,0);
                    }
                });
            }

            @Override
            public void handleing(int statusCode, FileTask fileTask) {

            }

            @Override
            public void failure(int statusCode, FileTask fileTask) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgView.setImageResource(R.drawable.default_image);
                    }
                });
            }
        });
    }

    public static Bitmap decodeBitmapFromSDCard(String path) {
        Bitmap photoBitmap = BitmapFactory.decodeFile(path);
        if (photoBitmap == null) {
            return null;
        } else {
            return photoBitmap;
        }
    }
}
