package com.tradehero.th.fragments.fxonboard;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ViewAnimator;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.education.VideoDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.fragments.education.VideoAdapter;
import com.tradehero.th.fragments.education.VideoDTOUtil;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.service.VideoServiceWrapper;
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SingleAttributeEvent;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import rx.observers.EmptyObserver;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class FxOnBoardDialogFragment extends BaseDialogFragment
{
    private static final String TAG = FxOnBoardDialogFragment.class.getName();

    @InjectView(R.id.view_animator) ViewAnimator viewAnimator;
    @InjectView(R.id.introduction_videos_grid) GridView videosGrid;
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(R.id.progress) View progressBar;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject protected SecurityPositionDetailCacheRx securityPositionDetailCache;
    @Inject VideoServiceWrapper videoServiceWrapper;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject Analytics analytics;
    private SubscriptionList subscriptionList;
    private VideoAdapter videoAdapter;
    @NonNull private BehaviorSubject<UserActionType> userActionTypeBehaviorSubject;

    //<editor-fold desc="Constructors">
    public FxOnBoardDialogFragment()
    {
        super();
        userActionTypeBehaviorSubject = BehaviorSubject.create();
    }
    //</editor-fold>

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fx_onboard_dialog, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        videoAdapter = new VideoAdapter(getActivity(), null, R.layout.video_view);
        videosGrid.setAdapter(videoAdapter);

        subscriptionList = new SubscriptionList();
        Observable.just(viewAnimator)
                .flatMapIterable(animator -> {
                    List<FxOnBoardView<Boolean>> onBoardViews = new ArrayList<>();
                    for (int i = 0; i < animator.getChildCount(); ++i)
                    {
                        View child = animator.getChildAt(i);
                        if (child instanceof FxOnBoardView)
                        {
                            @SuppressWarnings("unchecked")
                            FxOnBoardView<Boolean> fxOnBoardView = (FxOnBoardView<Boolean>) child;
                            onBoardViews.add(fxOnBoardView);
                        }
                    }
                    return onBoardViews;
                })
                .flatMap(FxOnBoardView::result)
                .subscribe(
                        shouldShowNext -> {
                            if (shouldShowNext)
                            {
                                if (viewAnimator.getDisplayedChild() == 0)
                                {
                                    analytics.addEvent(new SingleAttributeEvent(AnalyticsConstants.ActivateTradeFX, AnalyticsConstants.ActivateTradeFX, AnalyticsConstants.PageTwo));
                                }
                                else if (viewAnimator.getDisplayedChild() == 1)
                                {
                                    checkFXPortfolio();
                                    analytics.addEvent(new SingleAttributeEvent(AnalyticsConstants.ActivateTradeFX, AnalyticsConstants.ActivateTradeFX, AnalyticsConstants.ActiviteFXTap));
                                    analytics.addEvent(new SingleAttributeEvent(AnalyticsConstants.ActivateTradeFX, AnalyticsConstants.ActivateTradeFX, AnalyticsConstants.PageThr));
                                }
                                viewAnimator.showNext();
                            }
                            else
                            {
                                onCloseClicked();
                            }
                        },
                        throwable -> Timber.e(throwable, "Unable to handle Forex onboard views"));
        subscriptionList.add(AndroidObservable.bindFragment(this, videoServiceWrapper.getFXVideosRx())
                .subscribe(new Subscriber<List<VideoDTO>>()
                {
                    @Override public void onCompleted()
                    {
                        progressBar.setVisibility(View.GONE);
                        if(videoAdapter.isEmpty())
                        {
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override public void onError(Throwable e)
                    {
                        THToast.show(R.string.error_loading_videos);
                    }

                    @Override public void onStart()
                    {
                        super.onStart();
                        progressBar.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }

                    @Override public void onNext(List<VideoDTO> videoDTOs)
                    {
                        videoAdapter.appendHead(videoDTOs);
                        videoAdapter.notifyDataSetChanged();
                    }
                }));
        videosGrid.setOnItemClickListener((parent, view1, position, id) -> {
            VideoDTO videoDTO = videoAdapter.getItem(position);
            VideoDTOUtil.openVideoDTO(getActivity(), navigator.get(), videoDTO);
        });

        analytics.addEvent(new SingleAttributeEvent(AnalyticsConstants.ActivateTradeFX, AnalyticsConstants.ActivateTradeFX, AnalyticsConstants.PageOne));
    }

    @Override public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        userActionTypeBehaviorSubject.onCompleted();
    }

    @NonNull public Observable<UserActionType> getUserActionTypeObservable()
    {
        return userActionTypeBehaviorSubject.asObservable();
    }

    private void checkFXPortfolio()
    {
        notifyUserAction(UserActionType.ENROLLED);
        subscriptionList.add(AndroidObservable.bindFragment(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .subscribe(new EmptyObserver<Pair<UserBaseKey, UserProfileDTO>>()
                {
                    @Override
                    public void onNext(Pair<UserBaseKey, UserProfileDTO> args)
                    {
                        if (args.second.fxPortfolio == null)
                        {
                            createFXPortfolio();
                        }
                    }
                }));
    }

    private void createFXPortfolio()
    {
        subscriptionList.add(AndroidObservable.bindFragment(
                this,
                userServiceWrapper.get().createFXPortfolioRx(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptyObserver<PortfolioDTO>()
                {
                    @Override
                    public void onNext(PortfolioDTO portfolioDTO)
                    {
                        userProfileCache.get().get(currentUserId.toUserBaseKey());
                        securityPositionDetailCache.invalidateAll();
                    }
                }));
    }

    @OnClick(R.id.close)
    public void onCloseClicked()
    {
        notifyUserAction(UserActionType.CANCELLED);
        dismiss();
        // TODO mark fx onboard handled
        analytics.fireEvent(new SingleAttributeEvent(AnalyticsConstants.ActivateTradeFX, AnalyticsConstants.ActivateTradeFX, AnalyticsConstants.PressXTap));
    }

    protected void notifyUserAction(@NonNull UserActionType actionType)
    {
        userActionTypeBehaviorSubject.onNext(actionType);
        userActionTypeBehaviorSubject.onCompleted();
    }

    public static FxOnBoardDialogFragment showOnBoardDialog(FragmentManager fragmentManager)
    {
        FxOnBoardDialogFragment dialogFragment = new FxOnBoardDialogFragment();
        dialogFragment.show(fragmentManager, TAG);
        return dialogFragment;
    }

    public static enum UserActionType
    {
        CANCELLED, ENROLLED
    }
}
