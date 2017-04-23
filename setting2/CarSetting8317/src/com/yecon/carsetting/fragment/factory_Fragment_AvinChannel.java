package com.yecon.carsetting.fragment;

import java.util.ArrayList;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yecon.carsetting.R;
import com.yecon.carsetting.fragment.Fragment_List.OnListDlgListener;
import com.yecon.carsetting.fragment.Fragment_Progress.OnProgressDlgListener;
import com.yecon.carsetting.unitl.Function;
import com.yecon.carsetting.unitl.Tag;
import com.yecon.carsetting.unitl.XmlParse;
import com.yecon.carsetting.unitl.tzutil;
import com.yecon.carsetting.view.HeaderLayout;
import com.yecon.carsetting.view.HeaderLayout.onOneButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onOneCheckBoxListener;
import com.yecon.carsetting.view.HeaderLayout.onTwoButtonListener;
import com.yecon.carsetting.view.HeaderLayout.onTwoRadioButtonListener;

public class factory_Fragment_AvinChannel extends DialogFragment implements
		onTwoButtonListener {

	private Context mContext;
	View mRootView;
	FragmentManager mFragmentManager;
	Fragment_List mDialog;

	int ID_TwoButton[] = { 
			R.id.factory_avin1_video_channel, R.id.factory_avin1_audio_channel, 
			R.id.factory_avin2_video_channel, R.id.factory_avin2_audio_channel,
			R.id.factory_tv_video_channel, R.id.factory_tv_audio_channel, 
			R.id.factory_dvr_video_channel, R.id.factory_camera_video_channel,
			R.id.factory_fm_audio_channel, R.id.factory_ipod_audio_channel
			};
	
	private HeaderLayout mLayout_TwoButton[] = new HeaderLayout[ID_TwoButton.length];

//	private String[] StringArray_crv_audio_input;
//	private String[] stringarray_drive_record;
//	private String[] stringarray_front_camera;

//	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			if (action.equals("")) {
//			}
//		}
//	};

	private void initData() {
		mContext = getActivity();
		mFragmentManager = getFragmentManager();
//		IntentFilter filter = new IntentFilter();
//		mContext.registerReceiver(mBroadcastReceiver, filter);

//		StringArray_crv_audio_input = tzutil.getStringArray(5);
//		stringarray_drive_record = getResources().getStringArray(R.array.aux_port);
//		stringarray_front_camera = getResources().getStringArray(R.array.aux_port);
	}

	public factory_Fragment_AvinChannel() {

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setStyle(DialogFragment.STYLE_NO_FRAME, 0);
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.factory_fragment_avinchannel, container, false);
		initView(mRootView);
		return mRootView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//mContext.unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	private void initView(View rootView) {

		int i = 0;
		for (i=0;i<mLayout_TwoButton.length;i++) {
			mLayout_TwoButton[i] = (HeaderLayout) rootView.findViewById(ID_TwoButton[i]);
			mLayout_TwoButton[i].setTwoButtonListener(this);
			mLayout_TwoButton[i].setMiddleTitle(XmlParse.avin_channel[i] + "");
		}
		
	}

	@Override
	public void onLeftButtonClick(View view) {
		// TODO Auto-generated method stub
		int value = -1; int id = view.getId();
		int index = -1;
		for(int i=0;i<ID_TwoButton.length;i++){
			if(ID_TwoButton[i] == id){				
				index = i;
			}
		}
		if(index>=0){
			value = XmlParse.avin_channel[index];
			value--;
			if(value<=0)value=5;
			XmlParse.avin_channel[index] = value;
			mLayout_TwoButton[index].setMiddleTitle(value + "");
		}
	}

	@Override
	public void onRightButtonClick(View view) {
		// TODO Auto-generated method stub
		int value = -1; int id = view.getId();
		int index = -1;
		for(int i=0;i<ID_TwoButton.length;i++){
			if(ID_TwoButton[i] == id){
				index = i;
			}
		}
		if(index>=0){
			value = XmlParse.avin_channel[index];
			value++;
			if(value>5)value=1;
			XmlParse.avin_channel[index] = value;
			mLayout_TwoButton[index].setMiddleTitle(value + "");
		}
	}

}