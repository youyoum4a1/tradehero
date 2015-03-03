package com.tradehero.chinabuild.fragment.competition;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.chinabuild.dialog.ShareSheetDialogLayout;
import com.tradehero.chinabuild.fragment.MyFragmentPagerAdapter;
import com.tradehero.chinabuild.fragment.message.DiscoveryDiscussSendFragment;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Created by palmer on 15/3/2.
 */
public class CompetitionMainFragment extends DashboardFragment {

    private UserCompetitionDTO userCompetitionDTO;
    private int competitionId = 0;
    @Inject CurrentUserId currentUserId;

    private Dialog mShareSheetDialog;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    private View detailLineView;
    private View discussLineView;
    private TextView detailTV;
    private TextView discussTV;

    private ViewPager viewPager;

    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private ArrayList<Fragment> fragmentList = new ArrayList();

    private CompetitionDetailFragment competitionDetailFragment;
    private CompetitionDiscussFragment competitionDiscussFragment;

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBundleCompetition();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }
        view = inflater.inflate(R.layout.competition_detail_main, container, false);
        detailLineView = view.findViewById(R.id.view_competition_detail_line);
        discussLineView = view.findViewById(R.id.view_competition_discuss_line);
        detailTV = (TextView)view.findViewById(R.id.textview_competition_detail_subtitle);
        discussTV = (TextView)view.findViewById(R.id.textview_competition_discuss_subtitle);
        viewPager = (ViewPager)view.findViewById(R.id.viewpager_competition_detail_page);

        initViewPager();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setMiddleMain();
        setInviteFriendView();
    }

    private void initViewPager(){
        if(myFragmentPagerAdapter == null){
            myFragmentPagerAdapter = new MyFragmentPagerAdapter(getFragmentManager(), fragmentList);
            fragmentList.clear();
            initCompetitionDetailFragment();
            initCompetitionDiscussFragment();
            fragmentList.add(competitionDetailFragment);
            fragmentList.add(competitionDiscussFragment);
        }

        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void initCompetitionDetailFragment(){
        competitionDetailFragment = new CompetitionDetailFragment();
        Bundle bundle = new Bundle();
        if(userCompetitionDTO!=null){
            bundle.putSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_DTO, userCompetitionDTO);
        }else{
            bundle.putSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_ID, competitionId);
        }
        competitionDetailFragment.setArguments(bundle);
    }

    private void initCompetitionDiscussFragment(){
        competitionDiscussFragment = new CompetitionDiscussFragment();
        Bundle bundle = new Bundle();
        if(userCompetitionDTO!=null){
            bundle.putSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_DTO, userCompetitionDTO);
        }else{
            bundle.putSerializable(CompetitionDetailFragment.BUNDLE_COMPETITION_ID, competitionId);
        }
        competitionDiscussFragment.setArguments(bundle);
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
                setHeadViewMiddleMain(userCompetitionDTO.name.substring(0, 6) + "...");
            }else{
                setHeadViewMiddleMain(userCompetitionDTO.name);
            }
        }else{
            setHeadViewMiddleMain("比赛详情");
        }
    }

    public void inviteFriendsToCompetition()
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DiscoveryDiscussSendFragment.BUNDLE_KEY_COMPETITION, userCompetitionDTO);
        pushFragment(DiscoveryDiscussSendFragment.class, bundle);
    }

    @Override
    public void onClickHeadRight0()
    {
        if (userCompetitionDTO == null)
        {
            return;
        }
        if(getActivity()==null){
            return;
        }
        String endPoint = THSharePreferenceManager.getShareEndPoint(getActivity());
        mShareSheetTitleCache.set(getString(R.string.share_detial_contest,
                currentUserId.get().toString(), userCompetitionDTO.id, userCompetitionDTO.name, endPoint));
        ShareSheetDialogLayout contentView = (ShareSheetDialogLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.share_sheet_local_dialog_layout, null);
        contentView.setLocalSocialClickedListener(
                new ShareSheetDialogLayout.OnLocalSocialClickedListener()
                {
                    @Override public void onShareRequestedClicked()
                    {
                        inviteFriendsToCompetition();
                        if (mShareSheetDialog != null)
                        {
                            mShareSheetDialog.dismiss();
                        }
                    }
                });
        mShareSheetDialog = THDialog.showUpDialog(getActivity(), contentView);
    }

}
