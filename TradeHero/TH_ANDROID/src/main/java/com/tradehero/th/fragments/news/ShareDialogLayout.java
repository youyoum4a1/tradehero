package com.ayondo.academy.fragments.news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.R;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.models.share.ShareDestination;
import com.ayondo.academy.models.share.ShareDestinationFactory;
import java.util.Comparator;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class ShareDialogLayout extends LinearLayout
{
    @Bind(R.id.news_action_share_title2) protected TextView shareTitleView;
    @Bind(R.id.news_action_share_cancel) protected View cancelView;
    @Bind(R.id.news_action_list_sharing_items) protected ListView listViewSharingOptions;

    @Inject ShareDestinationFactory shareDestinationFactory;
    @Inject @NonNull Comparator<ShareDestination> shareDestinationIndexResComparator;

    @Nullable protected DTO whatToShare;
    @NonNull protected BehaviorSubject<UserAction> shareActionBehavior;

    //<editor-fold desc="Constructors">
    public ShareDialogLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
        shareActionBehavior = BehaviorSubject.create();
    }
    //</editor-fold>

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
        if (!isInEditMode())
        {
            listViewSharingOptions.setAdapter(new ShareDestinationSetAdapter(
                    getContext(),
                    shareDestinationIndexResComparator,
                    shareDestinationFactory.getAllShareDestinations()));
            listViewSharingOptions.setDividerHeight(1);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @NonNull public Observable<UserAction> show(@SuppressWarnings("NullableProblems") @NonNull DTO whatToShare)
    {
        this.whatToShare = whatToShare;
        return shareActionBehavior.asObservable();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.news_action_share_cancel)
    protected void onCancelClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        shareActionBehavior.onNext(new CancelUserAction());
        shareActionBehavior.onCompleted();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(R.id.news_action_list_sharing_items)
    protected void onShareOptionsItemClicked(
            AdapterView<?> parent,
            @SuppressWarnings("UnusedParameters") View view,
            int position,
            @SuppressWarnings("UnusedParameters") long id)
    {
        shareActionBehavior.onNext(new ShareUserAction(
                (ShareDestination) parent.getItemAtPosition(position)));
        shareActionBehavior.onCompleted();
    }

    public interface UserAction
    {
    }

    public static class CancelUserAction implements UserAction
    {
    }

    public static class ShareUserAction implements UserAction
    {
        @NonNull public final ShareDestination shareDestination;

        //<editor-fold desc="Constructors">
        public ShareUserAction(@NonNull ShareDestination shareDestination)
        {
            this.shareDestination = shareDestination;
        }
        //</editor-fold>
    }
}
