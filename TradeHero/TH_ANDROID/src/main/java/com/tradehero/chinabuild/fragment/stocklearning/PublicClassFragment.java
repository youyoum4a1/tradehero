package com.tradehero.chinabuild.fragment.stocklearning;

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
import com.tradehero.chinabuild.fragment.videoPlay.VideoPlayer;
import com.tradehero.th.R;
import com.tradehero.th.adapters.VideoGridAdapter;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import java.util.ArrayList;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by palmer on 15/3/27.
 */
public class PublicClassFragment extends DashboardFragment
{
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @InjectView(R.id.gridView) GridView gridView;
    private VideoGridAdapter videoGridAdapter;

    @InjectView(R.id.tradeheroProgressBar) TradeHeroProgressBar progressBar;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.stock_learning_public_class, container, false);
        ButterKnife.inject(this, view);
        initGridView();
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();
        gotoDownloadVideoList();
    }

    private void initGridViewAdapter(VideoDTOList videoDTOList)
    {
        if(videoGridAdapter == null)
        {
            videoGridAdapter = new VideoGridAdapter(getActivity());
            ArrayList<VideoDTO> listData = new ArrayList<>();
            for (int i = 0; i < videoDTOList.size(); i++)
            {
                listData.add(videoDTOList.get(i));
            }
            videoGridAdapter.setListData(listData);
        }
    }

    private void initGridView()
    {
        gridView.setAdapter(videoGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Timber.d("GridView Clicked : " + position);
                VideoDTO videoDTO = videoGridAdapter.getItem(position);
                playVideo(videoDTO.vid);
            }
        });
    }

    private void showProgressBar()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void dismissProgressBar()
    {
        progressBar.setVisibility(View.GONE);
    }

    public void playVideo(String vid)
    {
        Bundle bundle = new Bundle();
        bundle.putString(VideoPlayer.BUNDLE_VIDEO_VID,vid);
        gotoDashboard(VideoPlayer.class, bundle);
    }

    public void gotoDownloadVideoList(){
        if(videoGridAdapter == null)
        {
            showProgressBar();
            userServiceWrapper.get().downloadVideoList(new DownloadVideoListCallback());
        }
    }

    private class DownloadVideoListCallback implements Callback<VideoDTOList>
    {

        @Override
        public void success(VideoDTOList videoDTOList, Response response) {
            //THToast.show("videoDTOlist success size = " + videoDTOList.size());
            if(videoDTOList!=null)
            {
                initGridViewAdapter(videoDTOList);
                initGridView();
            }
            dismissProgressBar();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            //THToast.show("videoDTOList get fail");
            dismissProgressBar();
        }
    }
}
