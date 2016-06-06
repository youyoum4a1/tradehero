package com.androidth.general.fragments.onboarding.last;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.R;
import com.androidth.general.adapters.ViewDTOSetAdapter;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.fragments.security.SecurityItemView;

public class OnBoardFavoriteAdapter extends ViewDTOSetAdapter<SecurityCompactDTO, SecurityItemView>
{
    public OnBoardFavoriteAdapter(@NonNull Context context)
    {
        super(context);
    }

    @Override protected int getViewResId(int position)
    {
        return R.layout.onboard_favorite_stock;
    }
}
