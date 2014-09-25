package com.tradehero.th.fragments.chinabuild.fragment.message;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.fragment.HasSelectedItem;
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
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.data.DiscoveryDiscussFormDTO;
import com.tradehero.th.fragments.chinabuild.fragment.search.SearchFragment;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserFriendsListFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * 对股票评论的讨论
 */
public class DiscussSendFragment extends DashboardFragment
{
    public static final String BUNDLE_KEY_RETURN_FRAGMENT = DiscussSendFragment.class.getName();
    private static final String SECURITY_TAG_FORMAT = "[$%s](tradehero://security/%d_%s)";
    private static final String MENTIONED_FORMAT = "<@@%s,%d@>";

    @InjectView(R.id.btnAt) Button btnAt;
    @InjectView(R.id.btnSelectStock) Button btnSelectStock;
    @InjectView(R.id.edtDiscussionPostContent) EditText discussionPostContent;


    @Inject RichTextCreator parser;
    private DiscussionKey discussionKey;
    private MiddleCallback<DiscussionDTO> discussionEditMiddleCallback;
    private MiddleCallback<TimelineItemDTO> dicoveryEditMiddleCallback;
    private ProgressDialog progressDialog;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject DiscussionServiceWrapper discussionServiceWrapper;
    private boolean isPosted;
    @Inject DiscussionCache discussionCache;
    @Inject DiscussionKeyFactory discussionKeyFactory;
    @Inject DiscussionFormDTOFactory discussionFormDTOFactory;
    @Inject SecurityCompactCache securityCompactCache;
    private DiscussionDTO discussionDTO;
    private NewsItemDTO newsItemDTO;
    @Inject CurrentUserId currentUserId;

