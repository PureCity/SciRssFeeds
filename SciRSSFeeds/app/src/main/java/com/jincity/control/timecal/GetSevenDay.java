package com.jincity.control.timecal;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.jincity.common.name.GlobalURL;

@SuppressLint("SimpleDateFormat")
public class GetSevenDay {

	private static final String TAG = "GetSevenDay";
	private static final String UPDATE_PATH_JUDGE_STRING = GlobalURL.ROOTURL + "sci/index.dat";

	/**
	 * show 从服务器获取最近7次的更新
	 * 
	 * @return 七天的日期(表示形式为 eg:140101)
	 * @throws ParseException
	 *             日期转换错误
	 */
	public String[] getSevenDays() throws ParseException {
		String[] sevenDays = new String[7];
		String theDayString = "";

		try {
			Log.d(TAG, "URL:" + UPDATE_PATH_JUDGE_STRING);
			URL uri = new URL(UPDATE_PATH_JUDGE_STRING);// 定义我们要访问的地址url
			URLConnection ucon = uri.openConnection();// 打开这个url连接
			ucon.connect();
			InputStream is = ucon.getInputStream();// 从上面的链接中取得InputStream
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(100);
			int current = 0;
			/* 一直读到文件结束 */
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			theDayString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "getUpdateInSevenDays error");
		}

		String[] tempDayString = theDayString.split("\r\n");
		for (int i = 0; i < tempDayString.length && i < 7; i++) {
			sevenDays[i] = tempDayString[i];// 仅读取少于等于7条日期记录

		}

		return sevenDays;
	}
}
