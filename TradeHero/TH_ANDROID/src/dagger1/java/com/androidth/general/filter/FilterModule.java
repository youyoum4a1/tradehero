package com.androidth.general.filter;

import com.androidth.general.common.widget.filter.BaseListCharSequencePredicateFilter;
import com.androidth.general.common.widget.filter.CharSequencePredicate;
import com.androidth.general.common.widget.filter.ListCharSequencePredicateFilter;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.compact.WarrantDTO;
import com.androidth.general.filter.security.SecurityCompactPaddedSymbolCIPredicate;
import com.androidth.general.filter.security.SecurityIdPaddedSymbolCIPredicate;
import com.androidth.general.filter.security.WarrantPaddedSymbolOrUnderlyingCIPredicate;
import dagger.Module;
import dagger.Provides;

@Module(
        staticInjections =
                {
                },
        injects =
                {
                        com.androidth.general.fragments.security.SimpleSecurityItemViewAdapter.class,
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
