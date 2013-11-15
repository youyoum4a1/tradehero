package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
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
public class TradeListHeaderView extends LinearLayout
{
    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<SecurityCompactCache> securityCache;

    private OwnedPositionId ownedPositionId;
    private String securityName;
    private TextView securityNameTextView;
    private Button buyButton;
    private Button sellButton;

    private WeakReference<TradeListHeaderClickListener> listener;

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

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        initViews();
    }

    private void initViews()
    {
        securityNameTextView = (TextView)findViewById(R.id.trade_history_header_username);
        buyButton = (Button)findViewById(R.id.trade_list_header_buy);
        buyButton.setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(View v)
            {
                if (listener == null) return;

                TradeListHeaderClickListener l = listener.get();
                if(l != null)
                    l.onBuyButtonClicked(TradeListHeaderView.this, ownedPositionId);
            }
        });
        sellButton = (Button)findViewById(R.id.trade_list_header_sell);
        sellButton.setOnClickListener(new OnClickListener()
        {
            @Override public void onClick(View v)
            {
                if (listener == null) return;

                TradeListHeaderClickListener l = listener.get();
                if(l != null)
                    l.onSellButtonClicked(TradeListHeaderView.this, ownedPositionId);
            }
        });
    }

    public void bindOwnedPositionId(OwnedPositionId ownedPositionId)
    {
        this.ownedPositionId = ownedPositionId;

        PositionDTO position = positionCache.get().get(ownedPositionId);

        if (position== null) return;

        if (position.isClosed())
        {
            this.sellButton.setVisibility(GONE);
        }

        SecurityIntegerId securityIntegerId = position.getSecurityIntegerId();

        if (securityIntegerId == null) return;

        SecurityId secId = this.securityIdCache.get().get(securityIntegerId);

        if (secId == null) return;

        SecurityCompactDTO security = this.securityCache.get().get(secId);

        if (security != null && security.name != null) {
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

    public OwnedPositionId getOwnedPositionId()
    {
        return ownedPositionId;
    }

    public TradeListHeaderClickListener getListener()
    {
        if (listener == null)
            return null;

        return listener.get();
    }

    public void setListener(TradeListHeaderClickListener listener)
    {
        this.listener = new WeakReference<>(listener);
    }

    public static interface TradeListHeaderClickListener
    {
        public void onBuyButtonClicked(TradeListHeaderView tradeListHeaderView, OwnedPositionId ownedPositionId);
        public void onSellButtonClicked(TradeListHeaderView tradeListHeaderView, OwnedPositionId ownedPositionId);
    }
}
