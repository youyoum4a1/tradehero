package com.tradehero.th.fragments.discussion.stock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.discussion.SecurityDiscussionEditPostFragment;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import javax.inject.Inject;

public class SecurityDiscussionFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_SECURITY_ID = SecurityDiscussionFragment.class.getName() + ".securityId";

    @Inject DiscussionListCacheNew discussionListCache;
    @InjectView(R.id.stock_discussion_view) SecurityDiscussionView securityDiscussionView;
    private SecurityId securityId;

    public static void putSecurityId(Bundle args, SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
    }

    public static SecurityId getSecurityId(Bundle args)
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
        View view = inflater.inflate(R.layout.security_discussion, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();

        SecurityId fromArgs = getSecurityId(getArguments());
        if (fromArgs != null)
        {
            linkWith(fromArgs, true);
        }
    }

    @OnClick(R.id.security_discussion_add) void onAddNewDiscussionRequested()
    {
        if (securityId != null)
        {
            Bundle bundle = new Bundle();
            SecurityDiscussionEditPostFragment.putSecurityId(bundle, securityId);
            getDashboardNavigator().pushFragment(SecurityDiscussionEditPostFragment.class, bundle);
        }
    }

    private void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId  = securityId;

        if (andDisplay)
        {
            securityDiscussionView.display(securityId);
        }
    }

    @Override public void onDestroy()
    {
        //invalidate cache
        if (discussionListCache != null)
        {
            discussionListCache.invalidateAllForDiscussionType(DiscussionType.SECURITY);
        }
        super.onDestroy();
    }
}
