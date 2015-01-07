package com.zhaoxinwo.model;

import java.util.ArrayList;
import java.util.List;

public class Result {
	public Integer page_prev;
	public Integer page_next;
	public Integer page_count;
	public Integer page_num;
	public List<House> result = new ArrayList<House>();
	public String query;
}
