package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.graphics.ForUserPhoto;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class MessageItemView extends LinearLayout
        implements DTOView<MessageHeaderDTO>
{
    @Inject Picasso picasso;
    @Inject PrettyTime prettyTime;
    @Inject @ForUserPhoto Transformation userPhotoTransformation;

    @InjectView(R.id.message_item_icon) ImageView mMessageIcon;
    @InjectView(R.id.message_item_title) TextView mMessageTitle;
    @InjectView(R.id.message_item_sub_title) TextView mMessageSubTitle;
    @InjectView(R.id.message_item_date) TextView mMessageDate;
    @InjectView(R.id.message_item_content) TextView mMessageContent;
    @InjectView(R.id.message_unread_flag) View mUnreadFlag;

    private MessageHeaderDTO messageHeaderDTO;
    @NonNull private BehaviorSubject<UserAction> userActionBehavior;

    //<editor-fold desc="Constructors">
    public MessageItemView(Context context)
    {
        super(context);
        userActionBehavior = BehaviorSubject.create();
    }

    public MessageItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        userActionBehavior = BehaviorSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        resetMessageIcon();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @NonNull public Observable<UserAction> getUserActionObservable()
    {
        return userActionBehavior.asObservable();
    }

    @Override public void display(MessageHeaderDTO dto)
    {
        this.messageHeaderDTO = dto;
        if (messageHeaderDTO != null)
        {
            setBackgroundColor(getResources().getColor(R.color.white));
        }
        display();
    }

    private void display()
    {
        if (messageHeaderDTO != null)
        {
            mMessageTitle.setText(messageHeaderDTO.title);
            mMessageSubTitle.setText(messageHeaderDTO.subTitle);
            mMessageContent.setText(messageHeaderDTO.latestMessage);
            mMessageDate.setText(prettyTime.format(messageHeaderDTO.latestMessageAtUtc));
            mUnreadFlag.setVisibility(messageHeaderDTO.unread ? View.VISIBLE : View.GONE);

            resetMessageIcon();
            if (messageHeaderDTO.imageUrl != null && mMessageIcon != null)
            {
                picasso.load(messageHeaderDTO.imageUrl)
                        .transform(userPhotoTransformation)
                        .into(mMessageIcon, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                            }

                            @Override public void onError()
                            {
                                resetMessageIcon();
                            }
                        });
            }
        }
    }

    private void resetMessageIcon()
    {
        ImageView iconViewCopy = mMessageIcon;
        if (iconViewCopy != null)
        {
            picasso.cancelRequest(iconViewCopy);
            picasso.load(R.drawable.superman_facebook)
                    .transform(userPhotoTransformation)
                    .into(iconViewCopy);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick({R.id.message_item_icon, R.id.message_item_title})
    protected void handleUserClicked()
    {
        userActionBehavior.onNext(new UserActionUserClicked(messageHeaderDTO));
    }

    abstract public static class UserAction
    {
        @NonNull public MessageHeaderDTO messageHeaderDTO;

        //<editor-fold desc="Constructors">
        public UserAction(@NonNull MessageHeaderDTO messageHeaderDTO)
        {
            this.messageHeaderDTO = messageHeaderDTO;
        }
        //</editor-fold>
    }

    public static class UserActionUserClicked extends UserAction
    {
        //<editor-fold desc="Constructors">
        public UserActionUserClicked(@NonNull MessageHeaderDTO messageHeaderDTO)
        {
            super(messageHeaderDTO);
        }
        //</editor-fold>
    }
}
