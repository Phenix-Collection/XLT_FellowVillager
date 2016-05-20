package com.xianglin.fellowvillager.app.fragment;

import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.adpter.ExpressionAdapter;
import com.xianglin.fellowvillager.app.chat.adpter.ExpressionPagerAdapter;
import com.xianglin.fellowvillager.app.chat.widget.ExpandGridView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;


/**
 * 聊天页底部表情片段
 * @author zhanglisan
 * @version 1.0 2016-1-12
 */
@EFragment(R.layout.fragment_chat_menu_emoji)
public class ChatFaceFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private List<View> mViews;
    private EmoSelectListener mListener;
    /**表情资源数量*/
    private static final int EMO_RES_NUM = 99;
    /**删除图标的文件名称*/
    private static final String DELETE_EXPRESSION = "delete_expression";

    @ViewById(R.id.vp_emoji)
    ViewPager mEmoVP;

    @ViewById(R.id.dot_1)
    ImageView mDot1;

    @ViewById(R.id.dot_2)
    ImageView mDot2;

    @ViewById(R.id.dot_3)
    ImageView mDot3;

    @ViewById(R.id.dot_4)
    ImageView mDot4;

    @ViewById(R.id.dot_5)
    ImageView mDot5;

    @AfterViews
    void assignViews() {
        // 表情list
        List<String> reslist = getExpressionRes(EMO_RES_NUM);
        // 初始化表情viewpager
        mViews = new ArrayList<View>();
        View gv1 = getGridChildView(1, reslist);
        View gv2 = getGridChildView(2, reslist);
        View gv3 = getGridChildView(3, reslist);
        View gv4 = getGridChildView(4, reslist);
        View gv5 = getGridChildView(5, reslist);
        mViews.add(gv1);
        mViews.add(gv2);
        mViews.add(gv3);
        mViews.add(gv4);
        mViews.add(gv5);
        mEmoVP.setAdapter(new ExpressionPagerAdapter(mViews));
        mEmoVP.addOnPageChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mEmoVP.removeOnPageChangeListener(this);
    }

    /**
     * 获取表情的gridview的子view
     *
     * @param i
     * @return
     */
    private View getGridChildView(int i, List<String> reslist) {
        View view = View.inflate(
                mContext,
                R.layout.expression_gridview,
                null
        );
        if (reslist == null || reslist.isEmpty()) {
            return view;
        }
        ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
        List<String> list = new ArrayList<String>();
        if (i == 1) {
            List<String> list1 = reslist.subList(0, 20);
            list.addAll(list1);
            list.add(DELETE_EXPRESSION);
        } else if (i == 2) {
            list.addAll(reslist.subList(20, 40));
            list.add(DELETE_EXPRESSION);
        } else if (i == 3) {
            list.addAll(reslist.subList(40, 60));
            list.add(DELETE_EXPRESSION);
        } else if (i == 4) {
            list.addAll(reslist.subList(60, 80));
            list.add(DELETE_EXPRESSION);
        } else if (i == 5) {
            list.addAll(reslist.subList(80, 100));
            list.add(DELETE_EXPRESSION);
        }
        final ExpressionAdapter expressionAdapter = new ExpressionAdapter(
                mContext,
                1,
                list
        );
        gv.setAdapter(expressionAdapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String fileName = expressionAdapter.getItem(position);
                if (TextUtils.isEmpty(fileName)) {
                    return;
                }
                mListener.addEmoToSend(fileName);

            }
        });
        return view;
    }

    /**
     * 获取表情资源
     * @param getSum 表情数目
     * @return 表情图片文件名列表
     */
    private List<String> getExpressionRes(int getSum) {
        List<String> reslist = new ArrayList<String>();
        for (int x = 0; x <= getSum; x++) {
            String filename = "em_0" + x;

            reslist.add(filename);

        }
        return reslist;

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentDot(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    /**
     * 设置底部小圆点状态
     * @param position
     */
    private void setCurrentDot(int position) {
        if (position < 0 || position > mViews.size() - 1) {
            return;
        }
        mDot1.setImageResource(R.drawable.dot_light);
        mDot2.setImageResource(R.drawable.dot_light);
        mDot3.setImageResource(R.drawable.dot_light);
        mDot4.setImageResource(R.drawable.dot_light);
        mDot5.setImageResource(R.drawable.dot_light);
        if (position == 0) {
            mDot1.setImageResource(R.drawable.dot_dark);
            return;
        }
        if (position == 1) {
            mDot2.setImageResource(R.drawable.dot_dark);
            return;
        }
        if (position == 2) {
            mDot3.setImageResource(R.drawable.dot_dark);
            return;
        }
        if (position == 3) {
            mDot4.setImageResource(R.drawable.dot_dark);
            return;
        }
        if (position == 4) {
            mDot5.setImageResource(R.drawable.dot_dark);
            return;
        }

    }

    public void setEmoSelectListener(EmoSelectListener listener) {
        this.mListener = listener;
    }

    /**
     * 表情选择监听器
     */
    public interface EmoSelectListener {
        /**
         * 将选中的表情添加到输入栏
         * @param fileName 表情文件名
         */
        void addEmoToSend(String fileName);
    }
}
