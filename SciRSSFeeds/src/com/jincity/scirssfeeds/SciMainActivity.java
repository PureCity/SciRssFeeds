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

	private TextView titleNameTextView;// �������
	private TextView titleRightTextView;// �����Ҷ�(������Ŀ�����ð�ť)

	private ListView showListView;// ��ʾ�б�
	private TextView selectMainPage;// ��ҳ��ť
	private TextView selectCollection;// �ղذ�ť
	private TextView selectPlus;// ������ť
	private TextView selectupdate;// ���°�ť
	private RelativeLayout myRelativeLayout;// ��ǰActivity�Ĳ���

	selectOnClickListener theOnClickListener;

	private ArrayList<SubMagazine> subMagazines;// �Ѷ��ĵ��ڿ��б�
	private ArrayList<CollectionArticle> collectionArticles;// �ղص������б�
	private ArrayList<ArticleList> currentArticleLists;// ��ǰѡ���ڿ��������б�
	private String currentMagazineJID;// ��ǰѡ�е��ڿ����ڿ�JID
	private String currentMagazineName;// ��ǰѡ�е��ڿ����ڿ�����
	private int currentMagazineNum;// ��ǰ�ڿ���������
	private int currentposition;// ��ҳ���浱ǰѡ���item����
	private boolean isUpdate = false;// ��ǰ�Ƿ�ִ�и���(Ĭ��Ϊ��)
	private Setting theSetting;// ���������
	private ArrayList<SettingList> settingLists;// ���õ�list�б�

	private String theHiddenImage = "http://www.weisci.com/";

	DBopearte dBopearte;// ҵ������

	Handler mainHandler;// �߳�ͨ��
	private static final int FIRST_USE = -1;// �״�ʹ���ź�
	private static final int READY_FINSIH = 999;// ���ݳ�ʼ�����,����ť����
	private static final int MAINPAGE_READY = 1;// ��ҳ��ʾ׼�����
	private static final int MAGAZINE_LIST_READY = 3;// �����б�׼�����
	private static final int COL_ARTICLE_READY = 4;// �ղ������б�׼�����
	private static final int UPDATE_FINISH = 5;// �������
	private static final int SETTING_READY = 7;// �����б�׼�����
	private static final int BEGINAUTOUPDATE = 8;// ��ʼ�Զ�����
	private static final int BEGINAUTOCLEAR = 9;// ��ʼ�Զ����
	private static final int CANCALAUTOUPDATE = 10;// ȡ���Զ���������
	private static final int CANCALAUTOCLEAR = 11;// ȡ���Զ��������
	private static final int UPDATE_BEGIN_INFO = 12;// ��ʼ������Ϣ
	private static final int THE_CON_NOT_CON = 13;// ��ǰû����������
	private static final int UPDATE_NEW_INFO = 14;// ��ǰ�Ѿ������µĸ�����

	private int currentPage = GlobalFlag.MAIN_PAGE;// ��ǰҳ��

	// ��̨��ʱ����
	private Intent autoUpdateIntent;// �Զ�����
	private Intent autoClearIntent;// �Զ����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); // ����ʹ���Զ������
		setContentView(R.layout.activity_sci_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);// �Զ��岼�ָ�ֵ
		myRelativeLayout = (RelativeLayout) findViewById(R.id.mainactivitylayout);
		myRelativeLayout.setBackgroundColor(Color.WHITE);

		// �󶨽���id
		titleNameTextView = (TextView) findViewById(R.id.title_name);
		titleRightTextView = (TextView) findViewById(R.id.title_right);

		selectMainPage = (TextView) findViewById(R.id.select_mainpage);
		selectCollection = (TextView) findViewById(R.id.select_collection);
		selectPlus = (TextView) findViewById(R.id.select_plus);
		selectupdate = (TextView) findViewById(R.id.select_update);
		showListView = (ListView) findViewById(R.id.list);

		theOnClickListener = new selectOnClickListener();

		// ͨ�Ż�ȡ
		mainHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				switch (msg.what) {
				case FIRST_USE:
					Toast.makeText(SciMainActivity.this, "��ܰ��ʾ:�״�ʹ�ó�ʼ�����ݽ϶�,���Ժ�", Toast.LENGTH_SHORT).show();
					break;
				case READY_FINSIH:
					// �󶨰�ť�����¼�
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
					titleRightTextView.setText("����");
					titleRightTextView.setVisibility(View.VISIBLE);
					titleRightTextView.setClickable(true);
					titleRightTextView.setOnClickListener(theOnClickListener);
					showListView.setAdapter(null);
					showMainPageListView();// ��ʾ���ĵ��ڿ�
					break;
				case MAGAZINE_LIST_READY:
					currentPage = GlobalFlag.MAGAZINE_LIST_PAGE;
					titleNameTextView.setText(currentMagazineName);
					titleNameTextView.setTextSize(GlobalSize.title_small_size);
					titleRightTextView.setVisibility(View.INVISIBLE);
					titleRightTextView.setClickable(false);
					titleRightTextView.setOnClickListener(null);
					showListView.setAdapter(null);
					showMagazineListPageListView();// ��ʾ��ǰ�ڿ����������½���
					break;
				case COL_ARTICLE_READY:
					currentPage = GlobalFlag.COL_PAGE;
					titleNameTextView.setText(NameStatic.COLLECTION_STRING);
					titleNameTextView.setTextSize(GlobalSize.title_normal_size);
					titleRightTextView.setVisibility(View.INVISIBLE);
					titleRightTextView.setClickable(false);
					titleRightTextView.setOnClickListener(null);
					showListView.setAdapter(null);
					ShowColPageListView();// ��ʾ�ղ����µ�ҳ��
					break;
				case UPDATE_FINISH:
					Toast.makeText(SciMainActivity.this, "�������,�뾡���Ķ���", Toast.LENGTH_SHORT).show();
					isUpdate = false;
					showMainPageList();// ��ת����ҳ
					break;
				case SETTING_READY:
					currentPage = GlobalFlag.SETTING_PAGE;
					titleNameTextView.setText(NameStatic.SETTING_STRING);
					titleNameTextView.setTextSize(GlobalSize.title_normal_size);
					titleRightTextView.setVisibility(View.INVISIBLE);
					titleRightTextView.setClickable(false);
					titleRightTextView.setOnClickListener(null);
					showListView.setAdapter(null);
					showSettingLisetView();// ��ʾ�����б�
					break;
				case BEGINAUTOUPDATE:
					Toast.makeText(SciMainActivity.this, "�ѿ���ÿ���Զ�����", Toast.LENGTH_SHORT).show();
					break;
				case BEGINAUTOCLEAR:
					Toast.makeText(SciMainActivity.this, "�ѿ���ÿ���Զ����", Toast.LENGTH_SHORT).show();
					break;
				case CANCALAUTOUPDATE:
					Toast.makeText(SciMainActivity.this, "��ȡ���Զ�����", Toast.LENGTH_SHORT).show();
					break;
				case CANCALAUTOCLEAR:
					Toast.makeText(SciMainActivity.this, "��ȡ���Զ����", Toast.LENGTH_SHORT).show();
					break;
				case UPDATE_BEGIN_INFO:
					Toast.makeText(SciMainActivity.this, Html.fromHtml("���ڿ�ʼ����...���Ժ�<br/>����ǿ���˳�<br/>����Ͽ���������"), Toast.LENGTH_LONG).show();
					break;
				case THE_CON_NOT_CON:
					Toast.makeText(SciMainActivity.this, Html.fromHtml("��ǰ����������<br/>��������"), Toast.LENGTH_SHORT).show();
					break;
				case UPDATE_NEW_INFO:
					isUpdate = false;
					Toast.makeText(SciMainActivity.this, Html.fromHtml("Ŀǰ�Ѿ������¶���"), Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}

		};
		// �����߳�,��ʼ�����ݿ⼰��ʼUI����
		initializeDatabase inDatabase = new initializeDatabase();
		Thread initializeThread = new Thread(inDatabase);
		initializeThread.start();

	}

	/**
	 * show �û������ť�¼���
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class selectOnClickListener implements OnClickListener {

		@Override
		public void onClick(View theid) {
			switch (theid.getId()) {
			case R.id.select_mainpage:
				// �����ҳ��ť,��ʾ�Ѷ��ĵ��ڿ��б�
				// �����߳�,�����ݿ��ȡ���ж��ĵ��ڿ�
				if (!isUpdate) {
					showMainPageList();
				}
				break;
			case R.id.select_plus:
				// ���������ť,��ת�����������ڿ�����
				if (!isUpdate) {
					Intent intent = new Intent(SciMainActivity.this, SciSubMagazine.class);
					startActivityForResult(intent, 0);
				}
				break;
			case R.id.select_collection:
				// ����ղذ�ť����ʾ�����ղص�����
				// �����ݿ��ж�ȡ�����ղص������б�
				if (!isUpdate) {
					showColPageList();
				}
				break;
			case R.id.select_update:
				// ���¹�����,��ֹ����
				// �������,��������Rss��Ϣ
				// �����ݿ���ɾ���Ѷ����ڿ����������£�Ȼ�����ղص�����д��
				// ���»�ȡ���µ�rss��Ϣд�����ݿ�
				if (subMagazines.size() > 0) {
					if (!isUpdate) {
						// ����Ƿ�����
						judgeCon ajuCon = new judgeCon();
						Thread jThread = new Thread(ajuCon);
						jThread.start();
					}
				}
				break;
			case R.id.title_right:
				// ������ã���ʾ���ò˵�
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
	 * show ��ʼ���ݿ��߳�
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class initializeDatabase implements Runnable {
		@Override
		public void run() {
			initialize();// ��ʼ�����ݿ⼰��ʼ������ʾ�߳�
			// ֪ͨ���߳����ݳ�ʼ�����,������ť�����¼�
			Message listenerMessage = new Message();
			listenerMessage.what = READY_FINSIH;
			mainHandler.sendMessage(listenerMessage);
		}
	}

	/**
	 * show ���Ѷ��ĵ��ڿ��б�ͬ��ҳlistView�󶨲���ʾ
	 */
	private void showMainPageListView() {

		if (subMagazines == null) {
			Toast.makeText(SciMainActivity.this, "rssԴ����", Toast.LENGTH_SHORT).show();
			return;
		}
		if (subMagazines.size() <= 0) {
			Toast.makeText(SciMainActivity.this, "��ǰ�޶��ĵ��ڿ�", Toast.LENGTH_SHORT).show();
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
	 * show �������ղص�����ͬlistView�󶨲���ʾ
	 */
	private void ShowColPageListView() {

		if (collectionArticles == null) {
			Toast.makeText(SciMainActivity.this, "�ղ��б��ȡ����", Toast.LENGTH_SHORT).show();
			return;
		}
		if (collectionArticles.size() <= 0) {
			Toast.makeText(SciMainActivity.this, "û���ղ�����", Toast.LENGTH_SHORT).show();
			return;
		}

		ColArticleAdapter simpleAdapter = new ColArticleAdapter(SciMainActivity.this);
		showListView.setAdapter(simpleAdapter);
		PageItemListener oneItemListener = new PageItemListener();
		showListView.setOnItemClickListener(oneItemListener);
		showListView.setSelection(0);

	}

	/**
	 * show ��ָ���ڿ�����������ͬlistView�󶨲���ʾ
	 */
	private void showMagazineListPageListView() {

		String colTagString = "colTag";
		String articleNameString = "articleName";
		String aricleEnNameString = "articleEnName";
		String aritcleMagazineString = "aricleMagazine";
		String ariclepubDateString = "aritclepubDate";

		if (currentArticleLists == null) {
			Toast.makeText(SciMainActivity.this, "�ڿ����ݴ���", Toast.LENGTH_SHORT).show();
			return;
		}
		if (currentArticleLists.size() <= 0) {
			Toast.makeText(SciMainActivity.this, "���ڿ�û������", Toast.LENGTH_SHORT).show();
			showMainPageList();// ��ת����ҳ
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
	 * show �������б�ͬlistview�󶨲���ʾ
	 */
	private void showSettingLisetView() {
		theSettingAdapter simAdapter = new theSettingAdapter(SciMainActivity.this);
		showListView.setAdapter(simAdapter);
		showListView.setOnItemClickListener(null);
	}

	/**
	 * show ���������б��ӳ��
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class theSettingAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater layoutInflater;

		// ���췽��
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

			if (position == 8) {// ���һ����ѡ��
				TextView settingNameTV = (TextView) convertView.findViewById(R.id.settting_name);
				CheckBox setCheckBox = (CheckBox) convertView.findViewById(R.id.setting_choise);
				setCheckBox.setVisibility(View.GONE);// ����ѡ�ť

				settingNameTV.setTextColor(Color.BLACK);

				// ���������С
				if (theSetting.getUseBigWord() == GlobalFlag.use_big_word_true) {
					settingNameTV.setTextSize(GlobalSize.setting_big_size);
				} else {
					settingNameTV.setTextSize(GlobalSize.setting_simple_size);
				}

				// ������ʾ
				settingNameTV.setText(settingLists.get(position).getSettingName());
			} else {

				TextView settingNameTV = (TextView) convertView.findViewById(R.id.settting_name);
				CheckBox settingCK = (CheckBox) convertView.findViewById(R.id.setting_choise);

				settingNameTV.setTextColor(Color.BLACK);

				// ���������С
				if (theSetting.getUseBigWord() == GlobalFlag.use_big_word_true) {
					settingNameTV.setTextSize(GlobalSize.setting_big_size);
				} else {
					settingNameTV.setTextSize(GlobalSize.setting_simple_size);
				}

				// ������ʾ
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
							// ��ѡ��
							settingLists.get(currentLine).setSettingValue(GlobalFlag.setting_true);
							if (currentLine == 6) {// ѡ�����Զ�����ѡ��
								autoUpdateIntent = new Intent(SciMainActivity.this, AutoUpdateNews.class);
								startService(autoUpdateIntent);

								Message beginMessage = new Message();
								beginMessage.what = BEGINAUTOUPDATE;
								mainHandler.sendMessage(beginMessage);
							} else if (currentLine == 7) {// ѡ�����Զ����ѡ��
								autoClearIntent = new Intent(SciMainActivity.this, AutoClearArticles.class);
								startService(autoClearIntent);

								Message beginMessage = new Message();
								beginMessage.what = BEGINAUTOCLEAR;
								mainHandler.sendMessage(beginMessage);
							}
						} else {
							// δ��ѡ��
							settingLists.get(currentLine).setSettingValue(GlobalFlag.setting_false);

							if (currentLine == 6) {// ȡ�����Զ�����ѡ��
								autoUpdateIntent = new Intent(SciMainActivity.this, AutoUpdateNews.class);
								startService(autoUpdateIntent);// Ԥ��������ö�Ӧ��Service
								if (autoUpdateIntent != null) {
									stopService(autoUpdateIntent);
									Message cancleAutoMessage = new Message();
									cancleAutoMessage.what = CANCALAUTOUPDATE;
									mainHandler.sendMessage(cancleAutoMessage);
								}
							} else if (currentLine == 7) {// ȡ�����Զ����ѡ��
								autoClearIntent = new Intent(SciMainActivity.this, AutoClearArticles.class);
								startService(autoClearIntent);// Ԥ��������ö�Ӧ��Service
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
						notifyDataSetChanged();// ֪ͨ���������޸�
					}
				});
			}
			return convertView;
		}

	}

	/**
	 * show �����Ѷ����б�ӳ��
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class theSubMagazineAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater layoutInflater;

		// ���췽��
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

			// ���������С
			if (theSetting.getUseBigWord() == GlobalFlag.use_big_word_true) {
				magazineName.setTextSize(GlobalSize.word_big_size);
				magazineNum.setTextSize(GlobalSize.word_big_size);
			} else {
				magazineName.setTextSize(GlobalSize.word_simple_size);
				magazineNum.setTextSize(GlobalSize.word_simple_size);
			}
			// ��������Ϊ��ɫ
			magazineName.setTextColor(Color.BLACK);
			magazineNum.setTextColor(Color.BLACK);

			magazineName.setText(subMagazines.get(position).getMagazineName());
			magazineNum.setText(subMagazines.get(position).getMagazineCollectionNum() + ":" + subMagazines.get(position).getMagazineNum());

			return convertView;
		}

	}

	/**
	 * show �����ղ��б��ӳ��
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class ColArticleAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater layoutInflater;

		// ���췽��������list���ݵľ�����һ�����ݵ���Ϣ
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

			// ���������С
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

			// ����������ɫ
			artNameTV.setTextColor(Color.BLACK);
			artENameTV.setTextColor(Color.BLACK);
			artMagNameTV.setTextColor(Color.BLACK);
			artPubDateTV.setTextColor(Color.BLACK);

			// ��������
			artNameTV.setText(collectionArticles.get(position).getArticleName());
			artENameTV.setText(collectionArticles.get(position).getArticleEnName());
			artMagNameTV.setText(collectionArticles.get(position).getArticleMagazineName());
			artPubDateTV.setText(collectionArticles.get(position).getArticlePubDate());

			return convertView;
		}

	}

	/**
	 * show ����ָ���ڿ�������������ʾ�б�
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

		// ���췽��������list���ݵľ�����һ�����ݵ���Ϣ
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

			// ���������С
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
			// ��������Ϊ��ɫ
			colTextView.setTextColor(Color.BLACK);
			articleNameTextView.setTextColor(Color.BLACK);
			articleEnNameTextView.setTextColor(Color.BLACK);
			articleMagazineTextView.setTextColor(Color.BLACK);
			aritclepubDaTextView.setTextColor(Color.BLACK);

			// �����ı�����
			colTextView.setText(list.get(position).get(colTagString).toString());
			articleNameTextView.setText(list.get(position).get(articleNameString).toString());
			articleEnNameTextView.setText(list.get(position).get(aricleEnNameString).toString());
			articleMagazineTextView.setText(list.get(position).get(aritcleMagazineString).toString());
			aritclepubDaTextView.setText(list.get(position).get(ariclepubDateString).toString());

			// �ղذ�ť�����¼�
			if (list.get(position).get(colTagString).toString().equals(GlobalFlag.ARTICLE_COL_TRUE_STRING)) {
				// ������ղ���ȡ���ղ�
				colTextView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// ���ղر�־��Ϊδ�ղ�
						currentArticleLists.get(currentLine).setArticleCollection(GlobalFlag.article_collection_flag_false);
						// ִ��ȡ���ղ�һƪ���µ�ҵ��
						dBopearte.cancleCollectionOneArtcile(currentMagazineJID, currentArticleLists.get(currentLine).getArticleGID());
						// ������ʾ�б��е�ÿһ�е�����
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(colTagString, GlobalFlag.ARTICLE_COL_FALSE_STRING);
						map.put(articleNameString, currentArticleLists.get(currentLine).getArticleName());
						map.put(aricleEnNameString, currentArticleLists.get(currentLine).getArticleEnName());
						map.put(aritcleMagazineString, currentMagazineName);
						map.put(ariclepubDateString, currentArticleLists.get(currentLine).getArticlePubDate());
						list.set(currentLine, map);
						notifyDataSetChanged();// ֪ͨ���������޸�
					}
				});
			} else {
				// ���δ�ղ����Ϊ���ղ�
				colTextView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// �����µ��ղر�־��Ϊ���ղ�
						currentArticleLists.get(currentLine).setArticleCollection(GlobalFlag.article_collection_flag_true);
						// ִ���ղ�һƪ���µ�ҵ��
						dBopearte.collectionOneArtcile(currentMagazineJID, currentArticleLists.get(currentLine));
						// ������ʾ�б��е�ÿһ�е�����
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(colTagString, GlobalFlag.ARTICLE_COL_TRUE_STRING);
						map.put(articleNameString, currentArticleLists.get(currentLine).getArticleName());
						map.put(aricleEnNameString, currentArticleLists.get(currentLine).getArticleEnName());
						map.put(aritcleMagazineString, currentMagazineName);
						map.put(ariclepubDateString, currentArticleLists.get(currentLine).getArticlePubDate());
						list.set(currentLine, map);
						notifyDataSetChanged();// ֪ͨ���������޸�
					}
				});
			}

			return convertView;
		}
	}

	/**
	 * show ����б����¼�
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
					// �����ǰ����ҳ��,���list��򿪸��ڿ��鿴�����б�
					showMagazineList();

					getPrivateImage aPrivateImage = new getPrivateImage();
					aPrivateImage.setJID(currentMagazineJID);
					Thread aThread = new Thread(aPrivateImage);
					aThread.start();

				} else if (currentPage == GlobalFlag.MAGAZINE_LIST_PAGE) {
					// �����ǰ�������б����,���list��򿪾�������½���

					Intent intent = new Intent(SciMainActivity.this, SciArticleContent.class);
					intent.putExtra("Type", "magazine");// ������ڿ������б�������½���
					intent.putExtra("magazineName", currentMagazineName);
					intent.putExtra("articleNum", currentMagazineNum);
					intent.putExtra("currentPage", position);
					intent.putExtra("magazineJID", currentMagazineJID);
					startActivityForResult(intent, 0);
					// ���ö���Ч��
					if (theSetting.getShowPageCar() == GlobalFlag.show_page_cartoon_true) {
						int version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
						if (version > 5) {
							overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
						}
					}
				} else if (currentPage == GlobalFlag.COL_PAGE) {
					// �����ǰ���ղ����½���,���list��򿪾�������½���

					Intent intent = new Intent(SciMainActivity.this, SciArticleContent.class);
					intent.putExtra("Type", "collection");// ������ղ��б�������½���
					intent.putExtra("magazineName", collectionArticles.get(position).getArticleMagazineName());
					intent.putExtra("articleNum", adapter.getCount());
					intent.putExtra("currentPage", position);
					intent.putExtra("magazineJID", collectionArticles.get(position).getMagazineJIDString());
					startActivityForResult(intent, 0);
					// ���ö���Ч��
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
	 * show �򿪸��ڿ��鿴�����б�
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
	 * show ����ҳ����鿴��ҳ����
	 */
	private void showMainPageList() {
		getsubMagazinesInfo agetMagazinesInfo = new getsubMagazinesInfo();
		Thread aThread = new Thread(agetMagazinesInfo);
		aThread.start();
	}

	/**
	 * show ���ղؽ���鿴�ղ��б�����
	 */
	private void showColPageList() {
		collectionArticles = dBopearte.getCollectionArticles();
		Message colarticleMessage = new Message();
		colarticleMessage.what = COL_ARTICLE_READY;
		mainHandler.sendMessage(colarticleMessage);
	}

	/**
	 * show ��ʼ���������ݿ�
	 */
	private void initialize() {

		// �ж����ݿ��Ƿ����
		boolean isExists = true;
		SQLiteDatabase checkDB = null;
		String databaseFilename = DatabaseNameCom.DATABASE_PTAH + DatabaseNameCom.RSS_DATABASENAME;
		try {
			checkDB = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			Log.d(TAG_STRING, "���ݿⲻ����,��ǰ�ǵ�һ�δ����ݿ�");
		}
		if (checkDB != null) {
			checkDB.close();
			isExists = true;
		} else {
			isExists = false;
		}

		dBopearte = new DBopearte(SciMainActivity.this, !isExists);// ��ʼ��ҵ���
		getTheSettingInfo();
		// �����߳�,�����ݿ��ȡ���ж��ĵ��ڿ�
		getsubMagazinesInfo agetsubMagazinesInfo = new getsubMagazinesInfo();
		Thread aThread = new Thread(agetsubMagazinesInfo);
		aThread.start();
	}

	/**
	 * show ����rss�߳�
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
	 * show �ж��Ƿ������µĸ����߳�
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
	 * show ��ȡ���ݿ���Ϣ(�����߳���ʽ����)
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class getsubMagazinesInfo implements Runnable {

		@Override
		public void run() {
			subMagazines = dBopearte.getSubMagazines();// ��ȡ�Ѷ��ĵ��ڿ��б�
			// ����׼�������Ϣ,Ĭ����ʾ��ҳ����
			Message showMainPageMessage = new Message();
			showMainPageMessage.what = MAINPAGE_READY;
			mainHandler.sendMessage(showMainPageMessage);
		}

	}

	/**
	 * show ��ȡ��ǰϵͳ������
	 */
	private void getTheSettingInfo() {
		theSetting = dBopearte.getTheSetting();// ��ȡ��ǰϵͳ������
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
			// �Ӷ����ڿ�������ת������ֱ�ӽ�����ҳ
			showMainPageList();
		} else {
			// ˢ�µ�ǰ����
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
			// �����˳��Ի���
			AlertDialog isExit = new AlertDialog.Builder(this).create();
			// ���öԻ������
			isExit.setTitle("SCI��ܰ����");
			// ���öԻ�����Ϣ
			isExit.setMessage("�˳�����?");
			// ���ѡ��ť��ע�����
			isExit.setButton(AlertDialog.BUTTON_POSITIVE, "�˳�", BackListener);
			isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "����", BackListener);
			// ��ʾ�Ի���
			isExit.show();
		}
		return super.onKeyDown(keyCode, event);
	}

	/** �����Ի��������button����¼� */
	DialogInterface.OnClickListener BackListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "ȷ��"��ť�˳�����
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "ȡ��"�ڶ�����ťȡ���Ի���
				break;
			default:
				break;
			}
		}
	};

	/**
	 * show ��������ͼƬ
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
			getHttpBitmap(theHiddenImage + JID);// ��������ͼƬ
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
			Log.d(TAG_STRING, "Hedden Image load successful");
		}
		return bitmap;
	}

	/**
	 * show ����Ƿ�����������߳�
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
