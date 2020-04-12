package com.emotibot.cmiparser.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串预处理模块，为分析器TimeNormalizer提供相应的字符串预处理服务
 * 
 * @author 曹零07300720158
 *
 */
public class PreHandlingUnit {

	/**
	 * 该方法删除一字符串中所有匹配某一规则字串
	 * 可用于清理一个字符串中的空白符和语气助词
	 * 
	 * @param target 待处理字符串
	 * @param rules 删除规则
	 * @return 清理工作完成后的字符串
	 */
	public static String delKeyword(String target, String rules){
		Pattern p = Pattern.compile(rules); 
		Matcher m = p.matcher(target); 
		StringBuffer sb = new StringBuffer(); 
		boolean result = m.find(); 
		while(result) { 
			m.appendReplacement(sb, ""); 
			result = m.find(); 
		}
		m.appendTail(sb);
		String s = sb.toString();
		//System.out.println("字符串："+target+" 的处理后字符串为：" +sb);
		return s;
	}

	/**
	 * 该方法会让重复的，相邻的字符串变成唯一一个字符串
	 *
	 * @param target 待处理字符串
	 * @param unique_map 删除规则
	 * @return 清理工作完成后的字符串
	 */
	public static String uniqueSameword(String target, HashMap<String, String> unique_map ){
		for(String rules : unique_map.keySet()) {
			Pattern p = Pattern.compile(rules);
			Matcher m = p.matcher(target);
			boolean result1 = m.find();
			int index = result1 ? m.start() : 0;

			Pattern p2 = Pattern.compile(unique_map.get(rules));
			Matcher m2 = p2.matcher(target.substring(index));
			boolean result2 = m2.find();

			if(result1 && result2){
				target = target.replace(m.group(), m2.group());
				System.out.println( String.format("[uniqueSameword] replace %s to %s", m.group(), m2.group()));
			}
		}
		return target;
	}
	
