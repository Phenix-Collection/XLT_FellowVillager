package com.xianglin.fellowvillager.app.chat.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;

import com.xianglin.fellowvillager.app.chat.model.AlbumModel;
import com.xianglin.fellowvillager.app.chat.model.PhotoModel;
import com.xianglin.mobile.common.logging.LogCatLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumController {

	private ContentResolver resolver;

	public AlbumController(Context context) {
		resolver = context.getContentResolver();
	}

	/** 获取最近照片列表 */
	public List<PhotoModel> getCurrent() {
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.DATA,
				ImageColumns.DATE_ADDED, ImageColumns.SIZE }, null, null, ImageColumns.DATE_ADDED);
		if (cursor == null || !cursor.moveToNext())
			return new ArrayList<PhotoModel>();
		List<PhotoModel> photos = new ArrayList<PhotoModel>();
		cursor.moveToLast();
		do {
			if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > 1024 * 10) {
				PhotoModel photoModel = new PhotoModel();
				photoModel.setOriginalPath(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
				photos.add(photoModel);
			}
		} while (cursor.moveToPrevious());

		colse(cursor);
		return photos;
	}

	/** 获取所有相册列表 */
	public List<AlbumModel> getAlbums() {
		List<AlbumModel> albums = new ArrayList<AlbumModel>();
		Map<String, AlbumModel> map = new HashMap<String, AlbumModel>();
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.DATA,
				ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.SIZE }, null, null, null);
		if (cursor == null || !cursor.moveToNext())
			return new ArrayList<AlbumModel>();
		cursor.moveToLast();
		AlbumModel current = new AlbumModel("最近照片", 0, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)), false); // "最近照片"相册
		albums.add(current);
		do {
			if (cursor.getInt(cursor.getColumnIndex(ImageColumns.SIZE)) < 1024 * 10)
				continue;

			current.increaseCount();
			String name = cursor.getString(cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME));
			if (map.keySet().contains(name))
				map.get(name).increaseCount();
			else {
				AlbumModel album = new AlbumModel(name, 1, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
				map.put(name, album);
				albums.add(album);
			}
		} while (cursor.moveToPrevious());

		colse(cursor);
		return albums;
	}

	/** 获取对应相册下的照片 */
	public List<PhotoModel> getAlbum(String name) {
		Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.BUCKET_DISPLAY_NAME,
				ImageColumns.DATA, ImageColumns.DATE_ADDED, ImageColumns.SIZE }, "bucket_display_name = ?",
				new String[] { name }, ImageColumns.DATE_ADDED);
		if (cursor == null || !cursor.moveToNext())
			return new ArrayList<PhotoModel>();
		List<PhotoModel> photos = new ArrayList<PhotoModel>();
		cursor.moveToLast();
		do {
			if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > 1024 * 10) {
				PhotoModel photoModel = new PhotoModel();
				photoModel.setOriginalPath(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
				photos.add(photoModel);
			}
		} while (cursor.moveToPrevious());

		colse(cursor);
		return photos;
	}



	/**
	 * 关闭cursor
	 *
	 * @param cursor
	 * @return true success false error
	 */
	public boolean colse(Cursor cursor) {
		try {
			if (cursor != null) {
				cursor.close();
				return true;
			}
		} catch (Exception e) {
			LogCatLog.e("AlbumController", "关闭cursor失败 错误信息" + e.getLocalizedMessage());
		}
		return false;

	}
}
