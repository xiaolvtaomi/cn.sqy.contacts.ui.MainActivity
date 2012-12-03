package cn.sqy.contacts.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.sqy.contacts.R;
import cn.sqy.contacts.tool.ContantsUtil;

public class ChooseCityActivity extends Activity implements OnClickListener, OnItemClickListener{
	private String TAG = "ChooseCityActivity";
	private Context context ;
	private ImageView title_left ,title_right;
	private TextView title_mid;
	
	private TextView tv_curentcity;
	private ListView lv;
	private MyCityAdapter adapter;
	private String curentcity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_choose);
		curentcity = getIntent().getStringExtra("city");
		init();
	}
	
	public void init(){
		context = ChooseCityActivity.this;
		
		title_left = (ImageView)findViewById(R.id.common_title_left);
		title_right = (ImageView)findViewById(R.id.common_title_right);
		title_mid = (TextView)findViewById(R.id.common_title_mid);
		title_left.setOnClickListener(this);
		title_right.setOnClickListener(this);
		title_mid.setText("Ñ¡Ôñ³ÇÊÐ");
		
		tv_curentcity = (TextView)findViewById(R.id.city_tv);
		tv_curentcity.setText(curentcity);
		lv = (ListView)findViewById(R.id.lv_city);
		adapter = new MyCityAdapter();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_title_left:
			finish();
			break;
		default:
			break;
		}
	}
	
	class MyCityAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return ContantsUtil.cities.length;
		}

		@Override
		public Object getItem(int position) {
			return ContantsUtil.cities[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.item_city, null);
				holder = new ViewHolder();
				holder.tv = (TextView)convertView.findViewById(R.id.city_tv);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.tv.setText(ContantsUtil.cities[position]);
			return convertView;
		}
		
		class ViewHolder {
			TextView tv;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}
}
