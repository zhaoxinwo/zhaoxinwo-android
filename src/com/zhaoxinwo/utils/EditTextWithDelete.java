package com.zhaoxinwo.utils;

import com.zhaoxinwo.ui.R;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class EditTextWithDelete extends EditText implements OnFocusChangeListener {

	protected static final String TAG = "EditTextWithDelete";
	private Context context;
	private Drawable drawable;
	
	public EditTextWithDelete(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}
	
	public EditTextWithDelete(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}
	
	public EditTextWithDelete(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		this.drawable = context.getResources().getDrawable(R.drawable.text_clear);
		addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				setDrawable();
				Log.v(TAG, "after text changed, len = "+length());
			}
		});
		setDrawable();
	}

	protected void setDrawable() {
		// TODO Auto-generated method stub
		if(length()==0){
			setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		}
		else{
			setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
		}
	}


	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if(hasFocus()){
			setDrawable();
		}
		else{
			setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(drawable!=null && event.getAction()==MotionEvent.ACTION_UP){		//判断是否点击了clear图标的范围，是则清空text
			int x = (int) event.getX();
			boolean isInnerWidth = (x>(getWidth()-getTotalPaddingRight())) &&
					(x<(getWidth()-getPaddingRight()));
			Rect rect = drawable.getBounds();
			int height = rect.height();
			int y = (int) event.getY();
			int distance = (getHeight()-height)/2;
			boolean isInnerHeight = (y>distance) && (y<(distance+height));
			
			if(isInnerWidth && isInnerHeight){
				this.setText("");
//				this.setImeOptions(EditorInfo.IME_ACTION_DONE);
			}
		}
		return super.onTouchEvent(event);
	}
}


