package com.zhaoxinwo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);

		Toast.makeText(getApplicationContext(), "HomeActivity",
				Toast.LENGTH_SHORT).show();

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
}