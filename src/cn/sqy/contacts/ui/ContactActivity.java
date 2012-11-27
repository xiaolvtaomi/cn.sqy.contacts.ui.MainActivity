package cn.sqy.contacts.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sqy.contacts.R;
import cn.sqy.contacts.model.Contact;
import cn.sqy.contacts.model.Email;
import cn.sqy.contacts.myview.MyPopupWindow;
import cn.sqy.contacts.tool.CommonUtil;
import cn.sqy.contacts.tool.ImageTools;

public class ContactActivity extends Activity {
	private Context context;// 当前上下文对象
	private ImageButton imbDownContact, imbNewContact;// 选择分组、新建联系人
	private Button btnGroupMgr;// 联系人管理按钮
	private TextView txtContactTool;
	private List<String> groupNames;// 分组名称的集合
	private String[] mItems;// 分组名称的数据
	private List<Contact> contacts;
	private ListView lsvContact, lsvGroup;
	private MyPopupWindow popupWindow;// 自定义PopupWindow弹出选择分组列表
	private RelativeLayout rlContactTool;
	private int groupPosition = 0;// 记录当前选中的分组位置
	private EditText edtSearchContact;
	private int nowGroupPosition = 0;// 当前分组位置
	// 删除联系人的相关字段
	private boolean isContactMgr = false;// true:联系人管理界面,false:联系人列表界面
	private Map<Integer, Boolean> isChecked;// 标记联系人的CheckBox是否选中
	private Menu myMenu;// 菜单
	private int ckbCount = 0;// 当前选中联系人个数
	private int nowPosMenu;// 标记当前使用上下文菜单的项的Position
	private AlertDialog dialog;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			toNotContactMgrMenu();// 跳转到主界面
			CommonUtil.Toast(context, "批量删除成功!");
			dialog.dismiss();
		}
	};

	public static final int REQUEST_CONTACT_ITEM_CLICK = 1;
	public static final int REQUEST_NEW_CONTACT_CLICK = 2;
	public static final int REQUEST_GROUP_MGR_CLICK = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		init();// 初始化数据

		registerForContextMenu(lsvContact);// 注册上下文菜单

		// 选择分组
		rlContactTool.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				check();// 初始化PopupWindow
				refleshLsvGroup();

				imbDownContact.setBackgroundResource(R.drawable.spinner_down);
				popupWindow.showAsDropDown(findViewById(R.id.rlToolbar01), 80,
						-10);
				CommonUtil.Log("sqy", "MainActivity", "showAsDropDown", 'i');
				lsvGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// CommonUtil.Toast(context, arg2 + "," + arg3);
						nowGroupPosition = arg2;
						txtContactTool.setText(mItems[arg2]);
						groupPosition = arg2;
						refleshLsvContact();
						popupWindow.dismiss();
					}
				});
				btnGroupMgr.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context,
								GroupMgrActivity.class);
						startActivityForResult(intent, REQUEST_GROUP_MGR_CLICK);
						popupWindow.dismiss();
					}
				});
			}
		});

		// 新建联系人
		imbNewContact.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, NewContactActivity.class);
				startActivityForResult(intent, REQUEST_NEW_CONTACT_CLICK);
				isContactMgr = false;
				if (myMenu != null) {
					myMenu.getItem(0).setTitle("批量管理");
					myMenu.getItem(1).setEnabled(false);
					myMenu.getItem(2).setEnabled(false);
					myMenu.getItem(3).setEnabled(false);
				}
			}
		});

		// 每项联系人的监听
		lsvContact
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						CommonUtil.Log("sqy", "ContactActivity",
								"lsvContact.setOnItemClickListener:id=" + id
										+ ",position=" + position, 'i');
						int contactId = (int) id;
						// 显示联系人详细信息并可修改联系人信息
						if (!isContactMgr) {
							Intent intent = new Intent(context,
									NewContactActivity.class);
							intent.putExtra("contactId", contactId);
							startActivityForResult(intent,
									REQUEST_CONTACT_ITEM_CLICK);
						}
						// 联系人管理，可批量删除
						else {
							CheckBox ckb = (CheckBox) view
									.findViewById(R.id.ckbContact);
							if (ckb.isChecked()) {
								ckb.setChecked(false);
								isChecked.put(contactId, false);
								ckbCount--;
							} else {
								ckb.setChecked(true);
								isChecked.put(contactId, true);
								ckbCount++;
							}
						}
					}
				});
	}

	/**
	 * 数据初始化
	 */
	public void init() {
		context = this;
		contacts = new ArrayList<Contact>();
		initGroup();
		contacts = this.getContactsByGroupPosition();
		Collections.sort(contacts);// 按姓名拼音进行排序
		imbDownContact = (ImageButton) this.findViewById(R.id.imbDownContact);
		txtContactTool = (TextView) this.findViewById(R.id.txtContactTool);
		lsvContact = (ListView) this.findViewById(R.id.Lsv_contacts);
		imbNewContact = (ImageButton) findViewById(R.id.imbNewContact);
		rlContactTool = (RelativeLayout) findViewById(R.id.rlContactTool);
		edtSearchContact = (EditText) findViewById(R.id.edtFindContact);
		isChecked = new HashMap<Integer, Boolean>();
		MyAdapter adapter = new MyAdapter(context);
//		lsvContact.setAdapter(adapter);
	}

	/**
	 * 初始化分组
	 */
	public void initGroup() {
	}

	/**
	 * 获取当前选择分组后要显示的联系人实体集
	 * 
	 * @return 联系人实体集
	 */
	public List<Contact> getContactsByGroupPosition() {
		return new ArrayList<Contact>();
	}

	/**
	 * 刷新当前联系人列表
	 */
	public void refleshLsvContact() {
		contacts = this.getContactsByGroupPosition();
		Collections.sort(contacts);// 按姓名拼音进行排序
		if (contacts.size() == 0)
			CommonUtil.Toast(context, "当前分组无联系人");
		MyAdapter adapter = new MyAdapter(context);
		lsvContact.setAdapter(adapter);
	}

	/**
	 * 刷新当前分组列表
	 */
	public void refleshLsvGroup() {
		initGroup();
		//nowGroupPosition=0;
		//groupPosition=0;
		System.out.println("===>"+groupNames.size());
		System.out.println("===>"+groupPosition);
		System.out.println("===>"+nowGroupPosition);
		txtContactTool.setText(mItems[nowGroupPosition]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				R.layout.vlist_group, mItems);
		lsvGroup.setAdapter(adapter);
	}

	/*
	 * 创建上下文菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		CommonUtil.Log("menu", "ContactActivity", "onCreateContextMenu", 'i');
		menu.add(0, 10, 0, "删除联系人");
		menu.add(0, 11, 1, "编辑联系人");
		menu.add(0, 12, 2, "新建联系人");
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
		case 10:// 删除联系人
			new AlertDialog.Builder(context)
					.setTitle("警告")
					.setMessage("您确定要删除联系人吗？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).setNegativeButton("取消", null).create().show();
			break;
		case 11:// 编辑联系人
			Intent intent = new Intent(context, NewContactActivity.class);
			intent.putExtra("contactId", contacts.get(nowPosMenu).getId());
			startActivityForResult(intent, REQUEST_CONTACT_ITEM_CLICK);
			break;
		case 12:// 新建联系人
			Intent intent1 = new Intent(context, NewContactActivity.class);
			startActivityForResult(intent1, REQUEST_NEW_CONTACT_CLICK);
			isContactMgr = false;
			if (myMenu != null) {
				myMenu.getItem(0).setTitle("批量管理");
				myMenu.getItem(1).setEnabled(false);
				myMenu.getItem(2).setEnabled(false);
				myMenu.getItem(3).setEnabled(false);
			}
			break;

		case 1:// 批量管理
			if (isContactMgr) {
				toNotContactMgrMenu();
			} else {
				toIsContactMgrMenu();
			}
			break;
		case 2:// 全选
			for (Contact contact : contacts)
				isChecked.put(contact.getId(), true);
			refleshLsvContact();
			ckbCount = isChecked.size();
			break;
		case 3:// 全消
			for (Contact contact : contacts)
				isChecked.put(contact.getId(), false);
			refleshLsvContact();
			ckbCount = 0;
			break;
		case 4:// 批量删除
			if (ckbCount > 0) {
				LayoutInflater inflater = LayoutInflater.from(context);
				View view = inflater.inflate(R.layout.progress_dialog, null);
				((TextView) view.findViewById(R.id.progress_msg))
						.setText(" 删 除 中 . . .");
				dialog = new AlertDialog.Builder(context).setView(view)
						.create();
				new AlertDialog.Builder(context)
						.setTitle("警告")
						.setMessage("您确定要删除选中的联系人吗?")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										ContactActivity.this.dialog.show();
										new Thread(new Runnable() {

											@Override
											public void run() {
												try {
													Thread.sleep(500);
												} catch (InterruptedException e) {
													e.printStackTrace();
												}
											}
										}).start();
									}
								}).setNegativeButton("取消", null).create()
						.show();
			} else
				CommonUtil.Toast(context, "当前无选中项!");
			break;
		}
		return true;
	}

	private void toIsContactMgrMenu() {
		isContactMgr = true;
		myMenu.getItem(0).setTitle("取消管理");
		myMenu.getItem(1).setEnabled(true);
		myMenu.getItem(2).setEnabled(true);
		myMenu.getItem(3).setEnabled(true);
		refleshLsvContact();
		// 初始化当前界面所有联系人的选择框状态为false
		for (Contact contact : contacts)
			isChecked.put(contact.getId(), false);
	}

	private void toNotContactMgrMenu() {
		isContactMgr = false;
		myMenu.getItem(0).setTitle("批量管理");
		refleshLsvContact();
		myMenu.getItem(1).setEnabled(false);
		myMenu.getItem(2).setEnabled(false);
		myMenu.getItem(3).setEnabled(false);
	}

	/**
	 * 创建菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		CommonUtil.Log("menu", "ContactActivity", "onCreateOptionsMenu", 'i');
		menu.add(1, 1, 1, "批量管理").setIcon(R.drawable.ic_menu_bachmanager);
		menu.add(1, 2, 2, "全选").setIcon(R.drawable.ic_menu_select_all)
				.setEnabled(false);
		menu.add(1, 3, 3, "全消").setIcon(R.drawable.ic_menu_unselect)
				.setEnabled(false);
		menu.add(1, 4, 4, "批量删除").setIcon(android.R.drawable.ic_menu_delete)
				.setEnabled(false);
		myMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Activity回调刷新界面
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_GROUP_MGR_CLICK) {
			refleshLsvGroup();
			refleshLsvContact();
			edtSearchContact.setText("");
		}
		if (requestCode == REQUEST_NEW_CONTACT_CLICK
				&& resultCode == NewContactActivity.RESULT_BTN_SAVE) {
			edtSearchContact.setText("");
			refleshLsvContact();
		}
		if (requestCode == REQUEST_CONTACT_ITEM_CLICK
				&& resultCode == NewContactActivity.RESULT_BTN_SAVE) {
			edtSearchContact.setText("");
			refleshLsvContact();
		}
	}

	/**
	 * 监听系统按键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && isContactMgr) {
			toNotContactMgrMenu();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 自定义适配器
	 * 
	 * @author sqy
	 * 
	 */
	class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private View popView;
		private PopupWindow mPopupWindow;

		public MyAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			edtSearchContact.setHint("联系人搜索 | 共" + contacts.size() + "人");
			return contacts.size();
		}

		@Override
		public Object getItem(int arg0) {
			return contacts.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return contacts.get(arg0).getId();
		}

		@Override
		public View getView(final int arg0, View arg1, ViewGroup arg2) {
			return arg1;
		}
	}

	/**
	 * 初始化popupWindow
	 */
	private void check() {
		if (popupWindow == null) {
			View view = getLayoutInflater().inflate(R.layout.popup_selectgroup,
					null);
			lsvGroup = (ListView) view.findViewById(R.id.lsvGroup);
			btnGroupMgr = (Button) view.findViewById(R.id.btnGroupMgr);
			popupWindow = new MyPopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, imbDownContact);
			// 下面三个设置可以使PopupWindow中的项和外面的键都可以响应点击或触摸事件
			popupWindow.setFocusable(true);
			popupWindow.setTouchable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());

		}
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}
}
