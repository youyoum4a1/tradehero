package com.tradehero.th.fragments.chinabuild.fragment.portfolio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PositionTradeListAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOKey;
import com.tradehero.th.api.position.PositionDTOKeyFactory;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.trade.TradeListCache;
import com.tradehero.th.utils.DateUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

/*
    单个Position的交易详情
 */
public class PositionDetailFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE =
            PositionDetailFragment.class.getName() + ".purchaseApplicablePortfolioId";
    private static final String BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE = PositionDetailFragment.class.getName() + ".positionDTOKey";

    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<TradeListCache> tradeListCache;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject PositionDTOKeyFactory positionDTOKeyFactory;

    protected PositionDTOKey positionDTOKey;
    protected DTOCacheNew.Listener<PositionDTOKey, PositionDTO> fetchPositionListener;
    protected PositionDTO positionDTO;
    protected TradeDTOList tradeDTOList;
    private DTOCacheNew.Listener<OwnedPositionId, TradeDTOList> fetchTradesListener;

    @InjectView(R.id.tvPositionTotalCcy) TextView tvPositionTotalCcy;//累计盈亏
    @InjectView(R.id.tvPositionSumAmont) TextView tvPositionSumAmont;//总投资
    @InjectView(R.id.tvPositionStartTime) TextView tvPositionStartTime;//建仓时间
    @InjectView(R.id.tvPositionLastTime) TextView tvPositionLastTime;//最后交易
    @InjectView(R.id.tvPositionHoldTime) TextView tvPositionHoldTime;//持有时间

    @InjectView(R.id.listTrade) SecurityListView listView;
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;

    private PositionTradeListAdapter adapter;

    public static void putApplicablePortfolioId(@NotNull Bundle args, @NotNull OwnedPortfolioId ownedPortfolioId)
    {
        args.putBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
    }

    public static void putPositionDTOKey(@NotNull Bundle args, @NotNull PositionDTOKey positionDTOKey)
    {
        args.putBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE, positionDTOKey.getArgs());
    }

    @NotNull private static PositionDTOKey getPositionDTOKey(@NotNull Bundle args, @NotNull PositionDTOKeyFactory positionDTOKeyFactory)
    {
        return positionDTOKeyFactory.createFrom(args.getBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE));
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        fetchPositionListener = createPositionCacheListener();
        fetchTradesListener = createTradeListeCacheListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("交易详情");
        setHeadViewRight0("行情");
    }

    @Override public void onClickHeadRight0()
    {
        Timber.d("进入行情页面");
        Bundle bundle = new Bundle();
        bundle.putAll(getArguments());
        pushFragment(SecurityDetailFragment.class, bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.position_detail_fragment, container, false);
        ButterKnife.inject(this, view);

        initListView();

        //if (adapter.getCount() == 0)
        //{
        //    betterViewAnimator.setDisplayedChildByLayoutId(R.id.progress);
        //}
        //else
        //{
        betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTrade);
        //}

        return view;
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    public void initListView()
    {
        adapter = new PositionTradeListAdapter(getActivity());
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
    }

    @Override public void onDestroyView()
    {
        detachFetchPosition();
        detachFetchTrades();

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
        linkWith(getPositionDTOKey(getArguments(), positionDTOKeyFactory));
    }

    public void linkWith(@NotNull PositionDTOKey newPositionDTOKey)
    {
        this.positionDTOKey = newPositionDTOKey;
        fetchPosition();
    }

    protected void detachFetchPosition()
    {
        positionCache.get().unregister(fetchPositionListener);
    }

    protected void detachFetchTrades()
    {
        tradeListCache.get().unregister(fetchTradesListener);
    }

    protected void fetchPosition()
    {
        detachFetchPosition();
        positionCache.get().register(positionDTOKey, fetchPositionListener);
        positionCache.get().getOrFetchAsync(positionDTOKey);
    }

    protected DTOCacheNew.Listener<PositionDTOKey, PositionDTO> createPositionCacheListener()
    {
        return new TradeListFragmentPositionCacheListener();
    }

    protected class TradeListFragmentPositionCacheListener implements DTOCacheNew.Listener<PositionDTOKey, PositionDTO>
    {
        @Override public void onDTOReceived(@NotNull PositionDTOKey key, @NotNull PositionDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull PositionDTOKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_position_list_info);
        }
    }

    public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
        fetchTrades();
        displayPosition(positionDTO);
    }

    public void displayPosition(PositionDTO positionDTO)
    {
        THSignedNumber roi = THSignedPercentage.builder(positionDTO.getROISinceInception() * 100)
                .withSign()
                .signTypeArrow()
                .build();
        tvPositionTotalCcy.setTextColor(getResources().getColor(roi.getColorResId()));
        tvPositionTotalCcy.setText("" + positionDTO.getTotalScoreOfTrade() + "(" + roi.toString() + ")");
        tvPositionSumAmont.setText("$" + Math.round(positionDTO.sumInvestedAmountRefCcy));
        tvPositionStartTime.setText(DateUtils.getFormattedDate(getResources(), positionDTO.earliestTradeUtc));
        tvPositionLastTime.setText(DateUtils.getFormattedDate(getResources(), positionDTO.latestTradeUtc));
        tvPositionHoldTime.setText(getResources().getString(R.string.position_hold_days,
                DateUtils.getNumberOfDaysBetweenDates(positionDTO.earliestTradeUtc, positionDTO.getLatestHoldDate())));
    }

    protected void fetchTrades()
    {
        if (positionDTO != null)
        {
            detachFetchTrades();
            OwnedPositionId key = positionDTO.getOwnedPositionId();
            tradeListCache.get().register(key, fetchTradesListener);
            tradeListCache.get().getOrFetchAsync(key);
        }
    }

    protected TradeListCache.Listener<OwnedPositionId, TradeDTOList> createTradeListeCacheListener()
    {
        return new GetTradesListener();
    }

    private class GetTradesListener implements TradeListCache.Listener<OwnedPositionId, TradeDTOList>
    {
        @Override public void onDTOReceived(@NotNull OwnedPositionId key, @NotNull TradeDTOList tradeDTOs)
        {

            linkWith(tradeDTOs, true);
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull OwnedPositionId key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_trade_list_info);
            onFinish();
        }

        public void onFinish()
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTrade);
        }
    }

    public void linkWith(TradeDTOList tradeDTOs, boolean andDisplay)
    {
        Timber.d("Tradehero: PositionDetailFragment LinkWith");
        this.tradeDTOList = tradeDTOs;
        adapter.setTradeList(tradeDTOList);
    }
}
