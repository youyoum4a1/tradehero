package com.tradehero.th.fragments.social;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

public class RelationsListItemView extends RelativeLayout
        implements DTOView<AllowableRecipientDTO>, View.OnClickListener
{
    public static final String TAG = RelationsListItemView.class.getName();

    @InjectView(R.id.user_name) TextView name;
    @InjectView(R.id.user_profile_avatar) ImageView avatar;
    @InjectView(R.id.country_logo) ImageView countryLogo;
    @InjectView(R.id.user_type) TextView userType;
    @InjectView(R.id.upgrade_now) TextView upgradeNow;
    @InjectView(R.id.user_message_left) TextView messageLeft;
    private AllowableRecipientDTO allowableRecipientDTO;

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
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
        initViews();
    }

    private void initViews()
    {

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

    public AllowableRecipientDTO getAllowableRecipientDTO()
    {
        return allowableRecipientDTO;
    }

    public void linkWith(AllowableRecipientDTO allowableRecipientDTO, boolean andDisplay)
    {
        this.allowableRecipientDTO = allowableRecipientDTO;
        //Timber.d("lyl relationship=%s", allowableRecipientDTO.relationship.toString());
        if (andDisplay)
        {
            displayPicture();
            displayTitle();
            updateUserType();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayPicture();
        displayTitle();
        updateUserType();
    }

    public void displayPicture()
    {
        if (avatar != null)
        {
            loadDefaultPicture();
            if (allowableRecipientDTO != null && allowableRecipientDTO.user.picture != null)
            {
                picassoLazy.get().load(allowableRecipientDTO.user.picture)
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
        if (name != null)
        {
            name.setText(allowableRecipientDTO.user.displayName);
        }
        if (messageLeft != null)
        {
            int count = allowableRecipientDTO.relationship.freeSendsRemaining;
            if (count > 0)
            {
                messageLeft.setText(getContext().getString(R.string.free_message_left, count));
            }
            else
            {
                messageLeft.setText(getContext().getString(R.string.upgrade_to_message_more));

            }
            messageLeft.setVisibility(allowableRecipientDTO.relationship.isFriend ? INVISIBLE : VISIBLE);
            messageLeft.setVisibility(allowableRecipientDTO.relationship.isFollower ? INVISIBLE : VISIBLE);
        }
        if (upgradeNow != null)
        {
            upgradeNow.setVisibility(allowableRecipientDTO.relationship.isHero && allowableRecipientDTO.relationship.freeFollow ? VISIBLE : INVISIBLE);
        }

    }

    public void updateUserType()
    {
        if (userType != null)
        {
            if (allowableRecipientDTO.relationship.isFollower)
            {
                userType.setText(getContext().getString(
                        R.string.user_profile_count_followers));
            }
            else
            {
                userType.setText(getContext().getString(R.string.user_profile_count_heroes));
            }
            if (allowableRecipientDTO.relationship.freeFollow)
            {
                userType.setText(userType.getText() + "(" + getContext().getString(
                        R.string.not_follow_subtitle2) + ")");
            }
            else
            {
                userType.setText(userType.getText() + "(" + getContext().getString(
                        R.string.not_follow_premium_subtitle2) + ")");
            }
        }
    }

    @Override public void display(AllowableRecipientDTO allowableRecipientDTO)
    {
        linkWith(allowableRecipientDTO, true);
    }
}
