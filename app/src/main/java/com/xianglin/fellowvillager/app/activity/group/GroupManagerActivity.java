package com.xianglin.fellowvillager.app.activity.group;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fima.cardsui.objects.GoodCard;
import com.fima.cardsui.views.CardUI;
import com.xianglin.appserv.common.service.facade.model.GroupMemberDTO;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.activity.MainActivity_;
import com.xianglin.fellowvillager.app.activity.WebviewActivity_;
import com.xianglin.fellowvillager.app.adapter.GroupManagerGridAdapter;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.CardDBHandler;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.db.GroupMemberDBHandler;
import com.xianglin.fellowvillager.app.loader.CardLoader;
import com.xianglin.fellowvillager.app.loader.GroupMemberLoader;
import com.xianglin.fellowvillager.app.loader.SQLiteCursorLoader;
import com.xianglin.fellowvillager.app.model.GoodsDetailBean;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.model.GroupMember;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.model.NameCardBean;
import com.xianglin.fellowvillager.app.model.NewsCard;
import com.xianglin.fellowvillager.app.rpc.remote.GroupMemberSync;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.SingleThreadExecutor;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.fellowvillager.app.widget.dialog.SendCardDialog;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.xianglin.xlappcore.common.service.facade.vo.ContactListVo;
import com.xianglin.xlappcore.common.service.facade.vo.MemberVo;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：乡邻小站
 * 类描述：
 * 创建人：何正纬
 * 创建时间：2015/11/25 15:45
 * 修改人：hezhengwei
 * 修改时间：2015/11/25 15:45
 * 修改备注：
 */

@EActivity(R.layout.activity_group_manager)
public class GroupManagerActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @ViewById(R.id.gv_group_manager)
    GridView mGridView;
    @ViewById(R.id.liy_group_name_update)
    LinearLayout mliy_group_name_update;
    @ViewById(R.id.liy_group_member)
    LinearLayout mliy_group_member;
    @ViewById(R.id.tv_group_name_value)
    TextView mtv_group_name_value;
    @ViewById(R.id.tv_group_change)
    TextView mTv_group_change;
    @ViewById(R.id.btn_exit_delect)
    Button mBtn_exit_delect;
    @ViewById(R.id.tv_group_member_num)
    TextView mTv_group_member_num;
    //    @ViewById(R.id.device_line)
