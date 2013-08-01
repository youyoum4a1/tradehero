/**
 * WebViewFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 28, 2013
 */
package android.tradehero.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.tradehero.activities.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WebViewFragment extends Fragment {
	
	private WebView mWebView;
	private ProgressBar mProgressSpinner;
	private ImageView mStopRelodBtn;
	private TextView mHeaderText;
	private boolean isLoading = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		view = inflater.inflate(R.layout.fragment_webview, container, false);
		initViews(view);
		return view;
	}
	
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initViews(View v) {
		
		mHeaderText =  (TextView) v.findViewById(R.id.header_txt);
		mWebView = (WebView) v.findViewById(R.id.webview);;
		mWebView.getSettings().setBuiltInZoomControls(true);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setPluginState(PluginState.ON);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		mWebView.getSettings().setRenderPriority(RenderPriority.HIGH);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		
		mWebView.setWebChromeClient(new WebChromeClient() {
			
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				
				if(newProgress == 100) {
					mProgressSpinner.setVisibility(View.GONE);
					mStopRelodBtn.setImageResource(android.R.drawable.ic_menu_rotate);
					isLoading = false;
				}
				else if(newProgress < 100) {
					mProgressSpinner.setVisibility(View.VISIBLE);
					mStopRelodBtn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
					isLoading = true;
				}
					
			}
		});
		
		mWebView.setWebViewClient(new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}
		});
		
		
		
		mProgressSpinner = (ProgressBar) v.findViewById(R.id.progress);
		mStopRelodBtn = (ImageView) v.findViewById(R.id.btn_stop_refresh);
		mStopRelodBtn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				if(isLoading)
					mWebView.stopLoading();
				else
					mWebView.reload();
				
			}
		});
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mHeaderText.setText(getArguments().getString(NewsFragment.HEADER));
		
		mWebView.loadUrl(getArguments().getString(NewsFragment.URL));
	}
	
	
	
}
