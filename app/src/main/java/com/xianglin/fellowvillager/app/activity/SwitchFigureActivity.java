package com.xianglin.fellowvillager.app.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.db.FigureDbHandler;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.widget.CircleImage;
import com.xianglin.fellowvillager.app.widget.FloatingActionMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 切换角色对话框页面
 * Created by zhanglisan on 2/29/16.
 */
public class SwitchFigureActivity extends Activity {

    private CircleImage mFigureBtn;
    private FloatingActionMenu circleMenu;
    private FrameLayout mRootLayout;

    public static final int RESULT_CODE_OK = 0X100;
    private static final String ALL_FIGURE = "ALL_FIGURE";
    private int figureIconSize; // 角色小图标大小
    /**根据角色取得的未读消息map*/
    private HashMap<String, Long> unReadMsgMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_figure_switch);
        figureIconSize = getResources().getDimensionPixelSize(R.dimen.dimen_33_dip);
        mFigureBtn = (CircleImage) findViewById(R.id.dialog_figure_btn);
        mRootLayout = (FrameLayout) findViewById(R.id.root_layout);
        unReadMsgMap = getUnReadMsg();
        loadCurrentFigure();
        List<FigureMode> figures = getFigures();
        ContactManager.getInstance().sortFigureByCreateTime(figures);
        initFigureAnim(figures);
        mRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (circleMenu == null) {
                    return;
                }
                if (circleMenu.isOpen()) {
                    circleMenu.close();
                }
                mFigureBtn.setVisibility(View.INVISIBLE);
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (circleMenu.isOpen()) {
                                    mRootLayout.performClick();
                                } else {
                                    finish();
                                }
                            }
                        },
                        500
                );
            }
        });

        mFigureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    /**
     * 获取未读消息
     */
    private HashMap<String, Long> getUnReadMsg() {
        FigureDbHandler figureDbHandler = new FigureDbHandler(this);
        return figureDbHandler.queryFigureWithMsgCount();
    }


    /**
     * 加载当前角色
     */
    private void loadCurrentFigure() {
        FigureMode currentFigure = ContactManager.getInstance().getCurrentFigure();
        if (currentFigure == null) {
            mFigureBtn.setImageResource(R.drawable.all_figure);
        } else {
            ImageUtils.showCommonImage(
                    this,
                    mFigureBtn,
                    FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                    currentFigure.getFigureImageid(),
                    R.drawable.head
            );
        }
    }

    /**
     * 获得除本身之外全部角色列表
     * @return 除本身之外所有角色的列表
     */
    private List<FigureMode> getFigures() {
        Map<String, FigureMode> figureMap = ContactManager.getInstance().getFigureTable();
        if (figureMap == null || figureMap.size() == 0) {
            return null;
        }
        FigureMode currentFigure = ContactManager.getInstance().getCurrentFigure();
        ArrayList<FigureMode> figureModes = new ArrayList<>();
        for (Map.Entry<String, FigureMode> entry:
             figureMap.entrySet()) {
            FigureMode value = entry.getValue();
            if (value == null
                    || value == currentFigure
                    || value.getFigureStatus() == FigureMode.Status.FREEZE) {
                continue;
            }
            figureModes.add(value);
        }
        return figureModes;
    }

    /**
     * 获得角色视图
     * @param headerImgId 角色头像id
     * @return 角色视图
     */
    private View getSubView(String headerImgId) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                figureIconSize,
                figureIconSize
        );
        FrameLayout figureView = (FrameLayout) LayoutInflater.from(this).inflate(
                R.layout.item_figure_red_dot,
                null,
                false
        );
        CircleImage circleImg = (CircleImage) figureView.findViewById(R.id.figure_img);
        ImageView redDotImg = (ImageView) figureView.findViewById(R.id.red_dot);
        figureView.setLayoutParams(params);
        circleImg.setLayoutParams(params);
        if (TextUtils.isEmpty(headerImgId)) {
            circleImg.setImageResource(R.drawable.head);
        } else if (headerImgId.equals(ALL_FIGURE)) {
            circleImg.setImageResource(R.drawable.all_figure);
        } else {
            ImageUtils.showCommonImage(
                    this,
                    circleImg,
                    FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                    headerImgId,
                    R.drawable.head
            );
        }

        return figureView;

    }


    /**
     * 显示角色切换
     */
    private void initFigureAnim(List<FigureMode> figureModes) {
        if (figureModes == null || figureModes.size() == 0) {
            finish();
            return;
        }
        ArrayList<View> figureViews = new ArrayList<>();
        FigureMode currentFigure = ContactManager.getInstance().getCurrentFigure();
        if (currentFigure != null) { // 当前角色部位全部时,菜单添加全部角色菜单
            FrameLayout rootView = (FrameLayout) getSubView(ALL_FIGURE);
            CircleImage defaultView = (CircleImage) rootView.findViewById(R.id.figure_img);
            if (defaultView != null) { // 全部角色
                defaultView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFigureBtn.setImageResource(R.drawable.all_figure);
                        ContactManager.getInstance().switchCurrentUserFigure(null);
                        setResult(RESULT_CODE_OK);
                        onBackPressed();
                    }
                });
            }
            figureViews.add(rootView);
        }

        for (final FigureMode figureMode:
             figureModes) {
            String imgPath = figureMode.getFigureImageid();
            final FrameLayout rootView = (FrameLayout) getSubView(imgPath);
            CircleImage subView = (CircleImage) rootView.findViewById(R.id.figure_img);
            if (subView == null) {
                continue;
            }
            subView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { // 单个角色
                    ImageUtils.showCommonImage(
                            SwitchFigureActivity.this,
                            mFigureBtn,
                            FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                            figureMode.getFigureImageid(),
                            R.drawable.head
                    );
                    ContactManager.getInstance().switchCurrentUserFigure(figureMode);
                    setResult(RESULT_CODE_OK);
                    onBackPressed();
                }
            });
            if (unReadMsgMap != null) {
                for (Map.Entry<String, Long> entry :
                        unReadMsgMap.entrySet()) {
                    if (entry == null) {
                        continue;
                    }
                    if (figureMode != null
                            && figureMode.getFigureUsersid() != null
                            && figureMode.getFigureUsersid().equals(entry.getKey())
                            && (entry.getValue() > 0L)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rootView.findViewById(R.id.red_dot).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }

            figureViews.add(rootView);
        }

        int startAngle;
        int endAngle;
        if (figureViews.size() == 1) {
            startAngle = -95;
            endAngle = -85;
        } else if (figureViews.size() == 2) {
            startAngle = -110;
            endAngle = -70;
        } else if (figureViews.size() == 3) {
            startAngle = -130;
            endAngle = -50;
        } else if (figureViews.size() == 4) {
            startAngle = -140;
            endAngle = -40;
        } else {
            startAngle = -160;
            endAngle = -20;
        }

        int radius = getResources().getDimensionPixelSize(R.dimen.dimen_100_dip);
        if (figureViews.size() > 7) {
            radius += (figureViews.size() - 7) * getResources().getDimensionPixelSize(R.dimen.dimen_8_dip);
        }
        int maxRadius = DeviceInfoUtil.getWidth(this) / 2 - figureIconSize / 2;
        if (radius > maxRadius) {
            radius = maxRadius;
        }

        circleMenu = new FloatingActionMenu.Builder(this)
                .setStartAngle(startAngle) // A whole circle!
                .setEndAngle(endAngle)
                .setRadius(radius)
                .addSubActionView(figureViews)
                .attachTo(mFigureBtn)
                .build();

        if (circleMenu == null) {
            return;
        }

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        circleMenu.open();
                    }
                },
                100
        );

    }

    @Override
    public void onBackPressed() {
        mRootLayout.performClick();
    }
}
