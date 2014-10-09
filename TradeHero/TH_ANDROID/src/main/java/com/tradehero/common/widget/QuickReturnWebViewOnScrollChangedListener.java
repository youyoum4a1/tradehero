package com.tradehero.common.widget;

import android.view.View;
import android.webkit.WebView;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnType;

public class QuickReturnWebViewOnScrollChangedListener implements NotifyingWebView.OnScrollChangedListener
{
    private int mMinFooterTranslation;
    private int mMinHeaderTranslation;
    private int mHeaderDiffTotal = 0;
    private int mFooterDiffTotal = 0;
    private View mHeader;
    private View mFooter;
    private QuickReturnType mQuickReturnType;

    public QuickReturnWebViewOnScrollChangedListener(QuickReturnType quickReturnType, View headerView, int headerTranslation, View footerView,
            int footerTranslation){
        mQuickReturnType = quickReturnType;
        mHeader =  headerView;
        mMinHeaderTranslation = headerTranslation;
        mFooter =  footerView;
        mMinFooterTranslation = footerTranslation;
    }

    @Override public void onScrollChanged(WebView who, int l, int t, int oldl, int oldt)
    {

    }
}
