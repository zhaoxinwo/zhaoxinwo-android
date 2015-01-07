package com.zhaoxinwo.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoxinwo.api.ZApi;
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

		final Handler avatarHandler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				super.handleMessage(message);

				Bitmap avatar = (Bitmap) message.obj;
				//TODO
				//change specified listitem avatar
			}
		};

		final Handler resultHandler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				super.handleMessage(message);

				Result result = (Result) message.obj;
				ListView list = (ListView) findViewById(R.id.listview_result);
				ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
				for (House house : result.result) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("title", house.title);
					final String avatarUrl = house.author.avatar;
					new Thread(new Runnable() {

						@Override
						public void run() {
							Bitmap bitmap = new ZApi().doGetImage(avatarUrl);

							Message message = Message.obtain();
							message.obj = bitmap;
							avatarHandler.sendMessage(message);
						}
					});
					map.put("jushi", house.jushi);
					map.put("dizhi", house.dizhi);
					map.put("ditie", house.ditie);
					map.put("sim", house.sim);
					listItem.add(map);
				}
				SimpleAdapter listItemAdapter = new SimpleAdapter(
						ResultActivity.this, listItem, R.layout.listview_item,
						new String[] { "title", "avatar", "jushi", "dizhi",
								"ditie", "sim" }, new int[] { R.id.title,
								R.id.avatar, R.id.jushi, R.id.dizhi,
								R.id.ditie, R.id.sim });
				list.setAdapter(listItemAdapter);
			}
		};

		// Get result
		new Thread(new Runnable() {
			@Override
			public void run() {
				ZApi api = new ZApi();
				Result result = api.search(keywords);
				System.out.println(result.query);
				System.out.println(result.page_count);

				Message message = Message.obtain();
				message.obj = result;
				resultHandler.sendMessage(message);
			}
		}).start();
	}
}