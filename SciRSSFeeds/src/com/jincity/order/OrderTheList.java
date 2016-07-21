package com.jincity.order;

import java.util.ArrayList;

import com.jincity.rss.article.ArticleList;
import com.jincity.rss.article.CollectionArticle;
import com.jincity.rss.magazine.SourceMagazine;
import com.jincity.rss.magazine.SubMagazine;

public class OrderTheList {

	/**
	 * show 将已订阅的期刊列表按照期刊号排序
	 * 
	 * @param subMagazines
	 * @return
	 */
	public ArrayList<SubMagazine> orderSubMagazines(ArrayList<SubMagazine> subMagazines) {
		SubMagazine tempSubMagazine = new SubMagazine();
		for (int i = 0; i < subMagazines.size(); i++) {
			for (int j = 0; j < subMagazines.size() - 1 - i; j++) {
				if (subMagazines.get(j).getMagazineJID().compareTo(subMagazines.get(j + 1).getMagazineJID()) > 0) {
					tempSubMagazine = subMagazines.get(j);
					subMagazines.set(j, subMagazines.get(j + 1));
					subMagazines.set(j + 1, tempSubMagazine);
				}
			}
		}
		return subMagazines;
	}

	/**
	 * show 将期刊文章列表按照时间由近至远排序
	 * 
	 * @param articleLists
	 * @return
	 */
	public ArrayList<ArticleList> orderArticleLists(ArrayList<ArticleList> articleLists) {
		ArticleList tempArticleList = new ArticleList();
		for (int i = 0; i < articleLists.size(); i++) {
			for (int j = 0; j < articleLists.size() - 1 - i; j++) {
				if (articleLists.get(j).getArticlePubDate().compareTo(articleLists.get(j + 1).getArticlePubDate()) < 0) {
					tempArticleList = articleLists.get(j);
					articleLists.set(j, articleLists.get(j + 1));
					articleLists.set(j + 1, tempArticleList);
				}
			}
		}
		return articleLists;
	}

	/**
	 * show 将收藏文章列表按照时间由近至远排序
	 * 
	 * @param collectionArticles
	 * @return
	 */
	public ArrayList<CollectionArticle> orderCollectionArticles(ArrayList<CollectionArticle> collectionArticles) {

		CollectionArticle tempCollectionArticle = new CollectionArticle();
		for (int i = 0; i < collectionArticles.size(); i++) {
			for (int j = 0; j < collectionArticles.size() - 1 - i; j++) {
				if (collectionArticles.get(j).getArticlePubDate().compareTo(collectionArticles.get(j + 1).getArticlePubDate()) < 0) {
					tempCollectionArticle = collectionArticles.get(j);
					collectionArticles.set(j, collectionArticles.get(j + 1));
					collectionArticles.set(j + 1, tempCollectionArticle);
				}
			}
		}

		return collectionArticles;
	}

	/**
	 * show 将订阅源列表按照期刊JID排序
	 * 
	 * @param sourceMagazines
	 * @return
	 */
	public ArrayList<SourceMagazine> orderSourceMagazines(ArrayList<SourceMagazine> sourceMagazines) {
		SourceMagazine tempSourceMagazine = new SourceMagazine();
		for (int i = 0; i < sourceMagazines.size(); i++) {
			for (int j = 0; j < sourceMagazines.size() - 1 - i; j++) {
				if (sourceMagazines.get(j).getMagazineJID().compareTo(sourceMagazines.get(j + 1).getMagazineJID()) > 0) {
					tempSourceMagazine = sourceMagazines.get(j);
					sourceMagazines.set(j, sourceMagazines.get(j + 1));
					sourceMagazines.set(j + 1, tempSourceMagazine);
				}
			}
		}
		return sourceMagazines;
	}

}
