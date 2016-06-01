package com.ayondo.academy.fragments.discussion;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnTextChanged;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.utils.EditableUtil;
import com.tradehero.common.utils.THToast;
import com.ayondo.academy.R;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.api.discussion.DiscussionType;
import com.ayondo.academy.api.discussion.form.DiscussionFormDTO;
import com.ayondo.academy.api.discussion.form.DiscussionFormDTOFactory;
import com.ayondo.academy.api.discussion.form.ReplyDiscussionFormDTO;
import com.ayondo.academy.api.discussion.key.DiscussionKey;
import com.ayondo.academy.api.discussion.key.DiscussionKeyFactory;
import com.ayondo.academy.api.news.NewsItemDTO;
import com.ayondo.academy.api.share.wechat.WeChatDTOFactory;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.fragments.base.DashboardFragment;
import com.ayondo.academy.misc.exception.THException;
import com.ayondo.academy.network.service.DiscussionServiceWrapper;
import com.ayondo.academy.network.share.SocialSharer;
import com.ayondo.academy.network.share.dto.SocialShareResult;
import com.ayondo.academy.persistence.discussion.DiscussionCacheRx;
import com.ayondo.academy.persistence.security.SecurityCompactCacheRx;
import com.ayondo.academy.rx.EmptyAction1;
import com.ayondo.academy.rx.ToastOnErrorAction1;
import com.ayondo.academy.rx.view.DismissDialogAction0;
import com.ayondo.academy.utils.DeviceUtil;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import timber.log.Timber;

public class DiscussionEditPostFragment extends DashboardFragment
{
    @Bind(R.id.discussion_post_content) EditText discussionPostContent;
    @Bind(R.id.discussion_new_post_action_buttons) protected DiscussionPostActionButtonsView discussionPostActionButtonsView;

    @Inject DiscussionServiceWrapper discussionServiceWrapper;
    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject DiscussionCacheRx discussionCache;
    @Inject MentionTaggedStockHandler mentionTaggedStockHandler;

    private DiscussionDTO discussionDTO;
    private Subscription discussionEditSubscription;
    protected MenuItem postMenuButton;
    private Subscription hasSelectedSubscription;

    @Nullable private DiscussionKey discussionKey;
    private DiscussionPostedListener discussionPostedListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_discussion_edit_post, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    protected void initView()
    {
        discussionPostActionButtonsView.setReturnFragmentName(this.getClass().getName());
        mentionTaggedStockHandler.setDiscussionPostContent(discussionPostContent);
        subscribeHasSelected();
        DeviceUtil.showKeyboardDelayed(discussionPostContent);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_discussion_edit_post, menu);
        postMenuButton = menu.findItem(R.id.discussion_edit_post);

        setActionBarTitle(R.string.discussion);
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        Timber.d("onDestroyOptionsMenu");

