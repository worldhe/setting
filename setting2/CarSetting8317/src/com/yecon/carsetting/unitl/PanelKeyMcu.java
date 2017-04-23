package com.yecon.carsetting.unitl;

import java.util.ArrayList;

import android.content.Context;
import android.mcu.McuListener;
import android.mcu.McuManager;
import android.os.RemoteException;

public class PanelKeyMcu {
	private static PanelKeyMcu instance = null;
	private McuManager mObjMcuMananger=null;
	private McuListener mMcuListener=null;
	
	public static PanelKeyMcu getInstance(Context context) {
		if (instance == null) {
			synchronized (PanelKeyMcu.class) {
				if (instance == null) {
					instance = new PanelKeyMcu(context);
				}
			}
		}
		
		return instance;
	}
	
	public PanelKeyMcu(Context context) {
		// TODO Auto-generated constructor stub
		mObjMcuMananger = (McuManager)context.getSystemService(Context.MCU_SERVICE);
	}
	
	/**
	 * status=1   start learn
	 * status=2   reset
	 * status=3   learn over
	 * @param status
	 */
	public void notifyMcuStudyStatus(int status) {
		if (mObjMcuMananger != null) {
			byte[] data = new byte[1];
			
			data[0] = (byte)status;
			
			try {
				mObjMcuMananger.RPC_SendExtendCmd(0xE0, data, 1);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * send all study key ad value to mcu
	 * @param panelKeyInfo
	 */
	public void notifyMcuPanelKeyInfo(ArrayList<PanelKeyInfo> panelKeyInfo, int channel0_ad, int channel1_ad) {
		if (panelKeyInfo != null && panelKeyInfo.size() > 0) {
			int len = panelKeyInfo.size();
			byte[] data = new byte[4*len+2];
			
			data[0] = (byte) channel0_ad;
			data[1] = (byte) channel1_ad;
			
			int i=0;
			int j=2;
			for (i=0; i<len; i++) {
				data[j++] = (byte)panelKeyInfo.get(i).channel;
				data[j++] = (byte)panelKeyInfo.get(i).advalue;
				data[j++] = (byte)panelKeyInfo.get(i).keyvalue;
				data[j++] = (byte)panelKeyInfo.get(i).longpress;
			}
			
			if (mObjMcuMananger != null) {
				try {
					mObjMcuMananger.RPC_SendExtendCmd(0xE1, data, 4*len+2);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
	}
	
	public void setCallback(McuListener listener) {
		if (mObjMcuMananger != null) {
			try {
				mObjMcuMananger.RPC_RequestMcuInfoChangedListener(listener);
				mMcuListener = listener;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void removeCallback() {
		if (mObjMcuMananger != null) {
			if (mMcuListener != null) {
				try {
					mObjMcuMananger.RPC_RemoveMcuInfoChangedListener(mMcuListener);
					mMcuListener = null;
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
	}
}
