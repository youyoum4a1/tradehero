package android.tradehero.activities;

import android.app.Activity;
import android.os.Bundle;
import android.tradehero.activities.R;
import android.webkit.WebView;

public class WebViewActivity extends Activity {
	 
		private WebView webView;
		public static final String SHOW_URL="showUrl"; 
	 
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.webview);
			String url = getIntent().getStringExtra(SHOW_URL);
			if(url!=null)
			{
				webView = (WebView) findViewById(R.id.webView1);
				webView.getSettings().setJavaScriptEnabled(true);
				webView.loadUrl(url);
			}else
			{
				finish();
			}
		
		}
	 
	} 


