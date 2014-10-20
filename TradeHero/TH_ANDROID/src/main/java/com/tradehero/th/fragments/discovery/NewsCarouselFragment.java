package com.tradehero.th.fragments.discovery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewsCarouselFragment extends Fragment
{
    private static final String NEWS_TYPE_KEY = NewsCarouselFragment.class.getName() + ".newsType";

    private NewsType newsType;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null)
        {
            int newsTypeOrdinal = args.getInt(NEWS_TYPE_KEY);
            if (newsTypeOrdinal >= 0 && newsTypeOrdinal < NewsType.values().length)
            {
                this.newsType = NewsType.values()[newsTypeOrdinal];
            }
        }
    }

    public static Fragment newInstance(NewsType newsType)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(NEWS_TYPE_KEY, newsType.ordinal());
        NewsCarouselFragment carouselFragment = new NewsCarouselFragment();
        carouselFragment.setArguments(bundle);
        return carouselFragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(newsType.titleViewResourceId, container, false);
    }
}
