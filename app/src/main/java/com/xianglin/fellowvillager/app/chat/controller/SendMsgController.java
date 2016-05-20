package com.xianglin.fellowvillager.app.chat.controller;

import android.content.Context;
import android.graphics.Bitmap;

import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity;
import com.xianglin.fellowvillager.app.chat.model.PhotoModel;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.GroupMemberDBHandler;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.model.RepeatMessage;
import com.xianglin.fellowvillager.app.utils.DataDealUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.WebPUtil;
import com.xianglin.fellowvillager.app.utils.crop.CropUtil;
import com.xianglin.fellowvillager.app.utils.messagetask.PrivateMessageTask;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.api.BackgroundExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 发送各种类型的消息(文字,图片,卡片,表情等)
 * Created by zhanglisan on 16/3/31.
 */
public class SendMsgController {

    private final String TAG = this.getClass().getSimpleName();

    private volatile static SendMsgController instance;
    private Context context;
    private int chatType;
    private String toChatId;
    private String toChatXlId;
    private String currentFigureId;
    private SendMsgCallBack callBack;

    private Bitmap mImgBmp; // 发送图片大图bitmap
    private Bitmap mThumbBmp; // 发送图片缩略图bitmap
    private List<MessageBean> pictureList = new ArrayList<>();
    private String filePath;
    private File photofile;
    private int exifRotation;
    private String imgSize;

    private SendMsgController() {}

    public static SendMsgController getInstance() {
        if (instance == null) {
            synchronized (SendMsgController.class) {
                if (instance == null) {
                    instance = new SendMsgController();
                }
            }
        }
        return instance;
    }

    public void init(
            Context context,
            int chatType,
            String toChatId,
            String toChatXlId,
            String currentFigureId,
            SendMsgCallBack callBack
    ) {
        this.context = context;
        this.chatType = chatType;
        this.toChatId = toChatId;
        this.toChatXlId = toChatXlId;
        this.currentFigureId = currentFigureId;
        this.callBack = callBack;
    }

    /**
     * 发送文本消息
     * @param bean
     * @param isNeedAdd
     */
    public void sendChatText(MessageBean bean, boolean isNeedAdd) {

        addToMessage(bean, bean.msgKey);// 添加消息 和 发送当前文字消息
    }


    /**
     * 发送语音消息
     * @param bean
     * @param isRepeat
     */
    public void sendChatVoice(
            MessageBean bean,
            boolean isRepeat
    ) {
        if (!isRepeat) {//不是重发
            bean.setTo(toChatId);
            sendMessage(bean, toChatXlId, toChatId, currentFigureId);
        } else {
            addToMessage(bean, bean.msgKey);// 添加音频文件
        }
    }

    /**
     * 发送图片消息
     * @param photoItemList
     * @param msgBean
     * @param isRepeat      是否是重新发送
     */
    public void sendChatImage(
            final List<PhotoModel> photoItemList,
            MessageBean msgBean,
            boolean isRepeat
    ) {
        if (!isRepeat) {
            sendPicture(photoItemList);
        } else {//重发
            addToMessage(msgBean, msgBean.msgKey);// 添加图片文件
        }
    }

    /**
     * 发送名片
     * @param bean
     * @param isRepeat 是否是重新发送
     */
    public void sendChatIDCard(
            final MessageBean bean,
            Contact contact,
            boolean isRepeat
    ) {
        if (!isRepeat) {
            MessageBean messagebean = MessageBean.createIDCardSendMessage(contact);
            sendMessage(messagebean, toChatXlId, toChatId, currentFigureId);
        } else {
            addToMessage(bean, bean.msgKey);
        }

    }

    /**
     * 发送商品卡片
     * @param bean
     * @param goodsId     商品id
     * @param title       商品标题
     * @param imgURL      商品图片imgURL
     * @param price       商品价格
     * @param abstraction 商品详情
     * @param url         商品链接
     * @param isRepeat    是否是重新发送
     */
    public void sendChatGoods(
            final MessageBean bean,
            final String goodsId,
            final String title,
            final String imgURL,
            final String price,
            final String abstraction,
            final String url,
            boolean isRepeat
    ) {
        if (!isRepeat) {

            MessageBean messageBean = MessageBean.createGoodsSendMessage(goodsId, title, imgURL, price, abstraction,
                    url);
            sendMessage(messageBean, toChatXlId, toChatId, currentFigureId);
        } else {

            addToMessage(bean, bean.msgKey);
        }

    }

    /**
     * 发送新闻卡片
     * @param bean
     * @param newsId      新闻id
     * @param title       新闻标题
     * @param imgUrl      新闻图片url
     * @param abstraction 详情
     * @param url         新闻url
     * @param isRepeat    是否是重发
     */
    public void sendChatNews(
            final MessageBean bean,
            final String newsId,
            final String title,
            final String imgUrl,
            final String abstraction,
            final String url,
            boolean isRepeat
    ) {
        if (!isRepeat) {
            MessageBean messageBean = MessageBean.createNewsSendMessage(newsId, title, imgUrl, abstraction, url);
            sendMessage(messageBean, toChatXlId, toChatId, currentFigureId);
        } else {
            addToMessage(bean, bean.msgKey);
        }
    }

