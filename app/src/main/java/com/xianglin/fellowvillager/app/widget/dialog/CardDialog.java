package com.xianglin.fellowvillager.app.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.mobile.common.filenetwork.listener.FileMessageListener;
import com.xianglin.mobile.common.filenetwork.model.FileTask;
import com.xianglin.mobile.common.logging.LogCatLog;


/**
 * Created by ex-zhangxiang on 2016/1/6.
 */
public class CardDialog extends Dialog {

    public CardDialog(Context context, int theme) {
        super(context, theme);
    }

    public CardDialog(Context context) {
        super(context);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
        private Context context;
        private String title; // 对话框标题
        private String message; // 对话框内容
        private String backButtonText; // 对话框返回按钮文本
        private String confirmButtonText; // 对话框确定文本
        private View contentView;
        private String fileId;
        private String xlId;
        private String type;//0:个人   1:群

        private boolean isShow = true;

        // 对话框按钮监听事件
        private DialogInterface.OnClickListener
                backButtonClickListener,
                confirmButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }
        public Builder(Context context, boolean isShow) {
            this.context = context;
            this.isShow = isShow;
        }

        /**
         * 使用字符串设置对话框消息
         * @param
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * 使用资源设置对话框消息
         * @param
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * 使用资源设置对话框标题信息
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * 使用字符串设置对话框标题信息
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setFileId(String fileId) {
            this.fileId = fileId;
            return this;
        }

        public Builder setType(String type){
            this.type = type;
            return this;
        }

        public Builder setXlId(String xlId) {
            this.xlId = xlId;
            return this;
        }

        /**
         * 设置自定义的对话框内容
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * 设置back按钮的事件和文本
         * @param backButtonText
         * @param listener
         * @return
         */
        public Builder setBackButton(int backButtonText, DialogInterface.OnClickListener listener) {
            this.backButtonText = (String)context.getText(backButtonText);
            this.backButtonClickListener = listener;
            return this;
        }

        /**
         * 设置back按钮的事件和文本
         * @param backButtonText
         * @param listener
         * @return
         */
        public Builder setBackButton(String backButtonText, DialogInterface.OnClickListener listener) {
            this.backButtonText = backButtonText;
            this.backButtonClickListener = listener;
            return this;
        }

        /**
         * 设置确定按钮事件和文本
         * @param confirmButtonText
         * @param listener
         * @return
         */
        public Builder setConfirmButton(int confirmButtonText, DialogInterface.OnClickListener listener) {
            this.confirmButtonText = (String)context.getText(confirmButtonText);
            this.confirmButtonClickListener = listener;
            return this;
        }

        /**
         * 设置确定按钮事件和文本
         * @param confirmButtonText
         * @param listener
         * @return
         */
        public Builder setConfirmButton(String confirmButtonText, DialogInterface.OnClickListener listener) {
            this.confirmButtonText = confirmButtonText;
            this.confirmButtonClickListener = listener;
            return this;
        }

        /**
         * 创建自定义的对话框
         */
        public CardDialog create() {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 实例化自定义的对话框主题
            final CardDialog dialog = new CardDialog(context, R.style.Dialog);

            View layout = inflater.inflate(R.layout.card_dialog, null);
            dialog.addContentView(layout,
                    new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            TextView dialog_title = (TextView) layout.findViewById(R.id.dialog_title);
            // 设置对话框标题
            dialog_title.setText(title);

            if(isShow){
                ImageView imgView = (ImageView) layout.findViewById(R.id.dialog_head);
                imgView.setVisibility(View.VISIBLE);
                if ((PersonSharePreference.getUserID() + "").equals(xlId)) {//本人头像
                    imgView.setImageBitmap(ImageUtils.decodeThumbnailsBitmap(FileUtils
                            .IMG_CACHE_HEADIMAGE_PATH + PersonSharePreference.getUserID() + ".webp"));
                } else if (fileId == null) {//文件ID为空设置默认头像
                    if (TextUtils.isEmpty(type) || "0".equals(type)) {
                        imgView.setImageResource(R.drawable.head);
                    } else if ("1".equals(type)){
                        imgView.setImageResource(R.drawable.group_icon);
                    }
                } else if (ImageUtils.isLocalImg(fileId)) {//头像在本地已存在
                    ImageUtils.showLocalImg(context, imgView, fileId);
                } else {//通过下载显示头像
                    downloadImageHeader((Activity) context, imgView);
                }

                // 设置对话框内容
                if (message != null && xlId != null) {
                    TextView dlgMsg = (TextView)layout.findViewById(R.id.dialog_message);
                    dlgMsg.setText(message);
                    dlgMsg = (TextView)layout.findViewById(R.id.dialog_xlid);
                    dlgMsg.setText("乡邻号：" + xlId);
                } else if (contentView != null) {
                    // if no message set
                    // 如果没有设置对话框内容，添加contentView到对话框主体
                    ((LinearLayout) layout.findViewById(R.id.dialog_content)).removeAllViews();
                    ((LinearLayout) layout.findViewById(R.id.dialog_content)).addView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                }
            }else{
                dialog_title.setGravity(Gravity.CENTER);
                TextView dialog_message = (TextView)layout.findViewById(R.id.dialog_message);
                dialog_message.setGravity(Gravity.CENTER);
                dialog_message.setText(message);
            }


            // 设置返回按钮事件和文本
            if (backButtonText != null) {
                Button bckButton = ((Button) layout.findViewById(R.id.dialog_back));
                bckButton.setText(backButtonText);

                if (backButtonClickListener != null) {
                    bckButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            backButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.dialog_back).setVisibility(View.GONE);
            }

            // 设置确定按钮事件和文本
            if (confirmButtonText != null) {
                Button cfmButton = ((Button) layout.findViewById(R.id.dialog_confirm));
                cfmButton.setText(confirmButtonText);

                if (confirmButtonClickListener != null) {
                    cfmButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            confirmButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            } else {
                layout.findViewById(R.id.dialog_confirm).setVisibility(View.GONE);
            }
            dialog.setContentView(layout);
            return dialog;
        }

        void downloadImageHeader(final Activity activity, final ImageView imgView) {
            FileUtils.downloadFile(context, PersonSharePreference.getUserID(), fileId + "",
                    FileUtils.IMG_CACHE_HEADIMAGE_PATH, new FileMessageListener<FileTask>() {
                        @Override
                        public void success(int statusCode, final FileTask fileTask) {
                            LogCatLog.i("fileName", "------------------" + fileTask.fileName);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImageUtils.loadImage(imgView, "file://" + FileUtils.IMG_CACHE_HEADIMAGE_PATH + fileTask
                                            .fileName, activity.getResources().getDrawable(R.drawable.head));
                                }
                            });
                        }

                        @Override
                        public void handleing(int statusCode, FileTask fileTask) {
                        }

                        @Override
                        public void failure(int statusCode, FileTask fileTask) {
                        }
                    });
        }
    }
}