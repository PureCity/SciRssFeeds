package com.jincity.control.analyse;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.util.Log;

import com.jincity.rss.article.ArticleList;
import com.jincity.type.change.ArticlesChange;

public class UpdateMagazineArticels {

	private static final String TAG_STRING = "UpdateMagazineArticels";

	private static final String RUL_PATH_STRING = "http://www.weisci.com/sci/";

	/**
	 * show ��ȡָ���ڿ���������
	 * 
	 * @param sevenDays
	 *            ʱ��(��ʽ��140131)
	 * @param MagazineJID
	 *            �ڿ���JID
	 * @return ���ڿ�7�������еĸ��µ�����
	 */
	public ArrayList<ArticleList> getUpdateInSevenDays(String[] sevenDays, String MagazineJID) {
		ArrayList<ArticleList> articleLists = new ArrayList<ArticleList>();

		String fileName = MagazineJID + ".dat";
		String[] sevenDaysContext = new String[7];// �������

		// ��ȡ7������ڿ������и����ļ�
		for (int i = 0; i < sevenDays.length; i++) {
			if (sevenDays[i] == null) {
				continue;
			}
			try {
				Log.d(TAG_STRING, "URL:" + RUL_PATH_STRING + sevenDays[i] + "/" + fileName);
				URL uri = new URL(RUL_PATH_STRING + sevenDays[i] + "/" + fileName);// ��������Ҫ���ʵĵ�ַurl
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
				sevenDaysContext[i] = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG_STRING, "getUpdateInSevenDays error");
			}
		}

		// ������Ӧ���ļ�,�õ���Ӧ�������б�
		ArticlesChange articlesChange = new ArticlesChange();
		for (int i = 0; i < sevenDaysContext.length; i++) {
			if (sevenDaysContext[i] == null) {
				continue;
			}
			ArrayList<ArticleList> theArticleLists = articlesChange.changeContextIntoArticlesLists(sevenDaysContext[i]);
			if (theArticleLists == null) {
				continue;
			}
			for (int k = 0; k < theArticleLists.size(); k++) {
				articleLists.add(theArticleLists.get(k));
			}
		}

		return articleLists;
	}

}