    public void sendMessage(
            MessageBean message,
            String toUserId,
            String toFigureId,
            String currentFigureId
    ) {
        // 如果是群聊，设置chattype,默认是单聊
        if (chatType == BorrowConstants.CHATTYPE_GROUP) {
            message.setChatType(MessageBean.ChatType.GroupChat);
            message.setTo(toChatId);
            message.xlID = toChatId;
            message.xlgroupmemberid = GroupMemberDBHandler.getMemberId(toChatId, currentFigureId, currentFigureId);
        } else {
            message.setTo(toChatId);
            message.figureUsersId = toChatId;
            message.xlID = toChatXlId;
        }
        message.figureId = currentFigureId;

         //判断是否开启私密消息
        if(DataDealUtil.isSecretMode(currentFigureId, toChatId)){

            int secret_index= DataDealUtil.getSecretIndex(currentFigureId, toChatId);
            int time_count=DataDealUtil.TIME_COUNT[secret_index];
            message.lifetime=time_count;
            message.isExpired=false;
            message.currentlifetime=message.lifetime* PrivateMessageTask.UI_RATE;
        }

        //发送消息
        ChatManager.getInstance().sendMessage(message, null);
        //刷新ui
        callBack.refreshUI();
    }

    /**
     * 发送消息回调接口
     */
    public interface SendMsgCallBack {
        /**
         * 回调方法,刷新聊天页面的listview
         */
        void refreshUI();
    }



    /**
     * 发送消息到服务器
     *
     * @param bean 消息bean
     */
    public void addToMessage(
            MessageBean bean,
            String messageKey
    ) {

        PersonSharePreference.setChatFidCount(bean.figureId);// 当前角色ID的消息次数
        RepeatMessage repeatMessage = new RepeatMessage();
        repeatMessage.setToChatId(bean.xlID);// 联系人ID
        repeatMessage.setChatType(chatType);// 聊天类型
        repeatMessage.setDateTime(bean.msgCreatedate);//设置当前发送时间
        repeatMessage.setMessageBean(bean);// 消息
        XLApplication.repeatSendMessageHandler.addMessage(repeatMessage);

    }