//    View view_line;
    @ViewById(R.id.device_linea)
    View view_linea;
    @ViewById(R.id.topview)
    TopView mTopView;

    @ViewById(R.id.ll_history_record_layout)
    LinearLayout history_record_layout_layout_ll;
    @ViewById(R.id.ll_history_record)
    LinearLayout history_record_ll;
    @ViewById(R.id.sv_codes)
    ScrollView codes_sv;
    @ViewById(R.id.cardsview)
    CardUI cardsview;

    CardLoader mCardLoader;
    List<MessageBean> mMessageBeanList;

    @Extra
    String currentFigureId;
    @Extra
    String toGroupId;
    @Extra
    String toGroupName;

    private String grouptype = "";

    private GroupManagerGridAdapter mAdapter;

    private List<MemberVo> mMemberVoList;
    private GroupMemberDBHandler mGroupMemberDBHandler;
    private GroupMemberLoader mGroupMemberLoader;
    private GroupDBHandler mGroupDBHandler;
    private boolean isfirstLoaded = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        syncGroupMember();
    }


    @Background
    void syncGroupMember() {
        SyncApi.getInstance().members(
                toGroupId,
                this,
                new SyncApi.CallBack<List<GroupMemberDTO>>() {
                    @Override
                    public void success(List<GroupMemberDTO> mode) {
                        if (mode != null) {
                            Group group = GroupManager.getInstance().getGroup( GroupDBHandler.getGroupId(toGroupId,currentFigureId));
                            GroupManager.getInstance().addGroupMemberList(group, mode);
                        }
                        getDataFromDb();
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                        getDataFromDb();
                    }
                }
        );
    }

    @UiThread
    void getDataFromDb() {
        mGroupDBHandler = new GroupDBHandler(GroupManagerActivity.this);
        mGroupMemberLoader = new GroupMemberLoader(GroupManagerActivity.this, GroupDBHandler.getGroupId(toGroupId,currentFigureId));
        getSupportLoaderManager().initLoader(0, getIntent().getExtras(), new GroupMemberListCallbacks());
        //没有数据时刷新;
        //  mGroupListLoader.onContentChanged();
        mCardLoader = new CardLoader(GroupManagerActivity.this, GroupDBHandler.getGroupId(toGroupId,currentFigureId), BorrowConstants.CHATTYPE_GROUP, 0, 5, true, true);
        getSupportLoaderManager().initLoader(1, getIntent().getExtras(), new CardListCallbacks());
        initAata();
    }

    void initAata() {
        mTopView.setAppTitle("群详情");
        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.setLeftImgOnClickListener();
        mGridView.setNumColumns(4);
        mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));//设置点击背景
        mGridView.setOnItemClickListener(GroupManagerActivity.this);
        mtv_group_name_value.setText(toGroupName);//设置群名称

        Group group = GroupManager.getInstance().getGroup( GroupDBHandler.getGroupId(toGroupId,currentFigureId));
        String ownerFigureId = group.ownerFigureId;
        if (ownerFigureId == null) {
            return;
        }
        boolean isGroupOwner = false;
        if (ownerFigureId.equals(currentFigureId)) {
            isGroupOwner = true;
        }

        if (group != null && isGroupOwner) {
            grouptype = "O";//自己管理的群
            mliy_group_name_update.setVisibility(View.VISIBLE);
            mBtn_exit_delect.setText("解散并删除");
//            view_line.setVisibility(View.VISIBLE);
            view_linea.setVisibility(View.VISIBLE);
        } else {
            grouptype = "";//参与的群
            mliy_group_name_update.setVisibility(View.GONE);
            mBtn_exit_delect.setText("退出并删除");
//            view_line.setVisibility(View.GONE);
            view_linea.setVisibility(View.GONE);
        }
    }


    @Click(R.id.liy_group_member)
    void groupManagerListClick() {
        /**
         * 群成员列表
         */
        GroupMemberActivity_.intent(GroupManagerActivity.this).
                toDelMemberflag(0).toGroupId(GroupDBHandler.getGroupId(toGroupId,currentFigureId)).toGroupName(toGroupName).start();
    }

    @Click(R.id.liy_group_name_update)
    void groupNameUdateClick() {
        /**
         * 修改群名称
         */
        Intent intent = new Intent(GroupManagerActivity.this, GroupSetNameActivity_.class);
        intent.putExtra("groupId", GroupDBHandler.getGroupId(toGroupId,currentFigureId));
        intent.putExtra("groupName", toGroupName);
        startActivityForResult(intent, BorrowConstants.UPDATE_GROUP_TITLE);
    }

    @Click(R.id.tv_group_change)
    void groupChangeClick() {
        /**
         * 转让群
         */
    }

    @Click(R.id.btn_exit_delect)
    void exitDelectClick() {
        /**
         * 解散并退出群
         */

        if ("O".equals(grouptype)) {     //管理的群
            showExitDialog(getString(R.string.group_exit_manager));
        } else {
            showExitDialog(getString(R.string.group_exit_member));
        }
    }

    /**
     * 群主解散群时，dialog弹出提示
     */
    SendCardDialog mSendCardDialog;

    void showExitDialog(final String message) {
        mSendCardDialog= new SendCardDialog.Builder(this).setTitle("退群").setMessage(message)
                .setBackButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setConfirmButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (message.equals(getString(R.string.group_exit_manager))) {
                            dismissGroup(toGroupId);
                        } else {
                            exitGroup(toGroupId, currentFigureId);
                        }
                    }
                }).create();

        mSendCardDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GroupMember mGm = (GroupMember) parent.getAdapter().getItem(position);
        if (mGm.xluserid.equals("ADD")) {
            GroupAddMemberActivity_.intent(GroupManagerActivity.this)
                    .mAction(GroupAddMemberActivity.ADD_CONTACT)
                    .toGroupId(GroupDBHandler.getGroupId(toGroupId,currentFigureId))
                    .toGroupName(toGroupName)
                    .addOrJoin(BorrowConstants.CHATTYPE_JOIN).start();
        } else if (mGm.xluserid.equals("DEL")) {
            GroupMemberActivity_.intent(GroupManagerActivity.this)
                    .mAction(GroupMemberActivity.DEL_ACTION).toDelMemberflag(1).
                    toGroupId(GroupDBHandler.getGroupId(toGroupId,currentFigureId))
                    .toGroupName(toGroupName).start();
        } else if (mGm.xluserid.equals(PersonSharePreference.getUserID() + "")) {
            return;
        } else {
            ChatMainActivity_.intent(GroupManagerActivity.this)
                    .currentFigureId(mGm.figureId)
                    .toChatXlId(mGm.xluserid)
                    .titleName(mGm.getUIName())
                    .toChatId(mGm.figureUsersId)
                    .chatType(BorrowConstants.CHATTYPE_SINGLE)
                    .headerImgId(mGm.file_id)
                    .start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) return;

        if (requestCode == BorrowConstants.UPDATE_GROUP_TITLE) {
            String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
            mtv_group_name_value.setText(result);
            toGroupName = result;
        } else if (requestCode == GOTO_GROUP_VISITING_CODE_ACTIVITY) {
            Intent intent = new Intent(GroupManagerActivity.this, ChatMainActivity_.class);
            intent.putExtra("MessageBean", (MessageBean) data.getSerializableExtra("MessageBean"));
            setResult(RESULT_OK, intent);
            finish();
            animRightToLeft();
        }
    }

    /**
     * 退出并删除本群
     */
    @Background
    void exitGroup(final String groupId, String figureId) {

        SyncApi.getInstance().quit(figureId, groupId, GroupManagerActivity.this,
                new SyncApi.CallBack<Boolean>() {
                    @Override
                    public void success(Boolean mode) {
                        if (mMemberVoList != null) {
                            mMemberVoList.clear();
                        }
                        if (mode) { //退出群成功
                            delGroupInDB(groupId,true);
                        } else { // 退出群失败
                            tip("退出群失败");
                        }
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                });
    }

    /**
     * 数据库中处理解散群
     *
     * @param groupId 要解散的群Id
     * @param isExit  退出或者解散
     */
    private void delGroupInDB(String groupId,boolean isExit) {

        if(isExit){
            GroupManager.getInstance().exitContactInternal(GroupDBHandler.getGroupId(toGroupId,currentFigureId));
        }else{
            GroupManager.getInstance().dismissContactInternal(GroupDBHandler.getGroupId(toGroupId,currentFigureId));
        }

        Intent intent = new Intent(GroupManagerActivity.this, MainActivity_.class);
        startActivity(intent);
        finish();
    }

    /**
     * 解散群
     */
    @Background
    void dismissGroup(final String groupId) {
        SyncApi.getInstance().dismiss(groupId, GroupManagerActivity.this,
                new SyncApi.CallBack<Boolean>() {
                    @Override
                    public void success(Boolean mode) {
                        if (mMemberVoList != null) {
                            mMemberVoList.clear();
                        }
                        if (mode) { // 解散群成功
                            delGroupInDB(groupId,false);
                        } else { // 解散群失败
                            tip("解散群失败");
                        }
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                });
    }

    /**
     * 获取群成员
     */
    void getGroupMember() {

        new GroupMemberSync(toGroupId).groupMemberSync(new GroupMemberSync.CallBack() {
            @Override
            public void success(List<GroupMember> list) {

            }

            @Override
            public void failed(String errMsg, int type) {
                tip(errMsg);
            }
        });
/*        final CommonReq commonReq = new CommonReq();
        final long xlid = PersonSharePreference.getUserID();
        commonReq.setBody(new HashMap<String, Object>() {
            {
                put("xlid", xlid);
                put("teamId", toGroupId);
            }
        });

        SyncApi.getInstance().teamMember(GroupManagerActivity.this,
                commonReq, new SyncApi.CallBack<ContactListVo>() {

            @Override
            public void success(ContactListVo mode) {
                loadData(mode);//将网络数据插入数据库
            }

            @Override
            public void failed(String errMsg, int type) {
                tip(errMsg);
            }
        });*/
    }

    /**
     * 加载数据;
     *
     * @param mode
     */
    public void loadData(ContactListVo mode) {

        List<GroupMember> gm = new ArrayList<GroupMember>();
        mGroupMemberDBHandler = new GroupMemberDBHandler(GroupManagerActivity.this);
        mMemberVoList = mode.getMemberList();
        LogCatLog.i(TAG, "---------------size-------------" + mMemberVoList.size());
        for (int i = 0; mMemberVoList != null && i < mMemberVoList.size(); i++) {
            MemberVo memberVo = mMemberVoList.get(i);
            GroupMember mGroupMember = new GroupMember.Builder()
                    .xluserid(memberVo.getXlid() + "")
                    .xlGroupId(memberVo.getTeamId() + "")
                    .xlRemarkName(memberVo.getRemarkName() + "")
                    .file_id(memberVo.getImgId() + "")
                    .isContact(memberVo.getIsContact() + "")
                    .isOwner(memberVo.getIsOwner() + "")
                    .sortLetters("")
                    .build();
            gm.add(mGroupMember);
        }
        mGroupMemberDBHandler.addlist(gm);
        gm.clear();

    }

    private class GroupMemberListCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return mGroupMemberLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if (!isfirstLoaded) {
                //如果改为动态 注意适配拼音索引
                isfirstLoaded = false;
                return;
            }

            GroupMemberDBHandler.GroupMemberCursor groupCursor =
                    new GroupMemberDBHandler.GroupMemberCursor(data);

            if (groupCursor.isAfterLast()) {
                groupCursor.moveToPosition(-1);
            }
            List<GroupMember> mList = new ArrayList<GroupMember>();
            mList.clear();
            int mGroupMemberCount = data.getCount();
            if (mGroupMemberCount > 0) {
                mTopView.setAppTitle("群详情(" + mGroupMemberCount + ")");
                mTv_group_member_num.setText(String.format(getString(R.string.group_list_me), mGroupMemberCount));
            }

            while (data != null && data.moveToNext()) {
                if (mList.size() >= 18) break;
                GroupMember mGroupMember = groupCursor.getGroupMember();
                mList.add(mGroupMember);
            }



            mList = handleGroupMemberList(mList, grouptype);

            GroupMember mAdd = new GroupMember.Builder().xluserid("ADD").build();
            mList.add(mAdd);

            if ("O".equals(grouptype)) {     //管理的群
                GroupMember mDel = new GroupMember.Builder().xluserid("DEL").build();
                mList.add(mDel);
            }

            if (mAdapter == null) {
                mAdapter = new GroupManagerGridAdapter(GroupManagerActivity.this, mList, grouptype);
                mGridView.setAdapter(mAdapter);
            } else {
                mAdapter.setData(mList);
                mGridView.setAdapter(mAdapter);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    /**
     * 处理群成员显示,仅显示两排(包含+/-号)
     *
     * @param list      原成员列表
     * @param groutType 群主还是群员类型
     * @return 修改后的群成员列表
     */
    private List<GroupMember> handleGroupMemberList(List<GroupMember> list, String groutType) {
        if (list == null) {
            return null;
        }
        if (groutType.equals("O")) { //群主
            if (list.size() <= 6) {
                return list;
            }
            return list.subList(0, 6);
        } else { // 群员
            if (list.size() <= 7) {
                return list;
            }
            return list.subList(0, 7);
        }
    }

    private boolean flag = true;

    private class CardListCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return mCardLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            CardDBHandler.MessageCursor messageCursor = new CardDBHandler.MessageCursor(data);
            mMessageBeanList = messageCursor.getMessageBeanList();
            if (mMessageBeanList == null || mMessageBeanList.size() == 0) {
                history_record_layout_layout_ll.setVisibility(View.GONE);
                return;
            }

            SQLiteCursorLoader sqLiteCursorLoader = (SQLiteCursorLoader) loader;
            data.unregisterContentObserver(sqLiteCursorLoader.getObserver());

            GoodCard mGoodCard = null;
            for (int i = mMessageBeanList.size() - 1; i >= 0; i--) {
                final MessageBean bean = mMessageBeanList.get(i);
                if (bean.msgType == MessageChatAdapter.IDCARD) {
                    final NameCardBean mNameCardBean = bean.idCard;
                    mGoodCard = new GoodCard(GroupManagerActivity.this,
                            mNameCardBean, MessageChatAdapter.IDCARD, codes_sv);
                    // 如果是个人名片，点击后就是去“聊天”
                    mGoodCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SingleThreadExecutor.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {
                                    new CardDBHandler().saveCardLastOpenTime(
                                            MessageChatAdapter.IDCARD, mNameCardBean.getMsg_key());
                                }
                            });
                            if (mNameCardBean.getType() == BorrowConstants.CHATTYPE_SINGLE) {
                                ChatMainActivity_.intent(GroupManagerActivity.this)
                                        .toChatXlId(mNameCardBean.getFigureId())
                                        .currentFigureId(mNameCardBean.getUserId())
                                        .titleName(mNameCardBean.getName())
                                        .toChatId(mNameCardBean.getToChatId())
                                        .chatType(BorrowConstants.CHATTYPE_SINGLE)
                                        .headerImgId(mNameCardBean.getImgId())
                                        .toChatName(mNameCardBean.getName())
                                        .start();
                            } else if (mNameCardBean.getType() == BorrowConstants.CHATTYPE_GROUP) {
                                ChatMainActivity_.intent(GroupManagerActivity.this)
                                        .titleName(mNameCardBean.getName())
                                        .toChatId(mNameCardBean.getFigureId())
                                        .chatType(BorrowConstants.CHATTYPE_GROUP)
                                        .headerImgId(mNameCardBean.getImgId())
                                        .toChatName(mNameCardBean.getName())
                                        .start();
                            }
                            animLeftToRight();
                        }
                    });
                } else if (bean.msgType == MessageChatAdapter.WEBSHOPPING) {
                    final GoodsDetailBean mGoodsDetailBean = bean.goodsCard;
                    mGoodCard = new GoodCard(GroupManagerActivity.this, mGoodsDetailBean,
                            MessageChatAdapter.WEBSHOPPING, codes_sv);
                    // 如果是链接，点击后跳到WebView
                    mGoodCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mGoodsDetailBean.getUrl() == null) {
                                return;
                            }
                            SingleThreadExecutor.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {
                                    new CardDBHandler().saveCardLastOpenTime(
                                            MessageChatAdapter.WEBSHOPPING, mGoodsDetailBean.getMsg_key());
                                }
                            });
                            Intent intent = new Intent(GroupManagerActivity.this, WebviewActivity_.class);
                            intent.putExtra("url", mGoodsDetailBean.getUrl());
                            startActivity(intent);
                            animLeftToRight();
                        }
                    });
                } else if (bean.msgType == MessageChatAdapter.NEWSCARD) {
                    final NewsCard mNewsCardBean = bean.newsCard;
                    mGoodCard = new GoodCard(GroupManagerActivity.this, mNewsCardBean,
                            MessageChatAdapter.NEWSCARD, codes_sv);
                    // 如果是链接，点击后跳到WebView
                    mGoodCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mNewsCardBean.getUrl() == null) {
                                return;
                            }
                            SingleThreadExecutor.getInstance().execute(new Runnable() {
                                @Override
                                public void run() {
                                    new CardDBHandler().saveCardLastOpenTime(
                                            MessageChatAdapter.NEWSCARD, mNewsCardBean.getMsg_key());
                                }
                            });
                            Intent intent = new Intent(GroupManagerActivity.this, WebviewActivity_.class);
                            intent.putExtra("url", mNewsCardBean.getUrl());
                            startActivity(intent);
                            animLeftToRight();
                        }
                    });
                }
                if (flag) {
                    flag = false;
                    cardsview.addCard(mGoodCard);
                }
                cardsview.addCardToLastStack(mGoodCard);
            }
            cardsview.refresh();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMemberVoList != null) {
            mMemberVoList.clear();
        }
        if(mSendCardDialog!=null&&mSendCardDialog.isShowing()){
            mSendCardDialog.dismiss();
        }
    }

    private static final int GOTO_GROUP_VISITING_CODE_ACTIVITY = 0;

    @Click(R.id.ll_history_record)
    public void onClick(View v) {
        switch (v.getId()) {
            // 历史记录
            case R.id.ll_history_record:
                Intent intent = new Intent(GroupManagerActivity.this, GroupVisitingCodeActivity_.class);
                intent.putExtra("toGroupId", toGroupId);
                intent.putExtra("toGroupName", toGroupName);
                intent.putExtra("grouptype", grouptype);
                startActivityForResult(intent, GOTO_GROUP_VISITING_CODE_ACTIVITY);
                animLeftToRight();
                break;
            default:
        }
    }


}
