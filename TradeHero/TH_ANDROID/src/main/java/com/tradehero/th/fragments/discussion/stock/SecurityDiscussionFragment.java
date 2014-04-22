package com.tradehero.th.fragments.discussion.stock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.discussion.DiscussionEditPostFragment;

/**
 * Created by thonguyen on 4/4/14.
 */
public class SecurityDiscussionFragment extends DashboardFragment
{
    @InjectView(R.id.stock_discussion_view) SecurityDiscussionView securityDiscussionView;
    private SecurityId securityId;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.security_discussion, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();

        Bundle args = getArguments();
        if (args != null)
        {
            Bundle securityIdBundle = args.getBundle(SecurityId.BUNDLE_KEY_SECURITY_ID_BUNDLE);
            if (securityIdBundle != null)
            {
                linkWith(new SecurityId(securityIdBundle), true);
            }
        }
    }

    @OnClick(R.id.security_discussion_add) void onAddNewDiscussionRequested()
    {
        if (securityId != null)
        {
            Bundle bundle = new Bundle();
            bundle.putBundle(SecurityId.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
            getNavigator().pushFragment(DiscussionEditPostFragment.class, bundle);
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

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
