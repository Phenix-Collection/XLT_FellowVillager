package com.xianglin.fellowvillager.app.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xianglin.appserv.common.service.facade.model.ContactsDTO;
import com.xianglin.appserv.common.service.facade.model.GroupDTO;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.activity.NewCardActivity;
import com.xianglin.fellowvillager.app.activity.NewContactActivity_;
import com.xianglin.fellowvillager.app.activity.SelectBusinessCard;
import com.xianglin.fellowvillager.app.activity.UserDetailBeforeChatActivity_;
import com.xianglin.fellowvillager.app.activity.group.GroupListInContactActivity_;
import com.xianglin.fellowvillager.app.adapter.ContactAdapter;
import com.xianglin.fellowvillager.app.chat.controller.ChatManager;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.longlink.XLNotifierEvent;
import com.xianglin.fellowvillager.app.longlink.listener.XLEventListener;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.ComparatorContact;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.utils.pinyin.PingYinUtil;
import com.xianglin.fellowvillager.app.widget.BladeView;
import com.xianglin.fellowvillager.app.widget.PinnedSectionListView;
import com.xianglin.fellowvillager.app.widget.XExpandableListView;
import com.xianglin.fellowvillager.app.widget.dialog.CardDialog;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * 首页-名录
 *
 * @author chengshengli
 * @version v 1.0.0 2015/11/10 16:07
 */
