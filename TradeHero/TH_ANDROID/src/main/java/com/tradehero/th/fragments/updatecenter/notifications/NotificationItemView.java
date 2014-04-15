package com.tradehero.th.fragments.updatecenter.notifications;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.notification.NotificationCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by thonguyen on 3/4/14.
 */
public class NotificationItemView
        extends LinearLayout
        implements DTOView<NotificationKey>
{
    @InjectView(R.id.discussion_content) TextView notificationContent;
    @InjectView(R.id.notification_user_picture) ImageView notificationPicture;
    @InjectView(R.id.discussion_time) TextView notificationTime;

    @Inject NotificationCache notificationCache;
    @Inject PrettyTime prettyTime;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;

    private NotificationKey notificationKey;
    private NotificationDTO notificationDTO;

    private DTOCache.Listener<NotificationKey, NotificationDTO> notificationFetchListener;
    private DTOCache.GetOrFetchTask<NotificationKey, NotificationDTO> notificationFetchTask;

    //<editor-fold desc="Constructors">
    public NotificationItemView(Context context)
    {
        super(context);
    }

    public NotificationItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NotificationItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);

        notificationFetchListener = new NotificationFetchListener();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        notificationFetchListener = new NotificationFetchListener();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachNotificationFetchTask();

        resetView();

        notificationFetchListener = null;
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(NotificationKey notificationKey)
    {
        this.notificationKey = notificationKey;

        fetchNotification();
    }

    private void display(NotificationDTO notificationDTO)
    {
        if (notificationDTO != null)
        {
            notificationContent.setText(notificationDTO.text);
            notificationTime.setText(prettyTime.format(notificationDTO.createdAtUtc));

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

    private void fetchNotification()
    {
        detachNotificationFetchTask();

        notificationFetchTask = notificationCache.getOrFetch(notificationKey, false, notificationFetchListener);
        notificationFetchTask.execute();
    }

    private void detachNotificationFetchTask()
    {
        if (notificationFetchTask != null)
        {
            notificationFetchTask.setListener(null);
        }
        notificationFetchTask = null;
    }


    private class NotificationFetchListener implements DTOCache.Listener<NotificationKey,NotificationDTO>
    {
        @Override public void onDTOReceived(NotificationKey key, NotificationDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(NotificationKey key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }

    private void linkWith(NotificationDTO notificationDTO, boolean andDisplay)
    {
        this.notificationDTO = notificationDTO;

        if (andDisplay)
        {
            display(notificationDTO);
        }
    }
}
