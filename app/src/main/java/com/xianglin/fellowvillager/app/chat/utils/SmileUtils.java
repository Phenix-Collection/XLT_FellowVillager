package com.xianglin.fellowvillager.app.chat.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.xianglin.fellowvillager.app.R;
import com.xianglin.fellowvillager.app.utils.DeviceInfoUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//表情
public class SmileUtils {
	public static final String em_00 = "[微笑]";
	public static final String em_01 = "[撇嘴]";
	public static final String em_02 = "[花痴]";
	public static final String em_03 = "[得意]";
	public static final String em_04 = "[流泪]";
	public static final String em_05 = "[害羞]";
	public static final String em_06 = "[闭嘴]";
	public static final String em_07 = "[睡]";
	public static final String em_08 = "[大哭]";
	public static final String em_09 = "[尴尬]";
	public static final String em_010 = "[发怒]";
	public static final String em_011 = "[调皮]";
	public static final String em_012 = "[呲牙]";
	public static final String em_013 = "[惊讶]";
	public static final String em_014 = "[难过]";
	public static final String em_015 = "[酷]";
	public static final String em_016 = "[抓狂]";
	public static final String em_017 = "[吐]";
	public static final String em_018 = "[偷笑]";
	public static final String em_019 = "[愉快]";
	public static final String em_020 = "[冷汗]";
	public static final String em_021 = "[白眼]";
	public static final String em_022 = "[困]";
	public static final String em_023 = "[流汗]";
	public static final String em_024 = "[憨笑]";
	public static final String em_025 = "[奋斗]";
	public static final String em_026 = "[悠闲]";
	public static final String em_027 = "[咒骂]";
	public static final String em_028 = "[疑问]";
	public static final String em_029 = "[嘘]";
	public static final String em_030 = "[晕]";
	public static final String em_031 = "[衰]";
	public static final String em_032 = "[骷髅]";
	public static final String em_033 = "[敲打]";
	public static final String em_034 = "[再见]";
	public static final String em_035 = "[擦汗]";
	public static final String em_036 = "[抠鼻]";
	public static final String em_037 = "[鼓掌]";
	public static final String em_038 = "[右哼哼]";
	public static final String em_039 = "[左哼哼]";
	public static final String em_040 = "[鄙视]";
	public static final String em_041 = "[委屈]";
	public static final String em_042 = "[快哭了]";
	public static final String em_043 = "[阴险]";
	public static final String em_044 = "[亲亲]";
	public static final String em_045 = "[大牙]";
	public static final String em_046 = "[无奈]";//
	public static final String em_047 = "[怪叔叔]";
	public static final String em_048 = "[嘲笑]";
	public static final String em_049 = "[装酷]";
	public static final String em_050 = "[哭笑]";
	public static final String em_051 = "[叫卖]";
	public static final String em_052 = "[没钱]";
	public static final String em_053 = "[钱钱钱]";
	public static final String em_054 = "[小二]";
	public static final String em_055 = "[多谢]";
	public static final String em_056 = "[算账]";
	public static final String em_057 = "[看我]";//
	public static final String em_058 = "[旁观]";
	public static final String em_059 = "[看书]";
	public static final String em_060 = "[摸头]";
	public static final String em_061 = "[考虑]";
	public static final String em_062 = "[菜刀]";
	public static final String em_063 = "[啤酒]";
	public static final String em_064 = "[咖啡]";
	public static final String em_065 = "[饭]";
	public static final String em_066 = "[猪头]";
	public static final String em_067 = "[元宝]";
	public static final String em_068 = "[合作愉快]";
	public static final String em_069 = "[保佑]";
	public static final String em_070 = "[晚安]";
	public static final String em_071 = "[握手]";
	public static final String em_072 = "[OK]";
	public static final String em_073 = "[NO]";
	public static final String em_074 = "[差劲]";
	public static final String em_075 = "[爱你]";
	public static final String em_076 = "[拳头]";
	public static final String em_077 = "[抱拳]";
	public static final String em_078 = "[勾引]";
	public static final String em_079 = "[礼物]";
	public static final String em_080 = "[便便]";
	public static final String em_081 = "[爱心]";
	public static final String em_082 = "[心碎]";
	public static final String em_083 = "[红唇]";
	public static final String em_084 = "[玫瑰]";
	public static final String em_085 = "[凋谢]";
	public static final String em_086 = "[抱抱]";
	public static final String em_087 = "[瓢虫]";
	public static final String em_088 = "[蛋糕]";
	public static final String em_089 = "[红包]";
	public static final String em_090 = "[兔子]";
	public static final String em_091 = "[性感]";
	public static final String em_092 = "[棒棒糖]";
	public static final String em_093 = "[爆米花]";
	public static final String em_094 = "[火堆]";
	public static final String em_095 = "[草]";
	public static final String em_096 = "[太阳]";
	public static final String em_097 = "[点赞]";
	public static final String em_098 = "[差评]";
	public static final String em_099 = "[胜利]";

