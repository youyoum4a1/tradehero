package com.tradehero.th.filter;

import com.android.internal.util.Predicate;
import com.tradehero.common.widget.filter.BaseListCharSequencePredicateFilter;
import com.tradehero.common.widget.filter.CharSequencePredicate;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.WarrantDTO;
import com.tradehero.th.filter.security.SecurityCompactPaddedSymbolCIPredicate;
import com.tradehero.th.filter.security.SecurityIdPaddedSymbolCIPredicate;
import com.tradehero.th.filter.security.WarrantPaddedSymbolOrUnderlyingCIPredicate;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/13/14.
 */
@Module(
        staticInjections =
                {
                },
        injects =
                {
                },
        complete = false,
        library = true
)
public class FilterModule
{
    public static final String TAG = FilterModule.class.getSimpleName();

    public FilterModule()
    {
    }

    @Provides @Singleton CharSequencePredicate<SecurityId> provideSecurityIdPredicate()
    {
        // Your choice
        //return new SecurityIdSymbolCIPredicate();
        return new SecurityIdPaddedSymbolCIPredicate();
    }

    @Provides @Singleton CharSequencePredicate<SecurityCompactDTO> provideSecurityCompactPredicate()
    {
        // Your choice
        //return new SecurityCompactSymbolCIPredicate<>();
        return new SecurityCompactPaddedSymbolCIPredicate<>();
    }

    // When matching Warrants, it includes underlying name
    @Provides @Singleton CharSequencePredicate<WarrantDTO> provideWarrantPredicate()
    {
        return new WarrantPaddedSymbolOrUnderlyingCIPredicate<>();
    }

    @Provides @Singleton ListCharSequencePredicateFilter<SecurityId> provideSecurityIdPatternFilter(CharSequencePredicate<SecurityId> predicate)
    {
        return new BaseListCharSequencePredicateFilter<>(predicate);
    }

    @Provides @Singleton ListCharSequencePredicateFilter<WarrantDTO> provideWarrantDTOPatternFilter(CharSequencePredicate<WarrantDTO> predicate)
    {
        return new BaseListCharSequencePredicateFilter<>(predicate);
    }
}
