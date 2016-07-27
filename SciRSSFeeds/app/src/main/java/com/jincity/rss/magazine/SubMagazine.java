package com.jincity.rss.magazine;

public class SubMagazine extends Magazine {

	private int MagazineNum = 0;// 期刊文章总数
	private int MagazineCollectionNum = 0;// 期刊内被收藏的文章总数

	public int getMagazineNum() {
		return MagazineNum;
	}

	public void setMagazineNum(int magazineNum) {
		MagazineNum = magazineNum;
	}

	public int getMagazineCollectionNum() {
		return MagazineCollectionNum;
	}

	public void setMagazineCollectionNum(int magazineCollectionNum) {
		MagazineCollectionNum = magazineCollectionNum;
	}

}
