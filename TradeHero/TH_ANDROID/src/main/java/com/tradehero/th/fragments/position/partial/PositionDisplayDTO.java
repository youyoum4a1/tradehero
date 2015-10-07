package com.tradehero.th.fragments.position.partial;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.tradehero.common.annotation.ViewVisibilityValue;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.security.FxCurrency;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;
import com.tradehero.th.api.users.CurrentUserId;

public class PositionDisplayDTO extends PositionCompactDisplayDTO
{
    @Nullable public final Drawable stockLogo;
    @Nullable public final String stockLogoUrl;
    @DrawableRes public final int stockLogoRes;
    @NonNull public final String stockSymbol;
    @ViewVisibilityValue public final int companyNameVisibility;
    @NonNull public final String companyName;
    public final FxPairSecurityId fxPair;
    @ViewVisibilityValue public final int btnCloseVisibility;

    public PositionDisplayDTO(@NonNull Resources resources, @NonNull CurrentUserId currentUserId, @NonNull PositionDTO positionDTO,
            @NonNull SecurityCompactDTO securityCompactDTO)
    {
        super(resources, securityCompactDTO, positionDTO);

        //<editor-fold desc="Symbol and FxPair">
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            fxPair = ((FxSecurityCompactDTO) securityCompactDTO).getFxPair();
            stockSymbol = String.format("%s/%s", fxPair.left, fxPair.right);
        }
        else
        {
            fxPair = null;
            stockSymbol = String.format("%s:%s", securityCompactDTO.exchange, securityCompactDTO.symbol);
        }
        //</editor-fold>

        //<editor-fold desc="Stock Logo">
        if (securityCompactDTO.imageBlobUrl != null)
        {
            stockLogo = null;
            stockLogoUrl = securityCompactDTO.imageBlobUrl;
            stockLogoRes = R.drawable.default_image;
        }
        else if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            LayerDrawable layer = new LayerDrawable(new Drawable[] {resources.getDrawable(FxCurrency.create(fxPair.right).flag),
                    resources.getDrawable(FxCurrency.create(fxPair.left).flag)});
            int padding = resources.getDimensionPixelSize(R.dimen.margin_xsmall);
            layer.setLayerInset(0, padding, padding, 0, 0);
            layer.setLayerInset(1, 0, 0, padding, padding);
            stockLogo = layer;
            stockLogoUrl = null;
            stockLogoRes = R.drawable.default_image;
        }
        else
        {
            stockLogo = null;
            stockLogoUrl = null;
            stockLogoRes = securityCompactDTO.getExchangeLogoId();
        }
        //</editor-fold>

        //<editor-fold desc="Company Name">
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            companyNameVisibility = View.GONE;
            companyName = "";
        }
        else
        {
            companyNameVisibility = View.VISIBLE;
            companyName = securityCompactDTO.name;
        }
        //</editor-fold>

        //<editor-fold desc="Share Count">
        if (securityCompactDTO instanceof FxSecurityCompactDTO)
        {
            if (positionDTO.positionStatus == PositionStatus.CLOSED
                    || positionDTO.positionStatus == PositionStatus.FORCE_CLOSED
                    || currentUserId.get() != positionDTO.userId)
            {
                btnCloseVisibility = View.GONE;
            }
            else
            {
                btnCloseVisibility = View.VISIBLE;
            }
        }
        else
        {
            btnCloseVisibility = View.GONE;
        }

        //</editor-fold>
    }
}
