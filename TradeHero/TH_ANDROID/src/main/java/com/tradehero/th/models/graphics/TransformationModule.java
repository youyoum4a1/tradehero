package com.tradehero.th.models.graphics;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.squareup.picasso.Transformation;
import com.tradehero.common.graphics.AbstractSequentialTransformation;
import com.tradehero.common.graphics.FastBlurTransformation;
import com.tradehero.common.graphics.GaussianTransformation;
import com.tradehero.common.graphics.GradientTransformation;
import com.tradehero.common.graphics.GrayscaleTransformation;
import com.tradehero.common.graphics.RoundedCornerTransformation;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import dagger.Module;
import dagger.Provides;

/**
 * Created by xavier on 1/30/14.
 */
@Module(
        staticInjections =
                {
                },
        injects =
                {
                },
        complete = false,
        library = true
)
public class TransformationModule
{
    public static final String TAG = TransformationModule.class.getSimpleName();

    public TransformationModule()
    {
        super();
    }

    @Provides @ForUserPhoto
    public Transformation provideUserImageTransformation()
    {
        return new RoundedShapeTransformation();
    }

    @Provides @ForUserPhoto
    public Drawable provideDefaultUserPhoto(Context context, @ForUserPhoto Transformation userImageTransformation)
    {
        return new BitmapDrawable (
                context.getResources(),
                userImageTransformation.transform(
                        BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.superman_facebook)));
    }

    @Provides @ForUserPhotoBackground
    public Transformation provideUserImageBackgroundTransformation(Context context)
    {
        AbstractSequentialTransformation transformation = new AbstractSequentialTransformation()
        {
            @Override public String key()
            {
                return "toGaussianGrayscale11";
            }
        };
        transformation.add(new GrayscaleTransformation());
        transformation.add(new FastBlurTransformation(30));
        transformation.add(new GradientTransformation(
                context.getResources().getColor(R.color.profile_view_gradient_top),
                context.getResources().getColor(R.color.profile_view_gradient_bottom)));
        return transformation;
    }

    @Provides @ForSecurityItemBackground
    public Transformation provideSecurityItemBackgroundTransformation(Context context)
    {
        AbstractSequentialTransformation transformation = new AbstractSequentialTransformation()
        {
            @Override public String key()
            {
                return "toFastBlurGrayScale";
            }
        };
        transformation.add(new GrayscaleTransformation());
        transformation.add(new FastBlurTransformation(10));
        transformation.add(new RoundedCornerTransformation(
                context.getResources().getDimensionPixelSize(R.dimen.trending_grid_item_corner_radius),
                context.getResources().getColor(R.color.black)));
        return transformation;
    }

    @Provides @ForSecurityItemForeground
    public Transformation provideSecurityItemForegroundTransformation()
    {
        return new WhiteToTransparentTransformation();
    }

    @Provides @ForSearchPeopleItemBackground
    public Transformation provideSearchPeopleItemBackgroundTransformation(Context context)
    {
        AbstractSequentialTransformation backgroundTransformation = new AbstractSequentialTransformation()
        {
            @Override public String key()
            {
                return "toRoundedGaussianGrayscale2";
            }
        };
        backgroundTransformation.add(new GrayscaleTransformation());
        backgroundTransformation.add(new GaussianTransformation());
        backgroundTransformation.add(new RoundedCornerTransformation(
                context.getResources().getDimensionPixelSize(R.dimen.trending_grid_item_corner_radius),
                context.getResources().getColor(R.color.black)));
        return backgroundTransformation;
    }

    @Provides @ForExtraTileBackground Transformation provideExtraTileBackgroundTransformation(Context context)
    {
        return new RoundedCornerTransformation(
                context.getResources().getDimensionPixelSize(R.dimen.trending_grid_item_corner_radius),
                context.getResources().getColor(R.color.black));
    }
}
