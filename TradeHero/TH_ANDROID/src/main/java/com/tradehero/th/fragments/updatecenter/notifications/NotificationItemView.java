package com.ayondo.academy.fragments.updatecenter.notifications;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.ayondo.academy.R;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.api.notification.NotificationDTO;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.timeline.MeTimelineFragment;
import com.ayondo.academy.fragments.timeline.PushableTimelineFragment;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.models.graphics.ForUserPhoto;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

public class NotificationItemView
        extends LinearLayout
        implements DTOView<NotificationDTO>
{
    @Bind(R.id.discussion_content) TextView notificationContent;
    @Bind(R.id.notification_user_picture) ImageView notificationPicture;
    @Bind(R.id.discussion_time) TextView notificationTime;
    @Bind(R.id.notification_unread_flag) ImageView notificationUnreadFlag;

    @Inject DashboardNavigator navigator;
    @Inject CurrentUserId currentUserId;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;

    PrettyTime prettyTime;
    private NotificationDTO notificationDTO;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public NotificationItemView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public NotificationItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public NotificationItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.bind(this);
        HierarchyInjector.inject(this);
        prettyTime = new PrettyTime();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        resetView();
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.notification_user_picture) void onUserProfileClicked(View ignored)
    {
        Bundle bundle = new Bundle();
        if (notificationDTO != null && notificationDTO.referencedUserId != null)
        {
            UserBaseKey referencedUser = new UserBaseKey(notificationDTO.referencedUserId);
            if (currentUserId.toUserBaseKey().equals(referencedUser))
            {
                navigator.pushFragment(MeTimelineFragment.class, bundle);
            }
            else
            {
                PushableTimelineFragment.putUserBaseKey(bundle, referencedUser);
                navigator.pushFragment(PushableTimelineFragment.class, bundle);
            }
        }
    }

    @Override public void display(NotificationDTO notificationDTO)
    {
        if (notificationDTO != null)
        {
            this.notificationDTO = notificationDTO;
            notificationContent.setText(notificationDTO.text);
            notificationTime.setText(prettyTime.format(notificationDTO.createdAtUtc));
            notificationUnreadFlag.setVisibility(notificationDTO.unread ? View.VISIBLE : View.INVISIBLE);
            if (notificationDTO.imageUrl != null)
            {
                picasso.load(notificationDTO.imageUrl)
                        .transform(userPhotoTransformation)
                        .into(notificationPicture);
            }
            else
            {
                resetNotificationProfilePicture();
            }
        }
        else
        {
            resetView();
        }
    }

    private void resetView()
    {
        if (notificationContent != null)
        {
            notificationContent.setText(null);
        }
        if (notificationTime != null)
        {
            notificationTime.setText(null);
        }

        resetNotificationProfilePicture();
    }

    private void resetNotificationProfilePicture()
    {
        if (notificationPicture != null)
        {
            picasso.cancelRequest(notificationPicture);
            picasso.load(R.drawable.superman_facebook)
                    .transform(userPhotoTransformation)
                    .into(notificationPicture);
        }
    }
}
