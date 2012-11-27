package cn.sqy.contacts.ui;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sqy.contacts.R;
import cn.sqy.contacts.model.HotelBaseBean;

public class QiuzhiListActivity extends Activity implements OnClickListener, OnItemClickListener{
	private Context context ;
	private String TAG = "QiuzhiListActivity";
	
	private ImageButton title_left, title_right, title_mid_iv;
	private TextView title_mid_tv;
	private RelativeLayout title_mid ;
	private ListView lv ;
	private MyAdapter adapter;
	
	private ArrayList<HotelBaseBean> al;
	
	private static final int LOADSOURCEDATA = 100;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOADSOURCEDATA:
				adapter.setData(al);
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qiuzhilist);
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
		context = QiuzhiListActivity.this;
		
		title_left = (ImageButton)findViewById(R.id.title_left);
		title_right = (ImageButton)findViewById(R.id.title_right);
		title_mid_iv = (ImageButton)findViewById(R.id.title_mid_ib);
		title_mid_tv = (TextView)findViewById(R.id.title_mid_tv);
		title_mid = (RelativeLayout)findViewById(R.id.title_mid_rl);
		title_left.setOnClickListener(this);
		title_right.setOnClickListener(this);
		title_mid_iv.setOnClickListener(this);
		title_mid_tv.setOnClickListener(this);
		
		lv = (ListView)findViewById(R.id.qiuzhi_lv);
		adapter = new MyAdapter(context);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		
		al = new ArrayList<HotelBaseBean>();
		getData();
	}
	
	public void getData(){
		Thread thread = new Thread(){
			@Override
			public void run() {
				//TODO 获取html解析其中的参数
				try {
					String city = "北京";
					city = URLEncoder.encode(city, "gbk");
					Log.v(TAG, city);
					Document doc = Jsoup.connect("http://hotel.yingjiesheng.com/infolist.php?city="+city+"&hType=1").get();
					Element container = doc.getElementsByClass("cityHotel").first();
					Log.v(TAG, container.nextElementSibling().text());
					Elements eles_hotel = container.getElementsByTag("li");
					Log.v(TAG, "hotel size = "+eles_hotel.size()+"\n");
					for (int i = 0; i < eles_hotel.size(); i++) {
						Element ele = eles_hotel.get(i);
						HotelBaseBean bean = new HotelBaseBean();
						String iconurl = ele.getElementsByTag("img").first().attr("src");
						Element ele_first = ele.getElementsByTag("p").get(0);
						Element ele_second = ele.getElementsByTag("p").get(1);
						Element ele_title = ele_first.getElementsByTag("a").first();
						String detailurl = ele_title.attr("href");
						String title = ele_title.text();
						String price = ele_first.getElementsByTag("span").first().text();
						String info = ele_second.text();
						String area = ele_first.text().replace(title, "").replace(price, "").trim();
						area = area.substring(0, area.length()-1);
						Log.v(TAG, "----------"+i+"----------");
						Log.v(TAG, "title ==>"+title);
						Log.v(TAG, "price ==>"+price);
						Log.v(TAG, "area ==>"+area);
						Log.v(TAG, "iconurl ==>"+iconurl);
						Log.v(TAG, "detailurl ==>"+detailurl);
						Log.v(TAG, "info ==>"+info);
						bean.setArea(area);
						bean.setDetailurl(detailurl);
						bean.setIconurl(iconurl);
						bean.setInfo(info);
						bean.setPrice(price);
						bean.setTitle(title);
						
						al.add(bean);
					}
					handler.sendEmptyMessage(LOADSOURCEDATA);
				} catch (IOException e) {
					//TODO 网络连接出错
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	
	class MyAdapter extends BaseAdapter{
		ArrayList<HotelBaseBean> data ;
		LayoutInflater li = null;
		private ImageDownloader imageDownloader ;

		public MyAdapter(Context context){
			li = LayoutInflater.from(context);
			data = new ArrayList<HotelBaseBean>();
			imageDownloader =  new ImageDownloader(context);
		}
		
		public void setData(ArrayList<HotelBaseBean> data){
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
				convertView = li.inflate(R.layout.item, null);
				holder = new ViewHolder();
				holder.area = (TextView)convertView.findViewById(R.id.item_area);
				holder.icon = (ImageView)convertView.findViewById(R.id.item_icon);
				holder.info = (TextView)convertView.findViewById(R.id.item_info);
				holder.price = (TextView)convertView.findViewById(R.id.item_price);
				holder.title = (TextView)convertView.findViewById(R.id.item_title);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.title.setText(data.get(position).getTitle());
			holder.area.setText(data.get(position).getArea());
			holder.price.setText(data.get(position).getPrice());
			holder.info.setText(data.get(position).getInfo());
			imageDownloader.download(ConstantBean.ROOT+data.get(position).getIconurl(), holder.icon);
			
			return convertView;
		}
		
		class ViewHolder{
			TextView title, area, price, info;
			ImageView icon;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent_detail = new Intent(context, HotelDetailActivity.class);
		intent_detail.putExtra("hotelbasebean", al.get(position));
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
