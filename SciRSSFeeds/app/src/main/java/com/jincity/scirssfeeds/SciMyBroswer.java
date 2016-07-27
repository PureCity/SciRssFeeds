package com.jincity.scirssfeeds;

import com.jincity.common.name.GlobalFlag;
import com.jincity.common.name.NameStatic;

import android.view.View.OnClickListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class SciMyBroswer extends Activity {

	private LinearLayout myLinearLayout;
	private WebView theView;

	private TextView title;
	private TextView titleLeft;
	private TextView titleRight;

	String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); // 声明使用自定义标题
		setContentView(R.layout.browser);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);// 自定义布局赋值
		myLinearLayout = (LinearLayout) findViewById(R.id.themybroActivity);
		myLinearLayout.setBackgroundColor(Color.WHITE);

		title = (TextView) findViewById(R.id.title_name);
		titleRight = (TextView) findViewById(R.id.title_right);
		title.setVisibility(View.INVISIBLE);
		titleRight.setText(NameStatic.GET_BACK_STRING);
		titleRight.setTextSize(20);
		titleRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		titleLeft = (TextView) findViewById(R.id.title_left);
		titleLeft.setText(NameStatic.MAIN_TITLE_STRING);
		titleLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(SciMyBroswer.this, SciArticleContent.class);
				setResult(GlobalFlag.MAIN_PAGE, intent);
				finish();
			}
		});

		theView = (WebView) findViewById(R.id.own_bro);
		WebSettings webSettings = theView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		Intent intent = getIntent();
		url = intent.getStringExtra("URL");
		theView.loadUrl(url);
		theView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url); // 在当前的webview中跳转到新的url
				return true;
			}
		});
		theView.getSettings().setUseWideViewPort(true);
		theView.getSettings().setLoadWithOverviewMode(true);
		Toast.makeText(SciMyBroswer.this, "正在加载...", Toast.LENGTH_LONG).show();
	}

}
