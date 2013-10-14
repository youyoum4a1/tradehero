package com.tradehero.th.widget.portfolio;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 12:28 PM To change this template use File | Settings | File Templates. */
public class PortfolioHeaderItemView extends RelativeLayout
{
    public static final String TAG = PortfolioHeaderItemView.class.getName();

    private ImageView userIcon;
    private TextView title;
    private TextView description;

    private UserBaseDTO userBaseDTO;
    private PortfolioCompactDTO portfolioCompactDTO;
    private PortfolioDTO portfolioDTO;

    //<editor-fold desc="Constructors">
    public PortfolioHeaderItemView(Context context)
    {
        super(context);
    }

    public PortfolioHeaderItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PortfolioHeaderItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
    }

    private void initViews()
    {
        userIcon = (ImageView) findViewById(R.id.user_icon);
        title = (TextView) findViewById(R.id.portfolio_title);
        description = (TextView) findViewById(R.id.portfolio_description);
    }

    public void linkWith(UserBaseDTO userBaseDTO, boolean andDisplay)
    {
        this.userBaseDTO = userBaseDTO;
        if (andDisplay)
        {
            displayUserIcon();
        }
    }

    public void linkWith(PortfolioCompactDTO portfolioCompactDTO, boolean andDisplay)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        if (andDisplay)
        {
            displayTitle();
        }
    }

    public void linkWith(PortfolioDTO portfolioDTO, boolean andDisplay)
    {
        this.portfolioDTO = portfolioDTO;
        if (andDisplay)
        {
            displayDescription();
        }
    }

    public void display()
    {
        displayUserIcon();
        displayTitle();
        displayDescription();
    }

    public void displayUserIcon()
    {
        if (userIcon != null)
        {
            if (userBaseDTO != null)
            {
                Picasso.with(getContext())
                        .load(userBaseDTO.picture)
                        .into(userIcon);
            }
        }
    }

    public void displayTitle()
    {
        if (title != null)
        {
            if (portfolioCompactDTO != null)
            {
                title.setText(portfolioCompactDTO.title);
            }
        }
    }

    public void displayDescription()
    {
        if (description != null)
        {
            if (portfolioDTO != null)
            {
                description.setText(portfolioDTO.description);
            }
        }
    }
}
