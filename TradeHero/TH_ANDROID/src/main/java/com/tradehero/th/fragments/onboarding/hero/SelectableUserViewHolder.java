package com.tradehero.th.fragments.onboarding.hero;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.models.number.THSignedPercentage;

@Deprecated
class SelectableUserViewHolder implements DTOView<SelectableUserDTO>
{
    @NonNull final Context context;
    @NonNull final Picasso picasso;
    @NonNull final Transformation userImageTransformation;

    @InjectView(R.id.leaderboard_user_item_profile_picture) ImageView profilePictureView;
    @InjectView(R.id.leaderboard_user_item_display_name) protected TextView displayNameView;
    @InjectView(R.id.lbmu_roi) protected TextView roiView;
    @InjectView(R.id.tick_selected) protected View tickSelectedView;

    @Nullable protected SelectableUserDTO selectableDTO;

    //<editor-fold desc="Constructors">
    SelectableUserViewHolder(
            @NonNull Context context,
            @NonNull Picasso picasso,
            @NonNull Transformation userImageTransformation)
    {
        this.context = context;
        this.picasso = picasso;
        this.userImageTransformation = userImageTransformation;
    }
    //</editor-fold>

    public void attachView(View view)
    {
        ButterKnife.inject(this, view);
        display();
    }

    public void detachView()
    {
        picasso.cancelRequest(profilePictureView);
        ButterKnife.reset(this);
    }

    @Override public void display(SelectableUserDTO selectableUserDTO)
    {
        this.selectableDTO = selectableUserDTO;
        display();
    }

    void display()
    {
        displayName();
        displayPicture();
        displayRoi();
        displayTickSelected();
    }

    void displayName()
    {
        if (displayNameView != null)
        {
            displayNameView.setText(getDisplayNameString());
        }
    }

    String getDisplayNameString()
    {
        if (selectableDTO == null)
        {
            return context.getString(R.string.na);
        }
        return UserBaseDTOUtil.getShortDisplayName(context, selectableDTO.value);
    }

    void displayPicture()
    {
        if (profilePictureView != null)
        {
            RequestCreator request;
            if (selectableDTO != null
                    && selectableDTO.value.picture != null
                    && !selectableDTO.value.picture.isEmpty())
            {
                request = picasso.load(selectableDTO.value.picture);
            }
            else
            {
                request = picasso.load(R.drawable.superman_facebook);
            }
            request.transform(userImageTransformation)
                    .placeholder(R.drawable.superman_facebook)
                    .into(profilePictureView);
        }
    }

    void displayRoi()
    {
        if (roiView != null)
        {
            if (selectableDTO == null)
            {
                roiView.setText(R.string.na);
            }
            else
            {
                THSignedPercentage
                        .builder(selectableDTO.value.roiInPeriod * 100)
                        .withSign()
                        .signTypeArrow()
                        .relevantDigitCount(3)
                        .withDefaultColor()
                        .build()
                        .into(roiView);
            }
        }
    }

    void displayTickSelected()
    {
        if (tickSelectedView != null)
        {
            tickSelectedView.setVisibility(selectableDTO != null
                    && selectableDTO.selected ? View.VISIBLE : View.GONE);
        }
    }
}
