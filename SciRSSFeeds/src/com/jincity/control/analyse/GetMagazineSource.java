package com.jincity.control.analyse;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;

import android.util.Log;

import com.jincity.rss.magazine.SourceMagazine;
import com.jincity.type.change.SourceMagazineChange;

public class GetMagazineSource {

	private static final String TAG_STRING = "GetMagazineSource";

	private static final String SourcePath = "http://www.xxxxxx.com/src/";
	private static final String yixue1 = "yixue1.txt";
	private static final String yixue2 = "yixue2.txt";
	private static final String yixue3 = "yixue3.txt";
	private static final String huaxue1 = "huaxue1.txt";
	private static final String huaxue2 = "huaxue2.txt";
	private static final String wuli1 = "wuli1.txt";
	private static final String wuli2 = "wuli2.txt";
	private static final String shengwu1 = "shengwu1.txt";
	private static final String shengwu2 = "shengwu2.txt";
	private static final String shengwu3 = "shengwu3.txt";
	private static final String qita1 = "qita1.txt";
	private static final String qita2 = "qita2.txt";

	// 读取网络上该对应的文件
	/**
	 * show 获取yixue期刊列表
	 * 
	 * @param i
	 *            只能是 1 或 2 或 3
	 * @return 返回该期刊列表下的所有期刊
	 */
	public ArrayList<SourceMagazine> getYixue(int i) {
		ArrayList<SourceMagazine> sourcebMagazines = new ArrayList<SourceMagazine>();

		String getString = null;
		String type = null;
		switch (i) {
		case 1:
			type = yixue1;
			break;
		case 2:
			type = yixue2;
			break;
		case 3:
			type = yixue3;
			break;
		default:
			break;
		}

		try {
			Log.d(TAG_STRING, "URL:" + SourcePath + type);
			URL uri = new URL(SourcePath + type);// 定义我们要访问的地址url
			URLConnection ucon = uri.openConnection();// 打开这个url连接
			ucon.connect();
			InputStream is = ucon.getInputStream();// 从上面的链接中取得InputStream
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(100);
			int current = 0;
			/* 一直读到文件结束 */
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			getString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
			SourceMagazineChange sourceMagazineChange = new SourceMagazineChange();
			sourcebMagazines = sourceMagazineChange.getSourceMagazines(getString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG_STRING, "getYixue error");
		}

		return sourcebMagazines;
	}

	/**
	 * show 获取化学期刊列表
	 * 
	 * @param i
	 *            只能是 1 2 3
	 * @return 期刊列表下所有的期刊
	 */
	public ArrayList<SourceMagazine> gethuaxue(int i) {
		ArrayList<SourceMagazine> sourcebMagazines = new ArrayList<SourceMagazine>();

		String getString = null;
		String type = null;
		switch (i) {
		case 1:
			type = huaxue1;
			break;
		case 2:
			type = huaxue2;
			break;
		default:
			break;
		}

		try {
			URL uri = new URL(SourcePath + type);// 定义我们要访问的地址url
			URLConnection ucon = uri.openConnection();// 打开这个url连接
			InputStream is = ucon.getInputStream();// 从上面的链接中取得InputStream
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(100);
			int current = 0;
			/* 一直读到文件结束 */
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			getString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
			SourceMagazineChange sourceMagazineChange = new SourceMagazineChange();
			sourcebMagazines = sourceMagazineChange.getSourceMagazines(getString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG_STRING, "gethuaxue error");
		}

		return sourcebMagazines;
	}

	/**
	 * show 获取化物理刊列表
	 * 
	 * @param i
	 *            只能是 1 2 3
	 * @return 期刊列表下所有的期刊
	 */
	public ArrayList<SourceMagazine> getwuli(int i) {
		ArrayList<SourceMagazine> sourcebMagazines = new ArrayList<SourceMagazine>();

		String getString = null;
		String type = null;
		switch (i) {
		case 1:
			type = wuli1;
			break;
		case 2:
			type = wuli2;
			break;
		default:
			break;
		}

		try {
			URL uri = new URL(SourcePath + type);// 定义我们要访问的地址url
			URLConnection ucon = uri.openConnection();// 打开这个url连接
			InputStream is = ucon.getInputStream();// 从上面的链接中取得InputStream
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(100);
			int current = 0;
			/* 一直读到文件结束 */
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			getString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
			SourceMagazineChange sourceMagazineChange = new SourceMagazineChange();
			sourcebMagazines = sourceMagazineChange.getSourceMagazines(getString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG_STRING, "getwuli error");
		}

		return sourcebMagazines;
	}

	/**
	 * show 获取生物期刊列表
	 * 
	 * @param i
	 *            只能是 1 2 3
	 * @return 期刊列表下所有的期刊
	 */
	public ArrayList<SourceMagazine> getshengwu(int i) {
		ArrayList<SourceMagazine> sourcebMagazines = new ArrayList<SourceMagazine>();

		String getString = null;
		String type = null;
		switch (i) {
		case 1:
			type = shengwu1;
			break;
		case 2:
			type = shengwu2;
			break;
		case 3:
			type = shengwu3;
			break;
		default:
			break;
		}

		try {
			URL uri = new URL(SourcePath + type);// 定义我们要访问的地址url
			URLConnection ucon = uri.openConnection();// 打开这个url连接
			InputStream is = ucon.getInputStream();// 从上面的链接中取得InputStream
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(100);
			int current = 0;
			/* 一直读到文件结束 */
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			getString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
			SourceMagazineChange sourceMagazineChange = new SourceMagazineChange();
			sourcebMagazines = sourceMagazineChange.getSourceMagazines(getString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG_STRING, "getshengwu error");
		}

		return sourcebMagazines;
	}

	/**
	 * show 获取其它期刊列表
	 * 
	 * @param i
	 *            只能是 1 2 3
	 * @return 期刊列表下所有的期刊
	 */
	public ArrayList<SourceMagazine> getqita(int i) {
		ArrayList<SourceMagazine> sourcebMagazines = new ArrayList<SourceMagazine>();

		String getString = null;
		String type = null;
		switch (i) {
		case 1:
			type = qita1;
			break;
		case 2:
			type = qita2;
			break;
		default:
			break;
		}

		try {
			URL uri = new URL(SourcePath + type);// 定义我们要访问的地址url
			URLConnection ucon = uri.openConnection();// 打开这个url连接
			InputStream is = ucon.getInputStream();// 从上面的链接中取得InputStream
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(100);
			int current = 0;
			/* 一直读到文件结束 */
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			getString = EncodingUtils.getString(baf.toByteArray(), "UTF-8");
			SourceMagazineChange sourceMagazineChange = new SourceMagazineChange();
			sourcebMagazines = sourceMagazineChange.getSourceMagazines(getString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d(TAG_STRING, "getqita error");
		}

		return sourcebMagazines;
	}

}
