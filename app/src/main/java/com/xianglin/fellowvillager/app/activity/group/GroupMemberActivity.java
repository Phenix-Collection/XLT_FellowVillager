package com.xianglin.fellowvillager.app.activity.group;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.xianglin.appserv.common.service.facade.model.GroupOperationRequest;
import com.xianglin.appserv.common.service.facade.model.UserFigureIdDTO;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.XLApplication;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.chat.ChatMainActivity_;
import com.xianglin.fellowvillager.app.chat.controller.GroupManager;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.GroupDBHandler;
import com.xianglin.fellowvillager.app.db.GroupMemberDBHandler;
import com.xianglin.fellowvillager.app.loader.GroupListLoader;
import com.xianglin.fellowvillager.app.loader.GroupMemberLoader;
import com.xianglin.fellowvillager.app.model.Group;
import com.xianglin.fellowvillager.app.model.GroupMember;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.CharacterParser;
import com.xianglin.fellowvillager.app.utils.FileUtils;
import com.xianglin.fellowvillager.app.utils.ImageUtils;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.pinyin.PingYinUtil;
import com.xianglin.fellowvillager.app.utils.pinyin.PinyinComp;
import com.xianglin.fellowvillager.app.widget.PinyinSideBar;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.mobile.common.logging.LogCatLog;
import com.xianglin.xlappcore.common.service.facade.base.CommonReq;
import com.xianglin.xlappcore.common.service.facade.vo.TeamVo;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 项目名称：乡邻小站
 * 类描述：
 * 创建人：何正纬
 * 创建时间：2015/12/1 16:35
 * 修改人：hezhengwei
 * 修改时间：2015/12/1 16:35
 * 修改备注： 删除 和展示功能
 */
@EActivity(R.layout.activity_group_list_pinyin)
public class GroupMemberActivity extends BaseActivity {

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
    String toGroupId;

    @Extra
    String toGroupName;

    @Extra
    int toDelMemberflag; //默认为0 成员列表展示,/值为1时,删除列表展示;

    public static final int DEL_ACTION = 0x10002;

    private PinyinSideBar sideBar;
    private GroupListAdapter adapter;
    private GroupListAdapter.ViewHolder holder;
    private int lastFirstVisibleItem = -1;
    private CharacterParser characterParser;//汉字转换成拼音的类
    private List<GroupMember> SourceDateList;
    private PinyinComp pinyinComparator;//根据拼音来排列ListView里面的数据类
    private List<GroupMember> mMemberList = new ArrayList<GroupMember>();
    private GroupDBHandler groupDBHandler;
    private List<TeamVo> teamVoList;
    private GroupListLoader mGroupListLoader;
    private List<String> listMemberId;
    private List<GroupMember> mDelMember;
    private GroupMemberLoader mGroupMemberLoader;
    private GroupMemberDBHandler mGroupMemberDBHandler;
    private boolean isfirstLoaded = true;//

    private List<UserFigureIdDTO> userFigureIdDTOList = new ArrayList<UserFigureIdDTO>();

    @AfterInject
    void init() {

        mGroupMemberLoader = new GroupMemberLoader(GroupMemberActivity.this, toGroupId);
        mGroupMemberDBHandler = new GroupMemberDBHandler(GroupMemberActivity.this);
        //没有数据时刷新;
        //  mGroupListLoader.onContentChanged();

        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(0, getIntent().getExtras(), new GroupMembeCallbacks());
    }

