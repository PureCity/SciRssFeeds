package com.jincity.control.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jincity.common.name.DatabaseNameCom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

@SuppressLint("SdCardPath")
public class DatabaseFirstOperate {

	private static final String TAG_STRING = "DatabaseFirstOperate";
	private static String DB_PATH = DatabaseNameCom.DATABASE_PTAH;
	private static String DB_NAME = DatabaseNameCom.RSS_DATABASENAME;
	Context myContext;

	public DatabaseFirstOperate(Context myContext) {
		this.myContext = myContext;
	}

	/**
	 * show 将打包的数据库复制到app对应的数据库目录下
	 * 
	 * @return true表示复制成功,否则表示复制失败
	 */
	public boolean copyMyDB() {
		try {
			InputStream myInput = myContext.getAssets().open(DB_NAME);
			File dir = new File(DB_PATH);
            if(!dir.exists())//判断文件夹是否存在，不存在就新建一个
                dir.mkdir();
			String outFileName = DB_PATH + DB_NAME;
			OutputStream myOutput = new FileOutputStream(outFileName);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}
			myOutput.flush();
			myOutput.close();
			myInput.close();
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG_STRING, "copyMyDB fail");
			return false;
		}
		return true;
	}

}
