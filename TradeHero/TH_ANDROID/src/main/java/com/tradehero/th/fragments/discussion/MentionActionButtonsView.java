package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.persistence.user.UserSearchResultCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class MentionActionButtonsView extends LinearLayout
{
    private static final String SECURITY_TAG_FORMAT = "[$%s](tradehero://security/%d_%s)";
    private static final String MENTIONED_FORMAT = "<@@%s,%d@>";

    @Inject UserSearchResultCache userSearchResultCache;

    @InjectView(R.id.btn_mention) TextView mMention;
    @InjectView(R.id.btn_security_tag) TextView mSecurityTag;

    //<editor-fold desc="Constructors">
    public MentionActionButtonsView(Context context)
    {
        super(context);
    }

    public MentionActionButtonsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MentionActionButtonsView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        DaggerUtils.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    protected Navigator getNavigator()
    {
        return ((NavigatorActivity) getContext()).getNavigator();
    }

    //<editor-fold desc="To be used in future, we should encapsulate searching for people and stock within this view, instead of doing it in the parent fragment">
    public static interface OnMentionListener
    {
        void onMentioned(UserBaseKey userBaseKey);
    }

    public static interface OnSecurityTaggedListener
    {
        void onTagged(SecurityId securityId);
    }
    //</editor-fold>
}
