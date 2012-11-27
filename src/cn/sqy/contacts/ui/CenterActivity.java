package cn.sqy.contacts.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import cn.sqy.contacts.R;
import cn.sqy.contacts.tool.CommonUtil;

public class CenterActivity extends Activity {
	private Context context;
	private GridView gridViewCenter;
	private LayoutInflater inflater;
	private AlertDialog dialog;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch ((Integer) msg.obj) {
			case -1:
				dialog.dismiss();
				CommonUtil.Toast(context, "操作失败!");
				break;
			case 0:
				dialog.dismiss();
				CommonUtil.Toast(context, "联系人备份成功!");
				break;
			case 1:
				dialog.dismiss();
				CommonUtil.Toast(context, "联系人恢复成功!");
				break;
			case 2:
				dialog.dismiss();
				CommonUtil.Toast(context, "SIM卡导入成功!");
				break;
			case 3:
				dialog.dismiss();
				CommonUtil.Toast(context, "信息备份成功!");
				break;
			case 4:
				dialog.dismiss();
				CommonUtil.Toast(context, "信息恢复成功!");
				break;
			case 5:
				dialog.dismiss();
				CommonUtil.Toast(context, "系统联系人导入成功!");
				break;
			}

		}
	};
	/** gridViewBar图片 **/
	private int[] gridView_image_array = { R.drawable.contact_backup,
			R.drawable.contact_restore, R.drawable.sim_load,
			R.drawable.msg_backup, R.drawable.msg_restore,
			R.drawable.syscontact_load };
	/** gridViewBar文字 **/
	private String[] gridView_name_array = { "联系人备份", "恢复联系人", "SIM卡导入",
			"信息备份", "信息恢复", "系统联系人导入" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_center);

	}

	private void init() {
	}

	/**
	 * 返回SD卡的绝对路径
	 * 
	 * @return
	 */
	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();
	}

	/**
	 * GridView的adapter
	 * 
	 * @param nameArray
	 * @param resourceArray
	 * @return
	 */
	private SimpleAdapter getAdapter(String[] nameArray, int[] resourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < nameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", resourceArray[i]);
			map.put("itemText", nameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_menu1, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
	}
}
