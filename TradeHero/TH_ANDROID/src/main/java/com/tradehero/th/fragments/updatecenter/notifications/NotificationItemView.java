package com.tradehero.th.fragments.updatecenter.notifications;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

public class NotificationItemView
        extends LinearLayout
        implements DTOView<NotificationDTO>
{
    @InjectView(R.id.discussion_content) TextView notificationContent;
    @InjectView(R.id.notification_user_picture) ImageView notificationPicture;
    @InjectView(R.id.discussion_time) TextView notificationTime;
    @InjectView(R.id.notification_unread_flag) ImageView notificationUnreadFlag;

    @Inject DashboardNavigator navigator;
    @Inject THRouter thRouter;
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

        ButterKnife.inject(this);
        HierarchyInjector.inject(this);
        prettyTime = new PrettyTime();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        resetView();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.notification_user_picture) void onUserProfileClicked(View ignored)
    {
        Bundle bundle = new Bundle();
        if (notificationDTO != null && notificationDTO.referencedUserId != null)
        {
            UserBaseKey referencedUser = new UserBaseKey(notificationDTO.referencedUserId);
            thRouter.save(bundle, referencedUser);
            if (currentUserId.toUserBaseKey().equals(referencedUser))
            {
                navigator.pushFragment(MeTimelineFragment.class, bundle);
            }
            else
            {
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
        notificationContent.setText(null);
        notificationTime.setText(null);

        resetNotificationProfilePicture();
    }

    private void resetNotificationProfilePicture()
    {
        picasso.cancelRequest(notificationPicture);
        picasso.load(R.drawable.superman_facebook)
                .transform(userPhotoTransformation)
                .into(notificationPicture);
    }
}
