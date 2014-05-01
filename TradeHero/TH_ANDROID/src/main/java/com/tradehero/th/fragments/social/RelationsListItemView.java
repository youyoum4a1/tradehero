package com.tradehero.th.fragments.social;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

public class RelationsListItemView extends RelativeLayout
        implements DTOView<UserProfileCompactDTO>, View.OnClickListener
{
    public static final String TAG = RelationsListItemView.class.getName();

    private ImageView avatar;
    private TextView title;
    private UserProfileCompactDTO userProfileCompactDTO;

    @Inject protected Lazy<Picasso> picassoLazy;
    @Inject @ForUserPhoto protected Lazy<Transformation> peopleIconTransformationLazy;

    //<editor-fold desc="Constructors">
    public RelationsListItemView(Context context)
    {
        super(context);
    }

    public RelationsListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RelationsListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
        DaggerUtils.inject(this);
    }

    private void initViews()
    {
        avatar = (ImageView) findViewById(R.id.user_profile_avatar);
        title = (TextView) findViewById(R.id.user_name);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    @Override public void onClick(View v)
    {
        //if (v.getId() == R.id.follower_profile_picture)
        //{
        //    if (userProfileCompactDTO != null) {
        //        handleUserIconClicked();
        //    }
        //}
    }

    public UserProfileCompactDTO getUserProfileCompactDTO()
    {
        return userProfileCompactDTO;
    }

    public void display(UserProfileCompactDTO userProfileCompactDTO)
    {
        linkWith(userProfileCompactDTO, true);
    }

    public void linkWith(UserProfileCompactDTO userProfileCompactDTO, boolean andDisplay)
    {
        this.userProfileCompactDTO = userProfileCompactDTO;
        if (andDisplay)
        {
            displayPicture();
            displayTitle();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayPicture();
        displayTitle();
    }

    public void displayPicture()
    {
        if (avatar != null)
        {
            loadDefaultPicture();
            if (userProfileCompactDTO != null && userProfileCompactDTO.picture != null)
            {
                picassoLazy.get().load(userProfileCompactDTO.picture)
                        .transform(peopleIconTransformationLazy.get())
                        .placeholder(avatar.getDrawable())
                        .into(avatar);
            }
        }
    }

    protected void loadDefaultPicture()
    {
        if (avatar != null)
        {
            picassoLazy.get().load(R.drawable.superman_facebook)
                    .transform(peopleIconTransformationLazy.get())
                    .into(avatar);
        }
    }

    public void displayTitle()
    {
        if (title != null)
        {
            title.setText(userProfileCompactDTO.displayName);
        }
    }
}