        super.onDestroyOptionsMenu();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.discussion_edit_post:
                postDiscussion();
                return true;
            case android.R.id.home:
                DeviceUtil.dismissKeyboard(getActivity());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        if (args != null)
        {
            if (args.containsKey(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE))
            {
                DiscussionKey discussionKey = DiscussionKeyFactory.fromBundle(args.getBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE));
                linkWith(discussionKey);
            }
        }

        mentionTaggedStockHandler.collectSelection();
        fragmentElements.get().getMovableBottom().animateHide();
    }

    @Override public void onPause()
    {
        super.onPause();
        fragmentElements.get().getMovableBottom().animateShow();
    }

    @Override public void onDestroyView()
    {
        setActionBarSubtitle(null);
        unsubscribe(discussionEditSubscription);
        discussionEditSubscription = null;
        unsubscribe(hasSelectedSubscription);
        hasSelectedSubscription = null;
        mentionTaggedStockHandler.setDiscussionPostContent(null);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        mentionTaggedStockHandler.setHasSelectedItemFragment(null);
        mentionTaggedStockHandler = null;
        discussionPostedListener = null;
        super.onDestroy();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        unsubscribe(discussionEditSubscription);
        discussionEditSubscription = null;
    }

    private void linkWith(DiscussionDTO discussionDTO)
    {
        this.discussionDTO = discussionDTO;
    }

    private boolean validate()
    {
        boolean notEmptyText = validateNotEmptyText();
        if (!notEmptyText)
        {
            THToast.show(R.string.error_discussion_empty_post);
        }
        return notEmptyText;
    }

    protected void postDiscussion()
    {
        if (validate())
        {
            DiscussionFormDTO discussionFormDTO = buildDiscussionFormDTO();
            if (discussionFormDTO == null) return;

            discussionPostActionButtonsView.populate(discussionFormDTO);
            discussionPostActionButtonsView.onPostDiscussion();

            final ProgressDialog progressDialog = ProgressDialog.show(
                    getActivity(),
                    getString(R.string.alert_dialog_please_wait),
                    getString(R.string.processing),
                    true);
            unsubscribe(discussionEditSubscription);
            Action0 dismissDialogAction0 = new DismissDialogAction0(progressDialog);
            discussionEditSubscription = AppObservable.bindSupportFragment(
                    this,
                    discussionServiceWrapper.createDiscussionRx(discussionFormDTO))
                    .observeOn(AndroidSchedulers.mainThread())
                    .finallyDo(dismissDialogAction0)
                    .doOnUnsubscribe(dismissDialogAction0)
                    .subscribe(
                            new Action1<DiscussionDTO>()
                            {
                                @Override public void call(DiscussionDTO discussion)
                                {
                                    DiscussionEditPostFragment.this.onDiscussionReceived(discussion);
                                }
                            },
                            new ToastOnErrorAction1());
        }
    }

    public void onDiscussionReceived(DiscussionDTO discussionDTO)
    {
        linkWith(discussionDTO);

        if (discussionPostActionButtonsView.isShareEnabled(SocialNetworkEnum.WECHAT))
        {
            socialSharerLazy.get().share(WeChatDTOFactory.createFrom(discussionDTO))
                    .subscribe(
                            new EmptyAction1<SocialShareResult>(),
                            new EmptyAction1<Throwable>()); // Proper callback?
        }

        DeviceUtil.dismissKeyboard(getActivity());
        if (discussionPostedListener != null)
        {
            discussionPostedListener.onDiscussionPosted();
        }
        navigator.get().popFragment();
    }

    protected DiscussionFormDTO buildDiscussionFormDTO()
    {
        DiscussionFormDTO discussionFormDTO;
        DiscussionType discussionType = getDiscussionType();
        if (discussionType != null)
        {
            discussionFormDTO = DiscussionFormDTOFactory.createEmpty(discussionType);
        }
        else
        {
            discussionFormDTO = new DiscussionFormDTO();
        }
        if (discussionKey != null && discussionFormDTO instanceof ReplyDiscussionFormDTO)
        {
            ((ReplyDiscussionFormDTO) discussionFormDTO).inReplyToId = discussionKey.id;
        }
        discussionFormDTO.text = EditableUtil.unSpanText(discussionPostContent.getText()).toString();
        return discussionFormDTO;
    }

    @Nullable protected DiscussionType getDiscussionType()
    {
        if (discussionKey != null)
        {
            return discussionKey.getType();
        }
        return null;
    }

    private boolean validateNotEmptyText()
    {
        // wow
        return !discussionPostContent.getText().toString().trim().isEmpty();
    }

    private void subscribeHasSelected()
    {
        unsubscribe(hasSelectedSubscription);
        hasSelectedSubscription = discussionPostActionButtonsView.getSelectedItemObservable()
                .subscribe(createSelectedItemObserver());
    }

    private Observer<HasSelectedItem> createSelectedItemObserver()
    {
        return new Observer<HasSelectedItem>()
        {
            @Override public void onCompleted()
            {
                // do nothing
            }

            @Override public void onError(Throwable e)
            {
                THToast.show(new THException(e));
            }

            @Override public void onNext(HasSelectedItem hasSelectedItem)
            {
                mentionTaggedStockHandler.setHasSelectedItemFragment(hasSelectedItem);
            }
        };
    }

    private void linkWith(@NonNull DiscussionKey discussionKey)
    {
        this.discussionKey = discussionKey;
        AbstractDiscussionCompactDTO abstractDiscussionDTO = discussionCache.getCachedValue(discussionKey);
        linkWith(abstractDiscussionDTO);
    }

    private void linkWith(@Nullable AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        // TODO question, should we subclass this to have a NewsEditPostFragment?
        if (abstractDiscussionCompactDTO instanceof NewsItemDTO)
        {
            linkWith((NewsItemDTO) abstractDiscussionCompactDTO);
        }
    }

    private void linkWith(@NonNull NewsItemDTO newsItemDTO)
    {
        setActionBarSubtitle(getString(R.string.discussion_edit_post_subtitle, newsItemDTO.title));
        if (getActivity() != null)
        {
            getActivity().invalidateOptionsMenu();
        }
    }

    public void setCommentPostedListener(DiscussionPostedListener discussionPostedListener)
    {
        this.discussionPostedListener = discussionPostedListener;
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnTextChanged(value = R.id.discussion_post_content,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable s)
    {
        if (postMenuButton != null)
        {
            boolean notEmptyText = validateNotEmptyText();
            if (notEmptyText != postMenuButton.isVisible())
            {
                // TODO do something to enable Post menu button
                getActivity().invalidateOptionsMenu();
            }
        }
    }

    public static interface DiscussionPostedListener
    {
        void onDiscussionPosted();
    }
}
