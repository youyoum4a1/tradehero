package com.ayondo.academy.fragments.level;

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
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.squareup.picasso.Picasso;
import com.ayondo.academy.R;
import com.ayondo.academy.api.level.LevelDefDTO;
import com.ayondo.academy.api.level.key.LevelDefId;
import com.ayondo.academy.fragments.base.BaseDialogFragment;
import com.ayondo.academy.persistence.level.LevelDefCacheRx;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;

public class LevelUpDialogFragment extends BaseDialogFragment
{
    private static final String BUNDLE_KEY_CURRENT = LevelUpDialogFragment.class.getName() + ".current";
    private static final String BUNDLE_KEY_NEXT = LevelUpDialogFragment.class.getName() + ".next";

    private static final float DIM_AMOUNT = 0.8f;

    @Bind(R.id.user_level_next_badge) ImageView nextBadge;
    @Bind(R.id.user_level_current_badge) ImageView currentBadge;
    @Bind(R.id.user_level_up_description) TextView levelUpDescription;
    @Bind(R.id.user_level_up_main_container) ViewGroup container;

    @Inject Picasso picasso;
    @Inject LevelDefCacheRx levelDefCache;

    private LevelDefId mCurrentLevelDefId;
    private LevelDefId mNextLevelDefId;

    private LevelDefDTO mCurrentLevelDefDTO;
    private LevelDefDTO mNextLevelDefDTO;
    private AnimatorSet animatorSet;

    public static LevelUpDialogFragment newInstance(@NonNull LevelDefId fromLevelId, @NonNull LevelDefId toLevelId)
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

    @Override @NonNull public Dialog onCreateDialog(@NonNull Bundle savedInstanceState)
    {
        Dialog d = super.onCreateDialog(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TH_Dialog);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        d.getWindow().setDimAmount(DIM_AMOUNT);
        d.setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
            {
                //Handle backPressed an end the animation if it's running.
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    boolean handled = LevelUpDialogFragment.this.handleDismissingDialog();
                    if (handled)
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

        AppObservable.bindSupportFragment(
                this,
                Observable.merge(
                        levelDefCache.get(mCurrentLevelDefId),
                        levelDefCache.get(mNextLevelDefId)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LevelDefObserver());
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
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
                        Dialog dialog = getDialog();
                        if (dialog != null)
                        {
                            dialog.dismiss();
                        }
                    }
                }, getResources().getInteger(R.integer.user_level_level_up_end_delay));
            }
        });
        animatorSet.start();

        container.setOnTouchListener(new View.OnTouchListener()
        {
            @Override public boolean onTouch(View view1, MotionEvent motionEvent)
            {
                return LevelUpDialogFragment.this.handleDismissingDialog();
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
        super.onDestroyView();
    }

    private void update(LevelDefId key, @NonNull LevelDefDTO levelDefDTO)
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

    private class LevelDefObserver implements Observer<Pair<LevelDefId, LevelDefDTO>>
    {
        @Override public void onNext(Pair<LevelDefId, LevelDefDTO> pair)
        {
            update(pair.first, pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
        }
    }
}
