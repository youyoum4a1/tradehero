package com.tradehero.th.fragments.fxonboard;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewAnimator;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import rx.observers.EmptyObserver;
import timber.log.Timber;

public class FxOnboardDialogFragment extends BaseDialogFragment
{
    private static final String TAG = FxOnboardDialogFragment.class.getName();

    @InjectView(R.id.view_animator) ViewAnimator viewAnimator;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject protected SecurityPositionDetailCacheRx securityPositionDetailCache;
    private SubscriptionList subscriptionList;
    private CloseListener closeListener;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fx_onboard_dialog, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        subscriptionList = new SubscriptionList();
        Observable.just(viewAnimator)
                .flatMapIterable(animator -> {
                    List<FxOnBoardView<Boolean>> onboardViews = new ArrayList<>();
                    for (int i = 0; i < animator.getChildCount(); ++i)
                    {
                        View child = animator.getChildAt(i);
                        if (child instanceof FxOnBoardView)
                        {
                            @SuppressWarnings("unchecked")
                            FxOnBoardView<Boolean> fxOnBoardView = (FxOnBoardView<Boolean>) child;
                            onboardViews.add(fxOnBoardView);
                        }
                    }
                    return onboardViews;
                })
                .flatMap(FxOnBoardView::result)
                .subscribe(
                        shouldShowNext -> {
                            if (shouldShowNext)
                            {
                                if (viewAnimator.getDisplayedChild() == 1)
                                {
                                    checkFXPortfolio();
                                }
                                viewAnimator.showNext();
                            }
                            else
                            {
                                onCloseClicked();
                            }
                        },
                        throwable -> Timber.e(throwable, "Unable to handle Forex onboard views"));
    }

    private void checkFXPortfolio() {
        subscriptionList.add(AndroidObservable.bindFragment(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .subscribe(new EmptyObserver<Pair<UserBaseKey, UserProfileDTO>>() {
                    @Override
                    public void onNext(Pair<UserBaseKey, UserProfileDTO> args) {
                        if (args.second.fxPortfolio == null) {
                            createFXProtfolio();
                        }
                    }
                }));
    }

    private void createFXProtfolio() {
        subscriptionList.add(AndroidObservable.bindFragment(
                this,
                userProfileCache.get().createFXPortfolio(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptyObserver<PortfolioDTO>() {
                    @Override
                    public void onNext(PortfolioDTO portfolioDTO) {
                        userProfileCache.get().invalidate(currentUserId.toUserBaseKey());
                        securityPositionDetailCache.invalidateAll();
                    }
                }));
    }

    @OnClick(R.id.close)
    public void onCloseClicked()
    {
        dismiss();
        // TODO mark fx onboard handled
    }

    public static FxOnboardDialogFragment showOnBoardDialog(FragmentManager fragmentManager)
    {
        FxOnboardDialogFragment dialogFragment = new FxOnboardDialogFragment();
        dialogFragment.show(fragmentManager, TAG);
        return dialogFragment;
    }

    @Override public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        closeListener.onClose();
    }

    public void setOnCloseListener(CloseListener closeListener)
    {
        this.closeListener = closeListener;
    }

    public interface CloseListener
    {
        void onClose();
    }
}
