package com.zhaoxinwo.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoxinwo.api.ZApi;
import com.zhaoxinwo.api.ZColor;
import com.zhaoxinwo.model.House;
import com.zhaoxinwo.model.Result;

public class ResultActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_result);

		// Get keywords from HomeActivity
		final String keywords = getIntent().getStringExtra("keywords");

		// Set title
		((TextView) findViewById(R.id.text_title)).setText("搜索: " + keywords);
		Toast.makeText(getApplicationContext(), keywords, Toast.LENGTH_SHORT)
				.show();

		final Handler resultHandler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				super.handleMessage(message);

				ArrayList<Object> list = (ArrayList<Object>) message.obj;

				Result result = (Result) list.get(0);
				ArrayList<Bitmap> avatars = (ArrayList<Bitmap>) list.get(1);
				ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

				for (int i = 0; i < result.result.size(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("sim", result.result.get(i).sim);
					map.put("title", result.result.get(i).title);
					map.put("name", result.result.get(i).author.name);
					map.put("pub_time", result.result.get(i).pub_time);
					map.put("avatar", avatars.get(i));
					map.put("text", result.result.get(i).text);
					map.put("ditie", result.result.get(i).ditie);
					map.put("dizhi", result.result.get(i).dizhi);
					map.put("jushi", result.result.get(i).jushi);
					map.put("zujin", result.result.get(i).zujin);
					map.put("shouji", result.result.get(i).shouji);
					/*
					 * map.put("dizhi", result.result.get(i).dizhi);
					 * map.put("shouji", result.result.get(i).shouji);
					 * map.put("images", result.result.get(i).images);
					 * map.put("url", result.result.get(i).url); map.put("sim",
					 * result.result.get(i).sim);
					 */
					listItem.add(map);
				}
				SimpleAdapter listItemAdapter = new SimpleAdapter(
						ResultActivity.this, listItem, R.layout.listview_item,
						new String[] { "sim", "title", "name", "pub_time",
								"avatar", "text", "ditie", "dizhi", "jushi",
								"zujin", "shouji" }, new int[] { R.id.sim,
								R.id.title, R.id.name, R.id.pub_time,
								R.id.avatar, R.id.text, R.id.ditie, R.id.dizhi,
								R.id.jushi, R.id.zujin, R.id.shouji });

				((ListView) findViewById(R.id.listview_result))
						.setAdapter(listItemAdapter);
				listItemAdapter.setViewBinder(new ViewBinder() {
					public boolean setViewValue(View view, Object data,
							String textRepresentation) {
						// Filter empty list and string
						if (data instanceof String) {
							if (((String) data).length() == 0) {
								view.setVisibility(View.GONE);
								return true;
							}
						}
						if (data instanceof ArrayList) {
							if (((ArrayList) data).size() == 0) {
								view.setVisibility(View.GONE);
								return true;
							}
						}

						if (view.getId() == R.id.sim) {
							((TextView) view).setText(String.format("重复发贴%d次",
									((ArrayList<Object>) data).size()));
							return true;

						}
						if (view instanceof ImageView && data instanceof Bitmap) {
							ImageView iv = (ImageView) view;
							iv.setImageBitmap((Bitmap) data);
							return true;
						}
						if (view.getId() == R.id.ditie) {
							ArrayList<String> dities = (ArrayList<String>) data;
							String html = "";
							ZColor color = new ZColor();
							for (String ditie : dities) {
								String ditieColor = color.ditie(ditie);
								html += String.format(
										"<font color='%s'>%s</font> ",
										ditieColor, ditie);
							}
							((TextView) view).setText(Html.fromHtml(html),
									TextView.BufferType.SPANNABLE);
							return true;

						}
						if (view.getId() == R.id.dizhi) {
							((TextView) view)
									.setText(((ArrayList<String>) data)
											.toString().replaceAll("[\\[\\]]",
													""));
							return true;

						}

						return false;
					}
				});
			}
		};

		// Get result
		new Thread(new Runnable() {
			@Override
			public void run() {
				ZApi api = new ZApi();
				Result result = api.search(keywords);
				ArrayList<Bitmap> avatars = new ArrayList<Bitmap>();

				for (House house : result.result) {

					System.out.println(house.author.avatar);
					Bitmap avatar = api.doGetImage(house.author.avatar);
					avatars.add(avatar);
					System.out.println(avatar);
				}

				ArrayList<Object> list = new ArrayList<Object>();
				list.add(result);
				list.add(avatars);

				Message message = Message.obtain();
				message.obj = list;
				resultHandler.sendMessage(message);
			}
		}).start();
	}
}