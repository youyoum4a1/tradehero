package com.tradehero.th.api.social;

import com.android.internal.util.Predicate;
import android.support.annotation.Nullable;

abstract public class HeroDTOActiveFreePredicate implements Predicate<HeroDTO>
{
    @Override abstract public boolean apply(@Nullable HeroDTO heroDTO);
}
