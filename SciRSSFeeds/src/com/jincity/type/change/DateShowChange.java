package com.jincity.type.change;

public class DateShowChange {
	// �޸Ĵ����ݿ��л�ȡ������ת��Ϊ��-��-�յ���ʽ

	public String getDate(String thePubDate) {
		String pubDate = null;

		String tempString = thePubDate.substring(5, 16);// ��ȡ�����е� �� �� ��
		pubDate = tempString.substring(7);// ��ȡ���
		String aString = tempString.substring(3, 6);// ��ȡ�·�
		int month = 0;
		switch (aString) {
		case "Jan":
			month = 1;
			break;
		case "Feb":
			month = 2;
			break;
		case "Mar":
			month = 3;
			break;
		case "Apr":
			month = 4;
			break;
		case "May":
			month = 5;
			break;
		case "Jun":
			month = 6;
			break;
		case "Jul":
			month = 7;
			break;
		case "Aug":
			month = 8;
			break;
		case "Sep":
			month = 9;
			break;
		case "Oct":
			month = 10;
			break;
		case "Nov":
			month = 11;
			break;
		case "Dec":
			month = 12;
			break;
		default:
			break;
		}
		pubDate = pubDate + "-" + month;
		pubDate = pubDate + "-" + tempString.substring(0, 2);

		return pubDate;
	}

}
