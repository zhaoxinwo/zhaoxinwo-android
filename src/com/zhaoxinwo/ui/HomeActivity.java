package com.zhaoxinwo.ui;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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

import com.umeng.analytics.MobclickAgent;
import com.zhaoxinwo.api.ZApi;

public class HomeActivity extends Activity {
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
				}
				((TextView) findViewById(R.id.textUpdate)).setText("当前版本: "
						+ version);
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
		
		MobclickAgent.updateOnlineConfig(getApplicationContext());
		
		((Button) findViewById(R.id.buttonSearch))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onButtonSearchClick(v);
					}
				});
		((TextView) findViewById(R.id.textSite))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onTextSiteClick(v);
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
		((TextView) findViewById(R.id.textStatistic))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onTextStatisticClick(v);
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
		this.autoUpdate();
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

	private static Boolean isExit = false;  
    private static Boolean hasTask = false;  
    Timer tExit = new Timer();  
    TimerTask task = new TimerTask() {  
        @Override  
        public void run() {  
            isExit = false;  
            hasTask = true;  
        }  
    };  
  
  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        // TODO Auto-generated method stub  
        if(keyCode == KeyEvent.KEYCODE_BACK){
                if(isExit == false ) {  
                    isExit = true;  
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
                    if(!hasTask) {  
                        tExit.schedule(task, 2000);  
                    }
                }
                else {
	                finish();  
	                System.exit(0);
                }
        }
        return false;  
    }

	public void autoUpdate() {
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

	public void onTextSiteClick(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("http://zhaoxinwo.com"));
		startActivity(intent);
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
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse((new ZApi())
				.getBaseUri() + "#/donate"));
		startActivity(intent);
	}

	public void onTextStatisticClick(View v) {
		Intent intent = new Intent(HomeActivity.this, StatisticActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
}