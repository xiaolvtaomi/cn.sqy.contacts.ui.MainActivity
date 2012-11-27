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
	private ImageView imgMsgPhoto;// 联系人信息头像
	private TextView txtMsgName;// 联系人姓名,如果为陌生人则显示电话号码
	private ImageButton imbMsgCall;// 打电话按钮
	private Button btnSendMsg;// 发送消息
	private EditText edtInputMsg;// 信息编辑框
	private List<Msg> msgs;
	private int nowThreadId;// 用于接收主界面传过来的短信序列号
	private boolean changedFlag = false;// 标记当前页面是否有数据改变
	private PopupWindow mPopupWindow;
	private View popView;
	private boolean flag;// 接收主界面传过来的flag;
	private ViewFlipper vFlipper;
	private float touchDownX;// 左右滑动时手指按下的X坐标
	private float touchUpX;// 左右滑动时手指松开的X坐标
	private ImageView imgPhoneChoiceLeft, imgPhoneChoiceRight;
	private TextView txtCurrentTel, txtTempTel;
	private String currentNumber;// 记录当前的电话号码
	private String tempNumber;// 记录第二个号码
	private String msgContent;// 用于存储信息内容

	public static final int RESULT_MSG_CHANGED = 1;
	public static final int REQUEST_MSG_EDIT_NEW_CONTACT = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_edit);

	}

	/**
	 * Activity的初始化
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
	 * 创建上下文菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		CommonUtil.Log("menu", "MsgEditActivity", "onCreateContextMenu", 'i');
		menu.add(0, 1, 0, "转发信息");
		menu.add(0, 2, 1, "删除信息");
	}

	/*
	 * 响应上下文菜单和菜单
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();// 存放当前点击项的信息
		CommonUtil.Log("menu", "ContactActivity", "onMenuItemSelected", 'i');
		final int nowPosition = info.position;
		switch (item.getItemId()) {
		case 1:// 转发信息
			Intent intent = new Intent(context, NewMsgActivity.class);
			intent.putExtra("content", msgs.get(nowPosition).getContent());
			startActivity(intent);
			finish();
			break;
		case 2:// 删除信息
			new AlertDialog.Builder(context)
					.setTitle("警告")
					.setMessage("确定删除些消息吗?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									refresh();
									changedFlag = true;
									CommonUtil.Toast(context, "删除信息成功!");
								}
							}).setNegativeButton("取消", null).create().show();
			break;
		}
		return true;
	}

	/**
	 * 刷新界面
	 */
	public void refresh() {
	}


	/**
	 * 回调刷新界面
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	/**
	 * onClickListener事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imbMsgCall:// 打电话
			CommonUtil.dial(context, currentNumber);
			break;
		case R.id.btnSendMsg:// 发送短信
			break;
		}
	}

	/**
	 * onTouchListener事件
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// 取得左右滑动时手指按下的X坐标
			touchDownX = event.getX();
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			// 取得左右滑动时手指松开的X坐标
			touchUpX = event.getX();
			// 从左往右，看前一个View
			if (touchUpX - touchDownX > 100) {
				// 设置View切换的动画
				vFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						android.R.anim.slide_in_left));
				vFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						android.R.anim.slide_out_right));
				// 显示下一个View
				vFlipper.showPrevious();
				// 改变当前显示号码
				String str = null;
				str = currentNumber;
				currentNumber = tempNumber;
				tempNumber = str;
				// 从右往左，看后一个View
			} else if (touchDownX - touchUpX > 100) {
				// 设置View切换的动画
				// 由于Android没有提供slide_out_left和slide_in_right，所以仿照slide_in_left和slide_out_right编写了slide_out_left和slide_in_right
				vFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.slide_in_right));
				vFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.slide_out_left));
				// 显示前一个View
				vFlipper.showNext();
				// 改变当前显示号码
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
	 * 自定义Adapter,显示信息列表
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
			if (msg.getMsgMark() == 1)// 接收到的信息
				convertView = inflater.inflate(R.layout.vlist_receive_msgs,
						null);
			else
				convertView = inflater.inflate(R.layout.vlist_send_msgs, null);
			TextView txtDate = (TextView) convertView
					.findViewById(R.id.txtDate);
			TextView txtMsgContent = (TextView) convertView
					.findViewById(R.id.txtMsgContent);
			// 设置信息的时间
			String date = CommonUtil.getNowDate();// 得到当前系统日期
			if (msg.getMsgDate().substring(0, 10).equals(date))
				txtDate.setText(msg.getMsgDate().substring(11));
			else
				txtDate.setText(msg.getMsgDate().substring(0, 10));
			// 设置信息内容
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
