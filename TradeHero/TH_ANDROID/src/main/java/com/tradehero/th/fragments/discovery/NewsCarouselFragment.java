package com.tradehero.th.fragments.discovery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;

public class NewsCarouselFragment extends Fragment
{
    private NewsType newsType;

    public NewsCarouselFragment(NewsType newsType)
    {
        super();
        this.newsType = newsType;
    }

    public static Fragment newInstance(NewsType newsType)
    {
        return new NewsCarouselFragment(newsType);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(newsType.titleViewResourceId, container, false);
    }
}
