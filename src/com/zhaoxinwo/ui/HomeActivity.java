package com.zhaoxinwo.ui;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoxinwo.api.ZApi;

public class HomeActivity extends Activity {
	private boolean autoUpdate = false;

	protected void showUpdateDialog(Intent intent) {
		final Intent browserIntent = intent;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("更新");
		builder.setMessage("程序员熬夜发布新版本哦！");
		builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				startActivity(browserIntent);
			}
		}).setNegativeButton("取消", null).show();
	}

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			super.handleMessage(message);
			if (message.obj == null) {
				Toast.makeText(getApplicationContext(), "网络不佳额",
						Toast.LENGTH_SHORT).show();
				return;
			}

			HashMap<String, String> map = (HashMap) message.obj;

			String version = map.get("version");
			String uri = map.get("uri");
			PackageManager pm = getApplicationContext().getPackageManager();
			PackageInfo pi;
			try {
				pi = pm.getPackageInfo(
						getApplicationContext().getPackageName(), 0);
				String currentVersion = pi.versionName;

				if (Integer.parseInt(currentVersion.replaceAll("\\.", "")) < Integer
						.parseInt(version.replaceAll("\\.", ""))) {

					Intent browserIntent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(uri));
					HomeActivity.this.showUpdateDialog(browserIntent);
				} else if (!HomeActivity.this.autoUpdate) {
					Toast.makeText(getApplicationContext(), "已经是最新版本",
							Toast.LENGTH_SHORT).show();
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);

		((Button) findViewById(R.id.buttonSearch))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onButtonSearchClick(v);
					}
				});
		((TextView) findViewById(R.id.textUpdate))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onTextUpdateClick(v);
					}
				});
		((TextView) findViewById(R.id.textShare))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onTextShareClick(v);
					}
				});
		((TextView) findViewById(R.id.textDonate))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onTextDonateClick(v);
					}
				});
		// EditText submit event
		((EditText) findViewById(R.id.textKeywords))
				.setOnKeyListener(new OnKeyListener() {

					@Override
					public boolean onKey(View v, int keyCode, KeyEvent event) {
						if (KeyEvent.KEYCODE_ENTER == keyCode
								&& event.getAction() == KeyEvent.ACTION_DOWN) {
							onButtonSearchClick(findViewById(R.id.buttonSearch));
							return true;
						}
						return false;

					}
				});

		// Update
		this.onTextUpdateClick(null);
	}

	public void onButtonSearchClick(View v) {
		String keywords = ((TextView) findViewById(R.id.textKeywords))
				.getText().toString().trim();
		Intent intent = new Intent(HomeActivity.this, ResultActivity.class);

		if (!keywords.isEmpty()) {
			intent.putExtra("keywords", keywords);
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(), "请先输入地点名称",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void onTextUpdateClick(View v) {
		this.autoUpdate = v == null ? true : false;

		new Thread(new Runnable() {
			@Override
			public void run() {

				ZApi api = new ZApi();

				Message message = Message.obtain();
				message.obj = api.latestVersion();
				updateHandler.sendMessage(message);
			}
		}).start();
	}

	public void onTextShareClick(View v) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		intent.putExtra(Intent.EXTRA_TEXT,

		"#找新窝# 基于豆瓣租房小组数据的搜房App，用豆瓣找房子的亲可以试试哦！ http://zhaoxinwo.com");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(Intent.createChooser(intent, getTitle()));
	}

	public void onTextDonateClick(View v) {
		Intent intent = new Intent(HomeActivity.this, DonateActivity.class);
		startActivity(intent);
	}
}