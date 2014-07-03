package com.tradehero.th.fragments.discussion;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.text.RichTextCreator;
import com.tradehero.common.text.Span;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.form.DiscussionFormDTOFactory;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.share.wechat.WeChatDTOFactory;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.trending.SearchStockPeopleFragment;
import com.tradehero.th.fragments.trending.TrendingSearchType;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserSearchResultCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class DiscussionEditPostFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_SECURITY_ID = DiscussionEditPostFragment.class.getName() + ".securityId";
    private static final String SECURITY_TAG_FORMAT = "[$%s](tradehero://security/%d_%s)";
    private static final String MENTIONED_FORMAT = "<@@%s,%d@>";

    @InjectView(R.id.discussion_post_content) EditText discussionPostContent;
    @InjectView(R.id.discussion_new_post_action_buttons) DiscussionPostActionButtonsView discussionPostActionButtonsView;
    @InjectView(R.id.btn_wechat) ToggleButton mWeChatShareButton;

    @Inject DiscussionServiceWrapper discussionServiceWrapper;
    @Inject SecurityCompactCache securityCompactCache;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject UserSearchResultCache userSearchResultCache;
    @Inject SocialSharer socialSharer;
    @Inject RichTextCreator parser;
    @Inject DiscussionKeyFactory discussionKeyFactory;
    @Inject DiscussionFormDTOFactory discussionFormDTOFactory;
    @Inject DiscussionCache discussionCache;
    @Inject WeChatDTOFactory weChatDTOFactory;

    @Nullable private SecurityId securityId;
    private DiscussionDTO discussionDTO;
    private MiddleCallback<DiscussionDTO> discussionEditMiddleCallback;
    private ProgressDialog progressDialog;
    private MenuItem postMenuButton;
    private TextWatcher discussionEditTextWatcher;

    private SearchStockPeopleFragment searchStockPeopleFragment;
    private DiscussionKey discussionKey;
    private boolean isPosted;

    public static void putSecurityId(@NotNull Bundle args, @NotNull SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
    }

    @Nullable public static SecurityId getSecurityId(@Nullable Bundle args)
    {
        SecurityId extracted = null;
        if (args != null && args.containsKey(BUNDLE_KEY_SECURITY_ID))
        {
            extracted = new SecurityId(args.getBundle(BUNDLE_KEY_SECURITY_ID));
        }
        return extracted;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_discussion_edit_post, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    private void initView()
    {
        discussionEditTextWatcher = new DiscussionEditTextWatcher();
        discussionPostContent.addTextChangedListener(discussionEditTextWatcher);
        DeviceUtil.showKeyboardDelayed(discussionPostContent);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_discussion_edit_post, menu);
        postMenuButton = menu.findItem(R.id.discussion_edit_post);

        getSherlockActivity().getSupportActionBar().setTitle(R.string.discussion);
    }

    @Override public void onDestroyOptionsMenu()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        //actionBar.setTitle(null);
        actionBar.setSubtitle(null);
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

    @Override public void onDestroyView()
    {
        unsetDiscussionEditMiddleCallback();
        discussionPostContent.removeTextChangedListener(discussionEditTextWatcher);

        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        unsetDiscussionEditMiddleCallback();
    }

    @Override public void onDetach()
    {
        searchStockPeopleFragment = null;
        super.onDetach();
    }

    //<editor-fold desc="View's events handling">
    // TODO following views' events should be handled inside DiscussionPostActionButtonsView
    @OnClick({
            R.id.btn_mention,
            R.id.btn_security_tag
    }) void onMentionButtonClicked(View clickedButton)
    {
        TrendingSearchType searchType = null;
        switch (clickedButton.getId())
        {
            case R.id.btn_mention:
                searchType = TrendingSearchType.PEOPLE;
                break;
            case R.id.btn_security_tag:
                searchType = TrendingSearchType.STOCKS;
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putString(Navigator.BUNDLE_KEY_RETURN_FRAGMENT, this.getClass().getName());
        if (searchType != null)
        {
            bundle.putString(SearchStockPeopleFragment.BUNDLE_KEY_RESTRICT_SEARCH_TYPE, searchType.name());
            searchStockPeopleFragment = getDashboardNavigator().pushFragment(SearchStockPeopleFragment.class, bundle);
        }
    }
    //</editor-fold>

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

    private void postDiscussion()
    {
        // TODO a subclass that has a @NotNull SecurityId
        SecurityCompactDTO securityCompactDTO = null;
        if (securityId != null)
        {
            securityCompactDTO = securityCompactCache.get(securityId);
        }
        if (validate())
        {
            DiscussionType discussionType = null;
            int discussionId = 0;
            if (securityCompactDTO != null)
            {
                discussionType = DiscussionType.SECURITY;
                discussionId = securityCompactDTO.id;
            }
            else if (discussionKey != null)
            {
                discussionType = discussionKey.getType();
                discussionId = discussionKey.id;
            }
            else
            {
                return;
            }

            DiscussionFormDTO discussionFormDTO = discussionFormDTOFactory.createEmpty(discussionType);
            discussionFormDTO.inReplyToId = discussionId;
            discussionFormDTO.text = unSpanText(discussionPostContent.getText()).toString();
            discussionPostActionButtonsView.populate(discussionFormDTO);

            unsetDiscussionEditMiddleCallback();
            progressDialog = progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.processing);
            discussionEditMiddleCallback = discussionServiceWrapper.createDiscussion(discussionFormDTO, new SecurityDiscussionEditCallback());
        }
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

    @Override public void onResume()
    {
        super.onResume();

        isPosted = false;

        SecurityId fromArgs = getSecurityId(getArguments());
        if (fromArgs != null)
        {
            linkWith(fromArgs, true);
        }

        Bundle args = getArguments();
        if (args != null)
        {
            if (args.containsKey(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE))
            {
                DiscussionKey discussionKey = discussionKeyFactory.fromBundle(args.getBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE));
                linkWith(discussionKey, true);
            }
        }

        if (searchStockPeopleFragment != null)
        {
            Object extraInput = searchStockPeopleFragment.getSelectedItem();
            handleExtraInput(extraInput);
        }
    }

    private void handleExtraInput(Object extraInput)
    {
        String extraText = "";
        Editable editable = discussionPostContent.getText();

        if (extraInput instanceof SecurityCompactDTO)
        {
            SecurityCompactDTO taggedSecurity = (SecurityCompactDTO) extraInput;

            String exchangeSymbol = taggedSecurity.getExchangeSymbol();
            String exchangeSymbolUrl = exchangeSymbol.replace(':', '_');
            extraText = String.format(SECURITY_TAG_FORMAT, exchangeSymbol, taggedSecurity.id, exchangeSymbolUrl);
        }

        if (extraInput instanceof UserBaseKey)
        {
            UserSearchResultDTO mentionedUserProfileDTO = userSearchResultCache.get((UserBaseKey) extraInput);
            extraText = String.format(MENTIONED_FORMAT, mentionedUserProfileDTO.userthDisplayName, mentionedUserProfileDTO.userId);
        }

        String nonMarkUpText = extraText;
        if (!editable.toString().isEmpty())
        {
            int start = discussionPostContent.getSelectionStart();
            int end = discussionPostContent.getSelectionEnd();
            editable = editable.replace(start, end, extraText);
            nonMarkUpText = unSpanText(editable).toString();
        }

        Timber.d("Original text: %s", nonMarkUpText);
        discussionPostContent.setText(parser.load(nonMarkUpText).create(), TextView.BufferType.SPANNABLE);
        discussionPostContent.setSelection(discussionPostContent.length());
    }

    private Editable unSpanText(Editable editable)
    {
        // keep editable unchange
        SpannableStringBuilder editableCopy = new SpannableStringBuilder(editable);
        Span[] spans = editableCopy.getSpans(0, editableCopy.length(), Span.class);

        // replace all span string with its original text
        for (int i = spans.length - 1; i >= 0; --i)
        {
            Span span = spans[i];
            int spanStart = editableCopy.getSpanStart(span);
            int spanEnd = editableCopy.getSpanEnd(span);

            editableCopy = editableCopy.replace(spanStart, spanEnd, span.getOriginalText());
        }
        return editableCopy;
    }

    private void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;

        if (andDisplay && securityId != null)
        {
            String securityName = String.format("%s:%s", securityId.getExchange(), securityId.getSecuritySymbol());
            discussionPostContent.setHint(getString(R.string.discussion_new_post_hint, securityName));
        }

        SecurityCompactDTO securityCompactDTO = securityCompactCache.get(securityId);
        if (andDisplay && securityCompactDTO != null)
        {
            getSherlockActivity().getSupportActionBar().setSubtitle(getString(R.string.discussion_edit_post_subtitle, securityCompactDTO.name));
            getSherlockActivity().invalidateOptionsMenu();
        }
    }

    private void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        this.discussionKey = discussionKey;
        AbstractDiscussionCompactDTO abstractDiscussionDTO = discussionCache.get(discussionKey);
        linkWith(abstractDiscussionDTO, andDisplay);
    }

    private void linkWith(AbstractDiscussionCompactDTO abstractDiscussionCompactDTO, boolean andDisplay)
    {
        // TODO question, should we subclass this to have a NewsEditPostFragment?
        if (abstractDiscussionCompactDTO instanceof NewsItemDTO)
        {
            linkWith((NewsItemDTO) abstractDiscussionCompactDTO, andDisplay);
        }
    }

    private void linkWith(NewsItemDTO newsItemDTO, boolean andDisplay)
    {
        if (andDisplay)
        {
            getSherlockActivity().getSupportActionBar().setSubtitle(getString(R.string.discussion_edit_post_subtitle, newsItemDTO.title));
            getSherlockActivity().invalidateOptionsMenu();
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

            if (mWeChatShareButton.isChecked())
            {
                socialSharer.share(weChatDTOFactory.createFrom(discussionDTO)); // Proper callback?
            }

            isPosted = true;

            DeviceUtil.dismissKeyboard(getActivity());
            getDashboardNavigator().popFragment();
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
                    getSherlockActivity().invalidateOptionsMenu();
                }
            }
        }
    }
}
