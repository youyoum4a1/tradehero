package com.tradehero.th.fragments.onboarding;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;

public class OnBoardPagingHolder
{
    @InjectView(R.id.next_button) View nextButton;
    @InjectView(R.id.done_button) View doneButton;
    @InjectView(R.id.pager) ViewPager pager;
    PagerAdapter pagerAdapter;

    //<editor-fold desc="Constructors">
    public OnBoardPagingHolder(FragmentManager fm)
    {
        super();
        pagerAdapter = new OnBoardFragmentPagerAdapter(fm);
    }
    //</editor-fold>

    void attachView(View view)
    {
        ButterKnife.inject(this, view);
        pager.setAdapter(pagerAdapter);
        pager.setCurrentItem(0, false);
    }

    void detachView()
    {
        ButterKnife.reset(this);
    }

    @OnClick(R.id.next_button)
    public void onNextClicked(/*View view*/)
    {
        int currentPosition = pager.getCurrentItem();
        pager.setCurrentItem(currentPosition + 1, true);
        updateButtons(currentPosition + 1);
    }

    @OnClick(R.id.done_button)
    public void onDoneClicked(/*View view*/)
    {
        // TODO fade out
    }

    protected void updateButtons(int position)
    {
        boolean isLastPage = position == pagerAdapter.getCount() - 1;
        nextButton.setVisibility(isLastPage ? View.GONE : View.VISIBLE);
        nextButton.setVisibility(isLastPage ? View.VISIBLE : View.GONE);
    }
}
