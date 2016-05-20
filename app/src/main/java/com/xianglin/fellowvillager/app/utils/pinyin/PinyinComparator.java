package com.xianglin.fellowvillager.app.utils.pinyin;


import com.xianglin.fellowvillager.app.model.Contact;

import java.util.Comparator;

public class PinyinComparator implements Comparator {

	@Override
	public int compare(Object arg0, Object arg1) {
		// 按照名字排序
		Contact user0 = (Contact) arg0;
		Contact user1 = (Contact) arg1;
		String catalog0 = "";
		String catalog1 = "";

		if (user0 != null && user0.getUIName() != null
				&& user0.getUIName().length() > 1)
			catalog0 = PingYinUtil.converterToFirstSpell(user0.getUIName())
					.substring(0, 1).toUpperCase();

		if (user1 != null && user1.getUIName() != null
				&& user1.getUIName().length() > 1)
			catalog1 = PingYinUtil.converterToFirstSpell(user1.getUIName())
					.substring(0, 1).toUpperCase();
		int flag = catalog0.compareTo(catalog1);
		return flag;

	}

}
