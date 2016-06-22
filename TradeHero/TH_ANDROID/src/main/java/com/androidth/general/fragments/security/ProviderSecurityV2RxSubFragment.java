package com.androidth.general.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.key.BasicProviderSecurityV2ListType;
import com.androidth.general.api.market.Exchange;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.security.ExchangeCompactDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityCompactDTOList;
import com.androidth.general.api.security.SecurityCompactDTOUtil;
import com.androidth.general.api.security.SecurityCompositeDTO;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.fragments.billing.BasePurchaseManagerFragment;
import com.androidth.general.fragments.trade.AbstractBuySellFragment;
import com.androidth.general.persistence.security.SecurityCompositeListCacheRx;
import com.androidth.general.utils.StringUtils;
import com.squareup.picasso.Picasso;
import com.tencent.mm.sdk.platformtools.Log;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class ProviderSecurityV2RxSubFragment extends BasePurchaseManagerFragment
{
    @Bind(R.id.listview) protected AbsListView listView;
    @Bind(R.id.progress) protected ProgressBar progressBar;
    private static final String BUNDLE_PROVIDER_ID_KEY = ProviderSecurityListRxFragment.class.getName() + ".providerId";
    protected ProviderId providerId;
    private static List<SecurityCompactDTO>  items;
    SimpleSecurityItemViewAdapter adapter;

    public static void setItems(List<SecurityCompactDTO> items)
    {
        ProviderSecurityV2RxSubFragment.items = items;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new SimpleSecurityItemViewAdapter(getContext(), R.layout.trending_security_item);
        adapter.setItems(items);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_provider_security_list, container, false);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        this.listView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @OnItemClick(R.id.listview)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //noinspection unchecked
        Log.i("onItemClick", String.valueOf(position));

        SecurityCompactDTO clicked = (SecurityCompactDTO)parent.getItemAtPosition(position);

        Bundle args = getArguments();
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        AbstractBuySellFragment.putRequisite(
                args,
                new AbstractBuySellFragment.Requisite(
                        clicked.getSecurityId(),
                        applicablePortfolioId,
                        0)); // TODO proper
        navigator.get().pushFragment(SecurityCompactDTOUtil.fragmentFor(clicked), args);
    }

}
