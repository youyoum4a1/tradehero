package com.tradehero.th.fragments.social;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: alex Date: 10/14/13 Time: 12:28 PM To change this template use
 * File | Settings | File Templates.
 */
public class RelationsListItemView extends RelativeLayout
        implements DTOView<UserBaseDTO>, View.OnClickListener
{
    public static final String TAG = RelationsListItemView.class.getName();

    private ImageView avatar;
    private TextView title;
    private UserBaseDTO userBaseDTO;

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
        initViews();
        DaggerUtils.inject(this);
    }

    private void initViews()
    {
        avatar = (ImageView) findViewById(R.id.user_profile_avatar);
        title = (TextView) findViewById(R.id.user_name);
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
        //    if (userBaseDTO != null) {
        //        handleUserIconClicked();
        //    }
        //}
    }

    public UserBaseDTO getUserBaseDTO()
    {
        return userBaseDTO;
    }

    public void display(UserBaseDTO userBaseDTO)
    {
        Timber.d("lyl 1 display");
        linkWith(userBaseDTO, true);
    }

    public void linkWith(UserBaseDTO userBaseDTO, boolean andDisplay)
    {
        Timber.d("lyl 2 linkWith %s", userBaseDTO.toString());
        this.userBaseDTO = userBaseDTO;
        if (andDisplay)
        {
            displayPicture();
            displayTitle();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        Timber.d("lyl display");
        displayPicture();
        displayTitle();
    }

    public void displayPicture()
    {
        if (avatar != null)
        {
            loadDefaultPicture();
            if (userBaseDTO != null && userBaseDTO.picture != null)
            {
                picassoLazy.get().load(userBaseDTO.picture)
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
        if (title != null)
        {
            title.setText(userBaseDTO.displayName);
        }
    }
}
