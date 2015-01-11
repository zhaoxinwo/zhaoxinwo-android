package com.zhaoxinwo.api;

import java.util.HashMap;

public class ZColor {
	public String ditie(String ditie) {
		HashMap<String, String> colorMap = new HashMap<String, String>();
		colorMap.put("1号线", "#A8353A");
		colorMap.put("2号线", "#004A88");
		colorMap.put("4号线", "#008996");
		colorMap.put("5号线", "#AF0061");
		colorMap.put("6号线", "#B78500");
		colorMap.put("7号线", "#FFC66E");
		colorMap.put("8号线", "#009878");
		colorMap.put("9号线", "#94D600");
		colorMap.put("10号线", "#008FBF");
		colorMap.put("13号线", "#F6DA40");
		colorMap.put("14号线", "#CC9A8E");
		colorMap.put("15号线", "#673278");
		colorMap.put("八通线", "#A8353A");
		colorMap.put("昌平线", "#DE85BB");
		colorMap.put("大兴线", "#008996");
		colorMap.put("房山线", "#DC6016");
		colorMap.put("门头沟线", "#A65A2A");
		colorMap.put("燕房线", "#DC6016");
		colorMap.put("亦庄线", "#D6006E");
		colorMap.put("西郊线", "#E1261C");
		colorMap.put("机场线", "#A292B3");

		if (colorMap.containsKey(ditie)) {
			return colorMap.get(ditie);
		}
		return "#000000";
	}
}
