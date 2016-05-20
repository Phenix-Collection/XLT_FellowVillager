package com.xianglin.fellowvillager.app.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.activity.BaseActivity;
import com.xianglin.fellowvillager.app.chat.adpter.MessageChatAdapter;

public class ContextMenu extends BaseActivity {

	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int type = getIntent().getIntExtra("type", -1);
		if (type == MessageChatAdapter.TEXT) {
			setContentView(R.layout.context_menu_for_text);
		} else if (type ==MessageChatAdapter.LOCATION) {
			setContentView(R.layout.context_menu_for_location);
		} else if (type ==MessageChatAdapter.IMAGE) {
			setContentView(R.layout.context_menu_for_image);
		} else if (type ==MessageChatAdapter.VOICE) {
			setContentView(R.layout.context_menu_for_voice);
		} else if (type ==MessageChatAdapter.VIDEO) {
			setContentView(R.layout.context_menu_for_video);
		}

		/*
		 * switch (getIntent().getIntExtra("type", -1)) { case txtValue:
		 * setContentView(R.layout.context_menu_for_text); break; case
		 * EMMessage.Type.LOCATION.ordinal():
		 * setContentView(R.layout.context_menu_for_location); break; case
		 * EMMessage.Type.IMAGE.ordinal():
		 * setContentView(R.layout.context_menu_for_image); break; case
		 * EMMessage.Type.VOICE.ordinal():
		 * setContentView(R.layout.context_menu_for_voice); break; //need to
		 * support netdisk and send netsdk? case
		 * setContentView(R.layout.context_menu_for_netdisk); break; case
		 * setContentView(R.layout.context_menu_for_sent_netdisk); break;
		 * default: break; }
		 */
		position = getIntent().getIntExtra("position", -1);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void copy(View view) {
		setResult(ChatMainActivity.RESULT_CODE_COPY,
				new Intent().putExtra("position", position));
		finish();
	}

	public void delete(View view) {
		setResult(ChatMainActivity.RESULT_CODE_DELETE,
				new Intent().putExtra("position", position));
		finish();
	}

	public void forward(View view) {
		setResult(ChatMainActivity.RESULT_CODE_FORWARD,
				new Intent().putExtra("position", position));
		finish();
	}

	public void open(View v) {
		setResult(ChatMainActivity.RESULT_CODE_OPEN,
				new Intent().putExtra("position", position));
		finish();
	}

	public void download(View v) {
		setResult(ChatMainActivity.RESULT_CODE_DWONLOAD,
				new Intent().putExtra("position", position));
		finish();
	}

	public void toCloud(View v) {
		setResult(ChatMainActivity.RESULT_CODE_TO_CLOUD,
				new Intent().putExtra("position", position));
		finish();
	}

}
