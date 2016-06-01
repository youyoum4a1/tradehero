package com.ayondo.academy.fragments.billing.store;

import com.android.internal.util.Predicate;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.system.SystemStatusDTO;
import com.ayondo.academy.api.system.SystemStatusKey;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.base.TestTHApp;
import com.ayondo.academy.billing.ProductIdentifierDomain;
import com.ayondo.academy.persistence.system.SystemStatusCache;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
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
