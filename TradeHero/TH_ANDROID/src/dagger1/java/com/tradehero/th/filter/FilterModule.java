package com.ayondo.academy.filter;

import com.tradehero.common.widget.filter.BaseListCharSequencePredicateFilter;
import com.tradehero.common.widget.filter.CharSequencePredicate;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.security.SecurityId;
import com.ayondo.academy.api.security.compact.WarrantDTO;
import com.ayondo.academy.filter.security.SecurityCompactPaddedSymbolCIPredicate;
import com.ayondo.academy.filter.security.SecurityIdPaddedSymbolCIPredicate;
import com.ayondo.academy.filter.security.WarrantPaddedSymbolOrUnderlyingCIPredicate;
import dagger.Module;
import dagger.Provides;

@Module(
        staticInjections =
                {
                },
        injects =
                {
                        com.ayondo.academy.fragments.security.SimpleSecurityItemViewAdapter.class,
                },
        complete = false,
        library = true
)
public class FilterModule
{
    public FilterModule()
    {
    }

    @Provides CharSequencePredicate<SecurityId> provideSecurityIdPredicate()
    {
        // Your choice
        //return new SecurityIdSymbolCIPredicate();
        return new SecurityIdPaddedSymbolCIPredicate();
    }

    @Provides CharSequencePredicate<SecurityCompactDTO> provideSecurityCompactPredicate()
    {
        // Your choice
        //return new SecurityCompactSymbolCIPredicate<>();
        return new SecurityCompactPaddedSymbolCIPredicate<>();
    }

    // When matching Warrants, it includes underlying name
    @Provides CharSequencePredicate<WarrantDTO> provideWarrantPredicate()
    {
        return new WarrantPaddedSymbolOrUnderlyingCIPredicate<>();
    }

    @Provides ListCharSequencePredicateFilter<SecurityId> provideSecurityIdPatternFilter(CharSequencePredicate<SecurityId> predicate)
    {
        return new BaseListCharSequencePredicateFilter<>(predicate);
    }

    @Provides ListCharSequencePredicateFilter<SecurityCompactDTO> provideSecurityCompactDTOPatternFilter(CharSequencePredicate<SecurityCompactDTO> predicate)
    {
        return new BaseListCharSequencePredicateFilter<>(predicate);
    }

    @Provides ListCharSequencePredicateFilter<WarrantDTO> provideWarrantDTOPatternFilter(CharSequencePredicate<WarrantDTO> predicate)
    {
        return new BaseListCharSequencePredicateFilter<>(predicate);
    }
}
