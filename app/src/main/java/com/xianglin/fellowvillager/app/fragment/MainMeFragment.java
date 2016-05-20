package com.xianglin.fellowvillager.app.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.MainActivity;
import com.xianglin.fellowvillager.app.activity.WebviewActivity_;
import com.xianglin.fellowvillager.app.activity.personal.PersonalInfoActivity;
import com.xianglin.fellowvillager.app.activity.personal.PersonalInfoActivity_;
import com.xianglin.fellowvillager.app.activity.personal.QRCodeActivity_;
import com.xianglin.fellowvillager.app.activity.personal.SettingActivity_;
import com.xianglin.fellowvillager.app.adapter.MyPagerAdapter;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.constants.ENVController;
import com.xianglin.fellowvillager.app.db.FigureDbHandler;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.utils.DataDealUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.widget.CircleImage;
import com.xianglin.mobile.common.info.AppInfo;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 */
@EFragment(R.layout.fragment_main_me)
public class MainMeFragment extends BaseFragment {

    @ViewById(R.id.textView_id_me)
    TextView textViewId;
    @ViewById(R.id.textView_name_me)
    TextView textViewName;
    @ViewById(R.id.textView_version_me)
    TextView textViewVersion;

    @ViewById(R.id.imageView_head_me)
    CircleImage imageViewHead;
    @ViewById(R.id.ic_current_role)
    LinearLayout ll_current_role;//单角色

    @ViewById(R.id.rela_all_figure)
    RelativeLayout rela_all_figure;//全部角色
    @ViewById(R.id.all_role)
    ViewPager all_role;
    @ViewById(R.id.iv_before)
    ImageView iv_before;
    @ViewById(R.id.iv_next)
    ImageView iv_next;
    @ViewById(R.id.tv_cur_page)
    TextView tv_cur_page;
    private List<View> figurelist = new ArrayList<View>();
    MyPagerAdapter figureAdapter;

    @ViewById(R.id.rl_shopping)
    RelativeLayout rlShopping;
    String figureId;
    String figureName;
    String figureImgId;
    FigureDbHandler figureDbHandler;
    FigureMode currentFigure;
    List<FigureMode> allFigureList;
    int currentPage = 0;

