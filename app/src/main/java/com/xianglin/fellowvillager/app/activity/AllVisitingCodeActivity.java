package com.xianglin.fellowvillager.app.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.adapter.VisitingCodeAdapter;
import com.xianglin.fellowvillager.app.constants.BorrowConstants;
import com.xianglin.fellowvillager.app.db.CardDBHandler;
import com.xianglin.fellowvillager.app.loader.CardLoader;
import com.xianglin.fellowvillager.app.loader.SQLiteCursorLoader;
import com.xianglin.fellowvillager.app.model.MessageBean;
import com.xianglin.fellowvillager.app.widget.TopView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.Calendar;
import java.util.List;

/**
 列出所有卡片
 */
@EActivity(R.layout.activity_all_visiting_code)
public class AllVisitingCodeActivity extends BaseActivity {

	@ViewById(R.id.topview)
	TopView mTopView;

	@ViewById(R.id.tv_visiting_code_time)
	TextView visiting_code_time_tv;

	@ViewById(R.id.lv_visiting_code)
	ListView visiting_code_lv;

	@ViewById(R.id.view_line)
	View view_line;

//	@Extra
//	String headerImgId;
	@Extra
	String toChatName;
//	@Extra
//	String toChatId;

	CardLoader mCardLoader;
	List<MessageBean> mMessageBeanList;
	private Calendar calendar;
	private String currentMonth;

	@AfterInject
	public void init () {
		calendar = Calendar.getInstance();
		currentMonth = String.valueOf(calendar.get(Calendar.MONTH)+1);
		if(currentMonth.length() == 1){
			currentMonth = "0" + currentMonth;
		}
		mCardLoader = new CardLoader(this, null, BorrowConstants.CHATTYPE_SINGLE, 0, 0, false, false);
		LoaderManager lm = getSupportLoaderManager();
		lm.initLoader(0, getIntent().getExtras(), new CardListCallbacks());
	}

	@AfterViews
	public void initView () {
		mTopView.setAppTitle("卡片");
//		mTopView.setLeftImageResource(R.drawable.icon_back);
//		mTopView.setLeftImgOnClickListener();

		mTopView.setRightTextViewText("取消");
		mTopView.getRightTextView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick (View v) {
				onBackPressed();
			}
		});
	}

	private class CardListCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
		@Override
		public Loader<Cursor> onCreateLoader (int id, Bundle args) {
			return mCardLoader;
		}

		@Override
		public void onLoadFinished (Loader<Cursor> loader, Cursor data) {
			if (data == null) {
				return;
			}

			SQLiteCursorLoader sqLiteCursorLoader= (SQLiteCursorLoader) loader;
			data.unregisterContentObserver(sqLiteCursorLoader.getObserver());

			CardDBHandler.MessageCursor messageCursor = new CardDBHandler.MessageCursor(data);
			mMessageBeanList = messageCursor.getMessageBeanList();
			if (mMessageBeanList == null) {
				return;
			}
			if(mMessageBeanList.size() <= SpecificVisitingCodeActivity.SHOWEDCARDCOUNT){
				view_line.setVisibility(View.VISIBLE);
			}else{
				view_line.setVisibility(View.GONE);
			}
			visiting_code_lv.setAdapter(new VisitingCodeAdapter(AllVisitingCodeActivity.this, mMessageBeanList, toChatName, false));
			visiting_code_lv.setDivider(null);
			visiting_code_lv.setOnScrollListener(new AbsListView.OnScrollListener() {
				@Override
				public void onScrollStateChanged (AbsListView view, int scrollState) {
				}

				@Override
				public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					if (mMessageBeanList.size() > 0) {
						visiting_code_time_tv.setText(showTime(mMessageBeanList.get(firstVisibleItem).msgDate));
					}
				}
			});
			if(mMessageBeanList.size() == 0){
				return;
			}
			visiting_code_lv.setSelection(mMessageBeanList.size() - 1);
		}

		@Override
		public void onLoaderReset (Loader<Cursor> loader) {

		}
	}

	public String showTime(String time){
		String newTime = "";
		if(time == null){
			return newTime;
		}
		if(time.contains("-")){
			String[] strTime = time.split("-");
			newTime = strTime[0] + "年" + strTime[1] + "月";
			if(currentMonth.equals(strTime[1])){
				newTime = "本月";
			}
		}
		return newTime;
	}

	@Override
	public void onBackPressed() {
		finish();
		animTopToBottom();
	}
}
