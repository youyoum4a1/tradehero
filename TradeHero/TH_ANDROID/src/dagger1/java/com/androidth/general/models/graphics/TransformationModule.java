package com.androidth.general.models.graphics;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.androidth.general.common.graphics.AbstractSequentialTransformation;
import com.androidth.general.common.graphics.AlphaTransformation;
import com.androidth.general.common.graphics.GradientTransformation;
import com.androidth.general.common.graphics.GrayscaleTransformation;
import com.androidth.general.common.graphics.RoundedCornerShaderTransformation;
import com.androidth.general.common.graphics.RoundedShapeTransformation;
import com.androidth.general.common.graphics.StackBlurTransformation;
import com.androidth.general.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
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
    public Transformation provideUserImageTransformation(RoundedShapeTransformation transformation)
    {
        return transformation;
    }

    @Provides @ForUserPhoto
    public Drawable provideDefaultUserPhoto(Context context, @ForUserPhoto Transformation userImageTransformation)
    {
        return new BitmapDrawable(
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
        transformation.add(new StackBlurTransformation(25));
        transformation.add(new AlphaTransformation(picasso, 0.3f));
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
        transformation.add(new GradientTransformation(
                context.getResources().getColor(R.color.white),
                context.getResources().getColor(R.color.black)));
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
        backgroundTransformation.add(new AlphaTransformation(picasso, 0.2f));
        return backgroundTransformation;
    }

    @Provides @ForExtraTileBackground Transformation provideExtraTileBackgroundTransformation(Context context)
    {
        int rad = context.getResources().getDimensionPixelSize(R.dimen.grid_item_bg_radius);
        return new RoundedCornerShaderTransformation(rad);
    }

    @Provides @ForUserNextLevelBadge Transformation provideUserNextLevelBadgeTransformation(Context context, Picasso picasso)
    {
        return new GrayscaleTransformation(picasso);
    }
}
