package com.tradehero.chinabuild.fragment.security;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.data.SecurityComment;
import com.tradehero.chinabuild.data.SecurityCommentList;
import com.tradehero.chinabuild.fragment.message.SecurityDiscussSendFragment;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by palmer on 15/6/10.
 */
public class SecurityDetailSubDiscussFragment extends Fragment implements View.OnClickListener {

    @Inject
    Analytics analytics;
    @Inject
    public Lazy<PrettyTime> prettyTime;

    private String securityName;
    private SecurityId securityId;

    private TextView moreTV;
    private LinearLayout emptyLL;
    private LinearLayout discussLL;

    private int securityDTOId = -1;

    //Layout 0
    private RelativeLayout ll0;
    private ImageView avatarIV0;
    private TextView nameTV0;
    private TextView contentTV0;
    private TextView dateTV0;
    private TextView moreTV0;

    //Layout 1
    private RelativeLayout ll1;
    private ImageView avatarIV1;
    private TextView nameTV1;
    private TextView contentTV1;
    private TextView dateTV1;
    private TextView moreTV1;
    private View seperateV1;

    //Layout 2
    private RelativeLayout ll2;
    private ImageView avatarIV2;
    private TextView nameTV2;
    private TextView contentTV2;
    private TextView dateTV2;
    private TextView moreTV2;
    private View seperateV2;

    //Layout 3
    private RelativeLayout ll3;
    private ImageView avatarIV3;
    private TextView nameTV3;
    private TextView contentTV3;
    private TextView dateTV3;
    private TextView moreTV3;
    private View seperateV3;

    //Layout 4
    private RelativeLayout ll4;
    private ImageView avatarIV4;
    private TextView nameTV4;
    private TextView contentTV4;
    private TextView dateTV4;
    private TextView moreTV4;
    private View seperateV4;

    private PaginatedDiscussionListKey discussionListKey;
    @Inject
    DiscussionServiceWrapper discussionServiceWrapper;

