package com.tradehero.th.fragments.location;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UpdateCountryCodeDTO;
import com.tradehero.th.api.users.UpdateCountryCodeResultDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.HashMap;
import javax.inject.Inject;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LocationListFragment extends DashboardFragment
{
    private static String KEY_ITEM_TITLE = "key_item_title";
    private static String KEY_ITEM_IMAGE = "key_item_image";
    private SimpleAdapter mListAdapter;
    private MiddleCallback<UpdateCountryCodeResultDTO> middleCallback;
    private ProgressDialog progressDialog;

    @Inject Context context;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtilLazy;
    @Inject Lazy<CurrentActivityHolder> currentActivityHolderLazy;

    @InjectView(android.R.id.list) ListView listView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ArrayList<HashMap<String, Object>> listItem = createCountryData();
        mListAdapter = new SimpleAdapter(context, listItem, R.layout.settings_location_list_item,
                new String[] {KEY_ITEM_TITLE, KEY_ITEM_IMAGE}, new int[] {R.id.country_name, R.id.country_logo});
    }

    private ArrayList<HashMap<String, Object>> createCountryData()
    {
        Country[] items = Country.values();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        for(int i=1;i<items.length;i++)
        {
            HashMap<String, Object> map = new HashMap<>();
            map.put(KEY_ITEM_TITLE, items[i].name());
            map.put(KEY_ITEM_IMAGE, items[i].logoId);
            listItem.add(map);
        }
        return listItem;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings_location_list, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(mListAdapter);
    }

    @Override public void onDestroyView()
    {
        listView.setEmptyView(null);
        ButterKnife.reset(this);
        progressDialog = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        if (mListAdapter != null)
        {
            mListAdapter = null;
        }
        super.onDestroy();
    }

    @OnItemClick(android.R.id.list)
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        getProgressDialog().show();
        detachMiddleCallback();
        UpdateCountryCodeDTO updateCountryCodeDTO = new UpdateCountryCodeDTO();
        updateCountryCodeDTO.countryCode = (String)((HashMap<String, Object>)(mListAdapter.getItem(position))).get(KEY_ITEM_TITLE);

        middleCallback = userServiceWrapperLazy.get().updateCountryCode(
                currentUserId.toUserBaseKey(), updateCountryCodeDTO, new UpdateCountryCodeCallback());
    }

    private class UpdateCountryCodeCallback implements retrofit.Callback<UpdateCountryCodeResultDTO>
    {
        @Override public void success(UpdateCountryCodeResultDTO updateCountryCodeResultDTO, Response response2)
        {
            getProgressDialog().hide();
            getDashboardNavigator().popFragment();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            getProgressDialog().hide();
        }
    }

    private void detachMiddleCallback()
    {
        if (middleCallback != null)
        {
            middleCallback.setPrimaryCallback(null);
        }
        middleCallback = null;
    }

    private ProgressDialog getProgressDialog()
    {
        if (progressDialog != null)
        {
            return progressDialog;
        }
        progressDialog = progressDialogUtilLazy.get().show(currentActivityHolderLazy.get().getCurrentActivity(), R.string.loading_loading,
                R.string.alert_dialog_please_wait);
        progressDialog.hide();
        return progressDialog;
    }
}
