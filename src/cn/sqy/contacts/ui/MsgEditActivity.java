package cn.sqy.contacts.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cn.sqy.contacts.R;
import cn.sqy.contacts.model.Contact;
import cn.sqy.contacts.model.Msg;
import cn.sqy.contacts.myview.SendMsgService;
import cn.sqy.contacts.tool.CommonUtil;
import cn.sqy.contacts.tool.ImageTools;

public class MsgEditActivity extends Activity implements OnClickListener,
		OnTouchListener {
	private Context context;
	private ListView lsvItemsMsg;
	private LayoutInflater inflater;
	private ImageView imgMsgPhoto;// ��ϵ����Ϣͷ��
	private TextView txtMsgName;// ��ϵ������,���Ϊİ��������ʾ�绰����
	private ImageButton imbMsgCall;// ��绰��ť
	private Button btnSendMsg;// ������Ϣ
	private EditText edtInputMsg;// ��Ϣ�༭��
	private List<Msg> msgs;
	private int nowThreadId;// ���ڽ��������洫�����Ķ������к�
	private boolean changedFlag = false;// ��ǵ�ǰҳ���Ƿ������ݸı�
	private PopupWindow mPopupWindow;
	private View popView;
	private boolean flag;// ���������洫������flag;
	private ViewFlipper vFlipper;
	private float touchDownX;// ���һ���ʱ��ָ���µ�X����
	private float touchUpX;// ���һ���ʱ��ָ�ɿ���X����
	private ImageView imgPhoneChoiceLeft, imgPhoneChoiceRight;
	private TextView txtCurrentTel, txtTempTel;
	private String currentNumber;// ��¼��ǰ�ĵ绰����
	private String tempNumber;// ��¼�ڶ�������
	private String msgContent;// ���ڴ洢��Ϣ����

	public static final int RESULT_MSG_CHANGED = 1;
	public static final int REQUEST_MSG_EDIT_NEW_CONTACT = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_edit);

	}

	/**
	 * Activity�ĳ�ʼ��
	 */
	private void init() {
		this.context = this;
		lsvItemsMsg = (ListView) findViewById(R.id.lsvItemsMsg);
		imgMsgPhoto = (ImageView) findViewById(R.id.imgMsgPhoto);
		txtMsgName = (TextView) findViewById(R.id.txtMsgName);
		imbMsgCall = (ImageButton) findViewById(R.id.imbMsgCall);
		btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
		edtInputMsg = (EditText) findViewById(R.id.edtInputMsg);
		vFlipper = (ViewFlipper) findViewById(R.id.compose_title_phonenumber);
		imgPhoneChoiceLeft = (ImageView) findViewById(R.id.phone_choice_left);
		imgPhoneChoiceRight = (ImageView) findViewById(R.id.phone_choice_right);
		txtCurrentTel = (TextView) findViewById(R.id.compose_title_current_phonenumber);
		txtTempTel = (TextView) findViewById(R.id.compose_title_temp_phonenumber);
		inflater = LayoutInflater.from(context);
		nowThreadId = this.getIntent().getIntExtra("threadId", -1);
		flag = this.getIntent().getBooleanExtra("flag", false);
	}

	/*
	 * ���������Ĳ˵�
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		CommonUtil.Log("menu", "MsgEditActivity", "onCreateContextMenu", 'i');
		menu.add(0, 1, 0, "ת����Ϣ");
		menu.add(0, 2, 1, "ɾ����Ϣ");
	}

	/*
	 * ��Ӧ�����Ĳ˵��Ͳ˵�
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();// ��ŵ�ǰ��������Ϣ
		CommonUtil.Log("menu", "ContactActivity", "onMenuItemSelected", 'i');
		final int nowPosition = info.position;
		switch (item.getItemId()) {
		case 1:// ת����Ϣ
			Intent intent = new Intent(context, NewMsgActivity.class);
			intent.putExtra("content", msgs.get(nowPosition).getContent());
			startActivity(intent);
			finish();
			break;
		case 2:// ɾ����Ϣ
			new AlertDialog.Builder(context)
					.setTitle("����")
					.setMessage("ȷ��ɾ��Щ��Ϣ��?")
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									refresh();
									changedFlag = true;
									CommonUtil.Toast(context, "ɾ����Ϣ�ɹ�!");
								}
							}).setNegativeButton("ȡ��", null).create().show();
			break;
		}
		return true;
	}

	/**
	 * ˢ�½���
	 */
	public void refresh() {
	}


	/**
	 * �ص�ˢ�½���
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	/**
	 * onClickListener�¼�
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imbMsgCall:// ��绰
			CommonUtil.dial(context, currentNumber);
			break;
		case R.id.btnSendMsg:// ���Ͷ���
			break;
		}
	}

	/**
	 * onTouchListener�¼�
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// ȡ�����һ���ʱ��ָ���µ�X����
			touchDownX = event.getX();
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// ȡ�����һ���ʱ��ָ�ɿ���X����
			touchUpX = event.getX();
			// �������ң���ǰһ��View
			if (touchUpX - touchDownX > 100) {
				// ����View�л��Ķ���
				vFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						android.R.anim.slide_in_left));
				vFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						android.R.anim.slide_out_right));
				// ��ʾ��һ��View
				vFlipper.showPrevious();
				// �ı䵱ǰ��ʾ����
				String str = null;
				str = currentNumber;
				currentNumber = tempNumber;
				tempNumber = str;
				// �������󣬿���һ��View
			} else if (touchDownX - touchUpX > 100) {
				// ����View�л��Ķ���
				// ����Androidû���ṩslide_out_left��slide_in_right�����Է���slide_in_left��slide_out_right��д��slide_out_left��slide_in_right
				vFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.slide_in_right));
				vFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.slide_out_left));
				// ��ʾǰһ��View
				vFlipper.showNext();
				// �ı䵱ǰ��ʾ����
				String str = null;
				str = currentNumber;
				currentNumber = tempNumber;
				tempNumber = str;
			}
			return true;
		}
		return false;
	}

	/**
	 * �Զ���Adapter,��ʾ��Ϣ�б�
	 * 
	 * @author dell
	 * 
	 */
	class MsgItemsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (nowThreadId == -1)
				return 0;
			else
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
			Msg msg = msgs.get(position);
			if (msg.getMsgMark() == 1)// ���յ�����Ϣ
				convertView = inflater.inflate(R.layout.vlist_receive_msgs,
						null);
			else
				convertView = inflater.inflate(R.layout.vlist_send_msgs, null);
			TextView txtDate = (TextView) convertView
					.findViewById(R.id.txtDate);
			TextView txtMsgContent = (TextView) convertView
					.findViewById(R.id.txtMsgContent);
			// ������Ϣ��ʱ��
			String date = CommonUtil.getNowDate();// �õ���ǰϵͳ����
			if (msg.getMsgDate().substring(0, 10).equals(date))
				txtDate.setText(msg.getMsgDate().substring(11));
			else
				txtDate.setText(msg.getMsgDate().substring(0, 10));
			// ������Ϣ����
			txtMsgContent.setText(msg.getContent());
			return convertView;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && (changedFlag || flag))
			setResult(RESULT_MSG_CHANGED);
		return super.onKeyDown(keyCode, event);
	}

}
