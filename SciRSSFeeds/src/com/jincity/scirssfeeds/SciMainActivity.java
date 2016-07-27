package com.jincity.scirssfeeds;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jincity.common.name.DatabaseNameCom;
import com.jincity.common.name.GlobalFlag;
import com.jincity.common.name.GlobalSize;
import com.jincity.common.name.NameStatic;
import com.jincity.control.db.DBopearte;
import com.jincity.rss.article.ArticleList;
import com.jincity.rss.article.CollectionArticle;
import com.jincity.rss.magazine.SubMagazine;
import com.jincity.rss.setting.Setting;
import com.jincity.rss.setting.SettingList;
import com.jincity.service.auto.AutoClearArticles;
import com.jincity.service.auto.AutoUpdateNews;
import com.jincity.type.change.SettingTypeChange;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "HandlerLeak", "ShowToast", "CutPasteId" })
public class SciMainActivity extends Activity {

	private static final String TAG_STRING = "SciMainActivity_TAG";

	private TextView titleNameTextView;// 界面标题
	private TextView titleRightTextView;// 界面右端(文章数目或设置按钮)

	private ListView showListView;// 显示列表
	private TextView selectMainPage;// 首页按钮
	private TextView selectCollection;// 收藏按钮
	private TextView selectPlus;// 增减按钮
	private TextView selectupdate;// 更新按钮
	private RelativeLayout myRelativeLayout;// 当前Activity的布局

	selectOnClickListener theOnClickListener;

	private ArrayList<SubMagazine> subMagazines;// 已订阅的期刊列表
	private ArrayList<CollectionArticle> collectionArticles;// 收藏的文章列表
	private ArrayList<ArticleList> currentArticleLists;// 当前选中期刊的文章列表
	private String currentMagazineJID;// 当前选中的期刊的期刊JID
	private String currentMagazineName;// 当前选中的期刊的期刊名称
	private int currentMagazineNum;// 当前期刊文章总数
	private int currentposition;// 主页界面当前选择的item行数
	private boolean isUpdate = false;// 当前是否执行更新(默认为否)
	private Setting theSetting;// 软件的设置
	private ArrayList<SettingList> settingLists;// 设置的list列表

	private String theHiddenImage = "http://www.weisci.com/";

	DBopearte dBopearte;// 业务层操作

	Handler mainHandler;// 线程通信
	private static final int FIRST_USE = -1;// 首次使用信号
	private static final int READY_FINSIH = 999;// 数据初始化完毕,允许按钮监听
	private static final int MAINPAGE_READY = 1;// 主页显示准备完毕
	private static final int MAGAZINE_LIST_READY = 3;// 文章列表准备完毕
	private static final int COL_ARTICLE_READY = 4;// 收藏文章列表准备完毕
	private static final int UPDATE_FINISH = 5;// 更新完毕
	private static final int SETTING_READY = 7;// 设置列表准备完毕
	private static final int BEGINAUTOUPDATE = 8;// 开始自动更新
	private static final int BEGINAUTOCLEAR = 9;// 开始自动清除
	private static final int CANCALAUTOUPDATE = 10;// 取消自动更新提醒
	private static final int CANCALAUTOCLEAR = 11;// 取消自动清除提醒
	private static final int UPDATE_BEGIN_INFO = 12;// 开始更新信息
	private static final int THE_CON_NOT_CON = 13;// 当前没有网络连接
	private static final int UPDATE_NEW_INFO = 14;// 当前已经是最新的更新了

	private int currentPage = GlobalFlag.MAIN_PAGE;// 当前页面

