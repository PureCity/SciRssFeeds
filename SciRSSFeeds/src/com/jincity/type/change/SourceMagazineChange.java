package com.jincity.type.change;

import java.util.ArrayList;

import com.jincity.common.name.GlobalFlag;
import com.jincity.rss.magazine.SourceMagazine;

public class SourceMagazineChange {

	/**
	 * show ���ݴ������ȡ���ı����ݽ���ת������Ҫ���ڿ��б�
	 * <p>
	 * show
	 * </p>
	 * 
	 * @param theMagazines
	 *            �ı�����
	 * @return ����Դ�ڿ��б�
	 */
	public ArrayList<SourceMagazine> getSourceMagazines(String theMagazines) {
		ArrayList<SourceMagazine> sourceMagazines = new ArrayList<SourceMagazine>();

		String[] magazines = theMagazines.split("\n");// ÿ�д���һ���ڿ�
		for (int i = 0; i < magazines.length; i++) {
			String[] magazine = magazines[i].split("\t");// ÿ��tab������һ����������
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
