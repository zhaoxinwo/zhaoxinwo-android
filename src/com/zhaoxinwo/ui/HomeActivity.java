package com.zhaoxinwo.ui;

import java.util.HashMap;

import android.app.Activity;
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
	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			super.handleMessage(message);
			if (message.obj == null) {
				Toast.makeText(getApplicationContext(), "网络不佳额",
						Toast.LENGTH_SHORT).show();
				return;
			}

			HashMap<String, String> map = (HashMap)message.obj;

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
					startActivity(browserIntent);
				} else {
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
}