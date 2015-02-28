package com.zhaoxinwo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.zhaoxinwo.api.ZApi;

public class StatisticActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_statistic);

		final WebView webView = (WebView) findViewById(R.id.webView);
		webView.loadUrl((new ZApi()).getBaseUri() + "#/statistic");
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);

		final TextView textProgress = (TextView) findViewById(R.id.textProgress);
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {

				textProgress.setText("加载中...(" + newProgress + "%)");
				if (newProgress == 100) {
					webView.setVisibility(View.VISIBLE);
					textProgress.setVisibility(View.GONE);
				} else {
					webView.setVisibility(View.GONE);
					textProgress.setVisibility(View.VISIBLE);
				}
			}

		});
	}
}
