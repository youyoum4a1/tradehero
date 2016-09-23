package com.androidth.general.fragments.discussion.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.androidth.general.api.discussion.key.DiscussionKey;
import com.androidth.general.api.pagination.PaginatedDTO;
import com.androidth.general.common.fragment.HasSelectedItem;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.discussion.AbstractDiscussionCompactItemViewLinearDTOFactory;
import com.androidth.general.fragments.discussion.DiscussionFragmentUtil;
import com.androidth.general.fragments.discussion.MentionActionButtonsView;
import com.androidth.general.fragments.discussion.MentionTaggedStockHandler;
import com.androidth.general.fragments.discussion.PostCommentView;
import com.androidth.general.fragments.security.AbstractSecurityInfoFragment;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.discussion.UserDiscussionAction;
import com.androidth.general.persistence.discussion.DiscussionCacheRx;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.androidth.general.R;
import com.androidth.general.api.discussion.AbstractDiscussionCompactDTO;
import com.androidth.general.api.discussion.DiscussionDTO;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.key.DiscussionListKey;
import com.androidth.general.api.discussion.key.PaginatedDiscussionListKey;
import com.androidth.general.api.discussion.key.SecurityDiscussionKey;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.androidth.general.fragments.discussion.AbstractDiscussionFragment;
import com.androidth.general.fragments.discussion.DiscussionSetAdapter;
import com.androidth.general.fragments.discussion.SecurityDiscussionEditPostFragment;
import com.androidth.general.fragments.discussion.SingleViewDiscussionSetAdapter;
import com.androidth.general.persistence.discussion.DiscussionListCacheRx;
import com.androidth.general.persistence.security.SecurityCompactCacheRx;
import com.androidth.general.widget.MultiScrollListener;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class SecurityDiscussionFragment extends Fragment
{
    private static final String BUNDLE_KEY_SECURITY_ID = "securityId";

    @Inject SecurityCompactCacheRx securityCompactCache;
    @Bind(R.id.security_discussion_add) View buttonAdd;
    private SecurityId securityId;

    @Inject Lazy<DashboardNavigator> navigator;
    @Inject DiscussionFragmentUtil discussionFragmentUtil;
    @Inject AbstractDiscussionCompactItemViewLinearDTOFactory viewDTOFactory;


    @Bind(android.R.id.list) protected ListView discussionList;
    @Inject DiscussionListCacheRx discussionListCache;
    @Inject protected MentionTaggedStockHandler mentionTaggedStockHandler;
    @Inject protected DiscussionCacheRx discussionCache;
    @Bind(R.id.post_comment_text) @Nullable protected EditText postCommentText;
    @Bind(R.id.mention_widget) @Nullable protected MentionActionButtonsView mentionActionButtonsView;
    @Bind(R.id.discussion_comment_widget) @Nullable protected PostCommentView postCommentView;
    protected DiscussionSetAdapter discussionListAdapter;
    protected DiscussionKey discussionKey;
    private PublishSubject<Boolean> nearEdgeSubject; // True means start reached, False means end reached
    private Subscription hasSelectedSubscription;

    private SubscriptionList onDestroyViewSubscriptions, onStopSubscriptions;

    public static void putSecurityId(Bundle args, SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
    }

    @Nullable public static SecurityId getSecurityId(Bundle args)
    {
        SecurityId extracted = null;
        if (args != null)
        {
            Bundle securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID);
            if (securityIdBundle != null)
            {
                extracted = new SecurityId(securityIdBundle);
            }
        }
        return extracted;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        securityId = getSecurityId(getArguments());
        nearEdgeSubject = PublishSubject.create();

        onStopSubscriptions = new SubscriptionList();
        onDestroyViewSubscriptions = new SubscriptionList();

        discussionListAdapter = new SingleViewDiscussionSetAdapter(
                getActivity(),
                R.layout.security_discussion_item_view);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.security_discussion, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        discussionList.setOnScrollListener(
                new MultiScrollListener(
                        new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.HEADER).header(buttonAdd)
                                .minHeaderTranslation(-getResources().getDimensionPixelSize(R.dimen.clickable_element_min_dimen))
                                .build()));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            HierarchyInjector.inject(context, this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchDiscussionList();
        registerUserActions();
    }

    @Override public void onDestroy()
    {
        //invalidate cache
        if (discussionListCache != null)
        {
            discussionListCache.invalidateAllForDiscussionType(DiscussionType.SECURITY);
        }

        mentionTaggedStockHandler.setHasSelectedItemFragment(null);
        mentionTaggedStockHandler = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        if(hasSelectedSubscription!=null){
            hasSelectedSubscription.unsubscribe();
        }

        onDestroyViewSubscriptions.unsubscribe();
        hasSelectedSubscription = null;
        mentionTaggedStockHandler.setDiscussionPostContent(null);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.security_discussion_add) void onAddNewDiscussionRequested(View clickedView)
    {
        if (securityId != null)
        {
            Bundle bundle = new Bundle();
            SecurityDiscussionEditPostFragment.putSecurityId(bundle, securityId);
            navigator.get().pushFragment(SecurityDiscussionEditPostFragment.class, bundle);
        }
    }

