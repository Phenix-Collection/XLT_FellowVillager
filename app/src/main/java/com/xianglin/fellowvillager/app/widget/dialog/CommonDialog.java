package com.xianglin.fellowvillager.app.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;

/**
 * 通用dialog
 *
 * @author bruce yang
 * @version v 1.0.0 2016/3/19
 */
public class CommonDialog extends Dialog {

    public CommonDialog(Context context, int theme) {
        super(context, theme);
    }

    public CommonDialog(Context context) {
        super(context);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
        private Context context;
        private String title;
        private String message; // 对话框内容
        private String backButtonText; // 对话框返回按钮文本
        private String confirmButtonText; // 对话框确定文本

        // 对话框按钮监听事件
        private OnClickListener cancelButtonClickListener, confirmButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * 是否显示title
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * 使用字符串设置对话框消息
         *
         * @param
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * 使用资源设置对话框消息
         *
         * @param
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * 设置自定义的对话框内容
         *
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            return this;
        }

        /**
         * 设置back按钮的事件和文本
         *
         * @param backButtonText
         * @param listener
         * @return
         */
        public Builder setCancleButton(int backButtonText, OnClickListener listener) {
            this.backButtonText = (String) context.getText(backButtonText);
            this.cancelButtonClickListener = listener;
            return this;
        }

        /**
         * 设置back按钮的事件和文本
         *
         * @param backButtonText
         * @param listener
         * @return
         */
        public Builder setCancleButton(String backButtonText, OnClickListener listener) {
            this.backButtonText = backButtonText;
            this.cancelButtonClickListener = listener;
            return this;
        }

        /**
         * 设置确定按钮事件和文本
         *
         * @param confirmButtonText
         * @param listener
         * @return
         */
        public Builder setConfirmButton(int confirmButtonText, OnClickListener listener) {
            this.confirmButtonText = (String) context.getText(confirmButtonText);
            this.confirmButtonClickListener = listener;
            return this;
        }

        /**
         * 设置确定按钮事件和文本
         *
         * @param confirmButtonText
         * @param listener
         * @return
         */
        public Builder setConfirmButton(String confirmButtonText, OnClickListener listener) {
            this.confirmButtonText = confirmButtonText;
            this.confirmButtonClickListener = listener;
            return this;
        }

        /**
         * 创建自定义的对话框
         */
        public CommonDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 实例化自定义的对话框主题
            final CommonDialog dialog = new CommonDialog(context, R.style.Dialog);

            View layout = inflater.inflate(R.layout.dialog_common_layout, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            TextView dialog_title = (TextView) layout.findViewById(R.id.dialog_title);
            if (title == null) {
                dialog_title.setVisibility(View.GONE);
            } else {
                dialog_title.setText(title);
            }

            TextView dialog_message = (TextView) layout.findViewById(R.id.dialog_message);
            dialog_message.setText(message);

            // 设置返回按钮事件和文本
            if (backButtonText != null) {
                Button bckButton = ((Button) layout.findViewById(R.id.dialog_back));
                bckButton.setText(backButtonText);

                if (cancelButtonClickListener != null) {
                    bckButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            cancelButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
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

    }
}
