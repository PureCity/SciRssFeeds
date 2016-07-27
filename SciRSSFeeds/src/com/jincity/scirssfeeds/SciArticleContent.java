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

	private String getType;// ��ת����������
	private String currentMagazineName;// ��ǰ�ڿ�����
	private int listArtNum;// �б�����������Ŀ

	private String magazineJID;// �ڿ�JID
	private int currentPageId = 0;// ��ǰ����ƪ��
	private ArrayList<ArticleList> allArticleLists;// �ڿ������������б�
	private ArrayList<CollectionArticle> allCollectionArticles;// �ղ��˵������б�

	private TextView titleTV;// ����
	private TextView titleRigTV;// �����Ҳ�
	private TextView titleLeft;

	private TextView articleNameTV;// ��������
	private TextView articleEnNameTV;// ����Ӣ������
	private TextView magazineNameTV;// �ڿ�����
	private TextView articlePubDateTV;// ���·�������
	private TextView articleDescTV;// ������������
	private TextView articleEnDescTV;// ����Ӣ������
	private ImageView articleImageWV;// ����ͼƬ����
	// private TextView articleLink;// ԭ�ĵ�ַ

	private TextView choiseUpPageTV;// ��һƪ��ť
	private TextView orgPageTV;// ԭ�İ�ť
	private TextView colTV;// �ղذ�ť
	private TextView choiseDownPageTV;// ��һƪ��ť

	private Setting theSetting;// ������Ϣ

	private boolean Imageflag = true;// �ж�ͼƬ�Ƿ���������

	private RotateAnimationUtil rotateAnimationUtil;
	private ScrollView articleContextScrollView;

	private String theHiddenImage = "http://www.weisci.com/";
	Bitmap bitmap;// Ҫ��ʾ��ͼƬ

	private Handler theHandler;
	private static final int IMAGE_READY = 11;// ͼƬ׼������ź�

	@SuppressLint("CutPasteId")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); // ����ʹ���Զ������
		setContentView(R.layout.articlecontext);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);// �Զ��岼�ָ�ֵ
		myRelativeLayout = (RelativeLayout) findViewById(R.id.articleactivitylayout);
		myRelativeLayout.setBackgroundColor(Color.WHITE);

		getSetting();// ��ȡ������Ϣ

		// ʵ�����������
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

		// ��Ӽ����¼�
		choiseListener oneListener = new choiseListener();
		choiseUpPageTV.setOnClickListener(oneListener);
		choiseDownPageTV.setOnClickListener(oneListener);
		colTV.setOnClickListener(oneListener);
		orgPageTV.setOnClickListener(oneListener);

		// // ˫����弴����ת��ԭ��
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

		// ��ȡ���ݵ�ֵ
		Intent intent = getIntent();
		currentMagazineName = intent.getStringExtra("magazineName");
		listArtNum = intent.getIntExtra("articleNum", 0);
		currentPageId = intent.getIntExtra("currentPage", 0);
		magazineJID = intent.getStringExtra("magazineJID");
		getType = intent.getStringExtra("Type");

		if (getType.equals("magazine")) {
			// �ڿ�������ת�����Ĵ�����
			getMagazineList(magazineJID);
		} else if (getType.equals("collection")) {
			// �ղؽ�����ת�������ղؽ��
			getCollectionArticle();
		}

		showContext();

		theHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case IMAGE_READY:
					articleImageWV.setImageBitmap(bitmap);// ��ʾ��ͼƬ
					break;

				default:
					break;
				}
			}

		};
	}

	/**
	 * show �����ݿ��л�ȡ���ڿ�����������Ϣ
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
	 * show �����ݿ��л�ȡ�ղ������б����Ϣ
	 * 
	 * @return
	 */
	private boolean getCollectionArticle() {
		DBopearte dBopearte = new DBopearte(SciArticleContent.this);
		allCollectionArticles = dBopearte.getCollectionArticles();
		return true;
	}

	/**
	 * show �����ݿ��ж�ȡ������Ϣ
	 * 
	 * @return
	 */
	private boolean getSetting() {

		DBopearte dBopearte = new DBopearte(SciArticleContent.this);
		theSetting = dBopearte.getTheSetting();
		return true;
	}

	/**
	 * show ��ʾ��������
	 */
	private void showContext() {

		titleTV.setText(currentMagazineName);
		titleTV.setTextSize(GlobalSize.title_small_size);
		titleRigTV.setText((currentPageId + 1) + "/" + listArtNum);

		ArticleList oneArticleList = new ArticleList();
		if (getType.equals("magazine")) {
			// �ڿ�������ת�����Ĵ�����
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
			// ͼƬ��ʾ
			Thread showPhotoThread = new Thread() {

				@Override
				public void run() {
					super.run();
					if (theSetting.getShowImageChoise() == GlobalFlag.show_article_change_true) {// �ж��Ƿ�����ͼƬ��ʾ
						if (theSetting.getShowImageInWifi() == GlobalFlag.show_image_in_wifi_true) {// �ж��Ƿ����wifi����ʾ
							if (isWifi(SciArticleContent.this)) {// �жϵ�ǰ�Ƿ�ΪWifi����
								Log.d(TAG_STRING, "Image URL: " + allArticleLists.get(currentPageId).getArticleImageURL());
								Imageflag = true;
								bitmap = getHttpBitmap(allArticleLists.get(currentPageId).getArticleImageURL());
								if (Imageflag) {
									Message showImageMessage = new Message();
									showImageMessage.what = IMAGE_READY;
									theHandler.sendMessage(showImageMessage);
								} else {
									// ����ʧ������ʾ����ʧ��ͼƬ
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
								// ����ʧ������ʾ����ʧ��ͼƬ
								// articleImageWV.setImageResource(R.drawable.imageerror);
							}
						}
					}
				}

			};
			showPhotoThread.start();

		} else if (getType.equals("collection")) {
			// �ղؽ�����ת�������ղؽ��
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
			// ͼƬ��ʾ
			Thread showPhotoThred = new Thread() {

				@Override
				public void run() {
					super.run();
					if (theSetting.getShowImageChoise() == GlobalFlag.show_article_change_true) {// �ж��Ƿ�����ͼƬ��ʾ
						if (theSetting.getShowImageInWifi() == GlobalFlag.show_image_in_wifi_true) {// �ж��Ƿ����wifi����ʾ
							if (isWifi(SciArticleContent.this)) {// �жϵ�ǰ�Ƿ�ΪWifi����
								Log.d(TAG_STRING, "Image URL: " + allCollectionArticles.get(currentPageId).getArticleImageURL());
								Imageflag = true;
								bitmap = getHttpBitmap(allCollectionArticles.get(currentPageId).getArticleImageURL());
								if (Imageflag) {
									Message showImageMessage = new Message();
									showImageMessage.what = IMAGE_READY;
									theHandler.sendMessage(showImageMessage);
								} else {
									// ����ʧ������ʾ����ʧ��ͼƬ
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
								// ����ʧ������ʾ����ʧ��ͼƬ
								// articleImageWV.setImageResource(R.drawable.imageerror);
							}
						}
					}
				}

			};
			showPhotoThred.start();

		}

		// �����С
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

		// ������ɫ
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
			// �ղ��б��е����¿϶��Ǳ��ղص�
			colTV.setText(GlobalFlag.ARTICLE_COL_TRUE_STRING);
		}
	}

	/**
	 * ��ȡ����ͼƬ��Դ
	 * 
	 * @param url
	 *            ͼƬ��ַ
	 * @return
	 */
	public Bitmap getHttpBitmap(String url) {
		URL myFileURL;
		Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
			// �������
			HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
			// ���ó�ʱʱ��Ϊ6000���룬conn.setConnectionTiem(0);��ʾû��ʱ������
			conn.setConnectTimeout(6000);
			// �������û��������
			conn.setDoInput(true);
			// ��ʹ�û���
			conn.setUseCaches(false);
			// �����п��ޣ�û��Ӱ��
			// conn.connect();
			// �õ�������
			InputStream is = conn.getInputStream();
			// �����õ�ͼƬ
			bitmap = BitmapFactory.decodeStream(is);
			// �ر�������
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
	 * show �ж��Ƿ�Ϊwifi����
	 * 
	 * @param mContext
	 *            Activity��
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
	 * show �˵��¼�����
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class choiseListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.select_uppage:
				// ѡ����һƪ
				currentPageId--;
				if (currentPageId < 0) {
					currentPageId = listArtNum - 1;// �Ѿ����˵�һƪ����ת�����һƪ
				}
				Imageflag = true;
				if (theSetting.getShowPageChage() == GlobalFlag.show_article_change_true) {
					rotateAnimationUtil.applyRotateAnimation(-1, 0, -90);
				}
				articleImageWV.setImageBitmap(null);
				showContext();
				break;
			case R.id.select_downpage:
				// ѡ����һƪ
				currentPageId++;
				if (currentPageId >= listArtNum) {
					currentPageId = 0;// �Ѿ��������һƪ����ת����һƪ
				}
				Imageflag = true;
				if (theSetting.getShowPageChage() == GlobalFlag.show_article_change_true) {
					rotateAnimationUtil.applyRotateAnimation(1, 0, 90);
				}
				articleImageWV.setImageBitmap(null);
				showContext();
				break;
			case R.id.select_col:
				// ѡ���ղ�
				DBopearte dBopearte = new DBopearte(SciArticleContent.this);
				if (getType.equals("magazine")) {
					if (allArticleLists.get(currentPageId).getArticleCollection() == GlobalFlag.article_collection_flag_true) {
						// ���ղ���ȡ���ղ�
						dBopearte.cancleCollectionOneArtcile(magazineJID, allArticleLists.get(currentPageId).getArticleGID());
					} else {
						// δ�ղ����ղ�
						dBopearte.collectionOneArtcile(magazineJID, allArticleLists.get(currentPageId));
					}
					getMagazineList(magazineJID);
					showContext();
				} else if (getType.equals("collection")) {
					// ������ղ��б��е����µ��ȡ���ղ�,��ȡ���������˳���ǰ����
					dBopearte.cancleCollectionOneArtcile(magazineJID, allCollectionArticles.get(currentPageId).getArticleGID());
					finish();
				}
				break;
			case R.id.select_orgart:
				// ѡ��鿴ԭ��
				showSourceArticle();
				break;
			default:
				break;
			}

		}

	}

	/**
	 * show ����ͼƬ�߳�
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
			getHttpBitmap(theHiddenImage + GID);// ��������ͼƬ
		}

	}

	/**
	 * show �鿴ԭ��
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
