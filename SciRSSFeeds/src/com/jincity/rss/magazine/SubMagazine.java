package com.jincity.rss.magazine;

public class SubMagazine extends Magazine {

	private int MagazineNum = 0;// �ڿ���������
	private int MagazineCollectionNum = 0;// �ڿ��ڱ��ղص���������

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
