package com.androidth.general.fragments.security;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.androidth.general.R;
import com.androidth.general.activities.DashboardActivity;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.key.BasicProviderSecurityV2ListType;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityCompactDTOUtil;
import com.androidth.general.api.security.SecurityCompositeDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.fragments.billing.BasePurchaseManagerFragment;
import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.fragments.trade.AbstractBuySellFragment;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.retrofit.RequestHeaders;
import com.androidth.general.network.service.SignalRInterface;
import com.androidth.general.network.service.SignalRManager;
import com.androidth.general.persistence.security.SecurityCompositeListCacheRx;
import com.androidth.general.utils.Constants;
import com.androidth.general.utils.DeviceUtil;
import com.tencent.mm.sdk.platformtools.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;

public class ProviderSecurityV2RxSubFragment extends BasePurchaseManagerFragment

{
    @Bind(R.id.listview) protected AbsListView listView;
    @Bind(R.id.progress) protected ProgressBar progressBar;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected RequestHeaders requestHeaders;

    SignalRManager signalRManager;
    String navigationUrl;

//    private static final String BUNDLE_PROVIDER_ID_KEY = ProviderSecurityListRxFragment.class.getName() + ".providerId";

    private List<SecurityCompactDTO>  items;
    SimpleSecurityItemViewAdapter adapter;
//    HubProxy hubProxy;
    List<SecurityCompactDTO> currentVisibleItemsList;
    String navigationColor;

    boolean itemClicked = false;

    public String[] getSecurityIds(List<SecurityCompactDTO> items){
        if(items==null){
            return null;
        }
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
        signalRManager = new SignalRManager(requestHeaders, currentUserId, LiveNetworkConstants.CLIENT_NOTIFICATION_HUB_NAME);

        try{
            //wait til listview has drawn children
            listView.post(new Runnable() {
                @Override
                public void run() {
                    try{
                        currentVisibleItemsList = getCurrentVisibleItems(listView);
                        String str[] = getSecurityIds(currentVisibleItemsList);
                        signalRManager.startConnection(LiveNetworkConstants.PROXY_METHOD_ADD_TO_GROUPS, str);

                        signalRManager.getCurrentProxy().on("UpdateQuote", new SubscriptionHandler1<SignatureContainer2>() {

                            @Override
                            public void run(SignatureContainer2 signatureContainer2) {
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
                            }
                        }, SignatureContainer2.class);
                    }catch (Exception e){
                        //listview might not be ready yet
                        e.printStackTrace();
                    }

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @UiThread
    public void update(LiveQuoteDTO dto){
        Log.i("SignalR", dto.toString());
        adapter.updatePrices(dto, listView);

    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new SimpleSecurityItemViewAdapter(getContext(), R.layout.trending_security_item);
        items = getArguments().getParcelableArrayList(ProviderSecurityV2RxFragment.BUNDLE_SECURITIES_KEY);

        navigationUrl = getArguments().getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL, null);
        navigationColor = getArguments().getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR, null);

        adapter.setItems(items);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_provider_security_list, container, false);
        return view;
    }

//    public static void setItems(List<SecurityCompactDTO> items)
//    {
//        ProviderSecurityV2RxSubFragment.items = items;
//    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!getUserVisibleHint()) {
            return;
        }

        setHubConnection();
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
                    try{
                        currentVisibleItemsList = getCurrentVisibleItems(listView);
                        String str[] = getSecurityIds(currentVisibleItemsList);
                        if(signalRManager!=null){
                            signalRManager.getCurrentProxy().invoke(LiveNetworkConstants.PROXY_METHOD_ADD_TO_GROUPS, str, currentUserId.get());
                        }
                        Log.d("Fired","Scroll changed");
                    }catch (Exception e){
                        //getCurrentVisibleItems might be null
                    }
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
        if(signalRManager!=null && !itemClicked){
            signalRManager.getCurrentConnection().disconnect();
        }

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
    View view;
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
//        super.onCreateOptionsMenu(menu, inflater);
        //menu.clear();
        if(view == null){
            view = getSupportActionBar().getCustomView();
        }
        //small hack to set back comp. action bar. Works like charm tho....
//        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(Gravity.CENTER);
//        getSupportActionBar().setCustomView(view);

        inflater.inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.btn_search);
        SearchView searchView = new SearchView(((DashboardActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.setIconifiedByDefault(true);

        MenuItemCompat.setOnActionExpandListener(item, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchView.requestFocus();
                searchView.requestFocusFromTouch();
                searchView.setIconified(false);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                searchView.clearFocus();
                MenuItemCompat.collapseActionView(item);
                return false;
            }
        });

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

        //        setActionBarColor(navigationColor);
        setActionBarCustomImage(getActivity(), navigationUrl, true);

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

        args.putParcelable(AbstractBuySellFragment.BUNDLE_KEY_SECURITY_DTO, clicked);
        navigator.get().pushFragment(SecurityCompactDTOUtil.fragmentFor(clicked), args);
        itemClicked = true;
    }

    public List<SecurityCompactDTO> getCurrentVisibleItems(AbsListView listView){

        int first = 0;
        int last = listView.getChildCount() - 1;
        if(last<0){
            return null;
        }
        List<SecurityCompactDTO> visibleList = new ArrayList<>();
        //If visible child is not fully visible, getTop returns negative value
        //if(listView.getChildAt(first).getTop() < 0){
        //    first++;
        //}
        //if(listView.getChildAt(last).getBottom() > listView.getHeight()){
        //    last--;
        //}
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
