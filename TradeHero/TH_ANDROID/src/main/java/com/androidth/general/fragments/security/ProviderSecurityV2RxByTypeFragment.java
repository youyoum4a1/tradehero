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
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.key.BasicProviderSecurityV2ListType;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityCompositeDTO;
import com.androidth.general.api.security.SecurityTypeDTO;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.persistence.security.SecurityCompositeListCacheRx;
import com.squareup.picasso.Picasso;
import com.tencent.mm.sdk.platformtools.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class ProviderSecurityV2RxByTypeFragment extends BaseFragment
{
    @Bind(R.id.listview) protected AbsListView listView;
    @Inject protected SecurityCompositeListCacheRx securityCompositeListCacheRx;
    @Inject protected Picasso picasso;

    private static final String BUNDLE_PROVIDER_ID_KEY = ProviderSecurityV2RxFragment.class.getName() + ".providerId";
    protected ProviderId providerId;
    protected SecurityCompositeDTO securityCompositeDTO;
    protected SecurityTypeAdapter securityTypeAdapter;

    @NonNull private static ProviderId getProviderId(@NonNull Bundle bundle)
    {
        Bundle providerBundle = bundle.getBundle(BUNDLE_PROVIDER_ID_KEY);
        if (providerBundle == null)
        {
            throw new NullPointerException("Provider needs to be passed");
        }
        return new ProviderId(providerBundle);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.providerId = getProviderId(getArguments());
        securityCompositeDTO = securityCompositeListCacheRx.getCachedValue(new BasicProviderSecurityV2ListType(providerId));
        if(securityCompositeDTO!=null)
            securityTypeAdapter = new SecurityTypeAdapter(getContext(), securityCompositeDTO.SecurityTypes.toArray(new SecurityTypeDTO[securityCompositeDTO.SecurityTypes.size()]));
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_stock_trending, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        this.listView.setAdapter(securityTypeAdapter);
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
        SecurityTypeDTO securityTypeDTO = ((SecurityTypeDTO) parent.getItemAtPosition(position));
        Log.i("onItemClick", securityTypeDTO.name);

        ArrayList<SecurityCompactDTO> secs = new ArrayList<SecurityCompactDTO>();
        for(SecurityCompactDTO sec : securityCompositeDTO.Securities)
        {
            if(sec.secTypeDesc.equals(securityTypeDTO.name))
            {
                secs.add(sec);
            }
        }

        getArguments().putParcelableArrayList(ProviderSecurityV2RxFragment.BUNDLE_SECURITIES_KEY, secs);
        navigator.get().pushFragment(ProviderSecurityV2RxSubFragment.class, getArguments());
    }
    public class SecurityTypeAdapter extends ArrayAdapter<SecurityTypeDTO>
    {
        private final Context context;
        private final SecurityTypeDTO[] values;

        public SecurityTypeAdapter(Context context, SecurityTypeDTO[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rowView = convertView;
            // reuse views
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.exchange_item, parent, false);
            }

            ImageView imgExchangeLogo = (ImageView) rowView.findViewById(R.id.exchange_logo);
            TextView txtExchangeName = (TextView) rowView.findViewById(R.id.exchange_name);
            LinearLayout layoutExchangeParent = (LinearLayout) rowView.findViewById(R.id.exchange_parent);

            layoutExchangeParent.setVisibility(View.INVISIBLE);

            txtExchangeName.setText(values[position].name);

            picasso.load(values[position].imageUrl)
                    .into(imgExchangeLogo);

            return rowView;
        }
    }
}