	private static final Factory spannableFactory = Spannable.Factory
			.getInstance();

	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	static {
		addPattern(emoticons, em_00, R.drawable.em_00);
		addPattern(emoticons, em_01, R.drawable.em_01);
		addPattern(emoticons, em_02, R.drawable.em_02);
		addPattern(emoticons, em_03, R.drawable.em_03);
		addPattern(emoticons, em_04, R.drawable.em_04);
		addPattern(emoticons, em_05, R.drawable.em_05);
		addPattern(emoticons, em_06, R.drawable.em_06);
		addPattern(emoticons, em_07, R.drawable.em_07);
		addPattern(emoticons, em_08, R.drawable.em_08);
		addPattern(emoticons, em_09, R.drawable.em_09);
		addPattern(emoticons, em_010, R.drawable.em_010);
		addPattern(emoticons, em_011, R.drawable.em_011);
		addPattern(emoticons, em_012, R.drawable.em_012);
		addPattern(emoticons, em_013, R.drawable.em_013);
		addPattern(emoticons, em_014, R.drawable.em_014);
		addPattern(emoticons, em_015, R.drawable.em_015);
		addPattern(emoticons, em_016, R.drawable.em_016);
		addPattern(emoticons, em_017, R.drawable.em_017);
		addPattern(emoticons, em_018, R.drawable.em_018);
		addPattern(emoticons, em_019, R.drawable.em_019);
		addPattern(emoticons, em_020, R.drawable.em_020);
		addPattern(emoticons, em_021, R.drawable.em_021);
		addPattern(emoticons, em_022, R.drawable.em_022);
		addPattern(emoticons, em_023, R.drawable.em_023);
		addPattern(emoticons, em_024, R.drawable.em_024);
		addPattern(emoticons, em_025, R.drawable.em_025);
		addPattern(emoticons, em_026, R.drawable.em_026);
		addPattern(emoticons, em_027, R.drawable.em_027);
		addPattern(emoticons, em_028, R.drawable.em_028);
		addPattern(emoticons, em_029, R.drawable.em_029);
		addPattern(emoticons, em_030, R.drawable.em_030);
		addPattern(emoticons, em_031, R.drawable.em_031);
		addPattern(emoticons, em_032, R.drawable.em_032);
		addPattern(emoticons, em_033, R.drawable.em_033);
		addPattern(emoticons, em_034, R.drawable.em_034);
		addPattern(emoticons, em_035, R.drawable.em_035);
		addPattern(emoticons, em_036, R.drawable.em_036);
		addPattern(emoticons, em_012, R.drawable.em_012);
		addPattern(emoticons, em_013, R.drawable.em_013);
		addPattern(emoticons, em_014, R.drawable.em_014);
		addPattern(emoticons, em_015, R.drawable.em_015);
		addPattern(emoticons, em_016, R.drawable.em_016);
		addPattern(emoticons, em_017, R.drawable.em_017);
		addPattern(emoticons, em_018, R.drawable.em_018);
		addPattern(emoticons, em_019, R.drawable.em_019);
		addPattern(emoticons, em_020, R.drawable.em_020);
		addPattern(emoticons, em_021, R.drawable.em_021);
		addPattern(emoticons, em_022, R.drawable.em_022);
		addPattern(emoticons, em_023, R.drawable.em_023);
		addPattern(emoticons, em_024, R.drawable.em_024);
		addPattern(emoticons, em_025, R.drawable.em_025);
		addPattern(emoticons, em_026, R.drawable.em_026);
		addPattern(emoticons, em_027, R.drawable.em_027);
		addPattern(emoticons, em_028, R.drawable.em_028);
		addPattern(emoticons, em_029, R.drawable.em_029);
		addPattern(emoticons, em_030, R.drawable.em_030);
		addPattern(emoticons, em_031, R.drawable.em_031);
		addPattern(emoticons, em_032, R.drawable.em_032);
		addPattern(emoticons, em_033, R.drawable.em_033);
		addPattern(emoticons, em_034, R.drawable.em_034);
		addPattern(emoticons, em_035, R.drawable.em_035);
		addPattern(emoticons, em_036, R.drawable.em_036);
		addPattern(emoticons, em_037, R.drawable.em_037);
		addPattern(emoticons, em_038, R.drawable.em_038);
		addPattern(emoticons, em_039, R.drawable.em_039);
		addPattern(emoticons, em_040, R.drawable.em_040);
		addPattern(emoticons, em_041, R.drawable.em_041);
		addPattern(emoticons, em_042, R.drawable.em_042);
		addPattern(emoticons, em_043, R.drawable.em_043);
		addPattern(emoticons, em_044, R.drawable.em_044);
		addPattern(emoticons, em_045, R.drawable.em_045);
		addPattern(emoticons, em_046, R.drawable.em_046);
		addPattern(emoticons, em_047, R.drawable.em_047);
		addPattern(emoticons, em_048, R.drawable.em_048);
		addPattern(emoticons, em_049, R.drawable.em_049);
		addPattern(emoticons, em_050, R.drawable.em_050);
		addPattern(emoticons, em_051, R.drawable.em_051);
		addPattern(emoticons, em_052, R.drawable.em_052);
		addPattern(emoticons, em_053, R.drawable.em_053);
		addPattern(emoticons, em_054, R.drawable.em_054);
		addPattern(emoticons, em_055, R.drawable.em_055);
		addPattern(emoticons, em_056, R.drawable.em_056);
		addPattern(emoticons, em_057, R.drawable.em_057);
		addPattern(emoticons, em_058, R.drawable.em_058);
		addPattern(emoticons, em_059, R.drawable.em_059);
		addPattern(emoticons, em_060, R.drawable.em_060);
		addPattern(emoticons, em_061, R.drawable.em_061);
		addPattern(emoticons, em_062, R.drawable.em_062);
		addPattern(emoticons, em_063, R.drawable.em_063);
		addPattern(emoticons, em_064, R.drawable.em_064);
		addPattern(emoticons, em_065, R.drawable.em_065);
		addPattern(emoticons, em_066, R.drawable.em_066);
		addPattern(emoticons, em_067, R.drawable.em_067);
		addPattern(emoticons, em_068, R.drawable.em_068);
		addPattern(emoticons, em_069, R.drawable.em_069);
		addPattern(emoticons, em_070, R.drawable.em_070);
		addPattern(emoticons, em_071, R.drawable.em_071);
		addPattern(emoticons, em_072, R.drawable.em_072);
		addPattern(emoticons, em_073, R.drawable.em_073);
		addPattern(emoticons, em_074, R.drawable.em_074);
		addPattern(emoticons, em_075, R.drawable.em_075);
		addPattern(emoticons, em_076, R.drawable.em_076);
		addPattern(emoticons, em_077, R.drawable.em_077);
		addPattern(emoticons, em_078, R.drawable.em_078);
		addPattern(emoticons, em_079, R.drawable.em_079);
		addPattern(emoticons, em_080, R.drawable.em_080);
		addPattern(emoticons, em_081, R.drawable.em_081);
		addPattern(emoticons, em_082, R.drawable.em_082);
		addPattern(emoticons, em_083, R.drawable.em_083);
		addPattern(emoticons, em_084, R.drawable.em_084);
		addPattern(emoticons, em_085, R.drawable.em_085);
		addPattern(emoticons, em_086, R.drawable.em_086);
		addPattern(emoticons, em_087, R.drawable.em_087);
		addPattern(emoticons, em_088, R.drawable.em_088);
		addPattern(emoticons, em_089, R.drawable.em_089);
		addPattern(emoticons, em_090, R.drawable.em_090);
		addPattern(emoticons, em_091, R.drawable.em_091);
		addPattern(emoticons, em_092, R.drawable.em_092);
		addPattern(emoticons, em_093, R.drawable.em_093);
		addPattern(emoticons, em_094, R.drawable.em_094);
		addPattern(emoticons, em_095, R.drawable.em_095);
		addPattern(emoticons, em_096, R.drawable.em_096);
		addPattern(emoticons, em_097, R.drawable.em_097);
		addPattern(emoticons, em_098, R.drawable.em_098);
		addPattern(emoticons, em_099, R.drawable.em_099);

	}

