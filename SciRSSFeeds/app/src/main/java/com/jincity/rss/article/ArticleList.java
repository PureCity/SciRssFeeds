package com.jincity.rss.article;

import com.jincity.common.name.GlobalFlag;

public class ArticleList extends Article {

	private int articleCollection = GlobalFlag.article_collection_flag_false;// 默认文章没有被收藏

	public int getArticleCollection() {
		return articleCollection;
	}

	public void setArticleCollection(int articleCollection) {
		this.articleCollection = articleCollection;
	}

}
