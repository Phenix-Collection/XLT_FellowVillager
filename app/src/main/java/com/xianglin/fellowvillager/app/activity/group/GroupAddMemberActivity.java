package com.xianglin.fellowvillager.app.activity.group;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.xianglin.appserv.common.service.facade.model.GroupDTO;
import com.xianglin.appserv.common.service.facade.model.GroupOperationRequest;
import com.xianglin.appserv.common.service.facade.model.UserFigureIdDTO;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.activity.ChooseRoleActivity_;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.db.GroupMemberDBHandler;
import com.xianglin.fellowvillager.app.loader.ContactLoader;
import com.xianglin.fellowvillager.app.loader.GroupListLoader;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.model.GroupMember;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.CharacterParser;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.ToastUtils;
import com.xianglin.fellowvillager.app.utils.pinyin.PingYinUtil;
import com.xianglin.fellowvillager.app.utils.pinyin.PinyinComperContact;
import com.xianglin.fellowvillager.app.widget.CircleImage;
import com.xianglin.fellowvillager.app.widget.PinyinSideBar;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.mobile.common.filenetwork.listener.FileMessageListener;
import com.xianglin.mobile.common.filenetwork.model.FileTask;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.xianglin.xlappcore.common.service.facade.vo.TeamVo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：乡邻小站
 * 类描述：创建群和加人
 * 创建人：何正纬
 * 创建时间：2015/12/1 16:35
 * 修改人：hezhengwei
 * 修改时间：2015/12/1 16:35
 * 修改备注：
 */
@EActivity(R.layout.activity_group_list_pinyin)
public class GroupAddMemberActivity extends BaseActivity {

    @ViewById(R.id.title_layout)
    LinearLayout titleLayout;
    @ViewById(R.id.title_layout_catalog)
    TextView title;
    @ViewById(R.id.title_layout_no_friends)
    TextView tvNofriends;
    @ViewById(R.id.country_lvcountry)
    ListView sortListView;
    @ViewById(R.id.dialog)
    TextView dialog;
    @ViewById(R.id.topview)
    TopView topView;

    @Extra
    int mAction;

    @Extra
    String toGroupId; //localGropid
    String xlGroupID; //xlgrougid


    @Extra
    String toGroupName;

    @Extra
    String addOrJoin;// 0为ADD ,1为添加成员


    public static final int ADD_CONTACT = 0x1000;
    public static final int CREATE_GROUP = 0x2000;

    private PinyinSideBar sideBar;
    private GroupListAdapter adapter;
    private GroupListAdapter.ViewHolder holder;
    private int lastFirstVisibleItem = -1;
    private CharacterParser characterParser;//汉字转换成拼音的类
    private List<Contact> SourceDateList;//名录里的人
    private PinyinComperContact pinyinComparator;//根据拼音来排列ListView里面的数据类
    private List<Contact> mContactList = new ArrayList<Contact>();
    private GroupDBHandler groupDBHandler;
    private List<TeamVo> teamVoList;
    private GroupListLoader mGroupListLoader;
    private ContactLoader contactLoader;
    private List<String> listMemberId = new ArrayList<>();
    private List<GroupMember> mMemberList = new ArrayList<GroupMember>();
    private List<Contact> selectContact = new ArrayList<Contact>();
    List<UserFigureIdDTO> userFigureIdDTOList = new ArrayList<UserFigureIdDTO>();//要添加到群的联系人列表
    ;
    private GroupMemberDBHandler mGroupMemberDBHandler;

    boolean isfirstLoaded = true; //只响应一次contactLoader 后期在ContactLoader中注销监听

    @ViewById(R.id.contact_plus)
    ImageView contact_plus;//加号

    @ViewById(R.id.ll_filter)
    LinearLayout ll_filter;//减号

    @ViewById(R.id.contact_minus)
    ImageView contact_minus;//减号

    private int currentLevel = 0;//当前层级0 1 2

    @Click(R.id.contact_plus)
    void click_plus() {//放大
        currentLevel++;
        if (currentLevel >= 2) {
            contact_plus.setImageResource(R.drawable.contact_grey_plus);
            currentLevel = 2;
            contact_plus.setClickable(false);
        } else {
            contact_plus.setClickable(true);
            contact_minus.setClickable(true);
            contact_plus.setImageResource(R.drawable.contact_plus_icon);
            contact_minus.setImageResource(R.drawable.contact_minus_icon);
        }
        //  refresh();
        contactLoader.onContentChanged(getcurrentLevel(currentLevel));
    }

    @Click(R.id.ll_filter)
    void click_() {
        //勿删,用来防止点击事件穿透
    }

