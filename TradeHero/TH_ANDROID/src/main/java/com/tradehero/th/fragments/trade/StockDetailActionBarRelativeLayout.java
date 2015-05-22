package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.fragments.security.SecurityCircleProgressBar;
import com.tradehero.th.utils.GraphicUtil;
import com.tradehero.th.utils.StringUtils;
import java.util.Map;
import rx.Observable;
import rx.subjects.PublishSubject;

public class StockDetailActionBarRelativeLayout extends RelativeLayout
        implements DTOView<StockDetailActionBarRelativeLayout.Requisite>
{
    private static final float WATCHED_ALPHA_UNWATCHED = 0.5f;
    private static final float WATCHED_ALPHA_WATCHED = 1f;

    @InjectView(R.id.tv_stock_title) TextView stockTitle;
    @InjectView(R.id.tv_stock_sub_title) TextView stockSubTitle;
    @InjectView(R.id.circle_progressbar) SecurityCircleProgressBar circleProgressBar;
    @InjectView(R.id.action_bar_market_closed_icon) View marketCloseIcon;
    @InjectView(R.id.btn_watched) ImageView btnWatched;
    @InjectView(R.id.btn_alerted) View btnAlerted;

    @NonNull private final PublishSubject<UserAction> userActionSubject;

    //<editor-fold desc="Constructors">
    public StockDetailActionBarRelativeLayout(Context context)
    {
        super(context);
        userActionSubject = PublishSubject.create();
    }

    public StockDetailActionBarRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        userActionSubject = PublishSubject.create();
    }

    public StockDetailActionBarRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        userActionSubject = PublishSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull Requisite dto)
    {
        if (dto.securityCompactDTO != null)
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

            circleProgressBar.display(dto.securityCompactDTO);
            if (marketCloseIcon != null)
            {
                boolean marketIsOpen = dto.securityCompactDTO.marketOpen == null || dto.securityCompactDTO.marketOpen;
                marketCloseIcon.setVisibility(marketIsOpen ? View.GONE : View.VISIBLE);
            }
        }

        if (btnWatched != null)
        {
            if (dto.watchedList == null)
            {
                // TODO show disabled
                btnWatched.setVisibility(View.INVISIBLE);
            }
            else
            {
                btnWatched.setVisibility(View.VISIBLE);
                boolean watched = dto.watchedList.contains(dto.securityId);
                btnWatched.setAlpha(watched ?
                        WATCHED_ALPHA_WATCHED :
                        WATCHED_ALPHA_UNWATCHED);
                GraphicUtil.applyColorFilter(
                        btnWatched,
                        getResources().getColor(
                                watched
                                        ? R.color.watchlist_button_color
                                        : R.color.white));
            }
        }

        if (btnAlerted != null)
        {
            btnAlerted.setVisibility(dto.mappedAlerts != null ? View.VISIBLE : View.GONE);
            if (dto.mappedAlerts != null)
            {
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
    @OnClick(R.id.btn_watched)
    protected void onButtonWatchedClicked(View view)
    {
        userActionSubject.onNext(UserAction.WATCHLIST);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_alerted)
    protected void onButtonAlertedClicked(View view)
    {
        userActionSubject.onNext(UserAction.ALERT);
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
        }
    }

    public enum UserAction
    {
        ALERT,
        WATCHLIST,
    }
}
