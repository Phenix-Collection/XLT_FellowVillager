package com.xianglin.fellowvillager.app.chat;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.adapter.CommonAdapter;
import com.xianglin.fellowvillager.app.adapter.ViewHolder;
import com.xianglin.fellowvillager.app.model.SecretBean;
import com.xianglin.fellowvillager.app.utils.DataDealUtil;
import com.xianglin.fellowvillager.app.widget.TopView;
import com.xianglin.mobile.common.logging.LogCatLog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author chengshengli
 * @version v 1.0.0 2016/3/29 15:29 XLXZ Exp $
 */
@EActivity(R.layout.activity_chat_secret_set)
public class ChatSecretSetActivity extends BaseActivity {

    @ViewById(R.id.top_bar)
    TopView topview;

    @ViewById(R.id.list_secret_set)
    ListView list_secret_set;

    @Extra
    String tochatId;
    @Extra
    String currentFigureId;

    CommonAdapter<SecretBean> adapter;
    List<SecretBean> secretList = new ArrayList<SecretBean>();
    String[] time_list;
    @AfterViews
    void initView() {
        topview.setAppTitle("私密聊天设置");
        topview.setLeftImageResource(R.drawable.icon_back);
        topview.setLeftImgOnClickListener();
        time_list=DataDealUtil.time_list;
        for(int i=0;i<time_list.length;i++){
            SecretBean bean=new SecretBean(time_list[i],false);
            secretList.add(bean);
        }

        adapter=new CommonAdapter<SecretBean>(context,secretList,R.layout.item_secret_list) {

            @Override
            public void convert(ViewHolder helper, int position) {
                helper.setText(R.id.secret_time, secretList.get(position).getTime());
                LogCatLog.e("Test","isSelected="+secretList.get(position).isSelected());
                if(secretList.get(position).isSelected()){
                    helper.setImageResource(R.id.secret_select,R.drawable.sex_select_icon);
                }else{
                    helper.setImageBitmap(R.id.secret_select,null);
                }
            }
        };
        list_secret_set.setAdapter(adapter);
        list_secret_set.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectTime(position);
            }
        });

        int index=DataDealUtil.getSecretIndex(currentFigureId,tochatId);
        if(index>=0)
            selectTime(index);
    }

    void selectTime(int position){
        for(int i=0;i<secretList.size();i++){
            secretList.get(i).setIsSelected(false);
        }
        secretList.get(position).setIsSelected(true);
        adapter.notifyDataSetChanged();
        DataDealUtil.setSecretTime(currentFigureId,tochatId,position);
    }



}
