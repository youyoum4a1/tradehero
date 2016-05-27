package com.tradehero.th.fragments.news;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.TextView;

import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.FragmentOuterElements;
import com.tradehero.th.fragments.trade.AbstractBuySellFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.fragments.trade.FXMainFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.security.SecurityMultiFetchAssistant;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.AnalyticsDuration;
import com.tradehero.th.utils.route.THRouter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.OnItemClickEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Routable({
        "news/:newsId"
})
public class NewsWebFragment extends WebViewFragment
{
    private static final String BUNDLE_KEY_PREVIOUS_SCREEN = NewsWebFragment.class + ".previousScreen";
    private static final String BUNDLE_KEY_NEWS_ID = NewsWebFragment.class + ".news.id";

    private static final int SWIPE_MIN_DISTANCE = 30;

    @Inject NewsServiceWrapper newsServiceWrapper;
    @Inject protected SecurityMultiFetchAssistant securityMultiFetchAssistant;
    //TODO Change Analytics
    //@Inject Analytics analytics;
    @Inject FragmentOuterElements fragmentElements;
    @Inject THRouter thRouter;
    @Inject PortfolioCompactListCacheRx portfolioCompactListCache;
    @Inject CurrentUserId currentUserId;

    @RouteProperty("newsId") Integer routedNewsId;

    private String previousScreen;
    private int newsId;
    private long beginTime;

    static DecimalFormat PRICE_FORMAT = new DecimalFormat("#.##");

    private Subscription subscription;

    private Gallery stockGallery;
    private ArrayAdapter<SecurityCompactDTO> securityArrayAdapter;

    public static void putPreviousScreen(@NonNull Bundle bundle, @NonNull String previousScreen)
    {
        //TODO Maybe use Enum here.
        bundle.putString(BUNDLE_KEY_PREVIOUS_SCREEN, previousScreen);
    }

    public static void putNewsId(@NonNull Bundle bundle, int id)
    {
        bundle.putInt(BUNDLE_KEY_NEWS_ID, id);
    }

    private int getNewsId()
    {
        if (!getArguments().containsKey(BUNDLE_KEY_NEWS_ID))
        {
            throw new IllegalArgumentException("Missing news id");
        }
        return getArguments().getInt(BUNDLE_KEY_NEWS_ID);
    }

