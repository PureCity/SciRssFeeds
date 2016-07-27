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

@SuppressLint("SimpleDateFormat")
public class GetSevenDay {

	private static final String TAG = "GetSevenDay";
	private static final String UPDATE_PATH_JUDGE_STRING = "http://www.weisci.com/sci/index.dat";

	/**
	 * show �ӷ�������ȡ���7�εĸ���
	 * 
	 * @return ���������(��ʾ��ʽΪ eg:140101)
	 * @throws ParseException
	 *             ����ת������
	 */
	public String[] getSevenDays() throws ParseException {
		String[] sevenDays = new String[7];
		String theDayString = null;

		try {
			Log.d(TAG, "URL:" + UPDATE_PATH_JUDGE_STRING);
			URL uri = new URL(UPDATE_PATH_JUDGE_STRING);// ��������Ҫ���ʵĵ�ַurl
			URLConnection ucon = uri.openConnection();// �����url����
			ucon.connect();
			InputStream is = ucon.getInputStream();// �������������ȡ��InputStream
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(100);
			int current = 0;
			/* һֱ�����ļ����� */
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			theDayString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "getUpdateInSevenDays error");
		}

		String[] tempDayString = theDayString.split("\n");
		for (int i = 0; i < tempDayString.length; i++) {
			sevenDays[i] = tempDayString[i];// ����ȡ���ڵ���7�����ڼ�¼
		}

		return sevenDays;
	}
}
