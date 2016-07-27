package com.jincity.control.db;

import java.text.ParseException;
import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jincity.common.name.DatabaseNameCom;
import com.jincity.common.name.GlobalFlag;
import com.jincity.control.analyse.GetMagazineSource;
import com.jincity.control.analyse.UpdateMagazineArticels;
import com.jincity.control.timecal.GetSevenDay;
import com.jincity.database.db.DatabaseOperate;
import com.jincity.database.db.RssSqliteOpenHelper;
import com.jincity.rss.article.ArticleList;
import com.jincity.rss.article.CollectionArticle;
import com.jincity.rss.magazine.SourceMagazine;
import com.jincity.rss.magazine.SubMagazine;
import com.jincity.rss.setting.Setting;
import com.jincity.type.change.ArticleChangeType;

public class DBopearte {

	private static final String TAG_STRING = "DBopearte";

	// Ĭ�ϴ����ݿ��ж�ȡ������������
	private ArrayList<SourceMagazine> sourceMagazines;// ����Դ�б�
	private ArrayList<SubMagazine> subMagazines;// �Ѷ��ĵ��ڿ��б�
	private ArrayList<CollectionArticle> collectionArticles;// �ղص������б�
	private ArrayList<ArticleList> currentMagazineLists;// ��ǰѡ���ڿ��������б�
	private Setting theSetting;// ����Ļ�������

	boolean theNewUpdate = false;// �Ƿ��Ѿ������µĸ���
	public int theNewUpdateFlag = 0;// ���¸��µı�־ 0 ��Ĭ�� 1��ʾ�����¸��� -1��ʾ�������¸��� -2�������ж�

	private SQLiteDatabase db;// �������ݿ�
	private Context context;
	DatabaseOperate databaseOperate;

	/**
	 * show ��ȡ��ǰ�Ƿ������¸��µı�־
	 * 
	 * @return
	 */
	public int getTheUPdateFlag() {
		return theNewUpdateFlag;
	}

	/**
	 * show ��ʼ��ҵ���߼������ݿ�����
	 * 
	 * @param theContext
	 */
	public DBopearte(Context theContext) {
		context = theContext;
		databaseOperate = new DatabaseOperate();
		dbOpen();
		sourceMagazines = databaseOperate.getAllResource(db);
		subMagazines = databaseOperate.getAllSubMagazines(db);
		collectionArticles = databaseOperate.getAllCollectionArticles(db);
		theSetting = databaseOperate.getSetting(db);
		dbClose();
	}

	/**
	 * show ��ʼ��ҵ���߼������ݿ�����
	 * 
	 * @param theContext
	 * @param firstUse
	 *            ���ݿ��ļ��Ƿ���ڵ��ж�
	 */
	public DBopearte(Context theContext, boolean firstUse) {// ��ʼ���������ݿ�
		context = theContext;
		databaseOperate = new DatabaseOperate();

		// �жϻ������ݿ��Ƿ����
		if (firstUse) {
			setBasicDB();// ��ʼ���������ݿ�
			dbOpen();
			databaseOperate.setneizhiMagazine(db);
		}

		dbOpen();
		sourceMagazines = databaseOperate.getAllResource(db);
		subMagazines = databaseOperate.getAllSubMagazines(db);
		collectionArticles = databaseOperate.getAllCollectionArticles(db);
		theSetting = databaseOperate.getSetting(db);
		dbClose();

		Log.d(TAG_STRING, "db path=" + db.getPath());
		dbClose();
	}

	// ��������
	/**
	 * show �򿪻򴴽����ݿ����
	 * <p>
	 * show <b><i>��ҵ�� �����</i></b>
	 * </p>
	 */
	public void dbOpen() {
		db = context.openOrCreateDatabase(DatabaseNameCom.RSS_DATABASENAME, Context.MODE_PRIVATE, null);
	}

	/**
	 * show �ر����ݿ�
	 * <p>
	 * show <b><i>��ҵ�� �����</i></b>
	 * </p>
	 */
	public void dbClose() {
		db.close();
	}

