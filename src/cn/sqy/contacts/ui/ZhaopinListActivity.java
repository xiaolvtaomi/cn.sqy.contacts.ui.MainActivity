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
import cn.sqy.contacts.model.HotelBaseBean;
import cn.sqy.contacts.tool.CommonUtil;
import cn.sqy.contacts.tool.ContantsUtil;

public class ZhaopinListActivity extends Activity implements OnClickListener, OnItemClickListener, OnScrollListener{
	private Context context ;
	private String TAG = "ZhaopinListActivity";
	
	private ImageButton title_left, title_right, title_mid_iv;
	private TextView title_mid_tv;
	private RelativeLayout title_mid ;
	private ListView lv ;
	private ProgressBar progress ;
	private MyAdapter adapter;
	
	private String city ;
	
	
	private ArrayList<HotelBaseBean> al;
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
		city = getIntent().getStringExtra("city");
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
		lv.setOnScrollListener(this);
		
		al = new ArrayList<HotelBaseBean>();
		getData(city);
	}
	
	public void getData(final String city){
		Thread thread = new Thread(){
			@Override
			public void run() {
				// 获取html解析其中的参数
				try {
					String newcity = URLEncoder.encode(city, "gbk");
					String newdistrict = URLEncoder.encode(district, "gbk");
					String newinfoname = URLEncoder.encode(infoname, "gbk");
					
					Log.v(TAG, "http://hotel.yingjiesheng.com/infolist.php?city="+newcity
							+"&district=" +newdistrict
							+"&hType="+hType
							+"&infoname="+newinfoname
							+"&start="+pageindex*10
							);
					handler.sendEmptyMessage(SHOWPROGRESS);
					Document doc = Jsoup.connect("http://hotel.yingjiesheng.com/infolist.php?city="+newcity
							+"&district=" +newdistrict
							+"&hType="+hType
							+"&infoname="+newinfoname
							+"&start="+pageindex*10
							).get();
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
					// 获取总共有多少个hotel
					Element ele_count = container.nextElementSibling();
					String temp = ele_count.text();
					Log.v(TAG, temp);
					if(count == 0){
						if(!temp.isEmpty() && temp.contains("总记录")){
							String temp_count = temp.substring(4, temp.indexOf("第"));
							Log.v(TAG, "=======count="+temp_count+"="+temp_count.trim()+"="+temp_count.replace(Jsoup.parse("&nbsp;").text(), "")+"=");
							count = Integer.parseInt(temp_count.replace(Jsoup.parse("&nbsp;").text(), "").trim());
						}else{
							count = al.size();
						}
					}
					
					curentIndex++;
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
	
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		visibleCount = visibleItemCount;
		lastindex = firstVisibleItem + visibleItemCount ;
		totalcount = totalItemCount;
		Log.e("========================= ","========================");
//		Log.e("firstVisibleItem = ",firstVisibleItem+"");
//		Log.e("visibleItemCount = ",visibleItemCount+"");
		Log.e("lastindex = ",lastindex+"");
		Log.e("totalcount = ",totalcount+"");
		Log.e("count = ",count+"");
		Log.e("========================= ","========================");
		
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && totalcount == lastindex) {
			Log.v("@@@@@@@@@@@@","@@@@@@@@@@@");
			Log.e("curentIndex = ",curentIndex+"");
			Log.e("count/10 = ",count/10+"");
			if( curentIndex == count/10 ){
				lv.removeFooterView(loadMoreView);
				CommonUtil.show_toast(context, "数据全部加载完", "blue");
			}
			
			
			// 如果是自动加载,可以在这里放置异步加载数据的代码
			progress.setVisibility(View.VISIBLE);
			getData(city, district, hType, infoname, curentIndex);
			Log.v("@@@@@@@@@@@@","@@@@@@@@@@@");
		}
		
	}

}
