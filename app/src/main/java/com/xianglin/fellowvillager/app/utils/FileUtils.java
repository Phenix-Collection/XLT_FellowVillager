package com.xianglin.fellowvillager.app.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.xianglin.mobile.common.filenetwork.FileNetWork;
import com.xianglin.mobile.common.filenetwork.listener.FileMessageListener;
import com.xianglin.mobile.common.filenetwork.model.FileTask;
import com.xianglin.mobile.common.filenetwork.utils.MD5FileUtil;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件工具类
 *
 * @author songdiyuan
 * @version $Id: XLFileUtils.java, v 1.0.0 2015-8-22 下午10:21:03 xl Exp $
 */
public class FileUtils {
    private final static String TAG = "FILESERVER";

    public File ImgCachePath;
    public File ImgSavePath;
    public File ImgSavePathPhoto;
    public File ImgThumbnailSavePath;
    public File ImgSharePath;
    public File ApkSavePath;
    public File LogSavePath;
    public File ImgCapTempPath;
    public File ImgCapCutPath;
    public File ImgCacheDefaultPath;
    public File VoiceCachePath;
    public File HeadImage_path;

    public static String APP_DATA_ROOT_PATH;
    public static String IMG_SAVE_PATH;
    public static String IMG_SAVE_PATH_PHOTO; //用户拍照原图
    public static String IMG_THUMBNAIL_SAVE_PATH;
    public static String IMG_SHARE_PATH;
    public static String APK_INSTALL_PATH;
    public static String APK_LOG_PATH;
    public static String IMG_SAVE_PATH_CAP_TEMP;
    public static String IMG_SAVE_PATH_CAP_CUT;
    public static String IMG_CACHE_XUTILS_SDCARD_PATH;
    public static String IMG_CACHE_XUTILS_DEFAULT_PATH;
    public static String IMG_CACHE_HEADIMAGE_PATH;
    public static String FINAL_IMAGE_PATH;
    public static String FINAL_TEMP_PATH;
    public static String SDPATH;
    public static String VOICE_CACHE_PATH;

    public File XiangLinPath;
    public Context mContext;
    private static FileUtils mInstance;

    public FileUtils(Context context) {
        mContext = context;
    }

