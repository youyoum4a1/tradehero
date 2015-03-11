package com.tradehero.chinabuild.fragment.message;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.chinabuild.data.DiscoveryDiscussFormDTO;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.chinabuild.fragment.search.SearchFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserHeroesListFragment;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.text.RichTextCreator;
import com.tradehero.common.text.Span;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.form.DiscussionFormDTOFactory;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;

/**
 * 对股票评论的讨论
 */
public class DiscussSendFragment extends DashboardFragment
{
    public static final String BUNDLE_KEY_RETURN_FRAGMENT = DiscussSendFragment.class.getName();
    private static final String SECURITY_TAG_FORMAT = "[$%s](tradehero://security/%d_%s)"; //选取的股票
    private static final String MENTIONED_FORMAT = "<@@%s,%d@>";//选取的user
    private static final String COMPETITION_FORMAT = "我参加了一个炒股比赛 <#%s,%d#> ，一起来切磋下吧～";//比赛

    public static final String BUNDLE_KEY_COMPETITION = "bundle_key_competition";
    public static final String BUNDLE_KEY_REWARD = "bundle_key_reward";
    public static final String BUNDLE_KEY_IS_GO_REWARD = "bundle_key_is_go_reward";

    @InjectView(R.id.btnAt) Button btnAt;
    @InjectView(R.id.btnSelectStock) Button btnSelectStock;
    protected @InjectView(R.id.edtDiscussionPostContent) EditText discussionPostContent;

    @Inject RichTextCreator parser;
    private DiscussionKey discussionKey;
    private MiddleCallback<DiscussionDTO> discussionEditMiddleCallback;
    private MiddleCallback<TimelineItemDTO> dicoveryEditMiddleCallback;
    private ProgressDialog progressDialog;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject DiscussionServiceWrapper discussionServiceWrapper;
    @Inject DiscussionCache discussionCache;
    @Inject DiscussionKeyFactory discussionKeyFactory;
    @Inject DiscussionFormDTOFactory discussionFormDTOFactory;
    @Inject SecurityCompactCache securityCompactCache;
    @Inject CurrentUserId currentUserId;

    private HasSelectedItem selectionFragment;

    //Competition
    private UserCompetitionDTO userCompetitionDTO;

    //Reward
    private boolean isReward = false;
    @InjectView(R.id.linearlayout_discuss_send_reward) LinearLayout discussSendRewardLL;
    @InjectView(R.id.textview_discuss_send_reward) TextView discussSendRewardTV;
    @InjectView(R.id.imageview_discuss_send_reward) ImageView discussSendRewardIV;
    @InjectView(R.id.linearlayout_reward_layout) LinearLayout rewardLayoutLL;
    @InjectView(R.id.edittext_reward_doc_title) EditText rewardTitleET;
    @InjectView(R.id.edittext_reward_doc_content) EditText rewardContentET;
    @InjectView(R.id.view_reward_divider)View dividerView;
    @InjectView(R.id.linearlayout_reward_money)LinearLayout rewardMoneyLayoutLL;
    @InjectView(R.id.spinner_reward_money_list)Spinner rewardMoneyListS;
    private ArrayAdapter<String> rewardMoneyListAdapter;
    private boolean isGoToReward = false;
    private int rewardColorGray;
    private int rewardColorOrange;
    private String[] moneyList;

