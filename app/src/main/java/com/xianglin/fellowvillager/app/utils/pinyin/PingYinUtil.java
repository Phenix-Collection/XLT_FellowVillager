package com.xianglin.fellowvillager.app.utils.pinyin;

import android.text.TextUtils;

import com.xianglin.fellowvillager.app.model.Contact;
import com.xianglin.fellowvillager.app.model.GroupMember;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PingYinUtil {
	/**
	 * 将字符串中的中文转化为拼音,其他字符不变
	 * 
	 * @param inputString
	 * @return
	 */
	public static String getPingYin(String inputString) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);

		char[] input = inputString.trim().toCharArray();
		String output = "";

		try {
			for (int i = 0; i < input.length; i++) {
				if (java.lang.Character.toString(input[i]).matches(
						"[\\u4E00-\\u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							input[i], format);
					if(temp!=null) {
						output += temp[0];
					}else{
						output += java.lang.Character.toString(input[i]);
					}
				} else
					output += java.lang.Character.toString(input[i]);
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return output.toUpperCase();
	}

	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 * 
	 * @param chines
	 *            汉字
	 * @return 拼音
	 */
	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
                    String[] foo = PinyinHelper.toHanyuPinyinStringArray(
                            nameChar[i], defaultFormat);
                    if (foo != null && foo.length != 0) {

                        pinyinName += foo[0].charAt(0);
                    }
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}

		if(TextUtils.isEmpty(pinyinName)){
			pinyinName="#";
		}
		return pinyinName;
	}
	public   static   String StringFilter(String   str)   throws PatternSyntaxException {
		// 只允许字母和数字
		// String   regEx  =  "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p   =   Pattern.compile(regEx);
		Matcher m   =   p.matcher(str);
		return   m.replaceAll("").trim();
	}
	/**
	 * 获取 字符集
	 * @param userList
	 * @return
	 */
	public char[]  getFirstSpell(List<Contact> userList){
		List<String> firstSpellList = new ArrayList<String>();
		for (Contact user :userList){
			if(user.getUIName()==null){
				firstSpellList.add("#");
			}else{
			String foo=	converterToFirstSpell(user.getUIName().toUpperCase().substring(0, 1));
				if("".equals(foo)){
					firstSpellList.add("#");
				}else{
					firstSpellList.add(foo);
				}

			}
		}
		List<String> newfirstSpellList = removeDuplicate(firstSpellList);
        for(int i=0;i<newfirstSpellList.size();i++){
			char alpha=newfirstSpellList.get(i).charAt(0);
			if((alpha>=65&&alpha<=90)||(alpha>=97&&alpha<=122)){

			}else{
				newfirstSpellList.remove(i);
				i--;
			}
		}
		StringBuffer buffer = new StringBuffer();
		Collections.sort(newfirstSpellList);
		buffer.append("#");
		for (int i = 0 ; i < newfirstSpellList.size() ;i++){
			String  s = newfirstSpellList.get(i).toUpperCase();
			buffer.append(s);
		}
		return buffer.toString().toCharArray();
	}

	public char[]  getFirstSpellGroup(List<GroupMember> userList){
		List<String> firstSpellList = new ArrayList<String>();
		for (GroupMember user :userList){
			if(user.xlRemarkName==null){
				firstSpellList.add("#");
			}else{
				firstSpellList.add(converterToFirstSpell(user.xlRemarkName.toUpperCase().substring(0, 1)));
			}
		}
		List<String> newfirstSpellList = removeDuplicate(firstSpellList);
		for(int i=0;i<newfirstSpellList.size();i++){
			char alpha=newfirstSpellList.get(i).charAt(0);
			if((alpha>=65&&alpha<=90)||(alpha>=97&&alpha<=122)){

			}else{
				newfirstSpellList.remove(i);
				i--;
			}
		}
		StringBuffer buffer = new StringBuffer();
		Collections.sort(newfirstSpellList);
		buffer.append("#");
		for (int i = 0 ; i < newfirstSpellList.size() ;i++){
			String  s = newfirstSpellList.get(i).toUpperCase();
			buffer.append(s);
		}
		return buffer.toString().toCharArray();
	}

	public static String getAlpha(String pinying) {
		String catalog = "#";
		try {// 首子母
			catalog = pinying.substring(0, 1);
			char alpha = catalog.charAt(0);
			if ((alpha >= 65 && alpha <= 90) || (alpha >= 97 && alpha <= 122)) {
				catalog = catalog.toUpperCase();
			} else {
				catalog = "#";
			}
		} catch (Exception e) {
			catalog = "#";
		}
		return catalog;
	}

	public  static  String  getSection(String UIName){

		String current_section="#";

		String pinying =PingYinUtil.getPingYin(UIName);

		if (TextUtils.isEmpty(pinying) ||
				PingYinUtil.getAlpha(pinying).equals("#")) {
			current_section = "#";
		} else {
			current_section = pinying.substring(0, 1).toUpperCase();
		}
		return current_section;
	}

	/**
	 * 数据去重
	 * @param list
	 * @return
	 */
	public   static   List  removeDuplicate(List list)  {
		HashSet h  =   new HashSet(list);
		list.clear();
		list.addAll(h);
		return list;
	}


}
