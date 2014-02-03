package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.graphics.ForUserPhoto;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import java.lang.ref.WeakReference;

/**
 * Created by julien on 28/10/13
 */
public class TradeListOverlayHeaderView extends LinearLayout
{
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject Lazy<Picasso> picasso;
    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<UserProfileCache> userCache;

    private OwnedPositionId ownedPositionId;
    private TextView usernameTextView;
    private ImageView imageProfile;
    private TextView qualifiedSecuritySymbol;

    private LinearLayout righSection;

    private PositionDTO position;
    private SecurityId securityId;
    private UserBaseDTO user;
    private String qualifiedSymbol;

    private WeakReference<Listener> listener = new WeakReference<>(null);

    //<editor-fold desc="Constructors">
    public TradeListOverlayHeaderView(Context context)
    {
        super(context);
    }

    public TradeListOverlayHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TradeListOverlayHeaderView(Context context, AttributeSet attrs, int defStyle)
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
        usernameTextView = (TextView) findViewById(R.id.trade_history_header_username);
        imageProfile = (ImageView) findViewById(R.id.trade_history_header_image);
        qualifiedSecuritySymbol = (TextView) findViewById(R.id.trade_history_header_security_symbol);
        righSection = (LinearLayout) findViewById(R.id.trade_history_header_right_section);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        OnClickListener usernameListener = new OnClickListener()
        {
            @Override public void onClick(View v)
            {
                Listener l = listener.get();
                if (l != null)
                {
                    l.onUserClicked(TradeListOverlayHeaderView.this, ownedPositionId.getUserBaseKey());
                }
            }
        };

        if (this.usernameTextView != null)
        {
            this.usernameTextView.setOnClickListener(usernameListener);
        }

        if (this.imageProfile != null)
        {
            this.imageProfile.setOnClickListener(usernameListener);
        }

        if (this.righSection != null)
        {
            this.righSection.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    Listener l = listener.get();
                    if (l != null)
                    {
                        l.onSecurityClicked(TradeListOverlayHeaderView.this, ownedPositionId);
                    }
                }
            });
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (this.usernameTextView != null)
        {
            this.usernameTextView.setOnClickListener(null);
        }

        if (this.imageProfile != null)
        {
            this.imageProfile.setOnClickListener(null);
        }

        if (this.righSection != null)
        {
            this.righSection.setOnClickListener(null);
        }

        super.onDetachedFromWindow();
    }

    public void bindOwnedPositionId(OwnedPositionId ownedPositionId)
    {
        this.ownedPositionId = ownedPositionId;
        this.user = userCache.get().get(ownedPositionId.getUserBaseKey());
        this.position = positionCache.get().get(ownedPositionId);
        SecurityIntegerId securityIntegerId = this.position.getSecurityIntegerId();
        this.securityId = this.securityIdCache.get().get(securityIntegerId);
        if (this.securityId != null)
        {
            this.qualifiedSymbol = String.format("%s:%s", securityId.exchange.toUpperCase(), securityId.securitySymbol.toUpperCase());
        }
        display();
    }

    private void display()
    {
        if (this.user != null)
        {
            if (this.usernameTextView != null)
            {
                usernameTextView.setText(user.displayName);
            }

            if (this.imageProfile != null)
            {
                picasso.get().load(user.picture)
                        .transform(peopleIconTransformation)
                        .into(this.imageProfile);
            }
        }

        if (this.qualifiedSecuritySymbol != null && this.qualifiedSymbol != null)
        {
            qualifiedSecuritySymbol.setText(qualifiedSymbol);
        }
    }

    public void setListener(Listener listener)
    {
        this.listener = new WeakReference<>(listener);
    }

    public static interface Listener
    {
        public void onSecurityClicked(TradeListOverlayHeaderView headerView, OwnedPositionId ownedPositionId);
        public void onUserClicked(TradeListOverlayHeaderView headerView, UserBaseKey userId);
    }
}
