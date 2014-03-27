package com.tradehero.th.fragments.news;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/24/14 Time: 5:36 PM Copyright (c) TradeHero
 */
public class NewsDetailFullView extends LinearLayout
        implements DTOView<NewsItemDTO>
{
    @InjectView(R.id.news_detail_desc) TextView mNewsDetailDesc;
    @InjectView(R.id.news_detail_content) TextView mNewsDetailContent;
    @InjectView(R.id.news_detail_loading) TextView mNewsDetailLoading;
    @InjectView(R.id.news_detail_reference_gv) GridView mNewsDetailReferenceGv;
    @InjectView(R.id.news_detail_reference_gv_container) LinearLayout mNewsDetailReferenceGvContainer;

    @Inject SecurityServiceWrapper securityServiceWrapper;

    private SimpleSecurityItemViewAdapter simpleSecurityItemViewAdapter;

    //<editor-fold desc="Constructors">
    public NewsDetailFullView(Context context)
    {
        super(context);
    }

    public NewsDetailFullView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NewsDetailFullView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);
        initViews();
    }

    private void initViews()
    {
        simpleSecurityItemViewAdapter = new SimpleSecurityItemViewAdapter(
                getContext(), LayoutInflater.from(getContext()), R.layout.trending_security_item);
        mNewsDetailReferenceGv.setAdapter(simpleSecurityItemViewAdapter);
        mNewsDetailReferenceGv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Object item = simpleSecurityItemViewAdapter.getItem(position);

                if (item instanceof SecurityCompactDTO)
                {
                    SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) item;
                    Bundle args = new Bundle();
                    args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityCompactDTO.getSecurityId().getArgs());
                    getNavigator().pushFragment(BuySellFragment.class, args);
                }
            }
        });
    }

    private Navigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(NewsItemDTO dto)
    {
        if (dto != null)
        {
            mNewsDetailContent.setText(dto.text);
            mNewsDetailContent.setVisibility(View.VISIBLE);
            mNewsDetailLoading.setVisibility(View.GONE);

            if (dto.getSecurityIds() != null)
            {
                securityServiceWrapper.getMultipleSecurities2(createNewsDetailSecurityCallback(), dto.getSecurityIds());
            }
        }
    }

    private Callback<List<SecurityCompactDTO>> createNewsDetailSecurityCallback()
    {
        return new Callback<List<SecurityCompactDTO>>()
        {
            @Override
            public void success(List<SecurityCompactDTO> securityCompactDTOList, Response response)
            {
                ViewGroup.LayoutParams lp = mNewsDetailReferenceGvContainer.getLayoutParams();
                //TODO it changes with solution
                lp.width = 510 * securityCompactDTOList.size();
                mNewsDetailReferenceGvContainer.setLayoutParams(lp);
                mNewsDetailReferenceGv.setNumColumns(securityCompactDTOList.size());
                simpleSecurityItemViewAdapter.setItems(securityCompactDTOList);
                simpleSecurityItemViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error)
            {
            }
        };
    }
}
