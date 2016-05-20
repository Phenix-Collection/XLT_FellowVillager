package com.xianglin.fellowvillager.app.recyclerview.viewholder;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xianglin.fellowvillager.app.activity.BaseActivity;

/**
 * viewholder基类,用于recyclerview的显示
 * Created by zhanglisan on 16/3/17.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

    protected int position;
    protected BaseActivity activity;
    protected Resources res;
    protected View rootView;
    protected View.OnClickListener clickListener;
    private Object model;

    public BaseViewHolder(
            View view,
            BaseActivity activity
    ) {
        super(view);
        this.activity = activity;
        this.res = activity.getResources();
        findViews(view);
        setListeners();
    }

    public BaseViewHolder(
            View view,
            BaseActivity activity,
            View.OnClickListener clickListener
    ) {
        this(
                view,
                activity
        );
        this.clickListener = clickListener;
    }

    protected void findViews(View view) {
        rootView = view;
    }

    protected void setListeners() {
    }

    public void updateView(int position) {
        this.position = position;
    }

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {
            v.setTag(getModel());
            clickListener.onClick(
                    v
            );
        }
    }


}