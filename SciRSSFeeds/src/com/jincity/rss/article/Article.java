package com.jincity.rss.article;

public class Article {

	private String articleGID = "";// 文章唯一标识GID
	private String articleName = "";// 文章名称
	private String articleEnName = "";// 文章英文名称
	private String articleLink = "";// 文章原地址
	private String articleDescription = "";// 文章正文
	private String articleEnDescription = "";// 文章英文正文
	private String articlePubDate = "";// 文章发表日期
	private String articleImageURL = "";// 文章图片地址
	private String articleMagazineName = "";// 文章所属期刊名称

	public String getArticleImageURL() {
		return articleImageURL;
	}

	public void setArticleImageURL(String articleImageURL) {
		this.articleImageURL = articleImageURL;
	}

	public String getArticleGID() {
		return articleGID;
	}

	public void setArticleGID(String articleGID) {
		this.articleGID = articleGID;
	}

	public String getArticleName() {
		return articleName;
	}

	public void setArticleName(String articleName) {
		this.articleName = articleName;
	}

	public String getArticleEnName() {
		return articleEnName;
	}

	public void setArticleEnName(String articleEnName) {
		if (articleEnName != null) {
			this.articleEnName = articleEnName;
		}
	}

	public String getArticleLink() {
		return articleLink;
	}

	public void setArticleLink(String articleLink) {
		this.articleLink = articleLink;
	}

	public String getArticleDescription() {
		return articleDescription;
	}

	public void setArticleDescription(String articleDescription) {

		if (articleDescription != null) {
			this.articleDescription = articleDescription;
		}
	}

	public String getArticleEnDescription() {
		return articleEnDescription;
	}

	public void setArticleEnDescription(String articleEnDescription) {
		if (articleEnDescription != null) {
			this.articleEnDescription = articleEnDescription;
		}
	}

	public String getArticlePubDate() {
		return articlePubDate;
	}

	public void setArticlePubDate(String articlePubDate) {
		this.articlePubDate = articlePubDate;
	}

	public String getArticleMagazineName() {
		return articleMagazineName;
	}

	public void setArticleMagazineName(String articleMagazineName) {
		this.articleMagazineName = articleMagazineName;
	}

}
