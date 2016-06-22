package com.androidth.general.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.preference.PreferenceManagerCompat;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.activities.DashboardActivity;
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
import com.androidth.general.utils.DeviceUtil;
import com.androidth.general.utils.StringUtils;
import com.squareup.picasso.Picasso;
import com.tencent.mm.sdk.platformtools.Log;

import java.util.ArrayList;
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

    protected EditText mSearchTextField;

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
        DeviceUtil.dismissKeyboard(getActivity());

        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        items = null;
        super.onDestroy();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        //menu.clear();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.btn_search);
        SearchView searchView = new SearchView(((DashboardActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);

        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.requestFocus();
        searchView.requestFocusFromTouch();
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("onQueryTextSubmit", query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("onQueryTextChange", newText);

                ArrayList<SecurityCompactDTO> subItems = new ArrayList<SecurityCompactDTO>();
                for(SecurityCompactDTO sec : items)
                {
                    if(sec.name.contains(newText) || sec.symbol.contains(newText))
                    {
                        subItems.add(sec);
                    }
                }
                adapter.setItems(subItems);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override public void onDestroyOptionsMenu()
    {
        mSearchTextField = null;
        super.onDestroyOptionsMenu();
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
