package com.tradehero.chinabuild.fragment.security;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tradehero.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListSecurityKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.persistence.news.NewsItemCompactListCacheNew;
import com.tradehero.th.utils.DaggerUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Time;

import javax.inject.Inject;

import dagger.Lazy;


/**
 * Created by palmer on 15/6/10.
 */
public class SecurityDetailSubNewsFragment extends Fragment implements View.OnClickListener{

    private String securityName;
    private SecurityId securityId;

    private LinearLayout newsLL;
    private ImageView emptyIV;
    private TextView moreTV;

    //Layout 0
    private RelativeLayout rl0;
    private TextView newsTV0;
    private TextView dateTV0;

    //Layout 1
    private RelativeLayout rl1;
    private TextView newsTV1;
    private TextView dateTV1;
    private View seperateLine1;

    //Layout 2
    private RelativeLayout rl2;
    private TextView newsTV2;
    private TextView dateTV2;
    private View seperateLine2;

    //Layout 3
    private RelativeLayout rl3;
    private TextView newsTV3;
    private TextView dateTV3;
    private View seperateLine3;

    //Layout 4
    private RelativeLayout rl4;
    private TextView newsTV4;
    private TextView dateTV4;
    private View seperateLine4;

    private int securityDTOId = -1;

    @Inject public Lazy<PrettyTime> prettyTime;
    private NewsItemListKey listKey;
    @Inject NewsItemCompactListCacheNew newsTitleCache;
    private DTOCacheNew.Listener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> newsCacheListener;

    private NewsViewHolder[] newsViewHolders = new NewsViewHolder[5];

