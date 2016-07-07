package com.androidth.general.fragments.contestcenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderDTOList;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.api.competition.key.ProviderListKey;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.persistence.competition.ProviderListCacheRx;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ContestCenterFragment extends DashboardFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;


    @Inject ProviderListCacheRx providerListCache;
    @Inject ProviderUtil providerUtil;
    @Bind(R.id.competition_list) RecyclerView competitionList;
    List<MultipleCompetitionData> multipleCompetitionDatas = new ArrayList<>();

    //private BaseLiveFragmentUtil liveFragmentUtil;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.dashboard_contest_center);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_contest_center, container, false);
        ButterKnife.bind(this, view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        competitionList.setLayoutManager(layoutManager);
        fetchProviderIdList();
        //initViews();
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();
        //loadContestData();
    }
    @Override public void onStart(){
        super.onStart();
        //loadContestData();
    }

    @Override public void onLiveTradingChanged(boolean isLive)
    {
        super.onLiveTradingChanged(isLive);

    }

    @Override public void onDestroyView()
    {

        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void fetchProviderIdList()
    {
        ProviderDTOList providerList = providerListCache.getCachedValue(new ProviderListKey());
        for(int i = 0; i < providerList.size(); i++){
            ProviderDTO providerDTO = providerList.get(i);
            multipleCompetitionDatas.add(new MultipleCompetitionData(providerDTO.multiImageUrl, providerDTO.isUserEnrolled));
        }
        competitionList.setAdapter(new MultipleCompetitionsAdapter(multipleCompetitionDatas, getContext()));

        /*if (providerList != null && providerList.size() != 0) {
            ProviderDTO providerDTO = providerList.get(0);
            if (providerDTO != null && providerDTO.isUserEnrolled) {
                Bundle args = new Bundle();
                MainCompetitionFragment.putProviderId(args, providerDTO.getProviderId());
                OwnedPortfolioId applicablePortfolioId = providerDTO.getAssociatedOwnedPortfolioId();
                if (applicablePortfolioId != null) {
                    MainCompetitionFragment.putApplicablePortfolioId(args, applicablePortfolioId);
                }
                navigator.get().pushFragment(MainCompetitionFragment.class, args);
            } else if (providerDTO != null) {
                Bundle args = new Bundle();
                CompetitionWebViewFragment.putUrl(args, providerUtil.getLandingPage(
                        providerDTO.getProviderId()
                ));
                navigator.get().pushFragment(CompetitionWebViewFragment.class, args);
            }
        }*/
    }

    private void initViews()
    {
        ContestCenterPagerAdapter adapter = new ContestCenterPagerAdapter(getChildFragmentManager());
        //fetchProviderIdList();
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

    }

    private class SingleCompetitionWebview {
        String webViewUrl;
        SingleCompetitionWebview(String webViewUrl){
            this.webViewUrl = webViewUrl;
        }
    }











    private class MultipleCompetitionData{
        String imageUrl;
        boolean isEnrolled;
        MultipleCompetitionData(String imageUrl, boolean isEnrolled){
            this.imageUrl = imageUrl;
            this.isEnrolled = isEnrolled;
        }
    }
    class MultipleCompetitionsAdapter extends RecyclerView.Adapter<MultipleCompetitionViewHolder>{
        List<MultipleCompetitionData> multipleCompetitionDataList;
        Context context;
        MultipleCompetitionsAdapter(List<MultipleCompetitionData> multipleCompetitionDataList, Context context){
            this.multipleCompetitionDataList = multipleCompetitionDataList;
            this.context = context;
        }
        @Override
        public MultipleCompetitionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.competition_list_item, null);
            MultipleCompetitionViewHolder multipleCompetitionViewHolder = new MultipleCompetitionViewHolder(view);
            return multipleCompetitionViewHolder;
        }

        @Override
        public void onBindViewHolder(MultipleCompetitionViewHolder holder, int position) {
            MultipleCompetitionData multipleCompetitionData = multipleCompetitionDataList.get(position);
            Picasso.with(context).load(multipleCompetitionData.imageUrl).into(holder.imageView);
            if(multipleCompetitionData.isEnrolled){
                holder.enrolledImage.setVisibility(View.VISIBLE);
            }
            else {
                holder.enrolledImage.setVisibility(View.INVISIBLE);
            }
        }


        @Override
        public int getItemCount() {
            return multipleCompetitionDataList.size();
        }
    }
    class MultipleCompetitionViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageView enrolledImage;
        public MultipleCompetitionViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView)itemView.findViewById(R.id.competition_banner);
            this.enrolledImage = (ImageView)itemView.findViewById(R.id.enrolled_tick);
        }
    }









    /*class SingleCompetitionsAdapter extends RecyclerView.Adapter<>{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }*/
    //We also need an observable which will fire CompetitionData as they are recieved from cache to Picasso which will again fire bitmap to our list of adapter


    private class ContestCenterPagerAdapter extends FragmentPagerAdapter
    {
        public ContestCenterPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override public Fragment getItem(int position)
        {
            ContestCenterTabType tabType = ContestCenterTabType.values()[position];
            Bundle args = getArguments();
            if (args == null)
            {
                args = new Bundle();
            }
            return Fragment.instantiate(getActivity(), tabType.tabClass.getName(), args);
        }

        @Override public int getCount()
        {
            //return ContestCenterTabType.values().length;
            return 1;//only Active tab
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return getString(ContestCenterTabType.values()[position].titleRes);
        }
    }
}