    private boolean isSending = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initArgument();
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
        if(isSending)return;
        if(isReward){
            postRewardTimeLine();
        }else {
            postDiscussion();
        }
    }

    public void initArgument()
    {
        Bundle args = getArguments();
        if (args != null)
        {
            if (args.containsKey(BUNDLE_KEY_COMPETITION))
            {
                userCompetitionDTO = (UserCompetitionDTO) args.getSerializable(BUNDLE_KEY_COMPETITION);
            }
            if(args.containsKey(BUNDLE_KEY_REWARD)){
                isReward = args.getBoolean(BUNDLE_KEY_REWARD);
            }
            if(args.containsKey(BUNDLE_KEY_IS_GO_REWARD)){
                isGoToReward = args.getBoolean(BUNDLE_KEY_IS_GO_REWARD);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discuss_send_layout, container, false);
        ButterKnife.inject(this, view);
        initView();
        isSending  = false;
        return view;
    }

    public void initView()
    {
        DeviceUtil.showKeyboardDelayed(discussionPostContent);
        if (userCompetitionDTO != null)
        {
            handleExtraInput(userCompetitionDTO);
        }
        if(isReward){
            discussSendRewardLL.setVisibility(View.VISIBLE);
            discussionPostContent.setVisibility(View.GONE);
            rewardLayoutLL.setVisibility(View.VISIBLE);
            rewardColorGray = getActivity().getResources().getColor(R.color.discovery_discuss_reward_gray);
            rewardColorOrange = getActivity().getResources().getColor(R.color.discovery_discuss_reward_orange);
            moneyList = getActivity().getResources().getStringArray(R.array.reward_money_list);
            discussSendRewardLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    refreshRewardView();
                }
            });
            if(isGoToReward){
                discussSendRewardTV.setTextColor(rewardColorOrange);
                discussSendRewardIV.setBackgroundResource(R.drawable.checkbox_orange);
                dividerView.setVisibility(View.VISIBLE);
                rewardMoneyLayoutLL.setVisibility(View.VISIBLE);
            }else{
                discussSendRewardTV.setTextColor(rewardColorGray);
                discussSendRewardIV.setBackgroundResource(R.drawable.checkbox_gray);
                dividerView.setVisibility(View.GONE);
                rewardMoneyLayoutLL.setVisibility(View.GONE);
            }
            rewardMoneyListAdapter = new ArrayAdapter(getActivity(), R.layout.spinner_item_reward, moneyList);
            rewardMoneyListAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_reward);
            rewardMoneyListS.setAdapter(rewardMoneyListAdapter);
        }else{
            discussSendRewardLL.setVisibility(View.GONE);
            rewardLayoutLL.setVisibility(View.GONE);
            discussionPostContent.setVisibility(View.VISIBLE);
            discussionPostContent.requestFocus();
        }
    }

    @Override public void onDestroyView()
    {
        unsetDiscussionEditMiddleCallback();
        unsetDiscoveryEditMiddleCallback();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onResume()
    {
        super.onResume();

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
        if(isReward){
            rewardContentET.requestFocus();
        }else{
            discussionPostContent.requestFocus();
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
    }

    @OnClick({R.id.btnAt, R.id.btnSelectStock})
    public void onDiscussButtonSelected(View view)
    {
        if (view.getId() == R.id.btnAt)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(UserHeroesListFragment.BUNDLE_SHOW_USER_ID, currentUserId.toUserBaseKey().key);
            bundle.putString(BUNDLE_KEY_RETURN_FRAGMENT, DiscussSendFragment.this.getClass().getName());
            selectionFragment = (UserHeroesListFragment) pushFragment(UserHeroesListFragment.class, bundle);
        }
        else if (view.getId() == R.id.btnSelectStock)
        {
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_KEY_RETURN_FRAGMENT, DiscussSendFragment.this.getClass().getName());
            selectionFragment = (SearchFragment) pushFragment(SearchFragment.class, bundle);
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
        if (extraInput == null) return;

        String extraText = "";


        if (extraInput instanceof SecurityCompactDTO)
        {
            SecurityCompactDTO taggedSecurity = (SecurityCompactDTO) extraInput;
            String exchangeSymbol = taggedSecurity.getExchangeSymbol();
            String exchangeSymbolUrl = exchangeSymbol.replace(':', '_');
            extraText = String.format(SECURITY_TAG_FORMAT, exchangeSymbol, taggedSecurity.id, exchangeSymbolUrl);
        }

        if (extraInput instanceof UserProfileCompactDTO)
        {
            extraText = String.format(MENTIONED_FORMAT, ((UserProfileCompactDTO) extraInput).getDisplayName(), ((UserProfileCompactDTO) extraInput).id);
        }

        if (extraInput instanceof UserCompetitionDTO)
        {
            extraText = String.format(COMPETITION_FORMAT, ((UserCompetitionDTO) extraInput).name, ((UserCompetitionDTO) extraInput).id);
        }

        Editable editable = null;
        if(isReward){
            editable = rewardContentET.getText();
            String nonMarkUpText = extraText;
            if (!editable.toString().isEmpty()) {
                int start = rewardContentET.getSelectionStart();
                int end = rewardContentET.getSelectionEnd();
                editable = editable.replace(start, end, extraText);
                nonMarkUpText = unSpanText(editable).toString();
            }
            rewardContentET.setText(parser.load(nonMarkUpText).create(), TextView.BufferType.SPANNABLE);
            rewardContentET.setSelection(rewardContentET.length());
        }else {
            editable = discussionPostContent.getText();
            String nonMarkUpText = extraText;
            if (!editable.toString().isEmpty()) {
                int start = discussionPostContent.getSelectionStart();
                int end = discussionPostContent.getSelectionEnd();
                editable = editable.replace(start, end, extraText);
                nonMarkUpText = unSpanText(editable).toString();
            }
            discussionPostContent.setText(parser.load(nonMarkUpText).create(), TextView.BufferType.SPANNABLE);
            discussionPostContent.setSelection(discussionPostContent.length());
        }
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

    private void postRewardTimeLine()
    {
        String title = rewardTitleET.getText().toString();
        if(TextUtils.isEmpty(title) || title.length()>18 || title.length()<3){
            THToast.show(R.string.discovery_discuss_send_reward_title_warning);
            return;
        }
        String content = rewardContentET.getText().toString();
        if(TextUtils.isEmpty(content)){
            THToast.show(R.string.discovery_discuss_send_reward_content_warning);
            return;
        }
        DiscoveryDiscussFormDTO dto = new DiscoveryDiscussFormDTO();
        dto.text = unSpanText(rewardContentET.getText()).toString();
        dto.header = title;
        if(isGoToReward){
            int position = rewardMoneyListS.getSelectedItemPosition();
            switch(position){
                case 0:
                    dto.prizeAmount = 1000;
                    break;
                case 1:
                    dto.prizeAmount = 2000;
                    break;
                case 2:
                    dto.prizeAmount = 5000;
                    break;
                case 3:
                    dto.prizeAmount = 10000;
                    break;
            }
        }
        progressDialog = progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.processing);
        isSending = true;
        discussionServiceWrapper.createRewardTimeLine(currentUserId.toUserBaseKey().key, dto, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                popCurrentFragment();
                THToast.show(R.string.discovery_discuss_send_reward_successfully);
                onFinish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                THException exception = new THException(retrofitError);
                THToast.show(exception.getMessage());
                onFinish();
            }

            private void onFinish(){
                if (progressDialog != null)
                {
                    progressDialog.hide();
                }

                isSending = false;
            }
        });
    }

    //发布普通的自己的TIMELINE流入最新动态
    protected void postDiscoveryDiscusstion()
    {
        if (validate())
        {
            isSending = true;
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

            isSending = false;
        }
    }

    private void refreshRewardView(){
        if(isGoToReward){
            isGoToReward = false;
            discussSendRewardTV.setTextColor(rewardColorGray);
            discussSendRewardIV.setBackgroundResource(R.drawable.checkbox_gray);
            dividerView.setVisibility(View.GONE);
            rewardMoneyLayoutLL.setVisibility(View.GONE);
        }else{
            isGoToReward = true;
            discussSendRewardTV.setTextColor(rewardColorOrange);
            discussSendRewardIV.setBackgroundResource(R.drawable.checkbox_orange);
            dividerView.setVisibility(View.VISIBLE);
            rewardMoneyLayoutLL.setVisibility(View.VISIBLE);
        }
    }
}
