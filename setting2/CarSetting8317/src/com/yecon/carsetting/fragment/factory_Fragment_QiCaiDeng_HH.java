package com.yecon.carsetting.fragment;

import android.app.DialogFragment;
import android.content.Context;
import android.mcu.McuManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yecon.carsetting.R;
import com.yecon.carsetting.unitl.McuMethodManager;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.view.ColorPlates;
import com.yecon.carsetting.view.ColorPlates.onColorListener;

public class factory_Fragment_QiCaiDeng_HH extends DialogFragment implements onColorListener {
	private final int mMessageID = 5008;
	private final int mDelayTime = 100;

	private int mColor = 0x00;
	private ColorPlates mColorPlates = null;

	private void initData() {
		int iRed = SystemProperties.getInt(Tag.PERSYS_BACK_LIGHT_R, XmlParse.light_r);
		int iGreen = SystemProperties.getInt(Tag.PERSYS_BACK_LIGHT_G, XmlParse.light_g);
		int iBlue = SystemProperties.getInt(Tag.PERSYS_BACK_LIGHT_B, XmlParse.light_b);
		mColor = 0xFF000000 | ((iRed*255/100 & 0xFF) << 16) | ((iGreen*255/100 & 0xFF)<< 8) | iBlue*255/100;
	}

	@Override
	public void onStart() {
		super.onStart();
		getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_FRAME, 0);
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mRootView = inflater.inflate(R.layout.factory_fragment_qicaideng_hh, container, false);
		mColorPlates = (ColorPlates) mRootView.findViewById(R.id.color_plates);
		mColorPlates.setColorListener(this);
		mColorPlates.setColor(mColor);
		return mRootView;
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == mMessageID) {
				setMcuParam_qicaideng();
			}
		}
	};

	private void setMcuParam_qicaideng() {
		int iRed = ((mColor >> 16) & 0xFF) * 100 / 255;
		int iGreen = ((mColor >> 8) & 0xFF) * 100 / 255;
		int iBlue = (mColor & 0xFF) * 100 / 255;
		SystemProperties.set(Tag.PERSYS_BACK_LIGHT_R, iRed + "");
		SystemProperties.set(Tag.PERSYS_BACK_LIGHT_G, iGreen + "");
		SystemProperties.set(Tag.PERSYS_BACK_LIGHT_B, iBlue + "");
		McuMethodManager.getInstance(getActivity()).setMcuParam_qicaideng();
	}

	private void sendMsg() {
		mHandler.removeMessages(mMessageID);
		mHandler.sendEmptyMessageDelayed(mMessageID, mDelayTime);
	}

	@Override
	public void onDataChange(int value) {
		mColor = value;
		sendMsg();
	}
}
