/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.utils.ViewHolder;
import com.xianglin.xlappcore.common.service.facade.vo.RandomXlidVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 注册时的获取的乡邻id列表
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/12 10:53  XLXZ Exp $
 */

public class XlidListAdapter extends XLBaseAdapter<RandomXlidVo> {


    private RandomXlidVo xlidSelected; //选择中的id

    /**
     * 默认选择中的 id ,   -1为不选中
     */
    private int mXIselectedWithPosition = -1; //选择控件的id

    public final static int MAXIMUM = 3; //可供选择的id数量

    private List<RandomXlidVo> backupData = new ArrayList<RandomXlidVo>();

    public XlidListAdapter(List<RandomXlidVo> list, Context context) {
        super(list, context);
        init();
    }

    public void init() {
        mlist.clear();
        RandomXlidVo randomXlidVo = new RandomXlidVo();
        randomXlidVo.setXLID(0L);
        mlist.add(randomXlidVo);
        mlist.add(randomXlidVo);
        mlist.add(randomXlidVo);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {

        if (super.getCount() >= MAXIMUM) {
            return MAXIMUM;
        } else {
            return super.getCount();
        }
    }

    public RandomXlidVo getXlidSelected() {
        return xlidSelected;
    }


    /**
     * -1时默认不选择图片
     *
     * @param XIselectedWithPosition
     */
    public void setXIselectedWithPosition(int XIselectedWithPosition) {
        this.mXIselectedWithPosition = XIselectedWithPosition;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_xlid, parent, false);
        }

        TextView tvXlid = ViewHolder.get(convertView, R.id.tv_xlid);
        ImageView ivIcon = ViewHolder.get(convertView, R.id.iv_xlid_icon);

        long id = getItem(position).getXLID();

        tvXlid.setText(mContext.getString(R.string.prefix_id) + (id == 0 ? "" : id));

        if (position == 0) {
            convertView.setBackgroundResource(R.drawable.btn_choose_top_dbg);
        } else if (position == getCount() - 1) {
            convertView.setBackgroundResource(R.drawable.btn_choose_bottom_dbg);
        } else {
            convertView.setBackgroundResource(R.drawable.btn_choose_mid_dbg);
        }

        if (mXIselectedWithPosition == position) {
            xlidSelected = getItem(position);
            ivIcon.setImageResource(R.drawable.btn_selectw_xlid_selected_new);
        } else {
            //选中状态
            ivIcon.setImageResource(R.drawable.btn_selectw_xlid_normal_new);
        }

        return convertView;
    }

    /**
     * 每次删掉前3个
     */
    public boolean nextData() {
        xlidSelected = null;
        mXIselectedWithPosition=-1;
        if (mlist.size() != 0){
            backupData.addAll(mlist.subList(0, MAXIMUM));
            mlist.subList(0, MAXIMUM).clear();
        }
        if (mlist.size() == 0) {
            return false;//可以重新请求结果
        }
        notifyDataSetChanged();
        return true;
    }

    /**
     * 注入新的xlid 列表,累加后再次循环
     */
    public void setNewData(List<RandomXlidVo> data) {
        mlist.clear();
        if(data != null && data.size()>0 ){
            data.addAll(backupData);
            mlist.addAll(data);
        } else {
            mlist.addAll(backupData);
        }
        backupData.clear();
        notifyDataSetChanged();
    }

}
