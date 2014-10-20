package com.tradehero.th.fragments.discussion;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.utils.EditableUtil;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.form.DiscussionFormDTOFactory;
import com.tradehero.th.api.discussion.form.ReplyDiscussionFormDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.share.wechat.WeChatDTOFactory;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

public class DiscussionEditPostFragment extends DashboardFragment
{
    @InjectView(R.id.discussion_post_content) EditText discussionPostContent;
    @InjectView(R.id.discussion_new_post_action_buttons) protected DiscussionPostActionButtonsView discussionPostActionButtonsView;

    @Inject DiscussionServiceWrapper discussionServiceWrapper;
    @Inject SecurityCompactCache securityCompactCache;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject DiscussionKeyFactory discussionKeyFactory;
    @Inject DiscussionFormDTOFactory discussionFormDTOFactory;
    @Inject DiscussionCache discussionCache;
    @Inject WeChatDTOFactory weChatDTOFactory;
    @Inject DashboardNavigator navigator;
    @Inject @BottomTabs Lazy<DashboardTabHost> dashboardTabHost;
    @Inject EditableUtil editableUtil;
    @Inject MentionTaggedStockHandler mentionTaggedStockHandler;

    private DiscussionDTO discussionDTO;
    private MiddleCallback<DiscussionDTO> discussionEditMiddleCallback;
    private ProgressDialog progressDialog;
    protected MenuItem postMenuButton;
    private TextWatcher discussionEditTextWatcher;
    private Subscription hasSelectedSubscription;

    @Nullable private DiscussionKey discussionKey;
    private boolean isPosted;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_discussion_edit_post, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    protected void initView()
    {
        discussionEditTextWatcher = new DiscussionEditTextWatcher();
        discussionPostContent.addTextChangedListener(discussionEditTextWatcher);
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

        isPosted = false;

        Bundle args = getArguments();
        if (args != null)
        {
            if (args.containsKey(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE))
            {
                DiscussionKey discussionKey = discussionKeyFactory.fromBundle(args.getBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE));
                linkWith(discussionKey, true);
            }
        }

        mentionTaggedStockHandler.collectSelection();
        dashboardTabHost.get().animateHide();
    }

    @Override public void onPause()
    {
        super.onPause();
        dashboardTabHost.get().animateShow();
    }

    @Override public void onDestroyView()
    {
        setActionBarSubtitle(null);
        unsetDiscussionEditMiddleCallback();
        detachSelectedSubscription();
        discussionPostContent.removeTextChangedListener(discussionEditTextWatcher);
        mentionTaggedStockHandler.setDiscussionPostContent(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        mentionTaggedStockHandler.setHasSelectedItemFragment(null);
        mentionTaggedStockHandler = null;
        super.onDestroy();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        unsetDiscussionEditMiddleCallback();
    }

    private void linkWith(DiscussionDTO discussionDTO, boolean andDisplay)
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

            unsetDiscussionEditMiddleCallback();
            progressDialog = progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.processing);
            discussionEditMiddleCallback = discussionServiceWrapper.createDiscussion(discussionFormDTO, new SecurityDiscussionEditCallback());
        }
    }

    protected DiscussionFormDTO buildDiscussionFormDTO()
    {
        @NotNull DiscussionFormDTO discussionFormDTO;
        DiscussionType discussionType = getDiscussionType();
        if (discussionType != null)
        {
            discussionFormDTO = discussionFormDTOFactory.createEmpty(discussionType);
        }
        else
        {
            discussionFormDTO = new DiscussionFormDTO();
        }
        if (discussionKey != null && discussionFormDTO instanceof ReplyDiscussionFormDTO)
        {
            ((ReplyDiscussionFormDTO) discussionFormDTO).inReplyToId = discussionKey.id;
        }
        discussionFormDTO.text = editableUtil.unSpanText(discussionPostContent.getText()).toString();
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

    private void unsetDiscussionEditMiddleCallback()
    {
        if (discussionEditMiddleCallback != null)
        {
            discussionEditMiddleCallback.setPrimaryCallback(null);
        }
        discussionEditMiddleCallback = null;
    }

    private boolean validateNotEmptyText()
    {
        // wow
        return !discussionPostContent.getText().toString().trim().isEmpty();
    }

    private void subscribeHasSelected()
    {
        detachSelectedSubscription();
        hasSelectedSubscription = AndroidObservable.bindFragment(this, discussionPostActionButtonsView.getSelectedItemObservable())
                .subscribe(createSelectedItemObserver());
    }

    private void detachSelectedSubscription()
    {
        Subscription hasSelectedSubscriptionCopy = hasSelectedSubscription;
        if (hasSelectedSubscriptionCopy != null)
        {
            hasSelectedSubscriptionCopy.unsubscribe();
        }
        hasSelectedSubscription = null;
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

    private void linkWith(@NotNull DiscussionKey discussionKey, boolean andDisplay)
    {
        this.discussionKey = discussionKey;
        AbstractDiscussionCompactDTO abstractDiscussionDTO = discussionCache.get(discussionKey);
        linkWith(abstractDiscussionDTO, andDisplay);
    }

    private void linkWith(@Nullable AbstractDiscussionCompactDTO abstractDiscussionCompactDTO, boolean andDisplay)
    {
        // TODO question, should we subclass this to have a NewsEditPostFragment?
        if (abstractDiscussionCompactDTO instanceof NewsItemDTO)
        {
            linkWith((NewsItemDTO) abstractDiscussionCompactDTO, andDisplay);
        }
    }

    private void linkWith(@NotNull NewsItemDTO newsItemDTO, boolean andDisplay)
    {
        if (andDisplay)
        {
            setActionBarSubtitle(getString(R.string.discussion_edit_post_subtitle, newsItemDTO.title));
            if(getActivity() != null)
            {
                getActivity().invalidateOptionsMenu();
            }
        }
    }

    public boolean isPosted()
    {
        return isPosted;
    }

    private class SecurityDiscussionEditCallback implements Callback<DiscussionDTO>
    {
        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            onFinish();

            linkWith(discussionDTO, true);

            if (discussionPostActionButtonsView.isShareEnabled(SocialNetworkEnum.WECHAT))
            {
                socialSharerLazy.get().share(weChatDTOFactory.createFrom(discussionDTO)); // Proper callback?
            }

            isPosted = true;

            DeviceUtil.dismissKeyboard(getActivity());
            navigator.popFragment();
        }

        @Override public void failure(RetrofitError error)
        {
            onFinish();

            THToast.show(new THException(error));
        }

        private void onFinish()
        {
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
        }
    }

    private class DiscussionEditTextWatcher implements TextWatcher
    {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override public void onTextChanged(CharSequence s, int start, int before, int count)
        {
        }

        @Override public void afterTextChanged(Editable s)
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
    }
}
