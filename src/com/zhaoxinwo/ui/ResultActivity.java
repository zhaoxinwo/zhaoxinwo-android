package com.zhaoxinwo.ui;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.zhaoxinwo.api.ZApi;
import com.zhaoxinwo.api.ZColor;
import com.zhaoxinwo.model.House;
import com.zhaoxinwo.model.Result;
import com.zhaoxinwo.utils.SwipeBackActivity;

public class ResultActivity extends SwipeBackActivity {
	protected static final String TAG = "ResultActivity";
	private String keywords = null;
	private int pageNum = 1;
	private ArrayList<House> houses = new ArrayList<House>();
	private SimpleAdapter listItemAdapter = null;
	private ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
	private ListView listview = null;
	private TextView text_more;
	private Handler resultHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			super.handleMessage(message);
			if (message.obj == null) {
				ResultActivity.this.text_more.setText("网络不佳额");
				Toast.makeText(getApplicationContext(), "网络不佳额",
						Toast.LENGTH_SHORT).show();
				return;
			}

			ArrayList<Object> list = (ArrayList<Object>) message.obj;
			ArrayList<House> result = (ArrayList) ((Result) list.get(0)).result;
			if (result.isEmpty()) {
				// Set title
				ResultActivity.this.text_more.setText("没有更多啦");
				Toast.makeText(getApplicationContext(), "没有更多啦",
						Toast.LENGTH_SHORT).show();
				return;

			}

			ArrayList<Bitmap> avatars = (ArrayList<Bitmap>) list.get(1);
			houses.addAll(result);
			for (int i = 0; i < result.size(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("sim", result.get(i).sim);
				map.put("title", result.get(i).title);
				map.put("name", result.get(i).author.name);
				map.put("pub_time", result.get(i).pub_time);
				map.put("avatar", avatars.get(i));
				map.put("text", result.get(i).text);
				map.put("ditie", result.get(i).ditie);
				map.put("dizhi", result.get(i).dizhi);
				map.put("jushi", result.get(i).jushi);
				map.put("zujin", result.get(i).zujin);
				map.put("shouji", result.get(i).shouji);
				map.put("url", result.get(i).url);
				map.put("image", result.get(i).images);
				listItem.add(map);
			}
			ResultActivity.this.text_more.setText("上拉加载更多");
			listItemAdapter.notifyDataSetChanged();
		}
	};

	private ViewBinder viewbinder = new ViewBinder() {
		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			// Filter empty list and string
			if (data instanceof String) {
				if (((String) data).length() == 0) {
					view.setVisibility(View.GONE);
					return true;
				} else {
					view.setVisibility(View.VISIBLE);
				}
			}
			if (data instanceof ArrayList) {
				if (((ArrayList) data).size() == 0) {
					view.setVisibility(View.GONE);
					return true;
				} else {
					view.setVisibility(View.VISIBLE);
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
					html += String.format("<font color='%s'>%s</font> ",
							ditieColor, ditie);
				}
				((TextView) view).setText(Html.fromHtml(html),
						TextView.BufferType.SPANNABLE);
				return true;

			}
			if (view.getId() == R.id.dizhi) {
				((TextView) view).setText(((ArrayList<String>) data).toString()
						.replaceAll("[\\[\\]]", ""));
				return true;

			}
			if (view.getId() == R.id.text) {
				final String text = (String) data;
				TextView textview = (TextView) view;
				textview.setText(text);
				textview.setMaxLines(5);
				textview.setEllipsize(TruncateAt.END);
				textview.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						TextView textview = (TextView) v;
						textview.setText(text);
						if (textview.getEllipsize() != null) {
							textview.setEllipsize(null);
							textview.setMaxLines(100);
						} else {
							textview.setEllipsize(TruncateAt.END);
							textview.setMaxLines(5);
						}

					}
				});

				return true;
			}
			if (view.getId() == R.id.shouji) {
				((TextView) view).setText((String) data);
				((TextView) view).getPaint()
						.setFlags(Paint.UNDERLINE_TEXT_FLAG);
				final String phoneNum = ((String) data).split(",")[0];
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_DIAL, Uri
								.parse("tel:" + phoneNum));
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						ResultActivity.this.startActivity(intent);
					}
				});
				return true;
			}
			return false;
		}

	};

	private void pullData() {

		new Thread(new Runnable() {
			@Override
			public void run() {

				ZApi api = new ZApi();
				Result result = api.search(keywords, pageNum);
				ArrayList<Bitmap> avatars = new ArrayList<Bitmap>();

				if (result == null) {
					Message message = Message.obtain();
					message.obj = null;
					resultHandler.sendMessage(message);
					return;
				}

				for (House house : result.result) {
					Bitmap avatar = api.doGetImage(house.author.avatar);
					if (avatar == null) {

						Message message = Message.obtain();
						message.obj = null;
						resultHandler.sendMessage(message);
						return;
					}
					avatars.add(avatar);
				}

				ArrayList<Object> list = new ArrayList<Object>();
				list.add(result);
				list.add(avatars);

				Message message = Message.obtain();
				message.obj = list;
				resultHandler.sendMessage(message);
				pageNum++;

			}

		}).start();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide title
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_result);

		// Get keywords from HomeActivity
		keywords = getIntent().getStringExtra("keywords");

		// Set title
		((TextView) findViewById(R.id.text_title)).setText("当前搜索: " + keywords);
