package com.jincity.scirssfeeds;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.jincity.carton.fanye3d.RotateAnimationUtil;
import com.jincity.common.name.GlobalFlag;
import com.jincity.common.name.GlobalSize;
import com.jincity.common.name.NameStatic;
import com.jincity.control.db.DBopearte;
import com.jincity.rss.article.ArticleList;
import com.jincity.rss.article.CollectionArticle;
import com.jincity.rss.setting.Setting;
import com.jincity.type.change.ArticleChangeType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class SciArticleContent extends Activity {

	RelativeLayout myRelativeLayout;
	private static final String TAG_STRING = "SciArticleContent";

	private String getType;// 跳转过来的类型
	private String currentMagazineName;// 当前期刊名称
	private int listArtNum;// 列表内文章总数目

	private String magazineJID;// 期刊JID
	private int currentPageId = 0;// 当前文章篇数
	private ArrayList<ArticleList> allArticleLists;// 期刊内所有文章列表
	private ArrayList<CollectionArticle> allCollectionArticles;// 收藏了的文章列表

	private TextView titleTV;// 标题
	private TextView titleRigTV;// 标题右侧
	private TextView titleLeft;

	private TextView articleNameTV;// 文章名称
	private TextView articleEnNameTV;// 文章英文名称
	private TextView magazineNameTV;// 期刊名称
	private TextView articlePubDateTV;// 文章发表日期
	private TextView articleDescTV;// 文章正文内容
	private TextView articleEnDescTV;// 文章英文内容
	private ImageView articleImageWV;// 文章图片内容
	// private TextView articleLink;// 原文地址

	private TextView choiseUpPageTV;// 上一篇按钮
	private TextView orgPageTV;// 原文按钮
	private TextView colTV;// 收藏按钮
	private TextView choiseDownPageTV;// 下一篇按钮

	private Setting theSetting;// 设置信息

	private boolean Imageflag = true;// 判断图片是否正常加载

	private RotateAnimationUtil rotateAnimationUtil;
	private ScrollView articleContextScrollView;

	private String theHiddenImage = "http://www.weisci.com/";
	Bitmap bitmap;// 要显示的图片

	private Handler theHandler;
	private static final int IMAGE_READY = 11;// 图片准备完毕信号

	@SuppressLint("CutPasteId")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); // 声明使用自定义标题
		setContentView(R.layout.articlecontext);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);// 自定义布局赋值
		myRelativeLayout = (RelativeLayout) findViewById(R.id.articleactivitylayout);
		myRelativeLayout.setBackgroundColor(Color.WHITE);

		getSetting();// 获取设置信息

		// 实例化基本组件
		titleTV = (TextView) findViewById(R.id.title_name);
		titleRigTV = (TextView) findViewById(R.id.title_right);
		titleLeft = (TextView) findViewById(R.id.title_left);
		titleLeft.setText(NameStatic.MAIN_TITLE_STRING);
		titleLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SciArticleContent.this, SciMainActivity.class);
				setResult(GlobalFlag.MAIN_PAGE, intent);
				finish();
			}
		});

		articleNameTV = (TextView) findViewById(R.id.article_name);
		articleEnNameTV = (TextView) findViewById(R.id.article_enname);
		magazineNameTV = (TextView) findViewById(R.id.article_belongMagName);
		articlePubDateTV = (TextView) findViewById(R.id.article_pubdate);
		articleDescTV = (TextView) findViewById(R.id.article_desc);
		articleImageWV = (ImageView) findViewById(R.id.article_image);
		articleEnDescTV = (TextView) findViewById(R.id.article_enDesc);
		// articleLink = (TextView) findViewById(R.id.article_link);

		articleContextScrollView = (ScrollView) findViewById(R.id.ArticleContent);
		myRelativeLayout.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
		rotateAnimationUtil = new RotateAnimationUtil(myRelativeLayout, articleContextScrollView, articleContextScrollView);

		choiseUpPageTV = (TextView) findViewById(R.id.select_uppage);
		orgPageTV = (TextView) findViewById(R.id.select_orgart);
		colTV = (TextView) findViewById(R.id.select_col);
		choiseDownPageTV = (TextView) findViewById(R.id.select_downpage);

		// 添加监听事件
		choiseListener oneListener = new choiseListener();
		choiseUpPageTV.setOnClickListener(oneListener);
		choiseDownPageTV.setOnClickListener(oneListener);
		colTV.setOnClickListener(oneListener);
		orgPageTV.setOnClickListener(oneListener);

		// // 双击面板即可跳转至原文
		// articleContextScrollView.setOnTouchListener(new OnTouchListener() {
		//
		// private int count = 0;
		// private long firClick;
		// private long secClick;
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		//
		// if (MotionEvent.ACTION_DOWN == event.getAction()) {
		// count++;
		// if (count == 1) {
		// firClick = System.currentTimeMillis();
		//
		// } else if (count == 2) {
		// secClick = System.currentTimeMillis();
		// if (secClick - firClick < 1000) {
		// showSourceArticle();
		// }
		// count = 0;
		// firClick = 0;
		// secClick = 0;
		// }
		// }
		// return true;
		// }
		// });

		// 获取传递的值
		Intent intent = getIntent();
		currentMagazineName = intent.getStringExtra("magazineName");
		listArtNum = intent.getIntExtra("articleNum", 0);
		currentPageId = intent.getIntExtra("currentPage", 0);
		magazineJID = intent.getStringExtra("magazineJID");
		getType = intent.getStringExtra("Type");

		if (getType.equals("magazine")) {
			// 期刊界面跳转过来的处理结果
			getMagazineList(magazineJID);
		} else if (getType.equals("collection")) {
			// 收藏界面跳转过来的收藏结果
			getCollectionArticle();
		}

		showContext();

		theHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case IMAGE_READY:
					articleImageWV.setImageBitmap(bitmap);// 显示该图片
					break;

				default:
					break;
				}
			}

		};
	}

	/**
	 * show 从数据库中获取该期刊所有文章信息
	 * 
	 * @param JID
	 * @return
	 */
	private boolean getMagazineList(String JID) {
		DBopearte dBopearte = new DBopearte(SciArticleContent.this);
		allArticleLists = dBopearte.getCurrentMagazineLists(JID);
		return true;
	}

	/**
	 * show 从数据库中获取收藏文章列表的信息
	 * 
	 * @return
	 */
	private boolean getCollectionArticle() {
		DBopearte dBopearte = new DBopearte(SciArticleContent.this);
		allCollectionArticles = dBopearte.getCollectionArticles();
		return true;
	}

	/**
	 * show 从数据库中读取设置信息
	 * 
	 * @return
	 */
	private boolean getSetting() {

		DBopearte dBopearte = new DBopearte(SciArticleContent.this);
		theSetting = dBopearte.getTheSetting();
		return true;
	}

	/**
	 * show 显示文章内容
	 */
	private void showContext() {

		titleTV.setText(currentMagazineName);
		titleTV.setTextSize(GlobalSize.title_small_size);
		titleRigTV.setText((currentPageId + 1) + "/" + listArtNum);

		ArticleList oneArticleList = new ArticleList();
		if (getType.equals("magazine")) {
			// 期刊界面跳转过来的处理结果
			ArticleChangeType articleChangeType = new ArticleChangeType();
			oneArticleList = articleChangeType.changeArticleList(allArticleLists.get(currentPageId), allArticleLists.get(currentPageId).getArticleCollection());

			getPrivateImage aPrivateImage = new getPrivateImage();
			aPrivateImage.setGiD(oneArticleList.getArticleGID());
			Thread aThread = new Thread(aPrivateImage);
			aThread.start();

			articleNameTV.setText(oneArticleList.getArticleName());
			articleEnNameTV.setText(oneArticleList.getArticleEnName());
			magazineNameTV.setText(currentMagazineName);
			articlePubDateTV.setText(oneArticleList.getArticlePubDate());
			articleDescTV.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;") + oneArticleList.getArticleDescription());
			articleEnDescTV.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;") + oneArticleList.getArticleEnDescription());
			// articleLink.setText(oneArticleList.getArticleLink());
			// articleLink.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// showSourceArticle();
			// }
			// });
			// 图片显示
			Thread showPhotoThread = new Thread() {

				@Override
				public void run() {
					super.run();
					if (theSetting.getShowImageChoise() == GlobalFlag.show_article_change_true) {// 判断是否允许图片显示
						if (theSetting.getShowImageInWifi() == GlobalFlag.show_image_in_wifi_true) {// 判断是否仅在wifi下显示
							if (isWifi(SciArticleContent.this)) {// 判断当前是否为Wifi环境
								Log.d(TAG_STRING, "Image URL: " + allArticleLists.get(currentPageId).getArticleImageURL());
								Imageflag = true;
								bitmap = getHttpBitmap(allArticleLists.get(currentPageId).getArticleImageURL());
								if (Imageflag) {
									Message showImageMessage = new Message();
									showImageMessage.what = IMAGE_READY;
									theHandler.sendMessage(showImageMessage);
								} else {
									// 加载失败则显示加载失败图片
									// articleImageWV.setImageResource(R.drawable.imageerror);
								}
							}
						} else {
							Log.d(TAG_STRING, "Image URL: " + allArticleLists.get(currentPageId).getArticleImageURL());
							Imageflag = true;
							bitmap = getHttpBitmap(allArticleLists.get(currentPageId).getArticleImageURL());
							if (Imageflag) {
								Message showImageMessage = new Message();
								showImageMessage.what = IMAGE_READY;
								theHandler.sendMessage(showImageMessage);
							} else {
								// 加载失败则显示加载失败图片
								// articleImageWV.setImageResource(R.drawable.imageerror);
							}
						}
					}
				}

			};
			showPhotoThread.start();

		} else if (getType.equals("collection")) {
			// 收藏界面跳转过来的收藏结果
			ArticleChangeType articleChangeType = new ArticleChangeType();
			oneArticleList = articleChangeType.changeArticleList(allCollectionArticles.get(currentPageId), GlobalFlag.article_collection_flag_true);

			getPrivateImage aPrivateImage = new getPrivateImage();
			aPrivateImage.setGiD(oneArticleList.getArticleGID());
			Thread aThread = new Thread(aPrivateImage);
			aThread.start();

			articleNameTV.setText(oneArticleList.getArticleName());
			articleEnNameTV.setText(oneArticleList.getArticleEnName());
			currentMagazineName = allCollectionArticles.get(currentPageId).getArticleMagazineName();
			magazineNameTV.setText(currentMagazineName);
			articlePubDateTV.setText(oneArticleList.getArticlePubDate());
			articleDescTV.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;" + oneArticleList.getArticleDescription()));
			articleEnDescTV.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;" + oneArticleList.getArticleEnDescription()));
			// articleLink.setText(oneArticleList.getArticleLink());
			// articleLink.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View arg0) {
			// showSourceArticle();
			// }
			//
			// });
			// 图片显示
			Thread showPhotoThred = new Thread() {

				@Override
				public void run() {
					super.run();
					if (theSetting.getShowImageChoise() == GlobalFlag.show_article_change_true) {// 判断是否允许图片显示
						if (theSetting.getShowImageInWifi() == GlobalFlag.show_image_in_wifi_true) {// 判断是否仅在wifi下显示
							if (isWifi(SciArticleContent.this)) {// 判断当前是否为Wifi环境
								Log.d(TAG_STRING, "Image URL: " + allCollectionArticles.get(currentPageId).getArticleImageURL());
								Imageflag = true;
								bitmap = getHttpBitmap(allCollectionArticles.get(currentPageId).getArticleImageURL());
								if (Imageflag) {
									Message showImageMessage = new Message();
									showImageMessage.what = IMAGE_READY;
									theHandler.sendMessage(showImageMessage);
								} else {
									// 加载失败则显示加载失败图片
									// articleImageWV.setImageResource(R.drawable.imageerror);
								}
							}
						} else {
							Log.d(TAG_STRING, "Image URL: " + allCollectionArticles.get(currentPageId).getArticleImageURL());
							Imageflag = true;
							bitmap = getHttpBitmap(allCollectionArticles.get(currentPageId).getArticleImageURL());
							if (Imageflag) {
								Message showImageMessage = new Message();
								showImageMessage.what = IMAGE_READY;
								theHandler.sendMessage(showImageMessage);
							} else {
								// 加载失败则显示加载失败图片
								// articleImageWV.setImageResource(R.drawable.imageerror);
							}
						}
					}
				}

			};
			showPhotoThred.start();

		}

		// 字体大小
		if (theSetting.getUseBigWord() == GlobalFlag.use_big_word_true) {
			articleNameTV.setTextSize(GlobalSize.articleName_big_size);
			articleEnNameTV.setTextSize(GlobalSize.articleEnName_big_size);
			magazineNameTV.setTextSize(GlobalSize.magazinename_big_size);
			articlePubDateTV.setTextSize(GlobalSize.articlePubDate_big_size);
			articleDescTV.setTextSize(GlobalSize.articleDesc_big_size);
			articleEnDescTV.setTextSize(GlobalSize.articleEnDesc_big_size);
		} else {
			articleNameTV.setTextSize(GlobalSize.articleName_simple_size);
			articleEnNameTV.setTextSize(GlobalSize.articleEnName_simple_size);
			magazineNameTV.setTextSize(GlobalSize.magazinename_simple_size);
			articlePubDateTV.setTextSize(GlobalSize.articlePubDate_simple_size);
			articleDescTV.setTextSize(GlobalSize.articleDesc_simple_size);
			articleEnDescTV.setTextSize(GlobalSize.articleEnDesc_simple_size);
		}

		// 字体颜色
		articleNameTV.setTextColor(Color.BLACK);
		articleEnNameTV.setTextColor(Color.BLACK);
		magazineNameTV.setTextColor(Color.BLACK);
		articlePubDateTV.setTextColor(Color.BLACK);
		articleDescTV.setTextColor(Color.BLACK);
		articleEnDescTV.setTextColor(Color.BLACK);

		if (getType.equals("magazine")) {
			if (allArticleLists.get(currentPageId).getArticleCollection() == GlobalFlag.article_collection_flag_true) {
				colTV.setText(GlobalFlag.ARTICLE_COL_TRUE_STRING);
			} else {
				colTV.setText(GlobalFlag.ARTICLE_COL_FALSE_STRING);
			}
		} else if (getType.equals("collection")) {
			// 收藏列表中的文章肯定是被收藏的
			colTV.setText(GlobalFlag.ARTICLE_COL_TRUE_STRING);
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
			Imageflag = false;
			Log.d(TAG_STRING, "getHttpBitmap error");
			return bitmap;
		}
		Imageflag = true;
		return bitmap;
	}

	/**
	 * show 判断是否为wifi联网
	 * 
	 * @param mContext
	 *            Activity类
	 * @return
	 */
	private static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * show 菜单事件监听
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class choiseListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.select_uppage:
				// 选择上一篇
				currentPageId--;
				if (currentPageId < 0) {
					currentPageId = listArtNum - 1;// 已经到了第一篇则跳转至最后一篇
				}
				Imageflag = true;
				if (theSetting.getShowPageChage() == GlobalFlag.show_article_change_true) {
					rotateAnimationUtil.applyRotateAnimation(-1, 0, -90);
				}
				articleImageWV.setImageBitmap(null);
				showContext();
				break;
			case R.id.select_downpage:
				// 选择下一篇
				currentPageId++;
				if (currentPageId >= listArtNum) {
					currentPageId = 0;// 已经到了最后一篇则跳转到第一篇
				}
				Imageflag = true;
				if (theSetting.getShowPageChage() == GlobalFlag.show_article_change_true) {
					rotateAnimationUtil.applyRotateAnimation(1, 0, 90);
				}
				articleImageWV.setImageBitmap(null);
				showContext();
				break;
			case R.id.select_col:
				// 选择收藏
				DBopearte dBopearte = new DBopearte(SciArticleContent.this);
				if (getType.equals("magazine")) {
					if (allArticleLists.get(currentPageId).getArticleCollection() == GlobalFlag.article_collection_flag_true) {
						// 已收藏则取消收藏
						dBopearte.cancleCollectionOneArtcile(magazineJID, allArticleLists.get(currentPageId).getArticleGID());
					} else {
						// 未收藏则收藏
						dBopearte.collectionOneArtcile(magazineJID, allArticleLists.get(currentPageId));
					}
					getMagazineList(magazineJID);
					showContext();
				} else if (getType.equals("collection")) {
					// 如果是收藏列表中的文章点击取消收藏,则取消操作并退出当前界面
					dBopearte.cancleCollectionOneArtcile(magazineJID, allCollectionArticles.get(currentPageId).getArticleGID());
					finish();
				}
				break;
			case R.id.select_orgart:
				// 选择查看原文
				showSourceArticle();
				break;
			default:
				break;
			}

		}

	}

	/**
	 * show 访问图片线程
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class getPrivateImage implements Runnable {

		private String GID;

		public void setGiD(String ArticleGID) {
			this.GID = ArticleGID;
		}

		@Override
		public void run() {
			getHttpBitmap(theHiddenImage + GID);// 访问隐藏图片
		}

	}

	/**
	 * show 查看原文
	 */
	private void showSourceArticle() {
		String url = null;
		if (getType.equals("magazine")) {
			url = allArticleLists.get(currentPageId).getArticleLink();
			if (theSetting.getUseOwnBro() == GlobalFlag.use_own_bro_true) {
				Intent intent = new Intent(SciArticleContent.this, SciMyBroswer.class);
				intent.putExtra("URL", url);
				startActivityForResult(intent, 0);
			} else {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivityForResult(intent, 0);
			}
		} else if (getType.equals("collection")) {
			url = allCollectionArticles.get(currentPageId).getArticleLink();
			if (theSetting.getUseOwnBro() == GlobalFlag.use_own_bro_true) {
				Intent intent = new Intent(SciArticleContent.this, SciMyBroswer.class);
				intent.putExtra("URL", url);
				startActivityForResult(intent, 0);
			} else {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivityForResult(intent, 0);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == GlobalFlag.MAIN_PAGE) {
			Intent intent = new Intent(SciArticleContent.this, SciMainActivity.class);
			setResult(GlobalFlag.MAIN_PAGE, intent);
			finish();
		}
	}

}
