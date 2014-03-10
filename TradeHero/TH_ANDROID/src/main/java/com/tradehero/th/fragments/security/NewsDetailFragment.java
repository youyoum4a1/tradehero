package com.tradehero.th.fragments.security;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.NewsServiceWrapper;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.Random;

/**
 * Created by tradehero on 14-3-10.
 */
public class NewsDetailFragment extends DashboardFragment /*AbstractSecurityInfoFragment*/ {


    TextView titleView;
    TextView dateView;
    View titlePlaceholderView;
    TextView contentView;

    NewsItemDTO sampleItemDto;
    NewsItemDTO detailItemDto;

    @Inject
    NewsServiceWrapper newsServiceWrapper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_detail_view,container,false);
        initViews(view);
        linkWith();
        return view;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //linkWith();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews(View view)
    {
        this.titleView = (TextView)view.findViewById(R.id.news_detail_title);
        this.dateView = (TextView)view.findViewById(R.id.news_detail_date);
        this.titlePlaceholderView = view.findViewById(R.id.news_detail_title_placeholder);
        this.contentView = (TextView)view.findViewById(R.id.news_detail_content);


    }

    private void linkWith() {

//        int[] backgroundResArray = null;
//        TypedArray array = getActivity().getResources().obtainTypedArray(R.array.news_item_background_list);
//        int len = array.length();
//        backgroundResArray = new int[len];
//        for (int i=0;i<len;i++){
//            backgroundResArray[i] = array.getInt(i,0);
//        }

        Bundle args =  getArguments();
        this.sampleItemDto =  NewsItemDTO.getSampleNewsItemDTO(args);
        this.titleView.setText(sampleItemDto.title);
        PrettyTime prettyTime = new PrettyTime();
        this.dateView.setText(prettyTime.format(sampleItemDto.createdAtUtc));

        int[]backgroundResArray = getActivity().getResources().getIntArray(R.array.news_item_background_list2);

        int imageBackroundRes = new Random().nextInt(backgroundResArray.length);
        //this.titlePlaceholderView.setBackgroundResource(imageBackroundRes);

        newsServiceWrapper.getSecurityNewsDetail(sampleItemDto.id,createNewsDetailCallback());
    }

    private void fillDetailData(NewsItemDTO data) {
        this.detailItemDto = data;
        this.contentView.setText(detailItemDto.text);
    }

    private Callback<NewsItemDTO> createNewsDetailCallback() {

        return new Callback<NewsItemDTO>() {
            @Override
            public void success(NewsItemDTO newsItemDTO, Response response) {
                fillDetailData(newsItemDTO);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
    }

    @Override
    public boolean isTabBarVisible() {
        return false;
    }
}
