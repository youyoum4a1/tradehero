package com.androidth.general.fragments.position;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewAnimator;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.activities.HelpActivity;
import com.androidth.general.adapters.TypedRecyclerAdapter;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.position.PositionDTOList;
import com.androidth.general.api.position.PositionStatus;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.compact.FxSecurityCompactDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.fragments.base.FragmentOuterElements;
import com.androidth.general.fragments.billing.BasePurchaseManagerFragment;
import com.androidth.general.fragments.position.partial.PositionPartialTopView;
import com.androidth.general.fragments.position.view.PositionNothingView;
import com.androidth.general.fragments.trade.TradeListFragment;
import com.androidth.general.fragments.tutorial.WithTutorial;
import com.androidth.general.persistence.position.PositionListCacheRx;
import com.androidth.general.persistence.security.SecurityCompactCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.ToastOnErrorAction1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SecurityPositionListFragment
        extends BasePurchaseManagerFragment
        implements WithTutorial
{
    private static final String BUNDLE_KEY_SHOWN_USER_ID_BUNDLE = SecurityPositionListFragment.class.getName() + ".userBaseKey";
    private static final String BUNDLE_KEY_FIRST_POSITION_VISIBLE = SecurityPositionListFragment.class.getName() + ".firstPositionVisible";
    private static final String BUNDLE_KEY_SECURITY_ID = SecurityPositionListFragment.class.getName() + ".securityId";

    private static final int FLIPPER_INDEX_LOADING = 0;
    private static final int FLIPPER_INDEX_LIST = 1;
    private static final int FLIPPER_INDEX_ERROR = 2;

    @Inject CurrentUserId currentUserId;
    @Inject SecurityCompactCacheRx securityCompactCacheRx;
    @Inject PositionListCacheRx positionListCacheRx;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject FragmentOuterElements fragmentElements;

    @Bind(R.id.list_flipper) ViewAnimator listViewFlipper;
    @Bind(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeToRefreshLayout;
    @Bind(R.id.position_recycler_view) RecyclerView positionListView;

    protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    protected PortfolioDTO portfolioDTO;
    protected List<Object> viewDTOs;
    protected UserBaseKey shownUser;
    @Nullable protected UserProfileDTO userProfileDTO;

    protected PositionItemAdapter positionItemAdapter;

    private int firstPositionVisible = 0;

    //<editor-fold desc="Arguments Handling">
    public static void putShownUser(@NonNull Bundle args, @NonNull UserBaseKey shownUser)
    {
        args.putBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE, shownUser.getArgs());
    }

    @NonNull private static UserBaseKey getUserBaseKey(@NonNull Bundle args)
    {
        Bundle userBundle = args.getBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE);
        if (userBundle == null)
        {
            throw new NullPointerException("ShownUser needs to be passed on");
        }
        return new UserBaseKey(userBundle);
    }

    public static void putSecurityId(@NonNull Bundle args, @NonNull SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
    }

    @NonNull private static SecurityId getSecurityId(@NonNull Bundle args)
    {
        Bundle securityBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID);
        if (securityBundle == null)
        {
            throw new NullPointerException("SecurityId needs to be passed on");
        }
        return new SecurityId(securityBundle);
    }
    //</editor-fold>

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.positionItemAdapter = new PositionItemAdapter(
                getActivity(),
                getLayoutResIds(),
                currentUserId);
        positionItemAdapter.setOnItemClickedListener(
                new TypedRecyclerAdapter.OnItemClickedListener<Object>()
                {
                    @Override public void onItemClicked(int position, TypedRecyclerAdapter.TypedViewHolder<Object> viewHolder, Object object)
                    {
                        handlePositionItemClicked(viewHolder.itemView, position, object);
                    }
                });
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        securityId = getSecurityId(args);
        if (args.containsKey(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE))
        {
            shownUser = getUserBaseKey(args);
        }
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View inflated = inflater.inflate(R.layout.fragment_positions_list, container, false);
        if (savedInstanceState != null)
        {
            firstPositionVisible = savedInstanceState.getInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
        }
        return inflated;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        positionListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        positionListView.setAdapter(positionItemAdapter);
        swipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                positionListCacheRx.get(securityId);
            }
        });
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchUserProfile();
        fetchPositions();
    }

    @Override public void onPause()
    {
        super.onPause();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_KEY_FIRST_POSITION_VISIBLE, firstPositionVisible);
    }

    @Override public void onDestroyView()
    {
        positionListView.clearOnScrollListeners();
        swipeToRefreshLayout.setOnRefreshListener(null);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.positionItemAdapter = null;
        super.onDestroy();
    }

    @NonNull private Map<Integer, Integer> getLayoutResIds()
    {
        Map<Integer, Integer> layouts = new HashMap<>();
        layouts.put(PositionItemAdapter.VIEW_TYPE_HEADER, R.layout.position_item_header);
        layouts.put(PositionItemAdapter.VIEW_TYPE_PLACEHOLDER, R.layout.position_quick_nothing);
        layouts.put(PositionItemAdapter.VIEW_TYPE_LOCKED, R.layout.position_locked_item);
        layouts.put(PositionItemAdapter.VIEW_TYPE_POSITION, R.layout.position_top_view_in_buy_sell);
        return layouts;
    }

    protected void fetchUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userProfileCache.get(shownUser))
                .subscribe(
                        new Action1<Pair<UserBaseKey, UserProfileDTO>>()
                        {
                            @Override public void call(Pair<UserBaseKey, UserProfileDTO> pair)
                            {
                                linkWith(pair.second);
                            }
                        },
                        new ToastOnErrorAction1(getString(R.string.error_fetch_user_profile))));
    }

    public void linkWith(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
        positionItemAdapter.linkWith(userProfileDTO);
    }

    protected void fetchPositions()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                Observable.combineLatest(
                        securityCompactCacheRx.getOne(securityId)
                                .map(new PairGetSecond<SecurityId, SecurityCompactDTO>()),
                        positionListCacheRx.get(securityId)
                                .map(new PairGetSecond<SecurityId, PositionDTOList>()),
                        new Func2<SecurityCompactDTO, PositionDTOList, List<Object>>()
                        {
                            @Override
                            public List<Object> call(SecurityCompactDTO securityCompactDTO, PositionDTOList positionDTOs)
                            {
                                SecurityPositionListFragment.this.securityCompactDTO = securityCompactDTO;
                                List<Object> viewDtos = new ArrayList<>();
                                if (positionDTOs.isEmpty())
                                {
                                    viewDtos.add(new PositionNothingView.DTO(getResources(), false));
                                }
                                else
                                {
                                    for (PositionDTO positionDTO : positionDTOs)
                                    {
                                        viewDtos.add(new PositionPartialTopView.DTO(
                                                getResources(),
                                                currentUserId,
                                                positionDTO,
                                                securityCompactDTO));
                                    }
                                }
                                return filterViewDTOs(viewDtos);
                            }
                        }))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<Object>>()
                        {
                            @Override public void call(List<Object> dtoList)
                            {
                                SecurityPositionListFragment.this.linkWith(dtoList);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable error)
                            {
                                SecurityPositionListFragment.this.handleGetPositionsError(error);
                            }
                        }));
    }

    @NonNull protected List<Object> filterViewDTOs(@NonNull List<Object> dtoList)
    {
        List<Object> filtered = new ArrayList<>();
        List<Object> longList = new ArrayList<>();
        List<Object> shortList = new ArrayList<>();
        List<Object> closedList = new ArrayList<>();
        for (Object dto : dtoList)
        {
            if (dto instanceof PositionPartialTopView.DTO)
            {
                Boolean isClosed = ((PositionPartialTopView.DTO) dto).positionDTO.isClosed();
                Integer shares = ((PositionPartialTopView.DTO) dto).positionDTO.shares;
                boolean isShort = shares != null && shares < 0;

                if (isClosed != null && isClosed)
                {
                    closedList.add(dto);
                }
                else if (isShort)
                {
                    shortList.add(dto);
                }
                else
                {
                    longList.add(dto);
                }
            }
            else
            {
                filtered.add(dto);
            }
        }

        if (!longList.isEmpty())
        {
            filtered.add(
                    new PositionSectionHeaderItemView.DTO(
                            getResources(),
                            PositionStatus.LONG,
                            getString(securityCompactDTO instanceof FxSecurityCompactDTO
                                    ? R.string.position_list_header_open_long_unsure
                                    : R.string.position_list_header_open_unsure),
                            null,
                            null,
                            PositionSectionHeaderItemView.Type.LONG));
            filtered.addAll(longList);
        }
        if (!shortList.isEmpty())
        {
            filtered.add(new PositionSectionHeaderItemView.DTO(
                    getResources(),
                    PositionStatus.SHORT,
                    getString(R.string.position_list_header_open_short),
                    null,
                    null,
                    PositionSectionHeaderItemView.Type.SHORT));
            filtered.addAll(shortList);
        }
        if (!closedList.isEmpty())
        {
            filtered.add(new PositionSectionHeaderItemView.DTO(
                    getResources(),
                    PositionStatus.CLOSED,
                    getString(R.string.position_list_header_closed_unsure),
                    null,
                    null,
                    PositionSectionHeaderItemView.Type.CLOSED));
            filtered.addAll(closedList);
        }

        return filtered;
    }

    public void handleGetPositionsError(Throwable e)
    {
        if (viewDTOs == null)
        {
            listViewFlipper.setDisplayedChild(FLIPPER_INDEX_ERROR);

            THToast.show(getString(R.string.error_fetch_position_list_info));
            Timber.d(e, "Error fetching the positionList info");
        }
    }

    public void linkWith(@NonNull List<Object> dtoList)
    {
        this.viewDTOs = dtoList;
        positionItemAdapter.addAll(dtoList);
        listViewFlipper.setDisplayedChild(FLIPPER_INDEX_LIST);
        swipeToRefreshLayout.setRefreshing(false);
    }

    protected void handlePositionItemClicked(View view, int position, Object object)
    {
        if (view instanceof PositionPartialTopView)
        {
            Bundle args = new Bundle();
            // By default tries
            TradeListFragment.putPositionDTOKey(args,
                    ((PositionPartialTopView.DTO) object).positionDTO.getPositionDTOKey());
            OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
            if (ownedPortfolioId != null)
            {
                TradeListFragment.putApplicablePortfolioId(args, ownedPortfolioId);
            }
            if (navigator != null)
            {
                navigator.get().pushFragment(TradeListFragment.class, args);
            }
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_help)
    protected void helpBtnClicked(View view)
    {
        HelpActivity.slideInFromRight(getActivity());
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.error)
    protected void handleErrorViewClicked(View view)
    {
        fetchPositions();
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_position_list;
    }
}
