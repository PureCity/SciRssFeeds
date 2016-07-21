package com.jincity.scirssfeeds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jincity.common.name.GlobalFlag;
import com.jincity.common.name.GlobalSize;
import com.jincity.common.name.NameStatic;
import com.jincity.control.analyse.GetMagazineSource;
import com.jincity.control.db.DBopearte;
import com.jincity.rss.magazine.SourceMagazine;
import com.jincity.rss.setting.Setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class SciSubMagazine extends Activity {

	private static final String TAG_STRING = "SciSubMagazine";

	private RelativeLayout myRelativeLayout;// ��ǰActivity�Ĳ���
	private TextView mainPageTextView;// ��ҳ��ť
	private TextView existsTextView;// �����ڿ���ť
	private TextView webSubTextView;// �����ڿ���ť

	private TextView titleTextView;
	private TextView titleRight;

	private ListView sourceListView;// �ڿ��б�

	private int currentPage = GlobalFlag.SOURCE_PAGE;// ��ǰ����(Ĭ��Ϊ�����ڿ�����)
	private boolean isSubing = false;// ��ǰ�Ƿ����ڶ���

	private ArrayList<SourceMagazine> sourceMagazines;// ��ǰ�������ʾ�б�
	SubMagazineAdapter simpleAdapter;

	private Setting theSetting;// ��������
	private DBopearte dBopearte;// ҵ������

	Handler subHandler;// �߳�ͨ��
	private static final int WEB_PAGE_READY = 1;// ��ҳ�б�׼�����
	private static final int SUB_GOING = 2;// ��ʼ�����ź�
	private static final int SUB_FINISH = 3;// ��������ź�

	private boolean subJudge = false;// ������ȴ��ж�
	private boolean conOK = true;// �����Ƿ����ӱ�־

	private LinearLayout theChoiseMenuLayout;// ��̬��ӿؼ������
	private static final int UPDATE_BEGIN_INFO = 12;// ��ʼ������Ϣ
	private static final int THE_CON_NOT_CON = 13;// ��ǰû����������

	// ����Ŀѡ��ť
	private TextView yixue1;
	private TextView yixue2;
	private TextView shengwu1;
	private TextView shengwu2;
	private TextView wuli1;
	private TextView wuli2;
	private TextView huaxue1;
	private TextView huaxue2;
	private TextView qita1;
	private TextView qita2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); // ����ʹ���Զ������
		setContentView(R.layout.activity_sci_submagazine);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);// �Զ��岼�ָ�ֵ
		titleTextView = (TextView) findViewById(R.id.title_name);
		titleTextView.setText(NameStatic.SUBMAGAZINE_STRING);
		titleRight = (TextView) findViewById(R.id.title_right);
		titleRight.setVisibility(View.INVISIBLE);

		myRelativeLayout = (RelativeLayout) findViewById(R.id.sub_mag_activitylayout);
		myRelativeLayout.setBackgroundColor(Color.WHITE);

		mainPageTextView = (TextView) findViewById(R.id.sub_mag_select_mainpage);
		existsTextView = (TextView) findViewById(R.id.sub_mag_select_exist);
		webSubTextView = (TextView) findViewById(R.id.sub_mag_select_plus);
		sourceListView = (ListView) findViewById(R.id.sourcelist);

		theChoiseMenuLayout = (LinearLayout) findViewById(R.id.submag_choisemenu);
		yixue1 = (TextView) findViewById(R.id.sub_mag_yixue1);
		yixue2 = (TextView) findViewById(R.id.sub_mag_yixue2);
		shengwu1 = (TextView) findViewById(R.id.sub_mag_shengwu1);
		shengwu2 = (TextView) findViewById(R.id.sub_mag_shengwu2);
		wuli1 = (TextView) findViewById(R.id.sub_mag_wuli1);
		wuli2 = (TextView) findViewById(R.id.sub_mag_wuli2);
		huaxue1 = (TextView) findViewById(R.id.sub_mag_huaxue1);
		huaxue2 = (TextView) findViewById(R.id.sub_mag_huaxue2);
		qita1 = (TextView) findViewById(R.id.sub_mag_qita1);
		qita2 = (TextView) findViewById(R.id.sub_mag_qita2);

		selectOnClicListener theOnClicListener = new selectOnClicListener();
		mainPageTextView.setOnClickListener(theOnClicListener);
		existsTextView.setOnClickListener(theOnClicListener);
		webSubTextView.setOnClickListener(theOnClicListener);

		choiseMenuOnClickLinstener theMenuOnClickLinstener = new choiseMenuOnClickLinstener();
		yixue1.setOnClickListener(theMenuOnClickLinstener);
		yixue2.setOnClickListener(theMenuOnClickLinstener);
		shengwu1.setOnClickListener(theMenuOnClickLinstener);
		shengwu2.setOnClickListener(theMenuOnClickLinstener);
		wuli1.setOnClickListener(theMenuOnClickLinstener);
		wuli2.setOnClickListener(theMenuOnClickLinstener);
		huaxue1.setOnClickListener(theMenuOnClickLinstener);
		huaxue2.setOnClickListener(theMenuOnClickLinstener);
		qita1.setOnClickListener(theMenuOnClickLinstener);
		qita2.setOnClickListener(theMenuOnClickLinstener);

		initializeBasicOperate();// ��ʼ��

		// �߳��ڲ�ͨ��
		subHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case WEB_PAGE_READY:
					ShowSubPageListView();// ��ʾ����
					break;
				case SUB_GOING:
					Toast.makeText(SciSubMagazine.this, "���ڶ��ĸ��ڿ��ĸ���,�����ĵȴ�...", Toast.LENGTH_SHORT).show();
					break;
				case SUB_FINISH:
					Toast.makeText(SciSubMagazine.this, "�������!", Toast.LENGTH_SHORT).show();
					isSubing = false;
					break;
				case UPDATE_BEGIN_INFO:
					Toast.makeText(SciSubMagazine.this, Html.fromHtml("���ڿ�ʼ����...���Ժ�<br/>����ǿ���˳�<br/>����Ͽ���������"), Toast.LENGTH_LONG).show();
					break;
				case THE_CON_NOT_CON:
					Toast.makeText(SciSubMagazine.this, Html.fromHtml("��ǰ����������<br/>��������"), Toast.LENGTH_LONG).show();
					break;
				default:
					break;
				}
			}

		};

	}

	/**
	 * show ���ò˵�ѡ���¼�
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class selectOnClicListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.sub_mag_select_mainpage:
				// �����ҳ��ť��رյ�ǰ���Ľ���
				if (!isSubing) {
					Intent intent = new Intent(SciSubMagazine.this, SciMainActivity.class);
					setResult(GlobalFlag.MAIN_PAGE, intent);
					finish();
				}
				break;
			case R.id.sub_mag_select_exist:
				// ��ת�������ڿ�����
				if (!isSubing) {
					currentPage = GlobalFlag.SOURCE_PAGE;
					sourceListView.setAdapter(null);// ����б�����
					setCanclaChoiseMenu();// ���ο�Ŀ�˵�
					showExistsSourcePage();
				}
				break;
			case R.id.sub_mag_select_plus:
				// ��ת�������ڿ�����(Ĭ��Ϊҽѧ1)
				currentPage = GlobalFlag.WEB_PAGE_YIXUE_1;
				sourceListView.setAdapter(null);// ����б�����
				setShowChoiseMenu();// ��ʾ��Ŀ�˵�
				GetWebPage getWebPage = new GetWebPage();
				Thread getThread = new Thread(getWebPage);
				getThread.start();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * show ��Ŀѡ��˵���ť�����¼�
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class choiseMenuOnClickLinstener implements OnClickListener {

		@Override
		public void onClick(View v) {
			sourceListView.setAdapter(null);// ����б�����
			switch (v.getId()) {
			case R.id.sub_mag_yixue1:
				currentPage = GlobalFlag.WEB_PAGE_YIXUE_1;
				break;
			case R.id.sub_mag_yixue2:
				currentPage = GlobalFlag.WEB_PAGE_YIXUE_2;
				break;
			case R.id.sub_mag_shengwu1:
				currentPage = GlobalFlag.WEB_PAGE_SHENGWU_1;
				break;
			case R.id.sub_mag_shengwu2:
				currentPage = GlobalFlag.WEB_PAGE_SHENGWU_2;
				break;
			case R.id.sub_mag_wuli1:
				currentPage = GlobalFlag.WEB_PAGE_WULI_1;
				break;
			case R.id.sub_mag_wuli2:
				currentPage = GlobalFlag.WEB_PAGE_WULI_2;
				break;
			case R.id.sub_mag_huaxue1:
				currentPage = GlobalFlag.WEB_PAGE_HUAXUE_1;
				break;
			case R.id.sub_mag_huaxue2:
				currentPage = GlobalFlag.WEB_PAGE_HUAXUE_2;
				break;
			case R.id.sub_mag_qita1:
				currentPage = GlobalFlag.WEB_PAGE_QITA_1;
				break;
			case R.id.sub_mag_qita2:
				currentPage = GlobalFlag.WEB_PAGE_QITA_2;
				break;

			default:
				break;
			}
			GetWebPage getWebPage = new GetWebPage();
			Thread getThread = new Thread(getWebPage);
			getThread.start();
		}

	}

	/**
	 * show ��ʼ���������ݺͺ�̨�ؼ�
	 */
	private void initializeBasicOperate() {

		dBopearte = new DBopearte(SciSubMagazine.this);// ʵ����ҵ����
		theSetting = dBopearte.getTheSetting();// ��ȡ��������
		setCanclaChoiseMenu();// ���ο�Ŀ�˵�
		showExistsSourcePage();// Ĭ����ʾ�����ڿ�����
	}

	/**
	 * show ��ʾ�����ڿ����Ľ���
	 */
	private void showExistsSourcePage() {
		sourceMagazines = dBopearte.getSourceMagazines();// ��ȡ���еĶ���Դ
		ShowSubPageListView();// ��ʾ�ý���
	}

	/**
	 * show �����ڶ��ƽ�����ʾ��Ŀ�˵�
	 */
	private void setShowChoiseMenu() {
		theChoiseMenuLayout.setVisibility(View.VISIBLE);// ��ʾ
	}

	/**
	 * show ����ȡ����ʾ��Ŀ�˵�
	 */
	private void setCanclaChoiseMenu() {
		theChoiseMenuLayout.setVisibility(View.GONE);// �����Ҳ�ռ�ռ�
	}

	/**
	 * show ��ȡ����ҳ����߳�
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class GetWebPage implements Runnable {

		@Override
		public void run() {
			GetMagazineSource getMagazineSource = new GetMagazineSource();
			switch (currentPage) {
			case GlobalFlag.WEB_PAGE_YIXUE_1:
				sourceMagazines = getMagazineSource.getYixue(1);
				break;
			case GlobalFlag.WEB_PAGE_YIXUE_2:
				sourceMagazines = getMagazineSource.getYixue(2);
				break;
			case GlobalFlag.WEB_PAGE_YIXUE_3:
				sourceMagazines = getMagazineSource.getYixue(3);
				break;
			case GlobalFlag.WEB_PAGE_SHENGWU_1:
				sourceMagazines = getMagazineSource.getshengwu(1);
				break;
			case GlobalFlag.WEB_PAGE_SHENGWU_2:
				sourceMagazines = getMagazineSource.getshengwu(2);
				break;
			case GlobalFlag.WEB_PAGE_SHENGWU_3:
				sourceMagazines = getMagazineSource.getshengwu(3);
				break;
			case GlobalFlag.WEB_PAGE_WULI_1:
				sourceMagazines = getMagazineSource.getwuli(1);
				break;
			case GlobalFlag.WEB_PAGE_WULI_2:
				sourceMagazines = getMagazineSource.getwuli(2);
				break;
			case GlobalFlag.WEB_PAGE_HUAXUE_1:
				sourceMagazines = getMagazineSource.gethuaxue(1);
				break;
			case GlobalFlag.WEB_PAGE_HUAXUE_2:
				sourceMagazines = getMagazineSource.gethuaxue(2);
				break;
			case GlobalFlag.WEB_PAGE_QITA_1:
				sourceMagazines = getMagazineSource.getqita(1);
				break;
			case GlobalFlag.WEB_PAGE_QITA_2:
				sourceMagazines = getMagazineSource.getqita(1);
				break;
			default:
				break;
			}
			// ������Ϣ,֪ͨ��ʾUI
			Message showReadyMessage = new Message();
			showReadyMessage.what = WEB_PAGE_READY;
			subHandler.sendMessage(showReadyMessage);
		}

	}

	/**
	 * show �����ж���ԴͬlistView�󶨲���ʾ
	 */
	private void ShowSubPageListView() {

		// String MagazineJID = "JID";
		String MagazineName = "MagazineName";
		String MagazineAff = "MagazineAff";
		String choiseString = "choiseSub";
		// String SubNum = "MagazineSubNum";

		if (sourceMagazines == null) {
			Toast.makeText(SciSubMagazine.this, "δ֪����,������APP", Toast.LENGTH_SHORT).show();
			return;
		}
		if (sourceMagazines.size() <= 0) {
			Toast.makeText(SciSubMagazine.this, "��ǰ�ڿ�Դ������", Toast.LENGTH_SHORT).show();
		} else {
			List<Map<String, Object>> sourceMagazinesList = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < sourceMagazines.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				// map.put(MagazineJID,
				// sourceMagazines.get(i).getMagazineJID());
				map.put(MagazineName, sourceMagazines.get(i).getMagazineName());
				map.put(MagazineAff, sourceMagazines.get(i).getMagazineAffectoi());
				// map.put(SubNum, sourceMagazines.get(i).getMagazineSubNum());
				String subtagString = null;

				if (sourceMagazines.get(i).getMagazineSub() == GlobalFlag.magazine_sub_flag_true) {
					subtagString = GlobalFlag.MAGAZINE_SUB_TRUE_STRING;
				} else {
					subtagString = GlobalFlag.MAGAZINE_SUB_FALSE_STRING;
				}
				map.put(choiseString, subtagString);
				sourceMagazinesList.add(map);
			}
			simpleAdapter = new SubMagazineAdapter(SciSubMagazine.this, sourceMagazinesList);
			sourceListView.setAdapter(simpleAdapter);
			sourceListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					simpleAdapter.setIndex(arg2);
					simpleAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	// ��������Դͬ�б��ӳ��
	private class SubMagazineAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater layoutInflater;
		private List<Map<String, Object>> list;
		private int index = 0;

		// String MagazineJID = "JID";
		String MagazineName = "MagazineName";
		String MagazineAff = "MagazineAff";
		String choiseString = "choiseSub";

		// String SubNum = "MagazineSubNum";

		// ���췽��������list���ݵľ�����һ�����ݵ���Ϣ
		public SubMagazineAdapter(Context context, List<Map<String, Object>> list) {
			this.context = context;
			layoutInflater = LayoutInflater.from(this.context);
			this.list = list;
		}

		public void setIndex(int select) {
			this.index = select;
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
				convertView = layoutInflater.inflate(R.layout.submagazinelistview, null);
			}

			TextView theContext = (TextView) convertView.findViewById(R.id.sub_magazineContext);
			TextView tagsub = (TextView) convertView.findViewById(R.id.sub_subtag);

			if (index == position) {
				convertView.findViewById(R.id.sub_magazineContext).setSelected(true);
			} else {
				convertView.findViewById(R.id.sub_magazineContext).setSelected(false);
			}

			// ���������С
			if (theSetting.getUseBigWord() == GlobalFlag.use_big_word_true) {
				theContext.setTextSize(GlobalSize.word_big_size);

				tagsub.setTextSize(GlobalSize.word_big_size);

			} else {
				theContext.setTextSize(GlobalSize.word_simple_size);
				tagsub.setTextSize(GlobalSize.word_simple_size);

			}
			// ��������Ϊ��ɫ
			theContext.setTextColor(Color.BLACK);
			tagsub.setTextColor(Color.BLACK);
			// �����ı�����
			String contextString = list.get(position).get(MagazineName).toString() + ":" + list.get(position).get(MagazineAff).toString();
			theContext.setText(contextString);
			tagsub.setText(Html.fromHtml("&nbsp;") + list.get(position).get(choiseString).toString() + Html.fromHtml("&nbsp;"));

			if (list.get(position).get(choiseString).toString().equals(GlobalFlag.MAGAZINE_SUB_TRUE_STRING)) {
				// �Ѷ�����ȡ�����ģ����ı䶩��ͼ��
				tagsub.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// ִ��ȡ���ղ�ҵ��
						if (!dBopearte.cancleSubMagazine(sourceMagazines.get(currentLine).getMagazineJID())) {
							Log.e(TAG_STRING, "SubMagazineAdapter fail");
						}

						Map<String, Object> map = new HashMap<String, Object>();
						// map.put(MagazineJID,
						// list.get(currentLine).get(MagazineJID).toString());
						map.put(MagazineName, list.get(currentLine).get(MagazineName).toString());
						map.put(MagazineAff, list.get(currentLine).get(MagazineAff).toString());
						// map.put(SubNum,
						// list.get(currentLine).get(SubNum).toString());
						map.put(choiseString, GlobalFlag.MAGAZINE_SUB_FALSE_STRING);
						list.set(currentLine, map);
						notifyDataSetChanged();// ֪ͨ���������޸�
					}
				});
			} else {
				// δ���������ڿ�,���ı�ͼ��
				tagsub.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (!isSubing) {
							subMagazineThread thesubMagazineThread = new subMagazineThread();
							thesubMagazineThread.setCurrentLine(currentLine);
							Thread subThread = new Thread(thesubMagazineThread);
							subThread.start();
							isSubing = true;

							while (!subJudge)
								;// �ȴ������жϽ��
							if (conOK) {
								Map<String, Object> map = new HashMap<String, Object>();
								// map.put(MagazineJID,
								// list.get(currentLine).get(MagazineJID).toString());
								map.put(MagazineName, list.get(currentLine).get(MagazineName).toString());
								map.put(MagazineAff, list.get(currentLine).get(MagazineAff).toString());
								// map.put(SubNum,
								// list.get(currentLine).get(SubNum).toString());
								map.put(choiseString, GlobalFlag.MAGAZINE_SUB_TRUE_STRING);
								list.set(currentLine, map);
								notifyDataSetChanged();// ֪ͨ���������޸�
							}
						}
					}
				});
			}
			return convertView;
		}
	}

	/**
	 * show ִ�ж����ڿ����߳�
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class subMagazineThread implements Runnable {

		private int currentLine;

		public void setCurrentLine(int currentLine) {
			this.currentLine = currentLine;
		}

		@Override
		public void run() {
			boolean isExist = true;
			if (currentPage != GlobalFlag.SOURCE_PAGE) {
				isExist = false;
			}

			Message beginMessage = new Message();
			beginMessage.what = SUB_GOING;
			subHandler.sendMessage(beginMessage);

			// ����Ƿ���������
			judgeCon juCon = new judgeCon();
			juCon.setCurrentLine(currentLine);
			juCon.setisExist(isExist);
			Thread juThread = new Thread(juCon);
			juThread.start();

		}

	}

	/**
	 * show ����Ƿ�����������߳�
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class judgeCon implements Runnable {

		private int currentLine;
		private boolean isExist;

		public void setCurrentLine(int currentLine) {
			this.currentLine = currentLine;
		}

		public void setisExist(boolean is) {
			this.isExist = is;
		}

		@Override
		public void run() {
			ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cwjManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				Message beginMessage = new Message();
				beginMessage.what = UPDATE_BEGIN_INFO;
				subHandler.sendMessage(beginMessage);

				subJudge = true;
				conOK = true;

				// ִ�и��²���
				if (!dBopearte.subAMagazine(sourceMagazines.get(currentLine), isExist)) {
					Log.e(TAG_STRING, "SubMagazineAdapter fail");
				}

				Message endMessage = new Message();
				endMessage.what = SUB_FINISH;
				subHandler.sendMessage(endMessage);
			} else {
				Message noConMessage = new Message();
				noConMessage.what = THE_CON_NOT_CON;
				subHandler.sendMessage(noConMessage);

				isSubing = false;// �����±�־ȡ��
				subJudge = true;
				conOK = false;

			}

		}
	}

}