    @AfterViews
    void initView() {
        if (toDelMemberflag == 0) {
            topView.setAppTitle(
                    R.string.group_member_list
            );
            topView.setLeftImageResource(R.drawable.icon_back);
            topView.setRightTextViewText("添加");
            topView.setLeftImgOnClickListener();
            topView.getRightTextView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupAddMemberActivity_.intent(GroupMemberActivity.this).
                            toGroupId(toGroupId).toGroupName(toGroupName).
                            addOrJoin(BorrowConstants.CHATTYPE_JOIN).start();
                }
            });
            sortListView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
        } else {
            addTitle();
        }
    }

    private class GroupMembeCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
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

            GroupMemberDBHandler.GroupMemberCursor groupCursor = new GroupMemberDBHandler.GroupMemberCursor(data);
            mMemberList.clear();
            while (data != null && data.moveToNext()) {
                GroupMember mGroupMember = groupCursor.getGroupMember();
                mMemberList.add(mGroupMember);

            }
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
        //删除群成员
        topView.setAppTitle("选择群成员");
//        topView.setLeftImageResource(R.drawable.icon_back);
//        topView.setLeftImgOnClickListener();
        topView.setLeftTextiewText(R.string.cancel);
        topView.setLeftTextOnClickListener();
        topView.setRightTextViewText("确定");
        topView.getRightTextView().setTextColor(getResources().getColor(R.color.black1));
        topView.getRightTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listMemberId = new ArrayList<String>();
                mDelMember = new ArrayList<GroupMember>();
                Iterator<Integer> iterator = adapter.isSelected.keySet().iterator();
                while (iterator.hasNext()) {
                    Integer next = iterator.next();
                    Boolean able = adapter.isSelected.get(next);
                    if (able) {
                        listMemberId.add(SourceDateList.get(next).xluserid + "");

                        UserFigureIdDTO userFigureIdDTO = new UserFigureIdDTO();
                        userFigureIdDTO.setFigureId(SourceDateList.get(next).figureUsersId);
                        userFigureIdDTO.setUserId(SourceDateList.get(next).xluserid);
                        userFigureIdDTOList.add(userFigureIdDTO);

                        mDelMember.add(SourceDateList.get(next));
                    }
                }
                LogCatLog.e(TAG, "111111 kickGroup mDelMember = " + mDelMember);
                LogCatLog.e(TAG, "111111 kickGroup userFigureIdDTOList = " + userFigureIdDTOList);

                /**
                 * 群组踢人
                 */
                if (listMemberId.size() > 0) {

                    Group group =GroupManager.getInstance().getGroup(toGroupId);

                    kickGroup(group.figureId, group.xlGroupID, userFigureIdDTOList);

                    //删除数据
                    mGroupMemberDBHandler.delList(mDelMember); //

                    //发送数据库变化的信号通知loader重新加载
                    XLApplication.getInstance().getContentResolver().
                            notifyChange(GroupMemberDBHandler.SYNC_SIGNAL_URI, null);
                } else {
                    tip(R.string.group_no_delete);
                }
                finish();
            }
        });
    }

    /**
     * @param figureId 操作人的figureId
     * @param groundId 群id
     * @param list     被踢的群id列表
     */
    @Background
    void kickGroup(String figureId, String groundId, List<UserFigureIdDTO> list) {
        GroupOperationRequest groupOperationRequest = new GroupOperationRequest();
        groupOperationRequest.setFigureId(figureId);
        groupOperationRequest.setGroupId(groundId);
        groupOperationRequest.setMembers(list);
        SyncApi.getInstance().kick(groupOperationRequest, GroupMemberActivity.this,
                new SyncApi.CallBack<Boolean>() {
                    @Override
                    public void success(Boolean mode) {
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        tip(errTip);
                    }
                });
    }

    /**
     * 群主踢人;
     */
    @Background
    void kickGroupMember() {

        final CommonReq commonReq = new CommonReq();
        final long xlid = PersonSharePreference.getUserID();
        commonReq.setBody(new HashMap<String, Object>() {
            {
                put("xlid", xlid);
                put("teamId", toGroupId);
                put("operateType", "KICK");
                put("contactXlidList", listMemberId);
            }
        });

        SyncApi.getInstance().teamManage(GroupMemberActivity.this, commonReq, new SyncApi.CallBack<TeamVo>() {

            @Override
            public void success(TeamVo mode) {
                //删除数据
                mGroupMemberDBHandler.delList(mDelMember); //

                //发送数据库变化的信号通知loader重新加载
                XLApplication.getInstance().getContentResolver().notifyChange(GroupMemberDBHandler.SYNC_SIGNAL_URI,
                        null);

                //结束界面
                if (SourceDateList != null) {
                    SourceDateList.clear();
                }
                if (mMemberList != null) {
                    mMemberList.clear();
                }
                if (mDelMember != null) {
                    mDelMember.clear();
                }
                GroupMemberActivity.this.finish();
            }

            @Override
            public void failed(String errMsg, int type) {
                tip(errMsg);
            }
        });

    }

    /**
     * 获取完网络数据后,加载在数据并显示
     */
    private void initViews() {
        characterParser = CharacterParser.getInstance(); // 实例化汉字转拼音类
        pinyinComparator = new PinyinComp();
        sideBar = (PinyinSideBar) findViewById(R.id.sidrbar);
        sideBar.setTextView(dialog);
        LogCatLog.e("Test", "init sideBar");
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



        SourceDateList = filledData(mMemberList); //加载数据
        Collections.sort(SourceDateList, pinyinComparator); // 根据a-z进行排序源数据

        if (toDelMemberflag == 0) {
            topView.setAppTitle(
                    getString(
                            R.string.group_member_list,
                            String.valueOf(SourceDateList.size())
                    )
            );
        }


        char[] alphaList = new PingYinUtil().getFirstSpellGroup(SourceDateList);
        // LogCatLog.e("Test", "alphaList=" + alphaList);
        sideBar.setAlpha(alphaList);
        sideBar.requestLayout();

        if (SourceDateList.size() > 0) {
            if (mMemberList.size() == 1) {//整个列表只有一个人,那么这个人必定是自己;
                topView.getRightTextView().setVisibility(View.GONE);
            }
            adapter = new GroupListAdapter(this, SourceDateList, toDelMemberflag);
            sortListView.setAdapter(adapter);

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
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                    .getLayoutParams();
                            params.topMargin = 0;
                            titleLayout.setLayoutParams(params);
                            title.setText(SourceDateList.get(
                                    getPositionForSection(section)).sortLetters);
                        }
                        if (nextSecPosition == firstVisibleItem + 1) {
                            View childView = view.getChildAt(0);
                            if (childView != null) {
                                int titleHeight = titleLayout.getHeight();
                                int bottom = childView.getBottom();
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
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
    private List<GroupMember> filledData(List<GroupMember> date) {
        List<GroupMember> mSortList = new ArrayList<GroupMember>();
        for (int i = 0; i < date.size(); i++) {

            String pinyin = "";
            String sortString = "";
            String wordStirng = "";
            if (!TextUtils.isEmpty(date.get(i).xlUserName)) {
                // 汉字转换成拼音
                //pinyin = characterParser.getSelling(date.get(i).xlRemarkName);
                pinyin = PingYinUtil.getPingYin(date.get(i).xlUserName);
                sortString = pinyin.substring(0, 1).toUpperCase();
            }

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                wordStirng = sortString.toUpperCase();
            } else {
                wordStirng = "#";
            }
            GroupMember sortModel = new GroupMember.Builder()
                    .file_id(date.get(i).file_id)
                    .xlUserName(date.get(i).xlUserName + "")
                    .xluserid(date.get(i).xluserid + "")
                    .figureId(date.get(i).figureId + "")
                    .figureUsersId(date.get(i).figureUsersId + "")
                    .sortLetters(wordStirng)
                    .xlImgPath(date.get(i).xlImgPath)
                    .xlGroupId(date.get(i).xlGroupId)
                    .isContact(date.get(i).isContact)
                    .isOwner(date.get(i).isOwner)
                    .build();
            mSortList.add(sortModel);
        }
        return mSortList;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return SourceDateList.get(position < SourceDateList.size() ? position : 0).sortLetters.charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < SourceDateList.size(); i++) {
            String sortStr = SourceDateList.get(i).sortLetters;
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 适配器
     */
    public class GroupListAdapter extends BaseAdapter implements SectionIndexer {
        public HashMap<Integer, Boolean> isSelected;
        private List<GroupMember> list = null;
        private Context mContext;
        private int showFlag;

        public GroupListAdapter(Context mContext, List<GroupMember> list, int flag) {
            this.mContext = mContext;
            this.list = list;
            this.showFlag = flag;
            init_1();
        }

        private void init_1() {
            if (isSelected == null) {
                isSelected = new HashMap<Integer, Boolean>();
                for (int i = 0; i < list.size(); i++) {
                    isSelected.put(i, false);
                }
            }
        }

        /**
         * 当ListView数据发生变化时,调用此方法来更新ListView
         *
         * @param list
         */
        public void updateListView(List<GroupMember> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public int getCount() {
            return this.list.size();
        }

        public Object getItem(int position) {
            return list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup arg2) {
            final ViewHolder viewHolder;
            final GroupMember mContent = list.get(position);
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(mContext).inflate(R.layout.item_group_list_pinyin, null);
                viewHolder.linelay = (LinearLayout) view.findViewById(R.id.linelay_check);
                viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
                viewHolder.imgTitle = (ImageView) view.findViewById(R.id.rember_img);
                viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
                viewHolder.isCheckBox = (CheckBox) view.findViewById(R.id.ckx_member_select);
                viewHolder.chk_item = (LinearLayout) view.findViewById(R.id.chk_item);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            if (showFlag == 0) {
                viewHolder.linelay.setVisibility(View.GONE);
            } else {
                viewHolder.linelay.setVisibility(View.VISIBLE);
            }

            if (list.get(position).xluserid.equals(PersonSharePreference.getUserID() + "")) {
                viewHolder.isCheckBox.setBackgroundResource(R.drawable.mul_grey);
            } else if (list.get(position).isOwner.equals("true")) {
                viewHolder.isCheckBox.setBackgroundResource(R.drawable.mul_grey);
            } else {
                viewHolder.isCheckBox.setBackgroundResource(R.drawable.group_multiple);
            }

            viewHolder.isCheckBox.setChecked(isSelected.get(position));

            // 根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);

            // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(section)) {
                viewHolder.tvLetter.setVisibility(View.VISIBLE);
                viewHolder.tvLetter.setText(mContent.sortLetters);

            } else {
                viewHolder.tvLetter.setVisibility(View.GONE);
            }


            //viewHolder.isCheckBox.setChecked(isSelected.get(position));
            //viewHolder.isCheckBox.setTag(position);
            // viewHolder.isCheckBox.setOnClickListener(this);
            viewHolder.tvTitle.setText(this.list.get(position).xlUserName);
            //            Resources res = mContext.getResources();
            //            Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.group_icon);
            //            viewHolder.imgTitle.setImageBitmap(bmp);


            viewHolder.chk_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (toDelMemberflag == 0) {
                        GroupMember mGroupMember = (GroupMember) adapter.getItem(position);
                        if (!mGroupMember.xluserid.equals(PersonSharePreference.getUserID() + "")) { //如果不是群主自己,则进入对话;
                            ChatMainActivity_.intent(GroupMemberActivity.this)
                                    .titleName(mGroupMember.xlUserName)
                                    .toChatId(mGroupMember.figureUsersId)
                                    .chatType(BorrowConstants.CHATTYPE_SINGLE)
                                    .currentFigureId(mGroupMember.figureId)
                                    .toChatXlId(mGroupMember.xluserid)
                                    .headerImgId(mGroupMember.file_id)
                                    .start();

                        }

                        return;
                    }

                    if (!list.get(position).xluserid.equals(PersonSharePreference.getUserID() + "")) {//点击的 item不为自己
                        //   LogCatLog.e("Test", "click userId=" + list.get(position).groupmemberid);
                        {
                            boolean isItemSelected = isSelected.get(position);
                            if (isItemSelected) {
                                isSelected.put(position, false);
                                viewHolder.isCheckBox.setChecked(false);
                            } else {
                                isSelected.put(position, true);
                                viewHolder.isCheckBox.setChecked(true);
                            }
                            //  LogCatLog.e("Test", "item selected=" + isSelected.get(position));
                        }
                        /* add by zx */
                        /**
                         * 未选择成员时，将“确认”置灰且不可点击
                         */
                        Iterator<Integer> iterator = adapter.isSelected.keySet().iterator();
                        List<String> listtest = new ArrayList<String>();
                        while (iterator.hasNext()) {
                            Integer next = iterator.next();
                            Boolean able = adapter.isSelected.get(next);
                            if (able) {
                                listtest.add("test");
                                topView.getRightTextView().setTextColor(getResources().getColor(R.color.white));
                                // LogCatLog.i(TAG, "listtest-----------:" + listtest.size());
                            }
                        }
                        if (listtest.size() > 0) {
                            Resources resource = (Resources) getBaseContext().getResources();
                            ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.head_text_color);
                            if (csl != null) {
                                topView.getRightTextView().setTextColor(csl);
                            }
                            //                        topView.getRightTextView().setTextColor(getResources().getColor
                            // (R.color.white));
                            topView.getRightTextView().setEnabled(true);
                        } else {
                            topView.getRightTextView().setTextColor(getResources().getColor(R.color.black1));
                            topView.getRightTextView().setEnabled(false);
                        }
                    }
                }
            });


            LogCatLog.i("fileName", "---------file_id---------" + this.list.get(position).file_id);
            LogCatLog.i("fileName", "---------xluserid---------" + this.list.get(position).xluserid);
            LogCatLog.i("fileName", "---------xlRemarkName---------" + this.list.get(position).xlUserName);


//            if ((PersonSharePreference.getUserID() + "").equals(list.get(position).xluserid)) {//本人头像
//                viewHolder.imgTitle.setImageBitmap(ImageUtils.decodeThumbnailsBitmap(FileUtils
//                        .IMG_CACHE_HEADIMAGE_PATH + PersonSharePreference.getUserID() + ".webp"));
//            } else if (list.get(position).file_id == null || "null".equals(list.get(position).file_id)) {//文件ID为空设置默认头像
//                viewHolder.imgTitle.setImageResource(R.drawable.head);
//            } else if (ImageUtils.isLocalImg(list.get(position).file_id + "")) {//头像在本地已存在
//                ImageUtils.showLocalImg(mContext, viewHolder.imgTitle, list.get(position).file_id + "");
//            } else {//通过下载显示头像
//                downloadImageHeader((Activity) mContext, viewHolder.imgTitle, position);
//            }
            ImageUtils.showCommonImage((Activity) mContext, viewHolder.imgTitle, FileUtils.IMG_CACHE_HEADIMAGE_PATH,
                    list.get(position).file_id + "", R.drawable.head);
            return view;
        }

        final class ViewHolder {
            TextView tvLetter;
            TextView tvTitle;
            ImageView imgTitle;
            CheckBox isCheckBox;
            LinearLayout linelay;
            LinearLayout chk_item;
        }

        /**
         * 根据ListView的当前位置获取分类的首字母的Char ascii值
         */
        public int getSectionForPosition(int position) {
            return list.get(position).sortLetters.charAt(0);
        }

        /**
         * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
         */
        public int getPositionForSection(int section) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = list.get(i).sortLetters;
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
    protected void onDestroy() {
        super.onDestroy();
        if (SourceDateList != null) {
            SourceDateList.clear();
        }
        if (mMemberList != null) {
            mMemberList.clear();
        }
        if (mDelMember != null) {
            mDelMember.clear();
        }
        GroupMemberActivity.this.finish();
    }
}
