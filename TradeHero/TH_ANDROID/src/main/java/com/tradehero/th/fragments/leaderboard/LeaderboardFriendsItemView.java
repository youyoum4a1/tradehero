package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class LeaderboardFriendsItemView extends RelativeLayout
        implements DTOView<UserFriendsDTO>
{
    @Inject Picasso picasso;

    @InjectView(R.id.leaderboard_user_item_network_label) ImageView networkLabel;
    @InjectView(R.id.leaderboard_user_item_profile_picture) ImageView avatar;
    @InjectView(R.id.leaderboard_user_item_social_name) TextView socialName;

    @Nullable private UserFriendsDTO userFriendsDTO;
    @NonNull private final PublishSubject<UserAction> userActionSubject;

    //<editor-fold desc="Constructors">
    public LeaderboardFriendsItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        userActionSubject = PublishSubject.create();
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @NonNull public Observable<UserAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    @Override public void display(@NonNull UserFriendsDTO dto)
    {
        userFriendsDTO = dto;

        if (networkLabel != null)
        {
            networkLabel.setBackgroundResource(dto.getNetworkLabelImage());
        }

        if (avatar != null)
        {
            String url = dto.getProfilePictureURL();
            if (url != null)
            {
                picasso.load(url)
                        .placeholder(R.drawable.superman_facebook)
                        .into(avatar);
            }
        }

        if (socialName != null)
        {
            socialName.setText(dto.name);
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.leaderboard_user_item_invite_btn)
    void invite(View view)
    {
        if (userFriendsDTO != null)
        {
            userActionSubject.onNext(new UserAction(userFriendsDTO));
        }
    }

    public static class UserAction
    {
        @NonNull public final UserFriendsDTO userFriendsDTO;

        public UserAction(@NonNull UserFriendsDTO userFriendsDTO)
        {
            this.userFriendsDTO = userFriendsDTO;
        }
    }
}
