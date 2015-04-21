package com.tradehero.th.fragments.onboarding.last;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.security.SecurityItemView;
import java.util.Collection;

/**
 * Created by liangyx on 4/21/15.
 */
public class OnboardFavoriteAdapter extends ViewDTOSetAdapter<SecurityCompactDTO, SecurityItemView>
{
    public OnboardFavoriteAdapter(@NonNull Context context)
    {
        super(context);
    }

    @Override protected int getViewResId(int position)
    {
        return R.layout.onboard_favorite_stock;
    }
}