//		Toast.makeText(getApplicationContext(), keywords, Toast.LENGTH_SHORT).show();

		listview = (ListView) findViewById(R.id.listview_result);

		listItemAdapter = new SimpleAdapter(ResultActivity.this, listItem,
				R.layout.listview_item, new String[] { "sim", "title", "name",
						"pub_time", "avatar", "text", "ditie", "dizhi",
						"jushi", "zujin", "shouji" }, new int[] { R.id.sim,
						R.id.title, R.id.name, R.id.pub_time, R.id.avatar,
						R.id.text, R.id.ditie, R.id.dizhi, R.id.jushi,
						R.id.zujin, R.id.shouji }) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);
				final TextView html = (TextView) view
						.findViewById(com.zhaoxinwo.ui.R.id.html);
				final TextView image = (TextView) view
						.findViewById(com.zhaoxinwo.ui.R.id.image);
				final TextView favorate = (TextView) view
						.findViewById(com.zhaoxinwo.ui.R.id.favorate);

				final int index = position;

				if (houses.get(index).images.isEmpty()) {
					image.setVisibility(View.GONE);
				} else {
					image.setVisibility(View.VISIBLE);
				}

				OnClickListener listener = new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (v == html) {
							Intent browserIntent = new Intent(
									Intent.ACTION_VIEW, Uri.parse(houses
											.get(index).url));
							startActivity(browserIntent);
						}
						if (v == image) {
							Intent intent = new Intent(ResultActivity.this,
									ImageActivity.class);

							ArrayList<String> imageUrls = new ArrayList<String>();
							ArrayList<String> imageTitles = new ArrayList<String>();
							for (ArrayList<String> list : houses.get(index).images) {
								imageUrls.add(list.get(0));
								imageTitles.add(list.get(1));
							}
							if (!imageUrls.isEmpty()) {
								intent.putExtra("urls", imageUrls);
								intent.putExtra("titles", imageTitles);
								startActivity(intent);
							}
						}
						if (v == favorate) {
							Intent intent = new Intent(Intent.ACTION_SEND);
							intent.setType("text/plain");
							intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
							intent.putExtra(
									Intent.EXTRA_TEXT,
									new Formatter()
											.format("来源: %s\n收藏自找新窝(zhaoxinwo.com)\n\n%s",
													houses.get(index).url,
													houses.get(index).text)
											.toString());
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(Intent.createChooser(intent,
									getTitle()));
						}

					}
				};
				html.setOnClickListener(listener);
				image.setOnClickListener(listener);
				favorate.setOnClickListener(listener);
				return view;
			}

		};

		listItemAdapter.setViewBinder(viewbinder);
		pullData();

		// Load more data
		text_more = new TextView(ResultActivity.this);
		text_more.setGravity(Gravity.CENTER_HORIZONTAL);
		text_more.setPadding(0, 0, 0, 24);
		text_more.setBackgroundColor(getResources()
				.getColor(R.color.WhiteSmoke));
		text_more.setText("加载中...");
		/*
		text_more.setText("点击加载更多");
		text_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pullData();
			}
		});
		*/
		
		listview.addFooterView(text_more);
		listview.setAdapter(listItemAdapter);
		listview.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){	//滚动停止
					if(view.getLastVisiblePosition() == view.getCount()-1){
						Log.v(TAG, "now at the bottom of listview, auto load more");
						ResultActivity.this.text_more.setText("加载中...");
						pullData();
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}
		});
		
		SharedPreferences sharedata = getSharedPreferences("first_run", 0); 
        Boolean isSoupon = sharedata.getBoolean("isSoupon", true); 
        if (isSoupon) { 
			ShowcaseView showcaseView = new ShowcaseView.Builder(this)
	        .setStyle(R.style.Custom_semi_transparent_demo)//setStyle instead of setTarget!
	        .hideOnTouchOutside()
	        .build();
	//		showcaseView.setBackground(getResources().getDrawable(R.drawable.swipe_back_en));//minAPI=16
			showcaseView.setBackgroundDrawable(getResources().getDrawable(R.drawable.swipe_back_en));//deprecated.
			//更新flag，第二次打开时不再显示
			Editor sharedataEditor = getSharedPreferences("first_run", 0).edit(); 
			sharedataEditor.putBoolean("isSoupon", false); 
			sharedataEditor.commit();
        }
	}
}