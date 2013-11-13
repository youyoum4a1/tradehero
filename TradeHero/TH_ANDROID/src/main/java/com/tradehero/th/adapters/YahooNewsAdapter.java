package com.tradehero.th.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.tradehero.th.api.yahoo.News;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.WebViewFragment;
import com.tradehero.th.widget.news.YahooNewsView;

/**
 * Created by julien on 11/10/13
 *
 * Map a Yahoo News object to a YahooNewsView.
 */
public class YahooNewsAdapter extends DTOAdapter<News, YahooNewsView>
{
    private final static String TAG = YahooNewsAdapter.class.getSimpleName();

    public YahooNewsAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override protected void fineTune(final int position, News dto, final YahooNewsView dtoView)
    {
        dtoView.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                News news = (News) getItem(position);
                if (news.getUrl() != null)
                {
                    Navigator navigator = ((NavigatorActivity) context).getNavigator();
                    Bundle bundle = new Bundle();
                    bundle.putString(WebViewFragment.BUNDLE_KEY_URL, news.getUrl());
                    navigator.pushFragment(WebViewFragment.class, bundle);
                }
            }
        });
    }
}
