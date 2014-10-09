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
            int footerTranslation)
    {
        mQuickReturnType = quickReturnType;
        mHeader = headerView;
        mMinHeaderTranslation = headerTranslation;
        mFooter = footerView;
        mMinFooterTranslation = footerTranslation;
    }

    @Override public void onScrollChanged(WebView who, int l, int t, int oldl, int oldt)
    {
        int diff = oldt - t;

        switch (mQuickReturnType)
        {
            case HEADER:
                if (diff <= 0)
                { // scrolling down
                    mHeaderDiffTotal = Math.max(mHeaderDiffTotal + diff, mMinHeaderTranslation);
                }
                else
                { // scrolling up
                    mHeaderDiffTotal = Math.min(Math.max(mHeaderDiffTotal + diff, mMinHeaderTranslation), 0);
                }

                mHeader.setTranslationY(mHeaderDiffTotal);
                break;
            case FOOTER:
                if (diff <= 0)
                { // scrolling down
                    mFooterDiffTotal = Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation);
                }
                else
                { // scrolling up
                    mFooterDiffTotal = Math.min(Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation), 0);
                }

                mFooter.setTranslationY(-mFooterDiffTotal);
                break;
            case BOTH:
                if (diff <= 0)
                { // scrolling down
                    mHeaderDiffTotal = Math.max(mHeaderDiffTotal + diff, mMinHeaderTranslation);
                    mFooterDiffTotal = Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation);
                }
                else
                { // scrolling up
                    mHeaderDiffTotal = Math.min(Math.max(mHeaderDiffTotal + diff, mMinHeaderTranslation), 0);
                    mFooterDiffTotal = Math.min(Math.max(mFooterDiffTotal + diff, -mMinFooterTranslation), 0);
                }

                mHeader.setTranslationY(mHeaderDiffTotal);
                mFooter.setTranslationY(-mFooterDiffTotal);
                break;
        }
    }
}
