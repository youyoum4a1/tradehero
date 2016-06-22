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
import com.androidth.general.network.service.SignalRInterface;
import com.google.gson.JsonObject;
import com.tencent.mm.sdk.platformtools.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

public class ProviderSecurityV2RxSubFragment extends BasePurchaseManagerFragment implements SignalRInterface
{
    @Bind(R.id.listview) protected AbsListView listView;
    @Bind(R.id.progress) protected ProgressBar progressBar;
    @Inject protected CurrentUserId currentUserId;

    private static final String BUNDLE_PROVIDER_ID_KEY = ProviderSecurityListRxFragment.class.getName() + ".providerId";
    protected ProviderId providerId;
    private static List<SecurityCompactDTO>  items;
    SimpleSecurityItemViewAdapter adapter;

    public HubConnection setConnection(String url) {
        return new HubConnection(url);
    }

    public HubProxy setProxy(String hubName, HubConnection connection) {
        return connection.createHubProxy(hubName);
    }

    public static void setItems(List<SecurityCompactDTO> items)
    {

        ProviderSecurityV2RxSubFragment.items = items;

    }
    public String[] getSecurityIds(List<SecurityCompactDTO> items){
        Iterator<SecurityCompactDTO> iterator = items.iterator();
        ArrayList<String > stringArray = new ArrayList<>();
        while (iterator.hasNext()){
            stringArray.add(iterator.next().getSecurityId().toString());
        }
        String[] strings = new String[stringArray.size()];
        stringArray.toArray(strings);
        return strings;
    }
    public void setHubConnection(){
        HubConnection hub = setConnection(LiveNetworkConstants.TRADEHERO_LIVE_ENDPOINT);
        try{
            HubProxy proxy = setProxy(LiveNetworkConstants.HUB_NAME, hub);
            //SignalRFuture<Void> connection = hub.start();
            proxy.invoke(LiveNetworkConstants.PROXY_METHOD, getSecurityIds(items),currentUserId.toUserBaseKey().getUserId()).done(new Action<Void>() {
                @Override
                public void run(Void aVoid) throws Exception {
                    Log.i("Yay","Nayy");
                }
            });
            JsonObject jsonElement = new JsonObject();
            hub.onReceived(jsonElement);
        }
        catch (Exception e){
            Log.i("Error","Could not connect to Hub Name");
        }

        //proxy.subscribe()


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
