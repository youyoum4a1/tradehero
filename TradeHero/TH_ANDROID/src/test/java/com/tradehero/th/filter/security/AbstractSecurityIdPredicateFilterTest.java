package com.tradehero.th.filter.security;

import com.android.internal.util.Predicate;
import com.tradehero.common.widget.filter.BaseListCharSequencePredicateFilter;
import com.tradehero.common.widget.filter.CharSequencePredicate;
import com.tradehero.common.widget.filter.ListCharSequencePredicateFilter;
import com.tradehero.th.api.security.SecurityId;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;

/**
 * Created by xavier on 1/24/14.
 */
abstract public class AbstractSecurityIdPredicateFilterTest
{
    public static final String TAG = AbstractSecurityIdPredicateFilterTest.class.getSimpleName();

    protected ListCharSequencePredicateFilter<SecurityId> securityIdPredicateFilter;

    @Before public void setUp()
    {
        securityIdPredicateFilter = new BaseListCharSequencePredicateFilter<>(provideSecurityIdPredicate());
    }

    @After public void tearDown()
    {
    }

    abstract protected CharSequencePredicate<SecurityId> provideSecurityIdPredicate();

    protected List<SecurityId> getList1a()
    {
        List<SecurityId> list = new ArrayList<>();
        list.add(new SecurityId("SGX", "RPTO"));
        return list;
    }

    protected List<SecurityId> getList1b()
    {
        List<SecurityId> list = new ArrayList<>();
        list.add(new SecurityId("SGX", "ROTP"));
        return list;
    }

    protected List<SecurityId> getList2a()
    {
        List<SecurityId> list = new ArrayList<>();
        list.add(new SecurityId("SGX", "RPTO"));
        list.add(new SecurityId("SGX", "ANBV"));
        return list;
    }

    protected List<SecurityId> getList2b()
    {
        List<SecurityId> list = new ArrayList<>();
        list.add(new SecurityId("SGX", "ROTP"));
        list.add(new SecurityId("SGX", "NBVA"));
        return list;
    }

}
