package com.xianglin.fellowvillager.app.activity.group;

import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xianglin.appserv.common.service.facade.model.GroupDTO;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.adapter.GroupListInContactAdapter;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.fellowvillager.app.widget.swipelistview.SwipeListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 名录页群聊列表展示
 * author:王力伟 time：2016.2.27
 */
@EActivity(R.layout.activity_group_chat)
public class GroupListInContactActivity extends BaseActivity {

    @ViewById(R.id.top_bar)
    TopView topView;

    // 没有数据时显示一张图片
    @ViewById(R.id.iv_no_data_tip)
    ImageView mNoDataTip;

    @ViewById(R.id.rv_group_char)
    SwipeListView rv_group_char;
    private GroupListInContactAdapter mGroupListInContactAdapter;

    // 群名称
    @ViewById(R.id.tv_minglu_contact_nick)
    TextView tv_minglu_contact_nick;
    /**
     * 已分类的群组列表
     */
    private HashMap<String, List<Group>> groupMap;
    private boolean flag = true;

    /**
     * 将api返回的群列表转换为本地数据库可用的map
     *
     * @param list 服务器返回的所有群组列表
     * @return
     */
    private Map<String, Group> getGroupMap(List<GroupDTO> list) {
        if (list == null) {
            return null;
        }
        Map<String, Group> map = new Hashtable<>(30);
        for (GroupDTO groupDTO :
                list) {
            if (groupDTO == null) {
                continue;
            }
            if (groupDTO.getGroupId() == null) {
                continue;
            }
            map.put(
                    GroupDBHandler.getGroupId(groupDTO.getGroupId(), groupDTO.getFigureId()),
                    GroupManager.getInstance().swapGroupDTOtoGroup(
                            groupDTO,
                            true
                    )
            );
        }
        return map;
    }

    // 第二个执行
    @AfterViews
    public void initView() {
        showLoadingDialog();
        topView.setAppTitle("群聊");
        topView.setLeftImageResource(R.drawable.icon_back);
        topView.getLeftlayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mNoDataTip.setImageDrawable(getResources().getDrawable(R.drawable.groupchat_no_people));

        groupMap = new HashMap<>();
        //setData();


        setData();//获取网络数据
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();//加载本地数据
    }

    @Background
   public void loadData() {
/*
        // 讲数据写入内存和本地数据库
          GroupManager.getInstance().setAllFigureGroupTable(getGroupMap(mode));*/

        ArrayList<Group> groupArrayList = new GroupDBHandler(XLApplication.getInstance()).queryGroupWithMsgCount();



        //Map<String, Group> currentFigureGroupTable = GroupManager.getInstance().getCurrentFigureGroupTable();

        ArrayList<Group> normalGroupList = new ArrayList<>();
        ArrayList<Group> blackGroupList = new ArrayList<>();

        if (groupArrayList != null) {

            for (int i = 0; i < groupArrayList.size(); i++) {
                Group value = groupArrayList.get(i);

                if(value==null){
                    continue;
                }
                //过滤本地退出的群,需要确保解散的群任然是isJoin 1
                if(value.isJoin.equals(BorrowConstants.IS_NO_JOIN_GROUP)){
                    continue;
                }
                if(ContactManager.getInstance().getFreezeFigureTable().containsKey(value.figureId)){
                    continue;
                }
                if(TextUtils.isEmpty(ContactManager.getInstance().getCurrentFigureID())||value.figureId.equals(ContactManager.getInstance().getCurrentFigureID())){
                    if (GroupListInContactAdapter.GROUP_TYPE_NORMAL.equals(value.groupType)) {
                        normalGroupList.add(value);
                    } else if (GroupListInContactAdapter.GROUP_TYPE_BLACK.equals(value.groupType)) {
                        blackGroupList.add(value);
                    }
                }

            }
        }

        if (!normalGroupList.isEmpty()) {
            flag = false;
            groupMap.put(
                    GroupListInContactAdapter.GROUP_TYPE_NORMAL,
                    normalGroupList
            );
        }

        if(normalGroupList.isEmpty()&&!blackGroupList.isEmpty()){
            flag=true;
            groupMap.clear();
        }


        //		if (!blackGroupList.isEmpty()) {
        //				flag = false;
        //			groupMap.put(
        //					GroupListInContactAdapter.GROUP_TYPE_BLACK,
        //					blackGroupList
        //			);
        //		}

        GroupListInContactActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideLoadingDialog();
                if (flag) {
                    mNoDataTip.setVisibility(View.VISIBLE);
                } else {
                    mNoDataTip.setVisibility(View.GONE);
                }
                mGroupListInContactAdapter = new GroupListInContactAdapter(GroupListInContactActivity.this, groupMap);
                rv_group_char.setLayoutManager(new LinearLayoutManager(GroupListInContactActivity.this));
   /*             rv_group_char.addItemDecoration(new ItemDivider(
                        GroupListInContactActivity.this,R.drawable.recycleview_line));*/
                rv_group_char.setAdapter(mGroupListInContactAdapter);
/*
                rv_group_char.setSwipeListViewListener(new BaseSwipeListViewListener() {
                    @Override
                    public void onOpened(int position, boolean toRight) {
                    }

                    @Override
                    public void onClosed(int position, boolean fromRight) {
                    }

                    @Override
                    public void onListChanged() {
                    }

                    @Override
                    public void onMove(int position, float x) {
                    }

                    @Override
                    public void onStartOpen(int position, int action, boolean right) {
                        Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
                    }

                    @Override
                    public void onStartClose(int position, boolean right) {
                        Log.d("swipe", String.format("onStartClose %d", position));
                    }

                    @Override
                    public void onClickFrontView(int position) {
                        Log.d("swipe", String.format("onClickFrontView %d", position));
                    }

                    @Override
                    public void onClickBackView(int position) {
                        Log.d("swipe", String.format("onClickBackView %d", position));
                    }

                    @Override
                    public void onDismiss(int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            groupMap.remove(position);
                        }
                        mGroupListInContactAdapter.notifyDataSetChanged();
                    }

                });


                rv_group_char.addOnItemTouchListener(new RecyclerItemClickListener(GroupListInContactActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Toast.makeText(GroupListInContactActivity.this,""+position,Toast.LENGTH_LONG).show();
                    }
                }) );
*/


            }
        });
    }

    /**
     * 发起网络请求获取所有角色的群列表,设置页面显示
     */
    @Background
    void setData() {
        SyncApi.getInstance().listGroup(this, new SyncApi.CallBack<List<GroupDTO>>() {
            @Override
            public void success(List<GroupDTO> mode) {
                long  count=  GroupManager.getInstance().addGroups(mode, true);
                if(count>0){
                    loadData();
                };
            }

            @Override
            public void failed(String errTip, int errCode) {
                tip(errTip);
                hideLoadingDialog();
            }
        });
    }


}
