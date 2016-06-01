package com.ayondo.academy.fragments.social.friend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.ayondo.academy.R;
import com.ayondo.academy.api.social.UserFriendsDTO;
import com.ayondo.academy.api.social.UserFriendsWeiboDTO;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.models.graphics.ForUserPhoto;
import javax.inject.Inject;
import timber.log.Timber;

public class SocialFriendUserView extends SocialFriendItemView
{
    @Bind(R.id.social_item_logo) ImageView friendLogo;
    @Bind(R.id.social_item_title) TextView friendTitle;
    @Bind(R.id.social_item_action_btn) TextView actionBtn;
    @Bind(R.id.social_friend_item_ll) LinearLayout socialFriendItem;
    @Bind(R.id.social_item_action_cb) CheckBox actionCb;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

    private SocialFriendListItemUserDTO socialFriendListItemUserDTO;
    @Nullable private OnElementClickListener onElementClickListener;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public SocialFriendUserView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public SocialFriendUserView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
        HierarchyInjector.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        picasso.cancelRequest(friendLogo);
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.social_item_action_btn)
    public void onActionButtonClick()
    {
        if (onElementClickListener != null)
        {
            if (socialFriendListItemUserDTO.userFriendsDTO.isTradeHeroUser())
            {
                onElementClickListener.onFollowButtonClick(socialFriendListItemUserDTO.userFriendsDTO);
            }
            else
            {
                onElementClickListener.onInviteButtonClick(socialFriendListItemUserDTO.userFriendsDTO);
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.social_item_action_cb)
    public void onActionCheckBoxClick()
    {
        if (onElementClickListener != null)
        {
            socialFriendListItemUserDTO.isSelected = actionCb.isChecked();
            onElementClickListener.onCheckBoxClick(socialFriendListItemUserDTO.userFriendsDTO);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.social_friend_item_ll)
    public void onActionItemViewClick()
    {
        if (isNeedCheckBoxShow())
        {
            actionCb.performClick();
        }
    }

    public void setOnElementClickedListener(@Nullable OnElementClickListener onElementClickListener)
    {
        this.onElementClickListener = onElementClickListener;
    }

    @Override
    public void display(@NonNull SocialFriendListItemDTO dto)
    {
        if(dto instanceof SocialFriendListItemUserDTO)
        {
            this.socialFriendListItemUserDTO = (SocialFriendListItemUserDTO) dto;
            displayUserIcon();
            displayTitle();
            displayActionButton();
            setItemViewClickable();
        }
    }

    private void setItemViewClickable()
    {
        socialFriendItem.setClickable(isNeedCheckBoxShow());
    }

    private void displayUserIcon()
    {
        displayDefaultUserIcon();
        picasso.load(socialFriendListItemUserDTO.userFriendsDTO.getProfilePictureURL())
                .placeholder(friendLogo.getDrawable())
                .transform(peopleIconTransformation)
                .error(R.drawable.superman_facebook)
                .into(friendLogo, new Callback()
                {
                    @Override public void onSuccess()
                    {
                        Timber.d("windy display User Icon success!");
                    }

                    @Override public void onError()
                    {
                        displayDefaultUserIcon();
                    }
                });
    }

    private void displayDefaultUserIcon()
    {
        picasso.load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(friendLogo);
    }

    private void displayTitle()
    {
        friendTitle.setText(socialFriendListItemUserDTO.userFriendsDTO.name);
    }

    private void displayActionButton()
    {
        int pL = actionBtn.getPaddingLeft();
        int pR = actionBtn.getPaddingRight();
        int pT = actionBtn.getPaddingTop();
        int pB = actionBtn.getPaddingBottom();

        if (socialFriendListItemUserDTO.userFriendsDTO.isTradeHeroUser())
        {
            actionBtn.setText(R.string.follow);
            actionBtn.setBackgroundResource(R.drawable.basic_green_selector);
            actionBtn.setEnabled(true);
        }
        else
        {
            actionBtn.setText(R.string.invite);
            actionBtn.setBackgroundResource(R.drawable.basic_blue_selector);
            actionBtn.setEnabled(!socialFriendListItemUserDTO.userFriendsDTO.alreadyInvited);
            setWeiboCheckBox();
        }
        actionBtn.setPadding(pL, pT, pR, pB);
    }

    private boolean isNeedCheckBoxShow()
    {
        return socialFriendListItemUserDTO.userFriendsDTO instanceof UserFriendsWeiboDTO
                && (!socialFriendListItemUserDTO.userFriendsDTO.isTradeHeroUser());
    }

    private void setWeiboCheckBox()
    {
        actionCb.setChecked(socialFriendListItemUserDTO.isSelected);

        // TODO change to be another test
        if (isNeedCheckBoxShow())
        {
            actionBtn.setVisibility(View.GONE);
            actionCb.setVisibility(View.VISIBLE);
        }
        else
        {
            actionBtn.setVisibility(View.VISIBLE);
            actionCb.setVisibility(View.GONE);
        }
    }

    public static interface OnElementClickListener
    {
        void onFollowButtonClick(@NonNull UserFriendsDTO userFriendsDTO);
        void onInviteButtonClick(@NonNull UserFriendsDTO userFriendsDTO);
        void onCheckBoxClick(@NonNull UserFriendsDTO userFriendsDTO);
    }
}
