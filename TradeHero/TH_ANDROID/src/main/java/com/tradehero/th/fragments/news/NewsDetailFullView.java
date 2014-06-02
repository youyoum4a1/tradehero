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
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;
import java.util.Map;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewsDetailFullView extends LinearLayout
        implements DTOView<NewsItemDTO>
{
    @InjectView(R.id.news_detail_wrapper) BetterViewAnimator mNewsContentWrapper;
    @InjectView(R.id.discussion_content) TextView mNewsDetailContent;
    @InjectView(R.id.news_detail_reference) GridView mNewsDetailReference;
    @InjectView(R.id.news_view_on_web) TextView mNewsViewOnWeb;
    @InjectView(R.id.news_detail_reference_container) LinearLayout mNewsDetailReferenceContainer;

    @Inject SecurityServiceWrapper securityServiceWrapper;

    private SimpleSecurityItemViewAdapter simpleSecurityItemViewAdapter;
    private NewsItemDTO newsItemDTO;
    private MiddleCallback<Map<Integer, SecurityCompactDTO>> fetchMultipleSecurityMiddleCallback;

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

    @OnClick(R.id.news_view_on_web) void onItemClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.news_view_on_web:
                openNewsOnWeb();
                break;
        }
    }

    private void openNewsOnWeb()
    {
        if (newsItemDTO != null)
        {
            Bundle bundle = new Bundle();
            bundle.putString(BaseWebViewFragment.BUNDLE_KEY_URL, newsItemDTO.url);
            getNavigator().pushFragment(WebViewFragment.class, bundle);
        }
    }

    private void initViews()
    {
        simpleSecurityItemViewAdapter = new SimpleSecurityItemViewAdapter(
                getContext(), LayoutInflater.from(getContext()), R.layout.trending_security_item);
        mNewsDetailReference.setAdapter(simpleSecurityItemViewAdapter);
        mNewsDetailReference.setOnItemClickListener(new AdapterView.OnItemClickListener()
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
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        unsetFetchMultipleSecurityMiddleCallback();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(NewsItemDTO dto)
    {
        this.newsItemDTO = dto;
        if (dto != null)
        {
            displayNewsDetailContent(dto);

            displaySecurityLogoList(dto);
        }
    }

    private void displaySecurityLogoList(NewsItemDTO dto)
    {
        if (dto.securityIds != null && !dto.securityIds.isEmpty())
        {
            unsetFetchMultipleSecurityMiddleCallback();
            fetchMultipleSecurityMiddleCallback = securityServiceWrapper.getMultipleSecurities(dto.securityIds, createNewsDetailSecurityCallback());
        }
    }

    private void displayNewsDetailContent(NewsItemDTO dto)
    {
        if (mNewsDetailContent != null && dto.text != null)
        {
            mNewsDetailContent.setText(dto.text);
            mNewsContentWrapper.setDisplayedChildByLayoutId(mNewsDetailContent.getId());
        }
    }

    private void unsetFetchMultipleSecurityMiddleCallback()
    {
        if (fetchMultipleSecurityMiddleCallback != null)
        {
            fetchMultipleSecurityMiddleCallback.setPrimaryCallback(null);
        }
        fetchMultipleSecurityMiddleCallback = null;
    }

    private Callback<Map<Integer, SecurityCompactDTO>> createNewsDetailSecurityCallback()
    {
        return new Callback<Map<Integer, SecurityCompactDTO>>()
        {
            @Override public void success(Map<Integer, SecurityCompactDTO> securityCompactDTOList, Response response)
            {
                if (mNewsDetailReferenceContainer == null || mNewsDetailReference == null || simpleSecurityItemViewAdapter == null || securityCompactDTOList == null)
                {
                    return; // TODO proper handling of middle callback
                }

                ViewGroup.LayoutParams lp = mNewsDetailReferenceContainer.getLayoutParams();
                //TODO it changes with solution
                lp.width = (int) getResources().getDimension(R.dimen.stock_item_width) * securityCompactDTOList.size();
                mNewsDetailReferenceContainer.setLayoutParams(lp);
                mNewsDetailReference.setNumColumns(securityCompactDTOList.size());
                simpleSecurityItemViewAdapter.setItems(new ArrayList<>(securityCompactDTOList.values()));
                simpleSecurityItemViewAdapter.notifyDataSetChanged();
            }

            @Override public void failure(RetrofitError error)
            {
                THToast.show(new THException(error));
            }
        };
    }
}
