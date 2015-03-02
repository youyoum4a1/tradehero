package com.tradehero.chinabuild.fragment.competition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by palmer on 15/3/2.
 */
public class CompetitionMainFragment extends DashboardFragment {

    private UserCompetitionDTO userCompetitionDTO;
    private int competitionId;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundleCompetition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.competition_detail_main, container, false);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setMiddleMain();
        setInviteFriendView();
    }

    private void getBundleCompetition() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            userCompetitionDTO = (UserCompetitionDTO) bundle.getSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_DTO);
            if (userCompetitionDTO != null) {
                competitionId = userCompetitionDTO.id;
            } else {
                competitionId = bundle.getInt(CompetitionDetailFragment.BUNDLE_COMPETITION_ID, 0);
            }
        }
    }

    private void setInviteFriendView() {
        if (userCompetitionDTO != null && userCompetitionDTO.isEnrolled && userCompetitionDTO.isOngoing) {
            setHeadViewRight0("邀请好友");
        }
    }

    private void setMiddleMain(){
        if(userCompetitionDTO!=null && userCompetitionDTO.name!=null){
            if(userCompetitionDTO.name.length()>6){
                setHeadViewMiddleMain(userCompetitionDTO.name.substring(0, 6));
            }else{
                setHeadViewMiddleMain(userCompetitionDTO.name);
            }
        }else{
            setHeadViewMiddleMain("比赛详情");
        }
    }

}
