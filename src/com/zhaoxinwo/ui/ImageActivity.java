package com.zhaoxinwo.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhaoxinwo.api.ZApi;

public class ImageActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Hide title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image);

		// Get image uris from ResultActivity
		final ArrayList<String> imageUris = getIntent()
				.getStringArrayListExtra("urls");
		final ArrayList<String> imageTitles = getIntent()
				.getStringArrayListExtra("titles");

		final Handler imageHandler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				super.handleMessage(message);

				ArrayList<Bitmap> images = (ArrayList<Bitmap>) message.obj;
				for (Bitmap image : images) {
					ImageView imageview = new ImageView(getApplicationContext());
					imageview.setImageBitmap(image);
					imageview.setPadding(0, 2, 0, 0);
					imageview.setAdjustViewBounds(true);
					((LinearLayout) findViewById(R.id.image_layout))
							.addView(imageview);
				}

			}
		};

		// Get result
		new Thread(new Runnable() {

			@Override
			public void run() {
				ZApi api = new ZApi();
				ArrayList<Bitmap> images = new ArrayList<Bitmap>();
				for (String imageUri : imageUris) {

					Bitmap image = api.doGetImage(imageUri);
					images.add(image);
				}

				Message message = Message.obtain();
				message.obj = images;
				imageHandler.sendMessage(message);
			}
		}).start();
	}
}