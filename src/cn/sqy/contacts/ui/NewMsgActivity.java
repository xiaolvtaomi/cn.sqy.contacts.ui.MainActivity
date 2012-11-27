package cn.sqy.contacts.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.sqy.contacts.R;
import cn.sqy.contacts.model.Contact;
import cn.sqy.contacts.model.Msg;
import cn.sqy.contacts.tool.CommonUtil;

public class NewMsgActivity extends Activity implements OnClickListener {
	private Context context;
	private LayoutInflater inflater;
	private GridView gridViewDisplayBtnContact;// չʾ�����˵�һ������
	private LinearLayout llDisplayTxtContact;// չʾ�����˵�һ������
	private Button btnSendMsg;// ������Ϣ
	private EditText edtInputMsg;// ��Ϣ�༭��
	private GridView gridViewRecentContact;// չʾ���������Ϣ����ϵ��
	private ImageButton imbAddMoreContact;// ���Ӹ������ϵ��
	private List<Msg> recentMsgs;
	private List<String> recieverTel;// �ռ��˵绰
	private List<String> recieverName;// �ռ� ������
	private TextView txtDisplayReceivers, txtDisplayRecerversCount;
	private AlertDialog dialog;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			CommonUtil.Toast(context, "���ͳɹ�!");
			edtInputMsg.setText("");
			// ��ת����Ϣ������
			setResult(RESULT_SEND_MESG_SUCCEED);
			finish();
		}
	};

	public static final int REQUEST_SELECT_SYS_CONTACT = 1;
	public static final int RESULT_SEND_MESG_SUCCEED = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_message);

		init();// ��ʼ��
		// չʾ�����ϵ������
		gridViewRecentContact.setAdapter(new ArrayAdapter<String>(context,
				R.layout.item_new_recent_contact, getRecentContactName()));

		// �����ϵ���б��ĵ������
		gridViewRecentContact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				gridViewDisplayBtnContact.setVisibility(View.VISIBLE);
				llDisplayTxtContact.setVisibility(View.GONE);
				String address = recentMsgs.get(position).getAddress();
				String person = recentMsgs.get(position).getPerson();
				for (int i = 0; i < recieverTel.size(); i++) {
					if (address.equals(recieverTel.get(i))
							&& person.equals(recieverName.get(i)))
						return;
				}
				recieverTel.add(address);
				recieverName.add(person);
				refreshReceivers();
			}
		});
		// �ռ���ÿһ�����ļ���
		gridViewDisplayBtnContact
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						recieverName.remove(position);
						recieverTel.remove(position);
						refreshReceivers();
					}
				});

		// �༭���Ƿ�۽��ļ���
		edtInputMsg.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					gridViewDisplayBtnContact.setVisibility(View.GONE);
					llDisplayTxtContact.setVisibility(View.VISIBLE);
					String str = "";
					for (String s : recieverName)
						str = str + s + ",";
					// str.substring(0, str.length() - 2);
					txtDisplayReceivers.setText(str);
					txtDisplayRecerversCount.setText("��" + recieverName.size()
							+ "��");
				} else {
					gridViewDisplayBtnContact.setVisibility(View.VISIBLE);
					llDisplayTxtContact.setVisibility(View.GONE);
				}
			}
		});
		// ����༭���ʱ��,�ռ�����״̬�仯
		edtInputMsg.setOnClickListener(this);
		// ���txt�ռ�����ʱ��,�ռ�����״̬�仯
		llDisplayTxtContact.setOnClickListener(this);
		// ���Ӹ�����ϵ��
		imbAddMoreContact.setOnClickListener(this);
		// ������Ϣ
		btnSendMsg.setOnClickListener(this);
	}

	/**
	 * Activity�ĳ�ʼ��
	 */
	private void init() {
		this.context = this;
		inflater = LayoutInflater.from(context);
		llDisplayTxtContact = (LinearLayout) findViewById(R.id.ll_display_textview_contact);
		gridViewDisplayBtnContact = (GridView) findViewById(R.id.gridViewDisplayBtnContact);
		btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
		edtInputMsg = (EditText) findViewById(R.id.edtInputMsg);
		gridViewRecentContact = (GridView) findViewById(R.id.gridViewRecentContact);
		imbAddMoreContact = (ImageButton) findViewById(R.id.imbAddMoreContact);
		txtDisplayReceivers = (TextView) findViewById(R.id.display_style_receiver_list);
		txtDisplayRecerversCount = (TextView) findViewById(R.id.display_stylereceiver_count);
		recentMsgs = new ArrayList<Msg>();
		recieverTel = new ArrayList<String>();
		recieverName = new ArrayList<String>();
		String content = this.getIntent().getStringExtra("content");
		if (content != null && !content.equals(""))// ��������Ϣת��
			edtInputMsg.setText(content);
	}

	/**
	 * ��ȡ�����ϵ�˵�����
	 * 
	 * @return
	 */
	private List<String> getRecentContactName() {
		List<String> list = new ArrayList<String>();
		for (Msg msg : recentMsgs)
			list.add(msg.getPerson());
		return list;
	}

	/**
	 * ˢ���ռ���
	 */
	public void refreshReceivers() {
		gridViewDisplayBtnContact.setAdapter(new ArrayAdapter<String>(context,
				R.layout.item_new_msg_contact, recieverName));
	}

	/**
	 * �ص�ˢ�½���
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// ���Ӹ�����ϵ��
		case R.id.imbAddMoreContact:
			View view = inflater.inflate(
					R.layout.dialog_add_more_contact_click, null);
			TextView txtAddSysContact = (TextView) view
					.findViewById(R.id.txtAddSysContact);
			TextView txtAddUnknowContact = (TextView) view
					.findViewById(R.id.txtAddUnknowContact);
			dialog = new AlertDialog.Builder(context).setView(view).create();
			dialog.show();
			txtAddSysContact.setOnClickListener(this);// ����ϵͳ��ϵ��
			txtAddUnknowContact.setOnClickListener(this);// ����İ���˺���
			break;
		// ����ϵͳ��ϵ��
		case R.id.txtAddSysContact:
			dialog.dismiss();
			Intent intent = new Intent(NewMsgActivity.this,
					AddRemoveContactToGroupActivity.class);
			intent.putExtra("isNewMsg", true);
			startActivityForResult(intent, REQUEST_SELECT_SYS_CONTACT);
			break;
		// ����İ���˺���
		case R.id.txtAddUnknowContact:
			dialog.dismiss();// �ر���һ�ε�Dialog
			View view1 = inflater.inflate(R.layout.dialog_add_group, null);
			TextView txtTitle = (TextView) view1
					.findViewById(R.id.txtNewGroupTitle);
			final EditText edtInput = (EditText) view1
					.findViewById(R.id.edtNewGroupName);
			Button btnSure = (Button) view1.findViewById(R.id.btnAddNewGroup);
			Button btnCancel = (Button) view1.findViewById(R.id.btnGroupCancel);
			txtTitle.setText("����绰����");
			edtInput.setHint("������绰����:");
			edtInput.setInputType(InputType.TYPE_CLASS_PHONE);
			dialog = new AlertDialog.Builder(context).setView(view1).create();
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
		// ������Ϣ
		case R.id.btnSendMsg:
			
			break;
		// ���txt�ռ�����ʱ��,�ռ�����״̬�仯
		case R.id.ll_display_textview_contact:
			gridViewDisplayBtnContact.setVisibility(View.VISIBLE);
			llDisplayTxtContact.setVisibility(View.GONE);
			break;
		// ����༭���ʱ��,�ռ�����״̬�仯
		case R.id.edtInputMsg:
			gridViewDisplayBtnContact.setVisibility(View.GONE);
			llDisplayTxtContact.setVisibility(View.VISIBLE);
			String str = "";
			for (String s : recieverName)
				str = str + s + ",";
			// str.substring(0, str.length() - 2);
			txtDisplayReceivers.setText(str);
			txtDisplayRecerversCount.setText("��" + recieverName.size() + "��");
			break;
		}
	}

	/**
	 * ���Ͷ���
	 * 
	 * @param number
	 *            �绰����
	 * @param msgContent
	 *            ��������
	 */
	public void sendMsg(String number, String msgContent) {
	}
}