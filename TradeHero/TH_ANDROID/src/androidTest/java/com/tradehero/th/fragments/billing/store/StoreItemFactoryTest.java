package com.tradehero.th.fragments.billing.store;

import com.android.internal.util.Predicate;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.system.SystemStatusKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.base.TestTHApp;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.persistence.system.SystemStatusCache;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class StoreItemFactoryTest
{
    @Inject SystemStatusCache systemStatusCache;
    @Inject CurrentUserId currentUserId;

    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    private Predicate<StoreItemDTO> createFindPerDomain(final ProductIdentifierDomain domain)
    {
        return new Predicate<StoreItemDTO>()
        {
            @Override public boolean apply(StoreItemDTO storeItemDTO)
            {
                return storeItemDTO instanceof StoreItemPromptPurchaseDTO
                        && ((StoreItemPromptPurchaseDTO) storeItemDTO).productIdentifierDomain == domain;
            }
        };
    }

    //<editor-fold desc="Have alerts depending on SystemStatus">
    @Test public void testIgnoreSystemStatusShouldHaveAlerts()
    {
        StoreItemDTOList list = StoreItemFactory.createAll(systemStatusCache, StoreItemFactory.WITH_IGNORE_SYSTEM_STATUS).toBlocking().single();
        assertThat(list.findFirstWhere(createFindPerDomain(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)))
                .isNotNull();
    }

    @Test public void testFollowSystemStatusButNoStatusShouldHaveAlerts()
    {
        StoreItemDTOList list = StoreItemFactory.createAll(systemStatusCache, StoreItemFactory.WITH_FOLLOW_SYSTEM_STATUS).toBlocking().single();
        assertThat(list.findFirstWhere(createFindPerDomain(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)))
                .isNotNull();
    }

    @Test public void testIgnoreSystemStatusWhereStatusSaysAlertsArePayingShouldHaveAlerts()
    {
        SystemStatusDTO systemStatusDTO = new SystemStatusDTO();
        systemStatusDTO.alertsAreFree = false;
        systemStatusCache.onNext(new SystemStatusKey(), systemStatusDTO);

        StoreItemDTOList list = StoreItemFactory.createAll(systemStatusCache, StoreItemFactory.WITH_IGNORE_SYSTEM_STATUS).toBlocking().single();
        assertThat(list.findFirstWhere(createFindPerDomain(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)))
                .isNotNull();
    }

    @Test public void testFollowSystemStatusWhereStatusSaysAlertsArePayingShouldHaveAlerts()
    {
        SystemStatusDTO systemStatusDTO = new SystemStatusDTO();
        systemStatusDTO.alertsAreFree = false;
        systemStatusCache.onNext(new SystemStatusKey(), systemStatusDTO);

        StoreItemDTOList list = StoreItemFactory.createAll(systemStatusCache, StoreItemFactory.WITH_FOLLOW_SYSTEM_STATUS).toBlocking().single();
        assertThat(list.findFirstWhere(createFindPerDomain(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)))
                .isNotNull();
    }

    @Test public void testIgnoreSystemStatusWhereStatusSaysAlertsAreFreeShouldHaveAlerts()
    {
        SystemStatusDTO systemStatusDTO = new SystemStatusDTO();
        systemStatusDTO.alertsAreFree = true;
        systemStatusCache.onNext(new SystemStatusKey(), systemStatusDTO);

        StoreItemDTOList list = StoreItemFactory.createAll(systemStatusCache, StoreItemFactory.WITH_IGNORE_SYSTEM_STATUS).toBlocking().single();
        assertThat(list.findFirstWhere(createFindPerDomain(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)))
                .isNotNull();
    }

    @Test public void testFollowSystemStatusWhereStatusSaysAlertsAreFreeShouldHaveNoAlert()
    {
        SystemStatusDTO systemStatusDTO = new SystemStatusDTO();
        systemStatusDTO.alertsAreFree = true;
        systemStatusCache.onNext(new SystemStatusKey(), systemStatusDTO);

        StoreItemDTOList list = StoreItemFactory.createAll(systemStatusCache, StoreItemFactory.WITH_FOLLOW_SYSTEM_STATUS).toBlocking().single();
        assertThat(list.findFirstWhere(createFindPerDomain(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)))
                .isNull();
    }
    //</editor-fold>
}
