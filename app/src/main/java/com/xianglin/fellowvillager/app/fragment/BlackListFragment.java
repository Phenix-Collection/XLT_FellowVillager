package com.xianglin.fellowvillager.app.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.adapter.BlackListAdapter;
import com.xianglin.fellowvillager.app.chat.controller.ContactManager;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.widget.PinnedSectionListView;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单
 *
 * @author bruce yang
 * @version v 1.0.0 2016/3/17
 */
@EFragment(R.layout.fragment_black_list)
public class BlackListFragment extends BaseFragment {

    @ViewById(R.id.blacklist)
    PinnedSectionListView mPinnedSectionListView;

    private List<String> mUserList = new ArrayList<>();
    private List<Group> mGroupList = new ArrayList<>();// contact黑名单列表
    private List<Contact> mUserBlacklist = new ArrayList<>(); //group黑名单列表
    private BlackListAdapter mUserAdapter, mGroupAdapter;
    private String mArg1 = "blacklist_type"; //0-联系人黑名单; 1-群黑名单
    private String mArg2 = "current_figure";

    private int mType;
    private String curFigureId;

    public BlackListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_black_list, container, false);
        mPinnedSectionListView = (PinnedSectionListView) rootView.findViewById(R.id.blacklist);
        init();
        return rootView;
    }

    void init() {
        curFigureId = getArguments().getString(mArg2);
        mType = getArguments().getInt(mArg1);

        if (0 == mType) {
            mUserBlacklist = ContactManager.getInstance().getBlackList(); //用户黑名单
//            if (mUserList.size() > 0 && !(mUserBlacklist.size() > 0)) {
//                Contact mContact;
//                for (int i = 0; i < mUserList.size(); i++) {
//                    mContact = ContactManager.getInstance().getContact(mUserList.get(i));
//                    if (!(mContact.figureId).isEmpty() && (mContact.figureId).equals(curFigureId))
//                        mUserBlacklist.add(ContactManager.getInstance().getContact(mUserList.get(i)));
//                }
//            }
            mUserAdapter = new BlackListAdapter(mContext);
            mUserAdapter.setUserData(mUserBlacklist);
            mPinnedSectionListView.setAdapter(mUserAdapter);
            mUserAdapter.setOnRightItemClickListener(new BlackListAdapter.onRightItemClickListener() {
                @Override
                public void onRightItemClick(View v, int position) {
                    if (position >= 0 & mUserBlacklist.size() > 0) {
                        final int curPosition = position;
                        moveUserOutofBlackList(mUserBlacklist, curPosition);
                    }
                }
            });
        } else if (1 == mType) {
            mGroupList = GroupManager.getInstance().getBlackListGroupByFigureId();//群黑名单列表

            mGroupAdapter = new BlackListAdapter(mContext);
            mGroupAdapter.setGroupData(mGroupList);
            mPinnedSectionListView.setAdapter(mGroupAdapter);
            mGroupAdapter.setOnRightItemClickListener(new BlackListAdapter.onRightItemClickListener() {
                @Override
                public void onRightItemClick(View v, int position) {
                    if (position >= 0 & mGroupList.size() > 0) {
                        final String mGroupId = mGroupList.get(position).xlGroupID;
                        final String mFigureId = mGroupList.get(position).figureId;
                        final int curPosition = position;
                        moveGroupOutofBlackList(mGroupList, curPosition, mFigureId, mGroupId);
                    }
                }
            });
        }
    }

    void moveUserOutofBlackList(final List<Contact> userList, final int position) {
        final Contact mContact = userList.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SyncApi.getInstance().moveOutofBlacklist(mContact.figureId, mContact.xlUserID,
                        mContact.figureUsersId, mContext, new SyncApi.CallBack<Boolean>() {
                            @Override
                            public void success(Boolean mode) {
                                //更新内存和数据库
                                ContactManager.getInstance().recoveryContactInternal(mContact.contactId);
                                //更新显示列表
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        userList.remove(position);
                                        mUserAdapter.setUserData(userList);
                                        mUserAdapter.notifyDataSetChanged();
                                    }
                                });
                            }

                            @Override
                            public void failed(String errTip, int errCode) {
                                tip(errTip);
                            }
                        }
                );
            }
        }).start();
    }

    @Background
    void moveGroupOutofBlackList(final List<Group> groupList, final int position,
                                 final String figureId,final String groupId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SyncApi.getInstance().moveOutofBlackList(figureId, groupId,
                        mContext, new SyncApi.CallBack<Boolean>() {
                            @Override
                            public void success(Boolean mode) {
                                //更新内存和数据库
                                GroupManager.getInstance().recoveryFromBlackList(GroupDBHandler.getGroupId(groupId, figureId));
                                //更新显示列表
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        groupList.remove(position);
                                        mGroupAdapter.setGroupData(groupList);
                                        mGroupAdapter.notifyDataSetChanged();
                                    }
                                });
                            }

                            @Override
                            public void failed(String errTip, int errCode) {
                                tip(errTip);
                            }
                        }
                );
            }
        }).start();
    }

    @SuppressWarnings("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        XLApplication.isHome = false;
    }
}