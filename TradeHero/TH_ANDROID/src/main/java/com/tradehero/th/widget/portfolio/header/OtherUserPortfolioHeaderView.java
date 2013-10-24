package com.tradehero.th.widget.portfolio.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by julien on 21/10/13
 */
public class OtherUserPortfolioHeaderView extends RelativeLayout implements PortfolioHeaderView
{
    private ImageView userImageView;
    private TextView usernameTextView;
    private ImageView followingImageView;
    private ImageButton followButton;

    @Inject @Named("CurrentUser") protected UserBaseDTO currentUserBase;
    @Inject Lazy<UserProfileCache> userCache;
    @Inject Lazy<Picasso> picasso;

    //<editor-fold desc="Description">
    public OtherUserPortfolioHeaderView(Context context)
    {
        super(context);
    }

    public OtherUserPortfolioHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public OtherUserPortfolioHeaderView(Context context, AttributeSet attrs, int defStyle)
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
        userImageView = (ImageView)findViewById(R.id.portfolio_header_avatar);
        usernameTextView = (TextView) findViewById(R.id.header_portfolio_username);
        followingImageView = (ImageView) findViewById(R.id.header_portfolio_following_image);
        followButton = (ImageButton) findViewById(R.id.header_portfolio_follow_button);
        followButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                THToast.show("Nope");
            }
        });
    }


    @Override public void bindOwnedPortfolioId(OwnedPortfolioId id)
    {
        UserProfileDTO user =  this.userCache.get().get(id.getUserBaseKey());
        configureUserViews(user);
        configureFollowItemsVisibility(user);
    }

    private void configureUserViews(UserProfileDTO user)
    {
        if (this.usernameTextView != null)
        {
            this.usernameTextView.setText(user.displayName);
        }

        if (this.userImageView != null)
        {
            picasso.get().load(user.picture)
                    .transform(new RoundedShapeTransformation())
                    .into(this.userImageView);
        }
    }

    private void configureFollowItemsVisibility(UserProfileDTO user)
    {
        UserProfileDTO currentUser = this.userCache.get().get(currentUserBase.getBaseKey());
        if (currentUser.isFollowingUser(user.id))
        {
            this.followingImageView.setVisibility(VISIBLE);
            this.followButton.setVisibility(GONE);
        }
        else
        {
            this.followingImageView.setVisibility(GONE);
            this.followButton.setVisibility(VISIBLE);
        }
    }
}
