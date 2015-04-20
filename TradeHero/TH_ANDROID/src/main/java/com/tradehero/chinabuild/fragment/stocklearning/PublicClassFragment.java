package com.tradehero.chinabuild.fragment.stocklearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.chinabuild.data.VideoDTO;
import com.tradehero.chinabuild.data.VideoDTOList;
import com.tradehero.th.R;
import com.tradehero.th.activities.VideoPlayActivity;
import com.tradehero.th.adapters.VideoGridAdapter;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * Created by palmer on 15/3/27.
 */
public class PublicClassFragment extends DashboardFragment {
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @InjectView(R.id.gridView) GridView gridView;
    private VideoGridAdapter videoGridAdapter;
    @Inject Analytics analytics;

    @InjectView(R.id.tradeheroProgressBar) TradeHeroProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        videoGridAdapter = new VideoGridAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stock_learning_public_class, container, false);
        ButterKnife.inject(this, view);
        initGridView();
        if(videoGridAdapter.getCount()<=0) {
            showProgressBar();
            gotoDownloadVideoList();
        }
        return view;
    }

    private void initGridViewAdapter(VideoDTOList videoDTOList) {
        ArrayList<VideoDTO> listData = new ArrayList<>();
        for (int i = 0; i < videoDTOList.size(); i++) {
            listData.add(videoDTOList.get(i));
        }
        videoGridAdapter.setListData(listData);
    }

    private void initGridView() {
        gridView.setAdapter(videoGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                VideoDTO videoDTO = videoGridAdapter.getItem(position);
                playVideo(videoDTO);
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.VIDEO_SELECT, String.valueOf(position)));
            }
        });
    }

    private void showProgressBar() {
        if(progressBar!=null) {
            progressBar.startLoading();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void dismissProgressBar() {
        if(progressBar!=null) {
            progressBar.stopLoading();
            progressBar.setVisibility(View.GONE);
        }
    }

    public void playVideo(VideoDTO videoDTO) {
        if(getActivity()==null){
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(VideoPlayer.BUNDLE_VIDEO_VID, videoDTO.vid);
        Intent playVideoIntent = new Intent(getActivity(), VideoPlayActivity.class);
        playVideoIntent.putExtras(bundle);
        getActivity().startActivity(playVideoIntent);
    }

    public void gotoDownloadVideoList() {
        userServiceWrapper.get().downloadVideoList(new DownloadVideoListCallback());
    }

    private class DownloadVideoListCallback implements Callback<VideoDTOList> {

        @Override
        public void success(VideoDTOList videoDTOList, Response response) {
            if(getActivity()==null){
                return;
            }
            if (videoDTOList != null) {
                initGridViewAdapter(videoDTOList);
            }
            dismissProgressBar();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            dismissProgressBar();
        }
    }
}
