package cn.sqy.contacts.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.sqy.contacts.R;
import cn.sqy.contacts.model.Contact;
import cn.sqy.contacts.model.Group;
import cn.sqy.contacts.tool.CommonUtil;

public class GroupMgrActivity extends Activity implements OnClickListener {
	private ImageButton imbAddGroup, imbBack;// ��ӷ��顢���ذ�ť
	private ListView lsvGroupMgr;// �����ListView
	private Context context;// ��ǰ�����Ķ���
	private List<Contact> contacts;// ���յ�ǰָ���������ϵ��
	private List<Group> groups;// �������з���
	private AlertDialog.Builder builder;// Dialog������
	private LayoutInflater inflater;// XML����
	private AlertDialog dialog;// Dialog
	private int nowGroupId;// ��¼���ڲ����ķ���

	public static final int REQUEST_TXTADDCONTACTS = 1;
	public static final int REQUEST_TXTREMOVECONTACTS = 2;
	public static final int RESULT_GROUPMGRACTIVITY_BACK = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_mgr);

		init();// ��ʼ��

		MyAdapter adapter = new MyAdapter(context);
//		lsvGroupMgr.setAdapter(adapter);

		imbAddGroup.setOnClickListener(this);// ���һ���µķ���
		imbBack.setOnClickListener(this);// ����
		// GroupItem�ĵ�������¼�
		lsvGroupMgr
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						nowGroupId = (int) id;
						View dialogViewItem = inflater.inflate(
								R.layout.dialog_group_item_click, null);
						TextView txtDialogTitle = (TextView) dialogViewItem
								.findViewById(R.id.txtDialogTitle);
						TextView txtAddContacts = (TextView) dialogViewItem
								.findViewById(R.id.txtAddContacts);
						TextView txtRemoveContacts = (TextView) dialogViewItem
								.findViewById(R.id.txtRemoveContacts);
						TextView txtEditGroupName = (TextView) dialogViewItem
								.findViewById(R.id.txtEditGroupName);
						CommonUtil.Log("sqy",
								"lsvGroupMgr.setOnItemClickListener",
								groups.get(position) + ",id=" + id, 'i');
						builder = new AlertDialog.Builder(context);
						dialog = builder.setView(dialogViewItem).create();
						dialog.show();
						txtAddContacts
								.setOnClickListener(GroupMgrActivity.this);
						txtRemoveContacts
								.setOnClickListener(GroupMgrActivity.this);
						txtEditGroupName
								.setOnClickListener(GroupMgrActivity.this);
					}
				});

	}

	/**
	 * ��ʼ��
	 */
	private void init() {
		this.context = this;
		imbAddGroup = (ImageButton) findViewById(R.id.imbAddGroup);
		imbBack = (ImageButton) findViewById(R.id.imb_groupmgr_back);
		lsvGroupMgr = (ListView) findViewById(R.id.lsvGroupMgr);
		builder = new AlertDialog.Builder(context);
		inflater = LayoutInflater.from(context);
	}

	/**
	 * ˢ��ListView
	 */
	public void refleshLsvGroupMgr() {
		groups = new ArrayList<Group>();
		MyAdapter adapter = new MyAdapter(context);
		lsvGroupMgr.setAdapter(adapter);
	}

	/**
	 * OnClickListener�ӿڵķ���
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imbAddGroup:// ��ӷ���
			showMyDialog(R.id.imbAddGroup, null);
			break;

		case R.id.imb_groupmgr_back:// ����
			setResult(RESULT_GROUPMGRACTIVITY_BACK);
			finish();
			break;
		case R.id.txtAddContacts:// �������ӳ�Ա
			Intent intent1 = new Intent(context,
					AddRemoveContactToGroupActivity.class);
			intent1.putExtra("groupId", nowGroupId);
			intent1.putExtra("isAdd", true);
			startActivityForResult(intent1, REQUEST_TXTADDCONTACTS);
			dialog.dismiss();
			break;
		case R.id.txtRemoveContacts:// �Ƴ������Ա
			Intent intent2 = new Intent(context,
					AddRemoveContactToGroupActivity.class);
			intent2.putExtra("groupId", nowGroupId);
			intent2.putExtra("isAdd", false);
			startActivityForResult(intent2, REQUEST_TXTREMOVECONTACTS);
			dialog.dismiss();
			break;
		case R.id.txtEditGroupName:// ����������
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		refleshLsvGroupMgr();
	}

	/**
	 * ��ʾһ���Ի���
	 * 
	 * @param viewId
	 *            ����Ŀؼ�ID��
	 */
	public void showMyDialog(int viewId, final Group group) {

		View dialog_view = inflater.inflate(R.layout.dialog_add_group, null);
		final EditText edtNewGroupName = (EditText) dialog_view
				.findViewById(R.id.edtNewGroupName);
		TextView txtDialogTitleName = (TextView) dialog_view
				.findViewById(R.id.txtNewGroupTitle);
		TextView txtMsgAlert = (TextView) dialog_view
				.findViewById(R.id.txtMsgAlert);
		Button btnSure = (Button) dialog_view.findViewById(R.id.btnAddNewGroup);
		Button btnCancel = (Button) dialog_view
				.findViewById(R.id.btnGroupCancel);

		switch (viewId) {
		// ɾ��һ������ʱ��ʾ�ĶԻ���
		case R.id.imbDelGroup:
			txtDialogTitleName.setText("��ܰ��ʾ");
			txtMsgAlert.setVisibility(View.VISIBLE);
			txtMsgAlert.setText("ȷ�Ͻ�ɢ����'" + group.getGroupName() + "'?");
			edtNewGroupName.setVisibility(View.GONE);
			builder = new AlertDialog.Builder(context);
			dialog = builder.setView(dialog_view).create();
			dialog.show();
			btnSure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;

		// ���һ������ʱ��ʾ�ĶԻ���
		case R.id.imbAddGroup:
			txtDialogTitleName.setText("�½�����");
			txtMsgAlert.setVisibility(View.GONE);
			edtNewGroupName.setVisibility(View.VISIBLE);
			edtNewGroupName.setHint("������������ƣ�");
			builder = new AlertDialog.Builder(context);
			dialog = builder.setView(dialog_view).create();
			dialog.show();
			btnSure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CommonUtil.Log("sqy", "GroupMgrActivity:R.id.imbAddGroup",
							"btnSure", 'i');
					String newGroupName = edtNewGroupName.getText().toString();
					if (newGroupName.equals("")) {
						CommonUtil.Toast(context, "����ʧ��,����������Ϊ��!");
						dialog.dismiss();
					} else if (newGroupName.equals("ȫ����ϵ��")
							|| newGroupName.equals("δ����")) {
						CommonUtil.Toast(context, "����ʧ��,�÷�����Ϊϵͳ����!");
						dialog.dismiss();
					} else {
						List<String> groupNames = new ArrayList<String>();
						// ��ȡ�½������ID��
						Intent intent3 = new Intent(context,
								AddRemoveContactToGroupActivity.class);
						intent3.putExtra("groupId", nowGroupId);
						intent3.putExtra("isAdd", true);
						startActivityForResult(intent3, REQUEST_TXTADDCONTACTS);
						dialog.dismiss();
						//refleshLsvGroupMgr();
					}
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CommonUtil.Log("sqy", "GroupMgrActivity", "btnCancel", 'i');
					dialog.dismiss();
				}
			});
			break;
		case R.id.txtEditGroupName:// ����������
			txtDialogTitleName.setText("�༭����");
			txtMsgAlert.setVisibility(View.GONE);
			edtNewGroupName.setVisibility(View.VISIBLE);
			edtNewGroupName.setHint("�������µķ������ƣ�");
			edtNewGroupName.setText(group.getGroupName());
			builder = new AlertDialog.Builder(context);
			dialog = builder.setView(dialog_view).create();
			dialog.show();
			btnSure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			});
			btnCancel.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					CommonUtil.Log("sqy", "GroupMgrActivity", "btnCancel", 'i');
					dialog.dismiss();
				}
			});
			break;
		}
	}

	/**
	 * ����ϵͳ�ķ��ؼ�
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_GROUPMGRACTIVITY_BACK);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * �Զ���Adapter
	 * 
	 * @author dell
	 * 
	 */
	public class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;

		public MyAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return groups.size();
		}

		@Override
		public Object getItem(int position) {
			return groups.get(position);
		}

		@Override
		public long getItemId(int position) {
			return groups.get(position).getGroupId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.vlist_group_mgr, null);
			TextView txtGroupName = (TextView) convertView
					.findViewById(R.id.txtGroupName);
			TextView txtGroupCount = (TextView) convertView
					.findViewById(R.id.txtContactCount);
			ImageButton imbDelGroup = (ImageButton) convertView
					.findViewById(R.id.imbDelGroup);

			final Group group = groups.get(position);

			txtGroupName.setText(group.getGroupName());
			txtGroupCount.setText(contacts.size() + "λ��ϵ��");
			imbDelGroup.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					showMyDialog(R.id.imbDelGroup, group);
				}
			});

			return convertView;
		}
	}

}