	/**
	 * show ��ʼ�����ݿ���Դ
	 * <p>
	 * show <b><i>��ҵ�� �����</i></b>
	 * </p>
	 * 
	 * @return true��ʾ�ɹ� false��ʾʧ��
	 */
	public boolean setBasicDB() {
		DatabaseFirstOperate databaseFirstOperate = new DatabaseFirstOperate(context);
		return databaseFirstOperate.copyMyDB();
	}

	// �ӷ�������ȡ����Դ�Ĳ���
	/**
	 * show �ӷ�������ȡ�ڿ�Դ������
	 * 
	 * @param type
	 *            ��Ҫ��ȡ���ڿ�Դ���� 1����ʾҽѧ 2����ʾ��ѧ 3����ʾ���� 4����ʾ���� 5����ʾ����
	 * @param num
	 *            ������� ֻ���� 1 �� 2 �� 3
	 * @return �ڿ��б�
	 */
	public ArrayList<SourceMagazine> getSourceMagazinesFromURL(int type, int num) {
		ArrayList<SourceMagazine> sourceMagazines = new ArrayList<SourceMagazine>();
		GetMagazineSource getMagazineSource = new GetMagazineSource();
		switch (type) {
		case 1:
			sourceMagazines = getMagazineSource.getYixue(num);
			break;
		case 2:
			sourceMagazines = getMagazineSource.gethuaxue(num);
			break;
		case 3:
			sourceMagazines = getMagazineSource.getwuli(num);
			break;
		case 4:
			sourceMagazines = getMagazineSource.getshengwu(num);
			break;
		case 5:
			sourceMagazines = getMagazineSource.getqita(num);
			break;
		default:
			break;
		}
		repairSourceMagazineArrayList(sourceMagazines);
		return sourceMagazines;
	}

	/**
	 * show ������ѯ�����綩��Դ�Ķ������
	 * <p>
	 * show <b><i>��ҵ�� �����</i></b>
	 * </p>
	 * 
	 * @param sourceMagazines
	 */
	public void repairSourceMagazineArrayList(ArrayList<SourceMagazine> sourceMagazines) {
		ArrayList<SourceMagazine> theSourceMagazines = databaseOperate.getAllResource(db);
		for (int i = 0; i < theSourceMagazines.size(); i++) {
			for (int j = 0; j < sourceMagazines.size(); j++) {
				if (theSourceMagazines.get(i).getMagazineJID().equals(sourceMagazines.get(j).getMagazineJID())) {
					// �������Դ�����и����綩��Դ����˵�����ڿ��ѱ�����
					sourceMagazines.get(j).setMagazineSub(GlobalFlag.magazine_sub_flag_true);
				}
			}
		}
	}

	/**
	 * show ���������ĸ��������뱾���Ѹ��µ����ڱȽ�
	 * 
	 * @param sevenDay
	 * @return ���ر���δ���µ�����
	 */
	private String[] getTheUpdateDate(String[] sevenDay) {
		String[] theDate = new String[sevenDay.length];
		dbOpen();
		ArrayList<String> allDateArrayList = databaseOperate.getUpdateDate(db);

		int K = 0;
		for (int i = 0; i < sevenDay.length; i++) {
			boolean flag = true;
			if (sevenDay[i] == null) {
				continue;
			}
			for (int j = 0; j < allDateArrayList.size(); j++) {
				if (sevenDay[i].equals(allDateArrayList.get(j).toString())) {
					flag = false;// ��ʾ�������Ѹ��¹�,�����ٸ���
					break;
				}
			}
			if (flag) {
				theDate[K] = sevenDay[i];
				K++;
			}
		}

		dbClose();
		return theDate;
	}

	/**
	 * show �����µ����ڼ���������ڱ���
	 * 
	 * @param sevenDay
	 * @return
	 */
	private boolean addUpdateDate(String[] sevenDay) {
		dbOpen();
		for (int i = 0; i < sevenDay.length; i++) {
			if (sevenDay[i] != null) {
				if (!databaseOperate.addDateIntoUpdate(sevenDay[i], db)) {
					Log.d(TAG_STRING, "addUpdateDate error");
					return false;
				}
			}
		}

		dbClose();
		return true;
	}

