package com.xianglin.fellowvillager.app.chat;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.chat.controller.ChatManager;
import com.xianglin.fellowvillager.app.chat.controller.PrivateMessageCallBack;
import com.xianglin.fellowvillager.app.chat.widget.TouchImageView.TouchImageView;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.longlink.XLConversation;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.mobile.common.filenetwork.model.AddressManager;
import com.xianglin.mobile.common.info.DeviceInfo;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@Fullscreen
@EActivity(R.layout.activity_show_big_image)
public class ShowBigImageActivity extends BaseActivity {

    @ViewById(R.id.image)
    TouchImageView touchImageView;
    @ViewById(R.id.tv_count)
    TextView tv_count;

    XLConversation xlConversation;
    MessageBean messageBean;
    @Extra
    int chatType;
    @Extra
    String msgKey;
    @Extra
    String toChatId;
    @Extra
    String currentFigureId;
    @Extra
    String imgPath;
    @AfterViews
    void initView(){

        LogCatLog.e("Test", "bigBitmap imgPath=" + imgPath);
        if(imgPath.contains(".")){
            Bitmap bigBitmap=ImageUtils.decodeBitmapFromSDCard(imgPath,
                    DeviceInfo.getInstance().getmScreenWidth(),
                    DeviceInfo.getInstance().getmScreenHeight());
            if(bigBitmap!=null)
              touchImageView.setImageBitmap(bigBitmap);
        }else{
            Bitmap bmp=ImageUtils.decodeBitmapFromSDCard(FileUtils.IMG_SAVE_PATH
                            + AddressManager.addressManager.env+"_"+ imgPath + ".webp",
                    DeviceInfo.getInstance().getmScreenWidth(),
                    DeviceInfo.getInstance().getmScreenHeight());
            if(bmp!=null)
              touchImageView.setImageBitmap(bmp);
        }

        if(chatType== BorrowConstants.CHATTYPE_SINGLE){
            String contactId= ContactDBHandler.getContactId(toChatId, currentFigureId);
            xlConversation= ChatManager.getInstance().getConversation(contactId,
                    false);
            List<MessageBean> messageBeanList= xlConversation.getAllMessages();
            for(int i=0;i<messageBeanList.size();i++){
                if(messageBeanList.get(i).msgKey.equals(msgKey)){
                    messageBean=messageBeanList.get(i);
                    break;
                }
            }
            if(messageBean!=null&&messageBean.isPrivate()){
                tv_count.setVisibility(View.VISIBLE);
                setPrivateMessageReceiveCallback(tv_count, messageBean);
            }else{
                tv_count.setVisibility(View.GONE);
            }

        }else{
            tv_count.setVisibility(View.GONE);
        }
    }
    @Click(R.id.image)
    void imgClick(){
        if(messageBean!=null)
            resumeOtherPrivateMsg(messageBean);
        finish();
    }
    /**
     * 设置私密聊天接收回调接口
     * @param messageBean
     */
    private void setPrivateMessageReceiveCallback(
            final TextView tv_count,
            final MessageBean messageBean
    ) {
        PrivateMessageCallBack p = new PrivateMessageCallBack() {
            @Override
            public void onStart() {
                LogCatLog.d(TAG, "结束计时,onStart:MessageBean=" + messageBean.msgContent
                        + " lifetime=" + messageBean.lifetime + "s");
            }

            @Override
            public void onPause(MessageBean messageBean) {
                LogCatLog.d(TAG, "结束计时,onPause:MessageBean=" + messageBean.msgContent
                        + " lifetime=" + messageBean.lifetime + "s");

            }

            @Override
            public void onEnd(MessageBean messageBean) {
                LogCatLog.d(
                        TAG,
                        "结束计时,onEnd:MessageBean=" + messageBean.msgContent
                                + " lifetime=" + messageBean.lifetime + "s"
                );
            }

            /**
             * 执行中回调方法
             * @param lefttime 总生存时间
             * @param percentTime 百分比时间
             * @param pictime 图片秒数时间
             * @param messageBean
             */
            @Override
            public void onProgress(
                    final int lefttime,
                    final int percentTime,
                    final int pictime,
                    final MessageBean messageBean) {
                LogCatLog.d(
                        TAG,
                        "结束计时,onProgress:MessageBean=" + messageBean.msgContent
                                + " lifetime=" + messageBean.lifetime + "s"
                );
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_count.setText(pictime+"");
                        if(pictime<=0){
                            resumeOtherPrivateMsg(messageBean);
                            finish();
                        }
                    }
                });
            }
        };
        messageBean.setBigImageMessageStatusCallBack(p);
    }

    /**
     * 恢复其他所有私密消息计时
     * @param messageBean
     */
    private void resumeOtherPrivateMsg(MessageBean messageBean) {
        if (messageBean == null) {
            return;
        }
        List<MessageBean> allMessages = ChatManager.getInstance().getAllMessageAboutSomeOne(
                toChatId,
                currentFigureId
        );

        if (allMessages == null) {
            return;
        }
        for (MessageBean bean :
                allMessages) {
            if (bean == null) {
                continue;
            }
            if (!bean.isPrivate()) {
                continue;
            }
            if (messageBean.msgKey.equals(bean.msgKey)) {
                continue;
            }
            bean.isPrivatePause = false;
        }
    }
}