	private static void addPattern(Map<Pattern, Integer> map, String smile,
			int resource) {
		map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	/**
	 * replace existing spannable with smiles
	 * 
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable, int imgSize) {
		imgSize = DeviceInfoUtil.dip2px(imgSize);
		boolean hasChanges = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
			Matcher matcher = entry.getKey().matcher(spannable);
			while (matcher.find()) {
				boolean set = true;
				for (ImageSpan span : spannable.getSpans(matcher.start(),
						matcher.end(), ImageSpan.class))
					if (spannable.getSpanStart(span) >= matcher.start()
							&& spannable.getSpanEnd(span) <= matcher.end())
						spannable.removeSpan(span);
					else {
						set = false;
						break;
					}
				if (set) {
					hasChanges = true;
					Drawable drawable = context.getResources().getDrawable(
							entry.getValue());
					if (drawable != null) {
						drawable.setBounds(0, 0, imgSize, imgSize);// 这里设置图片的大小
						ImageSpan imageSpan = new ImageSpan(drawable,
								ImageSpan.ALIGN_BOTTOM);
						spannable.setSpan(imageSpan, matcher.start(),
								matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
			}
		}
		return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text, int imgSize) {
		Spannable spannable = spannableFactory.newSpannable(text);
		addSmiles(context, spannable, imgSize);
		return spannable;
	}

	public static boolean containsKey(String key) {
		boolean b = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
			Matcher matcher = entry.getKey().matcher(key);
			if (matcher.find()) {
				b = true;
				break;
			}
		}

		return b;
	}

}
