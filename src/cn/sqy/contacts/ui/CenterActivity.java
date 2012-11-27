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
				CommonUtil.Toast(context, "����ʧ��!");
				break;
			case 0:
				dialog.dismiss();
				CommonUtil.Toast(context, "��ϵ�˱��ݳɹ�!");
				break;
			case 1:
				dialog.dismiss();
				CommonUtil.Toast(context, "��ϵ�˻ָ��ɹ�!");
				break;
			case 2:
				dialog.dismiss();
				CommonUtil.Toast(context, "SIM������ɹ�!");
				break;
			case 3:
				dialog.dismiss();
				CommonUtil.Toast(context, "��Ϣ���ݳɹ�!");
				break;
			case 4:
				dialog.dismiss();
				CommonUtil.Toast(context, "��Ϣ�ָ��ɹ�!");
				break;
			case 5:
				dialog.dismiss();
				CommonUtil.Toast(context, "ϵͳ��ϵ�˵���ɹ�!");
				break;
			}

		}
	};
	/** gridViewBarͼƬ **/
	private int[] gridView_image_array = { R.drawable.contact_backup,
			R.drawable.contact_restore, R.drawable.sim_load,
			R.drawable.msg_backup, R.drawable.msg_restore,
			R.drawable.syscontact_load };
	/** gridViewBar���� **/
	private String[] gridView_name_array = { "��ϵ�˱���", "�ָ���ϵ��", "SIM������",
			"��Ϣ����", "��Ϣ�ָ�", "ϵͳ��ϵ�˵���" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_center);

	}

	private void init() {
	}

	/**
	 * ����SD���ľ���·��
	 * 
	 * @return
	 */
	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// ��ȡ��Ŀ¼
		}
		return sdDir.toString();
	}

	/**
	 * GridView��adapter
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
