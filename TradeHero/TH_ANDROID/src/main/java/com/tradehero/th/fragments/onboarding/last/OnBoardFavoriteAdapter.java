package com.ayondo.academy.fragments.onboarding.last;

import android.content.Context;
import android.support.annotation.NonNull;
import com.ayondo.academy.R;
import com.ayondo.academy.adapters.ViewDTOSetAdapter;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.fragments.security.SecurityItemView;

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
