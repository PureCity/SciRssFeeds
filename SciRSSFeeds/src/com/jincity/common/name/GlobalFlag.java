package com.jincity.common.name;

public class GlobalFlag {

	/** 该文章被收藏 */
	public static final int article_collection_flag_true = 1;
	/** 该文章未被收藏 */
	public static final int article_collection_flag_false = 0;

	/** 期刊被订阅 */
	public static final int magazine_sub_flag_true = 1;
	/** 期刊未被订阅 */
	public static final int magazine_sub_flag_flase = 0;

	/** 期刊被订阅 */
	public static final String MAGAZINE_SUB_TRUE_STRING = "√";
	/** 期刊未被订阅 */
	public static final String MAGAZINE_SUB_FALSE_STRING = "+";

	/** 文章被收藏 */
	public static final String ARTICLE_COL_TRUE_STRING = "★";
	/** 文章未被收藏 */
	public static final String ARTICLE_COL_FALSE_STRING = "☆";

	// 设置的默认值
	/** 使用内置浏览器 */
	public static final int use_own_bro_true = 1;
	/** 不使用内置浏览器 */
	public static final int use_own_bro_false = 0;
	/** 仅在wifi下显示图片 */
	public static final int show_image_in_wifi_true = 1;
	/** 在非wifi下也显示图片 */
	public static final int show_image_in_wifi_false = 0;
	/** 显示页面动画 */
	public static final int show_page_cartoon_true = 1;
	/** 不显示页面动画 */
	public static final int show_page_cartoon_false = 0;
	/** 显示文章翻页效果 */
	public static final int show_article_change_true = 1;
	/** 不显示文章翻页效果 */
	public static final int show_article_change_false = 0;
	/** 显示图片选项 */
	public static final int show_image_choise_true = 1;
	/** 不显示图片选项 */
	public static final int show_image_choise_false = 0;
	/** 使用大字体 */
	public static final int use_big_word_true = 1;
	/** 使用普通字体 */
	public static final int use_big_word_false = 0;
	/** 自动更新(每天) */
	public static final int update_auto_day_true = 1;
	/** 不自动更新 */
	public static final int update_auto_day_false = 0;
	/** 自动清除(每周) */
	public static final int clear_auto_week_true = 1;
	/** 不自动清除 */
	public static final int clear_auto_week_false = 0;
	/** 设置值为真 */
	public static final int setting_true = 1;
	/** 设置值为假 */
	public static final int setting_false = 0;

	// 页面标识
	/** 主页 */
	public static final int MAIN_PAGE = 999;
	/** 收藏页 */
	public static final int COL_PAGE = 2;
	/** 文章列表页 */
	public static final int MAGAZINE_LIST_PAGE = 4;
	/** 设置界面 */
	public static final int SETTING_PAGE = 5;

	// 订阅期刊界面的页面标识
	/** 已有订阅源界面 */
	public static final int SOURCE_PAGE = 1;
	/** 定制订阅源界面的医学界面1 */
	public static final int WEB_PAGE_YIXUE_1 = 2;
	/** 定制订阅源界面的医学界面2 */
	public static final int WEB_PAGE_YIXUE_2 = 3;
	/** 定制订阅源界面的医学界面3 */
	public static final int WEB_PAGE_YIXUE_3 = 4;
	/** 定制订阅源界面的化学界面1 */
	public static final int WEB_PAGE_HUAXUE_1 = 5;
	/** 定制订阅源界面的化学界面2 */
	public static final int WEB_PAGE_HUAXUE_2 = 6;
	/** 定制订阅源界面的物理界面1 */
	public static final int WEB_PAGE_WULI_1 = 8;
	/** 定制订阅源界面的物理界面2 */
	public static final int WEB_PAGE_WULI_2 = 9;
	/** 定制订阅源界面的生物界面1 */
	public static final int WEB_PAGE_SHENGWU_1 = 11;
	/** 定制订阅源界面的生物界面2 */
	public static final int WEB_PAGE_SHENGWU_2 = 12;
	/** 定制订阅源界面的生物界面3 */
	public static final int WEB_PAGE_SHENGWU_3 = 13;
	/** 定制订阅源界面的其它界面1 */
	public static final int WEB_PAGE_QITA_1 = 14;
	/** 定制订阅源界面的其它界面2 */
	public static final int WEB_PAGE_QITA_2 = 15;
}
