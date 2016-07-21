package com.jincity.rss.article;

public class Article {

	private String articleGID = "";// ����Ψһ��ʶGID
	private String articleName = "";// ��������
	private String articleEnName = "";// ����Ӣ������
	private String articleLink = "";// ����ԭ��ַ
	private String articleDescription = "";// ��������
	private String articleEnDescription = "";// ����Ӣ������
	private String articlePubDate = "";// ���·�������
	private String articleImageURL = "";// ����ͼƬ��ַ
	private String articleMagazineName = "";// ���������ڿ�����

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
