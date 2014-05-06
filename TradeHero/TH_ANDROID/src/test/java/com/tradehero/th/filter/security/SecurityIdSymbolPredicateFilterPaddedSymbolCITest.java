package com.tradehero.th.filter.security;

import com.tradehero.common.widget.filter.CharSequencePredicate;
import com.tradehero.th.api.security.SecurityId;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class SecurityIdSymbolPredicateFilterPaddedSymbolCITest extends AbstractSecurityIdPredicateFilterTest
{
    public static final String TAG = SecurityIdSymbolPredicateFilterPaddedSymbolCITest.class.getSimpleName();

    @Override protected CharSequencePredicate<SecurityId> provideSecurityIdPredicate()
    {
        return new SecurityIdPaddedSymbolCIPredicate();
    }

    @Test public void nullListReturnsNull()
    {
        securityIdPredicateFilter.setCharSequence("abc");

        assertNull(securityIdPredicateFilter.filter(null));
    }

    @Test public void nullPredicateReturnsSame()
    {
        securityIdPredicateFilter.setCharSequence("");
        List<SecurityId> securityIds = new ArrayList<>();

        assertSame(securityIds, securityIdPredicateFilter.filter(securityIds, null));
    }

    @Test public void nullSymbolReturnsSame()
    {
        securityIdPredicateFilter.setCharSequence(null);
        List<SecurityId> securityIds = getList1a();
        List<SecurityId> filtered = securityIdPredicateFilter.filter(securityIds);

        assertSame(securityIds, filtered);
    }

    @Test public void emptySymbolReturnsSame()
    {
        securityIdPredicateFilter.setCharSequence("");
        List<SecurityId> securityIds = new ArrayList<>();
        List<SecurityId> filtered = securityIdPredicateFilter.filter(securityIds);

        assertSame(securityIds, filtered);
    }

    @Test public void unmatchedSymbolReturnsNewEmptyList()
    {
        securityIdPredicateFilter.setCharSequence("UNFOU");
        List<SecurityId> securityIds = getList1a();
        List<SecurityId> filtered = securityIdPredicateFilter.filter(securityIds);

        assertNotSame(securityIds, filtered);
        assertEquals(0, filtered.size());
    }

    @Test public void doesNotCheckOnExchange()
    {
        securityIdPredicateFilter.setCharSequence("SGX");
        List<SecurityId> securityIds = getList1a();
        List<SecurityId> filtered = securityIdPredicateFilter.filter(securityIds);

        assertEquals(0, filtered.size());
    }

    @Test public void matchedSymbolReturnsNewSimilarList()
    {
        securityIdPredicateFilter.setCharSequence("RPTO");
        List<SecurityId> securityIds = getList1a();
        List<SecurityId> filtered = securityIdPredicateFilter.filter(securityIds);

        assertNotSame(securityIds, filtered);
        assertEquals(1, filtered.size());
        assertEquals(new SecurityId("SGX", "RPTO"), filtered.get(0));
    }

    @Test public void matchedSymbolReturnsNewShrunkList()
    {
        securityIdPredicateFilter.setCharSequence("RPTO");
        List<SecurityId> securityIds = getList2a();
        List<SecurityId> filtered = securityIdPredicateFilter.filter(securityIds);

        assertNotSame(securityIds, filtered);
        assertEquals(1, filtered.size());
        assertEquals(new SecurityId("SGX", "RPTO"), filtered.get(0));
    }

    @Test public void matchedSymbolWrongCaseReturnsNewShrunkList()
    {
        securityIdPredicateFilter.setCharSequence("RpTo");
        List<SecurityId> securityIds = getList2a();
        List<SecurityId> filtered = securityIdPredicateFilter.filter(securityIds);

        assertNotSame(securityIds, filtered);
        assertEquals(1, filtered.size());
        assertEquals(new SecurityId("SGX", "RPTO"), filtered.get(0));
    }

    @Test public void shortSymbolReturnsNewShrunkList()
    {
        securityIdPredicateFilter.setCharSequence("R");
        List<SecurityId> securityIds = getList1a();
        List<SecurityId> filtered = securityIdPredicateFilter.filter(securityIds);

        assertNotSame(securityIds, filtered);
        assertEquals(1, filtered.size());
        assertEquals(new SecurityId("SGX", "RPTO"), filtered.get(0));
    }

    @Test public void longCandidateDoesNotMatchSmallSymbol()
    {
        securityIdPredicateFilter.setCharSequence("RPTO ");
        List<SecurityId> securityIds = getList1a();
        List<SecurityId> filtered = securityIdPredicateFilter.filter(securityIds);

        assertEquals(0, filtered.size());
    }

    @Test public void shortRegexCandidateMatchesSmallSymbol()
    {
        securityIdPredicateFilter.setCharSequence("RPT.?");
        List<SecurityId> securityIds = getList1a();
        List<SecurityId> filtered = securityIdPredicateFilter.filter(securityIds);

        assertEquals(1, filtered.size());
    }

    @Test public void longRegexCandidateMatchesSmallSymbol()
    {
        securityIdPredicateFilter.setCharSequence("RPTO|AFG");
        List<SecurityId> securityIds = getList1a();
        List<SecurityId> filtered = securityIdPredicateFilter.filter(securityIds);

        assertEquals(1, filtered.size());
    }
}
