package com.zhaoxinwo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoxinwo.api.ZApi;
import com.zhaoxinwo.model.Author;
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
					map.put("title", result.result.get(i).title);
					map.put("name", result.result.get(i).author.name);
					map.put("pub_time", result.result.get(i).pub_time);
					map.put("avatar", avatars.get(i));
					map.put("jushi", result.result.get(i).jushi);
					map.put("dizhi", result.result.get(i).dizhi);
					map.put("ditie", result.result.get(i).ditie);
					map.put("zujin", result.result.get(i).zujin);
					map.put("shouji", result.result.get(i).shouji);
					map.put("images", result.result.get(i).images);
					map.put("url", result.result.get(i).url);
					map.put("text", result.result.get(i).text);
					map.put("sim", result.result.get(i).sim);
					listItem.add(map);
				}
				SimpleAdapter listItemAdapter = new SimpleAdapter(
						ResultActivity.this, listItem, R.layout.listview_item,
						new String[] { "title", "name", "pub_time", "avatar",
								"jushi", "dizhi", "ditie", "zujin", "shouji",
								"images", "url", "text", "sim" }, new int[] {
								R.id.title, R.id.name, R.id.pub_time,
								R.id.avatar, R.id.jushi, R.id.dizhi,
								R.id.ditie, R.id.zujin, R.id.shouji,
								R.id.images, R.id.url, R.id.text, R.id.sim });

				((ListView) findViewById(R.id.listview_result))
						.setAdapter(listItemAdapter);
				listItemAdapter.setViewBinder(new ViewBinder() {
					public boolean setViewValue(View view, Object data,
							String textRepresentation) {
						if (view instanceof ImageView && data instanceof Bitmap) {
							ImageView iv = (ImageView) view;
							iv.setImageBitmap((Bitmap) data);
							return true;
						} else
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