    /**
     * 创建文件工具类示例
     *
     * @param context 上下文
     * @return
     */
    public static synchronized FileUtils createInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FileUtils(context);
            mInstance.initPath();
        }
        return mInstance;
    }

    /**
     * 获取文件工具类实例
     *
     * @return
     */
    public static synchronized FileUtils getInstance() {
        if (mInstance == null)
            throw new IllegalStateException("FileUtil must be create by call createInstance(Context context)");
        return mInstance;
    }

    /**
     * 初始化本地缓存路径
     */
    public void initPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            SDPATH = Environment.getExternalStorageDirectory() + "/";
            IMG_SAVE_PATH = SDPATH + "XiangLin/images/save/";
            IMG_SAVE_PATH_PHOTO = SDPATH + "XiangLin/images/save/photo/";
            IMG_THUMBNAIL_SAVE_PATH=IMG_SAVE_PATH+"thumbnail/";
            IMG_SHARE_PATH = SDPATH + "XiangLin/images/share/";
            APK_INSTALL_PATH = SDPATH + "XiangLin/app/";
            APK_LOG_PATH = SDPATH + "XiangLin/log/";
            IMG_SAVE_PATH_CAP_TEMP = SDPATH + "XiangLin/images/save/capture/XiangLin_temp/";
            IMG_SAVE_PATH_CAP_CUT = SDPATH + "XiangLin/images/save/capture/XiangLin_cut/";
            IMG_CACHE_XUTILS_SDCARD_PATH = SDPATH + "XiangLin/images/cache/";// 用于保存图片缓存吧
            IMG_CACHE_XUTILS_DEFAULT_PATH = SDPATH + "Android/data/" + mContext.getPackageName() + "/cache/imgCache/";

            IMG_CACHE_HEADIMAGE_PATH = SDPATH + "XiangLin/images/save/headimage/";//保存用户头像

            VOICE_CACHE_PATH = SDPATH + "XiangLin/voice/cache/";

            APP_DATA_ROOT_PATH = getAppPath() + "XiangLin/";
            FINAL_IMAGE_PATH = APP_DATA_ROOT_PATH + "images/";
            FINAL_TEMP_PATH = APP_DATA_ROOT_PATH + "temp/";

            XiangLinPath = new File(APP_DATA_ROOT_PATH);
            if (!XiangLinPath.exists()) {
                XiangLinPath.mkdirs();
            }
            XiangLinPath = new File(FINAL_IMAGE_PATH);
            if (!XiangLinPath.exists()) {
                XiangLinPath.mkdirs();
            }

            XiangLinPath = new File(FINAL_TEMP_PATH);
            if (!XiangLinPath.exists()) {
                XiangLinPath.mkdirs();
            }

            // 拍照图片保存地址
            ImgCapTempPath = new File(IMG_SAVE_PATH_CAP_TEMP);
            if (!ImgCapTempPath.exists()) {
                ImgCapTempPath.mkdirs();
            }
            // 裁剪后图片保存地址
            ImgCapCutPath = new File(IMG_SAVE_PATH_CAP_CUT);
            if (!ImgCapCutPath.exists()) {
                ImgCapCutPath.mkdirs();
            }
            // 图片保存、缓存地址
            ImgSavePath = new File(IMG_SAVE_PATH);
            if (!ImgSavePath.exists()) {
                ImgSavePath.mkdirs();
            }            // 用户拍照图片保存、缓存地址
            ImgSavePathPhoto = new File(IMG_SAVE_PATH_PHOTO);
            if (!ImgSavePathPhoto.exists()) {
                ImgSavePathPhoto.mkdirs();
            }
            // 图片缩略图保存地址
            ImgThumbnailSavePath = new File(IMG_THUMBNAIL_SAVE_PATH);
            if (!ImgThumbnailSavePath.exists()) {
                ImgThumbnailSavePath.mkdirs();
            }

            // 分享图片的临时保存路径
            ImgSharePath = new File(IMG_SHARE_PATH);
            if (!ImgSharePath.exists()) {
                ImgSharePath.mkdirs();
            }
            // 检测更新时保存路径
            ApkSavePath = new File(APK_INSTALL_PATH);
            if (!ApkSavePath.exists()) {
                ApkSavePath.mkdirs();
            }
            // 异常保存路径
            LogSavePath = new File(APK_LOG_PATH);
            if (!LogSavePath.exists()) {
                LogSavePath.mkdirs();
            }
            ImgCachePath = new File(IMG_CACHE_XUTILS_SDCARD_PATH);
            if (!ImgCachePath.exists()) {
                ImgCachePath.mkdirs();
            }
            ImgCacheDefaultPath = new File(IMG_CACHE_XUTILS_DEFAULT_PATH);
            if (!ImgCacheDefaultPath.exists()) {
                ImgCacheDefaultPath.mkdirs();
            }
            // 语音文件保存路径
            VoiceCachePath = new File(VOICE_CACHE_PATH);
            if (!VoiceCachePath.exists()) {
                VoiceCachePath.mkdirs();
            }
             // 头像保存路径
            HeadImage_path = new File(IMG_CACHE_HEADIMAGE_PATH);
            if (!HeadImage_path.exists()) {
                HeadImage_path.mkdirs();
            }

        }

    }

    private String getAppPath() {
        LogCatLog.i(TAG, "MyApplication-getAppPath():" + mContext.getFilesDir().getParent());
        return mContext.getFilesDir().getParent() + "/";
    }

    /**
     * [将文件保存到SDcard方法]<BR>
     * [功能详细描述]
     *
     * @param fileName
     * @throws IOException
     */
    public boolean saveFile2SDCard(String fileName, byte[] dataBytes) throws IOException {
        boolean flag = false;
        FileOutputStream fs = null;
        try {
            if (!TextUtils.isEmpty(fileName)) {
                File file = newFileWithPath(fileName.toString());
                if (file.exists()) {
                    file.delete();
                    LogCatLog.w(TAG, "httpFrame  threadName:" + Thread.currentThread().getName() + " 文件已存在 则先删除: "
                            + fileName.toString());
                }
                fs = new FileOutputStream(file);
                fs.write(dataBytes, 0, dataBytes.length);
                fs.flush();
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fs != null)
                fs.close();
        }

        return flag;
    }

    /**
     * 创建一个文件，如果其所在目录不存在时，他的目录也会被跟着创建
     *
     * @return
     * @author songdiyuan
     * @date 2015-8-24
     */
    public File newFileWithPath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        int index = filePath.lastIndexOf(File.separator);

        String path = "";
        if (index != -1) {
            path = filePath.substring(0, index);
            if (!TextUtils.isEmpty(path)) {
                File file = new File(path.toString());
                // 如果文件夹不存在
                if (!file.exists() && !file.isDirectory()) {
                    boolean flag = file.mkdirs();
                    if (flag) {
                        LogCatLog.i(TAG, "httpFrame  threadName:" + Thread.currentThread().getName() + " 创建文件夹成功："
                                + file.getPath());
                    } else {
                        LogCatLog.e(TAG, "httpFrame  threadName:" + Thread.currentThread().getName() + " 创建文件夹失败："
                                + file.getPath());
                    }
                }
            }
        }
        return new File(filePath);
    }

    /**
     * 判断文件是否存在
     *
     * @param strPath
     * @return
     */
    public boolean isExists(String strPath) {
        if (strPath == null) {
            return false;
        }

        final File strFile = new File(strPath);

        if (strFile.exists()) {
            return true;
        }
        return false;

    }

    public boolean deleteFile(String strPath) {
        if (strPath == null) {
            return false;
        }

        final File strFile = new File(strPath);

        if (strFile.exists()) {
           return strFile.delete();
        }
        return false;

    }

    /**
     * 下载文件
     *
     * @param context
     * @param xlid
     * @param fileId  需要下载的文件ID
     * @param path    保存的路径
     */
    public static void downloadFile(Context context, long xlid, String fileId, String path, FileMessageListener<FileTask> listener) {
        LogCatLog.d(TAG, "要下载的文件ID" + fileId);
        if(fileId != null && !fileId.equals("null")) {
            FileNetWork.getInstance()
                    .initFileNetWorkUtilAddTask(new FileTask.Builder()
                            .XLID(xlid)
                            .fileID(fileId)   //test:"569"
                            .fileToPath(path)     //test :    "/sdcard/"
                            .fileStatus(FileNetWork.FILE_DOWNLOAD)
                            .mContext(context)
                            .listener(listener)
                            .build());
        }else{
            LogCatLog.d(TAG,"文件ID 为null");
        }
    }



    /**
     * 下载文件
     *
     * @param context
     * @param xlid
     * @param fileId  需要下载的文件ID
     * @param path    保存的路径
     */
    public  void downloadFiles(Context context, long xlid, String fileId, String path, FileMessageListener<FileTask> listener) {
        LogCatLog.d(TAG, "要下载的文件ID" + fileId);
        if(fileId != null && !fileId.equals("null")) {
            FileNetWork.getInstance()
                    .initFileNetWorkUtilAddTask(new FileTask.Builder()
                            .XLID(xlid)
                            .fileID(fileId)   //test:"569"
                            .fileToPath(path)     //test :    "/sdcard/"
                            .fileStatus(FileNetWork.FILE_DOWNLOAD)
                            .mContext(context)
                            .listener(listener)
                            .build());
        }else{
            LogCatLog.d(TAG,"文件ID 为null");
        }
    }
    /**
     * 文件上传
     *
     * @param context
     * @param path    需上传的文件（路径加文件名）
     * @param xlid
     */
    public static void uploadFile(Context context, long xlid, String path, FileMessageListener<FileTask> listener) {
        LogCatLog.d(TAG,"需要上传文件的"+path);
       if (path != null && !path.equals("")) {
           File f = new File(path);
           File file = new File(f.getAbsoluteFile() + "");
           FileNetWork.getInstance()
                   .initFileNetWorkUtilAddTask(new FileTask.Builder()
                           .mContext(context)
                           .XLID(xlid)
                           .fileStatus(FileNetWork.FILE_UPLOAD)
                           .fileName(file.getName())
                           .filePath(file.getAbsolutePath())
                           .fileSize(file.length())
                           .token(MD5FileUtil.getFileMD5String(file))
                           .fileType("1")
                           .listener(listener)
                           .build());
       }else{
           LogCatLog.d(TAG,"要上传的文件路径不能为null");
       }
    }

    public static void downloadFile(Context context,String fileId,String path){
        long XLID=PersonSharePreference.getUserID();
        downloadFile(context, XLID, fileId, path, new FileMessageListener<FileTask>() {
            @Override
            public void success(int statusCode, FileTask fileTask) {

            }

            @Override
            public void handleing(int statusCode, FileTask fileTask) {

            }

            @Override
            public void failure(int statusCode, FileTask fileTask) {

            }
        });
    }

    /**
     * 拷贝一个文件到另一个目录
     */
    public boolean copyFile(String from,String to){

        File fromFile,toFile;
        fromFile = new File(from);
        toFile = new File(to);
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try{
            toFile.createNewFile();
            fis = new FileInputStream(fromFile);
            fos = new FileOutputStream(toFile);
            int bytesRead;
            byte[] buf = new byte[4 * 1024];  // 4K buffer
            while((bytesRead=fis.read(buf))!=-1){
                fos.write(buf,0,bytesRead);
            }
            fos.flush();
            fos.close();
            fis.close();
        }catch(IOException e){
            System.out.println(e);
            return false;
        }
        return true;

    }

    /**
     * 删除文件
     * @param path
     * @return
     */
    public static boolean removeFile(String path){
        try{
            File file = new File(path);
            if (file.exists()){
                return file.delete();
            }else{
                LogCatLog.d(TAG,"删除文件失败");
            }
        }catch (Exception e){
            LogCatLog.e(TAG,"删除文件失败"+e);
        }
        return false;
    }


}