	/**
	 * show ɾ�����������
	 * 
	 * @param sevenDay
	 * @return
	 */
	private boolean delUpdateDate(String[] sevenDay) {
		dbOpen();
		ArrayList<String> theDateArrayList = databaseOperate.getUpdateDate(db);
		for (int i = 0; i < theDateArrayList.size(); i++) {
			for (int j = 0; j < sevenDay.length; j++) {
				if (sevenDay[j] == null) {
					continue;
				} else {
					if (sevenDay[j].equals(theDateArrayList.get(i))) {
						break;
					}
				}
				if (j == sevenDay.length - 1) {
					databaseOperate.delDateIntoUpdate(theDateArrayList.get(i), db);
				}
			}
		}
		dbClose();
		return true;
	}

	// �û�����һ���ڿ��Ĳ�������
	/**
	 * show �����ڿ�JID������ڿ��ĸ��µ����´������л�ȡ���������ݿ�
	 * <p>
	 * show <b><i>��ҵ�� �����</i></b>
	 * </p>
	 * 
	 * @param MagazineJID
	 * @return true��ʾִ�гɹ� ����ʧ��
	 */
	public boolean getSubMagazineArticlesIntoDB(String MagazineJID, boolean isUpdate) {

		dbOpen();
		// ��ȡ�����������ڵ�ǰ(����)������
		GetSevenDay getSevenDay = new GetSevenDay();
		String[] sevenDay = null;
		try {
			sevenDay = getSevenDay.getSevenDays();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG_STRING, "getSubMagazineArticlesIntoDB error 0x01");
		}

		if (isUpdate) {// ����Ǹ���,���ж��Ѹ��µ�����
			theNewUpdateFlag = 0;
			theNewUpdate = false;

			if (!delUpdateDate(sevenDay)) {// ɾ�����������
				Log.d(TAG_STRING, "getSubMagazineArticlesIntoDB error 0x02");
			}
			sevenDay = getTheUpdateDate(sevenDay);
			if (!addUpdateDate(sevenDay)) {// ��Ӹ�������
				Log.d(TAG_STRING, "getSubMagazineArticlesIntoDB error 0x03");
			}
			for (int i = 0; i < sevenDay.length; i++) {
				if (sevenDay[i] != null) {
					continue;
				}
				// ��Ϊ����˵���������µĸ���
				if (i == sevenDay.length - 1) {
					theNewUpdate = true;
					theNewUpdateFlag = 1;
				}
			}
			if (!theNewUpdate) {
				theNewUpdateFlag = -1;
				// ɾ�������ĵ��ڿ��б������м�¼
				// ���ղر����ղصĸ���¼��ӵ���Ӧ���ڿ�����
				for (int i = 0; i < subMagazines.size(); i++) {// �������ڿ���,ɾ���ڿ����ڵ����м�¼
					dbOpen();
					if (!databaseOperate.deleteMagazineList(subMagazines.get(i).getMagazineJID(), db)) {
						Log.d(TAG_STRING, "UpdataSubMagazine error 0x04");
						dbClose();
						return false;
					}
				}
				addCollectionIntoMagazine();// �����ղص�����д���ڿ���
			}
		}

		// �ӷ�������ȡ��(7��)�ڸ��ڿ����µĸ�������
		UpdateMagazineArticels updateMagazineArticels = new UpdateMagazineArticels();
		ArrayList<ArticleList> articleLists = updateMagazineArticels.getUpdateInSevenDays(sevenDay, MagazineJID);

