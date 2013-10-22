package com.tradehero.th.widget.portfolio;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 12:28 PM To change this template use File | Settings | File Templates. */
public class PortfolioListItemView extends RelativeLayout
    implements DTOView<OwnedPortfolioId>
{
    public static final String TAG = PortfolioListItemView.class.getName();

    private ImageView userIcon;
    private TextView title;
    private TextView description;

    private OwnedPortfolioId ownedPortfolioId;
    private UserBaseDTO userBaseDTO;
    private PortfolioCompactDTO portfolioCompactDTO;
    private PortfolioDTO portfolioDTO;

    @Inject Lazy<PortfolioCompactCache> portfolioCompactCache;
    @Inject Lazy<PortfolioCache> portfolioCache;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<Picasso> picasso;

    private PortfolioCache.Listener<OwnedPortfolioId, PortfolioDTO> portfolioListener;
    private AsyncTask<Void, Void, PortfolioDTO> fetchPortfolioTask;
    private UserProfileCache.Listener<UserBaseKey, UserProfileDTO> userListener;
    private AsyncTask<Void, Void, UserProfileDTO> fetchUserProfileTask;

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
        picasso.get().load(R.drawable.superman_facebook)
                .transform(new RoundedShapeTransformation())
                .into(userIcon);
    }

    private void initViews()
    {
        userIcon = (ImageView) findViewById(R.id.user_icon);
        title = (TextView) findViewById(R.id.portfolio_title);
        description = (TextView) findViewById(R.id.portfolio_description);
    }

    @Override protected void onDetachedFromWindow()
    {
        portfolioListener = null;
        if (fetchPortfolioTask != null)
        {
            fetchPortfolioTask.cancel(false);
        }
        fetchPortfolioTask = null;
        userListener = null;
        if (fetchUserProfileTask != null)
        {
            fetchUserProfileTask.cancel(false);
        }
        fetchUserProfileTask = null;
        super.onDetachedFromWindow();
    }

    public OwnedPortfolioId getOwnedPortfolioId()
    {
        return ownedPortfolioId;
    }

    public void display(OwnedPortfolioId ownedPortfolioId)
    {
        linkWith(ownedPortfolioId, true);
    }

    public void linkWith(OwnedPortfolioId ownedPortfolioId, boolean andDisplay)
    {
        this.ownedPortfolioId = ownedPortfolioId;
        if (ownedPortfolioId != null)
        {
            linkWith(portfolioCompactCache.get().get(ownedPortfolioId.getPortfolioId()), false);

            portfolioListener = createPortfolioListener();
            fetchPortfolioTask = portfolioCache.get().getOrFetch(ownedPortfolioId, false, portfolioListener);
            fetchPortfolioTask.execute();

            userListener = createUserProfileListener();
            fetchUserProfileTask = userProfileCache.get().getOrFetch(ownedPortfolioId.getUserBaseKey(), false, userListener);
            fetchUserProfileTask.execute();
        }

        if (andDisplay)
        {
            display();
        }
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
                picasso.get().load(userBaseDTO.picture)
                             .transform(new RoundedShapeTransformation())
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

    private PortfolioCache.Listener<OwnedPortfolioId, PortfolioDTO> createPortfolioListener()
    {
        return new PortfolioCache.Listener<OwnedPortfolioId, PortfolioDTO>()
        {
            @Override public void onDTOReceived(OwnedPortfolioId key, PortfolioDTO value)
            {
                if (key.equals(ownedPortfolioId))
                {
                    linkWith(value, true);
                }
            }
        };
    }

    private UserProfileCache.Listener<UserBaseKey, UserProfileDTO> createUserProfileListener()
    {
        return new UserProfileCache.Listener<UserBaseKey, UserProfileDTO>()
        {
            @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
            {
                if (key.equals(ownedPortfolioId.getUserBaseKey()))
                {
                    linkWith(value, true);
                }
            }
        };
    }
}
