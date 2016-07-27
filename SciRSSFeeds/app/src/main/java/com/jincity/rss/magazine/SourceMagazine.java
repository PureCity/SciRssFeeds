package com.jincity.rss.magazine;

public class SourceMagazine extends Magazine {

	private String magazineAffectoi = "";// 期刊的影响因子
	private int magazineSubNum = 0;// 期刊订阅的数目
	private int magazineSub = 0;// 期刊是否被订阅标识

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