    @Nullable private String getPreviousScreenFromBundle()
    {
        if (getArguments() != null)
        {
            return getArguments().getString(BUNDLE_KEY_PREVIOUS_SCREEN);
        }
        return null;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        previousScreen = getPreviousScreenFromBundle();
        thRouter.inject(this);
        if (routedNewsId != null)
        {
            putNewsId(getArguments(), routedNewsId);
            routedNewsId = null;
        }
        newsId = getNewsId();
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        stockGallery = (Gallery) view.findViewById(R.id.security_list);
        securityArrayAdapter = new CompactSecurityListAdapter(getActivity(), R.layout.news_compact_stock_info);
        stockGallery.setAdapter(securityArrayAdapter);
        adjustFirstItemOfGallery();
        stockGallery.setVisibility(View.INVISIBLE);

        webView.getSettings().setBuiltInZoomControls(false);

        onDestroyViewSubscriptions.add(
                Observable.combineLatest(
                        WidgetObservable.itemClicks(stockGallery),
                        portfolioCompactListCache.getOne(currentUserId.toUserBaseKey())
                                .map(new PairGetSecond<UserBaseKey, PortfolioCompactDTOList>()),
                        new Func2<OnItemClickEvent, PortfolioCompactDTOList, Fragment>()
                        {
                            @Override public Fragment call(OnItemClickEvent onItemClickEvent, PortfolioCompactDTOList portfolioCompactDTOs)
                            {
                                SecurityCompactDTO dto =
                                        (SecurityCompactDTO) onItemClickEvent.parent().getItemAtPosition(onItemClickEvent.position());
                                Bundle args = new Bundle();
                                if (dto instanceof FxSecurityCompactDTO)
                                {
                                    FXMainFragment.putRequisite(
                                            args,
                                            new AbstractBuySellFragment.Requisite(
                                                    dto.getSecurityId(),
                                                    portfolioCompactDTOs.getDefaultFxPortfolio().getOwnedPortfolioId(),
                                                    0));
                                    return navigator.get().pushFragment(FXMainFragment.class, args);
                                }
                                else
                                {
                                    BuySellStockFragment.putRequisite(
                                            args,
                                            new AbstractBuySellFragment.Requisite(
                                                    dto.getSecurityId(),
                                                    portfolioCompactDTOs.getDefaultPortfolio().getOwnedPortfolioId(),
                                                    0));
                                    return navigator.get().pushFragment(BuySellStockFragment.class, args);
                                }
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<Fragment>()
                                {
                                    @Override public void call(Fragment o)
                                    {
                                        // Nothing to do
                                    }
                                },
                                new TimberAndToastOnErrorAction1("Failed to listen to clicks"))
        );

        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener()
        {
            @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
            {
                int height = stockGallery.getHeight();
                if (distanceY > SWIPE_MIN_DISTANCE)
                { //scroll up
                    ObjectAnimator animator = ObjectAnimator.ofFloat(stockGallery, "translationY", stockGallery.getTranslationY(), height);
                    animator.setDuration(100);
                    animator.start();
                    stockGallery.setVisibility(View.GONE);
                }
                else if (distanceY < (0 - SWIPE_MIN_DISTANCE))
                { //scroll down
                    ObjectAnimator animator = ObjectAnimator.ofFloat(stockGallery, "translationY", stockGallery.getTranslationY(), 0);
                    animator.setDuration(100);
                    animator.start();
                    stockGallery.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        subscription = AppObservable.bindSupportFragment(this,
                newsServiceWrapper.getSecurityNewsDetailRx(new NewsItemDTOKey(newsId))
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(new Action1<NewsItemDTO>()
                        {
                            @Override public void call(NewsItemDTO newsItemDTO)
                            {
                                if (getLoadingUrl() == null && newsItemDTO.url != null)
                                {
                                    loadUrl(newsItemDTO.url);
                                }
                            }
                        })
                        .observeOn(Schedulers.computation())
                        .flatMap(new Func1<NewsItemDTO, Observable<Map<SecurityIntegerId, SecurityCompactDTO>>>()
                        {
                            @Override public Observable<Map<SecurityIntegerId, SecurityCompactDTO>> call(NewsItemDTO newsItemDTO)
                            {
                                if (newsItemDTO.securityIds == null || newsItemDTO.securityIds.size() == 0)
                                {
                                    return Observable.empty();
                                }
                                ArrayList<SecurityIntegerId> keyList = new ArrayList<>();
                                for (int securityId : newsItemDTO.securityIds)
                                {
                                    keyList.add(new SecurityIntegerId(securityId));
                                }

                                return securityMultiFetchAssistant.get(keyList);
                            }
                        }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Map<SecurityIntegerId, SecurityCompactDTO>>()
                {
                    @Override public void call(Map<SecurityIntegerId, SecurityCompactDTO> map)
                    {

                        securityArrayAdapter.addAll(map.values());
                        stockGallery.setVisibility(View.VISIBLE);
                        webView.setOnTouchListener(new View.OnTouchListener()
                        {
                            @Override public boolean onTouch(View v, MotionEvent event)
                            {
                                return gestureDetector.onTouchEvent(event);
                            }
                        });
                    }
                }, new Action1<Throwable>()
                {
                    @Override public void call(Throwable throwable)
                    {
                        stockGallery.setVisibility(View.GONE);
                        webView.setOnTouchListener(null);
                        Timber.e("Error", throwable);
                    }
                });
    }

    private void adjustFirstItemOfGallery()
    {
        try
        {
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) stockGallery.getLayoutParams();
            mlp.setMargins(-((metrics.widthPixels / 2) + 100),
                    mlp.topMargin,
                    mlp.rightMargin,
                    mlp.bottomMargin
            );
        } catch (Exception e)
        {
            Timber.d("Error", e);
        }
    }

    @Override protected int getLayoutResId()
    {
        return R.layout.fragment_webview_news;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.discovery_news);
    }

    @Override public void onResume()
    {
        super.onResume();
        beginTime = System.currentTimeMillis();
        fragmentElements.getMovableBottom().animateHide();
    }

    @Override public void onPause()
    {
        fragmentElements.getMovableBottom().animateShow();
        reportAnalytics();
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    private void reportAnalytics()
    {
        Map<String, String> collections = new HashMap<>();
        collections.put(AnalyticsConstants.PreviousScreen, previousScreen);
        collections.put(AnalyticsConstants.TimeOnScreen, AnalyticsDuration.sinceTimeMillis(beginTime).toString());
        //TODO Change Analytics
        //analytics.fireEvent(new AttributesEvent(AnalyticsConstants.NewsItem_Show, collections));
    }

    class CompactSecurityListAdapter extends ArrayAdapter<SecurityCompactDTO>
    {

        int layoutID;

        public CompactSecurityListAdapter(Context context, int resource)
        {
            super(context, resource);
            layoutID = resource;
        }

        @Override public View getView(int position, View convertView, ViewGroup parent)
        {
            View itemView = convertView;
            if (convertView == null)
            {
                itemView = getActivity().getLayoutInflater().inflate(layoutID, parent, false);
            }
            SecurityCompactDTO dto = getItem(position);

            TextView symbol = (TextView) itemView.findViewById(R.id.security_symbol);
            TextView latestPrice = (TextView) itemView.findViewById(R.id.latest_price);
            TextView riseRate = (TextView) itemView.findViewById(R.id.rise_rate);
            symbol.setText(dto.getExchangeSymbol());
            latestPrice.setText(formatLastPrice(dto));
            if (dto.risePercent != null)
            {
                double risePercent = dto.risePercent;
                THSignedPercentage
                        .builder(risePercent * 100)
                        .relevantDigitCount(3)
                        .signTypePlusMinusAlways()
                        .build()
                        .into(riseRate);
                riseRate.setTextColor(getResources().getColor(R.color.text_primary_inverse));
                if (risePercent > 0)
                {
                    riseRate.setBackgroundResource(R.drawable.round_label_up);
                }
                else if (risePercent < 0)
                {
                    riseRate.setBackgroundResource(R.drawable.round_label_down);
                }
                else
                {
                    riseRate.setBackgroundColor(Color.TRANSPARENT);
                }
            }

            return itemView;
        }

        @Override public void addAll(Collection<? extends SecurityCompactDTO> collection)
        {
            super.clear();
            super.addAll(collection);
            notifyDataSetChanged();
        }

        Spanned formatLastPrice(SecurityCompactDTO dto)
        {
            return Html.fromHtml(String.format(getActivity().getString(R.string.watchlist_last_price_format),
                    dto.currencyDisplay,
                    dto.lastPrice != null ? PRICE_FORMAT.format(dto.lastPrice) : getString(R.string.na)));
        }
    }
}
