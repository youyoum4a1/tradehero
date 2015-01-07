package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.security.SecurityItemView;
import com.tradehero.th.fragments.security.SecurityListRxFragment;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;

abstract public class TrendingBaseFragment extends SecurityListRxFragment<SecurityItemView>
{
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;

    public void pushSearchIn()
    {
        Bundle args = new Bundle();
        navigator.get().pushFragment(SecuritySearchFragment.class, args);
    }

}
