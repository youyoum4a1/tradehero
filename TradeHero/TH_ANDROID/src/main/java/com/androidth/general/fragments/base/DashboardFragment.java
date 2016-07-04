package com.androidth.general.fragments.base;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderDTO;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import dagger.Lazy;

abstract public class DashboardFragment extends BaseFragment
{
    @Inject protected Lazy<FragmentOuterElements> fragmentElements;

    public boolean shouldShowLiveTradingToggle()
    {
        return false;
    }

    public void onLiveTradingChanged(boolean isLive)
    {
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (!actionBarOwnerMixin.shouldShowHomeAsUp())
                {
                    fragmentElements.get().onOptionItemsSelected(item);
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarColorSelf(null);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Override public void onResume(){
        super.onResume();
    }
    public void setActionBarColorSelf( ProviderDTO providerDTO){
        if(providerDTO == null)
        setActionBarColor(getString(R.string.tradehero_blue_default));
        else {

            setActionBarColor(providerDTO.hexColor);
            setActionBarImage(providerDTO.navigationLogoUrl);
            setActionBarTitle("");
        }
    }

    private boolean setActionBarImage(String url){
        try {
            ActionBar actionBar = getSupportActionBar();
            LayoutInflater mInflater = LayoutInflater.from(getContext());
            View mCustomView = mInflater.inflate(R.layout.actionbar_custom_view, null);
            mCustomView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,actionBar.getHeight()));
            ImageView imageView = (ImageView) mCustomView.findViewById(R.id.provider_logo);
            Picasso.with(getContext()).load(url).into(imageView);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
            actionBar.setCustomView(mCustomView, layoutParams);
            actionBar.setElevation(5);
            actionBar.setDisplayOptions(actionBar.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }
}
