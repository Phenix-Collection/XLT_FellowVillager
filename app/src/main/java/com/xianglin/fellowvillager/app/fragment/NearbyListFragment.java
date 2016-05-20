package com.xianglin.fellowvillager.app.fragment;


import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.xianglin.appserv.common.service.facade.model.LocationInfoDTO;
import com.xianglin.appserv.common.service.facade.model.UserFigureDTO;
import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.adapter.NearbyListAdapter;
import com.xianglin.fellowvillager.app.rpc.remote.SyncApi;
import com.xianglin.fellowvillager.app.utils.PersonSharePreference;
import com.xianglin.fellowvillager.app.utils.ToastUtils;
import com.xianglin.fellowvillager.app.utils.Utils;
import com.xianglin.fellowvillager.app.widget.WaterDropListView;
import com.xianglin.fellowvillager.app.widget.dialog.LoadingDialog;
import com.xianglin.mobile.common.info.DeviceInfo;
import com.xianglin.mobile.common.lbs.v2.LBSLocationManagerProxy;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 * 以列表的方式显示附近的人
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/18 13:48  XLXZ Exp $
 *          修改者：王力伟
 *          修改时间：2016.3.1
 */

@EFragment(R.layout.fragment_nearbylist)
public class NearbyListFragment extends BaseFragment implements AbsListView
        .OnScrollListener, LBSLocationManagerProxy.LocationListener, WaterDropListView.IWaterDropListViewListener {

    private String XLID = PersonSharePreference.getUserID() + "";
    private List<UserFigureDTO> mData = new ArrayList<>();
    private List<UserFigureDTO> initdate = new ArrayList<>();
    // 保存附近的人
    private List<UserFigureDTO> mList_UserFigureDTO;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数
    private NearbyListAdapter nearbyListAdapter;
    private LoadingDialog mLoadingDialog;

    @ViewById(R.id.lv_nearby)
    WaterDropListView mLVNearby;

    @ViewById(R.id.txt_nochar_tip)
    TextView mNoDataTip;

    @ViewById(R.id.rl_error_item)
    View mViewNetError;

    double longitude = DeviceInfo.getInstance().getLongitudeDoube();
    double latitude = DeviceInfo.getInstance().getLatitudeDoube();

    @AfterViews
    void init() {

        mLVNearby.setPullRefreshEnable(false);
        mLVNearby.setPullLoadEnable(false);
        mLVNearby.setWaterDropListViewListener(this);
        mLVNearby.setOnScrollListener(this);

        mLoadingDialog = new LoadingDialog(mContext);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.setCanceledOnTouchOutside(true);



        if (!Utils.isValidLatAndLon(longitude, latitude)) {
            //如果出错就显示 定位错误信息,同时两秒定位一次,
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mLoadingDialog.dismiss();
                    mNoDataTip.setText(LBSLocationManagerProxy.errorInfo);
                    mNoDataTip.setVisibility(View.VISIBLE);
                    LBSLocationManagerProxy.getInstance().setUIListener(NearbyListFragment.this);
                }
            });

        } else {
            mNoDataTip.setVisibility(View.GONE);
            getData(true);
        }
    }

    @SuppressWarnings("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mLVNearby.stopRefresh();
                    mLVNearby.setPullLoadEnable(true);
                    break;
                case 2:
                    mLVNearby.stopLoadMore();
                    break;
                case 3:
                    mLoadingDialog.show();
                    break;
                case 4:
                    mLoadingDialog.dismiss();
                    Object obj = msg.obj;
                    if (obj != null && mData.size() == 0) {
                        if (Integer.parseInt(obj.toString()) == 0) {
                            mNoDataTip.setText("数据加载失败，请点击重试！");
                        } else if (Integer.parseInt(obj.toString()) == 1) {
                            mNoDataTip.setText("网络异常，请点击重试！");
                        }
                        mNoDataTip.setVisibility(View.VISIBLE);
                    }
                    break;
                case 5:
                    updateUI(mList_UserFigureDTO);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Background
    /**
     * 附近联系人数据获取
     *
     * @param loadingFlag
     * */
    void getData(final boolean loadingFlag) {
        if (loadingFlag) {
            mHandler.sendEmptyMessage(3);
        }

        LocationInfoDTO lidto = new LocationInfoDTO();
        lidto.setPosition(null);
        lidto.setLongitude(longitude);
        lidto.setLatitude(latitude);
        SyncApi.getInstance().findNearbyUsers(lidto,
                getActivity(), new SyncApi.CallBack() {
                    @Override
                    public void success(Object mode) {
                        if (mode == null) {
                            Message msg = new Message();
                            msg.what = 4;
                            msg.obj = 0;
                            mHandler.sendMessage(msg);
                        } else {
                            mList_UserFigureDTO = (List<UserFigureDTO>) mode;
                            mHandler.sendEmptyMessage(5);
                        }
                    }

                    @Override
                    public void failed(String errTip, int errCode) {
                        Message msg = new Message();
                        msg.what = 4;
                        msg.obj = 1;
                        mHandler.sendMessage(msg);
                    }
                });


    }


    /**
     * 刷新UI
     *
     * @param data
     */
    @UiThread
    void updateUI(List<UserFigureDTO> data) {
        if (data == null) {
            return;
        }
        mLoadingDialog.dismiss();
        mData.addAll(data);
        if (mData.size() > 0) {
            mNoDataTip.setVisibility(View.GONE);
            if (mData.size() >= 20) {
                mLVNearby.setPullLoadEnable(true);//大于20条显示加载更多
                for (int i = 0; i < 20; i++) {
                    initdate.add(data.get(i));
                    //mLVNearbyClicked(data.get(i));
                }
            } else {
                for (int i = 0; i < mData.size(); i++) {
                    initdate.add(data.get(i));
                    //mLVNearbyClicked(data.get(i));
                }
            }
            if (nearbyListAdapter == null) {
                nearbyListAdapter = new NearbyListAdapter(initdate, mContext);
                mLVNearby.setAdapter(nearbyListAdapter);
            } else {
                nearbyListAdapter.setData(initdate);
                nearbyListAdapter.notifyDataSetChanged();
            }


        } else {
            mNoDataTip.setText("暂无附近联系人");
            mNoDataTip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 没有数据时重新刷新
     */
    @Click(R.id.txt_nochar_tip)
    void mTVNoDataClick() {
        getData(true);
    }


    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        mLVNearby.setPullLoadEnable(false);
        mData.clear();
        initdate.clear();
        getData(false);
        mHandler.sendEmptyMessageDelayed(1, 1000);
    }

    /**
     * 上拉加载
     */
    @Override
    public void onLoadMore() {
        loadData();
        nearbyListAdapter.notifyDataSetChanged(); //数据集变化后,通知adapter
        mLVNearby.setSelection(visibleLastIndex - visibleItemCount + 1); //设置选中项
        mHandler.sendEmptyMessageDelayed(2, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 加载数据
     */
    private void loadData() {
        int count = nearbyListAdapter.getCount();
        if (count == mData.size()) {
            ToastUtils.toastForShort(mContext, "已到最后一条");
            mLVNearby.setPullLoadEnable(false);
        } else if (mData.size() % 20 == 0 && count < mData.size()) {
            for (int i = count; i < count + 20; i++) {
                initdate.add(mData.get(i));
            }
        } else if (mData.size() % 20 != 0 && count < mData.size() - 20) {
            for (int i = count; i < count + 20; i++) {
                initdate.add(mData.get(i));
            }
        } else if (mData.size() % 20 != 0 && count < mData.size()) {
            for (int i = count; i < mData.size(); i++) {
                initdate.add(mData.get(i));
            }
        }
        if (nearbyListAdapter == null) {
            nearbyListAdapter = new NearbyListAdapter(initdate, mContext);
            mLVNearby.setAdapter(nearbyListAdapter);
        } else {
            nearbyListAdapter.setData(initdate);
            nearbyListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 滑动时被调用
     *
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
    }

    /**
     * 滑动状态被改变时调用
     *
     * @param view
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int itemsLastIndex = nearbyListAdapter.getCount() - 1;    //数据集最后一项的索引
        int lastIndex = itemsLastIndex + 1;             //加上底部的loadMoreView项
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
            //如果是自动加载,可以在这里放置异步加载数据的代码
        }
    }

    @Override
    public void onLocationSuccess(AMapLocation location) {
        //定位成功回调

        longitude = location.getLongitude();
        latitude = location.getLatitude();

        if (Utils.isValidLatAndLon(longitude, latitude)) {
            getData(true);
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        LBSLocationManagerProxy.getInstance().setUIListener(NearbyListFragment.this);
    }
}
