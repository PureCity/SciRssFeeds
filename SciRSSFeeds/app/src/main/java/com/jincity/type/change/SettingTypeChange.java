package com.jincity.type.change;

import java.util.ArrayList;
import com.jincity.common.name.NameStatic;
import com.jincity.rss.setting.Setting;
import com.jincity.rss.setting.SettingList;

public class SettingTypeChange {
	// 该类构造两个将Setting同SettingList相互转换的方法
	// 将数据顺序一一对应

	public ArrayList<SettingList> getSettingLists(Setting theSetting) {
		ArrayList<SettingList> theSettingLists = new ArrayList<SettingList>();

		SettingList settingList_useBro = new SettingList();
		settingList_useBro.setSettingName(NameStatic.SET_USE_OWN_BRO_STRING);
		settingList_useBro.setSettingValue(theSetting.getUseOwnBro());

		SettingList settingList_showImageWifi = new SettingList();
		settingList_showImageWifi.setSettingName(NameStatic.SET_IMAGE_WIFI_STRING);
		settingList_showImageWifi.setSettingValue(theSetting.getShowImageInWifi());

		SettingList settingList_showPageCar = new SettingList();
		settingList_showPageCar.setSettingName(NameStatic.SET_SHOW_PAGE_CAR_STRING);
		settingList_showPageCar.setSettingValue(theSetting.getShowPageCar());

		SettingList settingList_showchange = new SettingList();
		settingList_showchange.setSettingName(NameStatic.SET_SHOW_CHANGE_CAR_STRING);
		settingList_showchange.setSettingValue(theSetting.getShowPageChage());

		SettingList settingList_showImage = new SettingList();
		settingList_showImage.setSettingName(NameStatic.SET_SHOW_IMAGE_STRING);
		settingList_showImage.setSettingValue(theSetting.getShowImageChoise());

		SettingList settingList_bigWord = new SettingList();
		settingList_bigWord.setSettingName(NameStatic.SET_USE_BIG_WORD_STRING);
		settingList_bigWord.setSettingValue(theSetting.getUseBigWord());

		SettingList settingList_update = new SettingList();
		settingList_update.setSettingName(NameStatic.SET_UPDATE_AUTO_STRING);
		settingList_update.setSettingValue(theSetting.getUpdateAuto());

		SettingList settingList_chear = new SettingList();
		settingList_chear.setSettingName(NameStatic.SET_DELETE_AUTO_STRING);
		settingList_chear.setSettingValue(theSetting.getClearAuto());

		SettingList settingList_backList = new SettingList();
		settingList_backList.setSettingName(NameStatic.SET_BACK_STRING);

		// 依次添加
		theSettingLists.add(settingList_useBro);
		theSettingLists.add(settingList_showImageWifi);
		theSettingLists.add(settingList_showPageCar);
		theSettingLists.add(settingList_showchange);
		theSettingLists.add(settingList_showImage);
		theSettingLists.add(settingList_bigWord);
		theSettingLists.add(settingList_update);
		theSettingLists.add(settingList_chear);
		theSettingLists.add(settingList_backList);

		return theSettingLists;
	}

	public Setting getSetting(ArrayList<SettingList> theSettingLists) {
		Setting theSetting = new Setting();

		theSetting.setUseOwnBro(theSettingLists.get(0).getSettingValue());
		theSetting.setShowImageInWifi(theSettingLists.get(1).getSettingValue());
		theSetting.setShowPageCar(theSettingLists.get(2).getSettingValue());
		theSetting.setShowPageChage(theSettingLists.get(3).getSettingValue());
		theSetting.setShowImageChoise(theSettingLists.get(4).getSettingValue());
		theSetting.setUseBigWord(theSettingLists.get(5).getSettingValue());
		theSetting.setUpdateAuto(theSettingLists.get(6).getSettingValue());
		theSetting.setClearAuto(theSettingLists.get(7).getSettingValue());

		return theSetting;
	}
}
