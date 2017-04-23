/**   
 * Copyright © 2016 深圳益光实业有限公司. All rights reserved.
 * 
 * @Title: LogoAdapterView.java 
 * @Prject: UnitView
 * @Package: com.Jincy.CustomView 
 * @Description: TODO
 * @author: hzGuo   
 * @date: 2016年9月7日 上午10:10:15 
 * @version: V1.0   
 */
package com.yecon.carsetting.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

/** 
 * @ClassName: LogoGridView 
 * @Description: TODO
 * @author: hzGuo
 * @date: 2016年9月7日 上午10:10:15  
 */
public class LogoGridView extends GridView {
	
	protected static final String TAG = "LogoGridView";

	private LogoAdapter mAdapter;

	private Context mContext;
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action;
			if (intent != null) {
				action = intent.getAction();
				Log.e(TAG, "action:" + action);
				if (action != null) {
					updateAdapter();
				}
			}
		}
		
	};
	
	// logo存放目录
	public String def_dirs[] = new String[] {
		"/mnt/sdcard"
	};
	
	public List<String> lsDir;
	
	// logo后缀名
	private final String def_suffix[] = new String[] {
		".bmp"
	};
	
	public List<String> lsSuffix;
	
	/** 
	 * @Title:LogoGridView
	 * @Description:TODO 
	 * @param context
	 * @param attrs 
	 */
	public LogoGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		updateAdapter();
	}
	
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		filter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		filter.addDataScheme("file");
		mContext.registerReceiver(mBroadcastReceiver, filter);
	}


	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		mContext.unregisterReceiver(mBroadcastReceiver);
	}

	/**
	 * @Title: setFilter 
	 * @Description: 刷新logo列表
	 * @param logoDirs 存放logo的目录
	 * @param logoSuffixs logo文件后缀名
	 */
	public void setFilter(List<String> logoDirs, List<String> logoSuffixs) {
		lsDir = logoDirs;
		lsSuffix = logoSuffixs;
		updateAdapter();
	}
	
	public void updateAdapter() {
		mAdapter = new LogoAdapter();
		setAdapter(mAdapter);
		invalidate();
	}
	
	public String getFilePath(int pos) {
		try {
			return mAdapter.getFilePath(pos);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("TAG", "get file path failed!!!");
		}
		return "";
	}
	
	private class LogoAdapter extends BaseAdapter {
		
		public List<String> mList; 
		
		/** 
		 * @Title:LogoGridView.LogoAdapter
		 * @Description:TODO  
		 */
		public LogoAdapter() {
			// TODO Auto-generated constructor stub
			mList = new ArrayList<String>();
			if (lsDir == null || lsDir.isEmpty()) {
				for (String dir : def_dirs) {
					traverseFolder(dir, mList);
				}
			} else {
				for (String dir : lsDir) {
					traverseFolder(dir, mList);
				}
			}
			if (mList.isEmpty()) {
				Toast.makeText(mContext, "请将LOGO文件放到磁盘根目录下的logo文件夹!", Toast.LENGTH_SHORT).show();
			}
		}
		
		/* (non Javadoc) 
		 * @Title: getCount
		 * @Description: TODO
		 * @return 
		 * @see android.widget.Adapter#getCount() 
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		/* (non Javadoc) 
		 * @Title: getItem
		 * @Description: TODO
		 * @param position
		 * @return 
		 * @see android.widget.Adapter#getItem(int) 
		 */
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		/* (non Javadoc) 
		 * @Title: getItemId
		 * @Description: TODO
		 * @param position
		 * @return 
		 * @see android.widget.Adapter#getItemId(int) 
		 */
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		/* (non Javadoc) 
		 * @Title: getView
		 * @Description: TODO
		 * @param position
		 * @param convertView
		 * @param parent
		 * @return 
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup) 
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView;
			if (convertView == null) {
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(230, 140));
				imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			} else {
				imageView = (ImageView) convertView;
			}
			imageView.setImageURI(Uri.parse("file://" + mList.get(position)));
			return imageView;
		}
		
		public String getFilePath(int pos) {
			return mList.get(pos);
		}
		
		///////////////////////////////////
		// 单个文件夹遍历时间
		private final int MAX_TRAVEL_TIME = 5 * 1000;
		
		/*
		 * @param1: String path : 扫描的文件夹
		 * @param2:	List<String> lsFile : 用于存放扫描到文件的列表
		 */
		public void traverseFolder(String path, List<String> lsFile) {
			try {
				TravelThread thread = new TravelThread(path);
				thread.start();
				try {
					thread.join(MAX_TRAVEL_TIME);
					thread.interrupt();
				} catch (Exception e) {
					e.printStackTrace();
				}
				lsFile.addAll(thread.lsFile);
				thread = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public class TravelThread extends Thread {

			private String path;
			private List<String> lsFile = new ArrayList<String>();
			
			TravelThread(String path) {
				this.path = path;
			}
			
			@SuppressLint("DefaultLocale")
			private boolean isLogoFile(String file) {
				if (file != null) {
					if (lsSuffix == null || lsSuffix.isEmpty()) {
						for (String suf : def_suffix) {
							if (file.toLowerCase().endsWith(suf)) {
								return true;
							}
						}
					} else {
						for (String suf : lsSuffix) {
							if (file.toLowerCase().endsWith(suf)) {
								return true;
							}
						}
					}
				}
				return false;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				long lStart = SystemClock.uptimeMillis();
				Log.i(TAG, "++traversing folder++ " + path);
				try {
					File dir = new File(path);
					if (dir.exists()) {
						File[] files = dir.listFiles();
						if (files != null) {
							if (files.length != 0) {
								try {
									for (File file : files) {
										try {
											String filepath = file.getAbsolutePath();
											filepath.trim();
											if (filepath.isEmpty()) {
												continue;
											}
											if (!file.isDirectory()) {
												if (isLogoFile(filepath)) {
													lsFile.add(filepath);
												}
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Log.i(TAG, "--traversing folder-- " + path + " file:" + lsFile.size() + " cost:" + (SystemClock.uptimeMillis() - lStart));
			}
		}
		
	}
}
