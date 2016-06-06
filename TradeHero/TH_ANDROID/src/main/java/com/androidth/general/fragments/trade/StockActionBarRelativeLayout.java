package com.androidth.general.fragments.trade;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.android.internal.util.Predicate;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.androidth.general.common.annotation.ViewVisibilityValue;
import com.androidth.general.common.graphics.WhiteToTransparentTransformation;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.compact.FxSecurityCompactDTO;
import com.androidth.general.api.security.key.FxPairSecurityId;
import com.androidth.general.api.watchlist.WatchlistPositionDTO;
import com.androidth.general.api.watchlist.WatchlistPositionDTOList;
import com.androidth.general.fragments.security.FxFlagContainer;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.utils.StringUtils;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class StockActionBarRelativeLayout extends RelativeLayout
        implements DTOView<StockActionBarRelativeLayout.Requisite>
{
    private static final float WATCHED_ALPHA_UNWATCHED = 0.5f;
    private static final float WATCHED_ALPHA_WATCHED = 1f;
    @ColorRes private static final int COLOR_RES_UNWATCHED = R.color.watchlist_button_color_none;
    @ColorRes private static final int COLOR_RES_WATCHED = R.color.watchlist_button_color;

    @Inject Picasso picasso;

    @Bind(R.id.stock_logo) @Nullable ImageView stockLogo;
    @Bind(R.id.flags_container) @Nullable FxFlagContainer flagsContainer;
    @Bind(R.id.tv_stock_title) protected TextView stockTitle;
    @Bind(R.id.tv_stock_sub_title) protected TextView stockSubTitle;
    @Bind(R.id.btn_watched) @Nullable protected ImageView btnWatched;
    @Bind(R.id.btn_alerted) @Nullable protected View btnAlerted;

    @Nullable protected Requisite dto;
    @NonNull protected final PublishSubject<UserAction> userActionSubject;

    //<editor-fold desc="Constructors">
    public StockActionBarRelativeLayout(Context context)
    {
        super(context);
        userActionSubject = PublishSubject.create();
    }

    public StockActionBarRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        userActionSubject = PublishSubject.create();
    }

    public StockActionBarRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        userActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
        HierarchyInjector.inject(this);
        if (stockLogo != null)
        {
            stockLogo.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        if (stockLogo != null)
        {
            picasso.cancelRequest(stockLogo);
        }
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull final Requisite dto)
    {
        this.dto = dto;

        if (stockLogo != null)
        {
            stockLogo.setVisibility(dto.stockLogoVisibility);
            RequestCreator request;
            if (dto.stockLogoUrl != null)
            {
                request = picasso.load(dto.stockLogoUrl);
            }
            else
            {
                request = picasso.load(dto.stockLogoRes);
            }
            request.placeholder(R.drawable.default_image)
                    .transform(new WhiteToTransparentTransformation())
                    .into(stockLogo, new Callback()
                    {
                        @Override public void onSuccess()
                        {
                        }

                        @Override public void onError()
                        {
                            stockLogo.setImageResource(dto.stockLogoRes);
                        }
                    });
        }

        if (flagsContainer != null)
        {
            flagsContainer.setVisibility(dto.flagsContainerVisibility);
            flagsContainer.display(dto.fxPair);
        }

        if (dto.securityCompactDTO != null)
        {
            FxPairSecurityId fxPairSecurityId = null;
            if (dto.securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                fxPairSecurityId = ((FxSecurityCompactDTO) dto.securityCompactDTO).getFxPair();
            }

            if (fxPairSecurityId != null)
            {
                stockTitle.setText(String.format("%s/%s", fxPairSecurityId.left, fxPairSecurityId.right));
                stockSubTitle.setText(null);
            }
            else
            {
                if (!StringUtils.isNullOrEmpty(dto.securityCompactDTO.name))
                {
                    if (stockTitle != null)
                    {
                        stockTitle.setText(dto.securityCompactDTO.name);
                    }
                    if (stockSubTitle != null)
                    {
                        stockSubTitle.setText(dto.securityCompactDTO.getExchangeSymbol());
                    }
                }
                else
                {
                    if (stockTitle != null)
                    {
                        stockTitle.setText(dto.securityCompactDTO.getExchangeSymbol());
                    }
                    if (stockSubTitle != null)
                    {
                        stockSubTitle.setText(null);
                    }
                }
            }
        }

        if (btnWatched != null)
        {
            if (dto.watchedList == null
                    || dto.securityCompactDTO == null
                    || dto.securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                btnWatched.setVisibility(INVISIBLE);
                btnWatched.setEnabled(false);
            }
            else
            {
                btnWatched.setVisibility(VISIBLE);
                btnWatched.setEnabled(true);
                boolean watched = dto.watchedList.contains(dto.securityId);
                Drawable drawable = DrawableCompat.wrap(btnWatched.getDrawable());
                DrawableCompat.setTint(
                        drawable,
                        getResources().getColor(watched
                                ? COLOR_RES_WATCHED
                                : COLOR_RES_UNWATCHED));
                btnWatched.setImageDrawable(drawable);
                btnWatched.setAlpha(watched ?
                        WATCHED_ALPHA_WATCHED :
                        WATCHED_ALPHA_UNWATCHED);
            }
        }

        if (btnAlerted != null)
        {
            if (dto.mappedAlerts == null
                    || dto.securityCompactDTO == null
                    || dto.securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                btnAlerted.setVisibility(GONE);
            }
            else
            {
                btnAlerted.setVisibility(VISIBLE);
                float alpha;
                AlertCompactDTO compactDTO = dto.mappedAlerts.get(dto.securityId);
                if ((compactDTO != null) && compactDTO.active)
                {
                    alpha = WATCHED_ALPHA_WATCHED;
                }
                else
                {
                    alpha = WATCHED_ALPHA_UNWATCHED;
                }

                btnAlerted.setAlpha(alpha);
            }
        }
    }

    @SuppressWarnings("unused")
    @Nullable @OnClick(R.id.btn_watched)
    protected void onButtonWatchedClicked(View view)
    {
        if (dto != null && dto.watchedList != null)
        {
            userActionSubject.onNext(new WatchlistUserAction(
                    dto.securityId,
                    dto.watchedList.findFirstWhere(new Predicate<WatchlistPositionDTO>()
                    {
                        @Override public boolean apply(WatchlistPositionDTO watchlistPositionDTO)
                        {
                            return watchlistPositionDTO.securityDTO != null
                                    && watchlistPositionDTO.securityDTO.getSecurityId().equals(dto.securityId);
                        }
                    }) == null));
        }
    }

    @SuppressWarnings("unused")
    @Nullable @OnClick(R.id.btn_alerted)
    protected void onButtonAlertedClicked(View view)
    {
        if (dto != null && dto.mappedAlerts != null)
        {
            AlertCompactDTO alert = dto.mappedAlerts.get(dto.securityId);
            if (alert == null)
            {
                userActionSubject.onNext(new CreateAlertUserAction(dto.securityId));
            }
            else
            {
                userActionSubject.onNext(new UpdateAlertUserAction(dto.securityId, alert));
            }
        }
    }

    @NonNull public Observable<UserAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    public static class Requisite implements DTO
    {
        @NonNull final SecurityId securityId;
        @Nullable final SecurityCompactDTO securityCompactDTO;
        @Nullable final WatchlistPositionDTOList watchedList;
        @Nullable final Map<SecurityId, AlertCompactDTO> mappedAlerts;

        @ViewVisibilityValue public final int stockLogoVisibility;
        @Nullable public final String stockLogoUrl;
        @DrawableRes public final int stockLogoRes;
        @Nullable public final FxPairSecurityId fxPair;
        @ViewVisibilityValue public final int flagsContainerVisibility;

        public Requisite(
                @NonNull SecurityId securityId,
                @Nullable SecurityCompactDTO securityCompactDTO,
                @Nullable WatchlistPositionDTOList watchedList,
                @Nullable Map<SecurityId, AlertCompactDTO> mappedAlerts)
        {
            this.securityId = securityId;
            this.securityCompactDTO = securityCompactDTO;
            this.watchedList = watchedList;
            this.mappedAlerts = mappedAlerts;

            //<editor-fold desc="Stock Logo">
            if (securityCompactDTO != null && securityCompactDTO.imageBlobUrl != null)
            {
                stockLogoVisibility = VISIBLE;
                flagsContainerVisibility = GONE;
                stockLogoUrl = securityCompactDTO.imageBlobUrl;
                stockLogoRes = R.drawable.default_image;
            }
            else if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                stockLogoVisibility = GONE;
                flagsContainerVisibility = VISIBLE;
                stockLogoUrl = null;
                stockLogoRes = R.drawable.default_image;
            }
            else if (securityCompactDTO != null)
            {
                stockLogoVisibility = VISIBLE;
                flagsContainerVisibility = GONE;
                stockLogoUrl = null;
                stockLogoRes = securityCompactDTO.getExchangeLogoId();
            }
            else
            {
                stockLogoVisibility = GONE;
                flagsContainerVisibility = GONE;
                stockLogoUrl = null;
                stockLogoRes = R.drawable.default_image;
            }
            //</editor-fold>

            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                fxPair = ((FxSecurityCompactDTO) securityCompactDTO).getFxPair();
            }
            else
            {
                fxPair = null;
            }
        }
    }

    abstract public static class UserAction
    {
        @NonNull public final SecurityId securityId;

        public UserAction(@NonNull SecurityId securityId)
        {
            this.securityId = securityId;
        }
    }

    public static class WatchlistUserAction extends UserAction
    {
        public final boolean add;

        public WatchlistUserAction(@NonNull SecurityId securityId, boolean add)
        {
            super(securityId);
            this.add = add;
        }
    }

    public static class CreateAlertUserAction extends UserAction
    {
        public CreateAlertUserAction(@NonNull SecurityId securityId)
        {
            super(securityId);
        }
    }

    public static class UpdateAlertUserAction extends UserAction
    {
        @NonNull public final AlertCompactDTO alertCompactDTO;

        public UpdateAlertUserAction(@NonNull SecurityId securityId, @NonNull AlertCompactDTO alertCompactDTO)
        {
            super(securityId);
            this.alertCompactDTO = alertCompactDTO;
        }
    }
}
