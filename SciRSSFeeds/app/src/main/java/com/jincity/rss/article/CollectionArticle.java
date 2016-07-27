package com.jincity.rss.article;

public class CollectionArticle extends Article {

	private String magazineJID = "";// 该被收藏文章所属期刊的JID

	public String getMagazineJIDString() {
		return magazineJID;
	}

	public void setMagazineJIDString(String magazineJIDString) {
		this.magazineJID = magazineJIDString;
	}

}