//    @NonNull @Override protected DiscussionSetAdapter createDiscussionListAdapter()
//    {
//        return new SingleViewDiscussionSetAdapter(
//                getActivity(),
//                R.layout.security_discussion_item_view);
//    }

//    @NonNull @Override protected AbsListView.OnScrollListener createListScrollListener()
//    {
//        if(fragmentElements!=null){
//            return new MultiScrollListener(super.createListScrollListener(), fragmentElements.getListViewScrollListener(),
//                    new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.HEADER).header(buttonAdd)
//                            .minHeaderTranslation(-getResources().getDimensionPixelSize(R.dimen.clickable_element_min_dimen))
//                            .build());
//        }else{
//            return new MultiScrollListener(super.createListScrollListener(),
//                    new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.HEADER).header(buttonAdd)
//                            .minHeaderTranslation(-getResources().getDimensionPixelSize(R.dimen.clickable_element_min_dimen))
//                            .build());
//        }
//    }
//
//    @NonNull @Override protected Observable<AbstractDiscussionCompactDTO> getTopicObservable()
//    {
//        return Observable.empty();
//    }
//
//    @Nullable @Override protected View inflateTopicView(@NonNull AbstractDiscussionCompactDTO topicDiscussion)
//    {
//        return null;
//    }

    private Observable<DiscussionListKey> createTopicDiscussionListKey()
    {
        return securityCompactCache.getOne(securityId)
                .map(new Func1<Pair<SecurityId, SecurityCompactDTO>, DiscussionListKey>()
                {
                    @Override public DiscussionListKey call(Pair<SecurityId, SecurityCompactDTO> pair)
                    {
                        discussionKey = new SecurityDiscussionKey(pair.second.id);
                        discussionList.setAdapter(discussionListAdapter);

                        try{
                            mentionTaggedStockHandler.setDiscussionPostContent(postCommentText);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        subscribeHasSelected();
                        if (postCommentView != null)
                        {
                            postCommentView.linkWith(discussionKey);
                            postCommentView.setCommentPostedListener(createCommentPostedListener());
//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) postCommentView.getLayoutParams();
//            if(fragmentElements!=null){
//                params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, fragmentElements.getMovableBottom().getHeight());
//            }else{
//                params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, 10);
//            }
//
//            postCommentView.setLayoutParams(params);
                        }

                        if (mentionActionButtonsView != null)
                        {
                            mentionActionButtonsView.setReturnFragmentName(getClass().getName());
                        }

                        return new PaginatedDiscussionListKey(new DiscussionListKey(DiscussionType.SECURITY, pair.second.id), 1);
                    }
                });
    }

    @NonNull
    private Observable<AbstractDiscussionCompactItemViewLinear.DTO> createViewDTO(@NonNull AbstractDiscussionCompactDTO discussion)
    {
        return viewDTOFactory.createDiscussionItemViewLinearDTO((DiscussionDTO) discussion);
    }


    private void fetchDiscussionList()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                fetchAndCreateDTOs(createTopicDiscussionListKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                        {
                            @Override public void call(
                                    Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>> viewDTOsPair)
                            {
                                discussionListAdapter.appendTail(viewDTOsPair.second);
                                discussionListAdapter.notifyDataSetChanged();
                                fetchNextDiscussionList(
                                        viewDTOsPair.first,
                                        viewDTOsPair.second);
                                fetchMostRecentDiscussionList(
                                        viewDTOsPair.first,
                                        viewDTOsPair.second);
                            }
                        },
                        new TimberAndToastOnErrorAction1("Failed to load discussion")));
    }

    private void fetchNextDiscussionList(
            @NonNull DiscussionListKey latestKey,
            @NonNull List<AbstractDiscussionCompactItemViewLinear.DTO> latestDtos)
    {
        if (!latestDtos.isEmpty())
        {
            DiscussionListKey next = getNextKey(latestKey, latestDtos);
            if (next != null)
            {
                onStopSubscriptions.add(AppObservable.bindSupportFragment(
                        this,
                        fetchAndCreateDTOs(
                                Observable.just(next)
                                        .delay(new Func1<DiscussionListKey, Observable<Boolean>>()
                                        {
                                            @Override public Observable<Boolean> call(DiscussionListKey paginatedDiscussionListKey)
                                            {
                                                // Waiting for the flag to raise
                                                return nearEdgeSubject.filter(
                                                        new Func1<Boolean, Boolean>()
                                                        {
                                                            @Override public Boolean call(Boolean startReached)
                                                            {
                                                                return !startReached;
                                                            }
                                                        });
                                            }
                                        })))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                                {
                                    @Override
                                    public void call(
                                            Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>> viewDTOsPair)
                                    {
                                        discussionListAdapter.appendTail(viewDTOsPair.second);
                                        discussionListAdapter.notifyDataSetChanged();
                                        fetchNextDiscussionList(
                                                viewDTOsPair.first,
                                                viewDTOsPair.second);
                                    }
                                },
                                new ToastOnErrorAction1()));
            }
        }
    }

    @NonNull private Observable<Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>>>
    fetchAndCreateDTOs(@NonNull Observable<DiscussionListKey> keyObservable)
    {
        return keyObservable
                .flatMap(new Func1<DiscussionListKey, Observable<Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>>>>()
                {
                    @Override
                    public Observable<Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>>> call(DiscussionListKey discussionListKey)
                    {
                        return discussionListCache.get(discussionListKey).subscribeOn(Schedulers.computation());
                    }
                })
                .flatMap(new Func1<
                        Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>>,
                        Observable<Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>>>>()
                {
                    @Override
                    public Observable<Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>>> call(
                            final Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>> pair)
                    {
                        return createViewDTOs(pair.second.getData())
                                .map(new Func1<List<AbstractDiscussionCompactItemViewLinear.DTO>,
                                        Pair<DiscussionListKey,
                                                List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                                {
                                    @Override
                                    public Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>> call(
                                            List<AbstractDiscussionCompactItemViewLinear.DTO> dtos)
                                    {
                                        return Pair.create(pair.first, dtos);
                                    }
                                });
                    }
                });
    }

    @NonNull private Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>> createViewDTOs(
            @NonNull List<DiscussionDTO> discussions)
    {
        return Observable.from(discussions)
                .flatMap(new Func1<DiscussionDTO, Observable<AbstractDiscussionCompactItemViewLinear.DTO>>()
                {
                    @Override
                    public Observable<AbstractDiscussionCompactItemViewLinear.DTO> call(final DiscussionDTO discussion)
                    {
                        return createViewDTO(discussion);
                    }
                })
                .toList();
    }

    @Nullable private DiscussionListKey getNextKey(
            @NonNull DiscussionListKey latestKey,
            @NonNull List<AbstractDiscussionCompactItemViewLinear.DTO> latestDtos)
    {
        return ((PaginatedDiscussionListKey) latestKey).next();
    }

    private void fetchMostRecentDiscussionList(@NonNull DiscussionListKey latestKey,
                                                 @NonNull List<AbstractDiscussionCompactItemViewLinear.DTO> newestDtos)
    {
        DiscussionListKey prev = getMostRecentKey(latestKey, newestDtos);
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                fetchAndCreateDTOs(
                        Observable.just(prev)
                                .delay(new Func1<DiscussionListKey, Observable<Boolean>>()
                                {
                                    @Override public Observable<Boolean> call(DiscussionListKey paginatedDiscussionListKey)
                                    {
                                        // Waiting for the flag to raise
                                        return nearEdgeSubject.filter(
                                                new Func1<Boolean, Boolean>()
                                                {
                                                    @Override public Boolean call(Boolean startReached)
                                                    {
                                                        return !startReached;
                                                    }
                                                });
                                    }
                                })))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                        {
                            @Override
                            public void call(
                                    Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>> viewDTOsPair)
                            {
                                discussionListAdapter.appendHead(viewDTOsPair.second);
                                discussionListAdapter.notifyDataSetChanged();
                                fetchMostRecentDiscussionList(viewDTOsPair.first, viewDTOsPair.second);
                            }
                        },
                        new ToastOnErrorAction1()));
    }

    @Nullable private DiscussionListKey getMostRecentKey(
            @NonNull DiscussionListKey latestKey,
            @NonNull List<AbstractDiscussionCompactItemViewLinear.DTO> newestDtos)
    {
        return new PaginatedDiscussionListKey(latestKey, 1);
    }

    private void registerUserActions()
    {
        onStopSubscriptions.add(getUserActionObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserDiscussionAction>()
                        {
                            @Override public void call(UserDiscussionAction userDiscussionAction)
                            {
                                discussionFragmentUtil.handleUserAction(getActivity(), userDiscussionAction);
                            }
                        },
                        new ToastOnErrorAction1()));
    }

    @NonNull private Observable<UserDiscussionAction> getUserActionObservable()
    {
        return Observable.merge(
                discussionListAdapter.getUserActionObservable(),
                getTopicViewObservable()
                        .flatMap(new Func1<View, Observable<UserDiscussionAction>>()
                        {
                            @Override public Observable<UserDiscussionAction> call(@Nullable View topicView)
                            {
                                if (topicView instanceof AbstractDiscussionCompactItemViewLinear)
                                {
                                    return ((AbstractDiscussionCompactItemViewLinear) topicView).getUserActionObservable();
                                }
                                return Observable.empty();
                            }
                        }))
                .share();
    }

    //<editor-fold desc="Topic">
    @NonNull private Observable<AbstractDiscussionCompactDTO> getTopicObservable()
    {
        return discussionCache.getOne(discussionKey)
                .map(new PairGetSecond<DiscussionKey, AbstractDiscussionCompactDTO>())
                .share();
    }

    @NonNull @RxLogObservable private Observable<View> getTopicViewObservable() // It can pass null values
    {
        return getTopicObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<AbstractDiscussionCompactDTO, View>()
                {
                    @Override public View call(AbstractDiscussionCompactDTO topicDiscussion)
                    {
//                        return inflateTopicView(topicDiscussion);
                        return null;
                    }
                })
                .share();
    }

    private void subscribeHasSelected()
    {
        if(hasSelectedSubscription!=null){
            hasSelectedSubscription.unsubscribe();
        }

        if (mentionActionButtonsView != null)
        {
            hasSelectedSubscription = mentionActionButtonsView.getSelectedItemObservable()
                    .subscribe(
                            new Action1<HasSelectedItem>()
                            {
                                @Override public void call(HasSelectedItem hasSelectedItem)
                                {
                                    mentionTaggedStockHandler.setHasSelectedItemFragment(hasSelectedItem);
                                }
                            },
                            new ToastOnErrorAction1());
        }
    }

    private PostCommentView.CommentPostedListener createCommentPostedListener()
    {
        return new AbstractDiscussionCommentPostedListener();
    }

    private class AbstractDiscussionCommentPostedListener implements PostCommentView.CommentPostedListener
    {
        @Override public void success(DiscussionDTO discussionDTO)
        {
//            handleCommentPosted(discussionDTO);
        }

        @Override public void failure(Exception exception)
        {
            // Nothing to do
        }
    }

}
