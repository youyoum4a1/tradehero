package com.tradehero.th.filter;

import com.tradehero.common.widget.filter.BaseListCharSequencePredicateFilter;
import com.tradehero.common.widget.filter.CharSequencePredicate;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.compact.WarrantDTO;
import com.tradehero.th.filter.security.SecurityCompactPaddedSymbolCIPredicate;
import com.tradehero.th.filter.security.SecurityIdPaddedSymbolCIPredicate;
import com.tradehero.th.filter.security.WarrantPaddedSymbolOrUnderlyingCIPredicate;
import com.tradehero.th.fragments.competition.macquarie.MacquarieWarrantItemViewAdapter;
import dagger.Module;
import dagger.Provides;

@Module(
        staticInjections =
                {
                },
        injects =
                {
                        com.tradehero.th.fragments.competition.macquarie.MacquarieWarrantItemViewAdapter.class,
                        com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter.class,
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
