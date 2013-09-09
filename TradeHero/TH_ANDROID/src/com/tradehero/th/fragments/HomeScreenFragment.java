package com.tradehero.th.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.common.graphics.GaussianTransformation;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.th.R;
import com.tradehero.th.adapters.UserTimelineAdapter;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.UserTimelineService;

public class HomeScreenFragment extends SherlockFragment
{
    private ImageView userProfileAvatar;
    private ImageView userProfileBackgroundBySketchedAvatar;
    private PullToRefreshListView userTimelineItemList;
    private UserProfileDTO profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.profile_screen, container, false);
        profile = THUser.getCurrentUser();
        _initView(view);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    private void _initView(View view)
    {
        userTimelineItemList = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);
        View headerView = getActivity().getLayoutInflater().inflate(R.layout.profile_screen_user_detail, null);
        userTimelineItemList.getRefreshableView().addHeaderView(headerView);

        if (profile != null)
        {
            displayUserStatistic();

            getSherlockActivity().getSupportActionBar().setTitle(profile.displayName);

            userProfileAvatar = (ImageView) headerView.findViewById(R.id.user_profile_avatar);
            userProfileBackgroundBySketchedAvatar = (ImageView) headerView.findViewById(R.id.user_profile_background_by_sketched_avatar);
            loadPictureWithTransformation(profile.picture, new RoundedShapeTransformation()).into(userProfileAvatar);
            loadPictureWithTransformation(profile.picture, new GaussianTransformation()).into(userProfileBackgroundBySketchedAvatar);

            _getDataOfTrade();
        }
    }

    private void displayUserStatistic()
    {
        //TextView heroesCount = (TextView) getView().findViewById(R.id.user_profile_heroes_count);
        //heroesCount.setText(profile.heroIds.size());
        //
        //TextView followersCount = (TextView) getView().findViewById(R.id.user_profile_followers_count);
        //followersCount.setText(profile.followerCount);
        //
        //TextView tradesCount = (TextView) getView().findViewById(R.id.user_profile_trade_count);
        //tradesCount.setText(profile.tradesSharedCount_FB);
        //
        //TextView exchangesCount = (TextView) getView().findViewById(R.id.user_profile_exchanges_count);
        //exchangesCount.setText(profile.);
        //TextView heroesCount = (TextView) getView().findViewById(R.id.user_profile_heroes_count);

    }

    private RequestCreator loadPictureWithTransformation(String url, Transformation transformation)
    {
        return Picasso.with(getActivity()).load(url).transform(transformation);
    }

    private void _getDataOfTrade()
    {
        NetworkEngine.createService(UserTimelineService.class)
                .getTimeline(profile.id, 42, new THCallback<TimelineDTO>()
                {
                    @Override protected void success(TimelineDTO timelineDTO, THResponse thResponse)
                    {
                        userTimelineItemList.setVisibility(View.VISIBLE);
                        refreshTimeline(timelineDTO);
                    }

                    @Override protected void failure(THException ex)
                    {
                    }
                });
    }

    private void refreshTimeline(TimelineDTO timelineDTO)
    {
        userTimelineItemList.setAdapter(new UserTimelineAdapter(getActivity(), timelineDTO));
    }
}
