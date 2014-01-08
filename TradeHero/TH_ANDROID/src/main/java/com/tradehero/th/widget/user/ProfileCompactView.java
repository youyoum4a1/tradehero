package com.tradehero.th.widget.user;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.utils.THSignedNumber;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.fragments.portfolio.PortfolioRequestListener;
import java.lang.ref.WeakReference;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/17/13 Time: 12:51 PM Copyright (c) TradeHero */
public class ProfileCompactView extends RelativeLayout implements DTOView<UserProfileDTO>
{
    private ImageView avatar;

    private TextView roiSinceInception;

    private TextView followersCount;
    private TextView heroesCount;
    private TextView username;
    private ImageView btnDefaultPortfolio;

    @Inject protected Picasso picasso;
    private WeakReference<PortfolioRequestListener> portfolioRequestListener = new WeakReference<>(null);

    //<editor-fold desc="Constructors">
    public ProfileCompactView(Context context)
    {
        this(context, null);
    }

    public ProfileCompactView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ProfileCompactView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    private void init()
    {
        avatar = (ImageView) findViewById(R.id.user_profile_compact_avatar);
        username = (TextView) findViewById(R.id.user_profile_compact_display_name);
        roiSinceInception = (TextView) findViewById(R.id.user_profile_compact_roi);

        followersCount = (TextView) findViewById(R.id.user_profile_compact_followers_count);
        heroesCount = (TextView) findViewById(R.id.user_profile_compact_heroes_count);
        btnDefaultPortfolio = (ImageView) findViewById(R.id.btn_user_profile_default_portfolio);
        if (btnDefaultPortfolio != null)
        {
            btnDefaultPortfolio.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    pushDefaultPortfolio();
                }
            });
        }

        DaggerUtils.inject(this);
    }

    @Override public void display(UserProfileDTO dto)
    {
        if (dto == null || dto.picture == null)
        {
            return;
        }

        picasso
            .load(dto.picture)
            .transform(new RoundedShapeTransformation())
            .into(avatar);

        if (dto.portfolio != null && dto.portfolio.roiSinceInception != null)
        {
            THSignedNumber thRoiSinceInception = new THSignedNumber(THSignedNumber.TYPE_PERCENTAGE, dto.portfolio.roiSinceInception*100);
            roiSinceInception.setText(thRoiSinceInception.toString());
            roiSinceInception.setTextColor(getResources().getColor(thRoiSinceInception.getColor()));
        }

        followersCount.setText(Integer.toString(dto.followerCount));
        heroesCount.setText(Integer.toString(dto.heroIds.size()));
        username.setText(dto.displayName);
    }

    /**
     * Listeners should be strongly referenced elsewhere
     * @param portfolioRequestListener
     */
    public void setPortfolioRequestListener(PortfolioRequestListener portfolioRequestListener)
    {
        this.portfolioRequestListener = new WeakReference<>(portfolioRequestListener);
    }

    private void pushDefaultPortfolio()
    {
        PortfolioRequestListener listener = portfolioRequestListener.get();
        if (listener != null)
        {
            listener.onDefaultPortfolioRequested();
        }
    }
}
