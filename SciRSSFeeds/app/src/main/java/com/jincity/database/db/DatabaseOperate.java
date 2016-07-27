package com.jincity.database.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jincity.common.name.DatabaseNameCom;
import com.jincity.common.name.GlobalFlag;
import com.jincity.order.OrderTheList;
import com.jincity.rss.article.ArticleList;
import com.jincity.rss.article.CollectionArticle;
import com.jincity.rss.magazine.SourceMagazine;
import com.jincity.rss.magazine.SubMagazine;
import com.jincity.rss.setting.Setting;

public class DatabaseOperate {

	private static final String TAG_STRING = "DatabaseOperate";

	// 对数据库中订阅源表的数据操作
	/**
	 * show 向订阅源的数据库表中添加一条订阅源
	 * 
	 * @param oneMagazine
	 *            一个期刊类
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean insertIntoRssSource(SourceMagazine oneMagazine, SQLiteDatabase db) {
		ContentValues newValues = new ContentValues();

		newValues.put(DatabaseNameCom.RSS_RESOURCE_JID_STRING, IntoMagazineJID(oneMagazine.getMagazineJID()));
		newValues.put(DatabaseNameCom.RSS_RESOURCE_NAME_STRING, oneMagazine.getMagazineName());
		newValues.put(DatabaseNameCom.RSS_RESOURCE_AFFECTOI_STRING, oneMagazine.getMagazineAffectoi());
		newValues.put(DatabaseNameCom.RSS_RESOURCE_SUBNUM_STRING, oneMagazine.getMagazineSubNum());
		newValues.put(DatabaseNameCom.RSS_RESOURCE_SUB_STRING, GlobalFlag.magazine_sub_flag_flase);

		try {
			db.insert(DatabaseNameCom.ALL_RSS_SOURCE_MAG, null, newValues);
		} catch (SQLException e) {
			Log.e(TAG_STRING, "insertIntoRssSource fail");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * show 删除一个订阅源
	 * <p>
	 * show 根据期刊JID删除订阅源表中的一条订阅记录
	 * </p>
	 * 
	 * @param MagazineJID
	 *            期刊JID
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean delOneMagazine(String MagazineJID, SQLiteDatabase db) {
		MagazineJID = IntoMagazineJID(MagazineJID);
		db.beginTransaction();
		String where = DatabaseNameCom.RSS_RESOURCE_JID_STRING + "=?";
		try {
			db.delete(DatabaseNameCom.ALL_RSS_SOURCE_MAG, where, new String[] { MagazineJID });
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(TAG_STRING, "delOneMagazine fail");
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 修改一条订阅源
	 * <p>
	 * show 修改订阅该源的数目
	 * </P>
	 * 
	 * @param SourceMagazineJID
	 *            订阅源期刊的JID
	 * @param subNum
	 *            订阅的数目
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean changeMagazineSourceSubNum(String SourceMagazineJID, int subNum, SQLiteDatabase db) {
		SourceMagazineJID = IntoMagazineJID(SourceMagazineJID);
		db.beginTransaction();// 开始事务
		ContentValues updateValues = new ContentValues();

		updateValues.put(DatabaseNameCom.RSS_RESOURCE_SUBNUM_STRING, subNum);// 修改订阅的数目

		String where = DatabaseNameCom.RSS_RESOURCE_JID_STRING + "=?";
		try {
			db.update(DatabaseNameCom.ALL_RSS_SOURCE_MAG, updateValues, where, new String[] { SourceMagazineJID });
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG_STRING, "changeMagazineSourceSubNum fail");
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 修改订阅源的订阅标志
	 * 
	 * @param SourceMagazineJID
	 *            期刊JID
	 * @param subTag
	 *            期刊的订阅标志 1表示订阅 0表示未被订阅
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean changeMagazineSourceSubTag(String SourceMagazineJID, int subTag, SQLiteDatabase db) {
		SourceMagazineJID = IntoMagazineJID(SourceMagazineJID);
		db.beginTransaction();// 开始事务
		ContentValues updateValues = new ContentValues();

		updateValues.put(DatabaseNameCom.RSS_RESOURCE_SUB_STRING, subTag);

		String where = DatabaseNameCom.RSS_RESOURCE_JID_STRING + "=?";
		try {
			db.update(DatabaseNameCom.ALL_RSS_SOURCE_MAG, updateValues, where, new String[] { SourceMagazineJID });
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG_STRING, "changeMagazineSourceSubTag fail");
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 将订阅源表中的所有记录读取出来
	 * 
	 * @param db
	 *            调用的数据库
	 * @return 返回整个订阅源表的所有记录
	 */
	public ArrayList<SourceMagazine> getAllResource(SQLiteDatabase db) {
		ArrayList<SourceMagazine> theResourceArrayList = new ArrayList<SourceMagazine>();

		String[] result_columns = new String[] { DatabaseNameCom.RSS_RESOURCE_JID_STRING, DatabaseNameCom.RSS_RESOURCE_NAME_STRING, DatabaseNameCom.RSS_RESOURCE_AFFECTOI_STRING,
				DatabaseNameCom.RSS_RESOURCE_SUBNUM_STRING, DatabaseNameCom.RSS_RESOURCE_SUB_STRING };
		Cursor cursor = db.query(DatabaseNameCom.ALL_RSS_SOURCE_MAG, result_columns, null, null, null, null, null);

		while (cursor.moveToNext()) {
			SourceMagazine aMagazine = new SourceMagazine();
			aMagazine.setMagazineJID(OutMagazineJID(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.RSS_RESOURCE_JID_STRING))));
			aMagazine.setMagazineName(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.RSS_RESOURCE_NAME_STRING)));
			aMagazine.setMagazineAffectoi(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.RSS_RESOURCE_AFFECTOI_STRING)));
			aMagazine.setMagazineSubNum(cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.RSS_RESOURCE_SUBNUM_STRING)));
			aMagazine.setMagazineSub(cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.RSS_RESOURCE_SUB_STRING)));
			theResourceArrayList.add(aMagazine);
		}
		cursor.close();
		OrderTheList orderTheList = new OrderTheList();
		theResourceArrayList = orderTheList.orderSourceMagazines(theResourceArrayList);
		return theResourceArrayList;
	}

	// 对数据库中已订阅表的数据操作
	/**
	 * show 向已订阅表中添加一条记录代表订阅了一个期刊
	 * 
	 * @param oneSubMagazine
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean subMagazine(SubMagazine oneSubMagazine, SQLiteDatabase db) {

		ContentValues contentValues = new ContentValues();

		contentValues.put(DatabaseNameCom.SUB_MAG_JID_STRING, IntoMagazineJID(oneSubMagazine.getMagazineJID()));
		contentValues.put(DatabaseNameCom.SUB_MAG_NAME_STRING, oneSubMagazine.getMagazineName());
		contentValues.put(DatabaseNameCom.SUB_MAG_NUM_STRING, oneSubMagazine.getMagazineNum());
		contentValues.put(DatabaseNameCom.SUB_MAG_COLL_NUM_STRING, oneSubMagazine.getMagazineCollectionNum());
		db.beginTransaction();
		try {
			db.insertOrThrow(DatabaseNameCom.SUB_RSS_MAG, null, contentValues);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(TAG_STRING, "subMagazine fail");
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 从已订阅表中删除一条记录代表取消该期刊的订阅
	 * 
	 * @param JID
	 *            期刊的JID
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean deleteSubMagazine(String JID, SQLiteDatabase db) {

		JID = IntoMagazineJID(JID);
		String where = DatabaseNameCom.SUB_MAG_JID_STRING + "=?";
		db.beginTransaction();
		try {
			db.delete(DatabaseNameCom.SUB_RSS_MAG, where, new String[] { JID });
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
			Log.e(TAG_STRING, "deleteSubMagazine fail");
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 修改已订阅表的某一期刊内的文章数目
	 * 
	 * @param MagazineJID
	 *            期刊JID
	 * @param articlesNum
	 *            该期刊内的文章总数
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean changeSubMagazineArticleNum(String MagazineJID, int articlesNum, SQLiteDatabase db) {
		MagazineJID = IntoMagazineJID(MagazineJID);
		db.beginTransaction();// 开始事务

		ContentValues updateValues = new ContentValues();
		updateValues.put(DatabaseNameCom.SUB_MAG_NUM_STRING, articlesNum);

		String where = DatabaseNameCom.SUB_MAG_JID_STRING + "=?";
		try {
			db.update(DatabaseNameCom.SUB_RSS_MAG, updateValues, where, new String[] { MagazineJID });
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG_STRING, "changeSubMagazine fail");
			db.endTransaction();
			return false;
		}
		db.endTransaction();

		return true;
	}

	/**
	 * show 修改已订阅期刊表内某一期刊的收藏的文章的数目
	 * 
	 * @param MagazineJID
	 *            期刊JID
	 * @param collArticleNum
	 *            收藏的文章数目
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean changeSubMagazine(String MagazineJID, int collArticleNum, SQLiteDatabase db) {
		MagazineJID = IntoArticleGID(MagazineJID);
		db.beginTransaction();// 开始事务

		ContentValues updateValues = new ContentValues();
		updateValues.put(DatabaseNameCom.SUB_MAG_COLL_NUM_STRING, collArticleNum);

		String where = DatabaseNameCom.SUB_MAG_JID_STRING + "=?";
		try {
			db.update(DatabaseNameCom.SUB_RSS_MAG, updateValues, where, new String[] { MagazineJID });
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG_STRING, "changeSubMagazine fail");
			db.endTransaction();
			return false;
		}
		db.endTransaction();

		return true;
	}

	/**
	 * show 查询已订阅期刊表中所有的记录
	 * 
	 * @param db
	 *            调用的数据库
	 * @return 返回所有的订阅期刊
	 */
	public ArrayList<SubMagazine> getAllSubMagazines(SQLiteDatabase db) {
		ArrayList<SubMagazine> allSubMagazines = new ArrayList<SubMagazine>();

		String[] result_columns = new String[] { DatabaseNameCom.SUB_MAG_JID_STRING, DatabaseNameCom.SUB_MAG_NAME_STRING, DatabaseNameCom.SUB_MAG_NUM_STRING, DatabaseNameCom.SUB_MAG_COLL_NUM_STRING };
		Cursor cursor = db.query(DatabaseNameCom.SUB_RSS_MAG, result_columns, null, null, null, null, null);

		while (cursor.moveToNext()) {
			SubMagazine oneMagazine = new SubMagazine();
			oneMagazine.setMagazineJID(OutMagazineJID(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.SUB_MAG_JID_STRING))));
			oneMagazine.setMagazineName(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.SUB_MAG_NAME_STRING)));
			oneMagazine.setMagazineNum(cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.SUB_MAG_NUM_STRING)));
			oneMagazine.setMagazineCollectionNum(cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.SUB_MAG_COLL_NUM_STRING)));

			allSubMagazines.add(oneMagazine);
		}
		cursor.close();
		OrderTheList orderTheList = new OrderTheList();
		allSubMagazines = orderTheList.orderSubMagazines(allSubMagazines);
		return allSubMagazines;
	}

	// 对数据库中各期刊表的数据操作
	/**
	 * show 向期刊表中添加一篇文章
	 * 
	 * @param articleList
	 *            一个文章列
	 * @param MagazineJID
	 *            期刊JID
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean addOneArticle(ArticleList articleList, String MagazineJID, SQLiteDatabase db) {

		ContentValues newValues = new ContentValues();
		newValues.put(DatabaseNameCom.ARTICLE_GID_STRING, IntoArticleGID(articleList.getArticleGID()));
		newValues.put(DatabaseNameCom.ARTICLE_NAME_STRING, articleList.getArticleName());
		newValues.put(DatabaseNameCom.ARTICLE_EN_NANE_STRING, articleList.getArticleEnName());
		newValues.put(DatabaseNameCom.ARTICLE_LINK_STRING, articleList.getArticleLink());
		newValues.put(DatabaseNameCom.ARTICLE_DESCRIPTION_STRING, articleList.getArticleDescription());
		newValues.put(DatabaseNameCom.ARTICLE_EN_EDSCRIPTION_STRING, articleList.getArticleEnDescription());
		newValues.put(DatabaseNameCom.ARTICLE_PUBDATE_STRING, articleList.getArticlePubDate());
		newValues.put(DatabaseNameCom.ARTICLE_MAGAZINE_NAME_STRING, articleList.getArticleMagazineName());
		newValues.put(DatabaseNameCom.ARTICLE_COLLECTION_STRING, articleList.getArticleCollection());
		newValues.put(DatabaseNameCom.ARTICLE_IMAGE_URL_STRING, articleList.getArticleImageURL());

		db.beginTransaction();
		try {
			db.insert("M" + MagazineJID, null, newValues);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG_STRING, "addOneArticle fial");
			e.printStackTrace();
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 删除整个期刊表(被取消订阅)
	 * 
	 * @param MagazineJID
	 *            期刊JID
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean dropMagazineList(String MagazineJID, SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL("DROP TABLE " + "M" + MagazineJID + ";");
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG_STRING, "dropMagazineList fail");
			e.printStackTrace();
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 删除期刊内的所有文章
	 * 
	 * @param MagazineJID
	 *            期刊JID
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean deleteMagazineList(String MagazineJID, SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL("DELETE FROM " + "M" + MagazineJID + ";");
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG_STRING, "deleteMagazineList fail");
			e.printStackTrace();
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 根据GID修改期刊内的文章(设置收藏属性)
	 * 
	 * @param MagazineJID
	 *            期刊JID
	 * @param articleGID
	 *            文章GID
	 * @param articleColTag
	 *            文章是否被收藏的标志
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean UpdateArticle(String MagazineJID, String articleGID, int articleColTag, SQLiteDatabase db) {
		articleGID = IntoArticleGID(articleGID);
		ContentValues updateValues = new ContentValues();
		updateValues.put(DatabaseNameCom.ARTICLE_COLLECTION_STRING, articleColTag);

		String where = DatabaseNameCom.ARTICLE_GID_STRING + "=?";
		db.beginTransaction();
		try {
			db.update("M" + MagazineJID, updateValues, where, new String[] { articleGID });
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			db.endTransaction();
			Log.e(TAG_STRING, "UpdateArticle fail");
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 查找一个期刊所有的文章(返回文章总数)
	 * 
	 * @param MagazineJID
	 *            期刊JID
	 * @param db
	 *            调用的数据库
	 * @return 返回一个期刊内的文章总数
	 */
	public int MagazineArticleNum(String MagazineJID, SQLiteDatabase db) {
		int num = 0;

		String[] result_columns = new String[] { DatabaseNameCom.ARTICLE_GID_STRING };
		Cursor cursor = db.query("M" + MagazineJID, result_columns, null, null, null, null, null);

		while (cursor.moveToNext()) {
			num++;
		}

		return num;
	}

	/**
	 * show 查找一个期刊所有的文章(返回收藏的文章数目)
	 * 
	 * @param MagazineJID
	 *            期刊JID
	 * @param db
	 *            调用的数据库
	 * @return 返回一个期刊内收藏的文章总数
	 */
	public int MagazineCollArticleNum(String MagazineJID, SQLiteDatabase db) {
		int num = 0;

		String[] result_columns = new String[] { DatabaseNameCom.ARTICLE_COLLECTION_STRING };
		Cursor cursor = db.query("M" + MagazineJID, result_columns, null, null, null, null, null);

		while (cursor.moveToNext()) {
			if (cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.ARTICLE_COLLECTION_STRING)) == GlobalFlag.article_collection_flag_true) {
				num++;
			}
		}
		return num;
	}

	/**
	 * show 查找一个期刊的所有文章(返回整个文章列表)
	 * 
	 * @param MagazineJID
	 *            期刊JID
	 * @param db
	 *            调用的数据库
	 * @return 返回指定期刊的所有文章
	 */
	public ArrayList<ArticleList> getMagazineAllArticle(String MagazineJID, SQLiteDatabase db) {

		ArrayList<ArticleList> magazineAllArticleArrayList = new ArrayList<ArticleList>();

		String[] result_columns = new String[] { DatabaseNameCom.ARTICLE_GID_STRING, DatabaseNameCom.ARTICLE_NAME_STRING, DatabaseNameCom.ARTICLE_EN_NANE_STRING, DatabaseNameCom.ARTICLE_LINK_STRING,
				DatabaseNameCom.ARTICLE_IMAGE_URL_STRING, DatabaseNameCom.ARTICLE_DESCRIPTION_STRING, DatabaseNameCom.ARTICLE_EN_EDSCRIPTION_STRING, DatabaseNameCom.ARTICLE_PUBDATE_STRING,
				DatabaseNameCom.ARTICLE_MAGAZINE_NAME_STRING, DatabaseNameCom.ARTICLE_COLLECTION_STRING };
		Cursor cursor = db.query("M" + MagazineJID, result_columns, null, null, null, null, null);

		while (cursor.moveToNext()) {
			ArticleList aArticleList = new ArticleList();
			aArticleList.setArticleGID(OutArticleGID(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.ARTICLE_GID_STRING))));
			aArticleList.setArticleName(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.ARTICLE_NAME_STRING)));
			aArticleList.setArticleEnName(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.ARTICLE_EN_NANE_STRING)));
			aArticleList.setArticleLink(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.ARTICLE_LINK_STRING)));
			aArticleList.setArticleImageURL(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.ARTICLE_IMAGE_URL_STRING)));
			aArticleList.setArticleDescription(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.ARTICLE_DESCRIPTION_STRING)));
			aArticleList.setArticleEnDescription(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.ARTICLE_EN_EDSCRIPTION_STRING)));
			aArticleList.setArticlePubDate(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.ARTICLE_PUBDATE_STRING)));
			aArticleList.setArticleMagazineName(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.ARTICLE_MAGAZINE_NAME_STRING)));
			aArticleList.setArticleCollection(cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.ARTICLE_COLLECTION_STRING)));
			magazineAllArticleArrayList.add(aArticleList);
		}
		cursor.close();
		OrderTheList orderTheList = new OrderTheList();
		magazineAllArticleArrayList = orderTheList.orderArticleLists(magazineAllArticleArrayList);
		return magazineAllArticleArrayList;
	}

	// 对数据库中收藏的文章表操作
	/**
	 * show 添加一条收藏记录
	 * 
	 * @param collectionArticle
	 *            收藏文章类型
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean addOneCollArticle(CollectionArticle collectionArticle, SQLiteDatabase db) {

		ContentValues newValues = new ContentValues();
		newValues.put(DatabaseNameCom.COL_ARTICLE_GID_STRING, IntoArticleGID(collectionArticle.getArticleGID()));
		newValues.put(DatabaseNameCom.COL_ARTICLE_NAME_STRING, collectionArticle.getArticleName());
		newValues.put(DatabaseNameCom.COL_ARTICLE_EN_NAME_STRING, collectionArticle.getArticleEnName());
		newValues.put(DatabaseNameCom.COL_ARTICLE_LINK_STRING, collectionArticle.getArticleLink());
		newValues.put(DatabaseNameCom.COL_ARTICLE_DESCRIPTION_STRING, collectionArticle.getArticleDescription());
		newValues.put(DatabaseNameCom.COL_ARTICLE_EN_DESCRIPTION_STRING, collectionArticle.getArticleEnDescription());
		newValues.put(DatabaseNameCom.COL_PUBDATE_STRING, collectionArticle.getArticlePubDate());
		newValues.put(DatabaseNameCom.COL_ARTICLE_IMAGE_URL_STRING, collectionArticle.getArticleImageURL());
		newValues.put(DatabaseNameCom.COL_ART_MAG_JID_STRING, IntoMagazineJID(collectionArticle.getMagazineJIDString()));
		newValues.put(DatabaseNameCom.COL_ARTICLE_MAG_STRING, collectionArticle.getArticleMagazineName());

		db.beginTransaction();
		try {
			db.insert(DatabaseNameCom.COLL_ARTICLE, null, newValues);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG_STRING, "addOneCollArticle fail");
			e.printStackTrace();
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 根据文章GID删除一条收藏记录
	 * 
	 * @param articleGID
	 *            文章GID
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean deleteCollArticle(String articleGID, SQLiteDatabase db) {

		articleGID = IntoArticleGID(articleGID);
		String whereClause = DatabaseNameCom.COL_ARTICLE_GID_STRING + "=?";
		db.beginTransaction();
		try {
			db.delete(DatabaseNameCom.COLL_ARTICLE, whereClause, new String[] { articleGID });
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG_STRING, "deleteCollArticle fail");
			e.printStackTrace();
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 查询所有的收藏记录
	 * 
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public ArrayList<CollectionArticle> getAllCollectionArticles(SQLiteDatabase db) {
		ArrayList<CollectionArticle> allCollectionArticles = new ArrayList<CollectionArticle>();

		String[] result_columns = new String[] { DatabaseNameCom.COL_ARTICLE_GID_STRING, DatabaseNameCom.COL_ARTICLE_NAME_STRING, DatabaseNameCom.COL_ARTICLE_EN_NAME_STRING,
				DatabaseNameCom.COL_ARTICLE_LINK_STRING, DatabaseNameCom.COL_ARTICLE_DESCRIPTION_STRING, DatabaseNameCom.COL_ARTICLE_EN_DESCRIPTION_STRING, DatabaseNameCom.COL_PUBDATE_STRING,
				DatabaseNameCom.COL_ARTICLE_IMAGE_URL_STRING, DatabaseNameCom.COL_ART_MAG_JID_STRING, DatabaseNameCom.COL_ARTICLE_MAG_STRING };
		Cursor cursor = db.query(DatabaseNameCom.COLL_ARTICLE, result_columns, null, null, null, null, null);

		while (cursor.moveToNext()) {
			CollectionArticle oneCollectionArticle = new CollectionArticle();
			oneCollectionArticle.setArticleGID(OutArticleGID(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.COL_ARTICLE_GID_STRING))));
			oneCollectionArticle.setArticleName(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.COL_ARTICLE_NAME_STRING)));
			oneCollectionArticle.setArticleEnName(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.COL_ARTICLE_EN_NAME_STRING)));
			oneCollectionArticle.setArticleLink(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.COL_ARTICLE_LINK_STRING)));
			oneCollectionArticle.setArticleDescription(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.COL_ARTICLE_DESCRIPTION_STRING)));
			oneCollectionArticle.setArticleEnDescription(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.COL_ARTICLE_EN_DESCRIPTION_STRING)));
			oneCollectionArticle.setArticlePubDate(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.COL_PUBDATE_STRING)));
			oneCollectionArticle.setArticleImageURL(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.COL_ARTICLE_IMAGE_URL_STRING)));
			oneCollectionArticle.setMagazineJIDString(OutMagazineJID(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.COL_ART_MAG_JID_STRING))));
			oneCollectionArticle.setArticleMagazineName(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.COL_ARTICLE_MAG_STRING)));
			allCollectionArticles.add(oneCollectionArticle);
		}
		cursor.close();
		OrderTheList orderTheList = new OrderTheList();
		allCollectionArticles = orderTheList.orderCollectionArticles(allCollectionArticles);
		return allCollectionArticles;
	}

	// 对数据库中已更新的时间进行操作
	/**
	 * show 向表中添加一条已更新的日期
	 * 
	 * @param theDate
	 *            已更新的日期
	 * @param db
	 * @return
	 */
	public boolean addDateIntoUpdate(String theDate, SQLiteDatabase db) {

		ContentValues newValues = new ContentValues();
		newValues.put(DatabaseNameCom.THE_UPDATE_DAY_STRING, theDate);
		db.beginTransaction();
		try {
			db.insert(DatabaseNameCom.UPDATE_DAY_STRING, null, newValues);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e(TAG_STRING, "addDateIntoUpdate fail");
			e.printStackTrace();
			db.endTransaction();
			return false;
		}
		Log.d(TAG_STRING, "addDateIntoUpdate finish");
		db.endTransaction();
		return true;
	}

	/**
	 * show 向已更新日期表中删除日期(防止数据冗余,删除过时的日期)
	 * 
	 * @param theDate
	 *            需要删除的日期
	 * @param db
	 * @return
	 */
	public boolean delDateIntoUpdate(String theDate, SQLiteDatabase db) {

		String whereClause = DatabaseNameCom.THE_UPDATE_DAY_STRING + "=?";
		db.beginTransaction();
		try {
			db.delete(DatabaseNameCom.UPDATE_DAY_STRING, whereClause, new String[] { theDate });
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG_STRING, "delDateIntoUpdate fail");
			e.printStackTrace();
			db.endTransaction();
			return false;
		}
		Log.d(TAG_STRING, "delDateIntoUpdate:" + theDate);
		db.endTransaction();
		return true;
	}

	/**
	 * show 获取已更新时间表中的时间
	 * 
	 * @param db
	 * @return 返回所有已更新的日期
	 */
	public ArrayList<String> getUpdateDate(SQLiteDatabase db) {
		ArrayList<String> allDateString = new ArrayList<String>();

		String[] result_columns = new String[] { DatabaseNameCom.THE_UPDATE_DAY_STRING };
		Cursor cursor = db.query(DatabaseNameCom.UPDATE_DAY_STRING, result_columns, null, null, null, null, null);

		while (cursor.moveToNext()) {
			String tempString = cursor.getString(cursor.getColumnIndex(DatabaseNameCom.THE_UPDATE_DAY_STRING));
			Log.d(TAG_STRING, "getUpdateDate:" + tempString);
			allDateString.add(tempString);
		}

		return allDateString;
	}

	// 对数据库中的设置表进行操作
	/**
	 * show 向表中写入默认数据
	 * 
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean initializeSetting(SQLiteDatabase db) {

		ContentValues newValues = new ContentValues();

		db.beginTransaction();
		try {
			newValues.put(DatabaseNameCom.TYPE_STRING, DatabaseNameCom.SETTING_USE_OWN_BROWSER_STRING);
			newValues.put(DatabaseNameCom.TYPE_VALUE_STRING, GlobalFlag.use_own_bro_false);
			db.insert(DatabaseNameCom.SETTING_TOOL_STRING, null, newValues);

			newValues.put(DatabaseNameCom.TYPE_STRING, DatabaseNameCom.SETTING_SHOW_ONLY_WIFI_STRING);
			newValues.put(DatabaseNameCom.TYPE_VALUE_STRING, GlobalFlag.show_image_in_wifi_false);
			db.insert(DatabaseNameCom.SETTING_TOOL_STRING, null, newValues);

			newValues.put(DatabaseNameCom.TYPE_STRING, DatabaseNameCom.SETTING_SHOW_PAGE_CARTOON_STRING);
			newValues.put(DatabaseNameCom.TYPE_VALUE_STRING, GlobalFlag.show_page_cartoon_false);
			db.insert(DatabaseNameCom.SETTING_TOOL_STRING, null, newValues);

			newValues.put(DatabaseNameCom.TYPE_STRING, DatabaseNameCom.SETTING_SHOW_CHANGE_CARTOON_STRING);
			newValues.put(DatabaseNameCom.TYPE_VALUE_STRING, GlobalFlag.show_article_change_false);
			db.insert(DatabaseNameCom.SETTING_TOOL_STRING, null, newValues);

			newValues.put(DatabaseNameCom.TYPE_STRING, DatabaseNameCom.SETTING_SHOW_IMAGE_CHOISE_STRING);
			newValues.put(DatabaseNameCom.TYPE_VALUE_STRING, GlobalFlag.show_image_choise_false);
			db.insert(DatabaseNameCom.SETTING_TOOL_STRING, null, newValues);

			newValues.put(DatabaseNameCom.TYPE_STRING, DatabaseNameCom.SETTING_USE_BIG_WORD_STRING);
			newValues.put(DatabaseNameCom.TYPE_VALUE_STRING, GlobalFlag.use_big_word_false);
			db.insert(DatabaseNameCom.SETTING_TOOL_STRING, null, newValues);

			newValues.put(DatabaseNameCom.TYPE_STRING, DatabaseNameCom.SETTING_UPDATE_AUTO_EVERYDAY_STRING);
			newValues.put(DatabaseNameCom.TYPE_VALUE_STRING, GlobalFlag.update_auto_day_false);
			db.insert(DatabaseNameCom.SETTING_TOOL_STRING, null, newValues);

			newValues.put(DatabaseNameCom.TYPE_STRING, DatabaseNameCom.SETTING_CLEAR_EVERYWEEK_STRING);
			newValues.put(DatabaseNameCom.TYPE_VALUE_STRING, GlobalFlag.clear_auto_week_false);
			db.insert(DatabaseNameCom.SETTING_TOOL_STRING, null, newValues);
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG_STRING, "initializeSetting fail");
			e.printStackTrace();
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 修改某一设置的值
	 * 
	 * @param theSetting
	 *            设置类
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public boolean changeSetting(Setting theSetting, SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL("update " + DatabaseNameCom.SETTING_TOOL_STRING + " set " + DatabaseNameCom.TYPE_VALUE_STRING + "=" + theSetting.getUseOwnBro() + " where " + DatabaseNameCom.TYPE_STRING
					+ "=\'" + DatabaseNameCom.SETTING_USE_OWN_BROWSER_STRING + "\';");
			db.execSQL("update " + DatabaseNameCom.SETTING_TOOL_STRING + " set " + DatabaseNameCom.TYPE_VALUE_STRING + "=" + theSetting.getShowImageInWifi() + " where " + DatabaseNameCom.TYPE_STRING
					+ "=\'" + DatabaseNameCom.SETTING_SHOW_ONLY_WIFI_STRING + "\';");
			db.execSQL("update " + DatabaseNameCom.SETTING_TOOL_STRING + " set " + DatabaseNameCom.TYPE_VALUE_STRING + "=" + theSetting.getShowPageCar() + " where " + DatabaseNameCom.TYPE_STRING
					+ "=\'" + DatabaseNameCom.SETTING_SHOW_PAGE_CARTOON_STRING + "\';");
			db.execSQL("update " + DatabaseNameCom.SETTING_TOOL_STRING + " set " + DatabaseNameCom.TYPE_VALUE_STRING + "=" + theSetting.getShowPageChage() + " where " + DatabaseNameCom.TYPE_STRING
					+ "=\'" + DatabaseNameCom.SETTING_SHOW_CHANGE_CARTOON_STRING + "\';");
			db.execSQL("update " + DatabaseNameCom.SETTING_TOOL_STRING + " set " + DatabaseNameCom.TYPE_VALUE_STRING + "=" + theSetting.getShowImageChoise() + " where " + DatabaseNameCom.TYPE_STRING
					+ "=\'" + DatabaseNameCom.SETTING_SHOW_IMAGE_CHOISE_STRING + "\';");
			db.execSQL("update " + DatabaseNameCom.SETTING_TOOL_STRING + " set " + DatabaseNameCom.TYPE_VALUE_STRING + "=" + theSetting.getUseBigWord() + " where " + DatabaseNameCom.TYPE_STRING
					+ "=\'" + DatabaseNameCom.SETTING_USE_BIG_WORD_STRING + "\';");
			db.execSQL("update " + DatabaseNameCom.SETTING_TOOL_STRING + " set " + DatabaseNameCom.TYPE_VALUE_STRING + "=" + theSetting.getUpdateAuto() + " where " + DatabaseNameCom.TYPE_STRING
					+ "=\'" + DatabaseNameCom.SETTING_UPDATE_AUTO_EVERYDAY_STRING + "\';");
			db.execSQL("update " + DatabaseNameCom.SETTING_TOOL_STRING + " set " + DatabaseNameCom.TYPE_VALUE_STRING + "=" + theSetting.getClearAuto() + " where " + DatabaseNameCom.TYPE_STRING
					+ "=\'" + DatabaseNameCom.SETTING_CLEAR_EVERYWEEK_STRING + "\';");

			db.setTransactionSuccessful();
		} catch (SQLException e) {
			Log.e(TAG_STRING, "changeSetting fail");
			db.endTransaction();
			return false;
		}
		db.endTransaction();
		return true;
	}

	/**
	 * show 得到所有的设置内容
	 * 
	 * @param db
	 *            调用的数据库
	 * @return 返回true表示对数据库操作成功,否则失败
	 */
	public Setting getSetting(SQLiteDatabase db) {
		Setting aSetting = new Setting();

		String[] result_columns = new String[] { DatabaseNameCom.TYPE_STRING, DatabaseNameCom.TYPE_VALUE_STRING };
		Cursor cursor = db.query(DatabaseNameCom.SETTING_TOOL_STRING, result_columns, null, null, null, null, null);

		while (cursor.moveToNext()) {
			if (cursor.getString(cursor.getColumnIndex(DatabaseNameCom.TYPE_STRING)).equals(DatabaseNameCom.SETTING_USE_OWN_BROWSER_STRING)) {
				aSetting.setUseOwnBro(cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.TYPE_VALUE_STRING)));
			} else if (cursor.getString(cursor.getColumnIndex(DatabaseNameCom.TYPE_STRING)).equals(DatabaseNameCom.SETTING_SHOW_ONLY_WIFI_STRING)) {
				aSetting.setShowImageInWifi(cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.TYPE_VALUE_STRING)));
			} else if (cursor.getString(cursor.getColumnIndex(DatabaseNameCom.TYPE_STRING)).equals(DatabaseNameCom.SETTING_SHOW_PAGE_CARTOON_STRING)) {
				aSetting.setShowPageCar(cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.TYPE_VALUE_STRING)));
			} else if (cursor.getString(cursor.getColumnIndex(DatabaseNameCom.TYPE_STRING)).equals(DatabaseNameCom.SETTING_SHOW_CHANGE_CARTOON_STRING)) {
				aSetting.setShowPageChage(cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.TYPE_VALUE_STRING)));
			} else if (cursor.getString(cursor.getColumnIndex(DatabaseNameCom.TYPE_STRING)).equals(DatabaseNameCom.SETTING_SHOW_IMAGE_CHOISE_STRING)) {
				aSetting.setShowImageChoise(cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.TYPE_VALUE_STRING)));
			} else if (cursor.getString(cursor.getColumnIndex(DatabaseNameCom.TYPE_STRING)).equals(DatabaseNameCom.SETTING_USE_BIG_WORD_STRING)) {
				aSetting.setUseBigWord(cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.TYPE_VALUE_STRING)));
			} else if (cursor.getString(cursor.getColumnIndex(DatabaseNameCom.TYPE_STRING)).equals(DatabaseNameCom.SETTING_UPDATE_AUTO_EVERYDAY_STRING)) {
				aSetting.setUpdateAuto(cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.TYPE_VALUE_STRING)));
			} else if (cursor.getString(cursor.getColumnIndex(DatabaseNameCom.TYPE_STRING)).equals(DatabaseNameCom.SETTING_CLEAR_EVERYWEEK_STRING)) {
				aSetting.setClearAuto(cursor.getInt(cursor.getColumnIndex(DatabaseNameCom.TYPE_VALUE_STRING)));
			}
		}
		cursor.close();
		return aSetting;
	}

	// 补充
	/**
	 * show 将数据库中neizhi表的数据初始化到订阅源表中
	 * 
	 * @param db
	 * @return 执行结果
	 */
	public boolean setneizhiMagazine(SQLiteDatabase db) {

		SourceMagazine sourceMagazine;
		String[] result_columns = new String[] { DatabaseNameCom.RSS_RESOURCE_JID_STRING, DatabaseNameCom.RSS_RESOURCE_NAME_STRING, DatabaseNameCom.RSS_RESOURCE_AFFECTOI_STRING };
		Cursor cursor = db.query(DatabaseNameCom.SCI_LOCAL_DB_TABLE_NAME, result_columns, null, null, null, null, null);

		while (cursor.moveToNext()) {
			sourceMagazine = new SourceMagazine();
			sourceMagazine.setMagazineJID(OutMagazineJID(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.RSS_RESOURCE_JID_STRING))));
			sourceMagazine.setMagazineName(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.RSS_RESOURCE_NAME_STRING)));
			sourceMagazine.setMagazineAffectoi(cursor.getString(cursor.getColumnIndex(DatabaseNameCom.RSS_RESOURCE_AFFECTOI_STRING)));
			sourceMagazine.setMagazineSubNum(0);// 默认订阅了0篇
			sourceMagazine.setMagazineSub(GlobalFlag.magazine_sub_flag_flase);// 默认未订阅
			if (!insertIntoRssSource(sourceMagazine, db)) {// 写入数据库
				Log.d(TAG_STRING, "setneizhiMagazine error");
				return false;
			}
		}

		return true;
	}

	/**
	 * show 存入数据库的JID前均加上字母M保证开头的0能存入
	 * 
	 * @param MagazineJID
	 * @return
	 */
	private String IntoMagazineJID(String MagazineJID) {
		return "M" + MagazineJID;
	}

	/**
	 * show 取出数据库的JID前去掉大写字母M
	 * 
	 * @param MagazineJID
	 * @return
	 */
	private String OutMagazineJID(String MagazineJID) {
		return MagazineJID.substring(1);
	}

	/**
	 * show 存入数据库的GID前均加上字母M保证开头的0能存入
	 * 
	 * @param ArticleGID
	 * @return
	 */
	private String IntoArticleGID(String ArticleGID) {
		return "M" + ArticleGID;
	}

	/**
	 * show 取出数据库的GID前去掉字母M
	 * 
	 * @param ArticleGID
	 * @return
	 */
	private String OutArticleGID(String ArticleGID) {
		return ArticleGID.substring(1);
	}

}
