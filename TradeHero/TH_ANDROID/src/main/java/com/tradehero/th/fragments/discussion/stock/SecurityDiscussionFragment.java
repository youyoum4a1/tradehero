package com.tradehero.th.fragments.discussion.stock;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.discussion.SecurityDiscussionEditPostFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.discussion.DiscussionListCacheRx;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import javax.inject.Inject;

public class SecurityDiscussionFragment extends Fragment
{
    private static final String BUNDLE_KEY_SECURITY_ID = "securityId";

    @Inject DiscussionListCacheRx discussionListCache;
    @InjectView(R.id.stock_discussion_view) SecurityDiscussionView securityDiscussionView;
    @InjectView(R.id.security_discussion_add) View buttonAdd;
    private SecurityId securityId;
    @Inject protected Lazy<DashboardNavigator> navigator;

    @Inject @BottomTabsQuickReturnListViewListener protected Lazy<AbsListView.OnScrollListener> dashboardBottomTabsListViewScrollListener;

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
        HierarchyInjector.inject(this);
        QuickReturnListViewOnScrollListener addBtnQuickScrollListener = new QuickReturnListViewOnScrollListener(QuickReturnType.HEADER, buttonAdd, - getResources().getDimensionPixelSize(R.dimen.clickable_element_min_dimen), null, 0);
        securityDiscussionView.setScrollListener(new MultiScrollListener(dashboardBottomTabsListViewScrollListener.get(), addBtnQuickScrollListener));
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
            navigator.get().pushFragment(SecurityDiscussionEditPostFragment.class, bundle);
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

    @Override public void onDestroyView()
    {
        securityDiscussionView.removeScrollListener();
        super.onDestroyView();
    }
}
