package com.xianglin.fellowvillager.app.adapter;
/**
 * 
 * @author Aizaz AZ
 *
 */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.xianglin.fellowvillager.app.chat.AlbumItem;
import com.xianglin.fellowvillager.app.chat.model.AlbumModel;

import java.util.ArrayList;

public class AlbumAdapter extends MBaseAdapter<AlbumModel> {

	public AlbumAdapter(Context context, ArrayList<AlbumModel> models) {
		super(context, models);
	}
	private String  checkedName;
	public void setChecked(String  checkName){
		checkedName = checkName;
	};
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AlbumItem albumItem = null;
		if (convertView == null) {
			albumItem = new AlbumItem(context);
			convertView = albumItem;
		} else
			albumItem = (AlbumItem) convertView;
		if (checkedName.equals(models.get(position).getName()))
			models.get(position).setCheck(true);
		albumItem.update(models.get(position));
		return convertView;
	}

}
