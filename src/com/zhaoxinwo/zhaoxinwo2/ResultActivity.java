package com.zhaoxinwo.zhaoxinwo2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class ResultActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_result);

		// Get keywords from HomeActivity
		String keywords = getIntent().getStringExtra("keywords");

		// Set title
		keywords = "搜索: " + keywords;
		((TextView) findViewById(R.id.text_title)).setText(keywords);
		Toast.makeText(getApplicationContext(), keywords, Toast.LENGTH_SHORT)
				.show();
	}
}