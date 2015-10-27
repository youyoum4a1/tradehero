package com.tradehero.th.api.live;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.ViewAnimator;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnRecyclerViewOnScrollListener;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.activities.LiveAccountSettingActivity;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.portfolio.header.LivePortfolioHeaderView;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderView;
import com.tradehero.th.network.service.DummyAyondoLiveServiceWrapper;
import com.tradehero.th.persistence.prefs.IsLiveTrading;
import javax.inject.Inject;
import rx.functions.Action1;

public class LivePositionListFragment extends DashboardFragment
{
    @Bind(R.id.list_flipper) ViewAnimator listViewFlipper;
    @Bind(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeToRefreshLayout;
    @Bind(R.id.position_recycler_view) RecyclerView positionRecyclerView;
    @Bind(R.id.btn_help) ImageView btnHelp;
    @Bind(R.id.position_list_header_stub) ViewStub headerStub;

    @Inject LivePortfolioId livePortfolioId;
    @Inject DummyAyondoLiveServiceWrapper ayondoLiveServiceWrapper;
    @Inject @IsLiveTrading BooleanPreference isLiveTrading;

    public static final int CODE_PROMPT = 1;

    private PortfolioHeaderView portfolioHeaderView;
    private View inflatedView;
    private int headerHeight;

    public LivePositionListFragment()
    {

    }

    @Override public void onStart()
    {
        super.onStart();

        ayondoLiveServiceWrapper.getLivePortfolioDTO(livePortfolioId)
                .subscribe(new Action1<LivePortfolioDTO>()
                {
                    @Override public void call(LivePortfolioDTO livePortfolioDTO)
                    {
                        setUpLiveHeader(livePortfolioDTO);
                    }
                });
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_positions_list, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        positionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        positionRecyclerView.setHasFixedSize(true);
    }

    private void setUpLiveHeader(LivePortfolioDTO livePortfolioDTO)
    {
        int headerLayoutId = PortfolioHeaderFactory.layoutIdForLive();
        headerStub.setLayoutResource(headerLayoutId);
        inflatedView = headerStub.inflate();
        portfolioHeaderView = (PortfolioHeaderView) inflatedView;

        headerHeight = getResources().getDimensionPixelSize(PortfolioHeaderFactory.layoutHeightFor(headerLayoutId));
        inflatedView.postDelayed(new Runnable()
        {
            @Override public void run()
            {
                if (inflatedView == null)
                {
                    return;
                }
                headerHeight = inflatedView.getMeasuredHeight();
                positionRecyclerView.addOnScrollListener(new QuickReturnRecyclerViewOnScrollListener.Builder(QuickReturnViewType.HEADER)
                                .header(inflatedView)
                                .minHeaderTranslation(-headerHeight)
                                .build()
                );
                //if (positionItemAdapter.getItemCount() > 0)
                //{
                //    Object o = positionItemAdapter.getItem(0);
                //    if (o instanceof PositionDummyHeaderDisplayDTO)
                //    {
                //        ((PositionDummyHeaderDisplayDTO) o).headerHeight = headerHeight;
                //        positionItemAdapter.notifyItemChanged(0);
                //    }
                //}

                if (isLiveTrading.get())
                {
                    positionRecyclerView.addOnScrollListener(fragmentElements.get().getRecyclerViewScrollListener());
                }
            }
        }, 300);

        portfolioHeaderView.linkWith(livePortfolioDTO);

        if (portfolioHeaderView instanceof LivePortfolioHeaderView)
        {
            ((LivePortfolioHeaderView)portfolioHeaderView).settingBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    startActivityForResult(new Intent(getActivity(), LiveAccountSettingActivity.class), CODE_PROMPT);
                }
            });
        }
    }
}
