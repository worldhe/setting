package com.yecon.carsetting.unitl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;

/**
 * int channel;		//0-1
	int advalue;		//0-255
	int keyvalue;		//
	int longpress;			//if 1, then long press
	int btnid;
 * format
 * totalnums 2 byte
 * 
 * @author xuhonghai
 *
 */
public class PanelKeyRW {
	final static String mFilename = "/usr2/panelKeyData.dat";
	static ArrayList<PanelKeyInfo> mPanelKeyInfo = new ArrayList<PanelKeyInfo>();
	static int mChannel0_ad=255;
	static int mChannel1_ad=255;
	
	public static void onFirstPowerOn(Context context) {
		mPanelKeyInfo.clear();
		boolean ret = readData(mPanelKeyInfo);
		if (ret && mPanelKeyInfo.size() > 0) {
			PanelKeyMcu.getInstance(context).notifyMcuPanelKeyInfo(mPanelKeyInfo, mChannel0_ad, mChannel1_ad);
		}
	}
	
	/**
	 * del /k2config/panelKeyData.dat
	 */
	public static void clearData() {
		byte[] buff = new byte[16];
		Arrays.fill(buff, (byte) 0);
		try {
			FileOutputStream fout;
			fout = new FileOutputStream(mFilename);
			fout.write(buff);   
			fout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean readData(ArrayList<PanelKeyInfo> infos) {
		try {
			FileInputStream fin = new FileInputStream(mFilename);
			int length = fin.available();
			if (length >= 20) {
				byte[] buff = new byte[length];
				fin.read(buff);
				fin.close();
				
				int size = (int)(buff[0]&0xff);
				mChannel0_ad = (int)(buff[1]&0xff);
				mChannel1_ad = (int)(buff[2]&0xff);
				
				infos.clear();
				int j=16;
				for (int i=0; i<size; i++) {
					PanelKeyInfo info = new PanelKeyInfo();
					info.channel = (buff[j++]&0xff);
					info.advalue = (buff[j++]&0xff);
					info.keyvalue = (buff[j++]&0xff);
					info.longpress = (buff[j++]&0xff);
					infos.add(info);
				}
				
				return true;
			}else {
				fin.close();
				return false;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean writeData(ArrayList<PanelKeyInfo> infos, int channel0_ad, int channel1_ad) {
		if (infos == null) {
			return false;
		}
		
		if (infos.size() == 0) {
			return false;
		}
		
		int size = infos.size();
		byte[] buff = new byte[size*4+16];
		Arrays.fill(buff, (byte) 0);
		buff[0] = (byte)size;
		buff[1] = (byte) channel0_ad;
		buff[2] = (byte) channel1_ad;
		//buff3-buff15 can save other data, now is 0
		
		int j=16;
		for (int i=0; i<size; i++) {
			buff[j++] = (byte) infos.get(i).channel;
			buff[j++] = (byte) infos.get(i).advalue;			
			buff[j++] = (byte) infos.get(i).keyvalue;
			buff[j++] = (byte) infos.get(i).longpress;
		}
		
		try {
			FileOutputStream fout;
			fout = new FileOutputStream(mFilename);
			fout.write(buff);   
			fout.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
}
