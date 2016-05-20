
package com.xianglin.fellowvillager.app.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.adapter.FragmentMessageAdapter;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.ContactDBHandler;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.db.MessageDBHandler;
import com.xianglin.fellowvillager.app.db.MomentDialogueDBHandler;
import com.xianglin.fellowvillager.app.loader.MomentMessageLoader;
import com.xianglin.fellowvillager.app.longlink.MessageHandler;
import com.xianglin.fellowvillager.app.longlink.XLConversation;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.FigureMode;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.model.MomentDialogue;
import com.xianglin.fellowvillager.app.model.RecentMessageBean;
import com.xianglin.fellowvillager.app.utils.ComparatorMessage;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.event.FrozenEvent;
import com.xianglin.fellowvillager.app.widget.FragmentContactListView;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：乡邻小站
 * 类描述：
 * 创建人：何正纬
 * 创建时间：2015/11/25 14:34
 * 修改人：hezhengwei
 * 修改时间：2015/11/25 14:34
 * 修改备注：
 */
@EFragment(R.layout.fragment_contact_activity)
public class MainMessageFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @ViewById(R.id.listview)
    FragmentContactListView mListView;
    @ViewById(R.id.txt_nochar_tip)
    TextView mNoDataTip;
    @ViewById(R.id.rl_error_item)
    View net_none;

    private MessageDBHandler messageDBHandler;

    private MomentMessageLoader mMomentMessageLoader;
    private FragmentMessageAdapter mAdapter;
    private MomentDialogueDBHandler mMomentDialogueDBHandler;

    public void showNetNoneState(boolean show) {
        if (net_none != null) {
            if (show) {
                net_none.setVisibility(View.VISIBLE);
            } else {
                net_none.setVisibility(View.GONE);
            }
        }
    }

    @AfterViews
    void init() {
        messageDBHandler = new MessageDBHandler(mContext);
        mMomentDialogueDBHandler = new MomentDialogueDBHandler(mContext);
        mMomentMessageLoader = new MomentMessageLoader(mContext);
        LoaderManager lm = getLoaderManager();
        //标识当前fragment中唯一的 load id;
        lm.initLoader(0, getArguments(), new MomentMessageCallbacks());
    }

    public void onContentChanged() {

        if(mMomentMessageLoader==null){ //可能会被回收
            mMomentMessageLoader=new MomentMessageLoader(mContext);
        }
        mMomentMessageLoader.onContentChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        RecentMessageBean mRecentMessageBean = (RecentMessageBean) arg0.getAdapter().getItem(arg2);

        // readUnMsg(mRecentMessageBean.getXlListId());
        String headimage = !TextUtils.isEmpty(mRecentMessageBean.getXlImagePath()) ?
                mRecentMessageBean.getXlImagePath() : mRecentMessageBean.getFile_id();
        if (mRecentMessageBean.getXlListType() == BorrowConstants.CHATTYPE_SINGLE) {

            ChatMainActivity_//
                    .intent(this)//
                    .currentFigureId(mRecentMessageBean.getFigureId())// 当前角色
                    .toChatXlId(mRecentMessageBean.getXlListId())// 附近的人
                    .toChatId(ContactManager.getInstance().getContact(mRecentMessageBean.getContactId()).figureUsersId)//
                    .titleName(mRecentMessageBean.getXlListTitle())
                    .headerImgId(headimage)
                    .toChatName(mRecentMessageBean.getXlListTitle())
                    .chatType(BorrowConstants.CHATTYPE_SINGLE)//
                    .start();

            return;
        }

        //// TODO: 2015/12/23 卡顿,需要移到chatmain中
        ChatMainActivity_//
                .intent(this)//
                .currentFigureId(mRecentMessageBean.getFigureId())// 当前角色
                .toChatXlId(mRecentMessageBean.getXlListId())// 附近的人
                .toChatId(GroupManager.getInstance().getGroup(mRecentMessageBean.getXlListId()).xlGroupID)//
                .titleName(mRecentMessageBean.getXlListTitle())
                .headerImgId(headimage)
                .toChatName(mRecentMessageBean.getXlListTitle())
                .chatType(BorrowConstants.CHATTYPE_GROUP)//
                .start();
    }

    private class MomentMessageCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return mMomentMessageLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
                    initView(initData(data));
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    @SuppressWarnings("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(MainMessageFragment.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        XLApplication.isHome = false;
        //处理部分机型锁屏后断网的情况
        showNetNoneState(!DeviceInfoUtil.isNetAvailble(mContext));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(MainMessageFragment.this);
    }

    @Subscribe
    public void onEvent(FrozenEvent event) {
        String str = event.getFigureId();
        boolean isFrozen = event.isFrozen();
        onContentChanged();
    }

    /**
     * 初始化数据
     *
     * @param mCursor
     */
    public ArrayList<RecentMessageBean> initData(Cursor mCursor) {

        if (mCursor.isAfterLast()) {
            mCursor.moveToPosition(-1);
        }
        ArrayList<RecentMessageBean> mData =new ArrayList<RecentMessageBean>();
        while (mCursor.moveToNext()) {

            RecentMessageBean mWXMessage=getMessage(mCursor);

            if (mWXMessage==null) {

                //被动陌生消息来源
/*                if (mWXMessage.getXlListType() == BorrowConstants.CHATTYPE_GROUP) {
                    // getDetailGroup(mWXMessage.getXlListId());//陌生的群信息
                    LogCatLog.d(TAG, "有陌生群" + mWXMessage.toString());
                } else {
                    LogCatLog.d(TAG, "有陌生人" + mWXMessage.toString());
                    //  getDetailContact(mWXMessage);//陌生的联系人
                }*/
                //主动找附近的人
            } else {
                Map<String, FigureMode> frozenFigureList =
                        ContactManager.getInstance().getFreezeFigureTable();
                LogCatLog.d(TAG, "frozenFigureList = " + frozenFigureList);
                LogCatLog.d(TAG, "mWXMessage.getFigureId() = " + mWXMessage.getFigureId());
                    //按角色过滤
                    if (frozenFigureList.size() == 0) {
                        mData.add(mWXMessage);
                    } else {
                        if(!frozenFigureList.containsKey(mWXMessage.getFigureId())){
                            mData.add(mWXMessage);
                        }
                    }
            }
        }

        //开始排序
        new ComparatorMessage().sort(mData);
        return mData;
    }

    /**
     * 初始化views
     */
    public synchronized void initView(final ArrayList<RecentMessageBean> mData) {

        mNoDataTip.setVisibility(mData.size() > 0 ? View.GONE : View.VISIBLE);

        //  if (mAdapter == null) {
        mAdapter = new FragmentMessageAdapter(mContext, mData, mListView.getRightViewWidth());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(MainMessageFragment.this);
        mAdapter.setOnRightItemClickListener(new FragmentMessageAdapter.onRightItemClickListener() {
            @Override
            public void onRightItemClick(View v, int position) {
                if (position >= 0 & mData.size() > 0) {

                    RecentMessageBean mWXMessage = mData.get(position);
                    if (mWXMessage.getXlListType() == BorrowConstants.CHATTYPE_SINGLE) {
                        mMomentDialogueDBHandler.del(new MomentDialogue.Builder()
                                .xlID(PersonSharePreference.getUserID() + "")
                                .contactId(mWXMessage.getContactId())
                                .build());
                    } else if (mWXMessage.getXlListType() == BorrowConstants.CHATTYPE_GROUP) {
                        mMomentDialogueDBHandler.del(new MomentDialogue.Builder()
                                .xlID(PersonSharePreference.getUserID() + "")
                                .localGroupId(mWXMessage.getXlListId())
                                .build());
                    }

                    View view = mListView.getChildAt(position - mListView.getFirstVisiblePosition());
                    if (view != null) {
                        mListView.deleteItem(view);
                    }
                    mData.remove(position);
                    mAdapter.setData(mData);
                    mAdapter.notifyDataSetChanged();
                    if (mData.size() == 0) {
                        mNoDataTip.setVisibility(View.VISIBLE);
                    }
                    //  CustomToast.showToast(mContext, "删除第  " + (position) + " 对话记录", 1000);
                }
            }
        });
        /*} else {
              mAdapter.setData(mData);
              mAdapter.notifyDataSetChanged();
        }*/
    }


    void readUnMsg(final String toChatId) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                messageDBHandler.autoReadMsg(toChatId);//标记为,已读在加载数据之前调用
            }
        });
    }
    public RecentMessageBean getMessage(Cursor cursor) {


        String chatid = mMomentDialogueDBHandler.getChatIdForCursor(cursor);

        Hashtable<String, XLConversation> conversationHashtable= MessageHandler.getInstance().getConversations();

        if(conversationHashtable.containsKey(chatid)){
            XLConversation xlConversation =conversationHashtable.get(chatid);
            MessageBean messageBean=null;

            boolean isGroup = mMomentDialogueDBHandler.isGroup(cursor);

            List<MessageBean> list = xlConversation.getAllMessages();
            for (int i = list.size() - 1; i >= 0; i--) {
                MessageBean mb=list.get(i);
                if(mb.isExpired){
                    //如果最后一条是到期的私密消息,就显示上一条
                    continue;
                }else{
                    messageBean=mb;
                    break;
                }
            }
            //没有需要显示的消息
            if(messageBean==null){
                return null;
            }


            RecentMessageBean bean = new RecentMessageBean();
            bean.setExpired( messageBean.isExpired);
            bean.setPrivate(messageBean.isPrivate());

            //-----------end-----

            if (!isGroup) {

                Contact contact=ContactManager.getInstance().getContact(ContactDBHandler.getContactId(messageBean));

                if(contact==null){
                    return null ;
                }
                bean.setXlListType(BorrowConstants.CHATTYPE_SINGLE);
                //单聊
                bean.setXlListId(contact.xlUserID);
                bean.setXlListTitle(contact.getXlUserName());
                //头像
                bean.setXlImagePath(contact.xlImagePath);

            } else {
                Group group=GroupManager.getInstance().getGroup(GroupDBHandler.getGroupId(messageBean.xlID,messageBean.figureId));
                if(group==null){
                    return null ;
                }
                bean.setXlListType(BorrowConstants.CHATTYPE_GROUP);
                //群聊
                bean.setXlListId(group.localGroupId);
                //群名
                bean.setXlListTitle(group.xlGroupName);
                //头像
                bean.setXlImagePath(group.xlGroupImagePath);

            }

            bean.setXlLastMsg(messageBean.msgContent);
            bean.setXlLastTime(messageBean.msgDate);
            bean.setMsg_type(messageBean.msgType+"");
            bean.setFile_id(messageBean.file_id);
            // bean.setIssend(messageBean.d);
            bean.setFigureId(messageBean.figureId);
            bean.setCreatedate(messageBean.msgCreatedate);
            bean.setContactId(ContactDBHandler.getContactId(messageBean));
            bean.setMsgStatus(messageBean.msgStatus);
            String s = xlConversation.getUnreadMsgCount()+"";
            bean.setXlMsgNum(TextUtils.isEmpty(s) ? "0" : s);

            return bean;

        }


        return null;



    }
}