package com.tradehero.th.fragments.billing.store;

import com.tradehero.thm.R;
import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.fragments.alert.AlertManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.persistence.system.SystemStatusCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class StoreItemFactory
{
    public static final boolean WITH_IGNORE_SYSTEM_STATUS = true;
    public static final boolean WITH_FOLLOW_SYSTEM_STATUS = false;

    @NotNull private final SystemStatusCache systemStatusCache;
    @NotNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public StoreItemFactory(
            @NotNull SystemStatusCache systemStatusCache,
            @NotNull CurrentUserId currentUserId)
    {
        this.systemStatusCache = systemStatusCache;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @NotNull public StoreItemDTOList createAll(boolean ignoreSystemStatus)
    {
        StoreItemDTOList created = new StoreItemDTOList();

        created.add(new StoreItemTitleDTO(R.string.store_header_in_app_purchases));
        created.add(new StoreItemPromptPurchaseDTO(
                R.string.store_buy_virtual_dollars,
                R.drawable.icn_th_dollars,
                R.drawable.btn_buy_thd_large,
                ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR));
        created.add(new StoreItemPromptPurchaseDTO(
                R.string.store_buy_follow_credits,
                R.drawable.icn_follow_credits,
                R.drawable.btn_buy_credits_large,
                ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS));
        SystemStatusDTO systemStatusDTO = systemStatusCache.get(currentUserId.toUserBaseKey());
        if (ignoreSystemStatus
                || systemStatusDTO == null
                || !systemStatusDTO.alertsAreFree)
        {
            created.add(new StoreItemPromptPurchaseDTO(
                    R.string.store_buy_stock_alerts,
                    R.drawable.icn_stock_alert,
                    R.drawable.btn_buy_stock_alerts,
                    ProductIdentifierDomain.DOMAIN_STOCK_ALERTS));
        }
        created.add(new StoreItemPromptPurchaseDTO(
                R.string.store_buy_reset_portfolio,
                R.drawable.icn_reset_portfolio,
                R.drawable.btn_buy_reset_large,
                ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO));

        created.add(new StoreItemTitleDTO(R.string.store_header_manage_purchases));
        created.add(new StoreItemHasFurtherDTO(
                R.string.store_manage_heroes,
                R.drawable.icn_follow_credits,
                HeroManagerFragment.class));
        created.add(new StoreItemHasFurtherDTO(
                R.string.store_manage_followers,
                R.drawable.icn_view_followers,
                FollowerManagerFragment.class));
        created.add(new StoreItemHasFurtherDTO(
                R.string.store_manage_stock_alerts,
                R.drawable.icn_stock_alert,
                AlertManagerFragment.class));

        return created;
    }
}
