package com.xianglin.fellowvillager.app.widget;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.CaptureActivity;
import com.xianglin.fellowvillager.app.activity.group.GroupAddMemberActivity_;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;


/**
 * 综合界面顶部title切换频道的popuWindow
 *
 * @author wchen
 */
public class MenuPopuWindow {
    public PopupWindow mPopupWindow;
    private Context mContext;

    public MenuPopuWindow(Context mContext) {
        this.mContext = mContext;
        makePopupWindow(getPopContentView());
    }

    private View getPopContentView() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.menu_main_top_right_layout, null);
        LinearLayout llSweepQrCode = (LinearLayout) view.findViewById(R.id.ll_sweep_qrcode);
        LinearLayout llChatGroup = (LinearLayout) view.findViewById(R.id.ll_chat_group);
        ImageView ivClose = (ImageView) view.findViewById(R.id.iv_menu_close);
        llChatGroup.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               GroupAddMemberActivity_.intent(mContext).addOrJoin(BorrowConstants.CHATTYPE_ADD).start();
                                               mPopupWindow.dismiss();
                                           }
                                       }

        );
        llSweepQrCode.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 mContext.startActivity(new Intent(mContext, CaptureActivity.class));
                                                 mPopupWindow.dismiss();
                                             }
                                         }

        );
        ivClose.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View v) {
                                           mPopupWindow.dismiss();
                                       }
                                   }

        );


        return view;


    }

    public void dismissPopup() {
        if (null != mPopupWindow)
            mPopupWindow.dismiss();
    }

    private PopupWindow makePopupWindow(View ContentView) {
        mPopupWindow = new PopupWindow(ContentView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, false);
        // 设置点击窗口外边窗口消失
        // 设置此参数获得焦点，否则无法点击
//        mPopupWindow.setAnimationStyle(R.style.popwindowShowDismess);

        return mPopupWindow;
    }


}
