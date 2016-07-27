package com.jincity.service.auto;

import com.jincity.control.db.DBopearte;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

public class AutoClearArticles extends Service {
	private boolean flag = true;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("auto Clear", "create");
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (flag) {
					try {
						Thread.sleep(60 * 1000);// –›œ¢1∑÷÷”
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Time time = new Time();
					time.setToNow();
					int day = time.weekDay;
					int hour = time.hour;
					int minute = time.minute;
					if (day == 2 && hour == 0 && minute == 0) {
						DBopearte dBopearte = new DBopearte(AutoClearArticles.this);
						dBopearte.AutoClearEveryWeek();
						Log.d("Auto clear", "finish");
					}
				}

			}
		}).start();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		flag = false;
	}
}
