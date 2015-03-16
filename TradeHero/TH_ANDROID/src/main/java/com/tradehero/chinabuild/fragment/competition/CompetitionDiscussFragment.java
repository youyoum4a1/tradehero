package com.tradehero.chinabuild.fragment.competition;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.adapters.SecurityTimeLineDiscussOrNewsAdapter;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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

    private SecurityTimeLineDiscussOrNewsAdapter adapter;

    @Inject Lazy<CompetitionServiceWrapper> competitionService;
    private UserCompetitionDTO userCompetitionDTO;
    private int competitionId;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCache> userProfileCache;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private UserProfileDTO mUserProfileDTO;

    @Inject protected AlertDialogUtil alertDialogUtil;

    @Inject DiscussionCache discussionCache;
    @Inject DiscussionListCacheNew discussionListCache;
    private PaginatedDiscussionListKey discussionListKey;

    private CompetitionDiscussListener competitionDiscussListener;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    public final static String INTENT_REFRESH_COMPETITION_DISCUSSIONS = "intent_refresh_competition_discussions";
    private IntentFilter intentFilter = new IntentFilter();
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(INTENT_REFRESH_COMPETITION_DISCUSSIONS)) {
                discussionListKey.page = 1;
                fetchSecurityDiscuss();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCompetitionArguments();
        userProfileCacheListener = new UserProfileFetchListener();
        discussionListKey = new PaginatedDiscussionListKey(DiscussionType.COMPETITION, getCompetitionId(), 1, 20);
        discussionListCache.invalidate(discussionListKey);

        intentFilter.addAction(INTENT_REFRESH_COMPETITION_DISCUSSIONS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.competition_discuss_layout, container, false);
        lvTimeLine = (PullToRefreshListView) view.findViewById(R.id.pulltorefreshlistview_competition_timelines);
        ivEmpty = (ImageView) view.findViewById(R.id.imgEmpty);
        ivCreateCompetitionTimeLine = (ImageView) view.findViewById(R.id.ivCreateCompetitionTimeLine);
        ivCreateCompetitionTimeLine.setOnClickListener(this);
        ivCreateCompetitionTimeLine.setVisibility(View.GONE);
        tradeHeroProgressBar = (TradeHeroProgressBar) view.findViewById(R.id.tradeheroprogressbar_competition_discuss);
        if (adapter == null) {
            adapter = new SecurityTimeLineDiscussOrNewsAdapter(getActivity());
        }
        adapter.setTimeLineOperater(new UserTimeLineAdapter.TimeLineOperater() {
            @Override
            public void OnTimeLineItemClicked(int position) {
                AbstractDiscussionCompactDTO dto = adapter.getItem(position);
                enterTimeLineDetail(dto);
            }

            @Override
            public void OnTimeLinePraiseClicked(int position) {
            }

            @Override
            public void OnTimeLinePraiseDownClicked(int position) {
            }

            @Override
            public void OnTimeLineCommentsClicked(int position) {
            }

            @Override
            public void OnTimeLineShareClicked(int position) {
            }

            @Override
            public void OnTimeLineBuyClicked(int position) {
            }
        });

        lvTimeLine.setAdapter(adapter);
        lvTimeLine.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                discussionListKey.page = 1;
                fetchSecurityDiscuss();
            }

             @Override
             public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchSecurityDiscuss();
             }
        });
        lvTimeLine.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        if (adapter.getCount() == 0)

        {
            tradeHeroProgressBar.setVisibility(View.VISIBLE);
            tradeHeroProgressBar.startLoading();
            discussionListKey.page = 1;
            fetchSecurityDiscuss();
        }

        fetchUserProfile();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        detachUserProfileCache();
        detachCompetitionDiscuss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userProfileCacheListener=null;
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
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

    public void fetchSecurityDiscuss()
    {
        if (discussionListKey != null)
        {
            detachCompetitionDiscuss();
            competitionDiscussListener = new CompetitionDiscussListener();
            discussionListCache.register(discussionListKey, competitionDiscussListener);
            discussionListCache.getOrFetchAsync(discussionListKey, true);
        }
    }

    private void detachCompetitionDiscuss()
    {
        if(competitionDiscussListener!=null) {
            discussionListCache.unregister(competitionDiscussListener);
        }
    }

    private int getCompetitionId(){
        if(userCompetitionDTO!=null){
            return userCompetitionDTO.id;
        }else{
            return competitionId;
        }
    }


    public class CompetitionDiscussListener implements DiscussionListCacheNew.DiscussionKeyListListener {
        @Override
        public void onDTOReceived(@NotNull DiscussionListKey key, @NotNull DiscussionKeyList value) {
            List<AbstractDiscussionCompactDTO> listData = new ArrayList<>();
            for (int i = 0; i < value.size(); i++) {
                AbstractDiscussionCompactDTO dto = discussionCache.get(value.get(i));
                listData.add(dto);
            }

            if (discussionListKey.page == 1) {
                adapter.setListDataWithoutEmpty(listData);
            } else {
                adapter.addListData(listData);
            }

            if (value != null && value.size() > 0) {
                discussionListKey.page += 1;
            }
            onFinish();
        }

        @Override
        public void onErrorThrown(@NotNull DiscussionListKey key, @NotNull Throwable error) {
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
            lvTimeLine.getRefreshableView().setEmptyView(ivEmpty);
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

    private void enterTimeLineDetail(AbstractDiscussionCompactDTO dto){
        Bundle bundle = new Bundle();
        bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_ID, dto.getDiscussionKey().getArgs());
        bundle.putInt(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_TYPE, TimeLineItemDetailFragment.DISCUSSION_DISCUSSION_TYPE);
        pushFragment(TimeLineItemDetailFragment.class, bundle);
    }
}
