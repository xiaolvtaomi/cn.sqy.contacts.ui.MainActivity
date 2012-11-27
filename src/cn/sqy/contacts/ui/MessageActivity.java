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
	private List<Msg> msgs;// 接收每个联系人最新的一条短信
	private LayoutInflater inflater;
	private ListView lsvMsg;
	// 信息管理
	private Menu myMenu;// 菜单
	private boolean isMessageMgr = false;// true:信息管理界面,false:信息列表界面
	private Map<Integer, Boolean> isChecked;// 标记信息的每项的CheckBox是否选中
	private int nowPosMenu;// 标记当前使用上下文菜单的项的Position
	private int ckbCount = 0;// 当前选中信息个数
	private boolean flag = false;// 标记当前点击到对话列表前是否有未读短信
	private AlertDialog dialog;

	private ComposeObserver composeObserver = null;// 系统数据库监听

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if ((Integer) msg.obj == 1) {
				CommonUtil.Toast(context, "批量删除对话成功!");
				toNotMessageMgrMenu();// 跳转到主界面
			}
			if ((Integer) msg.obj == 2) {
				refreshMsg();
				CommonUtil.Toast(context, "删除对话成功!");
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

		init();// 初始化
		registerForContextMenu(lsvMsg);
		registerSMSObserver();// 注册系统数据库监听,看是否有新信息

	}

	/**
	 * Activity的初始化
	 */
	private void init() {
	}

	/**
	 * 刷新界面
	 */
	private void refreshMsg() {
		msgs = new ArrayList<Msg>();
		MsgUiAdapter adapter = new MsgUiAdapter();
	}

	/**
	 * 创建菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		CommonUtil.Log("menu", "MessageActivity", "onCreateOptionsMenu", 'i');
		menu.add(1, 1, 1, "批量管理").setIcon(R.drawable.menu_batch_mgr);
		menu.add(1, 2, 2, "全选").setIcon(R.drawable.menu_batch_all)
				.setEnabled(false);
		menu.add(1, 3, 3, "全消").setIcon(R.drawable.menu_batch_cancel)
				.setEnabled(false);
		menu.add(1, 4, 4, "已读").setIcon(R.drawable.menu_all_read)
				.setEnabled(false);
		menu.add(1, 5, 5, "批量删除").setIcon(R.drawable.menu_batch_delete)
				.setEnabled(false);
		myMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * 创建上下文菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		CommonUtil.Log("menu", "MessageActivity", "onCreateContextMenu", 'i');
		menu.add(0, 10, 0, "拨打电话");
		menu.add(0, 11, 1, "删除");
	}

	/*
	 * 响应上下文菜单和菜单
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();// 存放当前点击项的信息
		CommonUtil.Log("menu", "ContactActivity", "onMenuItemSelected", 'i');
		if (featureId == 6)// 表明是上下文菜单
			nowPosMenu = info.position;
		switch (item.getItemId()) {
		case 1:// 批量管理
			if (isMessageMgr) {
				toNotMessageMgrMenu();
			} else {
				toIsMessageMgrMenu();
			}
			break;
		case 2:// 全选
			for (Msg msg : msgs)
				isChecked.put(msg.getThreadId(), true);
			refreshMsg();
			ckbCount = isChecked.size();
			break;
		case 3:// 全消
			for (Msg msg : msgs)
				isChecked.put(msg.getThreadId(), false);
			refreshMsg();
			ckbCount = 0;
			break;
		case 4:// 已读
			break;
		case 5:// 批量删除
			break;
		case 10:// 打电话
			String num = msgs.get(nowPosMenu).getAddress();
			CommonUtil.dial(context, num);
			break;
		case 11:// 删除对话

			break;
		}
		return true;
	}

	private void toIsMessageMgrMenu() {
		isMessageMgr = true;
		myMenu.getItem(0).setTitle("取消管理");
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
		myMenu.getItem(0).setTitle("批量管理");
		refreshMsg();
		myMenu.getItem(1).setEnabled(false);
		myMenu.getItem(2).setEnabled(false);
		myMenu.getItem(3).setEnabled(false);
		myMenu.getItem(4).setEnabled(false);
	}

	/**
	 * 回调刷新界面
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
	 * 监听系统按键
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
	 * 自定义Adapter,显示信息列表
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
	 * 注册observer
	 */
	private void registerSMSObserver() {
		this.getContentResolver().registerContentObserver(
				Uri.parse(ContantsUtil.SMS_URI_ALL), true, composeObserver);

	}

	/**
	 * 数据库监听
	 * 
	 * @author Administrator
	 * 
	 */

	class ComposeObserver extends ContentObserver {
		public ComposeObserver(Handler handler) {
			super(handler);
		}

		public void onChange(boolean selfChange) {
			refreshMsg();// 刷新界面
			//CommonUtil.Log("sqy", "ComposeObserver", "====>refreshMsg()", 'i');
		}
	}

}
