package com.tradehero.th.auth.twitter;

/** Created with IntelliJ IDEA. User: tho Date: 8/19/13 Time: 6:40 PM Copyright (c) TradeHero */

import android.R;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class OAuthDialog extends Dialog
{
    private static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(-1, -1);
    private final String callbackUrl;
    private final String requestUrl;
    private final String serviceUrlIdentifier;
    private final FlowResultHandler handler;
    private ProgressDialog progressDialog;
    private ImageView closeImage;
    private WebView webView;
    private FrameLayout content;

    public OAuthDialog(Context context, String requestUrl, String callbackUrl,
            String serviceUrlIdentifier, FlowResultHandler resultHandler)
    {
        super(context, R.style.Theme_Translucent_NoTitleBar);
        this.requestUrl = requestUrl;
        this.callbackUrl = callbackUrl;
        this.serviceUrlIdentifier = serviceUrlIdentifier;
        this.handler = resultHandler;

        setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            public void onCancel(DialogInterface dialog)
            {
                OAuthDialog.this.handler.onCancel();
            }
        });
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.progressDialog = new ProgressDialog(getContext());
        this.progressDialog.requestWindowFeature(1);
        this.progressDialog.setMessage("Loading...");

        requestWindowFeature(1);
        this.content = new FrameLayout(getContext());

        createCloseImage();

        int webViewMargin = this.closeImage.getDrawable().getIntrinsicWidth() / 2;
        setUpWebView(webViewMargin);

        this.content.addView(this.closeImage, new ViewGroup.LayoutParams(-2, -2));
        addContentView(this.content, new ViewGroup.LayoutParams(-1, -1));
    }

    private void createCloseImage()
    {
        this.closeImage = new ImageView(getContext());

        this.closeImage.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                OAuthDialog.this.cancel();
            }
        });
        Drawable closeDrawable = getContext().getResources().getDrawable(R.drawable.btn_dialog);
        this.closeImage.setImageDrawable(closeDrawable);

        this.closeImage.setVisibility(4);
    }

    private void setUpWebView(int margin)
    {
        LinearLayout webViewContainer = new LinearLayout(getContext());
        this.webView = new WebView(getContext());
        this.webView.setVerticalScrollBarEnabled(false);
        this.webView.setHorizontalScrollBarEnabled(false);
        this.webView.setWebViewClient(new OAuth1WebViewClient());
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.loadUrl(this.requestUrl);
        this.webView.setLayoutParams(FILL);
        this.webView.setVisibility(4);

        webViewContainer.setPadding(margin, margin, margin, margin);
        webViewContainer.addView(this.webView);
        this.content.addView(webViewContainer);
    }

    public static abstract interface FlowResultHandler
    {
        public abstract void onCancel();

        public abstract void onError(int paramInt, String paramString1, String paramString2);

        public abstract void onComplete(String paramString);
    }

    private class OAuth1WebViewClient extends WebViewClient
    {
        private OAuth1WebViewClient()
        {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            if (url.startsWith(OAuthDialog.this.callbackUrl))
            {
                OAuthDialog.this.dismiss();
                OAuthDialog.this.handler.onComplete(url);
                return true;
            }
            if (url.contains(OAuthDialog.this.serviceUrlIdentifier))
            {
                return false;
            }

            OAuthDialog.this.getContext()
                    .startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl)
        {
            super.onReceivedError(view, errorCode, description, failingUrl);
            OAuthDialog.this.dismiss();
            OAuthDialog.this.handler.onError(errorCode, description, failingUrl);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            super.onPageStarted(view, url, favicon);
            OAuthDialog.this.progressDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
            try
            {
                OAuthDialog.this.progressDialog.dismiss();
            }
            catch (IllegalArgumentException localIllegalArgumentException)
            {
            }

            OAuthDialog.this.content.setBackgroundColor(0);
            OAuthDialog.this.webView.setVisibility(0);
            OAuthDialog.this.closeImage.setVisibility(0);
        }
    }
}