    private HasSelectedItem selectionFragment;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("发布");
        setHeadViewRight0(getResources().getString(R.string.private_message_btn_send));
    }

    @Override public void onClickHeadRight0()
    {
        Timber.d("发送！！！");
        postDiscussion();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discuss_send_layout, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    public void initView()
    {
        DeviceUtil.showKeyboardDelayed(discussionPostContent);
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        unsetDiscussionEditMiddleCallback();
        unsetDiscoveryEditMiddleCallback();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
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
        if (selectionFragment != null)
        {
            @Nullable Object extraInput = selectionFragment.getSelectedItem();
            handleExtraInput(extraInput);
            selectionFragment = null;
        }
    }

    @Override public void onDetach()
    {
        selectionFragment = null;
        super.onDetach();
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

        else if (abstractDiscussionCompactDTO instanceof DiscussionDTO)
        {
            linkWith((DiscussionDTO) abstractDiscussionCompactDTO, andDisplay);
        }
    }

    private void linkWith(NewsItemDTO newsItemDTO, boolean andDisplay)
    {
        this.newsItemDTO = newsItemDTO;
    }

    private void linkWith(DiscussionDTO discussionDTO, boolean andDisplay)
    {
        this.discussionDTO = discussionDTO;
    }

    @OnClick({R.id.btnAt, R.id.btnSelectStock})
    public void onDiscussButtonSelected(View view)
    {
        if (view.getId() == R.id.btnAt)
        {
            Timber.d("@ selected!! ");
            Bundle bundle = new Bundle();
            bundle.putInt(UserFriendsListFragment.BUNDLE_SHOW_USER_ID, currentUserId.toUserBaseKey().key);
            bundle.putInt(UserFriendsListFragment.BUNDLE_SHOW_FRIENDS_TYPE, UserFriendsListFragment.TYPE_FRIENDS_ALL);
            bundle.putString(BUNDLE_KEY_RETURN_FRAGMENT, DiscussSendFragment.this.getClass().getName());
            selectionFragment = (UserFriendsListFragment)pushFragment(UserFriendsListFragment.class,bundle);
        }
        else if (view.getId() == R.id.btnSelectStock)
        {
            Timber.d("$ selected!!");
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_KEY_RETURN_FRAGMENT, DiscussSendFragment.this.getClass().getName());
            selectionFragment = (SearchFragment)pushFragment(SearchFragment.class,bundle);
        }
    }

    private boolean validateNotEmptyText()
    {
        return !discussionPostContent.getText().toString().trim().isEmpty();
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

    protected DiscussionType getDiscussionType()
    {
        if (discussionKey != null)
        {
            return discussionKey.getType();
        }
        return null;
    }

    protected DiscussionFormDTO buildDiscussionFormDTO()
    {
        DiscussionType discussionType = getDiscussionType();
        if (discussionType != null)
        {
            DiscussionFormDTO discussionFormDTO = discussionFormDTOFactory.createEmpty(discussionType);
            if (discussionKey != null)
            {
                discussionFormDTO.inReplyToId = discussionKey.id;
            }
            discussionFormDTO.text = unSpanText(discussionPostContent.getText()).toString();
            return discussionFormDTO;
        }

        if (selectionFragment != null)
        {
            Object extraInput = selectionFragment.getSelectedItem();
            handleExtraInput(extraInput);
        }

        return null;
    }

    private void handleExtraInput(Object extraInput)
    {
        if(extraInput == null)return;

        String extraText = "";
        Editable editable = discussionPostContent.getText();

        if (extraInput instanceof SecurityCompactDTO)
        {
            SecurityCompactDTO taggedSecurity = (SecurityCompactDTO) extraInput;
            String exchangeSymbol = taggedSecurity.getExchangeSymbol();
            String exchangeSymbolUrl = exchangeSymbol.replace(':', '_');
            extraText = String.format(SECURITY_TAG_FORMAT, exchangeSymbol, taggedSecurity.id, exchangeSymbolUrl);
        }

        if (extraInput instanceof UserProfileCompactDTO)
        {
            //UserSearchResultDTO mentionedUserProfileDTO = userSearchResultCache.get((UserBaseKey) extraInput);
            extraText = String.format(MENTIONED_FORMAT, ((UserProfileCompactDTO) extraInput).displayName, ((UserProfileCompactDTO) extraInput).id);
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

    protected static Editable unSpanText(Editable editable)
    {
        SpannableStringBuilder editableCopy = new SpannableStringBuilder(editable);
        Span[] spans = editableCopy.getSpans(0, editableCopy.length(), Span.class);

        for (int i = spans.length - 1; i >= 0; --i)
        {
            Span span = spans[i];
            int spanStart = editableCopy.getSpanStart(span);
            int spanEnd = editableCopy.getSpanEnd(span);

            editableCopy = editableCopy.replace(spanStart, spanEnd, span.getOriginalText());
        }
        return editableCopy;
    }

    protected void postDiscussion()
    {
        if (validate())
        {
            DiscussionFormDTO discussionFormDTO = buildDiscussionFormDTO();
            if (discussionFormDTO == null) return;
            unsetDiscussionEditMiddleCallback();
            progressDialog = progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.processing);
            discussionEditMiddleCallback = discussionServiceWrapper.createDiscussion(discussionFormDTO, new SecurityDiscussionEditCallback());
        }
    }

    //发布普通的自己的TIMELINE流入最新动态
    protected void postDiscoveryDiscusstion()
    {
        if (validate())
        {
            DiscoveryDiscussFormDTO discussionFormDTO = new DiscoveryDiscussFormDTO();
            if (discussionFormDTO == null) return;
            discussionFormDTO.text = unSpanText(discussionPostContent.getText()).toString();
            unsetDiscoveryEditMiddleCallback();
            progressDialog = progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.processing);
            dicoveryEditMiddleCallback = discussionServiceWrapper.createDiscoveryDiscussion(currentUserId.toUserBaseKey().key, discussionFormDTO,
                    new DiscoveryDiscussionEditCallback());
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

    private void unsetDiscoveryEditMiddleCallback()
    {
        if (dicoveryEditMiddleCallback != null)
        {
            dicoveryEditMiddleCallback.setPrimaryCallback(null);
        }
        dicoveryEditMiddleCallback = null;
    }

    private class SecurityDiscussionEditCallback implements Callback<DiscussionDTO>
    {
        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            onFinish();
            //linkWith(discussionDTO, true);
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

    private class DiscoveryDiscussionEditCallback implements Callback<TimelineItemDTO>
    {
        @Override public void success(TimelineItemDTO discussionDTO, Response response)
        {
            onFinish();
            //linkWith(discussionDTO, true);
            THToast.show("发布成功");
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
}