    PaginatedDTO<NewsItemCompactDTO> news;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments();
        newsCacheListener = new NewsHeadlineNewsListListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    private void initArguments() {
        Bundle args = getArguments();
        Bundle securityIdBundle = args.getBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE);
        securityName = args.getString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME);
        if (securityIdBundle != null) {
            securityId = new SecurityId(securityIdBundle);
        }
        securityDTOId = args.getInt(SecurityDetailFragment.BUNDLE_KEY_SECURITY_DTO_ID_BUNDLE, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_detail_news, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchSecurityNews(true);
    }

    @Override
    public void onDestroyView() {
        newsTitleCache.unregister(newsCacheListener);
        super.onDestroyView();
    }

    private void initViews(View view) {
        moreTV = (TextView) view.findViewById(R.id.textview_more);
        moreTV.setOnClickListener(this);
        newsLL = (LinearLayout) view.findViewById(R.id.linearlayout_news);
        newsLL.setVisibility(View.GONE);
        emptyIV = (ImageView) view.findViewById(R.id.imageview_empty);
        emptyIV.setVisibility(View.VISIBLE);

        rl0 = (RelativeLayout) view.findViewById(R.id.rl_news0);
        newsTV0 = (TextView) view.findViewById(R.id.textview_news_content0);
        dateTV0 = (TextView) view.findViewById(R.id.textview_news_date0);
        rl0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toEnterTimeLineDetail(0);
            }
        });

        rl1 = (RelativeLayout) view.findViewById(R.id.rl_news1);
        newsTV1 = (TextView) view.findViewById(R.id.textview_news_content1);
        dateTV1 = (TextView) view.findViewById(R.id.textview_news_date1);
        seperateLine1 = view.findViewById(R.id.line1);
        rl1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toEnterTimeLineDetail(1);
            }
        });

        rl2 = (RelativeLayout) view.findViewById(R.id.rl_news2);
        newsTV2 = (TextView) view.findViewById(R.id.textview_news_content2);
        dateTV2 = (TextView) view.findViewById(R.id.textview_news_date2);
        seperateLine2 = view.findViewById(R.id.line2);
        rl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toEnterTimeLineDetail(2);
            }
        });

        rl3 = (RelativeLayout) view.findViewById(R.id.rl_news3);
        newsTV3 = (TextView) view.findViewById(R.id.textview_news_content3);
        dateTV3 = (TextView) view.findViewById(R.id.textview_news_date3);
        seperateLine3 = view.findViewById(R.id.line3);
        rl3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toEnterTimeLineDetail(3);
            }
        });

        rl4 = (RelativeLayout) view.findViewById(R.id.rl_news4);
        newsTV4 = (TextView) view.findViewById(R.id.textview_news_content4);
        dateTV4 = (TextView) view.findViewById(R.id.textview_news_date4);
        seperateLine4 = view.findViewById(R.id.line4);
        rl4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toEnterTimeLineDetail(4);
            }
        });

        newsViewHolders[0] = new NewsViewHolder(rl0, newsTV0, dateTV0, null);
        newsViewHolders[1] = new NewsViewHolder(rl1, newsTV1, dateTV1, seperateLine1);
        newsViewHolders[2] = new NewsViewHolder(rl2, newsTV2, dateTV2, seperateLine2);
        newsViewHolders[3] = new NewsViewHolder(rl3, newsTV3, dateTV3, seperateLine3);
        newsViewHolders[4] = new NewsViewHolder(rl4, newsTV4, dateTV4, seperateLine4);

    }

    private void enterDiscussList() {
        if (securityDTOId == -1) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(SecurityDiscussOrNewsFragment.BUNDLE_KEY_DISCUSS_OR_NEWS_TYPE, 1);
        bundle.putBundle(SecurityDiscussOrNewsFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        bundle.putString(SecurityDiscussOrNewsFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
        bundle.putInt(SecurityDiscussOrNewsFragment.BUNDLE_KEY_SECURIYT_COMPACT_ID, securityDTOId);
        bundle.putBoolean(SecurityDiscussOrNewsFragment.BUNDLE_ARGUMENT_IS_NEWS, true);
        pushFragment(SecurityDiscussOrNewsFragment.class, bundle);

    }

    private DashboardNavigator getDashboardNavigator() {
        DashboardNavigatorActivity activity = ((DashboardNavigatorActivity) getActivity());
        if (activity != null) {
            return activity.getDashboardNavigator();
        }
        return null;
    }

    private Fragment pushFragment(Class fragmentClass, Bundle args) {
        return getDashboardNavigator().pushFragment(fragmentClass, args);
    }


    private void fetchSecurityNews(boolean force) {
        if (listKey == null) {
            listKey = new NewsItemListSecurityKey(new SecurityIntegerId(securityDTOId), 1, 6);
        }

        newsTitleCache.unregister(newsCacheListener);
        newsTitleCache.register(listKey, newsCacheListener);
        newsTitleCache.getOrFetchAsync(listKey, force);

    }

    void updateNewsContent(PaginatedDTO<NewsItemCompactDTO> newsList) {

        news = newsList;

        if(newsList ==null || newsList.getData() == null || newsList.getData().size()<=0){
            emptyIV.setVisibility(View.VISIBLE);
            newsLL.setVisibility(View.GONE);
        }else{
            emptyIV.setVisibility(View.GONE);
            newsLL.setVisibility(View.VISIBLE);
        }
        if(newsList.getData().size()<5){
            moreTV.setVisibility(View.GONE);
        }else{
            moreTV.setVisibility(View.VISIBLE);
        }

        int maxLength = newsList.getData().size() > 5 ? 5 : newsList.getData().size();

        for (int i = 0; i < maxLength; i++) {
            newsViewHolders[i].displayNews(newsList.getData().get(i), prettyTime.get());
        }
        for (int i = maxLength; i < newsViewHolders.length; i++) {
            newsViewHolders[i].gone();
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.textview_more:
                enterDiscussList();
                break;
        }
    }

    class NewsHeadlineNewsListListener implements DTOCacheNew.HurriedListener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>{
        @Override public void onPreCachedDTOReceived(
                NewsItemListKey key,
                PaginatedDTO<NewsItemCompactDTO> value){
            updateNewsContent(value);

        }

        @Override public void onDTOReceived(
                NewsItemListKey key,
                PaginatedDTO<NewsItemCompactDTO> value){
            updateNewsContent(value);

        }

        @Override public void onErrorThrown(
                NewsItemListKey key,
                Throwable error) { }
    }

    static class NewsViewHolder {
        private View parent;
        private TextView news;
        private TextView date;
        private View separate;

        public NewsViewHolder(View parent, TextView news, TextView date, View separate) {
            this.parent = parent;
            this.news = news;
            this.date = date;
            this.separate = separate;
        }

        public void displayNews(NewsItemCompactDTO newsDTO, PrettyTime prettyTime) {
            parent.setVisibility(View.VISIBLE);
            if (separate != null) {
                separate.setVisibility(View.VISIBLE);
            }
            news.setText(newsDTO.description);
            date.setText(prettyTime.formatUnrounded(newsDTO.createdAtUtc));
        }

        public void gone() {
            parent.setVisibility(View.GONE);
            if (separate != null) {
                separate.setVisibility(View.GONE);
            }
        }
    }

    private void toEnterTimeLineDetail(int index){
        if(news == null){
            return;
        }
        if(news.getData() == null ){
            return;
        }
        if(news.getData().size() <= index){
            return;
        }
        NewsItemCompactDTO dto = news.getData().get(index);
        enterTimeLineDetail(dto);
    }

    public void enterTimeLineDetail(NewsItemCompactDTO dto)
    {
        if (dto != null)
        {
            Bundle bundle = new Bundle();
            bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_ID, dto.getDiscussionKey().getArgs());
            bundle.putInt(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_TYPE, TimeLineItemDetailFragment.DISCUSSION_DISCUSSION_TYPE);
            bundle.putBoolean(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_IS_NEWS, true);
            pushFragment(TimeLineItemDetailFragment.class, bundle);
        }
    }
}
