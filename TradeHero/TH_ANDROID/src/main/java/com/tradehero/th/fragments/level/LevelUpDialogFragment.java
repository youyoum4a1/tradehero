package com.tradehero.th.fragments.level;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.api.level.key.LevelDefId;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.persistence.level.LevelDefCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class LevelUpDialogFragment extends BaseDialogFragment
{
    private static final String BUNDLE_KEY_CURRENT = LevelUpDialogFragment.class.getName() + ".current";
    private static final String BUNDLE_KEY_NEXT = LevelUpDialogFragment.class.getName() + ".next";

    private static final float DIM_AMOUNT = 0.8f;

    @InjectView(R.id.user_level_next_badge) ImageView nextBadge;
    @InjectView(R.id.user_level_current_badge) ImageView currentBadge;
    @InjectView(R.id.user_level_up_description) TextView levelUpDescription;
    @InjectView(R.id.user_level_up_main_container) ViewGroup container;

    @Inject Picasso picasso;
    @Inject LevelDefCache levelDefCache;

    private LevelDefId mCurrentLevelDefId;
    private LevelDefId mNextLevelDefId;

    private DTOCacheNew.Listener<LevelDefId, LevelDefDTO> levelDefDTOListener;

    private LevelDefDTO mCurrentLevelDefDTO;
    private LevelDefDTO mNextLevelDefDTO;
    private AnimatorSet animatorSet;

    public static LevelUpDialogFragment newInstance(@NotNull LevelDefId fromLevelId, @NotNull LevelDefId toLevelId)
    {
        Bundle b = new Bundle();
        b.putBundle(BUNDLE_KEY_CURRENT, fromLevelId.getArgs());
        b.putBundle(BUNDLE_KEY_NEXT, toLevelId.getArgs());

        LevelUpDialogFragment dialog = new LevelUpDialogFragment();
        dialog.setArguments(b);
        return dialog;
    }

    protected static LevelDefId getLevelId(Bundle bundle, String key)
    {
        return new LevelDefId(bundle.getBundle(key));
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog d = super.onCreateDialog(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TH_Dialog);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        d.getWindow().setDimAmount(DIM_AMOUNT);
        d.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                //Handle backPressed an end the animation if it's running.
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    boolean handled = handleDismissingDialog();
                    if(handled)
                    {
                        return true;
                    }
                }
                return false;
            }
        });
        return d;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.level_up_dialog, container, false);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mCurrentLevelDefId = getLevelId(getArguments(), BUNDLE_KEY_CURRENT);
        mNextLevelDefId = getLevelId(getArguments(), BUNDLE_KEY_NEXT);

        levelDefDTOListener = new LevelDefDTOListener();

        levelDefCache.register(mCurrentLevelDefId, levelDefDTOListener);
        levelDefCache.register(mNextLevelDefId, levelDefDTOListener);

        levelDefCache.getOrFetchAsync(mCurrentLevelDefId);
        levelDefCache.getOrFetchAsync(mNextLevelDefId);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        display();
        final Handler mHandler = new Handler();
        Animator current = AnimatorInflater.loadAnimator(getActivity(), R.animator.rotate_flip_hide);
        Animator show = AnimatorInflater.loadAnimator(getActivity(), R.animator.rotate_flip_show);

        current.setTarget(currentBadge);
        show.setTarget(nextBadge);

        animatorSet = new AnimatorSet();
        animatorSet.setStartDelay(getResources().getInteger(R.integer.user_level_level_up_start_delay));
        animatorSet.play(current).with(show);
        animatorSet.addListener(new AnimatorListenerAdapter()
        {
            @Override public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                //Dismiss the dialog after some delay.
                mHandler.postDelayed(new Runnable()
                {
                    @Override public void run()
                    {
                        getDialog().dismiss();
                    }
                }, getResources().getInteger(R.integer.user_level_level_up_end_delay));
            }
        });
        animatorSet.start();

        container.setOnTouchListener(new View.OnTouchListener()
        {
            @Override public boolean onTouch(View view, MotionEvent motionEvent)
            {
                return handleDismissingDialog();
            }
        });
    }

    private boolean handleDismissingDialog()
    {
        if (animatorSet != null)
        {
            animatorSet.end();
            return true;
        }
        return false;
    }

    @Override public void onDestroyView()
    {
        if (animatorSet != null)
        {
            if (animatorSet.isRunning())
            {
                animatorSet.end();
            }
            animatorSet.cancel();
            animatorSet.removeAllListeners();
        }
        levelDefCache.unregister(levelDefDTOListener);
        super.onDestroyView();
    }

    private void update(LevelDefId key, @NotNull LevelDefDTO levelDefDTO)
    {
        if (key.equals(mCurrentLevelDefId))
        {
            mCurrentLevelDefDTO = levelDefDTO;
        }
        else if (key.equals(mNextLevelDefId))
        {
            mNextLevelDefDTO = levelDefDTO;
        }
        display();
    }

    private void display()
    {
        if (mCurrentLevelDefDTO != null && currentBadge != null)
        {
            loadBadge(currentBadge, mCurrentLevelDefDTO.badge);
        }

        if (mNextLevelDefDTO != null && nextBadge != null)
        {
            loadBadge(nextBadge, mNextLevelDefDTO.badge);
            levelUpDescription.setText(getString(R.string.user_level_up_description_text, mNextLevelDefDTO.level));
        }
    }

    private void loadBadge(ImageView img, String url)
    {
        picasso.cancelRequest(img);
        picasso.load(url).placeholder(R.drawable.ic_bronze_level).fit().into(img);
    }

    private class LevelDefDTOListener implements DTOCacheNew.HurriedListener<LevelDefId, LevelDefDTO>
    {

        @Override public void onPreCachedDTOReceived(@NotNull LevelDefId key, @NotNull LevelDefDTO value)
        {
            update(key, value);
        }

        @Override public void onDTOReceived(@NotNull LevelDefId key, @NotNull LevelDefDTO value)
        {
            update(key, value);
        }

        @Override public void onErrorThrown(@NotNull LevelDefId key, @NotNull Throwable error)
        {

        }
    }
}