    @Click(R.id.contact_minus)
    void click_minus() {//缩小
        currentLevel--;
        if (currentLevel < 1) {
            contact_minus.setImageResource(R.drawable.contact_grey_minus);
            currentLevel = 0;
            contact_minus.setClickable(false);
        } else {
            contact_plus.setClickable(true);
            contact_minus.setClickable(true);
            contact_plus.setImageResource(R.drawable.contact_plus_icon);
            contact_minus.setImageResource(R.drawable.contact_minus_icon);
        }
        //refresh();

        contactLoader.onContentChanged(getcurrentLevel(currentLevel));

    }


    public Contact.ContactLevel getcurrentLevel(int currentLevel) {
        if (currentLevel == 0) {
            return Contact.ContactLevel.HIGH;
        }
        if (currentLevel == 1) {
            return Contact.ContactLevel.NORMAL;
        }
        if (currentLevel == 2) {
            return Contact.ContactLevel.UMKNOWN;
        }
        return Contact.ContactLevel.UMKNOWN;
    }


    @AfterViews
    void initView() {
        addTitle();

        Group group = GroupManager.getInstance().getGroup(toGroupId);

        if (group != null) {
            xlGroupID = group.xlGroupID;
        }

        if (currentLevel ==0) {
            contact_minus.setImageResource(R.drawable.contact_grey_minus);
            currentLevel = 0;
            contact_minus.setClickable(false);
        }

        ll_filter.setVisibility(View.GONE);


        contactLoader = new ContactLoader(this, group);
        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(0, getIntent().getExtras(), new ContactCallbacks());
        mGroupMemberDBHandler = new GroupMemberDBHandler(GroupAddMemberActivity.this);
        //          mGroupListLoader.onContentChanged(); //没有数据时刷新;
    }



    private class ContactCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return contactLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            //  SimpleCursorAdapter simpleCursorAdapter=new SimpleCursorAdapter();


            if (!isfirstLoaded) {
                isfirstLoaded = false;
                return;
            }

            ContactDBHandler.ContactCursor mCursor = new ContactDBHandler.ContactCursor(data);

            List<Contact> contactsTemp = mCursor.getContactList();

            if (contactsTemp.size() == 0) return;

            List<Contact> contacts=new ArrayList<Contact>();

            for(int i=0;i<contactsTemp.size();i++){
                Contact contact=contactsTemp.get(i);
                if(contact!=null&&contact.figureGroup!=null&&contact.figureGroup.size()!=0){
                   if(!contact.xlUserID.equals("1111")){
                       //过滤掉,黑名单,和乡邻助手
                       contacts.add(contact);
                   }
                }
            }


            Map<String, Contact> map = new HashMap<String, Contact>();
/*

            for (Map.Entry<Integer,Boolean> entry : adapter.isSelectedList.entrySet()) {
               // map.put(mContactList.get(entry.getKey()).contactId,mContactList.get(entry.getKey()));
                for(int i=0;i<contacts.size();i++){
                    if(contacts.get(i).contactId.equals(mContactList.get(entry.getKey()).contactId)){
                        contacts.remove(i);
                    }
                }
            }
*/

/*
            if (contacts.size() >  mContactList.size()) {

                for (int i = 0; i < mContactList.size(); i++) {

                    for (int j = 0; j < contacts.size(); j++) {

                        if (contacts.get(j).contactId.equals(mContactList.get(i).contactId)) {
                            contacts.remove(j);
                        }

                    }
                }
                mContactList.addAll(contacts);


                for (int i = 0; i < mContactList.size(); i++) {

                    for (int j = 0; j < contacts.size(); j++) {

                        if (contacts.get(j).contactId.equals(tempContacts.get(i).contactId)) {
                            contacts.remove(j);
                        }

                    }
                }

                mContactList.addAll(tempContacts);
            } else if (contacts.size() ==  mContactList.size()){

               // mContactList.addAll(contacts);

            } else {
    */
/*            for (int i = 0; i < contacts.size(); i++) {

                    for (int j = 0; j < mContactList.size(); j++) {

                        if (contacts.get(j).contactId.equals(mContactList.get(i).contactId)) {

                           if(isSelectedMap.containsKey(contacts.get(j).contactId)){
                               tempContacts.add(contacts.get(j));
                               mContactList.remove(j);
                           }
                        }
                    }
                }
*//*

                //  mContactList.addAll(contacts);
            }
*/


            LogCatLog.d(TAG, "onLoadFinished可供添加的到群里的联系人");
            mContactList.clear();


