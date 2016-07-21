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

	private RelativeLayout myRelativeLayout;// 当前Activity的布局
	private TextView mainPageTextView;// 主页按钮
	private TextView existsTextView;// 已有期刊按钮
	private TextView webSubTextView;// 定制期刊按钮

	private TextView titleTextView;
	private TextView titleRight;

	private ListView sourceListView;// 期刊列表

	private int currentPage = GlobalFlag.SOURCE_PAGE;// 当前界面(默认为已有期刊界面)
	private boolean isSubing = false;// 当前是否正在订阅

	private ArrayList<SourceMagazine> sourceMagazines;// 当前界面的显示列表
	SubMagazineAdapter simpleAdapter;

	private Setting theSetting;// 设置内容
	private DBopearte dBopearte;// 业务层操作

	Handler subHandler;// 线程通信
	private static final int WEB_PAGE_READY = 1;// 网页列表准备完毕
	private static final int SUB_GOING = 2;// 开始订阅信号
	private static final int SUB_FINISH = 3;// 订阅完成信号

	private boolean subJudge = false;// 网络检查等待判断
	private boolean conOK = true;// 网络是否连接标志

	private LinearLayout theChoiseMenuLayout;// 动态添加控件的面板
	private static final int UPDATE_BEGIN_INFO = 12;// 开始更新信息
	private static final int THE_CON_NOT_CON = 13;// 当前没有网络连接

	// 各科目选择按钮
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
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); // 声明使用自定义标题
		setContentView(R.layout.activity_sci_submagazine);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);// 自定义布局赋值
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

		initializeBasicOperate();// 初始化

		// 线程内部通信
		subHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case WEB_PAGE_READY:
					ShowSubPageListView();// 显示界面
					break;
				case SUB_GOING:
					Toast.makeText(SciSubMagazine.this, "正在订阅该期刊的更新,请耐心等待...", Toast.LENGTH_SHORT).show();
					break;
				case SUB_FINISH:
					Toast.makeText(SciSubMagazine.this, "订阅完成!", Toast.LENGTH_SHORT).show();
					isSubing = false;
					break;
				case UPDATE_BEGIN_INFO:
					Toast.makeText(SciSubMagazine.this, Html.fromHtml("正在开始更新...请稍后<br/>请勿强制退出<br/>请勿断开网络连接"), Toast.LENGTH_LONG).show();
					break;
				case THE_CON_NOT_CON:
					Toast.makeText(SciSubMagazine.this, Html.fromHtml("当前无网络连接<br/>请检查网络"), Toast.LENGTH_LONG).show();
					break;
				default:
					break;
				}
			}

		};

	}

	/**
	 * show 设置菜单选项事件
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class selectOnClicListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.sub_mag_select_mainpage:
				// 点击主页按钮后关闭当前订阅界面
				if (!isSubing) {
					Intent intent = new Intent(SciSubMagazine.this, SciMainActivity.class);
					setResult(GlobalFlag.MAIN_PAGE, intent);
					finish();
				}
				break;
			case R.id.sub_mag_select_exist:
				// 跳转到已有期刊界面
				if (!isSubing) {
					currentPage = GlobalFlag.SOURCE_PAGE;
					sourceListView.setAdapter(null);// 清空列表内容
					setCanclaChoiseMenu();// 屏蔽科目菜单
					showExistsSourcePage();
				}
				break;
			case R.id.sub_mag_select_plus:
				// 跳转到定制期刊界面(默认为医学1)
				currentPage = GlobalFlag.WEB_PAGE_YIXUE_1;
				sourceListView.setAdapter(null);// 清空列表内容
				setShowChoiseMenu();// 显示科目菜单
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
	 * show 科目选择菜单按钮监听事件
	 * 
	 * @author JinCityMoon
	 * 
	 */
	private class choiseMenuOnClickLinstener implements OnClickListener {

		@Override
		public void onClick(View v) {
			sourceListView.setAdapter(null);// 清空列表内容
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
	 * show 初始化基本数据和后台控件
	 */
	private void initializeBasicOperate() {

		dBopearte = new DBopearte(SciSubMagazine.this);// 实例化业务类
		theSetting = dBopearte.getTheSetting();// 获取基本设置
		setCanclaChoiseMenu();// 屏蔽科目菜单
		showExistsSourcePage();// 默认显示已有期刊界面
	}

	/**
	 * show 显示已有期刊订阅界面
	 */
	private void showExistsSourcePage() {
		sourceMagazines = dBopearte.getSourceMagazines();// 获取所有的订阅源
		ShowSubPageListView();// 显示该界面
	}

	/**
	 * show 设置在定制界面显示科目菜单
	 */
	private void setShowChoiseMenu() {
		theChoiseMenuLayout.setVisibility(View.VISIBLE);// 显示
	}

	/**
	 * show 设置取消显示科目菜单
	 */
	private void setCanclaChoiseMenu() {
		theChoiseMenuLayout.setVisibility(View.GONE);// 隐藏且不占空间
	}

	/**
	 * show 获取定制页面的线程
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
			// 发送信息,通知显示UI
			Message showReadyMessage = new Message();
			showReadyMessage.what = WEB_PAGE_READY;
			subHandler.sendMessage(showReadyMessage);
		}

	}

	/**
	 * show 将所有订阅源同listView绑定并显示
	 */
	private void ShowSubPageListView() {

		// String MagazineJID = "JID";
		String MagazineName = "MagazineName";
		String MagazineAff = "MagazineAff";
		String choiseString = "choiseSub";
		// String SubNum = "MagazineSubNum";

		if (sourceMagazines == null) {
			Toast.makeText(SciSubMagazine.this, "未知错误,请重启APP", Toast.LENGTH_SHORT).show();
			return;
		}
		if (sourceMagazines.size() <= 0) {
			Toast.makeText(SciSubMagazine.this, "当前期刊源不存在", Toast.LENGTH_SHORT).show();
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

	// 构建订阅源同列表的映射
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

		// 构造方法，参数list传递的就是这一组数据的信息
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

			// 设置字体大小
			if (theSetting.getUseBigWord() == GlobalFlag.use_big_word_true) {
				theContext.setTextSize(GlobalSize.word_big_size);

				tagsub.setTextSize(GlobalSize.word_big_size);

			} else {
				theContext.setTextSize(GlobalSize.word_simple_size);
				tagsub.setTextSize(GlobalSize.word_simple_size);

			}
			// 设置字体为黑色
			theContext.setTextColor(Color.BLACK);
			tagsub.setTextColor(Color.BLACK);
			// 设置文本内容
			String contextString = list.get(position).get(MagazineName).toString() + ":" + list.get(position).get(MagazineAff).toString();
			theContext.setText(contextString);
			tagsub.setText(Html.fromHtml("&nbsp;") + list.get(position).get(choiseString).toString() + Html.fromHtml("&nbsp;"));

			if (list.get(position).get(choiseString).toString().equals(GlobalFlag.MAGAZINE_SUB_TRUE_STRING)) {
				// 已订阅则取消订阅，并改变订阅图标
				tagsub.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 执行取消收藏业务
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
						notifyDataSetChanged();// 通知适配器被修改
					}
				});
			} else {
				// 未订阅则订阅期刊,并改变图标
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
								;// 等待网络判断结果
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
								notifyDataSetChanged();// 通知适配器被修改
							}
						}
					}
				});
			}
			return convertView;
		}
	}

	/**
	 * show 执行订阅期刊的线程
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

			// 检查是否连接网络
			judgeCon juCon = new judgeCon();
			juCon.setCurrentLine(currentLine);
			juCon.setisExist(isExist);
			Thread juThread = new Thread(juCon);
			juThread.start();

		}

	}

	/**
	 * show 检查是否连接网络的线程
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

				// 执行更新操作
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

				isSubing = false;// 将更新标志取消
				subJudge = true;
				conOK = false;

			}

		}
	}

}