		// ����ȡ�������´����Ӧ���ڿ�����
		dbOpen();
		for (int i = 0; i < articleLists.size(); i++) {
			if (!databaseOperate.addOneArticle(articleLists.get(i), MagazineJID, db)) {
				Log.d(TAG_STRING, "getSubMagazineArticlesIntoDB error 0x05");
				dbClose();
				return false;
			}
		}
		dbClose();
		return true;
	}

	/**
	 * show �����ڿ�JID���ղ������б������еĸ��ڿ������µ�����ڿ�����
	 * <p>
	 * show ��Ҫ����ȡ��ĳ������Դ���Ǳ������ղص����º�,�ٴζ��ĸö���Դʱ�轫���ղص�����Ҳ¼���ڸ��ڿ�����<br>
	 * show ��Ҫ���ڸ�������²���
	 * </p>
	 * <p>
	 * show <b><i>��ҵ�� �����</i></b>
	 * </p>
	 * 
	 * @param MagazineJID
	 * @return
	 */
	public boolean getSubMagazineArticleFromCollection(String MagazineJID) {

		dbOpen();
		ArrayList<CollectionArticle> collectionArticles = getCollectionArticles();
		for (int i = 0; i < collectionArticles.size(); i++) {
			if (collectionArticles.get(i).getMagazineJIDString().equals(MagazineJID)) {
				dbOpen();
				ArticleChangeType articleChangeType = new ArticleChangeType();
				// ����ת��
				ArticleList oneArticleList = articleChangeType.changeArticleList(collectionArticles.get(i), GlobalFlag.article_collection_flag_true);
				if (!databaseOperate.addOneArticle(oneArticleList, collectionArticles.get(i).getMagazineJIDString(), db)) {// ����������ӵ��ڿ�����
					Log.d(TAG_STRING, "getSubMagazineArticleFromCollection error");
					dbClose();
					return false;
				}
			}
		}

		dbClose();
		return true;
	}

	/**
	 * show �����ڿ��Ŷ���һ���ڿ�
	 * <p>
	 * show UI�㶩��һ���ڿ�ʱ���ø÷���
	 * </p>
	 * 
	 * @param oneMagazine
	 * @param isExist
	 *            �ö���Դ�Ƿ�����ڶ���Դ��
	 * @return true������ɹ� ����ʧ��
	 */
	public boolean subAMagazine(SourceMagazine oneMagazine, boolean isExist) {
		dbOpen();
		RssSqliteOpenHelper rssSqliteOpenHelper = new RssSqliteOpenHelper();

		// ����û������߶���,�򽫶���Դ��ӵ�����Դ����
		if (!isExist) {
			if (!databaseOperate.insertIntoRssSource(oneMagazine, db)) {
				Log.d(TAG_STRING, "subAMagazine fail 0x01");
				dbClose();
				return false;
			}
		}

		// ������Դ���и��ڿ����ı�ʶ�޸�Ϊ�Ѷ���
		if (!databaseOperate.changeMagazineSourceSubTag(oneMagazine.getMagazineJID(), GlobalFlag.magazine_sub_flag_true, db)) {
			Log.d(TAG_STRING, "subAMagazine fail 0x02");
			dbClose();
			return false;
		}

		// ����һ�����ڿ��������ı�
		if (!rssSqliteOpenHelper.createMagazineTable(oneMagazine.getMagazineJID(), db)) {// ������JID
			Log.d(TAG_STRING, "subAMagazine fail 0x03");
			dbClose();
			return false;
		}

		getSubMagazineArticleFromCollection(oneMagazine.getMagazineJID());// ���ղ����±��и��ڿ��ĵ����µ���
		getSubMagazineArticlesIntoDB(oneMagazine.getMagazineJID(), false);// ��ȡ���7������¸��²��������ݿ�

		dbOpen();
		// ���ı�����Ӹö��ļ�¼
		SubMagazine oneSubMagazine = new SubMagazine();
		oneSubMagazine.setMagazineJID(oneMagazine.getMagazineJID());
		oneSubMagazine.setMagazineName(oneMagazine.getMagazineName());
		oneSubMagazine.setMagazineNum(databaseOperate.MagazineArticleNum(oneMagazine.getMagazineJID(), db));// ��ȡ�ڿ����������µ���Ŀ����ӵ����ı���
		oneSubMagazine.setMagazineCollectionNum(databaseOperate.MagazineCollArticleNum(oneMagazine.getMagazineJID(), db));// ��ȡ�ڿ��ڵ������ղ��˵���Ŀ����ӵ����ı���
		if (!databaseOperate.subMagazine(oneSubMagazine, db)) {
			Log.d(TAG_STRING, "subAMagazine fail 0x04");
			dbClose();
			return false;
		}
		dbClose();
		return true;
	}

	/**
	 * show �����ڿ���ȡ������һ���ڿ�(��ɾ���ղر����ղص�����)
	 * 
	 * @param MagazineJID
	 * @return true��ʾ�����ɹ� ����ʧ��
	 */
	public boolean cancleSubMagazine(String MagazineJID) {
		// ������Դ���и��ڿ����ı�ʶ����Ϊδ����
		// ɾ�����ڿ���
		// �Ӷ��ı���ɾ���ö��ļ�¼
		dbOpen();

		if (!databaseOperate.changeMagazineSourceSubTag(MagazineJID, GlobalFlag.magazine_sub_flag_flase, db)) {
			Log.d(TAG_STRING, "cancleSubMagazine error 0x01");
			dbClose();
			return false;
		}

		if (!databaseOperate.dropMagazineList(MagazineJID, db)) {// ɾ�����ڿ���
			Log.d(TAG_STRING, "cancleSubMagazine error 0x02");
			dbClose();
			return false;
		}

		if (!databaseOperate.deleteSubMagazine(MagazineJID, db)) {// �Ӷ��ı���ɾ���ü�¼
			Log.d(TAG_STRING, "cancleSubMagazine error 0x03");
			dbClose();
			return false;
		}
		dbClose();
		return true;
	}

	/**
	 * show ��������GID�ղ�һƪ����
	 * 
	 * @param MagazineJID
	 *            �ڿ�JID
	 * @param oneArticleList
	 *            һ�������б���
	 * @return true������ʾ�ɹ�,����ʧ��
	 */
	public boolean collectionOneArtcile(String MagazineJID, ArticleList oneArticleList) {
		// ���ڿ����н������µ��ղر�ʶ��Ϊ���ղ�
		// ���ղر�����Ӹ��ղؼ�¼
		dbOpen();
		if (!databaseOperate.UpdateArticle(MagazineJID, oneArticleList.getArticleGID(), GlobalFlag.article_collection_flag_true, db)) {
			Log.d(TAG_STRING, "collectionOneArtcile error 0x01");
			dbClose();
			return false;
		}

		ArticleChangeType articleChangeType = new ArticleChangeType();
		CollectionArticle collectionArticle = articleChangeType.changeCollectionArticle(MagazineJID, oneArticleList);

		if (!databaseOperate.addOneCollArticle(collectionArticle, db)) {
			Log.d(TAG_STRING, "collectionOneArtcile error 0x02");
			dbClose();
			return false;
		}

		dbClose();
		return true;
	}

	/**
	 * show ����GIDȡ���ղ�һƪ����
	 * 
	 * @param MagazineJID
	 *            �ڿ���JID
	 * @param ArticleGID
	 *            ���µ�GID
	 * @return true������ʾ�ɹ�,����ʧ��
	 */
	public boolean cancleCollectionOneArtcile(String MagazineJID, String ArticleGID) {
		// ���ڿ����н������µ��ղر�ʶ��Ϊδ�ղ�
		// ����GIDɾ�����ղر��е�һ����¼

		dbOpen();
		if (!databaseOperate.UpdateArticle(MagazineJID, ArticleGID, GlobalFlag.article_collection_flag_false, db)) {
			Log.d(TAG_STRING, "cancleCollectionOneArtcile error 0x01");
			dbClose();
			return false;
		}

		if (!databaseOperate.deleteCollArticle(ArticleGID, db)) {
			Log.d(TAG_STRING, "cancleCollectionOneArtcile error 0x02");
			dbClose();
			return false;
		}

		dbClose();
		return true;
	}

	/**
	 * show ���¶��ĵ��ڿ�
	 * 
	 * @return true������ʾ�ɹ�,����ʧ��
	 */
	public boolean UpdataSubMagazine() {

		// ���¸��ڿ����б�,����µ�rss����

		dbOpen();

		for (int i = 0; i < subMagazines.size(); i++) {// ���θ���ÿ�����ĵ��ڿ�rss����
			if (!getSubMagazineArticlesIntoDB(subMagazines.get(i).getMagazineJID(), true)) {
				Log.d(TAG_STRING, "UpdataSubMagazine error 0x02");
				dbClose();
				return false;
			}
		}

		dbClose();
		return true;
	}

	/**
	 * show ���ղر��и���¼��ӵ���Ӧ���ڿ�����
	 * 
	 * @return true������ʾ�ɹ�,����ʧ��
	 */
	private boolean addCollectionIntoMagazine() {
		dbOpen();

		collectionArticles = databaseOperate.getAllCollectionArticles(db);// ��ȡ�ղ��б�
		for (int i = 0; i < subMagazines.size(); i++) {
			if (!getSubMagazineArticleFromCollection(subMagazines.get(i).getMagazineJID())) {
				Log.d(TAG_STRING, "addCollectionIntoMagazine error");
			}
		}

		dbClose();
		return true;
	}

	/**
	 * show ÿ���Զ�����
	 * 
	 * @return true������ʾ�ɹ�,����ʧ��
	 */
	public boolean AutoUpdateEveryDay() {
		// ���¶��������ڿ�
		dbOpen();
		for (int i = 0; i < subMagazines.size(); i++) {// ���θ���ÿ�����ĵ��ڿ�rss����
			if (!getSubMagazineArticlesIntoDB(subMagazines.get(i).getMagazineJID(), true)) {
				Log.d(TAG_STRING, "AutoUpdateEveryDay error");
				dbClose();
				return false;
			}
		}
		dbClose();
		return true;
	}

	/**
	 * show ÿ���Զ����
	 * 
	 * @return true������ʾ�ɹ�,����ʧ��
	 */
	public boolean AutoClearEveryWeek() {
		dbOpen();
		for (int i = 0; i < subMagazines.size(); i++) {// �������ڿ���,ɾ���ڿ����ڵ����м�¼
			if (!databaseOperate.deleteMagazineList(subMagazines.get(i).getMagazineJID(), db)) {
				Log.e(TAG_STRING, "UpdataSubMagazine error");
				dbClose();
				return false;
			}
		}

		addCollectionIntoMagazine();// �����ղص�����д���ڿ���
		dbClose();
		return true;
	}

	/**
	 * show �����ݿ��л�ȡ���еĴ��ڶ���Դ��Ķ���Դ
	 * 
	 * @return
	 */
	public ArrayList<SourceMagazine> getSourceMagazines() {

		dbOpen();
		sourceMagazines = databaseOperate.getAllResource(db);
		dbClose();
		return sourceMagazines;
	}

	/**
	 * show �����ݿ��л�ȡ�����Ѷ��ĵ��ڿ�Դ
	 * 
	 * @return
	 */
	public ArrayList<SubMagazine> getSubMagazines() {

		dbOpen();
		subMagazines = databaseOperate.getAllSubMagazines(db);
		// ���ø��ڿ����µ�����Ŀ
		for (int i = 0; i < subMagazines.size(); i++) {
			subMagazines.get(i).setMagazineNum(databaseOperate.MagazineArticleNum(subMagazines.get(i).getMagazineJID(), db));
		}
		// ���ø��ڿ����ղ�������Ŀ��ֵ
		for (int i = 0; i < subMagazines.size(); i++) {
			subMagazines.get(i).setMagazineCollectionNum(databaseOperate.MagazineCollArticleNum(subMagazines.get(i).getMagazineJID(), db));
		}

		dbClose();
		return subMagazines;
	}

	/**
	 * show ��ȡ���ݿ����ղر������
	 * 
	 * @return �ղر��������ղص�����
	 */
	public ArrayList<CollectionArticle> getCollectionArticles() {

		dbOpen();
		collectionArticles = databaseOperate.getAllCollectionArticles(db);
		dbClose();
		return collectionArticles;
	}

	/**
	 * show �����ݿ��л�ȡָ���ڿ��ĵ����������б�
	 * 
	 * @return
	 */
	public ArrayList<ArticleList> getCurrentMagazineLists(String MagazineJID) {

		dbOpen();
		currentMagazineLists = databaseOperate.getMagazineAllArticle(MagazineJID, db);
		dbClose();
		return currentMagazineLists;
	}

	/**
	 * show �����ݿ��л�ȡ��������
	 * 
	 * @return
	 */
	public Setting getTheSetting() {
		dbOpen();
		theSetting = databaseOperate.getSetting(db);
		dbClose();
		return theSetting;
	}

	/**
	 * show �޸���������
	 * 
	 * @param oneSetting
	 *            ������
	 */
	public void setTheSetting(Setting oneSetting) {
		dbOpen();
		this.theSetting = oneSetting;
		if (!databaseOperate.changeSetting(theSetting, db)) {
			Log.e(TAG_STRING, "setTheSetting fail");
		}
		dbClose();
	}

}
