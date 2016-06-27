package com.androidth.general.fragments.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.androidth.general.R;
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
import com.tencent.mm.sdk.platformtools.Log;
import com.twitter.sdk.android.core.internal.util.ObservableScrollView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

public class ProviderSecurityV2RxSubFragment extends BasePurchaseManagerFragment implements SignalRInterface, ObservableScrollView.ScrollViewListener

{
    @Bind(R.id.listview) protected AbsListView listView;
    @Bind(R.id.progress) protected ProgressBar progressBar;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected RequestHeaders requestHeaders;

    private static final String BUNDLE_PROVIDER_ID_KEY = ProviderSecurityListRxFragment.class.getName() + ".providerId";
    protected ProviderId providerId;
    private static List<SecurityCompactDTO>  items;
    SimpleSecurityItemViewAdapter adapter;
    HubProxy proxy;
    List<SecurityCompactDTO> currentVisibleItemsList;

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
    public void setHubConnection(){
        HubConnection connection = setConnection(LiveNetworkConstants.TRADEHERO_LIVE_ENDPOINT);
        //connection.
        connection.setCredentials(new Credentials() {
            @Override
            public void prepareRequest(Request request) {

                System.out.print(requestHeaders.headerTokenLive());
                request.addHeader(Constants.AUTHORIZATION, requestHeaders.headerTokenLive());
                request.addHeader(Constants.USER_ID,currentUserId.get().toString());
                //Head
            }
        });
        try{
            proxy = setProxy(LiveNetworkConstants.HUB_NAME, connection);
            //proxy.subscribe()
            //SignalRFuture<Void> connection = hub.start();
            connection.start().done(aVoid -> {
                //String arr[] = {"700020341","700020415", "700020414", "700016180"};
                currentVisibleItemsList = getCurrentVisibleItems(listView);
                String str[] = getSecurityIds(currentVisibleItemsList);
                SignalRFuture<Void> signalProxy = proxy.invoke(LiveNetworkConstants.PROXY_METHOD_ADD_TO_GROUPS, str, currentUserId.get());
                //SignalRFuture<Void> signalProxy = proxy.invoke(LiveNetworkConstants.PROXY_METHOD_ADD_TO_GROUPS, arr, currentUserId.get());

                        signalProxy.done(req -> Log.i("Yay","Nayy"));
            });
            connection.connected(new Runnable() {
                @Override
                public void run() {
                    Log.i("SD","cONNECTED");
                }
            });
            connection.connectionSlow(new Runnable() {
                @Override
                public void run() {
                    Log.i("Slow","Slow Connection");
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
            proxy.on("UpdateQuote", signatureContainer -> {
                Log.i("Okay","What's this");
                Log.i("Response", signatureContainer.toString());
                //Update things
                adapter.updatePrices(signatureContainer.signedObject);

            }, SignatureContainer.class);
        }
        catch (Exception e){
            Log.e("Error","Could not connect to Hub Name");
        }

        //proxy.subscribe()


    }

    @Override
    public void onScrollChanged(int i) {

    }

    class SignatureContainer {
        public String Signature;
        public LiveQuoteDTO signedObject;

    }



    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new SimpleSecurityItemViewAdapter(getContext(), R.layout.trending_security_item);
        adapter.setItems(items);
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        setHubConnection();
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
        ButterKnife.bind(this, view);
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
