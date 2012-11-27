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
	private Context context;// ��ǰ�����Ķ���
	private ImageButton imbDownContact, imbNewContact;// ѡ����顢�½���ϵ��
	private Button btnGroupMgr;// ��ϵ�˹���ť
	private TextView txtContactTool;
	private List<String> groupNames;// �������Ƶļ���
	private String[] mItems;// �������Ƶ�����
	private List<Contact> contacts;
	private ListView lsvContact, lsvGroup;
	private MyPopupWindow popupWindow;// �Զ���PopupWindow����ѡ������б�
	private RelativeLayout rlContactTool;
	private int groupPosition = 0;// ��¼��ǰѡ�еķ���λ��
	private EditText edtSearchContact;
	private int nowGroupPosition = 0;// ��ǰ����λ��
	// ɾ����ϵ�˵�����ֶ�
	private boolean isContactMgr = false;// true:��ϵ�˹������,false:��ϵ���б����
	private Map<Integer, Boolean> isChecked;// �����ϵ�˵�CheckBox�Ƿ�ѡ��
	private Menu myMenu;// �˵�
	private int ckbCount = 0;// ��ǰѡ����ϵ�˸���
	private int nowPosMenu;// ��ǵ�ǰʹ�������Ĳ˵������Position
	private AlertDialog dialog;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			toNotContactMgrMenu();// ��ת��������
			CommonUtil.Toast(context, "����ɾ���ɹ�!");
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

		init();// ��ʼ������

		registerForContextMenu(lsvContact);// ע�������Ĳ˵�

		// ѡ�����
		rlContactTool.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				check();// ��ʼ��PopupWindow
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

		// �½���ϵ��
		imbNewContact.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, NewContactActivity.class);
				startActivityForResult(intent, REQUEST_NEW_CONTACT_CLICK);
				isContactMgr = false;
				if (myMenu != null) {
					myMenu.getItem(0).setTitle("��������");
					myMenu.getItem(1).setEnabled(false);
					myMenu.getItem(2).setEnabled(false);
					myMenu.getItem(3).setEnabled(false);
				}
			}
		});

		// ÿ����ϵ�˵ļ���
		lsvContact
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						CommonUtil.Log("sqy", "ContactActivity",
								"lsvContact.setOnItemClickListener:id=" + id
										+ ",position=" + position, 'i');
						int contactId = (int) id;
						// ��ʾ��ϵ����ϸ��Ϣ�����޸���ϵ����Ϣ
						if (!isContactMgr) {
							Intent intent = new Intent(context,
									NewContactActivity.class);
							intent.putExtra("contactId", contactId);
							startActivityForResult(intent,
									REQUEST_CONTACT_ITEM_CLICK);
						}
						// ��ϵ�˹���������ɾ��
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
	 * ���ݳ�ʼ��
	 */
	public void init() {
		context = this;
		contacts = new ArrayList<Contact>();
		initGroup();
		contacts = this.getContactsByGroupPosition();
		Collections.sort(contacts);// ������ƴ����������
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
	 * ��ʼ������
	 */
	public void initGroup() {
	}

	/**
	 * ��ȡ��ǰѡ������Ҫ��ʾ����ϵ��ʵ�弯
	 * 
	 * @return ��ϵ��ʵ�弯
	 */
	public List<Contact> getContactsByGroupPosition() {
		return new ArrayList<Contact>();
	}

	/**
	 * ˢ�µ�ǰ��ϵ���б�
	 */
	public void refleshLsvContact() {
		contacts = this.getContactsByGroupPosition();
		Collections.sort(contacts);// ������ƴ����������
		if (contacts.size() == 0)
			CommonUtil.Toast(context, "��ǰ��������ϵ��");
		MyAdapter adapter = new MyAdapter(context);
		lsvContact.setAdapter(adapter);
	}

	/**
	 * ˢ�µ�ǰ�����б�
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
	 * ���������Ĳ˵�
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		CommonUtil.Log("menu", "ContactActivity", "onCreateContextMenu", 'i');
		menu.add(0, 10, 0, "ɾ����ϵ��");
		menu.add(0, 11, 1, "�༭��ϵ��");
		menu.add(0, 12, 2, "�½���ϵ��");
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
		case 10:// ɾ����ϵ��
			new AlertDialog.Builder(context)
					.setTitle("����")
					.setMessage("��ȷ��Ҫɾ����ϵ����")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).setNegativeButton("ȡ��", null).create().show();
			break;
		case 11:// �༭��ϵ��
			Intent intent = new Intent(context, NewContactActivity.class);
			intent.putExtra("contactId", contacts.get(nowPosMenu).getId());
			startActivityForResult(intent, REQUEST_CONTACT_ITEM_CLICK);
			break;
		case 12:// �½���ϵ��
			Intent intent1 = new Intent(context, NewContactActivity.class);
			startActivityForResult(intent1, REQUEST_NEW_CONTACT_CLICK);
			isContactMgr = false;
			if (myMenu != null) {
				myMenu.getItem(0).setTitle("��������");
				myMenu.getItem(1).setEnabled(false);
				myMenu.getItem(2).setEnabled(false);
				myMenu.getItem(3).setEnabled(false);
			}
			break;

		case 1:// ��������
			if (isContactMgr) {
				toNotContactMgrMenu();
			} else {
				toIsContactMgrMenu();
			}
			break;
		case 2:// ȫѡ
			for (Contact contact : contacts)
				isChecked.put(contact.getId(), true);
			refleshLsvContact();
			ckbCount = isChecked.size();
			break;
		case 3:// ȫ��
			for (Contact contact : contacts)
				isChecked.put(contact.getId(), false);
			refleshLsvContact();
			ckbCount = 0;
			break;
		case 4:// ����ɾ��
			if (ckbCount > 0) {
				LayoutInflater inflater = LayoutInflater.from(context);
				View view = inflater.inflate(R.layout.progress_dialog, null);
				((TextView) view.findViewById(R.id.progress_msg))
						.setText(" ɾ �� �� . . .");
				dialog = new AlertDialog.Builder(context).setView(view)
						.create();
				new AlertDialog.Builder(context)
						.setTitle("����")
						.setMessage("��ȷ��Ҫɾ��ѡ�е���ϵ����?")
						.setPositiveButton("ȷ��",
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
								}).setNegativeButton("ȡ��", null).create()
						.show();
			} else
				CommonUtil.Toast(context, "��ǰ��ѡ����!");
			break;
		}
		return true;
	}

	private void toIsContactMgrMenu() {
		isContactMgr = true;
		myMenu.getItem(0).setTitle("ȡ������");
		myMenu.getItem(1).setEnabled(true);
		myMenu.getItem(2).setEnabled(true);
		myMenu.getItem(3).setEnabled(true);
		refleshLsvContact();
		// ��ʼ����ǰ����������ϵ�˵�ѡ���״̬Ϊfalse
		for (Contact contact : contacts)
			isChecked.put(contact.getId(), false);
	}

	private void toNotContactMgrMenu() {
		isContactMgr = false;
		myMenu.getItem(0).setTitle("��������");
		refleshLsvContact();
		myMenu.getItem(1).setEnabled(false);
		myMenu.getItem(2).setEnabled(false);
		myMenu.getItem(3).setEnabled(false);
	}

	/**
	 * �����˵�
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		CommonUtil.Log("menu", "ContactActivity", "onCreateOptionsMenu", 'i');
		menu.add(1, 1, 1, "��������").setIcon(R.drawable.ic_menu_bachmanager);
		menu.add(1, 2, 2, "ȫѡ").setIcon(R.drawable.ic_menu_select_all)
				.setEnabled(false);
		menu.add(1, 3, 3, "ȫ��").setIcon(R.drawable.ic_menu_unselect)
				.setEnabled(false);
		menu.add(1, 4, 4, "����ɾ��").setIcon(android.R.drawable.ic_menu_delete)
				.setEnabled(false);
		myMenu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Activity�ص�ˢ�½���
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
	 * ����ϵͳ����
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
	 * �Զ���������
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
			edtSearchContact.setHint("��ϵ������ | ��" + contacts.size() + "��");
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
	 * ��ʼ��popupWindow
	 */
	private void check() {
		if (popupWindow == null) {
			View view = getLayoutInflater().inflate(R.layout.popup_selectgroup,
					null);
			lsvGroup = (ListView) view.findViewById(R.id.lsvGroup);
			btnGroupMgr = (Button) view.findViewById(R.id.btnGroupMgr);
			popupWindow = new MyPopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, imbDownContact);
			// �����������ÿ���ʹPopupWindow�е��������ļ���������Ӧ��������¼�
			popupWindow.setFocusable(true);
			popupWindow.setTouchable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());

		}
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}
}
