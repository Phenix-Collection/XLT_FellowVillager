/**
 * 乡邻小站
 * Copyright (c) 2011-2015 Xianglin,Inc.All Rights Reserved.
 */
package com.xianglin.fellowvillager.app.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.widget.PinnedSectionListView;

import java.util.List;

/**
 * 联系人adapter
 *
 * @author pengyang
 * @version v 1.0.0 2015/11/30 16:03  XLXZ Exp $
 */
public class ContactSectionAdapter extends XLBaseAdapter<Contact> implements PinnedSectionListView
        .PinnedSectionListAdapter {


    public ContactSectionAdapter(List list, Context context) {
        super(list, context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder1 holder1 = null;

        ViewHolder0 holder0 = null;

        int type = getItemViewType(position);

        if (convertView == null) {
            switch (type) {

                case Contact.SECTION:
                    holder1 = new ViewHolder1();
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.listview_head, parent, false);

                    holder1.tv_section = (TextView) convertView.findViewById( R.id.friends_list_header_text);

                    convertView.setTag(holder1);

                    break;

                case Contact.ITEM:
                    convertView = LayoutInflater.from(mContext).inflate(
                            R.layout.contact_item, parent, false);
                    holder0 = new ViewHolder0();
                    holder0.ivAvatar = (ImageView) convertView.findViewById(
                            R.id.contactitem_avatar_iv);
                    holder0.tvCatalog = (TextView) convertView.findViewById(
                            R.id.contactitem_catalog);
                    holder0.tvNick = (TextView) convertView.findViewById(R.id.contactitem_nick);

                    convertView.setTag(holder0);
                    break;
            }

        } else {
            switch (type) {
                case Contact.SECTION:
                    holder1= (ViewHolder1) convertView.getTag();
                    break;
                case Contact.ITEM:
                    holder0= (ViewHolder0) convertView.getTag();
                    break;
            }
        }

        Contact user = getItem(position);

        switch (type) {
            case Contact.SECTION:
                holder1.tv_section.setText(user.section);
                break;
            case Contact.ITEM:
                holder0. ivAvatar.setImageResource(R.drawable.head);
                holder0.tvNick.setText(user.getUIName());
                break;
        }

        return convertView;
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * @param position
     * @return public static final int ITEM = 0;//内容
     * public static final int SECTION = 1;//标
     */
    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == Contact.SECTION;
    }


    class ViewHolder1 {
        TextView tv_section;
    }

    class ViewHolder0 {
        TextView tvCatalog;
        ImageView ivAvatar;
        TextView tvNick;
    }

}
