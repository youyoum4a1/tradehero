package com.tradehero.chinabuild.fragment.competition;

import android.app.Activity;
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
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
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

    private final int perPage = 15;
    private int pageNum = 1;

    @Inject Lazy<CompetitionServiceWrapper> competitionService;
    private UserCompetitionDTO userCompetitionDTO;
    private int competitionId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCompetitionArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.competition_discuss_layout, container, false);
        lvTimeLine = (PullToRefreshListView)view.findViewById(R.id.pulltorefreshlistview_competition_timelines);
        ivEmpty = (ImageView)view.findViewById(R.id.imgEmpty);
        ivCreateCompetitionTimeLine = (ImageView)view.findViewById(R.id.ivCreateCompetitionTimeLine);
        ivCreateCompetitionTimeLine.setOnClickListener(this);
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
        return view;
    }

    @Override public void onAttach(Activity activity){
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch(viewId){
            case R.id.ivCreateCompetitionTimeLine:
                break;
        }
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
}
