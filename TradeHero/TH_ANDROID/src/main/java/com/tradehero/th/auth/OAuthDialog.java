package com.tradehero.th.auth;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.requestUrl = requestUrl;
        this.callbackUrl = callbackUrl;
        this.serviceUrlIdentifier = serviceUrlIdentifier;
        this.handler = resultHandler;

        setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            public void onCancel(DialogInterface dialog)
            {
                handler.onCancel();
            }
        });
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        content = new FrameLayout(getContext());

        createCloseImage();

        int webViewMargin = closeImage.getDrawable().getIntrinsicWidth() / 2;
        setUpWebView(webViewMargin);

        content.addView(closeImage, new ViewGroup.LayoutParams(-2, -2));
        addContentView(content, new ViewGroup.LayoutParams(-1, -1));
    }

    private void createCloseImage()
    {
        closeImage = new ImageView(getContext());

        closeImage.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                OAuthDialog.this.cancel();
            }
        });
        Drawable closeDrawable = getContext().getResources().getDrawable(android.R.drawable.btn_dialog);
        closeImage.setImageDrawable(closeDrawable);

        closeImage.setVisibility(4);
    }

    public void setProgressDialog(ProgressDialog progressDialog)
    {
        this.progressDialog = progressDialog;
    }

    private void setUpWebView(int margin)
    {
        LinearLayout webViewContainer = new LinearLayout(getContext());
        webView = new WebView(getContext());
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new OAuth1WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(requestUrl);
        webView.setLayoutParams(FILL);
        webView.setVisibility(4);

        webViewContainer.setPadding(margin, margin, margin, margin);
        webViewContainer.addView(webView);
        content.addView(webViewContainer);
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

            cancel();
            //OAuthDialog.this.getContext()
            //        .startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
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
            if (progressDialog != null)
            {
                progressDialog.show();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
            if (progressDialog != null)
            {
                progressDialog.dismiss();
            }

            content.setBackgroundColor(0);
            webView.setVisibility(0);
            closeImage.setVisibility(0);
        }
    }
}