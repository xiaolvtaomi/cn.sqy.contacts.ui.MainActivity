package cn.sqy.contacts.ui;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sqy.contacts.R;
import cn.sqy.contacts.model.HotelBaseBean;
import cn.sqy.contacts.model.HotelDetailBean;
import cn.sqy.contacts.tool.CommonUtil;

public class ZhaopinDetailActivity extends Activity implements OnClickListener, OnItemClickListener{
	private Context context;
	private String TAG = "HotelDetailActivity";
	private HotelBaseBean hotelbasebean;
	private HotelDetailBean hoteldetailbean;
	
	private ImageView title_left ,title_right;
	private TextView title_mid;
	
	private TextView name, area_short, price_short;
	private Gallery gallery;
	private MyGalleryAdapter adapter;
	
	private RelativeLayout title_address, title_price, title_bedinfo, title_infolong, title_service, title_contactway;
	private LinearLayout address_ll, price_ll, bedinfo_ll,infolong_ll,service_ll,contactway_ll;
	private TextView address_tv, price_tv, bedinfo_tv, infolong_tv, service_tv, contactway_tv;
	
	private ProgressBar progress ;
	private static final int LOADSOURCEDATA = 100;
	private static final int NETWRONG = 101;
	private static final int SHOWPROGRESS = 102;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOADSOURCEDATA:
				progress.setVisibility(View.INVISIBLE);
				hoteldetailbean = (HotelDetailBean) msg.obj;
				adapter.setData(hoteldetailbean.getSmallimages());
				adapter.notifyDataSetChanged();
				refreshView();
				break;
			case NETWRONG:
				progress.setVisibility(View.INVISIBLE);
				CommonUtil.show_toast(context, "网络连接出错", "orange");
				break;
			case SHOWPROGRESS:
				progress.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotel_detail);
		hotelbasebean = (HotelBaseBean)getIntent().getSerializableExtra("hotelbasebean");
		init();
	}

	public void init(){
		context = ZhaopinDetailActivity.this;
		
		title_left = (ImageView)findViewById(R.id.common_title_left);
		title_right = (ImageView)findViewById(R.id.common_title_right);
		title_mid = (TextView)findViewById(R.id.common_title_mid);
		title_left.setOnClickListener(this);
		title_right.setOnClickListener(this);
		
		name = (TextView)findViewById(R.id.name);
		area_short = (TextView)findViewById(R.id.area);
		price_short = (TextView)findViewById(R.id.price);
		gallery = (Gallery)findViewById(R.id.gallery);
		adapter = new MyGalleryAdapter(context);
		
		title_address = (RelativeLayout)findViewById(R.id.title_address_rl);
		title_price = (RelativeLayout)findViewById(R.id.title_price_rl);
		title_bedinfo = (RelativeLayout)findViewById(R.id.title_bedinfo);
		title_infolong = (RelativeLayout)findViewById(R.id.title_infolong);
		title_service = (RelativeLayout)findViewById(R.id.title_service_rl);
		title_contactway = (RelativeLayout)findViewById(R.id.title_contactway);
		
		address_ll = (LinearLayout)findViewById(R.id.address_ll);
		price_ll = (LinearLayout)findViewById(R.id.price_ll);
		bedinfo_ll = (LinearLayout)findViewById(R.id.bedinfo_ll);
		infolong_ll = (LinearLayout)findViewById(R.id.infolong_ll);
		service_ll = (LinearLayout)findViewById(R.id.service_ll);
		contactway_ll = (LinearLayout)findViewById(R.id.contactway_ll);
		
		address_tv = (TextView)findViewById(R.id.address_tv); 
		price_tv = (TextView)findViewById(R.id.priceinfo_tv);
		bedinfo_tv  = (TextView)findViewById(R.id.bedinfo_tv);
		infolong_tv = (TextView)findViewById(R.id.infolong_tv);
		service_tv = (TextView)findViewById(R.id.service_tv);
		contactway_tv = (TextView)findViewById(R.id.contactway_tv);
		
		address_tv.setOnClickListener(this);
		price_tv.setOnClickListener(this);
		bedinfo_tv.setOnClickListener(this);
		infolong_tv.setOnClickListener(this);
		service_tv.setOnClickListener(this);
		contactway_tv.setOnClickListener(this);
		
		name.setText(hotelbasebean.getTitle());
		area_short.setText(hotelbasebean.getArea());
		price_short.setText(hotelbasebean.getPrice());
		gallery.setAdapter(adapter);
		gallery.setOnItemClickListener(this);
		
		progress = (ProgressBar)findViewById(R.id.progress);
		
		getData(hotelbasebean);
	}
	
	public void refreshView(){
		if(CommonUtil.isAvailable(hoteldetailbean.getAddress())){
			address_tv.setText(hoteldetailbean.getAddress());
		}else{
			title_address.setVisibility(View.GONE);
			address_ll.setVisibility(View.GONE);
		}
		
		if(CommonUtil.isAvailable(hoteldetailbean.getBedinfo())){
			bedinfo_tv.setText(hoteldetailbean.getBedinfo());
		}else{
			title_bedinfo.setVisibility(View.GONE);
			bedinfo_ll.setVisibility(View.GONE);
		}
		
		if(CommonUtil.isAvailable(hoteldetailbean.getInfo_long())){
			infolong_tv.setText(hoteldetailbean.getInfo_long());
		}else{
			title_infolong.setVisibility(View.GONE);
			infolong_ll.setVisibility(View.GONE);
		}
		
		if(CommonUtil.isAvailable(hoteldetailbean.getPriceinfo())){
			price_tv.setText(hoteldetailbean.getPriceinfo());
		}else{
			title_price.setVisibility(View.GONE);
			price_tv.setVisibility(View.GONE);
		}
		
		if(CommonUtil.isAvailable(hoteldetailbean.getService())){
			service_tv.setText(hoteldetailbean.getService());
		}else{
			title_service.setVisibility(View.GONE);
			service_ll.setVisibility(View.GONE);
		}
	}
	
	public void getData(final HotelBaseBean bean){
		
//		al_small_pics = new ArrayList<String>();
//		al_big_pics = new ArrayList<String>();
		//TODO 判断数据库中有缓存没，有的话就直接显示；
		
		//TODO 数据库中没有进行查询网络，查询结果保存在数据库中
		new Thread(){
			public void run(){
				try {
					Document doc = Jsoup.connect("http://hotel.yingjiesheng.com/"+bean.getDetailurl()).get();
					Element ele_hotelDetail = doc.getElementsByClass("hotelDetail").first();
					handler.sendEmptyMessage(SHOWPROGRESS);
					HotelDetailBean result = new HotelDetailBean();
					String address = "";
					String price = "";
					String bed = "";
					String attr = "";
					String service = "";
					String al_small_pics = "";
					String al_big_pics = "";
					
					//获取图片
					Element ele_picList = ele_hotelDetail.getElementsByClass("picList").first();
					Elements ele_pics = ele_picList.getElementsByTag("td");
					for (int i = 0; i < ele_pics.size(); i++) {
						Element pic = ele_pics.get(i);
						String smallpic = pic.getElementsByTag("img").first().attr("src");
						if(CommonUtil.isAvailable(smallpic)){
							String bigpic = pic.getElementsByTag("a").first().attr("href");
							al_small_pics += smallpic+"-";
							al_big_pics += bigpic+"-";
						}
					}
					Element ele_hotelInfo = ele_hotelDetail.getElementsByClass("hotelInfor").first();
					Elements ele_infos = ele_hotelInfo.getElementsByTag("tr");
					
					for (int i = 0; i < ele_infos.size(); i++) {
						Element temp = ele_infos.get(i);
						String head = temp.getElementsByTag("th").first().text();
						
						if(head.contains("地理位置")){
							address = temp.getElementsByTag("td").first().text();
							address = address.replace("<br />", "\n").replace("<br/>", "\n");
						}else if(head.contains("住宿价格")){
							price = temp.getElementsByTag("td").first().text();
							price = price.replace("<br />", "\n").replace("<br/>", "\n");
						}else if(head.contains("床位情况")){
							bed = temp.getElementsByTag("td").first().text();
							bed = bed.replace("<br />", "\n").replace("<br/>", "\n");
						}else if(head.contains("旅社简介")){
							attr = temp.getElementsByTag("td").first().text();
							attr = attr.replace("<br />", "\n").replace("<br/>", "\n");
						}else if(head.contains("设施服务")){
							service = temp.getElementsByTag("td").first().text();
							service = service.replace("<br />", "\n").replace("<br/>", "\n");
						}
					}
					result.setAddress(address);
					result.setPriceinfo(price);
					result.setBedinfo(bed);
					result.setInfo_long(attr);
					result.setService(service);
//					result.setContactway(contactway);
					result.setBigimages(al_big_pics);
					result.setSmallimages(al_small_pics);
					
					result.setArea_short(bean.getArea());
					result.setInfo_short(bean.getInfo());
					result.setPrice_short(bean.getPrice());
					result.setTitle(bean.getTitle());
					result.setUrl(bean.getDetailurl());
					// TODO 插入数据库中
					Message msg = new Message();
					msg.what = LOADSOURCEDATA;
					msg.obj = result;
					handler.sendMessage(msg);
				} catch (IOException e) {
					handler.sendEmptyMessage(NETWRONG);				
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.address_tv:
			break;
		case R.id.priceinfo_tv:
			break;
		case R.id.bedinfo_tv:
			break;
		case R.id.infolong_tv:
			break;
		case R.id.service_tv:
			break;
		case R.id.contactway_tv:
			
			break;
		case R.id.common_title_left:
			finish();
			break;
		case R.id.common_title_right:
			//TODO 收藏或者已收藏
			break;
		default:
			break;
		}
	}
	
	class MyGalleryAdapter extends BaseAdapter{
		private Context context ;
		private String[] urls;
		private ImageDownloader imageDownloader ;
		private LayoutInflater li ;
		public MyGalleryAdapter(Context context){
			this.context = context;
			urls = new String[]{};
			li = LayoutInflater.from(context);
			imageDownloader = new ImageDownloader(context);
		}
		
		public void setData(String urls){
			this.urls = urls.split("-");
		}
		
		@Override
		public int getCount() {
			return urls.length;
		}
		@Override
		public Object getItem(int position) {
			return urls[position];
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				convertView = li.inflate(R.layout.galleryitem, null);
				holder = new ViewHolder();
				holder.iv = (ImageView)convertView.findViewById(R.id.galleryitem_iv);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			imageDownloader.download(ConstantBean.ROOT+urls[position], holder.iv);
			return convertView;
		}
		
		class ViewHolder {
			ImageView iv;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO 显示大图片
	}
}