    private DiscussionViewHolder[] viewHolders = new DiscussionViewHolder[5];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArguments();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_security_detail_discuss, container, false);
        moreTV = (TextView) view.findViewById(R.id.textview_more);
        moreTV.setOnClickListener(this);
        emptyLL = (LinearLayout) view.findViewById(R.id.linearlayout_empty);
        emptyLL.setOnClickListener(this);
        emptyLL.setVisibility(View.VISIBLE);
        discussLL = (LinearLayout) view.findViewById(R.id.linearlayout_discusses);
        discussLL.setVisibility(View.GONE);
        initViews(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchSecurityDiscuss(true);
    }

    private void initArguments() {
        Bundle args = getArguments();
        Bundle securityIdBundle = args.getBundle(SecurityDetailFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE);
        securityName = args.getString(SecurityDetailFragment.BUNDLE_KEY_SECURITY_NAME);
        if (securityIdBundle != null) {
            securityId = new SecurityId(securityIdBundle);
        }
        securityDTOId = args.getInt(SecurityDetailFragment.BUNDLE_KEY_SECURITY_DTO_ID_BUNDLE, -1);
    }

    private void initViews(View view) {
        ll0 = (RelativeLayout) view.findViewById(R.id.relativelayout_user0);
        avatarIV0 = (ImageView) view.findViewById(R.id.imageview_avatar0);
        nameTV0 = (TextView) view.findViewById(R.id.textview_name0);
        contentTV0 = (TextView) view.findViewById(R.id.textview_content0);
        dateTV0 = (TextView) view.findViewById(R.id.textview_date0);
        moreTV0 = (TextView) view.findViewById(R.id.textview_more0);

        ll1 = (RelativeLayout) view.findViewById(R.id.relativelayout_user1);
        avatarIV1 = (ImageView) view.findViewById(R.id.imageview_avatar1);
        nameTV1 = (TextView) view.findViewById(R.id.textview_name1);
        contentTV1 = (TextView) view.findViewById(R.id.textview_content1);
        dateTV1 = (TextView) view.findViewById(R.id.textview_date1);
        moreTV1 = (TextView) view.findViewById(R.id.textview_more1);
        seperateV1 = view.findViewById(R.id.line1);

        ll2 = (RelativeLayout) view.findViewById(R.id.relativelayout_user2);
        avatarIV2 = (ImageView) view.findViewById(R.id.imageview_avatar2);
        nameTV2 = (TextView) view.findViewById(R.id.textview_name2);
        contentTV2 = (TextView) view.findViewById(R.id.textview_content2);
        dateTV2 = (TextView) view.findViewById(R.id.textview_date2);
        moreTV2 = (TextView) view.findViewById(R.id.textview_more2);
        seperateV2 = view.findViewById(R.id.line2);

        ll3 = (RelativeLayout) view.findViewById(R.id.relativelayout_user3);
        avatarIV3 = (ImageView) view.findViewById(R.id.imageview_avatar3);
        nameTV3 = (TextView) view.findViewById(R.id.textview_name3);
        contentTV3 = (TextView) view.findViewById(R.id.textview_content3);
        dateTV3 = (TextView) view.findViewById(R.id.textview_date3);
        moreTV3 = (TextView) view.findViewById(R.id.textview_more3);
        seperateV3 = view.findViewById(R.id.line3);

        ll4 = (RelativeLayout) view.findViewById(R.id.relativelayout_user4);
        avatarIV4 = (ImageView) view.findViewById(R.id.imageview_avatar4);
        nameTV4 = (TextView) view.findViewById(R.id.textview_name4);
        contentTV4 = (TextView) view.findViewById(R.id.textview_content4);
        dateTV4 = (TextView) view.findViewById(R.id.textview_date4);
        moreTV4 = (TextView) view.findViewById(R.id.textview_more4);
        seperateV4 = view.findViewById(R.id.line4);

        viewHolders[0] = new DiscussionViewHolder(ll0, avatarIV0, nameTV0, contentTV0, dateTV0, null);
        viewHolders[1] = new DiscussionViewHolder(ll1, avatarIV1, nameTV1, contentTV1, dateTV1, seperateV1);
        viewHolders[2] = new DiscussionViewHolder(ll2, avatarIV2, nameTV2, contentTV2, dateTV2, seperateV2);
        viewHolders[3] = new DiscussionViewHolder(ll3, avatarIV3, nameTV3, contentTV3, dateTV3, seperateV3);
        viewHolders[4] = new DiscussionViewHolder(ll4, avatarIV4, nameTV4, contentTV4, dateTV4, seperateV4);

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.textview_more:
                enterDiscussList();
                break;
            case R.id.linearlayout_empty:
                enterDiscussSend();
                break;
        }
    }


    private void enterDiscussSend() {
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityDiscussSendFragment.BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
        pushFragment(SecurityDiscussSendFragment.class, bundle);
    }

    private void enterDiscussList() {
        if (securityDTOId == -1) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(SecurityDiscussOrNewsFragment.BUNDLE_KEY_DISCUSS_OR_NEWS_TYPE, 0);
        bundle.putBundle(SecurityDiscussOrNewsFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        bundle.putString(SecurityDiscussOrNewsFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
        bundle.putInt(SecurityDiscussOrNewsFragment.BUNDLE_KEY_SECURIYT_COMPACT_ID, securityDTOId);
        bundle.putBoolean(SecurityDiscussOrNewsFragment.BUNDLE_ARGUMENT_IS_NEWS, false);
        pushFragment(SecurityDiscussOrNewsFragment.class, bundle);

    }

    private DashboardNavigator getDashboardNavigator() {
        DashboardNavigatorActivity activity = ((DashboardNavigatorActivity) getActivity());
        if (activity != null) {
            return activity.getDashboardNavigator();
        }
        return null;
    }

    private Fragment pushFragment(Class fragmentClass, Bundle args) {
        return getDashboardNavigator().pushFragment(fragmentClass, args);
    }

    public void fetchSecurityDiscuss(boolean force) {
        if (securityDTOId == -1) {
            return;
        }
        if (discussionListKey == null) {
            discussionListKey = new PaginatedDiscussionListKey(DiscussionType.SECURITY, securityDTOId, 1, 5);
        }

        Callback<SecurityCommentList> callback = new Callback<SecurityCommentList>() {
            @Override
            public void success(SecurityCommentList securityCommentList, Response response) {
                if (securityCommentList == null) {
                    return;
                }
                updateDiscussionList(securityCommentList);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
        discussionServiceWrapper.getSecurityComment(securityId, discussionListKey, callback);
    }

    private void updateDiscussionList(SecurityCommentList securityCommentList) {
        if (getActivity() == null) {
            return;
        }
        Intent intent = new Intent(SecurityDetailFragment.ACTION_UPDATE_DISCUSSION_COUNT);
        intent.putExtra(SecurityDetailFragment.BUNDLE_KEY_DISCUSSION_COUNT, securityCommentList.commentCount);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

        List<SecurityComment> comments = securityCommentList.comments;
        if (comments.size() < 0) {
            emptyLL.setVisibility(View.VISIBLE);
            discussLL.setVisibility(View.GONE);
        } else {
            emptyLL.setVisibility(View.GONE);
            discussLL.setVisibility(View.VISIBLE);
            if (comments.size() >= 5) {
                moreTV.setVisibility(View.VISIBLE);
            } else {
                moreTV.setVisibility(View.GONE);
            }
        }
        for (int i = 0; i < comments.size(); i++) {
            viewHolders[i].displayContent(comments.get(i), prettyTime.get());
        }
        for (int i = comments.size(); i < viewHolders.length; i++) {
            viewHolders[i].gone();
        }
    }

    static class DiscussionViewHolder {
        private View parent;
        private ImageView avatar;
        private TextView name;
        private TextView content;
        private TextView date;
        private View seprator;

        public DiscussionViewHolder(View parent, ImageView avatar, TextView name, TextView content,
                                    TextView date, View seprator) {
            this.parent = parent;
            this.avatar = avatar;
            this.name = name;
            this.content = content;
            this.date = date;
            this.seprator = seprator;
        }

        public void gone() {
            parent.setVisibility(View.GONE);
            if (seprator != null) {
                seprator.setVisibility(View.GONE);
            }
        }

        public void displayContent(SecurityComment comment, PrettyTime prettyTime) {
            if (comment == null) {
                return;
            }
            parent.setVisibility(View.VISIBLE);
            if (seprator != null) {
                seprator.setVisibility(View.VISIBLE);
            }
            ImageLoader.getInstance().displayImage(comment.userPicUrl, avatar,
                    UniversalImageLoader.getAvatarImageLoaderOptions());
            name.setText(comment.userName);
            content.setText(comment.text);
            date.setText(prettyTime.formatUnrounded(comment.createdAtUtc));
        }
    }
}
