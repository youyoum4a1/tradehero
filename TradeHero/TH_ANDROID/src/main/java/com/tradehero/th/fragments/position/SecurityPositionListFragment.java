package com.tradehero.th.fragments.position;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewAnimator;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.HelpActivity;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.FragmentOuterElements;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionNothingView;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.persistence.position.PositionListCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.ToastAction;
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

    @InjectView(R.id.list_flipper) ViewAnimator listViewFlipper;
    @InjectView(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeToRefreshLayout;
    @InjectView(R.id.position_list) ListView positionListView;

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
        return new UserBaseKey(args.getBundle(BUNDLE_KEY_SHOWN_USER_ID_BUNDLE));
    }

    public static void putSecurityId(@NonNull Bundle args, @NonNull SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
    }

    @NonNull private static SecurityId getSecurityId(@NonNull Bundle args)
    {
        return new SecurityId(args.getBundle(BUNDLE_KEY_SECURITY_ID));
    }
    //</editor-fold>

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.positionItemAdapter = new PositionItemAdapter(
                activity,
                getLayoutResIds(),
                currentUserId);
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
        ButterKnife.inject(this, view);
        positionListView.setAdapter(positionItemAdapter);
        positionListView.setPadding(
                positionListView.getPaddingLeft(),
                0,
                positionListView.getPaddingRight(),
                positionListView.getPaddingBottom());
        positionListView.setOnScrollListener(fragmentElements.getListViewScrollListener());
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
        firstPositionVisible = positionListView.getFirstVisiblePosition();
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
        positionListView.setOnScrollListener(null);
        swipeToRefreshLayout.setOnRefreshListener(null);
        ButterKnife.reset(this);
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
        layouts.put(PositionItemAdapter.VIEW_TYPE_OPEN_LONG, R.layout.position_top_view);
        layouts.put(PositionItemAdapter.VIEW_TYPE_OPEN_SHORT, R.layout.position_top_view);
        layouts.put(PositionItemAdapter.VIEW_TYPE_CLOSED, R.layout.position_top_view);
        return layouts;
    }

    protected void fetchUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
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
                        new ToastAction<Throwable>(getString(R.string.error_fetch_user_profile))));
    }

    public void linkWith(UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
        positionItemAdapter.linkWith(userProfileDTO);
    }

    protected void fetchPositions()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
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
                                        viewDtos.add(new PositionPartialTopView.DTO(getResources(), positionDTO, securityCompactDTO));
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
                            getString(securityCompactDTO instanceof FxSecurityCompactDTO
                                    ? R.string.position_list_header_open_long_unsure
                                    : R.string.position_list_header_open_unsure),
                            null,
                            null,
                            PositionSectionHeaderItemView.INFO_TYPE_LONG));
            filtered.addAll(longList);
        }
        if (!shortList.isEmpty())
        {
            filtered.add(new PositionSectionHeaderItemView.DTO(
                    getResources(),
                    getString(R.string.position_list_header_open_short),
                    null,
                    null,
                    PositionSectionHeaderItemView.INFO_TYPE_SHORT));
            filtered.addAll(shortList);
        }
        if (!closedList.isEmpty())
        {
            filtered.add(new PositionSectionHeaderItemView.DTO(
                    getResources(),
                    getString(R.string.position_list_header_closed_unsure),
                    null,
                    null,
                    PositionSectionHeaderItemView.INFO_TYPE_CLOSED));
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
        positionItemAdapter.setNotifyOnChange(false);
        positionItemAdapter.clear();
        positionItemAdapter.addAll(dtoList);
        positionItemAdapter.setNotifyOnChange(true);
        positionItemAdapter.notifyDataSetChanged();
        swipeToRefreshLayout.setRefreshing(false);
        listViewFlipper.setDisplayedChild(FLIPPER_INDEX_LIST);
        positionListView.smoothScrollToPosition(0);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(R.id.position_list)
    protected void handlePositionItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        if (view instanceof PositionPartialTopView)
        {
            Bundle args = new Bundle();
            // By default tries
            TradeListFragment.putPositionDTOKey(args,
                    ((PositionPartialTopView.DTO) parent.getItemAtPosition(position)).positionDTO.getPositionDTOKey());
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
        Intent intent = new Intent(getActivity(), HelpActivity.class);
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeCustomAnimation(getActivity(), R.anim.slide_right_in, R.anim.slide_left_out);
        ActivityCompat.startActivity(getActivity(), intent, optionsCompat.toBundle());
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
