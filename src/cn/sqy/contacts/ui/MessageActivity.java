package cn.sqy.contacts.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import cn.sqy.contacts.R;
import cn.sqy.contacts.model.Contact;
import cn.sqy.contacts.model.Msg;
import cn.sqy.contacts.tool.CommonUtil;
import cn.sqy.contacts.tool.ContantsUtil;
import cn.sqy.contacts.tool.ImageTools;

public class MessageActivity extends Activity {
	private ImageButton imbNewMsg;
	private Context context;
	private List<Msg> msgs;// ����ÿ����ϵ�����µ�һ������
	private LayoutInflater inflater;
	private ListView lsvMsg;
	// ��Ϣ����
	private Menu myMenu;// �˵�
	private boolean isMessageMgr = false;// true:��Ϣ�������,false:��Ϣ�б����
	private Map<Integer, Boolean> isChecked;// �����Ϣ��ÿ���CheckBox�Ƿ�ѡ��
	private int nowPosMenu;// ��ǵ�ǰʹ�������Ĳ˵������Position
	private int ckbCount = 0;// ��ǰѡ����Ϣ����
	private boolean flag = false;// ��ǵ�ǰ������Ի��б�ǰ�Ƿ���δ������
	private AlertDialog dialog;

	private ComposeObserver composeObserver = null;// ϵͳ���ݿ����

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if ((Integer) msg.obj == 1) {
				CommonUtil.Toast(context, "����ɾ���Ի��ɹ�!");
				toNotMessageMgrMenu();// ��ת��������
			}
			if ((Integer) msg.obj == 2) {
				refreshMsg();
				CommonUtil.Toast(context, "ɾ���Ի��ɹ�!");
			}
			dialog.dismiss();
		}

	};

	public static final int REQUEST_MSG_EDIT = 1;
	public static final int REQUEST_MSG_NEW_CONTACT = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);

		init();// ��ʼ��
		registerForContextMenu(lsvMsg);
		registerSMSObserver();// ע��ϵͳ���ݿ����,���Ƿ�������Ϣ

	}

	/**
	 * Activity�ĳ�ʼ��
	 */
	private void init() {
	}

	/**
	 * ˢ�½���
	 */
	private void refreshMsg() {
		msgs = new ArrayList<Msg>();
		MsgUiAdapter adapter = new MsgUiAdapter();
	}

	/**
	 * �����˵�
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		CommonUtil.Log("menu", "MessageActivity", "onCreateOptionsMenu", 'i');
		menu.add(1, 1, 1, "��������").setIcon(R.drawable.menu_batch_mgr);
		menu.add(1, 2, 2, "ȫѡ").setIcon(R.drawable.menu_batch_all)
				.setEnabled(false);
		menu.add(1, 3, 3, "ȫ��").setIcon(R.drawable.menu_batch_cancel)
				.setEnabled(false);
		menu.add(1, 4, 4, "�Ѷ�").setIcon(R.drawable.menu_all_read)
				.setEnabled(false);
		menu.add(1, 5, 5, "����ɾ��").setIcon(R.drawable.menu_batch_delete)
				.setEnabled(false);
		myMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * ���������Ĳ˵�
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		CommonUtil.Log("menu", "MessageActivity", "onCreateContextMenu", 'i');
		menu.add(0, 10, 0, "����绰");
		menu.add(0, 11, 1, "ɾ��");
	}

	/*
	 * ��Ӧ�����Ĳ˵��Ͳ˵�
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();// ��ŵ�ǰ��������Ϣ
		CommonUtil.Log("menu", "ContactActivity", "onMenuItemSelected", 'i');
		if (featureId == 6)// �����������Ĳ˵�
			nowPosMenu = info.position;
		switch (item.getItemId()) {
		case 1:// ��������
			if (isMessageMgr) {
				toNotMessageMgrMenu();
			} else {
				toIsMessageMgrMenu();
			}
			break;
		case 2:// ȫѡ
			for (Msg msg : msgs)
				isChecked.put(msg.getThreadId(), true);
			refreshMsg();
			ckbCount = isChecked.size();
			break;
		case 3:// ȫ��
			for (Msg msg : msgs)
				isChecked.put(msg.getThreadId(), false);
			refreshMsg();
			ckbCount = 0;
			break;
		case 4:// �Ѷ�
			break;
		case 5:// ����ɾ��
			break;
		case 10:// ��绰
			String num = msgs.get(nowPosMenu).getAddress();
			CommonUtil.dial(context, num);
			break;
		case 11:// ɾ���Ի�

			break;
		}
		return true;
	}

	private void toIsMessageMgrMenu() {
		isMessageMgr = true;
		myMenu.getItem(0).setTitle("ȡ������");
		refreshMsg();
		myMenu.getItem(1).setEnabled(true);
		myMenu.getItem(2).setEnabled(true);
		myMenu.getItem(3).setEnabled(true);
		myMenu.getItem(4).setEnabled(true);
		for (Msg msg : msgs)
			isChecked.put(msg.getThreadId(), false);
	}

	private void toNotMessageMgrMenu() {
		isMessageMgr = false;
		myMenu.getItem(0).setTitle("��������");
		refreshMsg();
		myMenu.getItem(1).setEnabled(false);
		myMenu.getItem(2).setEnabled(false);
		myMenu.getItem(3).setEnabled(false);
		myMenu.getItem(4).setEnabled(false);
	}

	/**
	 * �ص�ˢ�½���
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_MSG_NEW_CONTACT
				&& resultCode == NewContactActivity.RESULT_BTN_SAVE)
			refreshMsg();
		if (requestCode == REQUEST_MSG_EDIT
				&& resultCode == MsgEditActivity.RESULT_MSG_CHANGED)
			refreshMsg();
		if (resultCode == NewMsgActivity.RESULT_SEND_MESG_SUCCEED)
			refreshMsg();
	}

	/**
	 * ����ϵͳ����
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		CommonUtil.Log("sqy", "onKeyDown+++", "onKeyDown", 'i');
		if (keyCode == KeyEvent.KEYCODE_BACK && isMessageMgr) {
			toNotMessageMgrMenu();
			CommonUtil.Log("menu", "keyCode", 222 + "", 'i');
			return true;
		}
		CommonUtil.Log("menu", "keyCode", "111", 'i');
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * �Զ���Adapter,��ʾ��Ϣ�б�
	 * 
	 * @author dell
	 * 
	 */
	class MsgUiAdapter extends BaseAdapter {
		private PopupWindow mPopupWindow;
		private View popView;

		@Override
		public int getCount() {
			return msgs.size();
		}

		@Override
		public Object getItem(int position) {
			return msgs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
				convertView = inflater.inflate(R.layout.vlist_message, null);
			return convertView;
		}
	}

	/**
	 * ע��observer
	 */
	private void registerSMSObserver() {
		this.getContentResolver().registerContentObserver(
				Uri.parse(ContantsUtil.SMS_URI_ALL), true, composeObserver);

	}

	/**
	 * ���ݿ����
	 * 
	 * @author Administrator
	 * 
	 */

	class ComposeObserver extends ContentObserver {
		public ComposeObserver(Handler handler) {
			super(handler);
		}

		public void onChange(boolean selfChange) {
			refreshMsg();// ˢ�½���
			//CommonUtil.Log("sqy", "ComposeObserver", "====>refreshMsg()", 'i');
		}
	}

}
