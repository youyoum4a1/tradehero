package com.tradehero.th.fragments.discussion;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
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
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.form.SecurityDiscussionFormDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.trending.SearchStockPeopleFragment;
import com.tradehero.th.fragments.trending.TrendingSearchType;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserSearchResultCache;
import com.tradehero.th.utils.ForWeChat;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.SocialSharer;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by tho on 4/21/2014.
 */
public class DiscussionEditPostFragment extends DashboardFragment
{
    private static final String SECURITY_TAG_FORMAT = "<$$%s,%d$>";
    private static final String MENTIONED_FORMAT = "<@@%s,%d@>";

    @InjectView(R.id.discussion_post_content) EditText discussionPostContent;
    @InjectView(R.id.discussion_new_post_action_buttons) DiscussionPostActionButtonsView discussionPostActionButtonsView;
    @InjectView(R.id.btn_wechat) ToggleButton mWeChatShareButton;

    @Inject DiscussionServiceWrapper discussionServiceWrapper;
    @Inject SecurityCompactCache securityCompactCache;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject UserSearchResultCache userSearchResultCache;
    @Inject @ForWeChat SocialSharer weChatSharer;
    @Inject RichTextCreator parser;

    private SecurityId securityId;
    private DiscussionDTO discussionDTO;
    private MiddleCallback<DiscussionDTO> discussionEditMiddleCallback;
    private ProgressDialog progressDialog;
    private MenuItem postMenuButton;
    private TextWatcher discussionEditTextWatcher;

    private SearchStockPeopleFragment searchStockPeopleFragment;

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
        actionBar.setTitle(null);
        actionBar.setSubtitle(null);

        super.onDestroyOptionsMenu();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.discussion_edit_post:
                postDiscussion();
                return true;
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
    })
    void onMentionButtonClicked(View clickedButton)
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
            bundle.putString(SearchStockPeopleFragment.BUNDLE_KEY_SEARCH_TYPE, searchType.name());
            searchStockPeopleFragment = (SearchStockPeopleFragment) getNavigator().pushFragment(SearchStockPeopleFragment.class, bundle);
        }
    }
    //</editor-fold>

    private void linkWith(DiscussionDTO discussionDTO, boolean andDisplay)
    {
        this.discussionDTO = discussionDTO;

        if (mWeChatShareButton.isChecked())
        {
            weChatSharer.share(getActivity(), discussionDTO.getDiscussionKey());
        }
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
        SecurityCompactDTO securityCompactDTO = securityCompactCache.get(securityId);
        if (validate() && securityCompactDTO != null)
        {
            SecurityDiscussionFormDTO securityDiscussionFormDTO = new SecurityDiscussionFormDTO();
            securityDiscussionFormDTO.inReplyToId = securityCompactDTO.id;
            securityDiscussionFormDTO.text = discussionPostContent.getText().toString();
            discussionPostActionButtonsView.populate(securityDiscussionFormDTO);

            unsetDiscussionEditMiddleCallback();
            progressDialog = progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.processing);
            discussionEditMiddleCallback = discussionServiceWrapper.createDiscussion(securityDiscussionFormDTO, new SecurityDiscussionEditCallback());
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

        Bundle args = getArguments();
        if (args != null)
        {
            if (args.containsKey(SecurityId.BUNDLE_KEY_SECURITY_ID_BUNDLE))
            {
                Bundle securityBundle = args.getBundle(SecurityId.BUNDLE_KEY_SECURITY_ID_BUNDLE);
                SecurityId securityId = new SecurityId(securityBundle);
                linkWith(securityId, true);
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
        Timber.d("Extra: %s", extraInput);

        String extraText = "";
        String editingText = discussionPostContent.getText().toString();

        if (extraInput instanceof SecurityCompactDTO)
        {
            SecurityCompactDTO taggedSecurity = (SecurityCompactDTO) extraInput;

            extraText = String.format(SECURITY_TAG_FORMAT, taggedSecurity.getExchangeSymbol(), taggedSecurity.id);
        }

        if (extraInput instanceof UserBaseKey)
        {
            UserSearchResultDTO mentionedUserProfileDTO = userSearchResultCache.get((UserBaseKey) extraInput);
            extraText = String.format(MENTIONED_FORMAT, mentionedUserProfileDTO.userthDisplayName, mentionedUserProfileDTO.userId);
        }

        String newText = extraText;
        if (!editingText.isEmpty())
        {
            int start = discussionPostContent.getSelectionStart();
            int end = discussionPostContent.getSelectionEnd();
            newText = discussionPostContent.getText().replace(start, end, extraText).toString();
        }

        discussionPostContent.setText(parser.load(newText).create(), TextView.BufferType.SPANNABLE);
    }

    private void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;

        if (andDisplay && securityId != null)
        {
            String securityName = String.format("%s:%s", securityId.exchange, securityId.securitySymbol);
            discussionPostContent.setHint(getString(R.string.discussion_new_post_hint, securityName));
        }

        SecurityCompactDTO securityCompactDTO = securityCompactCache.get(securityId);
        if (andDisplay && securityCompactDTO != null)
        {
            getSherlockActivity().getSupportActionBar().setSubtitle(getString(R.string.discussion_security_subtitle, securityCompactDTO.name));
            getSherlockActivity().invalidateOptionsMenu();
        }
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }


    private class SecurityDiscussionEditCallback implements Callback<DiscussionDTO>
    {
        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            onFinish();

            linkWith(discussionDTO, true);
            getNavigator().popFragment();
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
