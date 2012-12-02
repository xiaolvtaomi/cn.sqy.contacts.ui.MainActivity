package cn.sqy.contacts.ui;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sqy.contacts.R;
import cn.sqy.contacts.tool.CommonUtil;
import cn.sqy.contacts.tool.ContantsUtil;

public class ZhaopinListActivity extends Activity implements OnClickListener, OnItemClickListener{
	private Context context ;
	private String TAG = "ZhaopinListActivity";
	
	private ImageButton title_left, title_right, title_mid_iv;
	private TextView title_mid_tv;
	private RelativeLayout title_mid ;
	private ListView lv ;
	private ProgressBar progress ;
	private MyAdapter adapter;
	
	private String city ;
	
	
	private ArrayList<ZhaopinBean> al;
	//这个模块总共有多少个hotel
	public int count = 0;
	int visibleCount = 0; //当前显示在一个界面中的个数
	int lastindex = 0;//当前显示的最后一项
	int totalcount = 0;//存放总共的个数
	public int curentIndex = 0;
	public LinearLayout loadMoreView ;
	
	private static final int LOADSOURCEDATA = 100;
	private static final int NETWRONG = 101;
	private static final int SHOWPROGRESS = 102;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOADSOURCEDATA:
				adapter.setData(al);
				progress.setVisibility(View.INVISIBLE);
				adapter.notifyDataSetChanged();
				break;
			case NETWRONG:
				progress.setVisibility(View.INVISIBLE);
				CommonUtil.show_toast(context, "网络连接出错", "orange");
				break;
			case SHOWPROGRESS:
				progress.setVisibility(View.VISIBLE);
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhaopinlist);
//		city = getIntent().getStringExtra("city");
		city = "北京";
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	public void init(){
		context = ZhaopinListActivity.this;
		
		title_left = (ImageButton)findViewById(R.id.title_left);
		title_right = (ImageButton)findViewById(R.id.title_right);
		title_mid_iv = (ImageButton)findViewById(R.id.title_mid_ib);
		title_mid_tv = (TextView)findViewById(R.id.title_mid_tv);
		title_mid = (RelativeLayout)findViewById(R.id.title_mid_rl);
		title_left.setOnClickListener(this);
		title_right.setOnClickListener(this);
		title_mid_iv.setOnClickListener(this);
		title_mid_tv.setOnClickListener(this);
		
		progress = (ProgressBar)findViewById(R.id.progress);
		lv = (ListView)findViewById(R.id.zhaopin_lv);
		adapter = new MyAdapter(context);
		loadMoreView = (LinearLayout)getLayoutInflater().inflate(R.layout.loadmore, null);
		lv.addFooterView(loadMoreView);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		
		al = new ArrayList<ZhaopinBean>();
		getData(CommonUtil.getPingYin(city));
	}
	
	public void getData(final String city){
		Thread thread = new Thread(){
			@Override
			public void run() {
				// 获取html解析其中的参数
				try {
					Log.v(TAG, "http://hotel.yingjiesheng.com/infolist.php?city="+city);
					handler.sendEmptyMessage(SHOWPROGRESS);
					Document doc = Jsoup.connect("http://hotel.yingjiesheng.com/infolist.php?city="+city).get();
					Element container = doc.getElementById("mainNav");
					Element table = container.getElementsByTag("table").first();
					if(table != null){
						Elements eles_trs = table.getElementsByTag("tr");
						for (int i = 0; i < eles_trs.size(); i++) {
							Element ele_tr = eles_trs.get(i);
							Elements eles_tds = ele_tr.getElementsByTag("td");
							if(eles_tds.size() == 3){
								ZhaopinBean bean = new ZhaopinBean();
								String url = eles_tds.get(0).absUrl("href");
								String title = eles_tds.get(0).text().toString();
								String address = eles_tds.get(1).text().toString();
								bean.setUrl(url);
								bean.setTitle(title);
								bean.setAddress(address);
								al.add(bean);
							}
						}
					}
					handler.sendEmptyMessage(LOADSOURCEDATA);
				} catch (IOException e) {
					// 网络连接出错
					handler.sendEmptyMessage(NETWRONG);
					
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	
	class MyAdapter extends BaseAdapter{
		ArrayList<ZhaopinBean> data ;
		LayoutInflater li = null;
		private ImageDownloader imageDownloader ;

		public MyAdapter(Context context){
			li = LayoutInflater.from(context);
			data = new ArrayList<ZhaopinBean>();
			imageDownloader =  new ImageDownloader(context);
		}
		
		public void setData(ArrayList<ZhaopinBean> data){
			this.data = data;
		}
		
		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = li.inflate(R.layout.zhaopinitem, null);
				holder = new ViewHolder();
				holder.area = (TextView)convertView.findViewById(R.id.zhaopin_address);
				holder.title = (TextView)convertView.findViewById(R.id.zhaopin_title);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.title.setText(data.get(position).getTitle());
			holder.area.setText(data.get(position).getAddress());
			
			return convertView;
		}
		
		class ViewHolder{
			TextView title, area;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent_detail = new Intent(context, ZhaopinDetailActivity.class);
		intent_detail.putExtra("zhaopinbean", al.get(position));
		startActivity(intent_detail);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			finish();
			break;

		default:
			break;
		}
	}
	
	
}
