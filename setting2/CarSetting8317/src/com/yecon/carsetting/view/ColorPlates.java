package com.yecon.carsetting.view;

import com.yecon.carsetting.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ColorPlates extends View {
	private Context mContext = null;
	private int mRingWidth = 0x01;
	private int mMaxAngle = 360;
	private int mCircleRadius = 10;
	private int mPixelColor = 0xFFFFFFFF;
	private Paint paint;
	private boolean mClick = false;
	private int mColorMode = 0x00;
	
	public ColorPlates(Context context) {
		super(context);
	}

	public ColorPlates(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		mContext = context;
		TypedArray typedArray = mContext.obtainStyledAttributes(attrs,R.styleable.ColorPlates); 
		mRingWidth = typedArray.getInteger(R.styleable.ColorPlates_ring_width, mRingWidth);
		mCircleRadius = typedArray.getInteger(R.styleable.ColorPlates_circle_radius, mMaxAngle);
		typedArray.recycle();
	}

	public void setColor(int value) {
		mPixelColor = value;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	    paint.setAntiAlias(true);                       //设置画笔为无锯齿  
	    paint.setStyle(Paint.Style.FILL);
	    paint.setColor(mPixelColor);
	    canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, mCircleRadius, paint);// 小圆 
	    paint.setStrokeWidth((float) mRingWidth);              	//线宽  
	    paint.setStyle(Paint.Style.STROKE);  
	    RectF oval= new RectF();                     			//RectF对象  
	    oval.left = getMeasuredWidth()/2 - mCircleRadius - mRingWidth;                              
	    oval.top= getMeasuredWidth()/2 - mCircleRadius - mRingWidth ;                                  
	    oval.right= getMeasuredWidth()/2 + mCircleRadius + mRingWidth ;                             
	    oval.bottom= getMeasuredWidth()/2 + mCircleRadius + mRingWidth; 
	    if (mClick) {
		    canvas.drawArc(oval, 0, 360, false, paint);  
		} else {
			paint.setColor(0x00000000);
			canvas.drawArc(oval, 0, 360, false, paint);  
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_DOWN:
//			mRotateListener.onChangeRotateAngle(mView, (float) rotate);
			invalidate();
			if (Math.hypot(event.getX() - getMeasuredWidth() / 2, getMeasuredHeight() / 2
							- event.getY()) < 50 ) {
				if (!mClick) {
					mClick = true;
					switchColor();
				}
			}else if (Math.hypot(event.getX() - getMeasuredWidth() / 2,
					getMeasuredHeight() / 2 - event.getY()) < getMeasuredHeight() / 2 -8
					&& Math.hypot(event.getX() - getMeasuredWidth() / 2, getMeasuredHeight() / 2
							- event.getY()) > 234/2) {
				Bitmap src =  BitmapFactory.decodeResource(getResources(),R.drawable.color_bk);
				mPixelColor = src.getPixel((int)event.getX(), (int)event.getY());
		        mClick = false;
			} else {
				mClick = false;
			}
			Log.i("LHb","pixelColor:"+mPixelColor);
			break;
		case MotionEvent.ACTION_UP:
			mClick = false;
			invalidate();
			if (mListener != null) {
				mListener.onDataChange(mPixelColor);
			}
			break;
		}
		return true;
	}

	private void switchColor() {
		if (mColorMode == 0x00) {
			mColorMode = 0x01;
			mPixelColor = 0xFFFF0000;
		} else if (mColorMode == 0x01) {
			mColorMode = 0x02;
			mPixelColor = 0xFF00FF00;
		} else if (mColorMode == 0x02) {
			mColorMode = 0x03;
			mPixelColor = 0xFF0000FF;
		} else if (mColorMode == 0x03) {
			mColorMode = 0x00;
			mPixelColor = 0xFFFFFFFF;
		}
	}
	
	public interface onColorListener {
		public void onDataChange(int value);
	}

	static onColorListener mListener;

	public void setColorListener(onColorListener hListener) {
		mListener = hListener;
	}

	public int getMaxAngle() {
		return mMaxAngle;
	}
}
