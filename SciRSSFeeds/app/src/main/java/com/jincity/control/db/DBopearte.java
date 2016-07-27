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

	// 默认从数据库中读取以下类型数据
	private ArrayList<SourceMagazine> sourceMagazines;// 订阅源列表
	private ArrayList<SubMagazine> subMagazines;// 已订阅的期刊列表
	private ArrayList<CollectionArticle> collectionArticles;// 收藏的文章列表
	private ArrayList<ArticleList> currentMagazineLists;// 当前选中期刊的文章列表
	private Setting theSetting;// 软件的基本设置

	boolean theNewUpdate = false;// 是否已经是最新的更新
	public int theNewUpdateFlag = 0;// 最新更新的标志 0 是默认 1表示是最新更新 -1表示不是最新更新 -2表意外中断

	private SQLiteDatabase db;// 本地数据库
	private Context context;
	DatabaseOperate databaseOperate;

	/**
	 * show 获取当前是否是最新更新的标志
	 * 
	 * @return
	 */
	public int getTheUPdateFlag() {
		return theNewUpdateFlag;
	}

	/**
	 * show 初始化业务逻辑的数据库内容
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
	 * show 初始化业务逻辑的数据库内容
	 * 
	 * @param theContext
	 * @param firstUse
	 *            数据库文件是否存在的判断
	 */
	public DBopearte(Context theContext, boolean firstUse) {// 初始化本地数据库
		context = theContext;
		databaseOperate = new DatabaseOperate();

		// 判断基本数据库是否存在
		if (firstUse) {
			setBasicDB();// 初始化基本数据库
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

	// 基本操作
	/**
	 * show 打开或创建数据库操作
	 * <p>
	 * show <b><i>仅业务 层调用</i></b>
	 * </p>
	 */
	public void dbOpen() {
		db = context.openOrCreateDatabase(DatabaseNameCom.RSS_DATABASENAME, Context.MODE_PRIVATE, null);
	}

	/**
	 * show 关闭数据库
	 * <p>
	 * show <b><i>仅业务 层调用</i></b>
	 * </p>
	 */
	public void dbClose() {
		db.close();
	}

	/**
	 * show 初始化数据库资源
	 * <p>
	 * show <b><i>仅业务 层调用</i></b>
	 * </p>
	 * 
	 * @return true表示成功 false表示失败
	 */
	public boolean setBasicDB() {
		DatabaseFirstOperate databaseFirstOperate = new DatabaseFirstOperate(context);
		return databaseFirstOperate.copyMyDB();
	}

	// 从服务器获取订阅源的操作
	/**
	 * show 从服务器获取期刊源的内容
	 * 
	 * @param type
	 *            需要获取的期刊源类型 1：表示医学 2：表示化学 3：表示物理 4：表示生物 5：表示其它
	 * @param num
	 *            代表界面 只能是 1 或 2 或 3
	 * @return 期刊列表
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
	 * show 修正查询的网络订阅源的订阅情况
	 * <p>
	 * show <b><i>仅业务 层调用</i></b>
	 * </p>
	 * 
	 * @param sourceMagazines
	 */
	public void repairSourceMagazineArrayList(ArrayList<SourceMagazine> sourceMagazines) {
		ArrayList<SourceMagazine> theSourceMagazines = databaseOperate.getAllResource(db);
		for (int i = 0; i < theSourceMagazines.size(); i++) {
			for (int j = 0; j < sourceMagazines.size(); j++) {
				if (theSourceMagazines.get(i).getMagazineJID().equals(sourceMagazines.get(j).getMagazineJID())) {
					// 如果订阅源表中有该网络订阅源，则说明该期刊已被订阅
					sourceMagazines.get(j).setMagazineSub(GlobalFlag.magazine_sub_flag_true);
				}
			}
		}
	}

	/**
	 * show 将服务器的更新日期与本地已更新的日期比较
	 * 
	 * @param sevenDay
	 * @return 返回本地未更新的日期
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
					flag = false;// 表示该日期已更新过,无须再更新
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
	 * show 将更新的日期加入更新日期表中
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
	 * show 删除冗余的日期
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

	// 用户订阅一个期刊的操作方法
	/**
	 * show 根据期刊JID将最该期刊的更新的文章从网络中获取并存入数据库
	 * <p>
	 * show <b><i>仅业务 层调用</i></b>
	 * </p>
	 * 
	 * @param MagazineJID
	 * @return true表示执行成功 否则失败
	 */
	public boolean getSubMagazineArticlesIntoDB(String MagazineJID, boolean isUpdate) {

		dbOpen();
		// 获取包括当天在内的前(七天)的日期
		GetSevenDay getSevenDay = new GetSevenDay();
		String[] sevenDay = null;
		try {
			sevenDay = getSevenDay.getSevenDays();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG_STRING, "getSubMagazineArticlesIntoDB error 0x01");
		}

		if (isUpdate) {// 如果是更新,则判断已更新的日期
			theNewUpdateFlag = 0;
			theNewUpdate = false;

			if (!delUpdateDate(sevenDay)) {// 删除冗余的日期
				Log.d(TAG_STRING, "getSubMagazineArticlesIntoDB error 0x02");
			}
			sevenDay = getTheUpdateDate(sevenDay);
			if (!addUpdateDate(sevenDay)) {// 添加更新日期
				Log.d(TAG_STRING, "getSubMagazineArticlesIntoDB error 0x03");
			}
			for (int i = 0; i < sevenDay.length; i++) {
				if (sevenDay[i] != null) {
					continue;
				}
				// 都为空则说明已是最新的更新
				if (i == sevenDay.length - 1) {
					theNewUpdate = true;
					theNewUpdateFlag = 1;
				}
			}
			if (!theNewUpdate) {
				theNewUpdateFlag = -1;
				// 删除各订阅的期刊列表中所有记录
				// 将收藏表中收藏的各记录添加到对应的期刊表中
				for (int i = 0; i < subMagazines.size(); i++) {// 遍历各期刊表,删除期刊表内的所有记录
					dbOpen();
					if (!databaseOperate.deleteMagazineList(subMagazines.get(i).getMagazineJID(), db)) {
						Log.d(TAG_STRING, "UpdataSubMagazine error 0x04");
						dbClose();
						return false;
					}
				}
				addCollectionIntoMagazine();// 将已收藏的文章写回期刊表
			}
		}

		// 从服务器获取这(7天)内该期刊文章的更新内容
		UpdateMagazineArticels updateMagazineArticels = new UpdateMagazineArticels();
		ArrayList<ArticleList> articleLists = updateMagazineArticels.getUpdateInSevenDays(sevenDay, MagazineJID);

		// 将获取到的文章存入对应的期刊表中
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
	 * show 根据期刊JID将收藏文章列表中已有的该期刊的文章导入该期刊表中
	 * <p>
	 * show 主要用于取消某条订阅源但是保留了收藏的文章后,再次订阅该订阅源时需将已收藏的文章也录入在该期刊表中<br>
	 * show 次要用于辅助表更新操作
	 * </p>
	 * <p>
	 * show <b><i>仅业务 层调用</i></b>
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
				// 类型转换
				ArticleList oneArticleList = articleChangeType.changeArticleList(collectionArticles.get(i), GlobalFlag.article_collection_flag_true);
				if (!databaseOperate.addOneArticle(oneArticleList, collectionArticles.get(i).getMagazineJIDString(), db)) {// 将该文章添加到期刊表中
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
	 * show 根据期刊号订阅一个期刊
	 * <p>
	 * show UI层订阅一个期刊时调用该方法
	 * </p>
	 * 
	 * @param oneMagazine
	 * @param isExist
	 *            该订阅源是否存在于订阅源表
	 * @return true表操作成功 否则失败
	 */
	public boolean subAMagazine(SourceMagazine oneMagazine, boolean isExist) {
		dbOpen();
		RssSqliteOpenHelper rssSqliteOpenHelper = new RssSqliteOpenHelper();

		// 如果用户是在线订阅,则将订阅源添加到订阅源表中
		if (!isExist) {
			if (!databaseOperate.insertIntoRssSource(oneMagazine, db)) {
				Log.d(TAG_STRING, "subAMagazine fail 0x01");
				dbClose();
				return false;
			}
		}

		// 将订阅源表中该期刊订阅标识修改为已订阅
		if (!databaseOperate.changeMagazineSourceSubTag(oneMagazine.getMagazineJID(), GlobalFlag.magazine_sub_flag_true, db)) {
			Log.d(TAG_STRING, "subAMagazine fail 0x02");
			dbClose();
			return false;
		}

		// 创建一个以期刊号命名的表
		if (!rssSqliteOpenHelper.createMagazineTable(oneMagazine.getMagazineJID(), db)) {// 创建表JID
			Log.d(TAG_STRING, "subAMagazine fail 0x03");
			dbClose();
			return false;
		}

		getSubMagazineArticleFromCollection(oneMagazine.getMagazineJID());// 将收藏文章表中该期刊的的文章导入
		getSubMagazineArticlesIntoDB(oneMagazine.getMagazineJID(), false);// 读取最近7天的文章更新并存入数据库

		dbOpen();
		// 向订阅表中添加该订阅记录
		SubMagazine oneSubMagazine = new SubMagazine();
		oneSubMagazine.setMagazineJID(oneMagazine.getMagazineJID());
		oneSubMagazine.setMagazineName(oneMagazine.getMagazineName());
		oneSubMagazine.setMagazineNum(databaseOperate.MagazineArticleNum(oneMagazine.getMagazineJID(), db));// 获取期刊内所有文章的数目并添加到订阅表中
		oneSubMagazine.setMagazineCollectionNum(databaseOperate.MagazineCollArticleNum(oneMagazine.getMagazineJID(), db));// 获取期刊内的所有收藏了的数目并添加到订阅表中
		if (!databaseOperate.subMagazine(oneSubMagazine, db)) {
			Log.d(TAG_STRING, "subAMagazine fail 0x04");
			dbClose();
			return false;
		}
		dbClose();
		return true;
	}

	/**
	 * show 根据期刊号取消订阅一个期刊(不删除收藏表中收藏的文章)
	 * 
	 * @param MagazineJID
	 * @return true表示操作成功 否则失败
	 */
	public boolean cancleSubMagazine(String MagazineJID) {
		// 将订阅源表中该期刊订阅标识设置为未订阅
		// 删除该期刊表
		// 从订阅表中删除该订阅记录
		dbOpen();

		if (!databaseOperate.changeMagazineSourceSubTag(MagazineJID, GlobalFlag.magazine_sub_flag_flase, db)) {
			Log.d(TAG_STRING, "cancleSubMagazine error 0x01");
			dbClose();
			return false;
		}

		if (!databaseOperate.dropMagazineList(MagazineJID, db)) {// 删除该期刊表
			Log.d(TAG_STRING, "cancleSubMagazine error 0x02");
			dbClose();
			return false;
		}

		if (!databaseOperate.deleteSubMagazine(MagazineJID, db)) {// 从订阅表中删除该记录
			Log.d(TAG_STRING, "cancleSubMagazine error 0x03");
			dbClose();
			return false;
		}
		dbClose();
		return true;
	}

	/**
	 * show 根据文章GID收藏一篇文章
	 * 
	 * @param MagazineJID
	 *            期刊JID
	 * @param oneArticleList
	 *            一个文章列表类
	 * @return true操作表示成功,否则失败
	 */
	public boolean collectionOneArtcile(String MagazineJID, ArticleList oneArticleList) {
		// 在期刊表中将该文章的收藏标识改为已收藏
		// 在收藏表中添加该收藏记录
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
	 * show 根据GID取消收藏一篇文章
	 * 
	 * @param MagazineJID
	 *            期刊的JID
	 * @param ArticleGID
	 *            文章的GID
	 * @return true操作表示成功,否则失败
	 */
	public boolean cancleCollectionOneArtcile(String MagazineJID, String ArticleGID) {
		// 在期刊表中将该文章的收藏标识改为未收藏
		// 根据GID删除在收藏表中的一条记录

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
	 * show 更新订阅的期刊
	 * 
	 * @return true操作表示成功,否则失败
	 */
	public boolean UpdataSubMagazine() {

		// 更新各期刊的列表,添加新的rss文章

		dbOpen();

		for (int i = 0; i < subMagazines.size(); i++) {// 依次更新每个订阅的期刊rss文章
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
	 * show 将收藏表中各记录添加到对应的期刊表中
	 * 
	 * @return true操作表示成功,否则失败
	 */
	private boolean addCollectionIntoMagazine() {
		dbOpen();

		collectionArticles = databaseOperate.getAllCollectionArticles(db);// 获取收藏列表
		for (int i = 0; i < subMagazines.size(); i++) {
			if (!getSubMagazineArticleFromCollection(subMagazines.get(i).getMagazineJID())) {
				Log.d(TAG_STRING, "addCollectionIntoMagazine error");
			}
		}

		dbClose();
		return true;
	}

	/**
	 * show 每日自动更新
	 * 
	 * @return true操作表示成功,否则失败
	 */
	public boolean AutoUpdateEveryDay() {
		// 重新订阅所有期刊
		dbOpen();
		for (int i = 0; i < subMagazines.size(); i++) {// 依次更新每个订阅的期刊rss文章
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
	 * show 每周自动清除
	 * 
	 * @return true操作表示成功,否则失败
	 */
	public boolean AutoClearEveryWeek() {
		dbOpen();
		for (int i = 0; i < subMagazines.size(); i++) {// 遍历各期刊表,删除期刊表内的所有记录
			if (!databaseOperate.deleteMagazineList(subMagazines.get(i).getMagazineJID(), db)) {
				Log.e(TAG_STRING, "UpdataSubMagazine error");
				dbClose();
				return false;
			}
		}

		addCollectionIntoMagazine();// 将已收藏的文章写回期刊表
		dbClose();
		return true;
	}

	/**
	 * show 从数据库中获取所有的存在订阅源表的订阅源
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
	 * show 从数据库中获取所有已订阅的期刊源
	 * 
	 * @return
	 */
	public ArrayList<SubMagazine> getSubMagazines() {

		dbOpen();
		subMagazines = databaseOperate.getAllSubMagazines(db);
		// 设置各期刊文章的总数目
		for (int i = 0; i < subMagazines.size(); i++) {
			subMagazines.get(i).setMagazineNum(databaseOperate.MagazineArticleNum(subMagazines.get(i).getMagazineJID(), db));
		}
		// 设置各期刊已收藏文章数目的值
		for (int i = 0; i < subMagazines.size(); i++) {
			subMagazines.get(i).setMagazineCollectionNum(databaseOperate.MagazineCollArticleNum(subMagazines.get(i).getMagazineJID(), db));
		}

		dbClose();
		return subMagazines;
	}

	/**
	 * show 获取数据库中收藏表的内容
	 * 
	 * @return 收藏表中所有收藏的文章
	 */
	public ArrayList<CollectionArticle> getCollectionArticles() {

		dbOpen();
		collectionArticles = databaseOperate.getAllCollectionArticles(db);
		dbClose();
		return collectionArticles;
	}

	/**
	 * show 从数据库中获取指定期刊的的所有文章列表
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
	 * show 从数据库中获取设置内容
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
	 * show 修改设置内容
	 * 
	 * @param oneSetting
	 *            设置类
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
