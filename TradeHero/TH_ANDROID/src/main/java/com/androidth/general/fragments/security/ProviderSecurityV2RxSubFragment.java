package com.androidth.general.fragments.security;

import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.androidth.general.R;
import com.androidth.general.activities.DashboardActivity;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityCompactDTOUtil;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.fragments.billing.BasePurchaseManagerFragment;
import com.androidth.general.fragments.trade.AbstractBuySellFragment;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.retrofit.RequestHeaders;
import com.androidth.general.network.service.SignalRInterface;
import com.androidth.general.utils.Constants;
import com.androidth.general.utils.DeviceUtil;
import com.tencent.mm.sdk.platformtools.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;

public class ProviderSecurityV2RxSubFragment extends BasePurchaseManagerFragment implements SignalRInterface

{
    @BindView(R.id.listview) protected AbsListView listView;
    @BindView(R.id.progress) protected ProgressBar progressBar;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected RequestHeaders requestHeaders;

    private static final String BUNDLE_PROVIDER_ID_KEY = ProviderSecurityListRxFragment.class.getName() + ".providerId";
    protected ProviderId providerId;
    private static List<SecurityCompactDTO>  items;
    SimpleSecurityItemViewAdapter adapter;
    HubProxy proxy;
    List<SecurityCompactDTO> currentVisibleItemsList;
    private Unbinder unbinder;

    public HubConnection setConnection(String url) {
        return new HubConnection(url);
    }

    public HubProxy setProxy(String hubName, HubConnection connection) { return connection.createHubProxy(hubName); }

    public String[] getSecurityIds(List<SecurityCompactDTO> items){
        Iterator<SecurityCompactDTO> iterator = items.iterator();
        ArrayList<String > stringArray = new ArrayList<>();

        while (iterator.hasNext()) {
            stringArray.add(iterator.next().getResourceId()+"");
        }

        String[] strings = new String[stringArray.size()];
        stringArray.toArray(strings);
        Log.i("Items",strings.length+"");

        return strings;
    }
    public void setHubConnection() {
        HubConnection connection = setConnection(LiveNetworkConstants.TRADEHERO_LIVE_ENDPOINT);
        connection.setCredentials(new Credentials() {
            @Override
            public void prepareRequest(Request request) {
                request.addHeader(Constants.AUTHORIZATION, requestHeaders.headerTokenLive());
                request.addHeader(Constants.USER_ID, currentUserId.get().toString());
            }
        });
        try {
            proxy = setProxy(LiveNetworkConstants.HUB_NAME, connection);
            connection.start().done(aVoid -> {
                currentVisibleItemsList = getCurrentVisibleItems(listView);
                String str[] = getSecurityIds(currentVisibleItemsList);
                SignalRFuture<Void> signalProxy = proxy.invoke(LiveNetworkConstants.PROXY_METHOD_ADD_TO_GROUPS, str, currentUserId.get());
            });
            connection.connected(new Runnable() {
                @Override
                public void run() {

                }
            });
            connection.connectionSlow(new Runnable() {
                @Override
                public void run() {

                }
            });
            connection.reconnected(new Runnable() {
                @Override
                public void run() {

                }
            });
            connection.closed(new Runnable() {
                @Override
                public void run() {

                }
            });

            /*proxy.on("UpdateQuote", new SubscriptionHandler1<Object>() {

                @Override
                public void run(Object object) {
                    //SignatureContainer2 signatureContainer = (object);
                    Log.v(getTag(), "Object signalR: "+object.toString());
                    //update(signatureContainer2.signedObject);
                }
            }, Object.class);*/

            proxy.on("UpdateQuote", new SubscriptionHandler1<SignatureContainer2>() {

                @Override
                public void run(SignatureContainer2 signatureContainer2) {
                    Log.v(getTag(), "Object signalR: "+signatureContainer2.toString());
                    if(signatureContainer2==null || signatureContainer2.signedObject==null || signatureContainer2.signedObject.id==121234) {
                        return;
                    }
                    else{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                update(signatureContainer2.signedObject);
                            }
                        });

                    }
                    //update(signatureContainer2.signedObject);
                }
            }, SignatureContainer2.class);

        } catch (Exception e) {
            Log.e("Error", "Could not connect to Hub Name");
        }

        //proxy.subscribe()
    }

    class SignatureContainer2
    {
        public LiveQuoteDTO signedObject;
        public String Signature;
    }

    @UiThread
    public void update(LiveQuoteDTO dto){
        Log.i("This is Live", dto.toString());
        adapter.updatePrices(dto);
        adapter.notifyDataSetChanged();

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

    public static void setItems(List<SecurityCompactDTO> items)
    {

        ProviderSecurityV2RxSubFragment.items = items;

    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        this.listView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState != SCROLL_STATE_FLING){
                    currentVisibleItemsList = getCurrentVisibleItems(listView);
                    String str[] = getSecurityIds(currentVisibleItemsList);
                    proxy.invoke(LiveNetworkConstants.PROXY_METHOD_ADD_TO_GROUPS, str, currentUserId.get());
                    Log.d("Fired","Scroll changed");
                    //proxy
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.i("Visible Item Count ", visibleItemCount+"");
                Log.i("Total Item Count ", totalItemCount+"");
            }
        });
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        setHubConnection();
    }

    @Override public void onDestroyView()
    {
        unbinder.unbind();
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

                String newTextLower = newText.toLowerCase();
                ArrayList<SecurityCompactDTO> subItems = new ArrayList<SecurityCompactDTO>();
                for(SecurityCompactDTO sec : items)
                {
                    if((sec.name != null && sec.name.toLowerCase().contains(newTextLower)) || (sec.symbol != null && sec.symbol.toLowerCase().contains(newTextLower)))
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
        //mSearchTextField = null;
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

    public List<SecurityCompactDTO> getCurrentVisibleItems(AbsListView listView){

        int first = 0;
        int last = listView.getChildCount() - 1;
        List<SecurityCompactDTO> visibleList = new ArrayList<>();
        //If visible child is not fully visible, getTop returns negative value
        if(listView.getChildAt(first).getTop() < 0){
            first++;
        }
        if(listView.getChildAt(last).getBottom() > listView.getHeight()){
            last--;
        }
        SimpleSecurityItemViewAdapter adapter = (SimpleSecurityItemViewAdapter) listView.getAdapter();
        while (first <= last){
             SecurityCompactDTO dto = (SecurityCompactDTO) adapter.getItem(first);
             visibleList.add(dto);
             first++;
            //item.
        }
        return visibleList;
    }

}
