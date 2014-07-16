package com.tradehero.th.fragments.billing.store;

import com.android.internal.util.Predicate;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.persistence.system.SystemStatusCache;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class StoreItemFactoryTest
{
    @Inject StoreItemFactory storeItemFactory;
    @Inject SystemStatusCache systemStatusCache;
    @Inject CurrentUserId currentUserId;

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
        StoreItemDTOList list = storeItemFactory.createAll(StoreItemFactory.WITH_IGNORE_SYSTEM_STATUS);
        assertThat(list.findFirstWhere(createFindPerDomain(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)))
                .isNotNull();
    }

    @Test public void testFollowSystemStatusButNoStatusShouldHaveAlerts()
    {
        StoreItemDTOList list = storeItemFactory.createAll(StoreItemFactory.WITH_FOLLOW_SYSTEM_STATUS);
        assertThat(list.findFirstWhere(createFindPerDomain(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)))
                .isNotNull();
    }

    @Test public void testIgnoreSystemStatusWhereStatusSaysAlertsArePayingShouldHaveAlerts()
    {
        SystemStatusDTO systemStatusDTO = new SystemStatusDTO();
        systemStatusDTO.alertsAreFree = false;
        systemStatusCache.put(currentUserId.toUserBaseKey(), systemStatusDTO);

        StoreItemDTOList list = storeItemFactory.createAll(StoreItemFactory.WITH_IGNORE_SYSTEM_STATUS);
        assertThat(list.findFirstWhere(createFindPerDomain(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)))
                .isNotNull();
    }

    @Test public void testFollowSystemStatusWhereStatusSaysAlertsArePayingShouldHaveAlerts()
    {
        SystemStatusDTO systemStatusDTO = new SystemStatusDTO();
        systemStatusDTO.alertsAreFree = false;
        systemStatusCache.put(currentUserId.toUserBaseKey(), systemStatusDTO);

        StoreItemDTOList list = storeItemFactory.createAll(StoreItemFactory.WITH_FOLLOW_SYSTEM_STATUS);
        assertThat(list.findFirstWhere(createFindPerDomain(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)))
                .isNotNull();
    }

    @Test public void testIgnoreSystemStatusWhereStatusSaysAlertsAreFreeShouldHaveAlerts()
    {
        SystemStatusDTO systemStatusDTO = new SystemStatusDTO();
        systemStatusDTO.alertsAreFree = true;
        systemStatusCache.put(currentUserId.toUserBaseKey(), systemStatusDTO);

        StoreItemDTOList list = storeItemFactory.createAll(StoreItemFactory.WITH_IGNORE_SYSTEM_STATUS);
        assertThat(list.findFirstWhere(createFindPerDomain(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)))
                .isNotNull();
    }

    @Test public void testFollowSystemStatusWhereStatusSaysAlertsAreFreeShouldHaveNoAlert()
    {
        SystemStatusDTO systemStatusDTO = new SystemStatusDTO();
        systemStatusDTO.alertsAreFree = true;
        systemStatusCache.put(currentUserId.toUserBaseKey(), systemStatusDTO);

        StoreItemDTOList list = storeItemFactory.createAll(StoreItemFactory.WITH_FOLLOW_SYSTEM_STATUS);
        assertThat(list.findFirstWhere(createFindPerDomain(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS)))
                .isNull();
    }
    //</editor-fold>
}
