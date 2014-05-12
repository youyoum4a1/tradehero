package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import javax.inject.Inject;

public class TradeListOverlayHeaderView extends LinearLayout
{
    @Inject @ForUserPhoto protected Transformation peopleIconTransformation;
    @Inject Lazy<Picasso> picasso;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<UserProfileCache> userCache;

    @InjectView(R.id.trade_history_header_username) protected TextView usernameTextView;
    @InjectView(R.id.trade_history_header_image) protected ImageView imageProfile;
    @InjectView(R.id.trade_history_header_security_symbol) protected TextView qualifiedSecuritySymbol;
    @InjectView(R.id.trade_history_header_right_section) protected View righSection;

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
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        OnClickListener usernameListener = new OnClickListener()
        {
            @Override public void onClick(View v)
            {
                Listener l = listener.get();
                if (l != null && position != null)
                {
                    l.onUserClicked(TradeListOverlayHeaderView.this, position.getUserBaseKey());
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
                    if (l != null && position != null)
                    {
                        l.onSecurityClicked(TradeListOverlayHeaderView.this, position.getOwnedPositionId());
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

    public void bindOwnedPositionId(PositionDTO positionDTO)
    {
        this.user = userCache.get().get(positionDTO.getUserBaseKey());
        this.position = positionDTO;
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
                loadDefaultImage();
                picasso.get().load(user.picture)
                        .transform(peopleIconTransformation)
                        .placeholder(this.imageProfile.getDrawable())
                        .into(this.imageProfile);
            }
        }

        if (this.qualifiedSecuritySymbol != null && this.qualifiedSymbol != null)
        {
            qualifiedSecuritySymbol.setText(qualifiedSymbol);
        }
    }

    protected void loadDefaultImage()
    {
        picasso.get().load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(this.imageProfile);
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
