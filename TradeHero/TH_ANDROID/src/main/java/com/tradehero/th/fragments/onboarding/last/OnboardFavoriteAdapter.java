package com.tradehero.th.fragments.onboarding.last;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.security.SecurityItemView;

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
