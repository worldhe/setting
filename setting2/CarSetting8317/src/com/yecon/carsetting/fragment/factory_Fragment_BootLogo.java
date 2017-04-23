package com.yecon.carsetting.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yecon.carsetting.R;
import com.yecon.carsetting.view.LogoGridView;
import com.yecon.logo.YeconLogo;

public class factory_Fragment_BootLogo extends DialogFragment implements OnClickListener,
		OnItemClickListener {

	private Context mContext;
	
	private LinearLayout mLayoutDefault;
	private LinearLayout mLayoutCustom;
	
	TextView TV_Ok, TV_Cancel, TV_User;
	GridView gridview;
	
	LogoGridView mLogoGridView;
	
	Bitmap mBitLogo = null;
	private int index = -1;
	private static final String LogoPicName = "/logo.bmp";
	private static final String LOGO_ROOT_PATH = "/mnt/sdcard";
	private Integer[] mImageLogoIds = { R.drawable.logo_1, R.drawable.logo_2, R.drawable.logo_3,
			R.drawable.logo_4, R.drawable.logo_5, R.drawable.logo_6, R.drawable.logo_7,
			R.drawable.logo_8, R.drawable.logo_9, R.drawable.logo_10, R.drawable.logo_11,
			R.drawable.logo_12, R.drawable.logo_13, R.drawable.logo_14, };

	private void initData() {
		mContext = getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setStyle(DialogFragment.STYLE_NO_FRAME, 0);
		initData();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// return super.onCreateView(inflater, container, savedInstanceState);
		View mRootView = inflater.inflate(R.layout.factory_fragment_dvdlogo, container, false);
		initView(mRootView);
		mLayoutDefault = (LinearLayout)mRootView.findViewById(R.id.default_logo_layout);
		mLayoutCustom = (LinearLayout)mRootView.findViewById(R.id.custom_logo_layout);
		mLayoutCustom.setVisibility(View.GONE);
		mLayoutDefault.setVisibility(View.VISIBLE);
		mLogoGridView = (LogoGridView) mRootView.findViewById(R.id.GridView02);
		List<String> logoDirs = new ArrayList<String>();
		List<String> logoSuffixs = new ArrayList<String>();
		logoDirs.add("mnt/sdcard/logo");
		logoDirs.add("mnt/ext_sdcard1/logo");
		logoDirs.add("mnt/ext_sdcard2/logo");
		logoDirs.add("mnt/udisk1/logo");
		logoDirs.add("mnt/udisk2/logo");
		logoDirs.add("mnt/udisk3/logo");
		logoDirs.add("mnt/udisk4/logo");
		logoDirs.add("mnt/udisk5/logo");
		logoSuffixs.add(".bmp");
		logoSuffixs.add(".png");
		logoSuffixs.add(".jpg");
		mLogoGridView.setFilter(logoDirs, logoSuffixs);
		mLogoGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "长按设置此图片为开机LOGO", Toast.LENGTH_SHORT).show();
			}
			
		});
		mLogoGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//Toast.makeText(mContext, mLogoGridView.getFilePath(position), Toast.LENGTH_SHORT).show();
				Bitmap bmp = BitmapFactory.decodeFile(mLogoGridView.getFilePath(position));
				saveLogoPic(bmp);
				if (YeconLogo.ChangeLogo(LOGO_ROOT_PATH + LogoPicName) == 0) {
					Toast.makeText(mContext, "设置成功!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, "设置失败!", Toast.LENGTH_SHORT).show();
				}
				return false;
			}
			
		});
		return mRootView;
	}

	private void initView(View rootView) {

		gridview = (GridView) rootView.findViewById(R.id.GridView01);
		gridview.setOnItemClickListener(this);
		gridview.setAdapter(new ImageAdapter(mContext));
		ShapeDrawable mDrawable;
		mDrawable = new ShapeDrawable(new RectShape());
		mDrawable.getPaint().setColor(0xff00deff);
		mDrawable.getPaint().setStrokeWidth(3);
		mDrawable.getPaint().setStyle(Paint.Style.STROKE);
		gridview.setSelector(mDrawable);
		gridview.setSelection(0);

		TV_Ok = (TextView) rootView.findViewById(R.id.logo_ok);
		TV_Cancel = (TextView) rootView.findViewById(R.id.logo_cancel);
		TV_User = (TextView) rootView.findViewById(R.id.logo_user);
		TV_Ok.setOnClickListener(this);
		TV_Cancel.setOnClickListener(this);
		TV_User.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.logo_ok:
			if (index >= 0) {
				YeconLogo.ChangeLogo(LOGO_ROOT_PATH + LogoPicName);
				// Toast.makeText(getApplicationContext(),
				// R.string.alert_set_success, Toast.LENGTH_SHORT).show();
			} else {
				// Toast.makeText(getApplicationContext(),
				// R.string.alert_set_fail, Toast.LENGTH_SHORT).show();
			}
			dismiss();
			break;
		case R.id.logo_cancel:
			dismiss();
			break;
		case R.id.logo_user:
//			Intent mIntent = new Intent(Intent.ACTION_MAIN);
//			mIntent.setComponent(new ComponentName("com.autochips.filebrowser",
//					"com.autochips.filebrowser.FilebrowserActivity"));
//			mIntent.putExtra("IsLogoSetting", true);
//			startActivity(mIntent);
			mLayoutCustom.setVisibility(View.VISIBLE);
			mLayoutDefault.setVisibility(View.GONE);
			// YeconLogo.ChangeLogo("/mnt/ext_sdcard2/picture/Tulips.bmp");
			break;
		default:
			break;
		}

	}

	public class ImageAdapter extends BaseAdapter {
		// ����Context
		private Context mContext;
		// ������������ ��ͼƬԴ
		private Integer[] mImageIds = { R.drawable.logo1, R.drawable.logo2, R.drawable.logo3,
				R.drawable.logo4, R.drawable.logo5, R.drawable.logo6, R.drawable.logo7,
				R.drawable.logo8, R.drawable.logo9, R.drawable.logo10, R.drawable.logo11,
				R.drawable.logo12, R.drawable.logo13, R.drawable.logo14, };

		public ImageAdapter(Context c) {
			mContext = c;
		}

		// ��ȡͼƬ�ĸ���
		public int getCount() {
			return mImageIds.length;
		}

		// ��ȡͼƬ�ڿ��е�λ��
		public Object getItem(int position) {
			return position;
		}

		// ��ȡͼƬID
		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) {
				// ��ImageView������Դ
				imageView = new ImageView(mContext);
				// ���ò��� ͼƬ��ʾ
				imageView.setLayoutParams(new GridView.LayoutParams(230, 140));
				// ������ʾ��������
				imageView.setScaleType(ImageView.ScaleType.CENTER);
			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(mImageIds[position]);
			// imageView.setBackgroundResource(R.xml.selector_gridview_state);
			return imageView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		index = arg2;
		mBitLogo = ((BitmapDrawable) getResources().getDrawable(mImageLogoIds[index])).getBitmap();
		saveLogoPic(mBitLogo);
	}

	public void savePNG() {
		File f = new File(LOGO_ROOT_PATH + LogoPicName); // "/mnt/ext_sdcard2/picture"
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			mBitLogo.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void saveLogoPic(Bitmap bitmap) {

		if (bitmap != null) {
			int w = bitmap.getWidth(), h = bitmap.getHeight();
			int[] pixels = new int[w * h];
			bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

			byte[] rgb = addBMP_RGB_888(pixels, w, h);
			byte[] header = addBMPImageHeader(rgb.length);
			byte[] infos = addBMPImageInfosHeader(w, h);

			byte[] buffer = new byte[54 + rgb.length];
			System.arraycopy(header, 0, buffer, 0, header.length);
			System.arraycopy(infos, 0, buffer, 14, infos.length);
			System.arraycopy(rgb, 0, buffer, 54, rgb.length);

			File f = new File(LOGO_ROOT_PATH + LogoPicName);
			if (f.exists()) {
				f.delete();
			}

			try {
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(buffer);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// BMP�ļ�ͷ
	private byte[] addBMPImageHeader(int size) {
		byte[] buffer = new byte[14];
		buffer[0] = 0x42;
		buffer[1] = 0x4D;
		buffer[2] = (byte) (size >> 0);
		buffer[3] = (byte) (size >> 8);
		buffer[4] = (byte) (size >> 16);
		buffer[5] = (byte) (size >> 24);
		buffer[6] = 0x00;
		buffer[7] = 0x00;
		buffer[8] = 0x00;
		buffer[9] = 0x00;
		buffer[10] = 0x36;
		buffer[11] = 0x00;
		buffer[12] = 0x00;
		buffer[13] = 0x00;
		return buffer;
	}

	// BMP�ļ���Ϣͷ
	private byte[] addBMPImageInfosHeader(int w, int h) {
		byte[] buffer = new byte[40];
		buffer[0] = 0x28;
		buffer[1] = 0x00;
		buffer[2] = 0x00;
		buffer[3] = 0x00;
		buffer[4] = (byte) (w >> 0);
		buffer[5] = (byte) (w >> 8);
		buffer[6] = (byte) (w >> 16);
		buffer[7] = (byte) (w >> 24);
		buffer[8] = (byte) (h >> 0);
		buffer[9] = (byte) (h >> 8);
		buffer[10] = (byte) (h >> 16);
		buffer[11] = (byte) (h >> 24);
		buffer[12] = 0x01;
		buffer[13] = 0x00;
		buffer[14] = 0x18;
		buffer[15] = 0x00;
		buffer[16] = 0x00;
		buffer[17] = 0x00;
		buffer[18] = 0x00;
		buffer[19] = 0x00;
		buffer[20] = 0x00;
		buffer[21] = 0x00;
		buffer[22] = 0x00;
		buffer[23] = 0x00;
		buffer[24] = (byte) 0xE0;
		buffer[25] = 0x01;
		buffer[26] = 0x00;
		buffer[27] = 0x00;
		buffer[28] = 0x02;
		buffer[29] = 0x03;
		buffer[30] = 0x00;
		buffer[31] = 0x00;
		buffer[32] = 0x00;
		buffer[33] = 0x00;
		buffer[34] = 0x00;
		buffer[35] = 0x00;
		buffer[36] = 0x00;
		buffer[37] = 0x00;
		buffer[38] = 0x00;
		buffer[39] = 0x00;
		return buffer;
	}

	private byte[] addBMP_RGB_888(int[] b, int w, int h) {
		int len = b.length;
		System.out.println(b.length);
		byte[] buffer = new byte[w * h * 3];
		int offset = 0;
		for (int i = len - 1; i >= w; i -= w) {
			// DIB�ļ���ʽ���һ��Ϊ��һ�У�ÿ�а�������˳��
			int end = i, start = i - w + 1;
			for (int j = start; j <= end; j++) {
				buffer[offset] = (byte) (b[j] >> 0);
				buffer[offset + 1] = (byte) (b[j] >> 8);
				buffer[offset + 2] = (byte) (b[j] >> 16);
				offset += 3;
			}
		}
		return buffer;
	}

}
