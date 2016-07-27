package com.jincity.type.change;

import java.util.ArrayList;

import com.jincity.rss.article.ArticleList;

public class ArticlesChange {

	/**
	 * show 根据网络文件的内容转型为articlelist类
	 * 
	 * @param articlesContext
	 * @return 该文件包含的文章列表
	 */
	public ArrayList<ArticleList> changeContextIntoArticlesLists(String articlesContext) {
		ArrayList<ArticleList> articleLists = new ArrayList<ArticleList>();

		if (articlesContext == null) {
			articleLists = null;
			return articleLists;
		}

		String[] articles = articlesContext.split("\n");
		for (int i = 0; i < articles.length; i++) {
			String[] allArticle = articles[i].split("\t");

			if (allArticle.length < 9) {
				articleLists = null;
				return articleLists;
			}
			ArticleList articleList = new ArticleList();
			articleList.setArticleGID(allArticle[0]);// GID
			articleList.setArticleName(allArticle[1]);
			articleList.setArticleEnName(allArticle[2]);
			articleList.setArticleDescription(allArticle[3]);
			articleList.setArticleEnDescription(allArticle[4]);
			articleList.setArticleImageURL(allArticle[5]);
			articleList.setArticleLink(allArticle[6]);
			articleList.setArticleMagazineName(allArticle[7]);
			articleList.setArticlePubDate(allArticle[8]);

			articleLists.add(articleList);
		}

		return articleLists;
	}

}
