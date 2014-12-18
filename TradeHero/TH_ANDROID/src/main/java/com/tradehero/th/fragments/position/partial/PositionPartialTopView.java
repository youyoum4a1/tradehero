package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.position.PositionStatus;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;
import com.tradehero.th.fragments.security.FxFlagContainer;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.utils.THColorUtils;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class PositionPartialTopView extends LinearLayout
{
    @Inject protected Picasso picasso;
    @Inject protected SecurityIdCache securityIdCache;
    @Inject protected SecurityCompactCacheRx securityCompactCache;
    @Inject protected PositionDTOUtils positionDTOUtils;

    @InjectView(R.id.stock_logo) protected ImageView stockLogo;
    @InjectView(R.id.flags_container) protected FxFlagContainer flagsContainer;
    @InjectView(R.id.stock_symbol) protected TextView stockSymbol;
    @InjectView(R.id.company_name) protected TextView companyName;
    @InjectView(R.id.share_count) protected TextView shareCount;
    @InjectView(R.id.last_price_container) protected View lastPriceContainer;
    @InjectView(R.id.stock_movement_indicator) protected TextView stockMovementIndicator;
    @InjectView(R.id.stock_last_price) protected TextView stockLastPrice;
    @InjectView(R.id.ic_market_close) protected ImageView marketClose;
    @InjectView(R.id.position_percentage) protected TextView positionPercent;
    @InjectView(R.id.position_unrealised_pl) protected TextView positionUnrealisedPL;
    @InjectView(R.id.position_last_amount_header) protected TextView positionLastAmountHeader;
    @InjectView(R.id.position_last_amount) protected TextView positionLastAmount;
    @InjectView(R.id.position_force_closed) protected View forceClosed;

    protected PositionDTO positionDTO;
    protected SecurityId securityId;
    private Subscription securityCompactCacheFetchSubscription;
    protected SecurityCompactDTO securityCompactDTO;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public PositionPartialTopView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PositionPartialTopView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PositionPartialTopView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
        if (stockLogo != null)
        {
            stockLogo.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        unsubscribe(securityCompactCacheFetchSubscription);
        securityCompactCacheFetchSubscription = null;
        if (stockLogo != null)
        {
            stockLogo.setImageDrawable(null);
        }
        super.onDetachedFromWindow();
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    public void linkWith(PositionDTO positionDTO, final boolean andDisplay)
    {
        boolean isDifferentSecurity = positionDTO != null
                && (this.positionDTO == null
                || !this.positionDTO.getSecurityIntegerId().equals(positionDTO.getSecurityIntegerId()));
        this.positionDTO = positionDTO;
        if (andDisplay)
        {
            displayPositionPercent();
            displayUnrealisedPL();
            displayPositionLastAmountHeader();
            displayPositionLastAmount();
            displayCompanyName();
            displayShareCount();
            displayForceClosed();
        }
        if (positionDTO == null)
        {
            unsubscribe(securityCompactCacheFetchSubscription);
        }
        else if (isDifferentSecurity)
        {
            unsubscribe(securityCompactCacheFetchSubscription);
            securityCompactCacheFetchSubscription = securityIdCache.get(positionDTO.getSecurityIntegerId())
                    .map(pair -> pair.second)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(this::linkWith)
                    .flatMap(securityId -> securityCompactCache.get(securityId))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            pair -> linkWith(pair.second),
                            this::handleSecurityError);
        }
    }

    public void handleSecurityError(Throwable e)
    {
        THToast.show("There was an error when fetching the security information");
        Timber.e(e, "Error fetching the security");
    }

    protected void linkWith(@NonNull SecurityId securityId)
    {
        this.securityId = securityId;
        displayStockSymbol();
    }

    protected void linkWith(SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
        displayStockSymbol();
        displayStockLogo();
        displayCompanyName();
        displayShareCount();
        displayLastPriceContainer();
        displayPositionPercent();
        displayUnrealisedPL();
        displayStockMovementIndicator();
        displayStockLastPrice();
        displayMarketClose();
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayStockLogo();
        displayStockSymbol();
        displayCompanyName();
        displayShareCount();
        displayLastPriceContainer();
        displayStockMovementIndicator();
        displayStockLastPrice();
        displayMarketClose();

        displayPositionPercent();
        displayUnrealisedPL();
        displayPositionLastAmountHeader();
        displayPositionLastAmount();
        displayForceClosed();
    }

    public void displayStockLogo()
    {
        if (stockLogo != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.imageBlobUrl != null)
            {
                stockLogo.setVisibility(VISIBLE);
                flagsContainer.setVisibility(GONE);
                picasso.load(securityCompactDTO.imageBlobUrl)
                        .placeholder(R.drawable.default_image)
                        .transform(new WhiteToTransparentTransformation())
                        .into(stockLogo, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                            }

                            @Override public void onError()
                            {
                                displayStockLogoExchange();
                            }
                        });
            }
            else if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                stockLogo.setVisibility(GONE);
                flagsContainer.setVisibility(VISIBLE);
                flagsContainer.display(((FxSecurityCompactDTO) securityCompactDTO).getFxPair());
            }
            else
            {
                displayStockLogoExchange();
            }
        }
    }

    public void displayStockLogoExchange()
    {
        stockLogo.setVisibility(VISIBLE);
        flagsContainer.setVisibility(GONE);
        if (securityCompactDTO != null)
        {
            picasso.load(securityCompactDTO.getExchangeLogoId())
                    .placeholder(R.drawable.default_image)
                    .into(stockLogo);
        }
        else
        {
            stockLogo.setImageResource(R.drawable.default_image);
        }
    }

    public void displayStockSymbol()
    {
        if (stockSymbol != null)
        {
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                FxPairSecurityId pair = ((FxSecurityCompactDTO) securityCompactDTO).getFxPair();
                stockSymbol.setText(String.format("%s/%s", pair.left, pair.right));
            }
            else if (securityId != null)
            {
                stockSymbol.setText(String.format("%s:%s", securityId.getExchange(), securityId.getSecuritySymbol()));
            }
            else
            {
                stockSymbol.setText("");
            }
        }
    }

    public void displayCompanyName()
    {
        if (companyName != null)
        {
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                companyName.setVisibility(GONE);
            }
            else if (securityCompactDTO != null)
            {
                companyName.setVisibility(VISIBLE);
                companyName.setText(securityCompactDTO.name);
            }
            else
            {
                companyName.setVisibility(VISIBLE);
                companyName.setText("");
            }
        }
    }

    public void displayShareCount()
    {
        if (shareCount != null)
        {
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                shareCount.setVisibility(VISIBLE);
                String count;
                if (positionDTO == null || positionDTO.shares == null)
                {
                    count = getResources().getString(R.string.na);
                }
                else
                {
                    count = THSignedNumber.builder(positionDTO.shares)
                            .build().toString();
                }
                shareCount.setText(getResources().getString(R.string.position_unit_count, count));
            }
            else
            {
                shareCount.setVisibility(GONE);
            }
        }
    }

    public void displayLastPriceContainer()
    {
        if (lastPriceContainer != null)
        {
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                lastPriceContainer.setVisibility(GONE);
            }
            else
            {
                lastPriceContainer.setVisibility(VISIBLE);
            }
        }
    }

    public void displayStockMovementIndicator()
    {
        if (stockMovementIndicator != null)
        {
            if (securityCompactDTO != null)
            {
                if (securityCompactDTO.pc50DMA == null)
                {
                    stockMovementIndicator.setText(R.string.na);
                    return;
                }
                else if (securityCompactDTO.pc50DMA > 0)
                {
                    stockMovementIndicator.setText(R.string.arrow_prefix_positive);
                }
                else if (securityCompactDTO.pc50DMA < 0)
                {
                    stockMovementIndicator.setText(R.string.arrow_prefix_negative);
                }
                stockMovementIndicator.setTextColor(THColorUtils.getProperColorForNumber(securityCompactDTO.pc50DMA / 5));
            }
        }
    }

    public void displayStockLastPrice()
    {
        if (stockLastPrice != null)
        {
            if (securityCompactDTO != null)
            {
                if (securityCompactDTO.lastPrice != null)
                {
                    stockLastPrice.setText(String.format("%s %.2f", securityCompactDTO.currencyDisplay, securityCompactDTO.lastPrice));
                }
                else
                {
                    stockLastPrice.setText(R.string.na);
                }

                if (securityCompactDTO.marketOpen == null || securityCompactDTO.marketOpen)
                {
                    stockLastPrice.setTextColor(getResources().getColor(R.color.exchange_symbol));
                }
                else
                {
                    stockLastPrice.setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
            }
        }
    }

    public void displayMarketClose()
    {
        if (marketClose != null)
        {
            if (securityCompactDTO != null)
            {
                marketClose.setVisibility(securityCompactDTO.marketOpen == null || securityCompactDTO.marketOpen ? INVISIBLE : VISIBLE);
            }
        }
    }

    public void displayPositionPercent()
    {
        if (positionPercent != null)
        {
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                positionPercent.setVisibility(GONE);
            }
            else if (positionDTO instanceof PositionInPeriodDTO && ((PositionInPeriodDTO) positionDTO).isProperInPeriod())
            {
                positionPercent.setVisibility(VISIBLE);
                positionDTOUtils.setROIInPeriod(positionPercent, (PositionInPeriodDTO) positionDTO);
            }
            else
            {
                positionPercent.setVisibility(VISIBLE);

                positionDTOUtils.setROISinceInception(positionPercent, positionDTO);
            }
        }
    }

    public void displayUnrealisedPL()
    {
        if (positionUnrealisedPL != null)
        {
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                positionUnrealisedPL.setVisibility(VISIBLE);
                String unrealised;
                if (positionDTO != null && positionDTO.unrealizedPLRefCcy != null)
                {
                    THSignedMoney unrealisedMoney = THSignedMoney.builder(positionDTO.unrealizedPLRefCcy)
                            .currency(positionDTO.getNiceCurrency())
                            .withSign()
                            .signTypeArrow()
                            .build();
                    positionUnrealisedPL.setTextColor(unrealisedMoney.getColor());
                    unrealised = unrealisedMoney.toString();
                }
                else
                {
                    unrealised = getResources().getString(R.string.na);
                }
                positionUnrealisedPL.setText(unrealised);
            }
            else
            {
                positionUnrealisedPL.setVisibility(GONE);
            }
        }
    }

    public void displayPositionLastAmountHeader()
    {
        if (positionLastAmountHeader != null)
        {
            Boolean isOpen = positionDTO == null
                    ? null
                    : positionDTO.isOpen();
            if (isOpen == null || isOpen)
            {
                positionLastAmountHeader.setVisibility(GONE);
            }
            else
            {
                positionLastAmountHeader.setVisibility(VISIBLE);
            }
        }
    }

    public void displayPositionLastAmount()
    {
        if (positionLastAmount != null)
        {
            THSignedNumber number = null;
            if (positionDTO != null)
            {
                Boolean closed = positionDTO.isClosed();
                if (closed != null && closed && positionDTO.realizedPLRefCcy != null)
                {
                    number = THSignedMoney.builder(positionDTO.realizedPLRefCcy)
                            .withSign()
                            .signTypeMinusOnly()
                            .currency(positionDTO.getNiceCurrency())
                            .build();
                }
                else if (closed != null && !closed)
                {
                    number = THSignedMoney.builder(positionDTO.marketValueRefCcy)
                            .withSign()
                            .signTypeMinusOnly()
                            .currency(positionDTO.getNiceCurrency())
                            .build();
                }
            }

            if (number == null)
            {
                positionLastAmount.setText(R.string.na);
            }
            else
            {
                positionLastAmount.setText(number.toString());
            }
        }
    }

    public void displayForceClosed()
    {
        if (forceClosed != null)
        {
            boolean isForceClosed = positionDTO != null && positionDTO.positionStatus != null &&
                    positionDTO.positionStatus.equals(PositionStatus.FORCE_CLOSED);
            forceClosed.setVisibility(isForceClosed ? VISIBLE : GONE);
        }
    }
    //</editor-fold>
}
