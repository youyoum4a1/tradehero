/**
 * NewsFragment.java 
 * TradeHero
 *
 * Created by @author Siddesh Bingi on Jul 28, 2013
 */
package com.tradehero.th.fragments;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import com.tradehero.th.R;
import com.tradehero.th.application.App;
import com.tradehero.th.application.Config;
import com.tradehero.th.models.Trend;
import com.tradehero.th.rss.RssFeed;
import com.tradehero.th.rss.RssItem;
import com.tradehero.th.rss.RssReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class NewsFragment extends ListFragment
{

    public final static String HEADER = "header";
    public final static String URL = "url";

    private Trend trend;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getListView().setCacheColorHint(android.R.color.transparent);
        //getListView().setDivider(new ColorDrawable(R.color.black));

        //trend = ((App) getActivity().getApplication()).getTrend();

        requestToGetTrendRssNewsFeed();
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id)
    {

        Bundle b = new Bundle();
        b.putString(HEADER, String.format("%s:%s", trend.getExchange(), trend.getSymbol()));
        b.putString(URL, ((RssItem) lv.getItemAtPosition(position)).getLink());

        Fragment newFragment = Fragment.instantiate(getActivity(),
                WebViewFragment.class.getName(), b);

        // Add the fragment to the activity, pushing this transaction
        // on to the back stack.
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.realtabcontent, newFragment, "trend_rss_detail");
        ft.addToBackStack("trend_rss_detail");
        ft.commit();
    }

    private void requestToGetTrendRssNewsFeed()
    {

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(Config.getTrendRssFeed(), trend.getYahooSymbol()), new AsyncHttpResponseHandler()
        {

            @Override
            public void onSuccess(String response)
            {

                RssFeed mRssFeed = null;

                try
                {
                    mRssFeed = RssReader.read(response);

                    if (mRssFeed != null)
                    {
                        setListAdapter(new TrendRssNewsFeedAdapter(getActivity(), mRssFeed.getRssItems()));
                    }
                } catch (MalformedURLException e)
                {
                    e.printStackTrace();
                } catch (SAXException e)
                {
                    e.printStackTrace();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable arg0, String arg1)
            {

            }
        });
    }

    private class TrendRssNewsFeedAdapter extends ArrayAdapter<RssItem>
    {

        public TrendRssNewsFeedAdapter(Context context, List<RssItem> rssItems)
        {
            super(context, 0, rssItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            if (convertView == null)
            {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.news_list_item, null);
            }

            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView date = (TextView) convertView.findViewById(R.id.date);

            title.setText(getItem(position).getTitle());
            date.setText(getItem(position).getPubDate().toString());

            return convertView;
        }
    }
}
