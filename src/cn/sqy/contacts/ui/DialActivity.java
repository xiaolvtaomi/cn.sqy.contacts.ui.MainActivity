package cn.sqy.contacts.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sqy.contacts.R;

public class DialActivity extends Activity implements OnClickListener{
	private Context context;
	private String TAG = "DialActivity";
	ImageButton title_left, title_right, title_mid_ib;
	RelativeLayout title_mid ;
	TextView title_mid_tv;
	
	LinearLayout view_qiuzhi ,view_duanzu, view_zhaodai, view_qingnian, view_jingji;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dial);
		init();
	}
	
	public void init(){
		context = DialActivity.this;
		
		title_left = (ImageButton)findViewById(R.id.title_left);
		title_right= (ImageButton)findViewById(R.id.title_right);
		title_mid = (RelativeLayout)findViewById(R.id.title_mid_rl);
		title_mid_tv = (TextView)findViewById(R.id.title_mid_tv);
		title_mid_ib = (ImageButton)findViewById(R.id.title_mid_ib);
		title_left.setOnClickListener(this);
		title_right.setOnClickListener(this);
		title_mid.setOnClickListener(this);
		
		view_qiuzhi = (LinearLayout)findViewById(R.id.home_qiuzhi);
		view_duanzu = (LinearLayout)findViewById(R.id.home_duanzu);
		view_zhaodai = (LinearLayout)findViewById(R.id.home_zhaodai);
		view_qingnian = (LinearLayout)findViewById(R.id.home_qingnian);
		view_jingji = (LinearLayout)findViewById(R.id.home_jingji);
		view_qiuzhi.setOnClickListener(this);
		view_duanzu.setOnClickListener(this);
		view_qingnian.setOnClickListener(this);
		view_zhaodai.setOnClickListener(this);
		view_jingji.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			
			break;
		case R.id.title_right:
			//TODO 定位
			break;
		case R.id.title_mid_rl:
			//TODO 显示城市选择
			break;
		case R.id.home_qiuzhi:
			//TODO 显示一个界面
			Intent intent_qiuzhi = new Intent(context, QiuzhiListActivity.class);
			startActivity(intent_qiuzhi);
			break;
		case R.id.home_duanzu:
			
			break;
		case R.id.home_zhaodai:
			
			break;
		case R.id.home_qingnian:
			
			break;
		case R.id.home_jingji:
			
			break;

		default:
			break;
		}
	}
}
