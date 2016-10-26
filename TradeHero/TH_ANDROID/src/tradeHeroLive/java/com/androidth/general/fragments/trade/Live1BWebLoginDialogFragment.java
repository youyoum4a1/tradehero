package com.androidth.general.fragments.trade;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidth.general.R;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class Live1BWebLoginDialogFragment extends DialogFragment
{
    private ProgressDialog progressDialog;
    public static final String BUNDLE_KEY_REDIRECT_URL_ID = "ssoUrl";
    private Boolean urlHasLoaded;
    private Boolean userHasLogin;

    private DialogInterface.OnDismissListener onDismissListener;

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        if(onDismissListener!=null && urlHasLoaded) {
            urlHasLoaded = false;
            onDismissListener.onDismiss(dialog);
        }
    }

    public Live1BWebLoginDialogFragment(){
        urlHasLoaded = false;
        userHasLogin = false;
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater,container,savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_fragment_web_tradehub_login, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnClose = (ImageView) view.findViewById(R.id.live_tradehub_login_imageview_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            }
        );

        final WebView webView = (WebView) view.findViewById(R.id.live_tradehub_login_view);

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);


        Bundle bundle = getArguments();
        String url = "";
        if(bundle!=null)
            url = bundle.getString(BUNDLE_KEY_REDIRECT_URL_ID);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished (WebView view, String url) {
                super.onPageFinished(view,url);
                Log.d("LWeb.java", "onPageFinished: ");
                progressDialog.dismiss();
                if(!urlHasLoaded || !userHasLogin) {
                    if(getDialog()!=null) {
                        getDialog().getWindow().setLayout(1000, 1200);
                        final View decorView = getDialog().getWindow().getDecorView();
                        urlHasLoaded = true;
                        YoYo.with(Techniques.BounceInDown).playOn(decorView);
                    }
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Toast.makeText(getActivity(),"shouldOverrideUrlLoading url received: " + url, Toast.LENGTH_LONG);

                Log.d("LWeb.java", "current URL : " + url);

                if(url.contains("sso/authenticated")) // show buy/sell
                {
                    userHasLogin = true;
                    dismiss();
                }
                return super.shouldOverrideUrlLoading(view, url);

            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            //    webView.loadUrl("http://localhost/"); // if no internet

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();

        if(getDialog()==null)
            return;
    //    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);     android.util.AndroidRuntimeException: requestFeature() must be called before adding content

        progressDialog = ProgressDialog.show(
                getActivity(),
                getString(R.string.loading_loading),
                getString(R.string.alert_dialog_please_wait),
                true);


    }

}
