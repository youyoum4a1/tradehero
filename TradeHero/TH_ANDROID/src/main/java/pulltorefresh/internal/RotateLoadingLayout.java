/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package pulltorefresh.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.tradehero.th.R;
import pulltorefresh.PullToRefreshBase.Mode;
import pulltorefresh.PullToRefreshBase.Orientation;


public class RotateLoadingLayout extends LoadingLayout {

    private Animation animationA;

    private Animation animationB;
    private Context context;
    private int loading_width = 0;
    private int loading_height = 0;

    public RotateLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);
        this.context = context;
        loading_width = (int)context.getResources().getDimension(R.dimen.pulltorefresh_loading_width);
        loading_height = (int)context.getResources().getDimension(R.dimen.pulltorefresh_loading_height);
        animationA = AnimationUtils.loadAnimation(context, R.anim.pull_to_refresh_loading_a);
        animationB = AnimationUtils.loadAnimation(context, R.anim.pull_to_refresh_loading_b);
        animationA.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHeaderImage.startAnimation(animationB);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationB.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mHeaderImage.startAnimation(animationA);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
	}

	public void onLoadingDrawableSet(Drawable imageDrawable) {
	}

	protected void onPullImpl(float scaleOfLayout) {
        //scaleOfLayout 0.0 - 1.0
        if(scaleOfLayout < 0.1f){
            scaleOfLayout = 0.1f;
        }
        if(scaleOfLayout >1.0f){
            scaleOfLayout = 1.0f;
        }
        float width = loading_width * scaleOfLayout;
        float height = loading_height * scaleOfLayout;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)width, (int)height);
        mHeaderImage.setLayoutParams(params);
	}

	@Override
	protected void refreshingImpl() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(loading_width, loading_height);
        mHeaderImage.setLayoutParams(params);
		mHeaderImage.startAnimation(animationA);
	}

	@Override
	protected void resetImpl() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(loading_width, loading_height);
        mHeaderImage.setLayoutParams(params);
		mHeaderImage.clearAnimation();
	}

	@Override
	protected void pullToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected void releaseToRefreshImpl() {
		// NO-OP
	}

	@Override
	protected int getDefaultDrawableResId() {
		return R.drawable.logo;
	}

}
