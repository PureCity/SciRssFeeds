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

	// �����ݿ��ж���Դ������ݲ���
	/**
	 * show ����Դ�����ݿ�������һ������Դ
	 * 
	 * @param oneMagazine
	 *            һ���ڿ���
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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
	 * show ɾ��һ������Դ
	 * <p>
	 * show �����ڿ�JIDɾ������Դ���е�һ�����ļ�¼
	 * </p>
	 * 
	 * @param MagazineJID
	 *            �ڿ�JID
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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
	 * show �޸�һ������Դ
	 * <p>
	 * show �޸Ķ��ĸ�Դ����Ŀ
	 * </P>
	 * 
	 * @param SourceMagazineJID
	 *            ����Դ�ڿ���JID
	 * @param subNum
	 *            ���ĵ���Ŀ
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
	 */
	public boolean changeMagazineSourceSubNum(String SourceMagazineJID, int subNum, SQLiteDatabase db) {
		SourceMagazineJID = IntoMagazineJID(SourceMagazineJID);
		db.beginTransaction();// ��ʼ����
		ContentValues updateValues = new ContentValues();

		updateValues.put(DatabaseNameCom.RSS_RESOURCE_SUBNUM_STRING, subNum);// �޸Ķ��ĵ���Ŀ

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
	 * show �޸Ķ���Դ�Ķ��ı�־
	 * 
	 * @param SourceMagazineJID
	 *            �ڿ�JID
	 * @param subTag
	 *            �ڿ��Ķ��ı�־ 1��ʾ���� 0��ʾδ������
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
	 */
	public boolean changeMagazineSourceSubTag(String SourceMagazineJID, int subTag, SQLiteDatabase db) {
		SourceMagazineJID = IntoMagazineJID(SourceMagazineJID);
		db.beginTransaction();// ��ʼ����
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
	 * show ������Դ���е����м�¼��ȡ����
	 * 
	 * @param db
	 *            ���õ����ݿ�
	 * @return ������������Դ������м�¼
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

	// �����ݿ����Ѷ��ı�����ݲ���
	/**
	 * show ���Ѷ��ı������һ����¼��������һ���ڿ�
	 * 
	 * @param oneSubMagazine
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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
	 * show ���Ѷ��ı���ɾ��һ����¼����ȡ�����ڿ��Ķ���
	 * 
	 * @param JID
	 *            �ڿ���JID
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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
	 * show �޸��Ѷ��ı��ĳһ�ڿ��ڵ�������Ŀ
	 * 
	 * @param MagazineJID
	 *            �ڿ�JID
	 * @param articlesNum
	 *            ���ڿ��ڵ���������
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
	 */
	public boolean changeSubMagazineArticleNum(String MagazineJID, int articlesNum, SQLiteDatabase db) {
		MagazineJID = IntoMagazineJID(MagazineJID);
		db.beginTransaction();// ��ʼ����

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
	 * show �޸��Ѷ����ڿ�����ĳһ�ڿ����ղص����µ���Ŀ
	 * 
	 * @param MagazineJID
	 *            �ڿ�JID
	 * @param collArticleNum
	 *            �ղص�������Ŀ
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
	 */
	public boolean changeSubMagazine(String MagazineJID, int collArticleNum, SQLiteDatabase db) {
		MagazineJID = IntoArticleGID(MagazineJID);
		db.beginTransaction();// ��ʼ����

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
	 * show ��ѯ�Ѷ����ڿ��������еļ�¼
	 * 
	 * @param db
	 *            ���õ����ݿ�
	 * @return �������еĶ����ڿ�
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

	// �����ݿ��и��ڿ�������ݲ���
	/**
	 * show ���ڿ��������һƪ����
	 * 
	 * @param articleList
	 *            һ��������
	 * @param MagazineJID
	 *            �ڿ�JID
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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
	 * show ɾ�������ڿ���(��ȡ������)
	 * 
	 * @param MagazineJID
	 *            �ڿ�JID
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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
	 * show ɾ���ڿ��ڵ���������
	 * 
	 * @param MagazineJID
	 *            �ڿ�JID
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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
	 * show ����GID�޸��ڿ��ڵ�����(�����ղ�����)
	 * 
	 * @param MagazineJID
	 *            �ڿ�JID
	 * @param articleGID
	 *            ����GID
	 * @param articleColTag
	 *            �����Ƿ��ղصı�־
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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
	 * show ����һ���ڿ����е�����(������������)
	 * 
	 * @param MagazineJID
	 *            �ڿ�JID
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����һ���ڿ��ڵ���������
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
	 * show ����һ���ڿ����е�����(�����ղص�������Ŀ)
	 * 
	 * @param MagazineJID
	 *            �ڿ�JID
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����һ���ڿ����ղص���������
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
	 * show ����һ���ڿ�����������(�������������б�)
	 * 
	 * @param MagazineJID
	 *            �ڿ�JID
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����ָ���ڿ�����������
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

	// �����ݿ����ղص����±����
	/**
	 * show ���һ���ղؼ�¼
	 * 
	 * @param collectionArticle
	 *            �ղ���������
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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
	 * show ��������GIDɾ��һ���ղؼ�¼
	 * 
	 * @param articleGID
	 *            ����GID
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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
	 * show ��ѯ���е��ղؼ�¼
	 * 
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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

	// �����ݿ����Ѹ��µ�ʱ����в���
	/**
	 * show ��������һ���Ѹ��µ�����
	 * 
	 * @param theDate
	 *            �Ѹ��µ�����
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
	 * show ���Ѹ������ڱ���ɾ������(��ֹ��������,ɾ����ʱ������)
	 * 
	 * @param theDate
	 *            ��Ҫɾ��������
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
	 * show ��ȡ�Ѹ���ʱ����е�ʱ��
	 * 
	 * @param db
	 * @return ���������Ѹ��µ�����
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

	// �����ݿ��е����ñ���в���
	/**
	 * show �����д��Ĭ������
	 * 
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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
	 * show �޸�ĳһ���õ�ֵ
	 * 
	 * @param theSetting
	 *            ������
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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
	 * show �õ����е���������
	 * 
	 * @param db
	 *            ���õ����ݿ�
	 * @return ����true��ʾ�����ݿ�����ɹ�,����ʧ��
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

	// ����
	/**
	 * show �����ݿ���neizhi������ݳ�ʼ��������Դ����
	 * 
	 * @param db
	 * @return ִ�н��
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
			sourceMagazine.setMagazineSubNum(0);// Ĭ�϶�����0ƪ
			sourceMagazine.setMagazineSub(GlobalFlag.magazine_sub_flag_flase);// Ĭ��δ����
			if (!insertIntoRssSource(sourceMagazine, db)) {// д�����ݿ�
				Log.d(TAG_STRING, "setneizhiMagazine error");
				return false;
			}
		}

		return true;
	}

	/**
	 * show �������ݿ��JIDǰ��������ĸM��֤��ͷ��0�ܴ���
	 * 
	 * @param MagazineJID
	 * @return
	 */
	private String IntoMagazineJID(String MagazineJID) {
		return "M" + MagazineJID;
	}

	/**
	 * show ȡ�����ݿ��JIDǰȥ����д��ĸM
	 * 
	 * @param MagazineJID
	 * @return
	 */
	private String OutMagazineJID(String MagazineJID) {
		return MagazineJID.substring(1);
	}

	/**
	 * show �������ݿ��GIDǰ��������ĸM��֤��ͷ��0�ܴ���
	 * 
	 * @param ArticleGID
	 * @return
	 */
	private String IntoArticleGID(String ArticleGID) {
		return "M" + ArticleGID;
	}

	/**
	 * show ȡ�����ݿ��GIDǰȥ����ĸM
	 * 
	 * @param ArticleGID
	 * @return
	 */
	private String OutArticleGID(String ArticleGID) {
		return ArticleGID.substring(1);
	}

}
