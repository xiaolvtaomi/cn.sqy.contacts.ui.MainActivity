package cn.sqy.contacts.ui;

import java.io.File;

import android.os.Environment;

public class ConstantBean {
	public static final String ROOT = "http://hotel.yingjiesheng.com/";
	public static final String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String FOLDER_PAHT = SDPATH + File.separator +"yjs_pics_folder";
	
}
