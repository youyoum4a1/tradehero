package com.tradehero.th.fragments.news;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.TextView;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.fragments.base.FragmentOuterElements;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.security.SecurityMultiFetchAssistant;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.AnalyticsDuration;
import com.tradehero.th.utils.metrics.events.AttributesEvent;
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
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NewsWebFragment extends WebViewFragment
{
    private static final String BUNDLE_KEY_PREVIOUS_SCREEN = NewsWebFragment.class + ".previousScreen";
    private static final String BUNDLE_KEY_NEWS_ID = NewsWebFragment.class + ".news.id";

    private static final int SWIPE_MIN_DISTANCE = 30;

    @Inject NewsServiceWrapper newsServiceWrapper;
    @Inject protected SecurityMultiFetchAssistant securityMultiFetchAssistant;
    @Inject Analytics analytics;
    @Inject FragmentOuterElements fragmentElements;

    private String previousScreen;
    private int newsID;
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

    public static void putNewsID(Bundle bundle, Integer id)
    {
        if (id == null)
        {
            return;
        }
        bundle.putInt(BUNDLE_KEY_NEWS_ID, id);
    }

    private int getNewsID()
    {
        if (getArguments() != null)
        {
            return getArguments().getInt(BUNDLE_KEY_NEWS_ID, 0);
        }
        return 0;
    }

    private String getPreviousScreenFromBundle()
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
        newsID = getNewsID();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        stockGallery = (Gallery) view.findViewById(R.id.security_list);
        securityArrayAdapter = new CompactSecurityListAdapter(getActivity(), R.layout.news_compact_stock_info);
        stockGallery.setAdapter(securityArrayAdapter);
        adjustFirstItemOfGallery();
        stockGallery.setVisibility(View.INVISIBLE);

        stockGallery.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                SecurityCompactDTO dto = securityArrayAdapter.getItem(position);
                Bundle args = new Bundle();
                BuySellStockFragment.putSecurityId(args, dto.getSecurityId());
                navigator.get().pushFragment(BuySellStockFragment.class, args);
            }
        });

        webView.getSettings().setBuiltInZoomControls(false);
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

        subscription = AppObservable.bindFragment(this,
                newsServiceWrapper.getSecurityNewsDetailRx(new NewsItemDTOKey(newsID))
                        .flatMap(new Func1<NewsItemDTO, Observable<Map<SecurityIntegerId, SecurityCompactDTO>>>()
                        {
                            @Override public Observable<Map<SecurityIntegerId, SecurityCompactDTO>> call(NewsItemDTO newsItemDTO)
                            {
                                if (newsItemDTO.securityIds == null || newsItemDTO.securityIds.size() == 0)
                                {
                                    return Observable.empty();
                                }
                                ArrayList<SecurityIntegerId> keyList = new ArrayList();
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
        return view;
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
        }
        catch (Exception e)
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
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    private void reportAnalytics()
    {
        Map<String, String> collections = new HashMap<>();
        collections.put(AnalyticsConstants.PreviousScreen, previousScreen);
        collections.put(AnalyticsConstants.TimeOnScreen, AnalyticsDuration.sinceTimeMillis(beginTime).toString());
        analytics.fireEvent(new AttributesEvent(AnalyticsConstants.NewsItem_Show, collections));
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
                double roi = dto.risePercent;
                THSignedPercentage
                        .builder(roi * 100)
                        .relevantDigitCount(3)
                        .signTypePlusMinusAlways()
                        .build()
                        .into(riseRate);
                riseRate.setTextColor(getResources().getColor(R.color.text_primary_inverse));
                if (roi > 0)
                {
                    riseRate.setBackgroundResource(R.drawable.round_label_up);
                }
                else if (roi < 0)
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
                    PRICE_FORMAT.format(dto.lastPrice)));
        }
    }
}
