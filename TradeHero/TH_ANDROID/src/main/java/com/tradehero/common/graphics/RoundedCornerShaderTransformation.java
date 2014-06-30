package com.tradehero.common.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import com.squareup.picasso.Transformation;

// enables hardware accelerated rounded corners
// original idea here : http://www.curious-creature.org/2012/12/11/android-recipe-1-image-with-rounded-corners/
public class RoundedCornerShaderTransformation
	implements Transformation
{
	// radius is corner radii in dp
	private final int radius;

	public RoundedCornerShaderTransformation(final int radius)
	{
		this.radius = radius;
	}

	@Override
	public Bitmap transform(final Bitmap source)
	{
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

		Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		canvas.drawRoundRect(new RectF(0.0f, 0.0f, source.getWidth(), source.getHeight()), radius, radius, paint);

		if (source != output)
		{
			source.recycle();
		}

		return output;
	}

	@Override
	public String key()
	{
		return "RoundedCornerShaderTransformation";
	}
}
