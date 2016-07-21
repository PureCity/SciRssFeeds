package com.jincity.database.db;

import com.jincity.common.name.DatabaseNameCom;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RssSqliteOpenHelper {

	private static final String TAG = "RssSqliteOpenHelper";

	// 创建订阅源表的基本语句
	private static final String CREATE_RSSRESOURCE_STRING = "create table " + DatabaseNameCom.ALL_RSS_SOURCE_MAG + " ( " + DatabaseNameCom.RSS_RESOURCE_JID_STRING + " text primary key, "
			+ DatabaseNameCom.RSS_RESOURCE_NAME_STRING + " text not null, " + DatabaseNameCom.RSS_RESOURCE_AFFECTOI_STRING + " text, " + DatabaseNameCom.RSS_RESOURCE_SUBNUM_STRING + " integer, "
			+ DatabaseNameCom.RSS_RESOURCE_SUB_STRING + " integer not null);";

	// 创建已订阅的期刊表的基本语句
	private static final String CREATE_SUBRSS_STRING = "create table " + DatabaseNameCom.SUB_RSS_MAG + " ( " + DatabaseNameCom.SUB_MAG_JID_STRING + " text primary key, "
			+ DatabaseNameCom.SUB_MAG_NAME_STRING + " text not null, " + DatabaseNameCom.SUB_MAG_NUM_STRING + " integer not null, " + DatabaseNameCom.SUB_MAG_COLL_NUM_STRING + " integer not null );";

	// 创建收藏订阅的文章表的基本语句
	private static final String CREATE_COLL_ARTICLE_STRING = "create table " + DatabaseNameCom.COLL_ARTICLE + " ( " + DatabaseNameCom.COL_ARTICLE_GID_STRING + " text primary key, "
			+ DatabaseNameCom.COL_ARTICLE_NAME_STRING + " text not null, " + DatabaseNameCom.COL_ARTICLE_EN_NAME_STRING + " text, " + DatabaseNameCom.COL_ARTICLE_LINK_STRING + " text not null, "
			+ DatabaseNameCom.COL_ARTICLE_DESCRIPTION_STRING + " text, " + DatabaseNameCom.COL_ARTICLE_EN_DESCRIPTION_STRING + " text, " + DatabaseNameCom.COL_PUBDATE_STRING + " text not null, "
			+ DatabaseNameCom.COL_ARTICLE_MAG_STRING + " text not null, " + DatabaseNameCom.COL_ARTICLE_IMAGE_URL_STRING + " text, " + DatabaseNameCom.COL_ART_MAG_JID_STRING + " text not null );";

	// 创建设置的基本表
	private static final String CREATE_SETTING_STRING = "create table " + DatabaseNameCom.SETTING_TOOL_STRING + " ( " + DatabaseNameCom.TYPE_STRING + " text primary key, "
			+ DatabaseNameCom.TYPE_VALUE_STRING + " integer not null );";

	/**
	 * show 初始化基本数据库
	 * 
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean onCreate(SQLiteDatabase db) {
		// 初始化创建表
		db.beginTransaction();
		try {
			db.execSQL(CREATE_RSSRESOURCE_STRING);
			db.execSQL(CREATE_SUBRSS_STRING);
			db.execSQL(CREATE_COLL_ARTICLE_STRING);
			db.execSQL(CREATE_SETTING_STRING);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "create basic table fialed");
			e.printStackTrace();
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 根据期刊的JID在GID前加上大写字母M为名创建文章列表
	 * 
	 * @param JID
	 *            期刊标志
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean createMagazineTable(String JID, SQLiteDatabase db) {
		String theTableJID = "M" + JID;// 表名为 M + 期刊JID

		String CREATE_MAGAZINE_ARTICLE_STRING = "create table " + theTableJID + " ( " + DatabaseNameCom.ARTICLE_GID_STRING + " text primary key, " + DatabaseNameCom.ARTICLE_NAME_STRING
				+ " text not null, " + DatabaseNameCom.ARTICLE_EN_NANE_STRING + " text," + DatabaseNameCom.ARTICLE_LINK_STRING + " text not null, " + DatabaseNameCom.ARTICLE_DESCRIPTION_STRING
				+ " text, " + DatabaseNameCom.ARTICLE_EN_EDSCRIPTION_STRING + " text, " + DatabaseNameCom.ARTICLE_PUBDATE_STRING + " text not null, " + DatabaseNameCom.ARTICLE_IMAGE_URL_STRING
				+ " text not null, " + DatabaseNameCom.ARTICLE_MAGAZINE_NAME_STRING + " text not null, " + DatabaseNameCom.ARTICLE_COLLECTION_STRING + " integer not null );";
		db.beginTransaction();
		try {
			db.execSQL(CREATE_MAGAZINE_ARTICLE_STRING);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG, "create table " + theTableJID + "failed");
			e.printStackTrace();
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

}
