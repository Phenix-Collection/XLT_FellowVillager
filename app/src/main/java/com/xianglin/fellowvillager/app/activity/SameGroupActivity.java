package com.xianglin.fellowvillager.app.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xianglin.appserv.common.service.facade.model.GroupDTO;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.recyclerview.adapter.SameGroupAdapter;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.widget.TopView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * 共同联系群
 * Created by zhanglisan on 16/3/16.
 */
@EActivity(R.layout.activity_same_group)
public class SameGroupActivity extends BaseActivity {

    @ViewById(R.id.topview)
    TopView mTopView;// 标题栏

    @Extra
    String otherFigureId;

    /**共同联系群视图*/
    @ViewById(R.id.same_group_rcv)
    RecyclerView mSameGroupRcv;
    /**共同联系群适配器*/
    private SameGroupAdapter adapter;


    /**
     * 同步当前用户所有角色的所有的群组
     */
    @Background
    void syncGroupInfo() {
        SyncApi.getInstance().listGroup(
                SameGroupActivity.this,
                new SyncApi.CallBack<List<GroupDTO>>() {
                    @Override
                    public void success(List<GroupDTO> mode) {
                        if (mode != null) {
                            GroupManager.getInstance().addGroups(mode, true);
                        }
                        getSameGroupData();
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                        getSameGroupData();
                    }
                });

    }


    /**
     * 获取相同联系群
     */
    @Background
    void getSameGroupData() {
        SyncApi.getInstance().sameGroups(
                otherFigureId,
                this,
                sameGroupCallBack

        );
    }

    /**
     * 相同联系群回调
     */
    private SyncApi.CallBack sameGroupCallBack = new SyncApi.CallBack<List<String>>() {
        @Override
        public void success(List<String> mode) {
            if (mode == null) {
                return;
            }
            refreshUI(mode);
        }

        @Override
        public void failed(String errTip, int errCode) {
            tip(errTip);
        }
    };

    @UiThread
    void refreshUI(List<String> mode) {
        mSameGroupRcv.setLayoutManager(
                new LinearLayoutManager(SameGroupActivity.this)
        );
        adapter = new SameGroupAdapter(
                SameGroupActivity.this,
                getGroupList(mode)
        );
        mSameGroupRcv.setAdapter(adapter);
        hideLoadingDialog();
    }

    /**
     * 从本地数据库获取共同联系群
     * @param groupIdList 群id
     * @return
     */
    private List<Group> getGroupList(List<String> groupIdList) {
        if (groupIdList == null) {
            return null;
        }
        List<Group> groups = new ArrayList<>();
        GroupDBHandler groupDBHandler = new GroupDBHandler(this);
        ArrayList<Group> allGroupList = groupDBHandler.queryAllFigureCommonGroup();
        if (allGroupList == null || allGroupList.isEmpty()) {
            return null;
        }
        for (Group group :
                allGroupList) {
            for (String groupId :
                    groupIdList) {
                if (groupId.equals(group.xlGroupID) && !groups.contains(group)) {
                    groups.add(group);
                    break;
                }
            }
        }
        return groups;
    }

    @AfterViews
    void initViews() {
        mTopView.setAppTitle(R.string.same_group);
        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.getLeftImg().setVisibility(View.VISIBLE);
        mTopView.getLeftlayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        showLoadingDialog();
        syncGroupInfo();
    }
}
