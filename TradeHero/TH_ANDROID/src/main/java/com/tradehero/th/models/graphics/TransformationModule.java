package com.tradehero.th.models.graphics;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.graphics.AbstractSequentialTransformation;
import com.tradehero.common.graphics.AlphaTransformation;
import com.tradehero.common.graphics.GradientTransformation;
import com.tradehero.common.graphics.GrayscaleTransformation;
import com.tradehero.common.graphics.RecyclerTransformation;
import com.tradehero.common.graphics.RoundedCornerShaderTransformation;
import com.tradehero.common.graphics.RoundedShapeTransformation;
import com.tradehero.common.graphics.StackBlurTransformation;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

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
    public TransformationModule()
    {
        super();
    }

    @Provides @ForUserPhoto
    public RecyclerTransformation provideUserImageRecyclerTransformation(RoundedShapeTransformation roundedShapeTransformation)
    {
        return roundedShapeTransformation;
    }

    @Provides @ForUserPhoto
    public Transformation provideUserImageTransformation(@ForUserPhoto RecyclerTransformation transformation)
    {
        return transformation;
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
    public Transformation provideUserImageBackgroundTransformation(Context context, Picasso picasso)
    {
        AbstractSequentialTransformation transformation = new AbstractSequentialTransformation()
        {
            @Override public String key()
            {
                return "toGaussianGrayscale11";
            }
        };
        transformation.add(new GrayscaleTransformation(picasso));
		transformation.add(new StackBlurTransformation(30));
        transformation.add(new GradientTransformation(
                context.getResources().getColor(R.color.profile_view_gradient_top),
                context.getResources().getColor(R.color.black)));
        return transformation;
    }

    @Provides @ForSecurityItemBackground @Singleton
    public Transformation provideSecurityItemBackgroundTransformation(Context context, Picasso picasso)
    {
        AbstractSequentialTransformation transformation = new AbstractSequentialTransformation()
        {
            @Override public String key()
            {
                return "toFastBlurGrayScale";
            }
        };
        transformation.add(new GrayscaleTransformation(picasso));
		transformation.add(new StackBlurTransformation(10));
        return transformation;
    }

    @Provides @ForSecurityItemBackground2 @Singleton
    public Transformation provideSecurityItemBackgroundTransformation2(Context context,Picasso picasso)
    {
        AbstractSequentialTransformation transformation = new AbstractSequentialTransformation()
        {
            @Override public String key()
            {
                return "toFastBlurGrayScale";
            }
        };
        transformation.add(new GrayscaleTransformation(picasso));
		transformation.add(new StackBlurTransformation(10));
        transformation.add(new AlphaTransformation(0.2f));
        return transformation;
    }

    @Provides @ForSecurityItemForeground @Singleton
    public Transformation provideSecurityItemForegroundTransformation()
    {
        return new WhiteToTransparentTransformation();
    }

    @Provides @ForSearchPeopleItemBackground
    public Transformation provideSearchPeopleItemBackgroundTransformation(Context context, Picasso picasso)
    {

        AbstractSequentialTransformation backgroundTransformation = new AbstractSequentialTransformation()
        {
            @Override public String key()
            {
                return "toRoundedGaussianGrayscale2";
            }
        };
        backgroundTransformation.add(new GrayscaleTransformation(picasso));
        backgroundTransformation.add(new StackBlurTransformation(10));
		backgroundTransformation.add(new AlphaTransformation(0.2f));
        return backgroundTransformation;
    }

    @Provides @ForExtraTileBackground Transformation provideExtraTileBackgroundTransformation(Context context)
    {
		int rad = context.getResources().getDimensionPixelSize(R.dimen.grid_item_bg_radius);
        return new RoundedCornerShaderTransformation(rad);
    }
}
