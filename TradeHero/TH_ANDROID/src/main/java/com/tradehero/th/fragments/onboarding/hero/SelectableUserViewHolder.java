package com.tradehero.th.fragments.onboarding.hero;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class SelectableUserViewHolder implements DTOView<SelectableUserDTO>
{
    @NotNull final Context context;
    @NotNull final UserBaseDTOUtil userBaseDTOUtil;
    @NotNull final Picasso picasso;
    @NotNull final Transformation userImageTransformation;

    @InjectView(R.id.leaderboard_user_item_profile_picture) ImageView profilePictureView;
    @InjectView(R.id.leaderboard_user_item_display_name) protected TextView displayNameView;
    @InjectView(R.id.lbmu_roi) protected TextView roiView;
    @InjectView(R.id.tick_selected) protected View tickSelectedView;

    @Nullable protected SelectableUserDTO selectableDTO;

    //<editor-fold desc="Constructors">
    SelectableUserViewHolder(
            @NotNull Context context,
            @NotNull UserBaseDTOUtil userBaseDTOUtil,
            @NotNull Picasso picasso,
            @NotNull Transformation userImageTransformation)
    {
        this.context = context;
        this.userBaseDTOUtil = userBaseDTOUtil;
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
        return userBaseDTOUtil.getShortDisplayName(context, selectableDTO.value);
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
                THSignedNumber roi = THSignedPercentage
                        .builder(selectableDTO.value.roiInPeriod * 100)
                        .withSign()
                        .signTypeArrow()
                        .relevantDigitCount(3)
                        .build();
                roiView.setText(roi.toString());
                roiView.setTextColor(context.getResources().getColor(roi.getColorResId()));
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