            mContactList.addAll(contacts);
            initViews();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    /**
     * 初始化头文件
     */
    public void addTitle() {
        //        if (mAction == ADD_CONTACT) {
        //            topView.setAppTitle("选择联系人");
        //        } else {
        //            topView.setAppTitle(R.string.group_member_new);
        //        }
        topView.setAppTitle("选择联系人");
        topView.setLeftTextiewText(R.string.crop__cancel);
        topView.setLeftTextOnClickListener();
        topView.getRightTextView().setEnabled(false);
        topView.getRightTextView().setTextColor(getResources().getColor(R.color.black1));
    }

    private StringBuffer currentFigureId = new StringBuffer();

    /**
     * 标题栏右侧点击事件; 添加群成员或者创建群
     */

    public class TopViewOnClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (!DeviceInfoUtil.isNetAvailble(context)) {
                ToastUtils.toastForShort(context, "网络异常,请联网后再试!");
                return;
            }

            if (adapter != null && isSelectedList != null) {


                mMemberList.clear();
                userFigureIdDTOList.clear();
                listMemberId.clear();


                Iterator<String> iterator = isSelectedMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String next = iterator.next();

                        //添加群成员 不包括群主
                        GroupMember mGroupMember = new GroupMember.Builder()
                                .xluserid(isSelectedMap.get(next).xlUserID + "")
                                .xlGroupId(xlGroupID + "")
                                .xlImgPath(isSelectedMap.get(next).xlImagePath + "")
                                .file_id(isSelectedMap.get(next).file_id)
                                .xlRemarkName(isSelectedMap.get(next).getXlReMarks())
                                .xlUserName(isSelectedMap.get(next).getUIName())
                                .isOwner(false + "")//是否群主标识
                                .isContact(true + "")//是否名录里的人
                                .sortLetters("")
                                .figureId(isSelectedMap.get(next).figureId)
                                .figureUsersId(isSelectedMap.get(next).figureUsersId)
                                .localgroupId(toGroupId)
                                .build();
                        mMemberList.add(mGroupMember);
                        selectContact.add(isSelectedMap.get(next));
                        UserFigureIdDTO userFigureIdDTO = new UserFigureIdDTO();
                        userFigureIdDTO.setFigureId(isSelectedMap.get(next).figureUsersId);
                        userFigureIdDTO.setUserId(isSelectedMap.get(next).xlUserID);
                        userFigureIdDTOList.add(userFigureIdDTO);
                        LogCatLog.e("Test", "figureUsersId=" + isSelectedMap.get(next).figureUsersId + "");
                        LogCatLog.e("Test", "111111 UserId=" + isSelectedMap.get(next).xlUserID + "");
                        listMemberId.add(isSelectedMap.get(next).xlUserID + "");

                }
            }
            LogCatLog.e("Test", "listMemberId.size()=" + listMemberId.size());
            if (addOrJoin.equals(BorrowConstants.CHATTYPE_ADD)) {
                //新建群-->必须选择两个人才能建群
                if (listMemberId.size() < 1) {
                    tip(R.string.select_groupmember_more);

                    return;

                } else if (listMemberId.size() < 2) {
                    ArrayList<FigureMode> figureModeArrayList = selectContact.get(0).figureGroup;
                    LogCatLog.e("Test", "figureMode=" + figureModeArrayList);
                    if (figureModeArrayList != null) {
                        FigureMode[] mFigureModeArray = figureModeArrayList
                                .toArray(new FigureMode[figureModeArrayList.size()]);
                        ChatMainActivity_.intent(GroupAddMemberActivity.this)
                                .currentFigureId(mFigureModeArray[0].getFigureUsersid())//
                                .toChatXlId(mMemberList.get(0).xluserid)
                                .toChatId(mMemberList.get(0).figureId)
                                .chatType(BorrowConstants.CHATTYPE_SINGLE)
                                .titleName(mMemberList.get(0).xlUserName)
                                .headerImgId(mMemberList.get(0).file_id)//
                                .toChatName(mMemberList.get(0).xlUserName)
                                .start();
                        finish();
                    }

                    return;
                }
                if (!ContactManager.getInstance().getCurrentFigureID().equals("")) {//单个角色建群
                    createGroup(ContactManager.getInstance().getCurrentFigureID(), userFigureIdDTOList);
                } else {
                    if (!currentFigureId.toString().contains(",")) {//建群时角色确定唯一
                        createGroup(currentFigureId.toString().toString(), userFigureIdDTOList);
                    } else {
                        //startActivityForResult(new I);
                        LogCatLog.e("Test", "idList=" + currentFigureId);
                        // 选择角色
                        Intent intent = new Intent(context, ChooseRoleActivity_.class)
                                .putExtra("gowhere", BorrowConstants.CHAT_CHAT)
                                .putExtra("figureIdList", currentFigureId.toString())
                                .putExtra("from", "add_group");
                        startActivityForResult(intent, 1);
                        animBottomToTop();
                    }
                }

            } else {
                //添加群成员-->先选一个联系人
                if (listMemberId.size() == 0) {
                    tip(R.string.select_groupmember);
                    return;
                }

                invateGroup(xlGroupID, GroupManager.getInstance().getGroup(toGroupId).figureId, userFigureIdDTOList);

            }

            /**
             * 创建群组/添加成员
             */
            //createGroup();

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String currentFigureId = data.getStringExtra("selectCurFigureId");
            createGroup(currentFigureId, userFigureIdDTOList);
        }
    }

    /**
     * @param list
     */
    @Background
    void createGroup(final String figureId, List<UserFigureIdDTO> list) {
        UserFigureIdDTO userFigureIdDTO3 = new UserFigureIdDTO();
        userFigureIdDTO3.setFigureId(figureId);
        userFigureIdDTO3.setUserId(PersonSharePreference.getUserID() + "");
        list.add(userFigureIdDTO3);
        SyncApi.getInstance().create(this, figureId, list, new SyncApi.CallBack<GroupDTO>() {
            @Override
            public void success(GroupDTO mode) {
                if (mode != null) {
                    Group group = GroupManager.getInstance().swapGroupDTOtoGroup(mode, true);
                    GroupManager.getInstance().addGroup(group);
                    addGroupMemberMethod(figureId, mode);  //添加群成员
                }
            }

            @Override
            public void failed(String errTip, int errCode) {
                tip(errTip);
            }
        });

    }

    public void startToChatActivity(String currentFigureId,
                                    String toChatId,
                                    String titleName) {

        ChatMainActivity_//
                .intent(this)//
                .currentFigureId(currentFigureId)//当前角色id
                .toChatId(toChatId)//对方角色id
                .titleName(titleName)//
                .chatType(BorrowConstants.CHATTYPE_GROUP)//
                .start();


    }


    /**
     * 批量加人
     *
     * @param groundId
     * @param figureId
     * @param list
     */
    @Background
    void invateGroup(String groundId, String figureId, List<UserFigureIdDTO> list) {
        GroupOperationRequest groupOperationRequest = new GroupOperationRequest();
        groupOperationRequest.setFigureId(figureId);
        groupOperationRequest.setGroupId(groundId);

        groupOperationRequest.setMembers(list);
        SyncApi.getInstance().invite(groupOperationRequest, GroupAddMemberActivity.this,
                new SyncApi.CallBack<Boolean>() {
                    @Override
                    public void success(Boolean mode) {
                        //插入数据库,在已有群的基础上添加成员;
                        mGroupMemberDBHandler.addlist(mMemberList);
                        finish();
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                });
    }


    //创建群时添加群成员方法
    public void addGroupMemberMethod(String currentFigureId, GroupDTO mode) {

        if (TextUtils.isEmpty(mode.getGroupId())) {
            return;
        }
        //   updateTime

        Group mGroup = new Group.Builder()
                .xlID(PersonSharePreference.getUserID() + "")
                .figureId(mode.getFigureId())
                .ownerFigureId(mode.getOwnerFigureId())
                .ownerUserId(mode.getOwnerUserId())
                .xlGroupName(mode.getGroupName() + "")
                .xlGroupID(mode.getGroupId() + "")
                .groupType(mode.getGroupType())
                .file_id(mode.getAvatarUrl() + "")
                .status(mode.getStatus())
                .description(mode.getDescription())
                .updateGroupTime(mode.getUpdateTime() + "")
                .createGroupTime(mode.getCreateTime() + "")
                .isJoin(BorrowConstants.IS_JOIN_GROUP) //表示有没有被删除
                .localGroupId(GroupDBHandler.getGroupId(mode.getGroupId(), mode.getFigureId()))
                .build();

        for (int i = 0; i < mMemberList.size(); i++) {
            GroupMember mGroupMember = mMemberList.get(i);
            mGroupMember.xlGroupId = mGroup.xlGroupID + "";

        }

        GroupMember mGroupMember = new GroupMember.Builder()
                .xluserid(PersonSharePreference.getUserID() + "")
                .xlGroupId(mGroup.xlGroupID + "")
                .xlImgPath("null")
                .file_id(ContactManager.getInstance().getCurrentFigure(currentFigureId).getFigureImageid())
                .xlUserName(PersonSharePreference.getUserNickName())
                .isOwner(true + "")
                .isContact(true + "")
                .figureId(currentFigureId)
                .figureUsersId(currentFigureId)
                .sortLetters("")
                .localgroupId(GroupDBHandler.getGroupId(mGroup))
                .build();
        mMemberList.add(mGroupMember);
        //插入数据库,创建群组成功后,再将群id插入群组成员;
        GroupDBHandler mGroupDBHandler = new GroupDBHandler(GroupAddMemberActivity.this);
        mGroupDBHandler.add(mGroup);
        if (mGroupDBHandler.query(mGroup.localGroupId) != null) {
            //插入群成员到数据库
            mGroupMemberDBHandler.addlist(mMemberList);
        } else {
            tip(R.string.group_create_fail);
        }


        startToChatActivity(
                mMemberList.get(0).figureId
                , mGroup.xlGroupID,
                mGroup.xlGroupName);


        finish();
    }

    /**
     * 获取完网络数据后,加载在数据并显示
     */
    private void initViews() {
        characterParser = CharacterParser.getInstance(); // 实例化汉字转拼音类
        pinyinComparator = new PinyinComperContact();
        sideBar = (PinyinSideBar) findViewById(R.id.sidrbar);
        sideBar.setTextView(dialog);
        ll_filter.setVisibility(View.VISIBLE);
        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new PinyinSideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });

        SourceDateList = filledData(mContactList); //加载数据
        Collections.sort(SourceDateList, pinyinComparator); // 根据a-z进行排序源数据
        char[] alphaList = new PingYinUtil().getFirstSpell(SourceDateList);
        LogCatLog.e("Test", "alphaList=" + alphaList);
        sideBar.setAlpha(alphaList);
        sideBar.requestLayout();
        if (SourceDateList.size() > 0) {
            topView.getRightTextView().setText("确定");
        //    if (mContactList.size() == 1) {//整个列表只有一个人,那么这个人必定是自己;

         //   } else {
                topView.getRightTextView().setOnClickListener(new TopViewOnClick());
        //    }

            //if(adapter==null){
                adapter = new GroupListAdapter(this, SourceDateList);
                sortListView.setAdapter(adapter);
   /*         }else{
                adapter.notifyDataSetChanged();
            }*/

            if (SourceDateList.size() > 1) {
                sortListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem,
                                         int visibleItemCount, int totalItemCount) {

                        autoShowLetter(visibleItemCount, totalItemCount);

                        int section = getSectionForPosition(firstVisibleItem);
                        int nextSection = getSectionForPosition(firstVisibleItem + 1);
                        int nextSecPosition = getPositionForSection(+nextSection);

                        if (firstVisibleItem != lastFirstVisibleItem) {
                            ViewGroup.MarginLayoutParams params =
                                    (ViewGroup.MarginLayoutParams) titleLayout
                                            .getLayoutParams();
                            params.topMargin = 0;
                            titleLayout.setLayoutParams(params);
                            title.setText(SourceDateList.get(
                                    getPositionForSection(section)).section);
                        }
                        if (nextSecPosition == firstVisibleItem + 1) {
                            View childView = view.getChildAt(0);
                            if (childView != null) {
                                int titleHeight = titleLayout.getHeight();
                                int bottom = childView.getBottom();
                                ViewGroup.MarginLayoutParams params =
                                        (ViewGroup.MarginLayoutParams) titleLayout
                                                .getLayoutParams();
                                if (bottom < titleHeight) {
                                    float pushedDistance = bottom - titleHeight;
                                    params.topMargin = (int) pushedDistance;
                                    titleLayout.setLayoutParams(params);
                                } else {
                                    if (params.topMargin != 0) {
                                        params.topMargin = 0;
                                        titleLayout.setLayoutParams(params);
                                    }
                                }
                            }
                        }
                        lastFirstVisibleItem = firstVisibleItem;
                    }
                });
            } else {

                sideBar.setVisibility(View.GONE);
            }


        } else {
            sideBar.setVisibility(View.GONE);
        }


    }


    private long isFirstSize = 0;//listview第一次加载完成后的adapter的size

    private void autoShowLetter(int visibleItemCount, int totalItemCount) {
        if (visibleItemCount > 0 && isFirstSize != totalItemCount) {
            isFirstSize = totalItemCount;
            if (totalItemCount > visibleItemCount) {
                //listview 的高度大于屏幕的高度 显示mLetter
                sideBar.setVisibility(View.VISIBLE);
            } else {
                sideBar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<Contact> filledData(List<Contact> date) {
        List<Contact> mSortList = new ArrayList<Contact>();
        for (int i = 0; i < date.size(); i++) {

            String pinyin = "";
            String sortString = "";
            if (!TextUtils.isEmpty(date.get(i).getUIName())) {
                // 汉字转换成拼音
                //pinyin = characterParser.getSelling(date.get(i).xlUserName);
                pinyin = PingYinUtil.getPingYin(date.get(i).getUIName());
                sortString = pinyin.substring(0, 1).toUpperCase();
            }

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                // sortModel.section(sortString.toUpperCase());
                date.get(i).section = sortString.toUpperCase();
            } else {
                date.get(i).section = "#";
            }

            mSortList.add(date.get(i));
        }
        return mSortList;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if(position>=SourceDateList.size()) return 0;



        return SourceDateList.get(position).section.charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < SourceDateList.size(); i++) {
            String sortStr = SourceDateList.get(i).section;
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    public HashMap<Integer, Boolean> isSelectedList= new HashMap<Integer, Boolean>();
    public HashMap<String, Contact> isSelectedMap= new  HashMap<String, Contact>();


    /**
     * 适配器
     */
    public class GroupListAdapter extends BaseAdapter implements OnClickListener, SectionIndexer {

        private List<Contact> list = null;
        private Context mContext;
        private Contact mcontact;

        private ArrayList<String> selectedFigure = new ArrayList<String>();
        private ArrayList<Integer> selectLength = new ArrayList<Integer>();
        private ArrayList<String> retainFigure = new ArrayList<String>();

        public GroupListAdapter(Context mContext, List<Contact> list) {
            this.mContext = mContext;
            this.list = list;

        }



        /**
         * 当ListView数据发生变化时,调用此方法来更新ListView
         *
         * @param list
         */
        public void updateListView(List<Contact> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public int getCount() {
            return this.list.size();
        }

        public Contact getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup arg2) {
            final ViewHolder viewHolder;
            final Contact mContent = list.get(position);
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_group_list_pinyin, null);
                viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
                viewHolder.imgTitle = (ImageView) view.findViewById(R.id.rember_img);
                viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
                viewHolder.isCheckBox = (CheckBox) view.findViewById(R.id.ckx_member_select);
                viewHolder.chk_item = (LinearLayout) view.findViewById(R.id.chk_item);
                viewHolder.ci_role_1 = (CircleImage) view.findViewById(R.id.ci_role_1);
                viewHolder.ci_role_2 = (CircleImage) view.findViewById(R.id.ci_role_2);
                viewHolder.ci_role_3 = (CircleImage) view.findViewById(R.id.ci_role_3);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            //  CircleImage[]  ci_role={viewHolder.ci_role_1,viewHolder.ci_role_2,viewHolder.ci_role_3};

            // if(mContent.figureGroup!=null) {


            int size = mContent.figureGroup.size() > 3 ? 3 : mContent.figureGroup.size();

            boolean isShow = false;
            if (TextUtils.isEmpty(ContactManager.getInstance().getCurrentFigureID())) {
                //过滤时需要针对所有角色过滤
                ArrayList<String> strlist = new ArrayList<String>();

                for (int i = 0; i < mContent.figureGroup.size(); i++) {

                    FigureMode figureMode = mContent.figureGroup.get(i);
                    if (figureMode != null) {
                        strlist.add(figureMode.getFigureUsersid());
                    }
                }

                isShow = getFigureList(strlist, retainFigure);

                //if(!selectedFigure.isEmpty()) {

                // }

                if (mContent.figureGroup != null && mContent.figureGroup.get(0) != null) {

                    if (size == 1) {
                        if (mContent.figureGroup.get(0) != null) {
                            viewHolder.ci_role_1.setVisibility(View.VISIBLE);
                            ImageUtils.showCommonImage((Activity) mContext,
                                    viewHolder.ci_role_1,
                                    FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                                    mContent.figureGroup.get(0).getFigureImageid(),
                                    R.drawable.head);
                        }
                        viewHolder.ci_role_2.setVisibility(View.GONE);
                        viewHolder.ci_role_3.setVisibility(View.GONE);
                    } else if (size == 2) {

                        viewHolder.ci_role_1.setVisibility(View.VISIBLE);
                        viewHolder.ci_role_2.setVisibility(View.VISIBLE);
                        if (mContent.figureGroup.get(0) != null) {
                            ImageUtils.showCommonImage((Activity) mContext,
                                    viewHolder.ci_role_1,
                                    FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                                    mContent.figureGroup.get(0).getFigureImageid(),
                                    R.drawable.head);
                        }

                        if (mContent.figureGroup.size() >= 2) {
                            if (mContent.figureGroup.get(1) != null) {
                                ImageUtils.showCommonImage((Activity) mContext,
                                        viewHolder.ci_role_2,
                                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                                        mContent.figureGroup.get(1).getFigureImageid(),
                                        R.drawable.head);
                            }

                        }
                        viewHolder.ci_role_3.setVisibility(View.GONE);
                    } else if (size == 3) {

                        viewHolder.ci_role_1.setVisibility(View.VISIBLE);
                        viewHolder.ci_role_2.setVisibility(View.VISIBLE);
                        viewHolder.ci_role_3.setVisibility(View.VISIBLE);
                        if (mContent.figureGroup.get(0) != null) {
                            ImageUtils.showCommonImage((Activity) mContext,
                                    viewHolder.ci_role_1,
                                    FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                                    mContent.figureGroup.get(0).getFigureImageid(),
                                    R.drawable.head);
                        }

                        if (mContent.figureGroup.size() >= 2) {
                            if (mContent.figureGroup.get(1) != null) {
                                ImageUtils.showCommonImage((Activity) mContext,
                                        viewHolder.ci_role_2,
                                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                                        mContent.figureGroup.get(1).getFigureImageid(),
                                        R.drawable.head);
                            }
                        }
                        if (mContent.figureGroup.size() >= 3) {
                            if (mContent.figureGroup.get(2) != null) {
                                ImageUtils.showCommonImage((Activity) mContext,
                                        viewHolder.ci_role_3,
                                        FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                                        mContent.figureGroup.get(2).getFigureImageid(),
                                        R.drawable.more);
                            }
                        }
                    }
                }
            } else {
                isShow = true;
                viewHolder.ci_role_1.setVisibility(View.GONE);
                viewHolder.ci_role_2.setVisibility(View.GONE);
                viewHolder.ci_role_3.setVisibility(View.GONE);

            }


            // 根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);

            // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText(mContent.section);


            } else {
                viewHolder.tvLetter.setVisibility(View.GONE);
            }

            mcontact = list.get(position);

            viewHolder.tvTitle.setText(this.list.get(position).getUIName());


            if (mcontact.xlUserID.equals(PersonSharePreference.getUserID() + "")) {
                viewHolder.isCheckBox.setBackgroundResource(R.drawable.mul_grey);
            } else if (mcontact.isgroupmember) {
                viewHolder.isCheckBox.setBackgroundResource(R.drawable.mul_grey);
            } else if (!isShow) {
                viewHolder.isCheckBox.setBackgroundResource(R.drawable.check_unable);
                mcontact.isSelected = true;
            } else {
                viewHolder.isCheckBox.setBackgroundResource(R.drawable.group_multiple);
            }


            ImageUtils.showCommonImage((Activity) mContext, viewHolder.imgTitle, FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                    list.get(position).file_id, R.drawable.head);


            if(isSelectedMap.containsKey(getItem(position).contactId)){
                viewHolder.isCheckBox.setChecked(true);
            }else{
                viewHolder.isCheckBox.setChecked(false);
            }




            viewHolder.chk_item.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogCatLog.e("Test", "click userId=" + list.get(position).xlUserID);
                    if (list.get(position).isgroupmember) {//已经是群成员

                    } else if (list.get(position).xlUserID.equals(PersonSharePreference.getUserID() + "")) {//自己默认选择

                    } else if (list.get(position).isSelected) {

                    } else {
                        //boolean isItemSelected = isSelectedList.get(position);
                        boolean isItemSelected = isSelectedMap.containsKey(getItem(position).contactId);




                        if (isItemSelected) {
                            //没有选中
                            isSelectedMap.remove(getItem(position).contactId);
                            isSelectedList.put(position, false);
                            viewHolder.isCheckBox.setChecked(false);


                            Contact contact = list.get(position);
                            int size = contact.figureGroup.size();
                            for (int i = 0; i < size; i++) {
                                FigureMode figureMode = contact.figureGroup.get(i);
                                selectedFigure.remove(figureMode.getFigureUsersid());
                            }
                            selectLength.remove(new Integer(size));
                            List<String> before = new ArrayList<String>(selectedFigure);
                            LogCatLog.e("Test", "item before=" + before);
                            int mEnd = selectedFigure.size();
                            for (int i = selectLength.size() - 1; i >= 0; i--) {
                                int mStart = mEnd - selectLength.get(i).intValue();
                                List<String> temp = selectedFigure.subList(mStart, mEnd);
                                LogCatLog.e("Test", "item temp=" + temp + ",mEnd=" + mEnd + ",mStart=" + mStart);
                                before.retainAll(temp);
                                mEnd = mStart;
                            }
                            retainFigure.retainAll(before);
                            LogCatLog.e("Test", "item retainFigure=" + retainFigure);
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).isSelected = false;
                            }

                            notifyDataSetChanged();


                        } else {
                            //选中
                            isSelectedList.put(position, true);
                            isSelectedMap.put(getItem(position).contactId, getItem(position));


                            viewHolder.isCheckBox.setChecked(true);

                            Contact contact = list.get(position);
                            int size = contact.figureGroup.size();
                            List<String> selectItem = new ArrayList<String>();
                            for (int i = 0; i < size; i++) {
                                FigureMode figureMode = contact.figureGroup.get(i);
                                selectedFigure.add(figureMode.getFigureUsersid());
                                selectItem.add(figureMode.getFigureUsersid());

                            }
                            if (retainFigure.size() == 0) {//第一次添加
                                retainFigure.addAll(selectItem);
                            }
                            retainFigure.retainAll(selectItem);

                            selectLength.add(new Integer(size));
                            for (int i = 0; i < list.size(); i++) {
                                list.get(i).isSelected = false;
                            }
                            notifyDataSetChanged();
                        }

                /*        LogCatLog.e("Test", "item selected=" + isSelectedList.get(position));
                        LogCatLog.e("Test", "item selectedFigure=" + retainFigure);*/
                    }
                    /* add by zx */
                    /**
                     * 未选择成员时，将“确认”置灰且不可点击
                     */
                    Iterator<Integer> iterator =isSelectedList.keySet().iterator();
                    List<String> listtest = new ArrayList<String>();
                    while (iterator.hasNext()) {
                        Integer next = iterator.next();
                        Boolean able = isSelectedList.get(next);
                        if (able) {
                            listtest.add("test");
                            LogCatLog.i(TAG, "listtest-----------:" + listtest.size());
                        }
                    }
                    if (listtest.size() > 0) {
                        Resources resource = (Resources) getBaseContext().getResources();
                        ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.head_text_color);
                        if (csl != null) {
                            topView.getRightTextView().setTextColor(csl);
                        }
                        topView.getRightTextView().setEnabled(true);
                    } else {
                        topView.getRightTextView().setTextColor(getResources().getColor(R.color.black1));
                        topView.getRightTextView().setEnabled(false);
                    }
                }
            });

            return view;
        }


        public boolean getFigureList(List<String> figureAll, List<String> selectFigure) {
            ArrayList<String> result = new ArrayList<String>(figureAll);
            result.retainAll(selectFigure);
            if (selectFigure.size() == 0)
                return true;

            boolean isRetain = false;
            if (result.size() > 0) {
                isRetain = true;
                currentFigureId.setLength(0);
                for (int i = 0; i < result.size(); i++) {
                    currentFigureId.append(result.get(i) + ",");
                }
                currentFigureId = currentFigureId.deleteCharAt(currentFigureId.lastIndexOf(","));
            }

            return isRetain;
        }


        void downloadImageHeader(final Activity activity, final ImageView imgView, final int position) {
            FileUtils.downloadFile(context, PersonSharePreference.getUserID(), list.get(position).file_id + "",
                    FileUtils.IMG_CACHE_HEADIMAGE_PATH, new FileMessageListener<FileTask>() {
                        @Override
                        public void success(int statusCode, final FileTask fileTask) {
                            LogCatLog.i("fileName", "------------------" + fileTask.fileName);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LogCatLog.e("Test", "imgPath=" + FileUtils.IMG_CACHE_HEADIMAGE_PATH + fileTask
                                            .fileName);
                                    // imgView.setImageBitmap(ImageUtils.decodeThumbnailsBitmap(FileUtils
                                    // .IMG_CACHE_HEADIMAGE_PATH + fileTask.fileName));
                                    ImageUtils.loadImage(imgView, "file://" + FileUtils.IMG_CACHE_HEADIMAGE_PATH +
                                            fileTask
                                                    .fileName, activity.getResources().getDrawable(R.drawable.head));
                                }
                            });
                        }

                        @Override
                        public void handleing(int statusCode, FileTask fileTask) {

                        }

                        @Override
                        public void failure(int statusCode, FileTask fileTask) {

                        }
                    });
        }

        @Override
        public void onClick(View v) {

        }

        final class ViewHolder {
            TextView tvLetter;
            TextView tvTitle;
            ImageView imgTitle;
            CheckBox isCheckBox;
            LinearLayout chk_item;
            public CircleImage ci_role_1;
            public CircleImage ci_role_2;
            public CircleImage ci_role_3;
        }

        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值
         */
        public int getSectionForPosition(int position) {
            return list.get(position).section.charAt(0);
        }

        /**
         * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
         */
        public int getPositionForSection(int section) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = list.get(i).section;
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 提取英文的首字母，非英文字母用#代替。
         *
         * @param str
         * @return
         */
        private String getAlpha(String str) {
            String sortStr = str.trim().substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortStr.matches("[A-Z]")) {
                return sortStr;
            } else {
                return "#";
            }
        }

        @Override
        public Object[] getSections() {
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (SourceDateList != null) {
            SourceDateList.clear();
        }
        if (mMemberList != null) {
            mMemberList.clear();
        }
        if (mContactList != null) {
            mContactList.clear();
        }

    }

}