	// 后台定时服务
	private Intent autoUpdateIntent;// 自动更新
	private Intent autoClearIntent;// 自动清除

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); // 声明使用自定义标题
		setContentView(R.layout.activity_sci_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);// 自定义布局赋值
		myRelativeLayout = (RelativeLayout) findViewById(R.id.mainactivitylayout);
		myRelativeLayout.setBackgroundColor(Color.WHITE);

		// 绑定界面id
		titleNameTextView = (TextView) findViewById(R.id.title_name);
		titleRightTextView = (TextView) findViewById(R.id.title_right);

		selectMainPage = (TextView) findViewById(R.id.select_mainpage);
		selectCollection = (TextView) findViewById(R.id.select_collection);
		selectPlus = (TextView) findViewById(R.id.select_plus);
		selectupdate = (TextView) findViewById(R.id.select_update);
		showListView = (ListView) findViewById(R.id.list);

		theOnClickListener = new selectOnClickListener();

		// 通信获取
		mainHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {
				case FIRST_USE:
					Toast.makeText(SciMainActivity.this, "温馨提示:首次使用初始化数据较多,请稍后", Toast.LENGTH_SHORT).show();
					break;
				case READY_FINSIH:
					// 绑定按钮监听事件
					selectMainPage.setOnClickListener(theOnClickListener);
					selectCollection.setOnClickListener(theOnClickListener);
					selectPlus.setOnClickListener(theOnClickListener);
					selectupdate.setOnClickListener(theOnClickListener);
					titleRightTextView.setOnClickListener(theOnClickListener);
					break;
				case MAINPAGE_READY:
					currentPage = GlobalFlag.MAIN_PAGE;
					titleNameTextView.setText(NameStatic.MAIN_TITLE_STRING);
					titleNameTextView.setTextSize(GlobalSize.title_normal_size);
					titleRightTextView.setText("设置");
					titleRightTextView.setVisibility(View.VISIBLE);
					titleRightTextView.setClickable(true);
					titleRightTextView.setOnClickListener(theOnClickListener);
					showListView.setAdapter(null);
					showMainPageListView();// 显示订阅的期刊
					break;
				case MAGAZINE_LIST_READY:
					currentPage = GlobalFlag.MAGAZINE_LIST_PAGE;
					titleNameTextView.setText(currentMagazineName);
					titleNameTextView.setTextSize(GlobalSize.title_small_size);
					titleRightTextView.setVisibility(View.INVISIBLE);
					titleRightTextView.setClickable(false);
					titleRightTextView.setOnClickListener(null);
					showListView.setAdapter(null);
					showMagazineListPageListView();// 显示当前期刊的所有文章界面
					break;
				case COL_ARTICLE_READY:
					currentPage = GlobalFlag.COL_PAGE;
					titleNameTextView.setText(NameStatic.COLLECTION_STRING);
					titleNameTextView.setTextSize(GlobalSize.title_normal_size);
					titleRightTextView.setVisibility(View.INVISIBLE);
					titleRightTextView.setClickable(false);
					titleRightTextView.setOnClickListener(null);
					showListView.setAdapter(null);
					ShowColPageListView();// 显示收藏文章的页面
					break;
				case UPDATE_FINISH:
					Toast.makeText(SciMainActivity.this, "更新完毕,请尽情阅读吧", Toast.LENGTH_SHORT).show();
					isUpdate = false;
					showMainPageList();// 跳转至主页
					break;
				case SETTING_READY:
					currentPage = GlobalFlag.SETTING_PAGE;
					titleNameTextView.setText(NameStatic.SETTING_STRING);
					titleNameTextView.setTextSize(GlobalSize.title_normal_size);
					titleRightTextView.setVisibility(View.INVISIBLE);
					titleRightTextView.setClickable(false);
					titleRightTextView.setOnClickListener(null);
					showListView.setAdapter(null);
					showSettingLisetView();// 显示设置列表
					break;
				case BEGINAUTOUPDATE:
					Toast.makeText(SciMainActivity.this, "已开启每日自动更新", Toast.LENGTH_SHORT).show();
					break;
				case BEGINAUTOCLEAR:
					Toast.makeText(SciMainActivity.this, "已开启每周自动清除", Toast.LENGTH_SHORT).show();
					break;
				case CANCALAUTOUPDATE:
					Toast.makeText(SciMainActivity.this, "已取消自动更新", Toast.LENGTH_SHORT).show();
					break;
				case CANCALAUTOCLEAR:
					Toast.makeText(SciMainActivity.this, "已取消自动清除", Toast.LENGTH_SHORT).show();
					break;
				case UPDATE_BEGIN_INFO:
					Toast.makeText(SciMainActivity.this, Html.fromHtml("正在开始更新...请稍后<br/>请勿强制退出<br/>请勿断开网络连接"), Toast.LENGTH_LONG).show();
					break;
				case THE_CON_NOT_CON:
					Toast.makeText(SciMainActivity.this, Html.fromHtml("当前无网络连接<br/>请检查网络"), Toast.LENGTH_SHORT).show();
					break;
				case UPDATE_NEW_INFO:
					isUpdate = false;
					Toast.makeText(SciMainActivity.this, Html.fromHtml("目前已经是最新订阅"), Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}

		};
		// 启动线程,初始化数据库及初始UI界面
		initializeDatabase inDatabase = new initializeDatabase();
		Thread initializeThread = new Thread(inDatabase);
		initializeThread.start();

	}

	/**
	 * show 用户点击按钮事件类
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class selectOnClickListener implements OnClickListener {

		@Override
		public void onClick(View theid) {
			switch (theid.getId()) {
			case R.id.select_mainpage:
				// 点击主页按钮,显示已订阅的期刊列表
				// 启动线程,从数据库读取所有订阅的期刊
				if (!isUpdate) {
					showMainPageList();
				}
				break;
			case R.id.select_plus:
				// 点击增减按钮,跳转至增减订阅期刊界面
				if (!isUpdate) {
					Intent intent = new Intent(SciMainActivity.this, SciSubMagazine.class);
					startActivityForResult(intent, 0);
				}
				break;
			case R.id.select_collection:
				// 点击收藏按钮，显示所有收藏的文章
				// 从数据库中读取所有收藏的文章列表
				if (!isUpdate) {
					showColPageList();
				}
				break;
			case R.id.select_update:
				// 更新过程中,禁止操作
				// 点击更新,更新最新Rss信息
				// 从数据库中删除已订阅期刊的所有文章，然后将已收藏的文章写回
				// 重新获取最新的rss信息写入数据库
				if (subMagazines.size() > 0) {
					if (!isUpdate) {
						// 检查是否联网
						judgeCon ajuCon = new judgeCon();
						Thread jThread = new Thread(ajuCon);
						jThread.start();
					}
				}
				break;
			case R.id.title_right:
				// 点击设置，显示设置菜单
				if (!isUpdate) {
					theSetting = dBopearte.getTheSetting();
					SettingTypeChange aSettingTypeChange = new SettingTypeChange();
					settingLists = aSettingTypeChange.getSettingLists(theSetting);

					Message settingReady = new Message();
					settingReady.what = SETTING_READY;
					mainHandler.sendMessage(settingReady);
				}
				break;
			default:
				break;
			}

		}

	}

	/**
	 * show 初始数据库线程
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class initializeDatabase implements Runnable {
		@Override
		public void run() {
			initialize();// 初始化数据库及初始界面显示线程
			// 通知主线程数据初始化完毕,开启按钮监听事件
			Message listenerMessage = new Message();
			listenerMessage.what = READY_FINSIH;
			mainHandler.sendMessage(listenerMessage);
		}
	}

	/**
	 * show 将已订阅的期刊列表同主页listView绑定并显示
	 */
	private void showMainPageListView() {

		if (subMagazines == null) {
			Toast.makeText(SciMainActivity.this, "rss源错误", Toast.LENGTH_SHORT).show();
			return;
		}
		if (subMagazines.size() <= 0) {
			Toast.makeText(SciMainActivity.this, "当前无订阅的期刊", Toast.LENGTH_SHORT).show();
			showListView.setAdapter(null);
			showListView.setOnItemClickListener(null);
		} else {
			theSubMagazineAdapter simpleAdapter = new theSubMagazineAdapter(SciMainActivity.this);
			showListView.setAdapter(simpleAdapter);
			PageItemListener theItemListener = new PageItemListener();
			showListView.setOnItemClickListener(theItemListener);
			showListView.setSelection(0);
		}
	}

	/**
	 * show 将所有收藏的文章同listView绑定并显示
	 */
	private void ShowColPageListView() {

		if (collectionArticles == null) {
			Toast.makeText(SciMainActivity.this, "收藏列表读取错误", Toast.LENGTH_SHORT).show();
			return;
		}
		if (collectionArticles.size() <= 0) {
			Toast.makeText(SciMainActivity.this, "没有收藏文章", Toast.LENGTH_SHORT).show();
			return;
		}

		ColArticleAdapter simpleAdapter = new ColArticleAdapter(SciMainActivity.this);
		showListView.setAdapter(simpleAdapter);
		PageItemListener oneItemListener = new PageItemListener();
		showListView.setOnItemClickListener(oneItemListener);
		showListView.setSelection(0);

	}

	/**
	 * show 将指定期刊的所有文章同listView绑定并显示
	 */
	private void showMagazineListPageListView() {

		String colTagString = "colTag";
		String articleNameString = "articleName";
		String aricleEnNameString = "articleEnName";
		String aritcleMagazineString = "aricleMagazine";
		String ariclepubDateString = "aritclepubDate";

		if (currentArticleLists == null) {
			Toast.makeText(SciMainActivity.this, "期刊数据错误", Toast.LENGTH_SHORT).show();
			return;
		}
		if (currentArticleLists.size() <= 0) {
			Toast.makeText(SciMainActivity.this, "该期刊没有文章", Toast.LENGTH_SHORT).show();
			showMainPageList();// 跳转至主页
		} else {
			List<Map<String, Object>> magazineArticleList = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < currentArticleLists.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				String colString;
				if (currentArticleLists.get(i).getArticleCollection() == GlobalFlag.article_collection_flag_true) {
					colString = GlobalFlag.ARTICLE_COL_TRUE_STRING;
				} else {
					colString = GlobalFlag.ARTICLE_COL_FALSE_STRING;
				}
				map.put(colTagString, colString);
				map.put(articleNameString, currentArticleLists.get(i).getArticleName());
				map.put(aricleEnNameString, currentArticleLists.get(i).getArticleEnName());
				map.put(aritcleMagazineString, currentMagazineName);
				map.put(ariclepubDateString, currentArticleLists.get(i).getArticlePubDate());
				magazineArticleList.add(map);
			}
			MagazineListAdapter simpleAdapter = new MagazineListAdapter(SciMainActivity.this, magazineArticleList);
			showListView.setAdapter(simpleAdapter);
			PageItemListener oneItemListener = new PageItemListener();
			showListView.setOnItemClickListener(oneItemListener);
			showListView.setSelection(0);
		}

	}

	/**
	 * show 将设置列表同listview绑定并显示
	 */
	private void showSettingLisetView() {
		theSettingAdapter simAdapter = new theSettingAdapter(SciMainActivity.this);
		showListView.setAdapter(simAdapter);
		showListView.setOnItemClickListener(null);
	}

	/**
	 * show 构建设置列表的映射
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class theSettingAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater layoutInflater;

		// 构造方法
		public theSettingAdapter(Context context) {
			this.context = context;
			layoutInflater = LayoutInflater.from(this.context);
		}

		@Override
		public int getCount() {
			return settingLists != null ? settingLists.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return settingLists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final int currentLine = position;

			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.settinglistview, null);
			}

			if (position == 8) {// 最后一个非选项
				TextView settingNameTV = (TextView) convertView.findViewById(R.id.settting_name);
				CheckBox setCheckBox = (CheckBox) convertView.findViewById(R.id.setting_choise);
				setCheckBox.setVisibility(View.GONE);// 隐藏选项按钮

				settingNameTV.setTextColor(Color.BLACK);

				// 设置字体大小
				if (theSetting.getUseBigWord() == GlobalFlag.use_big_word_true) {
					settingNameTV.setTextSize(GlobalSize.setting_big_size);
				} else {
					settingNameTV.setTextSize(GlobalSize.setting_simple_size);
				}

				// 设置显示
				settingNameTV.setText(settingLists.get(position).getSettingName());
			} else {

				TextView settingNameTV = (TextView) convertView.findViewById(R.id.settting_name);
				CheckBox settingCK = (CheckBox) convertView.findViewById(R.id.setting_choise);

				settingNameTV.setTextColor(Color.BLACK);

				// 设置字体大小
				if (theSetting.getUseBigWord() == GlobalFlag.use_big_word_true) {
					settingNameTV.setTextSize(GlobalSize.setting_big_size);
				} else {
					settingNameTV.setTextSize(GlobalSize.setting_simple_size);
				}

				// 设置显示
				settingNameTV.setText(settingLists.get(position).getSettingName());
				if (settingLists.get(position).getSettingValue() == GlobalFlag.setting_true) {
					settingCK.setChecked(true);
				} else {
					settingCK.setChecked(false);
				}

				settingCK.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton button, boolean isChecked) {
						if (isChecked) {
							// 被选择
							settingLists.get(currentLine).setSettingValue(GlobalFlag.setting_true);
							if (currentLine == 6) {// 选择了自动更新选项
								autoUpdateIntent = new Intent(SciMainActivity.this, AutoUpdateNews.class);
								startService(autoUpdateIntent);

								Message beginMessage = new Message();
								beginMessage.what = BEGINAUTOUPDATE;
								mainHandler.sendMessage(beginMessage);
							} else if (currentLine == 7) {// 选择了自动清除选项
								autoClearIntent = new Intent(SciMainActivity.this, AutoClearArticles.class);
								startService(autoClearIntent);

								Message beginMessage = new Message();
								beginMessage.what = BEGINAUTOCLEAR;
								mainHandler.sendMessage(beginMessage);
							}
						} else {
							// 未被选择
							settingLists.get(currentLine).setSettingValue(GlobalFlag.setting_false);

							if (currentLine == 6) {// 取消了自动更新选项
								autoUpdateIntent = new Intent(SciMainActivity.this, AutoUpdateNews.class);
								startService(autoUpdateIntent);// 预先启动获得对应的Service
								if (autoUpdateIntent != null) {
									stopService(autoUpdateIntent);
									Message cancleAutoMessage = new Message();
									cancleAutoMessage.what = CANCALAUTOUPDATE;
									mainHandler.sendMessage(cancleAutoMessage);
								}
							} else if (currentLine == 7) {// 取消了自动清除选项
								autoClearIntent = new Intent(SciMainActivity.this, AutoClearArticles.class);
								startService(autoClearIntent);// 预先启动获得对应的Service
								if (autoClearIntent != null) {
									stopService(autoClearIntent);
									Message cancleAutoMessage = new Message();
									cancleAutoMessage.what = CANCALAUTOCLEAR;
									mainHandler.sendMessage(cancleAutoMessage);
								}
							}
						}
						SettingTypeChange aSettingTypeChange = new SettingTypeChange();
						theSetting = aSettingTypeChange.getSetting(settingLists);
						dBopearte.setTheSetting(theSetting);
						theSetting = dBopearte.getTheSetting();
						settingLists = aSettingTypeChange.getSettingLists(theSetting);
						notifyDataSetChanged();// 通知适配器被修改
					}
				});
			}
			return convertView;
		}

	}

	/**
	 * show 构建已订阅列表映射
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class theSubMagazineAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater layoutInflater;

		// 构造方法
		public theSubMagazineAdapter(Context context) {
			this.context = context;
			layoutInflater = LayoutInflater.from(this.context);
		}

		@Override
		public int getCount() {
			return subMagazines != null ? subMagazines.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return subMagazines.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.mainpagelistview, null);
			}

			TextView magazineName = (TextView) convertView.findViewById(R.id.magazineName);
			TextView magazineNum = (TextView) convertView.findViewById(R.id.magazineNum);

			// 设置字体大小
			if (theSetting.getUseBigWord() == GlobalFlag.use_big_word_true) {
				magazineName.setTextSize(GlobalSize.word_big_size);
				magazineNum.setTextSize(GlobalSize.word_big_size);
			} else {
				magazineName.setTextSize(GlobalSize.word_simple_size);
				magazineNum.setTextSize(GlobalSize.word_simple_size);
			}
			// 设置字体为黑色
			magazineName.setTextColor(Color.BLACK);
			magazineNum.setTextColor(Color.BLACK);

			magazineName.setText(subMagazines.get(position).getMagazineName());
			magazineNum.setText(subMagazines.get(position).getMagazineCollectionNum() + ":" + subMagazines.get(position).getMagazineNum());

			return convertView;
		}

	}

	/**
	 * show 构建收藏列表的映射
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class ColArticleAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater layoutInflater;

		// 构造方法，参数list传递的就是这一组数据的信息
		public ColArticleAdapter(Context context) {
			this.context = context;
			layoutInflater = LayoutInflater.from(this.context);
		}

		@Override
		public int getCount() {
			return collectionArticles != null ? collectionArticles.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return collectionArticles.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.colarticlelistview, null);
			}

			TextView artNameTV = (TextView) convertView.findViewById(R.id.col_articlename);
			TextView artENameTV = (TextView) convertView.findViewById(R.id.col_articleenname);
			TextView artMagNameTV = (TextView) convertView.findViewById(R.id.col_magname);
			TextView artPubDateTV = (TextView) convertView.findViewById(R.id.col_artpubdate);

			// 设置字体大小
			if (theSetting.getUseBigWord() == GlobalFlag.use_big_word_true) {
				artNameTV.setTextSize(GlobalSize.name_big_size);
				artENameTV.setTextSize(GlobalSize.enname_big_size);
				artMagNameTV.setTextSize(GlobalSize.magazinename_big_size);
				artPubDateTV.setTextSize(GlobalSize.date_big_size);
			} else {
				artNameTV.setTextSize(GlobalSize.name_simple_size);
				artENameTV.setTextSize(GlobalSize.enname_simple_size);
				artMagNameTV.setTextSize(GlobalSize.magazinename_simple_size);
				artPubDateTV.setTextSize(GlobalSize.date_simple_size);
			}

			// 设置字体颜色
			artNameTV.setTextColor(Color.BLACK);
			artENameTV.setTextColor(Color.BLACK);
			artMagNameTV.setTextColor(Color.BLACK);
			artPubDateTV.setTextColor(Color.BLACK);

			// 设置内容
			artNameTV.setText(collectionArticles.get(position).getArticleName());
			artENameTV.setText(collectionArticles.get(position).getArticleEnName());
			artMagNameTV.setText(collectionArticles.get(position).getArticleMagazineName());
			artPubDateTV.setText(collectionArticles.get(position).getArticlePubDate());

			return convertView;
		}

	}

	/**
	 * show 构建指定期刊的所有文章显示列表
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class MagazineListAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater layoutInflater;
		private List<Map<String, Object>> list;

		String colTagString = "colTag";
		String articleNameString = "articleName";
		String aricleEnNameString = "articleEnName";
		String aritcleMagazineString = "aricleMagazine";
		String ariclepubDateString = "aritclepubDate";

		// 构造方法，参数list传递的就是这一组数据的信息
		public MagazineListAdapter(Context context, List<Map<String, Object>> list) {
			this.context = context;
			layoutInflater = LayoutInflater.from(this.context);
			this.list = list;
		}

		@Override
		public int getCount() {
			return this.list != null ? this.list.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return this.list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int currentLine = position;

			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.magazineartcilelistview, null);
			}

			TextView colTextView = (TextView) convertView.findViewById(R.id.collectionTag);
			TextView articleNameTextView = (TextView) convertView.findViewById(R.id.articlename);
			TextView articleEnNameTextView = (TextView) convertView.findViewById(R.id.articleenname);
			TextView articleMagazineTextView = (TextView) convertView.findViewById(R.id.articleMagazine);
			TextView aritclepubDaTextView = (TextView) convertView.findViewById(R.id.articlePubTime);

			// 设置字体大小
			if (theSetting.getUseBigWord() == GlobalFlag.use_big_word_true) {
				colTextView.setTextSize(GlobalSize.col_big_size);
				articleNameTextView.setTextSize(GlobalSize.name_big_size);
				articleEnNameTextView.setTextSize(GlobalSize.enname_big_size);
				articleMagazineTextView.setTextSize(GlobalSize.magazinename_big_size);
				aritclepubDaTextView.setTextSize(GlobalSize.date_big_size);

			} else {
				colTextView.setTextSize(GlobalSize.col_simple_size);
				articleNameTextView.setTextSize(GlobalSize.name_simple_size);
				articleEnNameTextView.setTextSize(GlobalSize.enname_simple_size);
				articleMagazineTextView.setTextSize(GlobalSize.magazinename_simple_size);
				aritclepubDaTextView.setTextSize(GlobalSize.date_simple_size);
			}
			// 设置字体为黑色
			colTextView.setTextColor(Color.BLACK);
			articleNameTextView.setTextColor(Color.BLACK);
			articleEnNameTextView.setTextColor(Color.BLACK);
			articleMagazineTextView.setTextColor(Color.BLACK);
			aritclepubDaTextView.setTextColor(Color.BLACK);

			// 设置文本内容
			colTextView.setText(list.get(position).get(colTagString).toString());
			articleNameTextView.setText(list.get(position).get(articleNameString).toString());
			articleEnNameTextView.setText(list.get(position).get(aricleEnNameString).toString());
			articleMagazineTextView.setText(list.get(position).get(aritcleMagazineString).toString());
			aritclepubDaTextView.setText(list.get(position).get(ariclepubDateString).toString());

			// 收藏按钮监听事件
			if (list.get(position).get(colTagString).toString().equals(GlobalFlag.ARTICLE_COL_TRUE_STRING)) {
				// 如果已收藏则取消收藏
				colTextView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 将收藏标志改为未收藏
						currentArticleLists.get(currentLine).setArticleCollection(GlobalFlag.article_collection_flag_false);
						// 执行取消收藏一篇文章的业务
						dBopearte.cancleCollectionOneArtcile(currentMagazineJID, currentArticleLists.get(currentLine).getArticleGID());
						// 构建显示列表中的每一列的内容
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(colTagString, GlobalFlag.ARTICLE_COL_FALSE_STRING);
						map.put(articleNameString, currentArticleLists.get(currentLine).getArticleName());
						map.put(aricleEnNameString, currentArticleLists.get(currentLine).getArticleEnName());
						map.put(aritcleMagazineString, currentMagazineName);
						map.put(ariclepubDateString, currentArticleLists.get(currentLine).getArticlePubDate());
						list.set(currentLine, map);
						notifyDataSetChanged();// 通知适配器被修改
					}
				});
			} else {
				// 如果未收藏则改为已收藏
				colTextView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 将文章的收藏标志改为已收藏
						currentArticleLists.get(currentLine).setArticleCollection(GlobalFlag.article_collection_flag_true);
						// 执行收藏一篇文章的业务
						dBopearte.collectionOneArtcile(currentMagazineJID, currentArticleLists.get(currentLine));
						// 构建显示列表中的每一列的内容
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(colTagString, GlobalFlag.ARTICLE_COL_TRUE_STRING);
						map.put(articleNameString, currentArticleLists.get(currentLine).getArticleName());
						map.put(aricleEnNameString, currentArticleLists.get(currentLine).getArticleEnName());
						map.put(aritcleMagazineString, currentMagazineName);
						map.put(ariclepubDateString, currentArticleLists.get(currentLine).getArticlePubDate());
						list.set(currentLine, map);
						notifyDataSetChanged();// 通知适配器被修改
					}
				});
			}

			return convertView;
		}
	}

	/**
	 * show 点击列表触发事件
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class PageItemListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
			if (!isUpdate) {
				if (currentPage == GlobalFlag.MAIN_PAGE) {
					currentposition = position;
					// 如果当前是主页面,点击list后打开该期刊查看文章列表
					showMagazineList();

					getPrivateImage aPrivateImage = new getPrivateImage();
					aPrivateImage.setJID(currentMagazineJID);
					Thread aThread = new Thread(aPrivateImage);
					aThread.start();

				} else if (currentPage == GlobalFlag.MAGAZINE_LIST_PAGE) {
					// 如果当前是文章列表界面,点击list后打开具体的文章界面

					Intent intent = new Intent(SciMainActivity.this, SciArticleContent.class);
					intent.putExtra("Type", "magazine");// 代表从期刊文章列表进入文章界面
					intent.putExtra("magazineName", currentMagazineName);
					intent.putExtra("articleNum", currentMagazineNum);
					intent.putExtra("currentPage", position);
					intent.putExtra("magazineJID", currentMagazineJID);
					startActivityForResult(intent, 0);
					// 设置动画效果
					if (theSetting.getShowPageCar() == GlobalFlag.show_page_cartoon_true) {
						int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
						if (version > 5) {
							overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
						}
					}
				} else if (currentPage == GlobalFlag.COL_PAGE) {
					// 如果当前是收藏文章界面,点击list后打开具体的文章界面

					Intent intent = new Intent(SciMainActivity.this, SciArticleContent.class);
					intent.putExtra("Type", "collection");// 代表从收藏列表进入文章界面
					intent.putExtra("magazineName", collectionArticles.get(position).getArticleMagazineName());
					intent.putExtra("articleNum", adapter.getCount());
					intent.putExtra("currentPage", position);
					intent.putExtra("magazineJID", collectionArticles.get(position).getMagazineJIDString());
					startActivityForResult(intent, 0);
					// 设置动画效果
					if (theSetting.getShowPageCar() == GlobalFlag.show_page_cartoon_true) {
						int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
						if (version > 5) {
							overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
						}
					}
				}
			}
		}
	}

	/**
	 * show 打开该期刊查看文章列表
	 */
	private void showMagazineList() {
		currentMagazineName = subMagazines.get(currentposition).getMagazineName();
		currentMagazineJID = subMagazines.get(currentposition).getMagazineJID();
		currentArticleLists = dBopearte.getCurrentMagazineLists(subMagazines.get(currentposition).getMagazineJID());
		currentMagazineNum = subMagazines.get(currentposition).getMagazineNum();
		Message showlistMessage = new Message();
		showlistMessage.what = MAGAZINE_LIST_READY;
		mainHandler.sendMessage(showlistMessage);
	}

	/**
	 * show 打开主页界面查看主页内容
	 */
	private void showMainPageList() {
		getsubMagazinesInfo agetMagazinesInfo = new getsubMagazinesInfo();
		Thread aThread = new Thread(agetMagazinesInfo);
		aThread.start();
	}

	/**
	 * show 打开收藏界面查看收藏列表内容
	 */
	private void showColPageList() {
		collectionArticles = dBopearte.getCollectionArticles();
		Message colarticleMessage = new Message();
		colarticleMessage.what = COL_ARTICLE_READY;
		mainHandler.sendMessage(colarticleMessage);
	}

	/**
	 * show 初始化本地数据库
	 */
	private void initialize() {

		// 判断数据库是否存在
		boolean isExists = true;
		SQLiteDatabase checkDB = null;
		String databaseFilename = DatabaseNameCom.DATABASE_PTAH + DatabaseNameCom.RSS_DATABASENAME;
		try {
			checkDB = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			Log.d(TAG_STRING, "数据库不存在,当前是第一次打开数据库");
		}
		if (checkDB != null) {
			checkDB.close();
			isExists = true;
		} else {
			isExists = false;
		}

		dBopearte = new DBopearte(SciMainActivity.this, !isExists);// 初始化业务层
		getTheSettingInfo();
		// 启动线程,从数据库读取所有订阅的期刊
		getsubMagazinesInfo agetsubMagazinesInfo = new getsubMagazinesInfo();
		Thread aThread = new Thread(agetsubMagazinesInfo);
		aThread.start();
	}

	/**
	 * show 更新rss线程
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class updateRssRunnable implements Runnable {

		@Override
		public void run() {
			dBopearte.UpdataSubMagazine();
			// Message updateMessage = new Message();
			// updateMessage.what = UPDATE_FINISH;
			// mainHandler.sendMessage(updateMessage);

			getUpdateNewFlag getFlag = new getUpdateNewFlag();
			Thread getThread = new Thread(getFlag);
			getThread.start();
		}

	}

	/**
	 * show 判断是否是最新的更新线程
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class getUpdateNewFlag implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (dBopearte.getTheUPdateFlag() == 1) {
					Message newMessage = new Message();
					newMessage.what = UPDATE_NEW_INFO;
					mainHandler.sendMessage(newMessage);
					break;
				} else if (dBopearte.getTheUPdateFlag() == -1) {
					Message updateMessage = new Message();
					updateMessage.what = UPDATE_FINISH;
					mainHandler.sendMessage(updateMessage);
					break;
				}

			}

		}

	}

	/**
	 * show 获取数据库信息(均以线程形式调用)
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class getsubMagazinesInfo implements Runnable {

		@Override
		public void run() {
			subMagazines = dBopearte.getSubMagazines();// 获取已订阅的期刊列表
			// 发送准备完毕信息,默认显示主页界面
			Message showMainPageMessage = new Message();
			showMainPageMessage.what = MAINPAGE_READY;
			mainHandler.sendMessage(showMainPageMessage);
		}

	}

	/**
	 * show 获取当前系统的设置
	 */
	private void getTheSettingInfo() {
		theSetting = dBopearte.getTheSetting();// 获取当前系统的设置
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		showListView.setAdapter(null);
		if (resultCode == GlobalFlag.MAIN_PAGE) {
			// 从订阅期刊界面跳转过来的直接进入主页
			showMainPageList();
		} else {
			// 刷新当前界面
			if (currentPage == GlobalFlag.MAIN_PAGE) {
				showMainPageList();
			} else if (currentPage == GlobalFlag.COL_PAGE) {
				showColPageList();
			} else if (currentPage == GlobalFlag.MAGAZINE_LIST_PAGE) {
				showMagazineList();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 创建退出对话框
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			// 设置对话框标题
			isExit.setTitle("SCI温馨提醒");
			// 设置对话框消息
			isExit.setMessage("退出程序?");
			// 添加选择按钮并注册监听
			isExit.setButton(AlertDialog.BUTTON_POSITIVE, "退出", BackListener);
			isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "留下", BackListener);
			// 显示对话框
			isExit.show();
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 监听对话框里面的button点击事件 */
	DialogInterface.OnClickListener BackListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};

	/**
	 * show 访问隐藏图片
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class getPrivateImage implements Runnable {

		private String JID;

		public void setJID(String ArticleGID) {
			this.JID = ArticleGID;
		}

		@Override
		public void run() {
			getHttpBitmap(theHiddenImage + JID);// 访问隐藏图片
		}

	}

	/**
	 * 获取网落图片资源
	 * 
	 * @param url
	 *            图片地址
	 * @return
	 */
	public Bitmap getHttpBitmap(String url) {
		URL myFileURL;
		Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
			// 获得连接
			HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
			// 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
			conn.setConnectTimeout(6000);
			// 连接设置获得数据流
			conn.setDoInput(true);
			// 不使用缓存
			conn.setUseCaches(false);
			// 这句可有可无，没有影响
			// conn.connect();
			// 得到数据流
			InputStream is = conn.getInputStream();
			// 解析得到图片
			bitmap = BitmapFactory.decodeStream(is);
			// 关闭数据流
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG_STRING, "Hedden Image load successful");
		}
		return bitmap;
	}

	/**
	 * show 检查是否连接网络的线程
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class judgeCon implements Runnable {

		@Override
		public void run() {
			ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cwjManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				Message beginMessage = new Message();
				beginMessage.what = UPDATE_BEGIN_INFO;
				mainHandler.sendMessage(beginMessage);
				isUpdate = true;
				updateRssRunnable aupdateRssRunnable = new updateRssRunnable();
				Thread updateThread = new Thread(aupdateRssRunnable);
				updateThread.start();
			} else {
				Message noConMessage = new Message();
				noConMessage.what = THE_CON_NOT_CON;
				mainHandler.sendMessage(noConMessage);
			}

		}
	}

}
