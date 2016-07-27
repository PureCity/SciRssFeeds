package com.jincity.common.name;

import android.annotation.SuppressLint;

@SuppressLint("SdCardPath")
public class DatabaseNameCom {

	/** 本地数据库名称 */
	public static final String RSS_DATABASENAME = "sci.db";
	/** 默认的本地数据库表 */
	public static final String SCI_LOCAL_DB_TABLE_NAME = "neizhi";
	/** 订阅源表名称 */
	public static final String ALL_RSS_SOURCE_MAG = "rssSource";
	/** 订阅的期刊表表名称 */
	public static final String SUB_RSS_MAG = "subRssMag";
	/** 收藏文章的表名称 */
	public static final String COLL_ARTICLE = "collectionArticle";
	/** 设置表 */
	public static final String SETTING_TOOL_STRING = "setting";
	/** 已更新的日期表*/
	public static final String UPDATE_DAY_STRING = "updateday";
	/** 数据库文件的路径 */
	public static final String DATABASE_PTAH = "/data/data/com.jincity.scirssfeeds/databases/";
	
	//已更新的表中各列名称
	/** 已更新的日期*/
	public static final String THE_UPDATE_DAY_STRING = "theday";

	// 订阅源表中各列名称
	/** 期刊的JID */
	public static final String RSS_RESOURCE_JID_STRING = "MagazineJID";
	/** 期刊名称 */
	public static final String RSS_RESOURCE_NAME_STRING = "MagazineName";
	/** 期刊的影响因子 */
	public static final String RSS_RESOURCE_AFFECTOI_STRING = "MagazineAffectoi";
	/** 期刊的订阅数目 */
	public static final String RSS_RESOURCE_SUBNUM_STRING = "MagazineSubNum";
	/** 期刊是否被订阅标识 */
	public static final String RSS_RESOURCE_SUB_STRING = "MagazineSub";

	// 已订阅的期刊表中各列名称
	/** 期刊的JID */
	public static final String SUB_MAG_JID_STRING = "MagazineJID";
	/** 期刊的名称 */
	public static final String SUB_MAG_NAME_STRING = "MagazineName";
	/** 期刊内文章的总数 */
	public static final String SUB_MAG_NUM_STRING = "MagazineNum";
	/** 收藏了的文章数目 */
	public static final String SUB_MAG_COLL_NUM_STRING = "MagazineCollNum";

	// 收藏文章的表中各列的名称
	/** 文章的gid */
	public static final String COL_ARTICLE_GID_STRING = "colArticlegid";
	/** 文章名称 */
	public static final String COL_ARTICLE_NAME_STRING = "colArticleName";
	/** 文章英文名称 */
	public static final String COL_ARTICLE_EN_NAME_STRING = "colArticleEnName";
	/** 文章的地址 */
	public static final String COL_ARTICLE_LINK_STRING = "colArticleLink";
	/** 文章的描述 */
	public static final String COL_ARTICLE_DESCRIPTION_STRING = "colArticleDesc";
	/** 文章的英文描述 */
	public static final String COL_ARTICLE_EN_DESCRIPTION_STRING = "colArticleEnDesc";
	/** 文章发表日期 */
	public static final String COL_PUBDATE_STRING = "colPubdate";
	/** 文章图片地址 */
	public static final String COL_ARTICLE_IMAGE_URL_STRING = "colImageURL";
	/** 该文章所属的期刊 */
	public static final String COL_ARTICLE_MAG_STRING = "colArtMag";
	/** 该期刊的JID */
	public static final String COL_ART_MAG_JID_STRING = "colArtMagJID";

	// 每个期刊下的文章表中各列的名称
	/** 文章GID */
	public static final String ARTICLE_GID_STRING = "articleGID";
	/** 文章名称 */
	public static final String ARTICLE_NAME_STRING = "articleName";
	/** 英文名 */
	public static final String ARTICLE_EN_NANE_STRING = "articleEnName";
	/** 文章的地址 */
	public static final String ARTICLE_LINK_STRING = "articleLink";
	/** 文章描述 */
	public static final String ARTICLE_DESCRIPTION_STRING = "articleDescription";
	/** 文章的英文描述 */
	public static final String ARTICLE_EN_EDSCRIPTION_STRING = "articleEnDescription";
	/** 文章发表日期 */
	public static final String ARTICLE_PUBDATE_STRING = "articlePubDate";
	/** 文章所属的期刊名称 */
	public static final String ARTICLE_MAGAZINE_NAME_STRING = "articleMagazineName";
	/** 文章的图片地址 */
	public static final String ARTICLE_IMAGE_URL_STRING = "articleImageURL";
	/** 文章是否被收藏 */
	public static final String ARTICLE_COLLECTION_STRING = "articleCol";

	// 软件设置
	/** 设置类型 */
	public static final String TYPE_STRING = "settingType";
	/** 该类型的值 */
	public static final String TYPE_VALUE_STRING = "typevalue";

	/** 使用内置浏览器 */
	public static final String SETTING_USE_OWN_BROWSER_STRING = "UseOwnBro";
	/** 仅在wifi下显示图片 */
	public static final String SETTING_SHOW_ONLY_WIFI_STRING = "ShowInWifi";
	/** 显示页面动画 */
	public static final String SETTING_SHOW_PAGE_CARTOON_STRING = "ShowPageCartoon";
	/** 显示翻页动画 */
	public static final String SETTING_SHOW_CHANGE_CARTOON_STRING = "ShowChangeCartoon";
	/** 显示图片选项 */
	public static final String SETTING_SHOW_IMAGE_CHOISE_STRING = "ShowImageChoise";
	/** 大字体 */
	public static final String SETTING_USE_BIG_WORD_STRING = "UseBigWord";
	/** 自动更新（每天） */
	public static final String SETTING_UPDATE_AUTO_EVERYDAY_STRING = "UpdateAutoEveryDay";
	/** 自动清除（每周） */
	public static final String SETTING_CLEAR_EVERYWEEK_STRING = "ClearAutoEveryWeek";

}