    public void sendPicture(
            final List<PhotoModel> photoModelList) {
        if (photoModelList == null) {
            return;
        }

        BackgroundExecutor.execute(new BackgroundExecutor.Task("", 0, "") {

            @Override
            public void execute() {
                pictureList.clear();
                if (context instanceof ChatMainActivity) {
                    ((ChatMainActivity) context).isToBottom = true;
                }
                for (int i = 0; i < photoModelList.size(); i++) {

                    filePath = photoModelList.get(i).getOriginalPath();
                    LogCatLog.d(
                            TAG,
                            "上传图片路径" + filePath
                    );
                    photofile = new File(filePath);
                    if (!photofile.exists()) {
                        LogCatLog.e(
                                TAG,
                                "需要上传的文件不存在,此消息为失败"
                        );
                        continue;
                    }
                    exifRotation = CropUtil.getExifRotation(photofile);
                    mImgBmp = CropUtil.rotaingImageView(
                            exifRotation,
                            ImageUtils.decodeBitmapFromSDCard(
                                    filePath,
                                    1080,
                                    1920
                            )
                    );
                    if (mImgBmp == null) {
                        LogCatLog.e("Test", "解析图片失败,开始下一张 " + filePath);
                        //return;
                        mImgBmp = photoModelList.get(i).getmBitmap();
                    }
                    imgSize = mImgBmp.getWidth() + "_" + mImgBmp.getHeight();
                    MessageBean bean = MessageBean.createPictrueSendMessage(filePath, "", imgSize);
                    SendMsgController.getInstance().sendMessage(bean, toChatXlId, toChatId, currentFigureId);
                    pictureList.add(bean);
                    LogCatLog.e("Test", "解析图片成功" + filePath);

                    final String fileName;
                    if (filePath.endsWith(".gif")) {
                        fileName = filePath.substring(
                                filePath.lastIndexOf("/") + 1,
                                filePath.lastIndexOf(".")
                        ) + ".gif";
                    } else {
                        fileName = filePath.substring(
                                filePath.lastIndexOf("/") + 1,
                                filePath.lastIndexOf(".")
                        ) + ".webp";
                    }

                    LogCatLog.e("Test", "fileName=" + fileName);

                    Bitmap thumbBmp = CropUtil.rotaingImageView(
                            exifRotation,
                            ImageUtils.decodeThumbnailsBitmap(filePath)
                    );//缩略图
                    File saveFile = new File(FileUtils.IMG_SAVE_PATH + fileName);//上传图片
                    File saveThumbFile = new File(
                            FileUtils.IMG_THUMBNAIL_SAVE_PATH + fileName);//保存的缩略图

                    if (fileName.endsWith(".gif")) {
                        if (!FileUtils.getInstance().isExists(FileUtils.IMG_SAVE_PATH + fileName)) {
                            FileUtils.getInstance().copyFile(filePath, FileUtils.IMG_SAVE_PATH + fileName);
                        }
                        if (!FileUtils.getInstance().isExists(FileUtils.IMG_THUMBNAIL_SAVE_PATH + fileName)) {
                            FileUtils.getInstance().copyFile(filePath, FileUtils.IMG_THUMBNAIL_SAVE_PATH + fileName);
                        }
                    } else if (!filePath.endsWith(".webp") && !filePath.endsWith(".gif")) {//不是webp格式需要转
                        WebPUtil.with(context).imageToWebp(mImgBmp, saveFile);//保存大图
                        WebPUtil.with(context).imageToWebp(thumbBmp, saveThumbFile);//保存缩略图
                    } else {
                        if (!FileUtils.getInstance().isExists(FileUtils.IMG_SAVE_PATH + fileName)) {

                            ImageUtils.savePhotoToSDCard(mImgBmp, FileUtils.IMG_SAVE_PATH, fileName);
                        }
                        if (!FileUtils.getInstance().isExists(FileUtils.IMG_THUMBNAIL_SAVE_PATH + fileName)) {

                            ImageUtils.savePhotoToSDCard(thumbBmp, FileUtils.IMG_THUMBNAIL_SAVE_PATH, fileName);
                        }
                    }


                    if (thumbBmp != null && !thumbBmp.isRecycled()) thumbBmp.recycle();
                }

                for (int i = 0; i < pictureList.size(); i++) {
                    MessageBean beans = pictureList.get(i);
                    filePath = beans.msgContent;
                    photofile = new File(filePath);
                    exifRotation = CropUtil.getExifRotation(photofile);
                    mImgBmp = CropUtil.rotaingImageView(
                            exifRotation,
                            ImageUtils.decodeBitmapFromSDCard(
                                    filePath,
                                    1080,
                                    1920
                            )
                    );
                    if (mImgBmp == null) {
                        LogCatLog.e("Test", "解析图片失败,开始下一张 " + filePath);
                        mImgBmp = photoModelList.get(i).getmBitmap();
                    }
                    LogCatLog.e("Test", "解析图片成功" + filePath);

                    final String fileName;
                    if (filePath.endsWith(".gif")) {
                        fileName = filePath.substring(
                                filePath.lastIndexOf("/") + 1,
                                filePath.lastIndexOf(".")
                        ) + ".gif";
                    } else {
                        fileName = filePath.substring(
                                filePath.lastIndexOf("/") + 1,
                                filePath.lastIndexOf(".")
                        ) + ".webp";
                    }

                    LogCatLog.e("Test", "fileName=" + fileName);

                    mThumbBmp = CropUtil.rotaingImageView(
                            exifRotation,
                            ImageUtils.decodeThumbnailsBitmap(filePath)
                    );//缩略图
                    File saveFile = new File(FileUtils.IMG_SAVE_PATH + fileName);//上传图片
                    File saveThumbFile = new File(
                            FileUtils.IMG_THUMBNAIL_SAVE_PATH + fileName);//保存的缩略图

                    if (mThumbBmp != null) {

                        if (fileName.endsWith(".gif")) {
                            if (!FileUtils.getInstance().isExists(FileUtils.IMG_SAVE_PATH + fileName)) {
                                FileUtils.getInstance().copyFile(filePath, FileUtils.IMG_SAVE_PATH + fileName);
                            }
                            if (!FileUtils.getInstance().isExists(FileUtils.IMG_THUMBNAIL_SAVE_PATH + fileName)) {
                                FileUtils.getInstance().copyFile(filePath, FileUtils.IMG_THUMBNAIL_SAVE_PATH + fileName);
                            }
                        } else if (!filePath.endsWith(".webp") && !filePath.endsWith(".gif")) {//不是webp格式需要转
                            WebPUtil.with(context).imageToWebp(mImgBmp, saveFile);//保存大图
                            WebPUtil.with(context).imageToWebp(mThumbBmp, saveThumbFile);//保存缩略图
                        } else {
                            if (!FileUtils.getInstance().isExists(FileUtils.IMG_SAVE_PATH + fileName)) {

                                ImageUtils.savePhotoToSDCard(mImgBmp, FileUtils.IMG_SAVE_PATH, fileName);
                            }
                            if (!FileUtils.getInstance().isExists(FileUtils.IMG_THUMBNAIL_SAVE_PATH + fileName)) {

                                ImageUtils.savePhotoToSDCard(mThumbBmp, FileUtils.IMG_THUMBNAIL_SAVE_PATH, fileName);
                            }
                        }
                    }

                    if (mThumbBmp != null && !mThumbBmp.isRecycled()) {
                        mThumbBmp.recycle();
                        mThumbBmp = null;
                    }
                    if (mImgBmp != null && !mImgBmp.isRecycled()) {
                        mImgBmp.recycle();
                        mImgBmp = null;
                    }
                    beans.msgContent = FileUtils.IMG_SAVE_PATH + fileName;
                    SendMsgController.getInstance().addToMessage(beans, beans.msgKey);// 添加图片文件
                }
            }
        });


    }


}
