package com.tradehero.th.fragments.onboarding;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import butterknife.InjectView;
import com.tradehero.th.R;

public class OnBoardProgressHeaderLinearView extends OnBoardHeaderLinearView
{
    @InjectView(R.id.progress) View progressView;

    //<editor-fold desc="Constructors">
    public OnBoardProgressHeaderLinearView(Context context)
    {
        super(context);
    }

    public OnBoardProgressHeaderLinearView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OnBoardProgressHeaderLinearView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    //</editor-fold>

    @Override public void displayRetry(boolean failed)
    {
        super.displayRetry(failed);
        progressView.setVisibility(GONE);
    }

    @Override protected void onButtonRetryClicked(View view)
    {
        progressView.setVisibility(VISIBLE);
        super.onButtonRetryClicked(view);
    }
}