    /**
     * 显示所有角色
     */
    void showALLFigure() {
        ll_current_role.setVisibility(View.GONE);
        rela_all_figure.setVisibility(View.VISIBLE);
        figurelist.clear();
        allFigureList = figureDbHandler.queryFigure("");
        allFigureList = ContactManager.getInstance().sortAllFigure(allFigureList);
        tv_cur_page.setText("1/" + allFigureList.size());
        LogCatLog.e(TAG, "allFigureList = " + allFigureList);
        for (int i = 0; allFigureList != null && i < allFigureList.size(); i++) {
            final FigureMode figureMode = allFigureList.get(i);
            View view = LayoutInflater.from(mBaseActivity).inflate(R.layout.user_figure_include, null);
            TextView textView_status = (TextView) view.findViewById(R.id.textView_status);
            ImageView imageView_head_me = (ImageView) view.findViewById(R.id.imageView_head_me);
            TextView textView_name_me = (TextView) view.findViewById(R.id.textView_name_me);
            TextView textView_id_me = (TextView) view.findViewById(R.id.textView_id_me);
            ImageUtils.showCommonImage(mActivity,
                    imageView_head_me, FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                    figureMode.getFigureImageid(), R.drawable.head);
            textView_status.setText((figureMode.getFigureStatus() == FigureMode.Status.FREEZE)
                    ? "已冻结" : "");
            textView_status.setVisibility((figureMode.getFigureStatus() == FigureMode.Status.FREEZE)
                    ? View.VISIBLE : View.GONE);

            DataDealUtil.showGenderImg(getActivity(), textView_name_me, figureMode);

            textView_id_me.setText("figure id:" + figureMode.getFigureUsersid());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(getActivity(), PersonalInfoActivity_.class)
                            .putExtra("operateType", BorrowConstants.TYPE_INFO)
                            .putExtra("figureId", "")
                            .putExtra("selectFID", figureMode.getFigureUsersid())
                            .putExtra("figureImgId", figureMode.getFigureImageid()), 1);
                }
            });
            figurelist.add(view);

        }
        if (currentPage == 0) {
            iv_before.setVisibility(View.INVISIBLE);
            iv_next.setVisibility(View.VISIBLE);
        }
        figureAdapter = new MyPagerAdapter(figurelist);
        all_role.setAdapter(figureAdapter);
        all_role.setCurrentItem(currentPage);
        all_role.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                tv_cur_page.setText((currentPage + 1) + "/" + allFigureList.size());
                hideOrShow(currentPage, figurelist.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * 显示或隐藏
     *
     * @param currentPage
     * @param figureListSize
     */
    public void hideOrShow(int currentPage, int figureListSize) {
        LogCatLog.d(TAG, "currentPage=" + currentPage + "figureListSize = " + figureListSize);

        if (currentPage == 0) {
            iv_before.setVisibility(View.INVISIBLE);
            iv_next.setVisibility(View.VISIBLE);
        } else if (currentPage > 0) {
            iv_before.setVisibility(View.VISIBLE);
            iv_next.setVisibility(View.VISIBLE);
            if (currentPage == figureListSize - 1) {
                iv_next.setVisibility(View.INVISIBLE);
                iv_before.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 显示指定角色
     */
    void showFigureById(String mFigureId) {
        ll_current_role.setVisibility(View.VISIBLE);
        rela_all_figure.setVisibility(View.GONE);
        currentFigure = ContactManager.getInstance().getCurrentFigure(mFigureId);
        if (currentFigure == null) {
            return;
        }
        figureImgId = currentFigure.getFigureImageid();
        figureName = currentFigure.getFigureName();
        textViewId.setText("figure id：" + mFigureId);
        LogCatLog.e(TAG, "figureImgId=" + figureImgId);
        DataDealUtil.showGenderImg(getActivity(), textViewName, currentFigure);
        textViewVersion.setText(AppInfo.getInstance().getProductVersion());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageUtils.showCommonImage(getActivity(), imageViewHead,
                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                        figureImgId, R.drawable.head);
            }
        }, 150);

        imageViewHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPersonalInfo();
            }
        });
        ll_current_role.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toPersonalInfo();
            }
        });
    }

    void toPersonalInfo() {
        startActivityForResult(new Intent(getActivity(), PersonalInfoActivity_.class)
                .putExtra("operateType", BorrowConstants.TYPE_INFO)
                .putExtra("figureId", figureId)
                .putExtra("figureImgId", figureImgId), 1);
    }

    /**
     * figureId为空时显示全部
     *
     * @param figureId
     */
    public void setDataByFigureId(String figureId) {
        this.figureId = figureId;
        if (figureId.equals("")) {
            showALLFigure();
        } else {
            showFigureById(figureId);
        }
    }

    @AfterViews
    void init() {
        figureDbHandler = new FigureDbHandler(getActivity());
        setDataByFigureId(ContactManager.getInstance().getCurrentFigureID());
    }

    @Click(R.id.iv_before)
    void clickBefore() {
        if (currentPage > 0) {
            currentPage--;
            all_role.setCurrentItem(currentPage);
        }
    }

    @Click(R.id.iv_next)
    void clickNext() {
        if (currentPage < figurelist.size() - 1) {
            currentPage++;
            all_role.setCurrentItem(currentPage);
        }
    }

    @Click(R.id.rl_qr_code)
    void clickQRCode() {
        FigureMode curFigure = (ContactManager.getInstance().getCurrentFigureID() != "")
                ? ContactManager.getInstance().getCurrentFigure()
                : allFigureList.get(currentPage);
        QRCodeActivity_.intent(mContext).currentUser(curFigure).start();
    }

    @Click(R.id.rl_setting)
    void clickSetting() {
        FigureMode curFigure = (ContactManager.getInstance().getCurrentFigureID() != "")
                ? ContactManager.getInstance().getCurrentFigure()
                : allFigureList.get(currentPage);
        SettingActivity_.intent(mContext).currentUser(curFigure).start();
    }

    @Click(R.id.rl_shopping)
    void clickShopping() {
        startActivity(new Intent(mBaseActivity, WebviewActivity_.class));
    }

    @LongClick(R.id.imageView_head_me)
    void clickImg() {
        tip(ENVController.ENV + "\n" + AppInfo.getInstance().getProductVersion());
    }

    @Override
    public void onResume() {
        super.onResume();
        setDataByFigureId(figureId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PersonalInfoActivity.RESULT_CODE_OK) {
            if (mBaseActivity instanceof MainActivity) {
                ((MainActivity) mBaseActivity).loadCurrentFigure();
            }
        }
    }


}
