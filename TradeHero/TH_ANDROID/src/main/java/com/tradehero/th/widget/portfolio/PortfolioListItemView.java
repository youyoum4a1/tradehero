package com.tradehero.th.widget.portfolio;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 12:28 PM To change this template use File | Settings | File Templates. */
public class PortfolioListItemView extends RelativeLayout implements DTOView<DisplayablePortfolioDTO>
{
    public static final String TAG = PortfolioListItemView.class.getName();

    private ImageView userIcon;
    private TextView title;
    private TextView description;

    private DisplayablePortfolioDTO displayablePortfolioDTO;
    @Inject Lazy<Picasso> picasso;

    //<editor-fold desc="Constructors">
    public PortfolioListItemView(Context context)
    {
        super(context);
    }

    public PortfolioListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PortfolioListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
        DaggerUtils.inject(this);
        if (userIcon != null)
        {
            picasso.get().load(R.drawable.superman_facebook)
                    .transform(new RoundedShapeTransformation())
                    .into(userIcon);
        }
    }

    private void initViews()
    {
        userIcon = (ImageView) findViewById(R.id.user_icon);
        title = (TextView) findViewById(R.id.portfolio_title);
        description = (TextView) findViewById(R.id.portfolio_description);
    }

    public DisplayablePortfolioDTO getDisplayablePortfolioDTO()
    {
        return displayablePortfolioDTO;
    }

    public void display(DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        linkWith(displayablePortfolioDTO, true);
    }

    public void linkWith(DisplayablePortfolioDTO displayablePortfolioDTO, boolean andDisplay)
    {
        this.displayablePortfolioDTO = displayablePortfolioDTO;
        if (andDisplay)
        {
            displayUserIcon();
            displayTitle();
            displayDescription();
        }
    }

    //<editor-fold desc="Display Methods">
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
            if (displayablePortfolioDTO != null && displayablePortfolioDTO.userBaseDTO != null)
            {
                picasso.get().load(displayablePortfolioDTO.userBaseDTO.picture)
                             .transform(new RoundedShapeTransformation())
                             .into(userIcon);
            }
        }
    }

    public void displayTitle()
    {
        if (title != null)
        {
            if (displayablePortfolioDTO != null && displayablePortfolioDTO.portfolioDTO != null)
            {
                title.setText(displayablePortfolioDTO.portfolioDTO.title);
            }
        }
    }

    public void displayDescription()
    {
        if (description != null)
        {
            if (displayablePortfolioDTO != null && displayablePortfolioDTO.portfolioDTO != null)
            {
                description.setText(displayablePortfolioDTO.portfolioDTO.description);
            }
        }
    }
    //</editor-fold>
}
