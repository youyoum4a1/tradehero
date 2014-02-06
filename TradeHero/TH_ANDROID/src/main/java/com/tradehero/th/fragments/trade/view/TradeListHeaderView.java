package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import java.lang.ref.WeakReference;

/**
 * Created by julien on 28/10/13
 */
public class TradeListHeaderView extends RelativeLayout
{
    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<SecurityCompactCache> securityCache;

    private PositionDTO positionDTO;
    private String securityName;
    private TextView securityNameTextView;
    private Button buyButton;
    private Button sellButton;

    private WeakReference<TradeListHeaderClickListener> listener = new WeakReference<>(null);

    //<editor-fold desc="Constructors">
    public TradeListHeaderView(Context context)
    {
        super(context);
    }

    public TradeListHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TradeListHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        initViews();
    }

    private void initViews()
    {
        securityNameTextView = (TextView) findViewById(R.id.trade_list_header_security_name);
        buyButton = (Button) findViewById(R.id.trade_list_header_buy);
        sellButton = (Button) findViewById(R.id.trade_list_header_sell);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (buyButton != null)
        {
            buyButton.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    if (listener == null)
                    {
                        return;
                    }

                    TradeListHeaderClickListener l = listener.get();
                    if (l != null)
                    {
                        l.onBuyButtonClicked(TradeListHeaderView.this);
                    }
                }
            });
        }
        if (sellButton != null)
        {
            sellButton.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    if (listener == null)
                    {
                        return;
                    }

                    TradeListHeaderClickListener l = listener.get();
                    if (l != null)
                    {
                        l.onSellButtonClicked(TradeListHeaderView.this);
                    }
                }
            });
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (buyButton != null)
        {
            buyButton.setOnClickListener(null);
        }
        if (sellButton != null)
        {
            sellButton.setOnClickListener(null);
        }
        super.onDetachedFromWindow();
    }

    public void bindOwnedPositionId(PositionDTO positionDTO)
    {
        this.positionDTO = positionDTO;

        if (positionDTO == null)
        {
            return;
        }

        if (positionDTO.isClosed())
        {
            this.sellButton.setVisibility(GONE);
        }

        SecurityIntegerId securityIntegerId = positionDTO.getSecurityIntegerId();

        if (securityIntegerId == null)
        {
            return;
        }

        SecurityId secId = this.securityIdCache.get().get(securityIntegerId);

        if (secId == null)
        {
            return;
        }

        SecurityCompactDTO security = this.securityCache.get().get(secId);

        if (security != null && security.name != null)
        {
            this.securityName = security.name.toUpperCase();
        }
        display();
    }

    private void display()
    {
        if (this.securityNameTextView != null && this.securityName != null)
        {
            this.securityNameTextView.setText(this.securityName);
        }
    }

    public TradeListHeaderClickListener getListener()
    {
        return listener.get();
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param listener
     */
    public void setListener(TradeListHeaderClickListener listener)
    {
        this.listener = new WeakReference<>(listener);
    }

    public static interface TradeListHeaderClickListener
    {
        public void onBuyButtonClicked(TradeListHeaderView tradeListHeaderView);
        public void onSellButtonClicked(TradeListHeaderView tradeListHeaderView);
    }
}