@EFragment(R.layout.fragment_main_contact)
public class MainContactFragment extends BaseFragment
        implements View.OnClickListener, XLEventListener {

    /**
     * 联系人列表控件
     */
    @ViewById(R.id.contactList)
    PinnedSectionListView mContactLv;
    /**
     * 联系人列表适配器
     */
    private ContactAdapter mAdapter;
    /**
     * 联系人数据列表
     */
    public List<Contact> mContactList = new ArrayList<>();// 根据等级获取好友信息包含陌生人
    public List<Contact> mRealContactList = new ArrayList<>();// 根据等级获取好友信息不包含陌生人
    public List<Contact> allContactList = new ArrayList<>();// 不区分等级好友信息包含陌生人


    @ViewById(R.id.sideBar)
    BladeView mLetter;
    // 没有数据时显示一张图片
    @ViewById(R.id.iv_no_data_tip)
    ImageView mNoDataTip;

    @ViewById(R.id.contact_plus)
    ImageView contact_plus;//加号

    @ViewById(R.id.contact_minus)
    ImageView contact_minus;//减号

    protected boolean hidden;

    private long XLID;
    private String isCard;
    private String name;


    private static final int MSG_UPDATE_LIST = 0;
    private static final int NO_DATA = 1;
    private static final int ADD_PEOPLE_COUNT = 2;
    private static final int SHOW_AND_HIDE_ITEMVIEW = 3;
    private long isFirstSize = 0;//listview第一次加载完成后的adapter的size

    private Dialog finalDialog = null;
    /**
     * 新联系人View
     */
    private LinearLayout mNewContactLayout;
    /**
     * 群聊View
     */
    private LinearLayout mGroupLayout;
    /**
     * 用于显示多少位联系人
     */
    //@ViewById(R.id.tv_people_count)
    TextView mFooterTv;
    View footerView;

    @ViewById(R.id.item_root)//底部
            RelativeLayout item_root;

    private int currentLeave = 0;//当前层级0 1 2 高 普通 陌生
    private int beforeLeave = 0;//先前层级0 1 2 高 普通 陌生
    private boolean hasCurrentHigh = false;
    private boolean hasCurrentNomal = false;
    private boolean hasCurrentUnknow = false;

    @Click(R.id.contact_plus)
    void click_plus() {//放大
        beforeLeave=currentLeave;
        currentLeave++;
        if (currentLeave >= 2) {
            contact_plus.setImageResource(R.drawable.contact_grey_plus);
            currentLeave=2;
            contact_plus.setClickable(false);
        } else {
            contact_plus.setClickable(true);
            contact_plus.setImageResource(R.drawable.contact_plus_icon);
        }
        contact_minus.setClickable(true);
        contact_minus.setImageResource(R.drawable.contact_minus_icon);
        refresh();
    }

    @Click(R.id.contact_minus)
    void click_minus() {//缩小
        beforeLeave=currentLeave;
        currentLeave--;
        if (currentLeave <1) {
            contact_minus.setImageResource(R.drawable.contact_grey_minus);
            currentLeave=0;
            contact_minus.setClickable(false);
        } else {
            contact_minus.setClickable(true);
            contact_minus.setImageResource(R.drawable.contact_minus_icon);
        }
        contact_plus.setClickable(true);
        contact_plus.setImageResource(R.drawable.contact_plus_icon);
        refresh();
    }

    public void setMinusAndPlusItem(){

        if (currentLeave <1) {
            contact_minus.setImageResource(R.drawable.contact_grey_minus);
            currentLeave=0;
            contact_minus.setClickable(false);
        } else {
            contact_minus.setClickable(true);
            contact_minus.setImageResource(R.drawable.contact_minus_icon);
        }
        if (currentLeave >= 2) {
            contact_plus.setImageResource(R.drawable.contact_grey_plus);
            currentLeave=2;
            contact_plus.setClickable(false);
        } else {
            contact_plus.setClickable(true);
            contact_plus.setImageResource(R.drawable.contact_plus_icon);
        }
    }


    /**
     * 从服务端获取最新联系人列表
     */
    @Background
    void getContact() {
     if (ContactManager.getInstance().getCurrentFigure() == null) { // 当前为全部角色
            SyncApi.getInstance().lists(
                    mContext,
                    new SyncApi.CallBack<List<ContactsDTO>>() {
                        @Override
                        public void success(List<ContactsDTO> mode) {
                            if (mode == null) {
                                return;
                            }
                            ContactManager.getInstance().loadContacts(
                                    mode,
                                    true
                            );
                        }

                        @Override
                        public void failed(String errTip, int errCode) {
                            tip(errTip);
                        }
                    }
            );
        } else { // 当前为单个角色
            SyncApi.getInstance().listByFigureId(
                    ContactManager.getInstance().getCurrentFigureID(),
                    mContext,
                    new SyncApi.CallBack<List<ContactsDTO>>() {
                        @Override
                        public void success(List<ContactsDTO> mode) {
                            if (mode == null) {
                                return;
                            }
                            ContactManager.getInstance().loadContacts(
                                    mode,
                                    true
                            );
                        }

                        @Override
                        public void failed(String errTip, int errCode) {
                            tip(errTip);
                        }
                    }
            );
        }


        SyncApi.getInstance().listGroup(
                mContext,
                new SyncApi.CallBack<List<GroupDTO>>() {
                    @Override
                    public void success(List<GroupDTO> mode) {
                        if (mode == null) {
                            return;
                        }
                        GroupManager.getInstance().addGroups(mode, true);
                        refresh();
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                });
    }

    //注解完成执行
    @AfterViews
    void initViews() {
        initHeadView();
        Bundle bundle = getArguments();//从activity传过来的Bundle
        if (bundle != null) {
            isCard = bundle.getString(SelectBusinessCard.ISCARD);
            name = bundle.getString(NewCardActivity.KEYNAME);
        }
        initListener(); // 设置Listview监听
        initData(); // 初始化数据,先从本地db读取数据
        getContact(); //网络同步联系人数据,刷新页面

        contact_plus.setImageResource(R.drawable.contact_plus_icon);
        contact_minus.setImageResource(R.drawable.contact_grey_minus);
        contact_minus.setClickable(false);
    }

    /**
     * 初始化联系人数据
     */
    private void initData() {
      //  mBlackList = ContactManager.getInstance().getBlackList();
        mAdapter = new ContactAdapter(mContext);
        /*点击删除时,软删除,讲联系人加入到黑名单列表中*/
        mAdapter.setOnRightItemClickListener(
                new ContactAdapter.onRightItemClickListener() {
                    @Override
                    public void onRightItemClick(View v, int position) {
                        if (mContactList == null) {
                            return;
                        }
                        Contact contact = mContactList.get(position);
                        if (contact == null
                                ||TextUtils.isEmpty(contact.contactId)) {
                            return;
                        }
                        moveToBlack(contact);
                    }
                }
        );
        mAdapter.setData(mContactList);
        mContactLv.setAdapter(mAdapter);

        XLID = PersonSharePreference.getUserID();
        if ("true".equals(isCard)) {
            mNewContactLayout.setVisibility(View.GONE);
            mGroupLayout.setVisibility(View.GONE);
        }


    }

    @Background
    void moveToBlack(final Contact contact){
        String figureId=contact.figureId;
        if(TextUtils.isEmpty(ContactManager.getInstance().getCurrentFigureID())){
            figureId="";
        }

        if(contact.figureUsersId.equals("1111")){
            tip("乡邻助手不能拉黑");
            return;
        }

        if(contact.contactLevel== Contact.ContactLevel.UMKNOWN){
            tip("陌生人不能拉黑");
            return;
        }
        SyncApi.getInstance().moveIntoBlacklist(figureId, contact.xlUserID,
                contact.figureUsersId, mContext, new SyncApi.CallBack() {
                    @Override
                    public void success(Object mode) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //ContactManager.getInstance().deleteContactInternal(contact.contactId);
                                ArrayList<FigureMode> figureModes=contact.figureGroup;
                                for(int i=0;i<figureModes.size();i++){
                                    Contact delContact=ContactManager.getInstance().getContact(ContactDBHandler
                                            .getContactId(contact.figureUsersId, figureModes.get(i).getFigureUsersid()));
                                    ContactManager.getInstance().deleteContactInternal(delContact.contactId);
                                }
                                refresh();
                            }
                        });

                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip("拉黑失败:" + errTip);
                    }
                });
    }

    @Override
    public void onEvent(XLNotifierEvent xlNotifierEvent) {
        switch (xlNotifierEvent.getEvent()) {
            case EventNewContact:
                Contact contact = (Contact) xlNotifierEvent.getData();
                refresh();
                break;
            case EventNewGroup:
                //showHeadViewContent();
                handler.obtainMessage(MSG_UPDATE_LIST).sendToTarget();
                //refresh();
                break;
            default:
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_search:// 搜索
                break;
            case R.id.layout_newContact:// 新联系人
                NewContactActivity_.intent(this).start();
                if (mBaseActivity != null) {
                    mBaseActivity.animLeftToRight();
                }
                break;
            case R.id.layout_group:// 群聊
                if ("true".equals(isCard)) {
                    tip("群名片暂未开通");
                } else {
                    GroupListInContactActivity_.intent(this).start();
                    if (mBaseActivity != null) {
                        mBaseActivity.animLeftToRight();
                    }
                }
                break;
        }
    }

    /**
     * 没有数据时显示一张图片进行提示
     */
    private void initNoDataTipView() {

        mNoDataTip.setVisibility(View.VISIBLE);
        mNoDataTip.setImageResource(R.drawable.minglu_no_person);

    }

    /**
     * 为联系人ListView设置点击事件监听
     */
    private void initListener() {

        mContactLv.setOnScrollListener(new XExpandableListView.OnXScrollListener() {
            @Override
            public void onXScrolling(View view) {

            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                autoShowLetter(visibleItemCount, totalItemCount);

            }
        });

        if ("true".equals(isCard)) { // 来自卡片
            mContactLv.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(
                                AdapterView<?> parent,
                                View view,
                                int position,
                                long id
                        ) {
                            if(position-1==mContactList.size()){
                                return;
                            }
                            showCardDialog(
                                    name,
                                    mContactList.get(position - 1)

                            ).show();
                        }
                    }
            );
            if (mAdapter != null)
                mAdapter.setOnRightItemClickListener(
                        new ContactAdapter.onRightItemClickListener() {
                            @Override
                            public void onRightItemClick(View v, int position) {
                                tip("请到名录页进行删除操作");
                            }
                        }
                );
        } else {
            mContactLv.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(
                                AdapterView<?> parent,
                                View view,
                                int position,
                                long id
                        ) {
                            if (mContactList == null || position > mContactList.size()) {
                                return;
                            }
                            if (mContactList.get(position - 1) == null
                                    || mContactList.get(position - 1).xlUserID == null
                                    || mContactList.get(position - 1).type == Contact.SECTION) { // 标题
                                return;
                            } else if (mContactList.get(position - 1).xlUserID.equals(XLID + "")) {//本人信息
                                return;
                            }
                            Contact contact = mContactList.get(position - 1);
                            if (contact == null) {
                                return;
                            }

                            goToUserDetailPage(contact);

                        }
                    }
            );
        }

    }

    /**
     * 初始化头部
     */
    private void initHeadView() {

        if (mContactLv.getAdapter() != null) {
            return;
        }
        View header = mBaseActivity.getLayoutInflater().inflate(
                R.layout.layout_head_friend,
                null
        );
        //头部 群
        View layout_header = header.findViewById(R.id.item_root);
        mNewContactLayout = (LinearLayout) layout_header.findViewById(R.id.layout_newContact);
        mGroupLayout = (LinearLayout) layout_header.findViewById(R.id.layout_group);

        //todo 需要更近一步判断 联系人类型

        showHeadViewContent();
        // 动态加入新联系人和群聊视图到ListView中
        mContactLv.addHeaderView(header);

    }

    /**
     * 是否显示HeadViewContent
     * 包括新联系人和群聊视图
     */
    private void showHeadViewContent() {
         String contactKey="contact_"+PersonSharePreference.getUserID()
                 +"_"+ ContactManager.getInstance().getCurrentFigureID();
        String contactAllKey="contact_"+PersonSharePreference.getUserID()
                +"_";
         String groupKey="group_"+PersonSharePreference.getUserID()
                 +"_"+ ContactManager.getInstance().getCurrentFigureID();
            if ((ContactManager.getInstance().getContactTable() != null
                    && mRealContactList.size()!=0)
                    ||Utils.getBooleanValue(contactKey)) {
                mNewContactLayout.setVisibility(View.VISIBLE);
            } else {
                mNewContactLayout.setVisibility(View.GONE);
            }
            if ((GroupManager.getInstance().getAllFigureGroupTable() != null
                    && !GroupManager.getInstance().getCurrentFigureGroupTable().isEmpty())
                    ||Utils.getBooleanValue(groupKey)) {
                mGroupLayout.setVisibility(View.VISIBLE);
            } else {
                mGroupLayout.setVisibility(View.GONE);
            }

        if(mNewContactLayout.getVisibility()==View.VISIBLE){//至少显示过一次
            Utils.putBooleanValue(contactKey,true);
            Utils.putBooleanValue(contactAllKey,true);
        }
        if(mGroupLayout.getVisibility()==View.VISIBLE){
            Utils.putBooleanValue(groupKey,true);
        }

        mNewContactLayout.setOnClickListener(this);
        mGroupLayout.setOnClickListener(this);

        if ("true".equals(isCard)) {
            mNewContactLayout.setVisibility(View.GONE);
            mGroupLayout.setVisibility(View.GONE);
        }

    }

    /**
     * 名片发送对话框
     *
     * @param title
     * @return
     */
    private Dialog showCardDialog(
            String title,
            final Contact contact
    ) {
        Dialog dialog;
        CardDialog.Builder myBuilder = new CardDialog.Builder(mContext);
        myBuilder.setTitle("发送到" + title);

        myBuilder.setMessage(contact.getXlUserName());
        myBuilder.setFileId(contact.file_id);
        myBuilder.setType("0");
        myBuilder.setXlId(contact.figureUsersId);


        myBuilder.setBackButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (finalDialog != null) {
                    finalDialog.dismiss();
                }
            }
        });
        myBuilder.setConfirmButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (finalDialog != null) {
                    finalDialog.dismiss();
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(NewCardActivity.KEYID, contact);
                intent.putExtras(bundle);
                mBaseActivity.setResult(NewCardActivity.RESULT_OK, intent);
                mBaseActivity.finish();
            }
        });
        dialog = myBuilder.create();
        finalDialog = dialog;
        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        return dialog;
    }


    private boolean isFirstLoading=true;

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoading) {
            isFirstLoading=false;
            refreshUI();
        }
        ChatManager.getInstance().registerEventListener(this,
                new XLNotifierEvent.Event[]{XLNotifierEvent.Event.EventNewContact,
                        XLNotifierEvent.Event.EventNewGroup});
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refreshUI();
        }
    }

    // 刷新ui
    public void refreshUI() {
        hasCurrentHigh=false;
        hasCurrentNomal=false;
        hasCurrentUnknow=false;
        currentLeave=0;
        contact_plus.setImageResource(R.drawable.contact_plus_icon);
        contact_minus.setImageResource(R.drawable.contact_grey_minus);
        contact_plus.setClickable(true);
        contact_minus.setClickable(false);


        refresh();
    }
    // 刷新ui
    public void refresh() {
        changeAdapter();
    }

      public   List<Contact> getCurrentFigureForContact(Map<String, Contact> contactMap ,Contact.ContactLevel contactLevel){
          List<Contact> contactList = new ArrayList<>(); // 联系人列表
          for (Map.Entry<String, Contact> entry :
                  contactMap.entrySet()) {
              String contactId = entry.getKey();
              Contact contact = entry.getValue();

              if (contact.figureId.equals(ContactManager.getInstance().getCurrentFigureID())){
                  if(contact.contactLevel.ordinal()<=contactLevel.ordinal()){
                      contactList.add(contact);
                  }

              }
          }
          return  contactList;
    }




    @Background
    public void changeAdapter() {

        synchronized (mContactList) {

            allContactList.clear();
            mContactList.clear();
            mRealContactList.clear();
              int contactSize=0;

            List<Contact> contactList = new ArrayList<>(); // 联系人列表
            if (ContactManager.getInstance().getCurrentFigure() != null) { // 单个角色

                //角色和角色之间切换
                Map<String, Contact> contactMap = ContactManager.getInstance().getContactTable();
                if (contactMap == null) {
                    return;
                }

                for (Map.Entry<String, Contact> entry :
                        contactMap.entrySet()) {
                    String contactId = entry.getKey();
                    Contact contact = entry.getValue();
                    if (contact.figureId.equals(ContactManager.getInstance().getCurrentFigureID())) {
                        if (contact.contactLevel != Contact.ContactLevel.UMKNOWN) {
                            mRealContactList.add(contact);
                        }

                        if (contact.contactLevel == Contact.ContactLevel.HIGH) {
                            hasCurrentHigh = true;
                        }else if (contact.contactLevel == Contact.ContactLevel.NORMAL
                                ||contact.contactLevel == Contact.ContactLevel.LOW) {
                            hasCurrentNomal = true;
                            //continue;
                        } else if (contact.contactLevel == Contact.ContactLevel.UMKNOWN) {
                            hasCurrentUnknow = true;
                        }
                    }
                }

                allContactList=getCurrentFigureForContact(contactMap, Contact.ContactLevel.UMKNOWN);
                if (currentLeave == 0) {
                    contactList=  getCurrentFigureForContact(contactMap,Contact.ContactLevel.HIGH);
                    if(contactList.isEmpty()){
                        currentLeave=1;
                    }
                }
                if (currentLeave ==1) {
                    contactList=  getCurrentFigureForContact(contactMap,Contact.ContactLevel.LOW);

                    List<Contact>  highContactlist = getCurrentFigureForContact(contactMap,Contact.ContactLevel.HIGH);

                    List<Contact>  umknownContactlist = getCurrentFigureForContact(contactMap,Contact.ContactLevel.UMKNOWN);

                    if( beforeLeave< currentLeave) {
                        if (highContactlist.size() == contactList.size()) {
                            currentLeave = 2;
                        }
                        if (umknownContactlist.size() == contactList.size()) {
                            currentLeave = 2;
                        }
                    }else{


                            if (highContactlist.size() == contactList.size()) {
                                contactList=highContactlist;
                                currentLeave = 0;
                            }

                        if (umknownContactlist.size() == contactList.size()) {
                            contactList=highContactlist;
                            currentLeave =0;
                        }




                    }
                }
                contactSize=contactList.size();//好友人数
                if (currentLeave ==2) {
                    contactList=  getCurrentFigureForContact(contactMap, Contact.ContactLevel.UMKNOWN);
                }


//-------------------------------------
/*                for (Map.Entry<String, Contact> entry :
                        contactMap.entrySet()) {
                    String contactId = entry.getKey();
                    Contact contact = entry.getValue();

                    if (contact.figureId.equals(ContactManager.getInstance().getCurrentFigureID())){
                        allContactList.add(contact);
                        if (currentLeave == 0) {
                            if (contact.contactLevel == Contact.ContactLevel.HIGH) {
                                hasCurrentHigh = true;
                            }else if (contact.contactLevel == Contact.ContactLevel.NORMAL
                                    ||contact.contactLevel == Contact.ContactLevel.LOW) {
                                hasCurrentNomal = true;
                                continue;
                            } else if (contact.contactLevel == Contact.ContactLevel.UMKNOWN) {
                                hasCurrentUnknow = true;
                                continue;
                            }
                        }

                        if (currentLeave == 1) {
                            if (contact.contactLevel == Contact.ContactLevel.UMKNOWN) {
                                continue;
                            }
                        }



                        if (currentLeave == 2) {

                        }

                        if (contact == null) {
                            continue;
                        }
                        contactList.add(contact);
                        if(contact.contactLevel!=Contact.ContactLevel.UMKNOWN){
                            mRealContactList.add(contact);
                        }
                    }



                }*/

                if(contactList.size()==0){//只有陌生人
                    contactList.addAll(allContactList);//把陌生人加进去
                    LogCatLog.e("Test","当前没有高频联系人");
                    currentLeave=1;
                }
                //开始排序
                new ComparatorContact().sort(contactList);

            } else { // 全部角色
                //角色和全部之间切换
                List<Contact> listContact=null;
                allContactList.addAll(new ContactDBHandler(mContext)
                        .queryAllFigureContact(Contact.ContactLevel.UMKNOWN));

                //if(allContactList.size()>15){
                if(currentLeave==0){
                    listContact = new ContactDBHandler(mContext)
                            .queryAllFigureContact(Contact.ContactLevel.HIGH);
                    if(listContact.isEmpty()){
                        currentLeave=1;
                    }
                }

                if(currentLeave==1){
                    listContact = new ContactDBHandler(mContext)
                            .queryAllFigureContact(Contact.ContactLevel.LOW);


                    List<Contact>  highContactlist = new ContactDBHandler(mContext)
                            .queryAllFigureContact(Contact.ContactLevel.HIGH);

                    List<Contact>  umknownContactlist = new ContactDBHandler(mContext)
                            .queryAllFigureContact(Contact.ContactLevel.UMKNOWN);

                    if( beforeLeave< currentLeave) {
                        if (highContactlist.size() == listContact.size()) {
                            currentLeave = 2;
                        }
                        if (umknownContactlist.size() == listContact.size()) {
                            currentLeave = 2;
                        }
                    }else{
                   /*     if (highContactlist.size() == listContact.size()) {
                            currentLeave = 0;
                        }*/


                        if (highContactlist.size() == listContact.size()) {
                            listContact=highContactlist;
                            currentLeave = 0;
                        }

                        if (umknownContactlist.size() == listContact.size()) {
                            listContact=highContactlist;
                            currentLeave =0;
                        }




                    }
                }
                //contactSize=listContact!=null?listContact.size():0;//好友人数
                if(currentLeave==2){
                    listContact = new ContactDBHandler(mContext)
                            .queryAllFigureContact(Contact.ContactLevel.UMKNOWN);
                }

            //    }
/*                else{
                    listContact=allContactList;
                }*/

                Map<String,Contact> mContactMap=new HashMap<String,Contact>();
                Map<String,Contact> mStrangerMap=new HashMap<String,Contact>();
                Contact contactTemp,strangerTemp;
                for(int i=0;i<listContact.size();i++){
                    Contact mContact=listContact.get(i);
                    contactTemp=mContact;
                    strangerTemp=mContact;
                    ArrayList<FigureMode> figureModeList=mContact.figureGroup;
                    //我与对方是联系人的角色列表
                    ArrayList<FigureMode> contactFigureList=new ArrayList<FigureMode>();
                    //我与对方是陌生人的角色列表
                    ArrayList<FigureMode> strangerList=new ArrayList<FigureMode>();
                    for(int j=0;j<figureModeList.size();j++){
                        FigureMode figureMode=figureModeList.get(j);

                        if(figureMode!=null){
                            String id= ContactDBHandler
                                    .getContactId(mContact.figureUsersId,figureMode.getFigureUsersid());
                            Contact contact=ContactManager.getInstance().getContact(id);


                            if(contact.contactLevel== Contact.ContactLevel.UMKNOWN){
                                strangerList.add(figureMode);
                                strangerTemp=contact;
                                //mContact.contactLevel=contact.contactLevel;

                            }else{
                                contactFigureList.add(figureMode);
                                //mContact.contactLevel=contact.contactLevel;
                                contactTemp=contact;
                            }
                        }
                    }
                 //   for(int j=0;j<2;j++){
                        if (strangerList.size() > 0) {
                            strangerTemp.figureGroup = strangerList;
                            mStrangerMap.put(mContact.contactId, strangerTemp);
                            hasCurrentUnknow = true;
                        }

                        if (contactFigureList.size() > 0) {
                            contactTemp.figureGroup = contactFigureList;
                            mContactMap.put(mContact.contactId, contactTemp);
                            hasCurrentHigh = true;
                        }
                  //  }

                }

                ArrayList<Contact> levelContactList=new ArrayList<>();
                for(int i=0;i<listContact.size();i++){
                    String contactId=listContact.get(i).contactId;
                    if(mContactMap.containsKey(contactId)){
                        levelContactList.add(mContactMap.get(contactId));
                    }

                }
                for(int i=0;i<listContact.size();i++){
                    String contactId=listContact.get(i).contactId;

                    if(mStrangerMap.containsKey(contactId)){
                        levelContactList.add(mStrangerMap.get(contactId));
                    }

                }

                contactList.addAll(levelContactList);
                for (int i=0;i<allContactList.size();i++){

                    Contact contact=allContactList.get(i);

                    ArrayList<FigureMode> figureModeList=contact.figureGroup;

                    for(int j=0;j<figureModeList.size();j++) {

                        FigureMode figureMode = figureModeList.get(j);

                        Contact contact1=ContactManager.getInstance().getContact(ContactDBHandler.
                                getContactId(contact.figureUsersId,figureMode.getFigureUsersid()));

                        if (contact1.contactLevel == Contact.ContactLevel.HIGH) {
                            hasCurrentHigh = true;
                            contactSize++;
                        }else if (contact1.contactLevel == Contact.ContactLevel.NORMAL
                                ||contact1.contactLevel == Contact.ContactLevel.LOW) {
                            contactSize++;
                            hasCurrentNomal = true;
                            //continue;
                        } else if (contact1.contactLevel == Contact.ContactLevel.UMKNOWN) {
                            hasCurrentUnknow = true;
                        }
                    }
                }
                if(contactList.size()==0){
                    currentLeave=1;
                    LogCatLog.e("Test","all figure has no friends");
                    contactList.addAll(allContactList);
                }
            }

            if("true".equals(isCard)){
                 //卡片模式下过滤乡邻助手
                Iterator<Contact> iterator= contactList.iterator() ;
                while (iterator.hasNext()){
                    if(iterator.next().xlUserID.equals("1111")){
                        iterator.remove();
                        contactSize--;
                    }
                }

            }



            // 首字母集
            List<String> mSections = new ArrayList<String>();
            // 首字母位置集
            List<Integer> mPositions = new ArrayList<Integer>();
            // 首字母对应的位置
            Map<String, Integer> mIndexer = new HashMap<String, Integer>();

            String previous_section = "";
            LinkedHashSet<Character> alphaChars = new LinkedHashSet<Character>();//动态长度的拼音索引
            int size = contactList.size();
            for (int i = 0; i < size; i++) {

                Contact contactVo = contactList.get(i);
                String current_section;
                if (TextUtils.isEmpty(contactVo.pinying) ||
                        PingYinUtil.getAlpha(contactVo.pinying).equals("#")) {
                    current_section = "#";
                    alphaChars.add(current_section.charAt(0));
                } else {
                    current_section = contactVo.pinying.substring(0, 1).toUpperCase();
                    alphaChars.add(current_section.charAt(0));
                }

                if (!current_section.equals(previous_section)) {
                    Contact contact = new Contact.Builder(Contact.SECTION)
                            .section(current_section).build();
                    mContactList.add(contact);
                }

                contactVo.type = Contact.ITEM;
                contactVo.section = current_section;

                mContactList.add(contactVo);
                previous_section = current_section;
            }

            Character[] characters = alphaChars.toArray(new Character[]{});
            Arrays.sort(characters);

            for (int i = 0; i < mContactList.size(); i++) {
                if (mContactList.get(i).type == Contact.SECTION) {
                    String alpha = mContactList.get(i).section;
                    mSections.add(alpha);//// 首字母集
                    mPositions.add(i);// 首字母在listview中位置，存入list中
                    mIndexer.put(alpha, i);// 存入map中，key为首字母字符串，value为首字母在listview中位置
                }
            }

            //层级小于2隐藏
            if (getCurrentLeave() < 2) {
                handler.obtainMessage(SHOW_AND_HIDE_ITEMVIEW,false).sendToTarget();
            }else{
                handler.obtainMessage(SHOW_AND_HIDE_ITEMVIEW,true).sendToTarget();
            }


            mAdapter.setData(mContactList);
            mAdapter.setAlphaPostion(mPositions);
            mAdapter.setAlphaSelection(mSections);

            mLetter.setAlpha(characters);
            mLetter.invalidateAlpha();
            mLetter.setOnItemClickListener(new MyOnItemClickListener(mIndexer));


            Message msg=new Message();
            msg.what=ADD_PEOPLE_COUNT;
            msg.arg1=contactSize;
            handler.sendMessage(msg);
            handler.obtainMessage(MSG_UPDATE_LIST).sendToTarget();

            if (allContactList.isEmpty()) {
                hideLoadingDialog();
                handler.sendEmptyMessage(NO_DATA);
                return;
            }

        }


        hideLoadingDialog();

    }








    /**
     * 获取当前层级
     * @return
     */
    private int getCurrentLeave(){
        int currentLeaveNum=0;
        if(hasCurrentHigh){
            currentLeaveNum++;
        }
        if(hasCurrentNomal){
            currentLeaveNum++;
        }
        if(hasCurrentUnknow){
            currentLeaveNum++;
        }
        LogCatLog.e("Test","currentLeaveNum="+currentLeaveNum);
        return currentLeaveNum;
    }

    /**
     * 自动显示sideBar
     *
     * @param visibleItemCount
     * @param totalItemCount
     */
    private void autoShowLetter(int visibleItemCount, int totalItemCount) {
        if (visibleItemCount > 0 && isFirstSize != totalItemCount) {
            isFirstSize = totalItemCount;
            if (totalItemCount > visibleItemCount) {
                //listview 的高度大于屏幕的高度 显示mLetter
                mLetter.setVisibility(View.VISIBLE);
            } else {
                mLetter.setVisibility(View.GONE);
            }
        }
    }


    /**
     * sidebar 快速选择联系人
     */
    private class MyOnItemClickListener implements BladeView.OnItemClickListener {

        Map<String, Integer> mIndexer = null;

        public MyOnItemClickListener(Map<String, Integer> mIndexer) {
            this.mIndexer = mIndexer;
        }

        @Override
        public void onItemClick(String s) {
            if (mIndexer.get(s) != null) {
                mContactLv.setSelection(mIndexer.get(s) + 1);//添加头部了，所以要加1
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();
       // ChatManager.getInstance().unregisterEventListener(this);
    }

    @SuppressWarnings("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_LIST: {
                    showHeadViewContent();
                    setMinusAndPlusItem();
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }

                }
                break;
                case SHOW_AND_HIDE_ITEMVIEW: {

                    boolean isShow= (boolean) msg.obj;
                    item_root.setVisibility(isShow?View.VISIBLE:View.GONE);

                }
                break;
                case NO_DATA: {
                    // 没有数据时不显示“新联系人”
                    if (mNewContactLayout != null) {
                        mNewContactLayout.setVisibility(View.GONE);
                    }
                    if (mFooterTv != null) {
                        mFooterTv.setVisibility(View.GONE);
                    }
                    if(footerView!=null&&mContactLv.getFooterViewsCount()==1){
                        mContactLv.removeFooterView(footerView);
                    }
                    initNoDataTipView();
                }
                break;
                case ADD_PEOPLE_COUNT: {
                    mNoDataTip.setVisibility(View.GONE);
						if (mContactLv.getFooterViewsCount() == 0) {
							// 在PinnedSectionListView底部添加TextView用于显示有多少联系人
                            footerView = mBaseActivity.getLayoutInflater().inflate(R.layout.layout_footer_people_count, null);
							mFooterTv = (TextView) footerView.findViewById(R.id.tv_people_count);
							mContactLv.addFooterView(footerView);
						}
                    //有数据时显示“新联系人”
/*                    if (mNewContactLayout != null) {
                        mNewContactLayout.setVisibility(View.VISIBLE);
                    }*/
                    if(footerView!=null)footerView.setVisibility(View.VISIBLE);
                    if (mFooterTv != null) {
                        mFooterTv.setVisibility(View.VISIBLE);
//                        List<Contact> tmpList = getContactExceptTitle(mContactList);
//                        if (tmpList == null) {
//                            mFooterTv.setText("0位联系人");
//                        } else {
//                            mFooterTv.setText(tmpList.size() + "位联系人");
//                        }
                        mFooterTv.setText(msg.arg1+ "位联系人");
                    }
                    autoShowLetter(mContactLv.getLastVisiblePosition() -
                            mContactLv.getFirstVisiblePosition(), mAdapter.getCount());
                }
                break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取去除字母标题的所有联系人列表
     *
     * @return 联系人列表
     */
    private List<Contact> getContactExceptTitle(
            List<Contact> list
    ) {
        if (list == null) {
            return null;
        }
        List<Contact> contacts = new ArrayList<>();
        for (Contact contact :
                list) {
            if (contact == null) {
                continue;
            }
            if (Contact.SECTION == contact.type) { // 标题
                continue;
            }
            if (contact.contactLevel == Contact.ContactLevel.UMKNOWN) { // 去掉陌生人
                continue;
            }
            contacts.add(contact);
        }
        return contacts;
    }

    @Background
    void refreshContactData(final Contact contact) {
        if (ContactManager.getInstance().getCurrentFigure() == null) { // 当前为全部角色
            SyncApi.getInstance().lists(
                    mContext,
                    new SyncApi.CallBack<List<ContactsDTO>>() {
                        @Override
                        public void success(List<ContactsDTO> mode) {
                            if (mode == null) {
                                return;
                            }
                            ContactManager.getInstance().loadContacts(
                                    mode,
                                    true
                            );
                            goToUserDetailPage(contact);

                        }

                        @Override
                        public void failed(String errTip, int errCode) {
                            tip(errTip);
                        }
                    }
            );
        } else { // 当前为单个角色
            SyncApi.getInstance().listByFigureId(
                    ContactManager.getInstance().getCurrentFigureID(),
                    mContext,
                    new SyncApi.CallBack<List<ContactsDTO>>() {
                        @Override
                        public void success(List<ContactsDTO> mode) {
                            if (mode == null) {
                                return;
                            }
                            ContactManager.getInstance().loadContacts(
                                    mode,
                                    true
                            );
                            goToUserDetailPage(contact);

                        }

                        @Override
                        public void failed(String errTip, int errCode) {
                            tip(errTip);
                        }
                    }
            );
        }
    }

    /**
     * 跳转到联系人详情页
     *
     * @param contact
     */
    @UiThread
    void goToUserDetailPage(Contact contact) {

        ArrayList<FigureMode> figureModes=contact.figureGroup;

        if(figureModes==null){
            LogCatLog.e(TAG,"goToUserDetailPage()-> figureGroup is null");
        }
        if (figureModes.isEmpty()||figureModes.get(0) == null) {
            return;
        }

        // 个人主页
        UserDetailBeforeChatActivity_//
                .intent(mContext)//
                .extra("gowhere", BorrowConstants.MINGLU)//
                .extra("serializable", contact)//
                .titleName(contact.xlUserName)//
                .toChatXlId(contact.xlUserID)//
                .toChatId(contact.figureUsersId)//
                .toChatName(contact.xlUserName)//
                .headerImgId(contact.file_id)//
                .chatType(BorrowConstants.CHATTYPE_SINGLE)//
                .contactId(contact.contactId)
                .currentFigureId(figureModes.get(0).getFigureUsersid())
                .start();
        if (mActivity instanceof BaseActivity) {
            ((BaseActivity) mActivity).animLeftToRight();
        }
    }

}
