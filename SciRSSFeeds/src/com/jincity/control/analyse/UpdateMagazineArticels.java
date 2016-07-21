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
	 * show 获取指定期刊更新内容
	 * 
	 * @param sevenDays
	 *            时间(格式：140131)
	 * @param MagazineJID
	 *            期刊的JID
	 * @return 该期刊7天内所有的更新的文章
	 */
	public ArrayList<ArticleList> getUpdateInSevenDays(String[] sevenDays, String MagazineJID) {
		ArrayList<ArticleList> articleLists = new ArrayList<ArticleList>();

		String fileName = MagazineJID + ".dat";
		String[] sevenDaysContext = new String[7];// 最多七天

		// 获取7内天该期刊的所有更新文件
		for (int i = 0; i < sevenDays.length; i++) {
			if (sevenDays[i] == null) {
				continue;
			}
			try {
				Log.d(TAG_STRING, "URL:" + RUL_PATH_STRING + sevenDays[i] + "/" + fileName);
				URL uri = new URL(RUL_PATH_STRING + sevenDays[i] + "/" + fileName);// 定义我们要访问的地址url
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
				sevenDaysContext[i] = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
				Log.d(TAG_STRING, "getUpdateInSevenDays error");
			}
		}

		// 解析对应的文件,得到对应的文章列表
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
