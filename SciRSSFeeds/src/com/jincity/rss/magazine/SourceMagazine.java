package com.jincity.rss.magazine;

public class SourceMagazine extends Magazine {

	private String magazineAffectoi = "";// �ڿ���Ӱ������
	private int magazineSubNum = 0;// �ڿ����ĵ���Ŀ
	private int magazineSub = 0;// �ڿ��Ƿ񱻶��ı�ʶ

	public String getMagazineAffectoi() {
		return magazineAffectoi;
	}

	public void setMagazineAffectoi(String magazineAffectoi) {
		this.magazineAffectoi = magazineAffectoi;
	}

	public int getMagazineSubNum() {
		return magazineSubNum;
	}

	public void setMagazineSubNum(int magazineSubNum) {
		this.magazineSubNum = magazineSubNum;
	}

	public int getMagazineSub() {
		return magazineSub;
	}

	public void setMagazineSub(int magazineSub) {
		this.magazineSub = magazineSub;
	}

}
