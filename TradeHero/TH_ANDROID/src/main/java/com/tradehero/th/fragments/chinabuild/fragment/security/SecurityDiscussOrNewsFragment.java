package com.tradehero.th.fragments.chinabuild.fragment.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListSecurityKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.fragment.test.FragmentTest02;
import com.tradehero.th.persistence.news.NewsItemCompactListCacheNew;
import com.tradehero.th2.R;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class SecurityDiscussOrNewsFragment extends DashboardFragment
{
    public final static String BUNDLE_KEY_SECURITY_NAME = SecurityDiscussOrNewsFragment.class.getName() + ".securityName";
    public final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = SecurityDiscussOrNewsFragment.class.getName() + ".securityId";
    public final static String BUNDLE_KEY_DISCUSS_OR_NEWS_TYPE = SecurityDiscussOrNewsFragment.class.getName() + ".discussOrNewsType";
    public final static String BUNDLE_KEY_SECURIYT_COMPACT_ID = SecurityDiscussOrNewsFragment.class.getName() + ".securityCompactDTOId";
    private Bundle securityIdBundle;
    private String securityName;
    private SecurityId securityId;
    private int securityDTOId;


    @Inject NewsItemCompactListCacheNew newsTitleCache;
    @Nullable private DTOCacheNew.Listener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> newsCacheListener;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null)
        {
            securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
            securityName = args.getString(BUNDLE_KEY_SECURITY_NAME);
            securityId = new SecurityId(securityIdBundle);
            securityDTOId = args.getInt(BUNDLE_KEY_SECURIYT_COMPACT_ID);
            Timber.d("SecurityID = " + securityId.toString());
        }

        newsCacheListener = createNewsCacheListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(securityName);
        //setHeadViewMiddleSub(securityId.getDisplayName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.test_fragment01, container, false);
        ButterKnife.inject(this, view);

        fetchSecurityNews();
        return view;
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        newsTitleCache.unregister(newsCacheListener);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    @NotNull protected DTOCacheNew.Listener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> createNewsCacheListener()
    {
        return new NewsHeadlineNewsListListener();
    }

    protected class NewsHeadlineNewsListListener implements DTOCacheNew.HurriedListener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>
    {
        @Override public void onPreCachedDTOReceived(
                @NotNull NewsItemListKey key,
                @NotNull PaginatedDTO<NewsItemCompactDTO> value)
        {
            linkWith(key, value);
        }

        @Override public void onDTOReceived(
                @NotNull NewsItemListKey key,
                @NotNull PaginatedDTO<NewsItemCompactDTO> value)
        {
            linkWith(key,value);
        }

        @Override public void onErrorThrown(
                @NotNull NewsItemListKey key,
                @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_security_info);
        }
    }

    public void linkWith(@NotNull NewsItemListKey key,
            @NotNull PaginatedDTO<NewsItemCompactDTO> value)
    {
        Timber.d("");
    }
    //protected void detachSecurityCache()
    //{
    //    securityCompactCache.unregister(this);
    //}

    private void fetchSecurityNews()
    {
        //Timber.d("%s fetchSecurityNews,consume: %s", TEST_KEY, (System.currentTimeMillis() - start));
        //detachSecurityCache();
        NewsItemListKey listKey = new NewsItemListSecurityKey(new SecurityIntegerId(securityDTOId), null, null);
        newsTitleCache.register(listKey, newsCacheListener);
        newsTitleCache.getOrFetchAsync(listKey, false);
    }
}
