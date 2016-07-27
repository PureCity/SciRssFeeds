package com.jincity.service.auto;

import com.jincity.control.db.DBopearte;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

public class AutoUpdateNews extends Service {

	private boolean flag = true;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("auto update", "create");
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (flag) {
					try {
						Thread.sleep(60 * 1000);// 休息1分钟
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Time time = new Time();
					time.setToNow();
					int hour = time.hour;
					int minute = time.minute;
					if (hour == 0 && minute == 1) {
						// 每天0时1分
						DBopearte dBopearte = new DBopearte(AutoUpdateNews.this);
						dBopearte.AutoUpdateEveryDay();
						Log.d("Auto update", "finish");
					}
				}

			}
		}).start();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
