package cn.sqy.contacts.ui;

import java.io.IOException;

import org.apache.http.util.EncodingUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.sqy.contacts.R;
import cn.sqy.contacts.db.ZhaopinDBHelper;
import cn.sqy.contacts.model.HotelDetailBean;
import cn.sqy.contacts.model.ZhaopinBaseBean;
import cn.sqy.contacts.model.ZhaopinDetailBean;
import cn.sqy.contacts.tool.CommonUtil;

public class ZhaopinDetailActivity extends Activity implements OnClickListener, OnItemClickListener{
	private Context context;
	private String TAG = "ZhaopinDetailActivity";
	private ZhaopinBaseBean zhaopinbasebean;
	private ZhaopinDetailBean zhaopindetailbean;
	
	private ImageView title_left ,title_right;
	private TextView title_mid;
	
	private WebView wv;
	
	private ProgressBar progress ;
	private static final int LOADSOURCEDATA = 100;
	private static final int NETWRONG = 101;
	private static final int SHOWPROGRESS = 102;
	private static final  int UNCHOOSE = 103;
	private static final  int CHOOSE = 104;
	
	private boolean store = false;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOADSOURCEDATA:
				progress.setVisibility(View.INVISIBLE);
//				zhaopindetailbean = (ZhaopinDetailBean) msg.obj;
//				refreshView();
				break;
			case NETWRONG:
				progress.setVisibility(View.INVISIBLE);
				CommonUtil.show_toast(context, "网络连接出错", "orange");
				break;
			case SHOWPROGRESS:
				progress.setVisibility(View.VISIBLE);
				break;
			case UNCHOOSE:
				title_right.setBackgroundResource(R.drawable.star_unchoose);
				break;
			case CHOOSE:
				title_right.setBackgroundResource(R.drawable.star_choose);
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhaopin_detail);
		zhaopinbasebean = (ZhaopinBaseBean)getIntent().getSerializableExtra("zhaopinbasebean");
		zhaopindetailbean = new ZhaopinDetailBean();
		zhaopindetailbean.setTitle(zhaopinbasebean.getTitle());
		zhaopindetailbean.setAddress(zhaopinbasebean.getAddress());
		zhaopindetailbean.setUrl(zhaopinbasebean.getUrl());
		
		init();
	}

	public void init(){
		context = ZhaopinDetailActivity.this;
		
		title_left = (ImageView)findViewById(R.id.common_title_left);
		title_right = (ImageView)findViewById(R.id.common_title_right);
		title_mid = (TextView)findViewById(R.id.common_title_mid);
		title_left.setOnClickListener(this);
		title_right.setOnClickListener(this);
		
		wv = (WebView)findViewById(R.id.wv);
		
		progress = (ProgressBar)findViewById(R.id.progress);
		
		ZhaopinDetailBean temp = ZhaopinDBHelper.getInstance(context).getZhaopin(zhaopinbasebean.getUrl());
		if(temp == null){
			store = false;
			handler.sendEmptyMessage(UNCHOOSE);
		}else{
			store = true;
			handler.sendEmptyMessage(CHOOSE);
			zhaopindetailbean = temp;
		}
		if(store){
			wv.loadData(zhaopindetailbean.getContent(), "text/html", "UTF-8");
		}else{
			getData(zhaopinbasebean);
		}
	}
	
	public void refreshView(){
		
	}
	
	public void getData(final ZhaopinBaseBean bean){
		
//		al_small_pics = new ArrayList<String>();
//		al_big_pics = new ArrayList<String>();
		//TODO 判断数据库中有缓存没，有的话就直接显示；
		
		//TODO 数据库中没有进行查询网络，查询结果保存在数据库中
		new Thread(){
			public void run(){
				try {
					Document doc = Jsoup.connect(bean.getUrl()).get();
					Element ele_content = doc.getElementsByClass("content").first();
					StringBuffer sb = new StringBuffer();
//					String begin =" <!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">"+
//					"<html>"+
//					 "<head>"+
//					 "<title> New Document </title>"+ 
//					 "<meta name=\"Generator\" content=\"EditPlus\">"+ 
//					 "<meta name=\"Author\" content=\"\">"+ 
//					 "<meta name=\"Keywords\" content=\"\">"+ 
//					 "<meta name=\"Description\" content=\"\">"+ 
//					 "</head>"+
//					 "<body>";
//					String end = " </body></html>";
//					sb.append(begin+ele_content.html()+end);
					sb.append(ele_content.html());
					wv.getSettings().setDefaultTextEncodingName("UTF-8");
					wv.loadData(ele_content.html(), "text/html", "UTF-8");
					// TODO 插入数据库中
//					Message msg = new Message();
//					msg.what = LOADSOURCEDATA;
//					msg.obj = result;
					
					zhaopindetailbean.setContent(sb.toString());
					
					
					
					handler.sendEmptyMessage(LOADSOURCEDATA);
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
			// 收藏或者已收藏
			if(store){
				// 删除数据库保存
				ZhaopinDBHelper.getInstance(context).deleteZhaopin(zhaopindetailbean);
				handler.sendEmptyMessage(UNCHOOSE);
			}else{
				//  插入数据库
				ZhaopinDBHelper.getInstance(context).addZhaopin(zhaopindetailbean);
				handler.sendEmptyMessage(CHOOSE);
			}
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
