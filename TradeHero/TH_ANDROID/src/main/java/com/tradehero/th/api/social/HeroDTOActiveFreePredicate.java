package com.tradehero.th.api.social;

import com.android.internal.util.Predicate;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

abstract public class HeroDTOActiveFreePredicate implements Predicate<HeroDTO>
{
    @Contract("null -> false; !null -> _")
    @Override abstract public boolean apply(@Nullable HeroDTO heroDTO);
}
