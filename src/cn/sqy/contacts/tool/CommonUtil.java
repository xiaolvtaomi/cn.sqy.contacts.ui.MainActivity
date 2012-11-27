package cn.sqy.contacts.tool;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import cn.sqy.contacts.R;
import cn.sqy.contacts.model.Email;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CommonUtil {
	
	/**
	 * 字符串如果是空或者无效就返回false
	 * @param temp
	 * @return
	 */
	public static boolean isAvailable(String temp){
		boolean b = true;
		if(temp==null || temp.trim().equals("")){
			b = false;
		}
		return b;
	}
	
	/**
	 * 创建本地SD卡上一个目录
	 * @param foldername
	 */
	public static void createLocalFolder(String foldername){
		//判断本地有这个路径没
		String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
		File file = new File(SD_PATH+File.separator+foldername);
		//没有创建
		if(!file.exists()){
			file.mkdir();
		}
	}

	
	/**
	 * 日志信息
	 * 
	 * @param tag
	 *            标志
	 * @param funcName
	 *            打日志的函数名
	 * @param msg
	 *            日志信息内容
	 * @param type
	 *            日志类型
	 */
	public static void Log(String tag, String funcName, String msg, char type) {
		switch (type) {
		case 'e':
			Log.e(tag, funcName + "===>" + msg);
			break;
		case 'v':
			Log.v(tag, funcName + "===>" + msg);
			break;
		case 'i':
			Log.i(tag, funcName + "===>" + msg);
			break;
		case 'd':
			Log.d(tag, funcName + "===>" + msg);
			break;
		default:
			Log.d(tag, funcName + "===>" + msg);
			break;
		}
	}

	/**
	 * 显示提示信息
	 * 
	 * @param context
	 *            要显示信息的当前实例
	 * @param msg
	 *            信息内容
	 */
	public static void Toast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 返回当前系统日期
	 * 
	 * @return
	 */
	public static String getNowDate() {
		String date = null;
		Calendar calendar = Calendar.getInstance();
		int mYear = calendar.get(Calendar.YEAR);
		int mMonth = calendar.get(Calendar.MONTH);
		int mDay = calendar.get(Calendar.DAY_OF_MONTH);
		mMonth++;
		if (mMonth < 10)
			date = mYear + "-0" + mMonth;
		else
			date = mYear + "-" + mMonth;
		if (mDay < 10)
			date = date + "-0" + mDay;
		else
			date = date + "-" + mDay;
		return date;
	}

	/**
	 * 返回当前系统时间
	 * 
	 * @return
	 */
	public static String getNowTime() {
		String time = null;
		Calendar calendar = Calendar.getInstance();
		int mHour = calendar.get(Calendar.HOUR_OF_DAY);
		int mMinute = calendar.get(Calendar.MINUTE);
		int mSecond = calendar.get(Calendar.SECOND);

		if (mHour < 10)
			time = "0" + mHour;
		else
			time = mHour + "";
		if (mMinute < 10)
			time = time + ":0" + mMinute;
		else
			time = time + ":" + mMinute;
		if (mSecond < 10)
			time = time + ":0" + mSecond;
		else
			time = time + ":" + mSecond;
		return time;
	}

	/**
	 * 打电话
	 * 
	 * @param num
	 *            电话号码
	 */
	public static void dial(Context context, String num) {
		if (num.equals("无")) {
			CommonUtil.Toast(context, "该联系人无号码!");
			return;
		}
		Intent phoneIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ num));
		context.startActivity(phoneIntent);
		CommonUtil.Toast(context, "正在拨打" + num);
	}

	/**
	 * 发邮件
	 * 
	 * @param emails
	 *            邮箱地址
	 */
	public static void sendEmail(Context context, List<Email> emails) {
		if (emails == null || emails.size() < 1) {
			CommonUtil.Toast(context, "该联系人无邮箱!");
			return;
		}
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("plain/text");
		String[] emailReciver = new String[] { emails.get(0).getEmailAcount() };
		String emailSubject = "你有一条短信";
		String emailBody = "....";

		// 设置邮件默认地址
		email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
		// 设置邮件默认标题
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
		// 设置要默认发送的内容
		email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
		// 调用系统的邮件系统
		context.startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
	}
	
	public static void show_toast(Context context, String str, String type) {
		// Toast.makeText(context, str, 1).show();
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.toast_layout, null);
		if (type.endsWith("blue")) {
			//1:正常情况下
			view.setBackgroundResource(R.drawable.app_btn_blue);
		} else if (type.endsWith("green")) {
			//2:校验、非空提示
			view.setBackgroundResource(R.drawable.app_btn_green);
		} else if (type.endsWith("orange")) {
			//3:在警告、异常的情况下使用
			view.setBackgroundResource(R.drawable.app_btn_orange);
		}
		TextView textView = (TextView) view.findViewById(R.id.toast_info_msg);
		textView.setText(str);
		Toast toast = new Toast(context);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(view);
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.show();
	}
}