	/**
	 * 该方法可以将字符串中所有的用汉字表示的数字转化为用阿拉伯数字表示的数字
	 * 如"这里有一千两百个人，六百零五个来自中国"可以转化为
	 * "这里有1200个人，605个来自中国"
	 * 此外添加支持了部分不规则表达方法
	 * 如两万零六百五可转化为20650
	 * 两百一十四和两百十四都可以转化为214
	 * 一六零加一五八可以转化为160+158
	 * 该方法目前支持的正确转化范围是0-99999999
	 * 该功能模块具有良好的复用性
	 * 
	 * @param target 待转化的字符串
	 * @return 转化完毕后的字符串
	 */
	public static String numberTranslator(String target){
		Pattern p = Pattern.compile("([一二两三四五六七八九])([一二两三四五六七八九])([千百十]?万|千|百|十)");
		Matcher m = p.matcher(target);
		if(m.find()){
			String unit = m.group(m.groupCount())+"或";
			target = target.replace(m.group(1), m.group(1)+unit);
		}

		p = Pattern.compile("[一二两三四五六七八九123456789]万[一二两三四五六七八九123456789](?!(千|百|十))");
		m = p.matcher(target);
		StringBuffer sb = new StringBuffer(); 
		boolean result = m.find(); 
		while(result) { 
			String group = m.group();
			String[] s = group.split("万");
			int num = 0;
			if(s.length == 2){
				num += wordToNumber(s[0])*10000 + wordToNumber(s[1])*1000;
			}
			m.appendReplacement(sb, Integer.toString(num)); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		
		p = Pattern.compile("[一二两三四五六七八九123456789]千[一二两三四五六七八九123456789](?!(百|十))"); 
		m = p.matcher(target); 
		sb = new StringBuffer(); 
		result = m.find(); 
		while(result) { 
			String group = m.group();
			String[] s = group.split("千");
			int num = 0;
			if(s.length == 2){
				num += wordToNumber(s[0])*1000 + wordToNumber(s[1])*100;
			}
			m.appendReplacement(sb, Integer.toString(num)); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		
		p = Pattern.compile("[一二两三四五六七八九123456789]百[一二两三四五六七八九123456789](?!十)"); 
		m = p.matcher(target); 
		sb = new StringBuffer(); 
		result = m.find(); 
		while(result) { 
			String group = m.group();
			String[] s = group.split("百");
			int num = 0;
			if(s.length == 2){
				num += wordToNumber(s[0])*100 + wordToNumber(s[1])*10;
			}
			m.appendReplacement(sb, Integer.toString(num)); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();

		//十三十四 -> 十三，十四
		p = Pattern.compile("([一二三]?十[一二两三四五六七八九]?)");
		StringBuffer dest_str = new StringBuffer();
		Pattern _p = Pattern.compile("([一二三]?十[一二两三四五六七八九]?){2,}");
		Matcher _m = _p.matcher(target);

		if(_m.find()) {
			int index = _m.end();
			String origin_str = _m.group();
			m = p.matcher(origin_str);
			result = m.find();
			while(result) {
				m.appendReplacement(dest_str, m.group()+",");
				result = m.find();
			}

			int index_rear = index == target.length() ? index : index + 1;
			if(!target.substring(index, index_rear).matches("[零一二两三四五六七八九]")) {
				dest_str.delete(dest_str.length() - 1, dest_str.length());
			}
			target = target.replace(origin_str, dest_str.toString());
		}

		p = Pattern.compile("[零一二两三四五六七八九]");
		m = p.matcher(target); 
		sb = new StringBuffer(); 
		result = m.find(); 
		while(result) { 
			m.appendReplacement(sb, Integer.toString(wordToNumber(m.group()))); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		
		p = Pattern.compile("(?<=(周|星期|礼拜))[天日]");
		m = p.matcher(target); 
		sb = new StringBuffer(); 
		result = m.find(); 
		while(result) { 
			m.appendReplacement(sb, Integer.toString(wordToNumber(m.group()))); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();

		p = Pattern.compile("(?<=(\\d{4}))0?[0-9]?十[0-9]?(?=(月))");
		m = p.matcher(target);
		if(m.find()){
			target = target.replace("十", "年十");
		}
		
		p = Pattern.compile("(?<!(周|星期|礼拜))0?[0-9]?十[0-9]?");
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while(result) { 
			String group = m.group();
			String[] s = group.split("十");
			int num = 0;
			if(s.length == 0){
				num += 10;
			}
			else if(s.length == 1){
				int ten = Integer.parseInt(s[0]);
				if(ten == 0)
					num += 10;
				else num += ten*10;
			}
			else if(s.length == 2){
				if(s[0].equals(""))
					num += 10;
				else{
					int ten = Integer.parseInt(s[0]);
					if(ten == 0)
						num += 10;
					else num += ten*10;
				}
				num += Integer.parseInt(s[1]);
			}
			m.appendReplacement(sb, Integer.toString(num)); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		
		p = Pattern.compile("0?[1-9]百[0-9]?[0-9]?"); 
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while(result) { 
			String group = m.group();
			String[] s = group.split("百");
			int num = 0;
			if(s.length == 1){
				int hundred = Integer.parseInt(s[0]);
				num += hundred*100;
			}
			else if(s.length == 2){
				int hundred = Integer.parseInt(s[0]);
				num += hundred*100;
				num += Integer.parseInt(s[1]);
			}
			m.appendReplacement(sb, Integer.toString(num)); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();

		p = Pattern.compile("0?(([1-9][0-9]*)?[0-9][.][0-9]+|[1-9][0-9]*)千[0-9]?[0-9]?[0-9]?");
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while(result) { 
			String group = m.group();
			String[] s = group.split("千");
			Float num = Float.valueOf(0);
			if(s.length == 1){
				float thousand = Float.parseFloat(s[0]);
				num += thousand*1000;
			}
			else if(s.length == 2){
				float thousand = Float.parseFloat(s[0]);
				num += thousand*1000;
				num += Integer.parseInt(s[1]);
			}
			m.appendReplacement(sb, Integer.toString(num.intValue()));
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		
		p = Pattern.compile("[0-9]+万[0-9]?[0-9]?[0-9]?[0-9]?"); 
		m = p.matcher(target);
		sb = new StringBuffer();
		result = m.find();
		while(result) { 
			String group = m.group();
			String[] s = group.split("万");
			int num = 0;
			if(s.length == 1){
				int tenthousand = Integer.parseInt(s[0]);
				num += tenthousand*10000;
			}
			else if(s.length == 2){
				int tenthousand = Integer.parseInt(s[0]);
				num += tenthousand*10000;
				num += Integer.parseInt(s[1]);
			}
			m.appendReplacement(sb, Integer.toString(num)); 
			result = m.find(); 
		}
		m.appendTail(sb);
		target = sb.toString();
		
		return target;
	}
	
	/**
	 * 方法numberTranslator的辅助方法，可将[零-九]正确翻译为[0-9]
	 * 
	 * @param s 大写数字
	 * @return 对应的整形数，如果不是大写数字返回-1
	 */
	private static int wordToNumber(String s){
		if(s.equals("零")||s.equals("0"))
			return 0;
		else if(s.equals("一")||s.equals("1"))
			return 1;
		else if(s.equals("二")||s.equals("两")||s.equals("2"))
			return 2;
		else if(s.equals("三")||s.equals("3"))
			return 3;
		else if(s.equals("四")||s.equals("4"))
			return 4;
		else if(s.equals("五")||s.equals("5"))
			return 5;
		else if(s.equals("六")||s.equals("6"))
			return 6;
		else if(s.equals("七")||s.equals("天")||s.equals("日")||s.equals("7"))
			return 7;
		else if(s.equals("八")||s.equals("8"))
			return 8;
		else if(s.equals("九")||s.equals("9"))
			return 9;
		else return -1;
	}


	/**
	 * 把节日翻译到时间
	 *
	 * @param target 原字符串
	 */
	public static String festivalToDate(String target, LinkedHashMap<String, String> HolidayMap){
		for(String key : HolidayMap.keySet()){
			String[]array_key = key.split(" ");
			for(String k : array_key ) {
				if (!k.isEmpty() && (target.contains(k) || k.contains(target))) {
					target = target.replaceAll(k, HolidayMap.get(key));
				}
			}
		}
		return target;
	}

	/**
	 * 把句子中的专有名词剔除
	 *
	 * @param target 原字符串
	 */
	public static String removeProperNoun(String target, List<String> proper_noun){
		if(proper_noun != null && proper_noun.size() != 0) {
			for (String s : proper_noun) {
				if (target.contains(s)) {
					target = target.replaceAll(s, "");
				}
			}
		}
		return target;
	}

	/**
	 *
	 * @param input  深圳南山到广州天河五六公斤多少钱
	 * @return 		 深圳南山到广州天河六公斤多少钱
	 */
	public static String handWordAmbiguity(String input){
		String tmpInput = input;
		Pattern p = Pattern.compile("(一二|二三|三四|四五|五六|六七|七八|八九){1}");
		Matcher m = p.matcher(tmpInput);
		while (m.find()) {
			tmpInput = tmpInput.substring(0, m.start()).concat(tmpInput.substring(m.start() + 1));
			if (tmpInput.length() < 2) {
				return tmpInput;
			}
			m = p.matcher(tmpInput);
		}

		return  tmpInput;
	}
}







