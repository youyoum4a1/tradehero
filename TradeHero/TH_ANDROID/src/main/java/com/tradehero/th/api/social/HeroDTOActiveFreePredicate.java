package com.tradehero.th.api.social;

import android.support.annotation.Nullable;
import com.android.internal.util.Predicate;

abstract public class HeroDTOActiveFreePredicate implements Predicate<HeroDTO>
{
    @Override abstract public boolean apply(@Nullable HeroDTO heroDTO);
}
