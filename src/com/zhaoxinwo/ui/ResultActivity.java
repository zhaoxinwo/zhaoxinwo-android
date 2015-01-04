package com.zhaoxinwo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaoxinwo.api.ZApi;
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

		// Get result
		new Thread(new Runnable() {
			@Override
			public void run() {
				ZApi api = new ZApi();
				Result result = api.search(keywords, 1);
				((TextView) findViewById(R.id.text_title)).setText("搜索: "
						+ keywords);
				System.out.println(result);
			}
		}).start();
	}
}