package com.tradehero.chinabuild.fragment.competition;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;

/**
 * TimeLines for Each Competition
 *
 * Created by palmer on 15/3/2.
 */
public class CompetitionDiscussFragment extends Fragment implements View.OnClickListener{

    private PullToRefreshListView lvTimeLine;
    private ImageView ivEmpty;
    private ImageView ivCreateCompetitionTimeLine;
    private TradeHeroProgressBar tradeHeroProgressBar;

    private UserTimeLineAdapter adapter;

    private final int perPage = 20;
    private int pageNum = 1;

    @Inject Lazy<CompetitionServiceWrapper> competitionService;
    private UserCompetitionDTO userCompetitionDTO;
    private int competitionId;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private UserProfileDTO mUserProfileDTO;

    @Inject protected AlertDialogUtil alertDialogUtil;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCompetitionArguments();
        userProfileCacheListener = new UserProfileFetchListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.competition_discuss_layout, container, false);
        lvTimeLine = (PullToRefreshListView)view.findViewById(R.id.pulltorefreshlistview_competition_timelines);
        ivEmpty = (ImageView)view.findViewById(R.id.imgEmpty);
        ivCreateCompetitionTimeLine = (ImageView)view.findViewById(R.id.ivCreateCompetitionTimeLine);
        ivCreateCompetitionTimeLine.setOnClickListener(this);
        ivCreateCompetitionTimeLine.setVisibility(View.GONE);
        tradeHeroProgressBar = (TradeHeroProgressBar)view.findViewById(R.id.tradeheroprogressbar_competition_discuss);
        if(adapter==null){
            adapter = new UserTimeLineAdapter(getActivity());
        }
        lvTimeLine.setAdapter(adapter);
        lvTimeLine.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                retrieveDiscuss();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                retrieveMoreDiscuss();
            }
        });
        lvTimeLine.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        if(adapter.getCount()==0){
            tradeHeroProgressBar.setVisibility(View.VISIBLE);
            tradeHeroProgressBar.startLoading();
            retrieveDiscuss();
        }

        fetchUserProfile();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        detachUserProfileCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userProfileCacheListener=null;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch(viewId){
            case R.id.ivCreateCompetitionTimeLine:
                gotoCreateCompetitionDiscussion();
                break;
        }
    }

    private void fetchUserProfile() {
        detachUserProfileCache();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    private void getCompetitionArguments(){
        Bundle bundle = getArguments();
        if (bundle != null) {
            this.userCompetitionDTO = (UserCompetitionDTO) bundle.getSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_DTO);
            if (userCompetitionDTO != null) {
                competitionId = userCompetitionDTO.id;
            } else {
                this.competitionId = bundle.getInt(CompetitionDetailFragment.BUNDLE_COMPETITION_ID, 0);
            }
        }
    }

    private void retrieveDiscuss(){
        competitionService.get().getCompetitionDiscuss(getCompetitionId(), pageNum, perPage, new TimeLineCallback());
    }

    private void retrieveMoreDiscuss(){
        pageNum++;
        retrieveDiscuss();
    }

    private int getCompetitionId(){
        if(userCompetitionDTO!=null){
            return userCompetitionDTO.id;
        }else{
            return competitionId;
        }
    }


    public class TimeLineCallback implements Callback<TimelineDTO> {
        @Override
        public void success(TimelineDTO timelineDTO, Response response) {
            if(adapter!=null){
                if(pageNum==1) {
                    adapter.setListData(timelineDTO);
                }else{
                    adapter.addItems(timelineDTO);
                }
                adapter.notifyDataSetChanged();
            }
            onFinish();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            THException exception = new THException(retrofitError);
            THToast.show(exception.getMessage());
            if(pageNum>1){
                pageNum--;
            }
            onFinish();
        }

        private void onFinish(){
            if(lvTimeLine==null){
                return;
            }
            lvTimeLine.onRefreshComplete();
            if(tradeHeroProgressBar.getVisibility()==View.VISIBLE){
                tradeHeroProgressBar.stopLoading();
                tradeHeroProgressBar.setVisibility(View.GONE);
            }
            lvTimeLine.setEmptyView(ivEmpty);
            lvTimeLine.setMode(PullToRefreshBase.Mode.BOTH);
        }
    }

    private Fragment pushFragment(@NotNull Class fragmentClass, Bundle args) {
        return getDashboardNavigator().pushFragment(fragmentClass, args);
    }

    private DashboardNavigator getDashboardNavigator() {
        @Nullable DashboardNavigatorActivity activity = ((DashboardNavigatorActivity) getActivity());
        if (activity != null) {
            return activity.getDashboardNavigator();
        }
        return null;
    }

    private void gotoCreateCompetitionDiscussion(){
        if (mUserProfileDTO == null) {
            return;
        }
        if (mUserProfileDTO.isVisitor) {
            alertDialogUtil.popWithOkCancelButton(getActivity(), R.string.app_name,
                    R.string.guest_user_dialog_summary,
                    R.string.ok, R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (getActivity() == null) {
                                return;
                            }
                            Intent gotoAuthticationIntent = new Intent(getActivity(), AuthenticationActivity.class);
                            startActivity(gotoAuthticationIntent);
                            getActivity().finish();
                        }
                    });
        } else {
            Bundle bundle = new Bundle();
            if (userCompetitionDTO != null) {
                bundle.putSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_DTO, userCompetitionDTO);
            } else {
                bundle.putSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_ID, competitionId);
            }
            pushFragment(CompetitionDiscussionSendFragment.class, bundle);
        }
    }

    private void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    protected class UserProfileFetchListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value){
            mUserProfileDTO = value;
            if(ivCreateCompetitionTimeLine!=null && mUserProfileDTO!=null){
                if(!mUserProfileDTO.isVisitor){
                    ivCreateCompetitionTimeLine.setVisibility(View.VISIBLE);
                }
            }
        }
        @Override
        public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error) { }
    }
}
