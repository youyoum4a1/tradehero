package com.tradehero.th.api.live;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
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
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.R;
import com.tradehero.th.activities.HelpActivity;
import com.tradehero.th.activities.LiveAccountSettingActivity;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.live.LivePositionListFragmentAlertView;
import com.tradehero.th.fragments.portfolio.header.LivePortfolioHeaderView;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderFactory;
import com.tradehero.th.fragments.portfolio.header.PortfolioHeaderView;
import com.tradehero.th.fragments.position.PositionDummyHeaderDisplayDTO;
import com.tradehero.th.fragments.position.PositionItemAdapter;
import com.tradehero.th.fragments.position.PositionSectionHeaderDisplayDTO;
import com.tradehero.th.fragments.position.partial.PositionCompactDisplayDTO;
import com.tradehero.th.fragments.position.partial.PositionDisplayDTO;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.position.view.PositionNothingView;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.fragments.trade.FXMainFragment;
import com.tradehero.th.fragments.trade.StockActionBarRelativeLayout;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.network.service.DummyAyondoLiveServiceWrapper;
import com.tradehero.th.persistence.prefs.IsLiveTrading;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.rx.dialog.AlertDialogRx;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class LivePositionListFragment extends DashboardFragment
{
    @Bind(R.id.list_flipper) ViewAnimator listViewFlipper;
    @Bind(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeToRefreshLayout;
    @Bind(R.id.position_recycler_view) RecyclerView positionRecyclerView;
    @Bind(R.id.btn_help) ImageView btnHelp;
    @Bind(R.id.position_list_header_stub) ViewStub headerStub;

    @Inject LivePortfolioId livePortfolioId;
    @Inject SecurityIdCache securityIdCache;
    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject DummyAyondoLiveServiceWrapper ayondoLiveServiceWrapper;
    @Inject CurrentUserId currentUserId;
    @Inject @IsLiveTrading BooleanPreference isLiveTrading;

    public static final int CODE_PROMPT = 1;
    private static final int FLIPPER_INDEX_LIST = 1;

    private List<Object> viewDTOs;
    private PositionItemAdapter positionItemAdapter;
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

        ayondoLiveServiceWrapper.getLivePositionsDTO(livePortfolioId).
                subscribe(new Action1<GetPositionsDTO>()
                {
                    @Override public void call(GetPositionsDTO getPositionsDTO)
                    {
                        if (getPositionsDTO.positions != null)
                        {
                            Observable<List<Pair<PositionDTO, SecurityCompactDTO>>> listPairObservable = PositionDTOUtils.getSecuritiesSoft(
                                    Observable.from(getPositionsDTO.positions),
                                    securityIdCache,
                                    securityCompactCache).toList();

                            listPairObservable.flatMap(new Func1<List<Pair<PositionDTO, SecurityCompactDTO>>, Observable<List<Object>>>()
                            {
                                @Override public Observable<List<Object>> call(List<Pair<PositionDTO, SecurityCompactDTO>> pairs)
                                {
                                    List<Object> adapterObjects = new ArrayList<>();

                                    for (Pair<PositionDTO, SecurityCompactDTO> pair : pairs)
                                    {
                                        if (pair.first.isLocked())
                                        {
                                            adapterObjects.add(new PositionLockedView.DTO(getResources(), pair.first));
                                        }
                                        else
                                        {
                                            adapterObjects.add(new PositionDisplayDTO(
                                                    getResources(),
                                                    currentUserId,
                                                    pair.first,
                                                    pair.second));
                                        }
                                    }

                                    return Observable.just(adapterObjects);
                                }
                            }).observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<List<Object>>()
                                    {
                                        @Override public void call(List<Object> objects)
                                        {
                                            linkWith(objects);
                                        }
                                    });
                        }
                    }
                });
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.positionItemAdapter = createPositionItemAdapter();
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

        btnHelp.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                HelpActivity.slideInFromRight(getActivity());
            }
        });
        positionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        positionRecyclerView.setHasFixedSize(true);
        positionRecyclerView.setAdapter(positionItemAdapter);
        positionRecyclerView.addItemDecoration(new TypedRecyclerAdapter.DividerItemDecoration(getActivity(), null, false, false));
        swipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                LivePositionListFragment.this.refreshSimplePage();
            }
        });
    }

    private PositionItemAdapter createPositionItemAdapter()
    {
        PositionItemAdapter adapter = new PositionItemAdapter(
                getActivity(),
                getLayoutResIds(),
                currentUserId
        );
        adapter.setOnItemClickedListener(
                new TypedRecyclerAdapter.OnItemClickedListener<Object>()
                {
                    @Override public void onItemClicked(int position, TypedRecyclerAdapter.TypedViewHolder<Object> viewHolder, Object object)
                    {
                        handlePositionItemClicked(viewHolder.itemView, position, object);
                    }
                });
        adapter.setOnItemLongClickedListener(
                new TypedRecyclerAdapter.OnItemLongClickedListener<Object>()
                {
                    @Override public boolean onItemLongClicked(int position, TypedRecyclerAdapter.TypedViewHolder<Object> viewHolder, Object object)
                    {
                        return handlePositionItemLongClicked(viewHolder.itemView, position, object);
                    }
                }
        );
        return adapter;
    }

    @NonNull private Map<Integer, Integer> getLayoutResIds()
    {
        Map<Integer, Integer> layouts = new HashMap<>();
        layouts.put(PositionItemAdapter.VIEW_TYPE_SECTION_HEADER, R.layout.position_item_header);
        layouts.put(PositionItemAdapter.VIEW_TYPE_PLACEHOLDER, R.layout.position_quick_nothing);
        layouts.put(PositionItemAdapter.VIEW_TYPE_LOCKED, R.layout.position_locked_item);
        layouts.put(PositionItemAdapter.VIEW_TYPE_POSITION, R.layout.position_view);
        return layouts;
    }

    protected void handlePositionItemClicked(View view, int position, Object object)
    {
        if (view instanceof PositionNothingView)
        {
            if (object instanceof PositionNothingView.DTO && ((PositionNothingView.DTO) object).isCurrentUser)
            {
                //pushSecuritiesFragment();
            }
        }

        else
        {
            Bundle args = new Bundle();
            // By default tries
            TradeListFragment.putPositionDTOKey(args,
                    ((PositionDisplayDTO) object).positionDTO.getPositionDTOKey());

            // TODO: change to live portfolio id
            TradeListFragment.putApplicablePortfolioId(args, new OwnedPortfolioId(currentUserId.get(), 7513));

            if (navigator != null)
            {
                navigator.get().pushFragment(TradeListFragment.class, args);
            }
        }
    }

    protected boolean handlePositionItemLongClicked(View view, int position, Object item)
    {
        if (item instanceof PositionDisplayDTO)
        {
            final PositionDisplayDTO positionDisplayDTO = (PositionDisplayDTO) item;
            Boolean isClosed = positionDisplayDTO.positionDTO.isClosed();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            LivePositionListFragmentAlertView alertView =
                    (LivePositionListFragmentAlertView) inflater.inflate(R.layout.live_position_list_fragment_alert_view, null);
            alertView.setImage(getContext(), positionDisplayDTO.stockLogoUrl, positionDisplayDTO.stockLogoRes);
            alertView.setStockNameText(positionDisplayDTO.companyName);
            alertView.setStockSymbolText(positionDisplayDTO.stockSymbol);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext())
                    .setView(alertView)
                    .setCancelable(true)
                    .setNegativeButton(R.string.timeline_trade, new DialogInterface.OnClickListener()
                    {
                        @Override public void onClick(DialogInterface dialog, int which)
                        {
                            handleAlertDialogTradeAndCloseBtn(false, positionDisplayDTO.securityCompactDTO, positionDisplayDTO.positionDTO);
                        }
                    })
                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener()
                    {
                        @Override public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    });

            if (isClosed != null && !isClosed)
            {
                alertDialogBuilder.setPositiveButton(R.string.position_close_position_action, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialog, int which)
                    {
                        handleAlertDialogTradeAndCloseBtn(true, positionDisplayDTO.securityCompactDTO, positionDisplayDTO.positionDTO);
                    }
                });
            }

            alertDialogBuilder.show();

            return true;
        }

        return false;
    }

    private void handleAlertDialogTradeAndCloseBtn(boolean andClose, @NonNull SecurityCompactDTO securityCompactDTO, @NonNull PositionDTO positionDTO)
    {
        Bundle args = new Bundle();
        BuySellStockFragment.putRequisite(
                args,
                new BuySellStockFragment.Requisite(
                        securityCompactDTO.getSecurityId(),
                        new OwnedPortfolioId(currentUserId.get(), 7513),
                        andClose && positionDTO.shares != null ? positionDTO.shares : 0));

        navigator.get().pushFragment(BuySellStockFragment.class, args);
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
                if (positionItemAdapter.getItemCount() > 0)
                {
                    Object o = positionItemAdapter.getItem(0);
                    if (o instanceof PositionDummyHeaderDisplayDTO)
                    {
                        ((PositionDummyHeaderDisplayDTO) o).headerHeight = headerHeight;
                        positionItemAdapter.notifyItemChanged(0);
                    }
                }

                if (isLiveTrading.get())
                {
                    positionRecyclerView.addOnScrollListener(fragmentElements.get().getRecyclerViewScrollListener());
                }
            }
        }, 300);

        portfolioHeaderView.linkWith(livePortfolioDTO);

        if (portfolioHeaderView instanceof LivePortfolioHeaderView)
        {
            ((LivePortfolioHeaderView) portfolioHeaderView).settingBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    startActivityForResult(new Intent(getActivity(), LiveAccountSettingActivity.class), CODE_PROMPT);
                }
            });
        }
    }

    private void linkWith(@NonNull List<Object> dtoList)
    {
        this.viewDTOs = dtoList;

        //Add the header of empty object to the list
        this.viewDTOs.add(new PositionDummyHeaderDisplayDTO(headerHeight));

        Object nothingDTO = new PositionNothingView.DTO(getResources(), true);
        if (this.viewDTOs.size() > 0 && positionItemAdapter.indexOf(nothingDTO) != RecyclerView.NO_POSITION)
        {
            positionItemAdapter.remove(nothingDTO);
        }
        else if (this.viewDTOs.isEmpty())
        {
            this.viewDTOs.add(nothingDTO);
            positionItemAdapter.removeAll();
        }

        if (this.viewDTOs.size() > 0)
        {
            boolean hasPending = false, hasLong = false, hasShort = false, hasClosed = false;

            for (Object object : dtoList)
            {
                if (object instanceof PositionCompactDisplayDTO)
                {
                    PositionStatus status = ((PositionCompactDisplayDTO) object).positionDTO.positionStatus;
                    if (status != null)
                    {
                        switch (status)
                        {
                            case PENDING:
                                hasPending = true;
                                break;
                            case SHORT:
                                hasShort = true;
                                break;
                            case LONG:
                                hasLong = true;
                                break;
                            case CLOSED:
                            case FORCE_CLOSED:
                                hasClosed = true;
                                break;
                        }
                    }
                }
            }

            if (hasPending)
            {
                this.viewDTOs.add(new PositionSectionHeaderDisplayDTO(PositionStatus.PENDING,
                        getString(R.string.position_list_header_pending),
                        PositionSectionHeaderDisplayDTO.Type.PENDING));
            }

            if (hasLong)
            {
                this.viewDTOs.add(
                        new PositionSectionHeaderDisplayDTO(PositionStatus.LONG, getString(R.string.position_list_header_open_long),
                                PositionSectionHeaderDisplayDTO.Type.LONG));
            }

            if (hasShort)
            {
                this.viewDTOs.add(new PositionSectionHeaderDisplayDTO(PositionStatus.SHORT,
                        getString(R.string.position_list_header_open_short),
                        PositionSectionHeaderDisplayDTO.Type.SHORT));
            }

            if (hasClosed)
            {
                this.viewDTOs.add(
                        new PositionSectionHeaderDisplayDTO(PositionStatus.CLOSED, getString(R.string.position_list_header_closed),
                                PositionSectionHeaderDisplayDTO.Type.CLOSED));
            }
        }

        positionItemAdapter.addAll(this.viewDTOs);
        swipeToRefreshLayout.setRefreshing(false);
        if (listViewFlipper.getDisplayedChild() != FLIPPER_INDEX_LIST)
        {
            listViewFlipper.setDisplayedChild(FLIPPER_INDEX_LIST);
        }
    }

    private void refreshSimplePage()
    {
        //getPositionsCache.invalidate(getPositionsDTOKey);
        //getPositionsCache.get(getPositionsDTOKey);
    }
}
