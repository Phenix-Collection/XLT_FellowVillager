package com.xianglin.fellowvillager.app.activity.group;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.adapter.CommonAdapter;
import com.xianglin.fellowvillager.app.adapter.ViewHolder;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.utils.ToastUtils;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * 群设置页面
 * Created by ex-zhangxiang on 2015/11/25.
 */
@EActivity(R.layout.activity_group_information)
public class GroupInformationActivity extends BaseActivity {
    @ViewById(R.id.gview_group_infor)
    GridView gridView;

    @ViewById(R.id.listview_group_info)
    ListView listView;

    private Context mContext;

//    @ViewById(R.id.all_menbers_group_infor)
//    RelativeLayout allMenbers;

   private List<Group> listgroup = new ArrayList<Group>();
   private List<String> listDate = new ArrayList<String>();

    @AfterViews
    void initView() {
        mContext = this;

        TopView topView = (TopView) findViewById(R.id.topview_group_info);
        topView.setAppTitle("聊天信息");
        topView.setLeftImageResource(R.drawable.icon_back);
        topView.setLeftImgOnClickListener();

        initListView();
        initGridView();

    }

    @Click(R.id.all_menbers_group_infor)
    void AllMenbers() {      //查看全部成员
        ToastUtils.toastForShort(mContext, "查看群成员");
    }

    @Click(R.id.btn_delete_group)
    void DeleteGroup() {        //删除并退出本群
        ToastUtils.toastForShort(mContext, "删除群");
    }

    /**
     * 初始化GridView
     */
    private void initGridView() {
        Group.Builder builder = new Group.Builder();
        builder.xlGroupName("优势");
        Group group = builder.build();
        listgroup.add(group);
        listgroup.add(group);

        builder.xlGroupName("");
        group = builder.build();
        listgroup.add(group);
        listgroup.add(group);

        gridView.setAdapter(new CommonAdapter<Group>(this, listgroup, R.layout.item_group_information) {
            @Override
            public void convert(ViewHolder helper, int position) {
                if (listgroup.size() - 1 == position) {
                    helper.setImageResource(R.id.img_group_item, R.drawable.icon_btn_deleteperson);
                } else if (listgroup.size() - 2 == position) {
                    helper.setImageResource(R.id.img_group_item, R.drawable.jy_drltsz_btn_addperson);
                } else {
                    helper.setImageResource(R.id.img_group_item, R.drawable.head);
                }
                helper.setText(R.id.textView_group_item, listgroup.get(position).xlGroupName);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listgroup.size() - 1 == position) {
                    ToastUtils.toastForShort(mContext, "删除");
                } else if (listgroup.size() - 2 == position) {
                    ToastUtils.toastForShort(mContext, "添加");
                } else {
                    ToastUtils.toastForShort(mContext, listgroup.get(position).xlGroupName);
                }
            }
        });
    }

    /**
     * 初始化ListView
     */
    private void initListView() {
        listDate.add("修改群名称");
        listDate.add("转让群");
        LogCatLog.i("test", listDate.size() + "");
        listView.setAdapter(new CommonAdapter<String>(this, listDate, R.layout.item_group_info_listview) {
            @Override
            public void convert(ViewHolder helper, int position) {
                helper.setText(R.id.textView_name, listDate.get(position));
            }
        });
        setListViewHeight(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.toastForShort(mContext, "" + position);
                if (position == 0) { // 修改群名称
                    ToastUtils.toastForShort(mContext, "群名称");
                } else if (position == 1) {//转让群
                    ToastUtils.toastForShort(mContext, "转让");
                }
            }
        });
    }

    /**
     * 重新计算ListView的高度，解决ScrollView和ListView两个View都有滚动的效果，在嵌套使用时起冲突的问题
     *
     * @param listView
     */
    public void setListViewHeight(ListView listView) {

        // 获取ListView对应的Adapter

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
