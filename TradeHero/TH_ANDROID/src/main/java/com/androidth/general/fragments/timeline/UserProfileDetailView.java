package com.androidth.general.fragments.timeline;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.level.LevelDefDTOList;
import com.androidth.general.api.users.UserProfileDTO;
import rx.Observable;

public class UserProfileDetailView extends LinearLayout implements DTOView<UserProfileDTO>
{
    @NonNull protected final UserProfileDetailViewHolder userProfileDetailViewHolder;

    //<editor-fold desc="Constructors">
    public UserProfileDetailView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        userProfileDetailViewHolder = new UserProfileDetailViewHolder(context);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        if (!isInEditMode())
        {
            ButterKnife.bind(userProfileDetailViewHolder, this);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (!isInEditMode())
        {
            ButterKnife.bind(userProfileDetailViewHolder, this);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.unbind(userProfileDetailViewHolder);
        super.onDetachedFromWindow();
    }

    @Override public void display(UserProfileDTO dto)
    {
        userProfileDetailViewHolder.display(dto);
    }

    public void setLevelDef(@NonNull LevelDefDTOList levelDefDTOList)
    {
        userProfileDetailViewHolder.setLevelDef(levelDefDTOList);
    }

    @NonNull public Observable<UserProfileCompactViewHolder.ButtonType> getButtonClickedObservable()
    {
        return userProfileDetailViewHolder.getButtonClickedObservable();
    }
}
