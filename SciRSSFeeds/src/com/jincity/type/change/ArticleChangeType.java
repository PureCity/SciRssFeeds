package com.jincity.type.change;

import com.jincity.rss.article.Article;
import com.jincity.rss.article.ArticleList;
import com.jincity.rss.article.CollectionArticle;

public class ArticleChangeType {

	/**
	 * show ��ArticleList����ת��ΪCollectionArticle����
	 * 
	 * @param MagazineJID
	 * @param oneArticleList
	 * @return CollectionAritcle����
	 */
	public CollectionArticle changeCollectionArticle(String MagazineJID, ArticleList oneArticleList) {
		CollectionArticle collectionArticle = new CollectionArticle();

		collectionArticle.setArticleGID(oneArticleList.getArticleGID());
		collectionArticle.setArticleName(oneArticleList.getArticleName());
		collectionArticle.setArticleEnName(oneArticleList.getArticleEnName());
		collectionArticle.setArticleDescription(oneArticleList.getArticleDescription());
		collectionArticle.setArticleEnDescription(oneArticleList.getArticleEnDescription());
		collectionArticle.setArticleImageURL(oneArticleList.getArticleImageURL());
		collectionArticle.setArticleLink(oneArticleList.getArticleLink());
		collectionArticle.setArticleMagazineName(oneArticleList.getArticleMagazineName());
		collectionArticle.setArticlePubDate(oneArticleList.getArticlePubDate());
		collectionArticle.setMagazineJIDString(MagazineJID);

		return collectionArticle;
	}

	/**
	 * show ��collectionArticle����ת��Ϊ ArticleList����
	 * 
	 * @param collectionArticle
	 * @param FLAG
	 *            �Ƿ��ղصı�־
	 * @return ArticleList����
	 */
	public ArticleList changeArticleList(CollectionArticle collectionArticle, int FLAG) {
		ArticleList oneArticleList = new ArticleList();

		oneArticleList.setArticleGID(collectionArticle.getArticleGID());
		oneArticleList.setArticleName(collectionArticle.getArticleName());
		oneArticleList.setArticleEnName(collectionArticle.getArticleEnName());
		oneArticleList.setArticleDescription(collectionArticle.getArticleDescription());
		oneArticleList.setArticleEnDescription(collectionArticle.getArticleEnDescription());
		oneArticleList.setArticleImageURL(collectionArticle.getArticleImageURL());
		oneArticleList.setArticleLink(collectionArticle.getArticleLink());
		oneArticleList.setArticlePubDate(collectionArticle.getArticlePubDate());
		oneArticleList.setArticleMagazineName(collectionArticle.getArticleMagazineName());
		oneArticleList.setArticleCollection(FLAG);

		return oneArticleList;
	}

	/**
	 * show ��Article����ת��ΪArticleList����
	 * 
	 * @param article
	 * @param FLAG
	 *            �Ƿ��ղصı�־
	 * @return ArticleList����
	 */
	public ArticleList changeArticleList(Article article, int FLAG) {
		ArticleList oneArticleList = new ArticleList();
		
		oneArticleList.setArticleGID(article.getArticleGID());
		oneArticleList.setArticleName(article.getArticleName());
		oneArticleList.setArticleEnName(article.getArticleEnName());
		oneArticleList.setArticleDescription(article.getArticleDescription());
		oneArticleList.setArticleEnDescription(article.getArticleEnDescription());
		oneArticleList.setArticleImageURL(article.getArticleImageURL());
		oneArticleList.setArticleLink(article.getArticleLink());
		oneArticleList.setArticleMagazineName(article.getArticleMagazineName());
		oneArticleList.setArticlePubDate(article.getArticlePubDate());
		oneArticleList.setArticleCollection(FLAG);
		
		return oneArticleList;
	}
	
	/**
	 * show ��ArticleList����ת����Article����
	 * @param articleList
	 * @return Article����
	 */
	public Article changeArticle(ArticleList articleList){
		Article article = new Article();
		
		article.setArticleGID(articleList.getArticleGID());
		article.setArticleName(articleList.getArticleName());
		article.setArticleEnName(articleList.getArticleEnName());
		article.setArticleDescription(articleList.getArticleDescription());
		article.setArticleEnDescription(articleList.getArticleEnDescription());
		article.setArticleImageURL(articleList.getArticleImageURL());
		article.setArticleLink(articleList.getArticleLink());
		article.setArticleMagazineName(articleList.getArticleMagazineName());
		article.setArticlePubDate(articleList.getArticlePubDate());
		
		return article;
	}

}
