package com.zhaoxinwo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class DonateActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_donate);
	}
}
