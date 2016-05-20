package com.xianglin.fellowvillager.app.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.adapter.ContactAdapter;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.widget.BladeView;
import com.xianglin.fellowvillager.app.widget.PinnedSectionListView;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.fellowvillager.app.widget.XExpandableListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 共同联系人
 * Created by zhanglisan on 16/3/16.
 */
@EActivity(R.layout.activity_same_contact)
public class SameContactActivity extends BaseActivity {

    @ViewById(R.id.topview)
    TopView mTopView;// 标题栏

    /**侧边拼音列表*/
    @ViewById(R.id.sideBar)
    BladeView mLetter;

    /**
     * 联系人列表适配器
     */
    private ContactAdapter mAdapter;

    @Extra
    String otherFigureId;

    /**联系人列表控件*/
    @ViewById(R.id.contactList)
    PinnedSectionListView mContactLv;
    /**所有共同联系人数据*/
    private List<Contact> mContactList = new ArrayList<>();
    private long isFirstSize = 0;//listview第一次加载完成后的adapter的size

    @Background
    void getSameContactData() {
        SyncApi.getInstance().sameContacts(
                otherFigureId,
                this,
                sameContactCallBack
        );
    }

    /**
     * 相同联系人回调
     */
    private SyncApi.CallBack sameContactCallBack = new SyncApi.CallBack<List<String>>() {
        @Override
        public void success(List<String> mode) {
            setAdapter(getSameContactForDb(mode));
        }

        @Override
        public void failed(String errTip, int errCode) {
            tip(errTip);
        }
    };

    @UiThread
    void setAdapter(List<Contact> list) {
        if (list == null) {
            return;
        }
        if (mAdapter == null) {
            mAdapter = new ContactAdapter(this, true);
        }
        /*点击删除时,软删除,讲联系人加入到黑名单列表中*/
        mAdapter.setOnRightItemClickListener(
                new ContactAdapter.onRightItemClickListener() {
                    @Override
                    public void onRightItemClick(View v, int position) {
                        if (mContactList == null) {
                            return;
                        }
                        Contact contact = mContactList.get(position);
                        if (contact == null) {
                            return;
                        }
                        String id = contact.contactId;
                        if (TextUtils.isEmpty(id)) {
                            return;
                        }
                        moveToBlack(contact);
                    }
                }
        );
        setSection(list);
        mAdapter.setData(mContactList);
        mContactLv.setAdapter(mAdapter);
        hideLoadingDialog();
    }

    @Background
    void moveToBlack(final Contact contact){
        String figureId=contact.figureId;
        if(TextUtils.isEmpty(ContactManager.getInstance().getCurrentFigureID())){
            figureId="";
        }
        if(contact.contactLevel== Contact.ContactLevel.UMKNOWN){
            tip("陌生人不能拉黑");
            return;
        }
        SyncApi.getInstance().moveIntoBlacklist(figureId, contact.xlUserID,
                contact.figureUsersId, this, new SyncApi.CallBack() {
                    @Override
                    public void success(Object mode) {
                        SameContactActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //ContactManager.getInstance().deleteContactInternal(contact.contactId);
                                ArrayList<FigureMode> figureModes=contact.figureGroup;
                                for(int i=0;i<figureModes.size();i++){
                                    Contact delContact=ContactManager.getInstance().getContact(ContactDBHandler
                                            .getContactId(contact.figureUsersId, figureModes.get(i).getFigureUsersid()));
                                    ContactManager.getInstance().deleteContactInternal(delContact.contactId);
                                }
                            }
                        });

                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip("拉黑失败:" + errTip);
                    }
                });
    }


    private void setSection(List<Contact> contactList) {
        if (mContactList != null) {
            mContactList.clear();
        }

        // 排序 必须要排序才能去重
        Collections.sort(contactList, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                if (lhs.pinying.equals(rhs.pinying)) {
                    return lhs.getUIName().compareTo(rhs.getUIName());
                } else {
                    if ("#".equals(lhs.pinying)) {
                        return 1;
                    } else if ("#".equals(rhs.pinying)) {
                        return -1;
                    }
                    return lhs.pinying.compareTo(rhs.pinying);
                }
            }
        });

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
                    mAdapter.getAlpha2(contactVo.pinying).equals("#")) {
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



        mAdapter.setData(mContactList);
        mAdapter.setAlphaPostion(mPositions);
        mAdapter.setAlphaSelection(mSections);

        mLetter.setAlpha(characters);
        mLetter.invalidateAlpha();
        mLetter.setOnItemClickListener(new MyOnItemClickListener(mIndexer));


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
     * 从内存和数据库中读取共同联系人
     * @param contactFigureIdList 共同联系人figureid列表
     * @return
     */
    private List<Contact> getSameContactForDb(List<String> contactFigureIdList) {
        if (contactFigureIdList == null) {
            return null;
        }
        List<Contact> contactList = new ArrayList<>();
        ContactDBHandler contactDBHandler = new ContactDBHandler(this);
        List<String> allFigureIdList = ContactManager.getInstance().getAllFigureIdList();
        if (allFigureIdList == null || allFigureIdList.isEmpty()) {
            return null;
        }
        // 获取本地所有联系人列表
        LinkedList<Contact> allContactList = contactDBHandler.queryAllFigureCommonContact();
        for (Contact contact:
             allContactList) {
            for (String contactFigureId :
                    contactFigureIdList) {
                if (contactFigureId.equals(contact.figureUsersId) && !contactList.contains(contact)) {
                    contactList.add(contact);
                    break;
                }
            }
        }

        return contactList;
    }

    private void setListener() {
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
                        } else if (mContactList.get(position - 1).xlUserID.equals(PersonSharePreference.getUserID() + "")) {//本人信息
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

    /**
     * 跳转到联系人详情页
     *
     * @param contact
     */
    @android.support.annotation.UiThread
    void goToUserDetailPage(Contact contact) {

//        getContactInfo(contact.xlUserID, contact.figureUsersId);

        // 个人主页
        UserDetailBeforeChatActivity_//
                .intent(this)//
                .extra("gowhere", BorrowConstants.MINGLU)//
                .extra("serializable", contact)//
                .titleName(contact.xlUserName)//
                .toChatXlId(contact.xlUserID)//
                .toChatId(contact.figureUsersId)//
                .toChatName(contact.xlUserName)//
                .headerImgId(contact.file_id)//
                .chatType(BorrowConstants.CHATTYPE_SINGLE)//
                .contactId(contact.contactId)
                .start();
            animLeftToRight();
    }

    @AfterViews
    void initViews() {
        mTopView.setAppTitle(R.string.same_contact);
        mTopView.setLeftImageResource(R.drawable.icon_back);
        mTopView.getLeftlayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        View view = new View(this);
        view.setLayoutParams(new AbsListView.LayoutParams(0, 0));
        mContactLv.addHeaderView(view);
        showLoadingDialog();
        setListener();
        getSameContactData();
    }
}
