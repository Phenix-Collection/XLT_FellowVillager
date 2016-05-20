package com.xianglin.fellowvillager.app.recyclerview.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.recyclerview.viewholder.SameGroupViewHolder;

import java.util.List;

/**
 * 相同联系群列表适配器
 * Created by zhanglisan on 16/3/17.
 */
public class SameGroupAdapter extends RecyclerView.Adapter<SameGroupViewHolder> {
    private BaseActivity activity;
    private List<Group> items;
    private LayoutInflater inflater;

    public SameGroupAdapter(BaseActivity activity, List<Group> items) {
        setHasStableIds(true);
        this.activity = activity;
        this.items = items;
        this.inflater = LayoutInflater.from(activity);
    }

    @Override
    public SameGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SameGroupViewHolder(
                inflater.inflate(
                        R.layout.item_group_in_contact,
                        parent,
                        false
                ),
                activity
        );
    }

    @Override
    public void onBindViewHolder(SameGroupViewHolder holder, int position) {
        holder.setModel(getItem(position));
        holder.updateView(position);
    }

    protected Group getItem(int position) {
        if (items == null || items.size() <= position) {
            return null;
        }
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }
}
