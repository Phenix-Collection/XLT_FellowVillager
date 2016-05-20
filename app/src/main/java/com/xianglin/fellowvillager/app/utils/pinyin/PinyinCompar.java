package com.xianglin.fellowvillager.app.utils.pinyin;

import com.xianglin.fellowvillager.app.model.Group;

import java.util.Comparator;


public class PinyinCompar implements Comparator<Group> {

	public int compare(Group o1, Group o2) {
		if (o1.sortLetters.equals("@")
				|| o2.sortLetters.equals("#")) {
			return -1;
		} else if (o1.sortLetters.equals("#")
				|| o2.sortLetters.equals("@")) {
			return 1;
		} else {
			return o1.sortLetters.compareTo(o2.sortLetters);
		}
	}

}
