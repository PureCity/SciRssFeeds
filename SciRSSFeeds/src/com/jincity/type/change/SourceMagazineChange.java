package com.jincity.type.change;

import java.util.ArrayList;

import com.jincity.common.name.GlobalFlag;
import com.jincity.rss.magazine.SourceMagazine;

public class SourceMagazineChange {

	/**
	 * show 根据从网络获取的文本内容将其转换成需要的期刊列表
	 * <p>
	 * show
	 * </p>
	 * 
	 * @param theMagazines
	 *            文本内容
	 * @return 订阅源期刊列表
	 */
	public ArrayList<SourceMagazine> getSourceMagazines(String theMagazines) {
		ArrayList<SourceMagazine> sourceMagazines = new ArrayList<SourceMagazine>();

		String[] magazines = theMagazines.split("\n");// 每行代表一个期刊
		for (int i = 0; i < magazines.length; i++) {
			String[] magazine = magazines[i].split("\t");// 每个tab键代表一个数据内容
			if (magazine.length < 3) {
				continue;
			}
			SourceMagazine aSourceMagazine = new SourceMagazine();
			aSourceMagazine.setMagazineJID(magazine[0]);
			aSourceMagazine.setMagazineName(magazine[1]);
			aSourceMagazine.setMagazineAffectoi(magazine[2]);
			if (magazine.length >= 4) {
				aSourceMagazine.setMagazineSubNum(Integer.parseInt(magazine[3]));
			} else {
				aSourceMagazine.setMagazineSubNum(0);
			}
			aSourceMagazine.setMagazineSub(GlobalFlag.magazine_sub_flag_flase);
			sourceMagazines.add(aSourceMagazine);
		}
		return sourceMagazines;
	}

}
