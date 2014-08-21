package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.timeline.SubTimelineAdapter;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

public class DiscoveryActivityFragment extends SherlockFragment
{
    private static final int TIMELINE_LOADER_ID = 0;

    @InjectView(R.id.content_wrapper) BetterViewAnimator mContentWrapper;
    @InjectView(android.R.id.progress) ProgressBar mProgressBar;
    @InjectView(R.id.timeline_list_view) AbsListView mTimelineListView;

    private int mDisplayedViewId;
    private SubTimelineAdapter mTimelineAdapter;
    @Inject CurrentUserId currentUserId;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_activity, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        ButterKnife.inject(this, view);

        mTimelineAdapter = new SubTimelineAdapter(getActivity(), getActivity().getLayoutInflater(), TIMELINE_LOADER_ID, R.layout.timeline_item_view);
        mTimelineAdapter.setDTOLoaderCallback(new LoaderDTOAdapter.ListLoaderCallback<TimelineItemDTOKey>()
        {
            @Override protected void onLoadFinished(ListLoader<TimelineItemDTOKey> loader, List<TimelineItemDTOKey> data)
            {
                mContentWrapper.setDisplayedChildByLayoutId(mTimelineListView.getId());
                Timber.d("count: %d", data.size());
            }

            @Override protected ListLoader<TimelineItemDTOKey> onCreateLoader(Bundle args)
            {
                return new TimelineListLoader(getActivity(), currentUserId.toUserBaseKey());
            }
        });

        mTimelineListView.setAdapter(mTimelineAdapter);
        getActivity().getSupportLoaderManager().initLoader(
                mTimelineAdapter.getLoaderId(), null,
                mTimelineAdapter.getLoaderCallback());
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override public void onResume()
    {
        super.onResume();

        if (mDisplayedViewId > 0)
        {
            mContentWrapper.setDisplayedChildByLayoutId(mDisplayedViewId);
        }
    }

    @Override public void onPause()
    {
        mDisplayedViewId = mContentWrapper.getDisplayedChildLayoutId();
        super.onPause();
    }
}
