package com.yecon.carsetting;

import java.util.ArrayList;

import com.yecon.carsetting.unitl.PanelKeyData;
import com.yecon.carsetting.unitl.PanelKeyDefine;
import com.yecon.carsetting.unitl.PanelKeyInfo;
import com.yecon.carsetting.unitl.PanelKeyMcu;
import com.yecon.carsetting.unitl.PanelKeyRW;
import com.yecon.carsetting.unitl.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.mcu.McuBaseInfo;
import android.mcu.McuExternalConstant;
import android.mcu.McuListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PanelKeyLearnMain extends Activity implements OnClickListener {
	// ,OnTouchListener{
	String TAG = "PanelKeyLearning";
	private final int MAX_LEARN_KEY=25;
	private final int PANELKEY_START_LEARN=1;
	private final int PANELKEY_RESET_LEARN=2;
	private final int PANELKEY_END_LEARN=0;
	
	private final int PANELKEY_MODE_ATTR=1;
	private final int PANELKEY_CONTROL_ATTR=2;
	
	private final int mStudyBtnsId[] = {
			R.id.mainActivity_panelkey,
			R.id.back_panelkey,
			R.id.model_panelkey,
			R.id.band_panelkey,
			R.id.sound_panelkey,
			//--------------------------------------------------------
			R.id.navigation_panelkey,
			R.id.dvd_panelkey,
			R.id.music_panelkey,
			R.id.video_panelkey,
			R.id.television_panelkey,
			
			R.id.bluetoothphone_panelkey,
			//---------------------------------------------------------
			R.id.shutdown_panelkey,
			R.id.auto_scan_panelkey,
			R.id.browse_panelkey,
			R.id.play_or_stop_panelkey,
			R.id.decrease_panelkey,
			//--------------------------------------------------------
			R.id.increase_panelkey,
			R.id.out_panelkey,
			R.id.previous_panelkey,
			R.id.next_panelkey,
			R.id.hang_up_panelkey,
			//--------------------------------------------------------
			R.id.balance_panelkey,
			R.id.mute_panelkey,
			R.id.off_screen_panelkey,
	};
	
	private final int mStudyBtnKey[] = {
			PanelKeyDefine.K_SOURCE_HOME,
			PanelKeyDefine.K_RETURN,
			PanelKeyDefine.K_SOURCE_MODE,
			PanelKeyDefine.K_TUNER,
			PanelKeyDefine.K_ISSSR,
			//--------------------------------------------------------
			PanelKeyDefine.K_NAVI,
			PanelKeyDefine.K_DVD,
			PanelKeyDefine.K_MUSIC,
			PanelKeyDefine.K_VIDEO,
			PanelKeyDefine.K_TV,
			
			PanelKeyDefine.K_PHONE_ON,
			//---------------------------------------------------------
			PanelKeyDefine.K_POWER,
			PanelKeyDefine.T_RADIO_AS,
			PanelKeyDefine.T_RADIO_PS,
			PanelKeyDefine.T_PLAY,
			PanelKeyDefine.K_VOL_DN,			
			//--------------------------------------------------------
			PanelKeyDefine.K_VOL_UP,
			PanelKeyDefine.K_EJECT,
			PanelKeyDefine.T_PREV,
			PanelKeyDefine.T_NEXT,
			PanelKeyDefine.K_PHONE_OFF,
			//--------------------------------------------------------
			PanelKeyDefine.K_EQ,
			PanelKeyDefine.K_MUTE,
			PanelKeyDefine.T_BLACKOUT,
	};
	Button controlImageBtn;
	Button modeImageBtn;

	TextView showTextView;
	SettingSize settingSize;

	private Dialog quiltDialog = null;
	// public int mFragmentType = Util.mode_fragment_type;
	public  boolean mLongPressType = false;// long/short key type
	private  Button mCurBtn = null;

	int mCurRestOrExitBtnID = 0;

	Button[] mLearnButtons = new Button[mStudyBtnsId.length];
	Button[] mainButtons = new Button[4];
	int keyupAdValue_channel0=255;
	int keyupAdValue_channel1=255;
	
	private LinearLayout mModeLayout;
	private LinearLayout mControlLayout;
	
	private  ArrayList<PanelKeyInfo> panelKeyArrayList = new ArrayList<PanelKeyInfo>();
	public ArrayList<PanelKeyData> panelKeyDataArrayList = new ArrayList<PanelKeyData>();
	
	public Handler mMainHandler;
	private boolean mbInSizeDialog;
	private Context mContext=null;

	/**
	 * get key value by button id
	 * @param btnid
	 * @return
	 */
	int getKeyByBtnId(int btnid) {
		for (int i=0; i<mStudyBtnsId.length; i++) {
			if (btnid == mStudyBtnsId[i]) {
				return mStudyBtnKey[i];
			}
		}
		
		return -1;
	}
	
	/**
	 * get button id by keyvalue
	 * @param keyvalue
	 * @return
	 */
	int getBtnIdByKeyValue(int keyvalue) {
		for (int i=0; i<mStudyBtnKey.length; i++) {
			if (keyvalue == mStudyBtnKey[i]) {
				return mStudyBtnsId[i];
			}
		}
		
		return -1;
	}
	
	int getIndexByBtnId(int id) {
		for (int i=0; i<mStudyBtnsId.length; i++) {
			if (id == mStudyBtnsId[i]) {
				return i;
			}
		}
		
		return -1;
	}
	
	
	// Rx数据
	private McuListener mObjPanelKeyCallback = new McuListener() {
		
		@Override
		public void onMcuInfoChanged(McuBaseInfo arg0, int arg1) {
			// TODO Auto-generated method stub
			if (arg0 != null){
				if (arg1 == McuExternalConstant.MCU_PANELKEY_INFO_TYPE) {
					try {
						int length = arg0.getOriginalInfo().getMcuData().length;
						if (length < 3) {
							return;
						}
						
						byte[] data = arg0.getOriginalInfo().getMcuData();
						Bundle bundle = new Bundle();					
						int channel = data[0];
						int status = data[1];
						int advalue = (int)(data[2]&0xff);
						bundle.putInt("channel", channel);		//0-1
						bundle.putInt("status", status);				//0 key up   1 key down
						bundle.putInt("advalue", advalue);			//key ad value
						Log.i(TAG, "panelkey---receive mcu data:"+"channel="+channel+",status="+status+",advalue="+advalue);
						
						if (status == 0) {
							//two case
							//case 1 when send start learn or send reset, mcu will send this twice.
							//case 2 when key down, then key up
							if (channel == 0) {
								keyupAdValue_channel0 = advalue;
							}else if (channel == 1) {
								keyupAdValue_channel1 = advalue;
							}
							
							if (panelKeyDataArrayList.size() > 0) {
								Message msg = Message.obtain();
								msg.what = Util.PUBLIC_MSG_ID;
								mMainHandler.removeMessages(Util.PUBLIC_MSG_ID);
								mMainHandler.sendMessage(msg);
							}
						}else if (status == 1) {
							PanelKeyData keyData = new PanelKeyData();
							keyData.advalue = advalue;
							keyData.status = status;
							keyData.channel = channel;
							panelKeyDataArrayList.add(keyData);
						}
					}catch (IndexOutOfBoundsException e) {
	                    e.printStackTrace();
	                } catch (Exception e) {
	                    e.printStackTrace();
	                } finally {
	                }
				}
			}
		}
	};
		
	boolean compareADValue(int ad1, int ad2) {
		if (ad1 == ad2) {
			return true;
		}else {
			return false;
		}		
	}
	
	boolean analysePanelKeyInfo(PanelKeyInfo info, ArrayList<PanelKeyInfo> list) {
		if (info == null || list == null) {
			return false;
		}
		
		if (list.size() == 0) {
			return true;
		}
		
		for (int i=0; i<list.size(); i++) {
			int advalue = list.get(i).advalue;
			int channel = list.get(i).channel;
			int longpress = list.get(i).longpress;
			
			if (compareADValue(info.advalue, advalue)
					&& info.channel == channel
					&& info.longpress == longpress) {
				//find same key
				return false;
			}
		}
		
		return true;
	}
	/*
	 * public ArrayList<TouchMsg> touchConArrayList = new ArrayList<TouchMsg>();
	 * public ArrayList<TouchData> touchConDataArrayList = new
	 * ArrayList<TouchData>();
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		initUI();
		initData();

		mMainHandler = new Handler() {
			@SuppressLint("ResourceAsColor")
			public void handleMessage(Message msg) {

				if (msg.what == Util.PUBLIC_MSG_ID) {
					if( mCurBtn != null)
					{
						if(panelKeyArrayList.size() > (MAX_LEARN_KEY))
						{
							mCurRestOrExitBtnID = 0;
							((TextView) quiltDialog.findViewById(R.id.dialog_caption)).setText(R.string.factory_tocuh_key_beyond);						
							quiltDialog.show();
							return;
						}
						
						PanelKeyInfo keyInfo = new PanelKeyInfo();
						keyInfo.channel = panelKeyDataArrayList.get(0).channel;
						keyInfo.advalue = panelKeyDataArrayList.get(0).advalue;
						if (!mLongPressType) {
							keyInfo.longpress = 0;
						}else {
							keyInfo.longpress = 1;
						}						
						keyInfo.keyvalue = Util.curPanelKeyCode;
						keyInfo.btnid = mCurBtn.getId();
						panelKeyDataArrayList.clear();
						
						if (!analysePanelKeyInfo(keyInfo, panelKeyArrayList)) {
							setLearningText(R.string.factory_panelkey_has_learned, Color.RED);
						}else {
							panelKeyArrayList.add(keyInfo);
							mCurBtn.setTextColor(Color.YELLOW);
							mCurBtn.invalidate();
							setLearningText(R.string.factory_touch_key_learning_success, Color.YELLOW);
							mCurBtn = null;
						}
					}
					else
					{
						Log.i(TAG, "add key  mCurBtn="+mCurBtn+"，threadId: " + Thread.currentThread().getId());
					}

				}
			}
		};
		
		panelKeyArrayList.clear();
		PanelKeyRW.readData(panelKeyArrayList);
		
		Button btn = null;
		for (int i = 0; i < panelKeyArrayList.size(); i++) {
			int keyvalue = panelKeyArrayList.get(i).keyvalue;
			int btnid = getBtnIdByKeyValue(keyvalue);
			btn = (Button) findViewById(btnid);
			if(btn != null)				
				btn.setTextColor(Color.YELLOW);
		}
		
		PanelKeyMcu.getInstance(mContext).setCallback(mObjPanelKeyCallback);		
	}

	private void initUI() {
		setContentView(R.layout.activity_panelkey_learning_main);

		mModeLayout = (LinearLayout) findViewById(R.id.modeBtnLayout_panelkey);
		mControlLayout = (LinearLayout) findViewById(R.id.controlBtnLayout_panelkey);
		
		controlImageBtn = (Button) findViewById(R.id.controlImageButton_panelkey);
		modeImageBtn = (Button) findViewById(R.id.modeImageButton_panelkey);
		showTextView = (TextView) findViewById(R.id.show_panelkey);

		mainButtons[0] = (Button) findViewById(R.id.longpress_panelkey);
		mainButtons[1] = (Button) findViewById(R.id.start_panelkey);
		mainButtons[2] = (Button) findViewById(R.id.reset_panelkey);
		mainButtons[3] = (Button) findViewById(R.id.quit_panelkey);

		for (int i=0; i<mStudyBtnsId.length; i++) {
			mLearnButtons[i] = (Button) findViewById(mStudyBtnsId[i]);
		}

		disableAllButton(); // all the buttons disable,text color is white
		buildQuiltDialog(this);// dialog

	}

	private void initData() {
		controlImageBtn.setOnClickListener(this);
		modeImageBtn.setOnClickListener(this);

		for (int i = 0; i < mStudyBtnsId.length; i++) {
			mLearnButtons[i].setOnClickListener(this);
		}
		
		for (int k = 0; k < mainButtons.length; k++) {
			mainButtons[k].setOnClickListener(this);
		}

		DisplayMetrics displayMetrics = new DisplayMetrics();// 1920
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	}

	boolean findIdInKeyInfoList(int id, ArrayList<PanelKeyInfo> list) {
		if (list == null) {
			return false;
		}
		
		for (int i=0; i<list.size(); i++) {
			if (id == list.get(i).btnid) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void onClick(View view) {
		int id = view.getId();
		
		if (id == R.id.modeImageButton_panelkey) {
			setControlBtnInvisible();
			controlImageBtn.setBackgroundResource(R.drawable.control_btn_not);
			modeImageBtn.setBackgroundResource(R.drawable.control_btn_hot);
		}else if (id == R.id.controlImageButton_panelkey) {
			setModeBtnInvisible();
			controlImageBtn.setBackgroundResource(R.drawable.control_btn_hot);
			modeImageBtn.setBackgroundResource(R.drawable.control_btn_not);
		}else if (id == R.id.longpress_panelkey) {
			if (mLongPressType) {
				mLongPressType = false;
				mainButtons[0].setText(R.string.factory_KeyName_shortpress);
			} else {
				mLongPressType = true;
				mainButtons[0].setText(R.string.factory_KeyName_longpress);
			}
		}else if (id == R.id.start_panelkey) {
			mCurBtn = null;
			panelKeyArrayList.clear();
			panelKeyDataArrayList.clear();
			setLearningText(R.string.factory_studying, Color.WHITE);
			enableAllButton();
			setEnableBackgroundAllButton();
			showTextView.setText(R.string.factory_studying);
			PanelKeyMcu.getInstance(mContext).notifyMcuStudyStatus(PANELKEY_START_LEARN);
			Log.i(TAG, "onClick  R.id.start mCurBtn=null touchArrayList size:"+panelKeyArrayList.size());
		}else if (id == R.id.reset_panelkey) {
			mCurRestOrExitBtnID = R.id.reset_panelkey;
			((TextView) quiltDialog.findViewById(R.id.dialog_caption))
					.setText(R.string.factory_reset_title);
			quiltDialog.show();
		}else if (id == R.id.quit_panelkey) {
			mCurRestOrExitBtnID = R.id.quit_panelkey;
			((TextView) quiltDialog.findViewById(R.id.dialog_caption))
					.setText(R.string.factory_tocuh_key_save_info);
			quiltDialog.show();
		}else {
			if (!findIdInKeyInfoList(id, panelKeyArrayList)) {
				int index = getIndexByBtnId(id);
				mLearnButtons[index].setTextColor(Color.RED);
				setCurBtn(mLearnButtons[index]);
			}
		}		
	}

	private void buildQuiltDialog(Context contxt) {
		LayoutInflater factory = LayoutInflater.from(contxt);
		View editBTNameView = factory.inflate(R.layout.quilt_dialog_key_learning, null);
		quiltDialog = new Dialog(contxt);
		if (quiltDialog == null) {
			return;
		}

		quiltDialog.setContentView(editBTNameView);

		((Button) quiltDialog.findViewById(R.id.ok)).setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View arg0) {

				if (mCurRestOrExitBtnID == R.id.reset_panelkey) {

					disableAllButton();
					setDisableBackgroundAllButton();
					panelKeyArrayList.clear();
					panelKeyDataArrayList.clear();
					PanelKeyRW.clearData();
					
					mCurBtn = null;
					Log.i(TAG, "onClick  R.id.reset mCurBtn=null touchDataArrayList size:"+panelKeyDataArrayList.size());
					quiltDialog.dismiss();
					setLearningText(R.string.factory_studying, Color.WHITE);
					mLongPressType = false;
					mainButtons[0].setText(R.string.factory_KeyName_shortpress);
					
					PanelKeyMcu.getInstance(mContext).notifyMcuStudyStatus(PANELKEY_RESET_LEARN);

				} else if (mCurRestOrExitBtnID == R.id.quit_panelkey) {
					PanelKeyMcu.getInstance(mContext).notifyMcuPanelKeyInfo(panelKeyArrayList, keyupAdValue_channel0, keyupAdValue_channel1);
					PanelKeyRW.writeData(panelKeyArrayList, keyupAdValue_channel0, keyupAdValue_channel1);
					quiltDialog.dismiss();
					finish();
				}
				else
				{
					quiltDialog.dismiss();
				}
			}
		});
		
		((Button) quiltDialog.findViewById(R.id.cancel)).setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View arg0) {
			if(mCurRestOrExitBtnID == 0)
			{
				quiltDialog.dismiss();				
			}
			else
			{
				quiltDialog.dismiss();
				finish();
				}
			}
		});

	}

	public void onBackPressed() {

		mCurRestOrExitBtnID = R.id.quit_panelkey;
		((TextView) quiltDialog.findViewById(R.id.dialog_caption))
				.setText(R.string.factory_tocuh_key_save_info);

		if (!quiltDialog.isShowing()) {
			quiltDialog.show();
		}

	}

	private void setCurBtn(Button btn) { // KeyCode
		if(mCurBtn != btn && mCurBtn != null)
		{
			mCurBtn.setTextColor(Color.WHITE);
		}
		
		mCurBtn = btn;
		
		Log.i(TAG, "setCurBtn  mCurBtn="+mCurBtn+"，threadId: " + Thread.currentThread().getId());

		Util.curPanelKeyCode = getKeyByBtnId(btn.getId()); 
		
		setLearningText(R.string.factory_studying, Color.WHITE);
	}

	private void setLearningText(int resid, int color) {
		showTextView.setText(resid);
		showTextView.setTextColor(color);
	}

	private void disableAllButton() { 
		for (int i = 0; i < mStudyBtnsId.length; i++) {
			mLearnButtons[i].setEnabled(false);
			mLearnButtons[i].setTextColor(Color.WHITE);
		}
	}

	private void enableAllButton() { 
		for (int i = 0; i < mStudyBtnsId.length; i++) {
			mLearnButtons[i].setEnabled(true);
			mLearnButtons[i].setTextColor(Color.WHITE);
		}		
	}

	private void setEnableBackgroundAllButton() { 
		for (int i = 0; i < mStudyBtnsId.length; i++) {
			mLearnButtons[i].setBackgroundResource(R.drawable.btn_notpress);
		}
	}

	private void setDisableBackgroundAllButton() { 
		for (int i = 0; i < mStudyBtnsId.length; i++) {
			mLearnButtons[i].setBackgroundResource(R.drawable.keyname_btn_nor);
		}
	}

	private void setModeBtnInvisible() { 
		mModeLayout.setVisibility(View.GONE);
		mControlLayout.setVisibility(View.VISIBLE);
	}

	private void setControlBtnInvisible() { 
		mModeLayout.setVisibility(View.VISIBLE);
		mControlLayout.setVisibility(View.GONE);
	}

	public void onResume() {
		mbInSizeDialog = false;
		super.onResume();
		// setNetLinkSts(true);
	}

	public void onPause() {
		super.onPause();
		// setNetLinkSts(false);
	}
	@Override
	protected void onStop() {
		if(!mbInSizeDialog)
		{
			mMainHandler.removeMessages(Util.PUBLIC_MSG_ID);
			quiltDialog.dismiss();
			finish();
		}
		
		super.onStop();
	}
	
	public void onDestroy() {
		super.onDestroy();
		mMainHandler.removeMessages(Util.PUBLIC_MSG_ID);
		PanelKeyMcu.getInstance(mContext).notifyMcuStudyStatus(PANELKEY_END_LEARN);
	}
}
