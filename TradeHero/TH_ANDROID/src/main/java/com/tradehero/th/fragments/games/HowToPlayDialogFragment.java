package com.tradehero.th.fragments.games;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTouch;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.games.MiniGameDefDTO;
import com.tradehero.th.api.games.MiniGameDefKey;
import com.tradehero.th.fragments.base.BaseDialogSupportFragment;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.persistence.games.MiniGameDefCache;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;

public class HowToPlayDialogFragment extends BaseDialogSupportFragment
{
    private static final String GAME_ID_KEY_ARG = HowToPlayDialogFragment.class.getName() + ".gameId";

    @Inject MiniGameDefCache miniGameDefCache;

    @InjectView(R.id.viewpager) ViewPager pager;
    FragmentPagerAdapter pagerAdapter;

    @NonNull protected MiniGameDefKey gameId;
    @Nullable Subscription miniGameDefSubscription;
    protected MiniGameDefDTO miniGameDefDTO;

    public static void putGameId(@NonNull Bundle args, @NonNull MiniGameDefKey miniGameDefKey)
    {
        args.putBundle(GAME_ID_KEY_ARG, miniGameDefKey.getArgs());
    }

    @NonNull public static MiniGameDefKey getGameId(@NonNull Bundle args)
    {
        return new MiniGameDefKey(args.getBundle(GAME_ID_KEY_ARG));
    }

    @NonNull public static HowToPlayDialogFragment newInstance(@NonNull MiniGameDefKey miniGameDefKey)
    {
        Bundle args = new Bundle();
        HowToPlayDialogFragment.putGameId(args, miniGameDefKey);
        HowToPlayDialogFragment howToPlay = new HowToPlayDialogFragment();
        howToPlay.setArguments(args);
        return howToPlay;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gameId = getGameId(getArguments());
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_web_view_pager, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchMiniGameDef();
    }

    @Override public void onStop()
    {
        unsubscribe(miniGameDefSubscription);
        miniGameDefSubscription = null;
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @NonNull protected FragmentPagerAdapter createPagerAdapter()
    {
        return new HowToPlayPagerAdapter(getChildFragmentManager());
    }

    protected class HowToPlayPagerAdapter extends FragmentPagerAdapter
    {
        public HowToPlayPagerAdapter(FragmentManager fragmentManager)
        {
            super(fragmentManager);
        }

        @Override public Fragment getItem(int position)
        {
            Bundle args = new Bundle();
            BaseWebViewFragment.putUrl(args, miniGameDefDTO.howToPlayUrl);
            return Fragment.instantiate(getActivity(), BaseWebViewFragment.class.getName(), args);
        }

        @Override public int getCount()
        {
            return 1;
        }
    }

    protected void fetchMiniGameDef()
    {
        unsubscribe(miniGameDefSubscription);
        miniGameDefSubscription = AppObservable.bindFragment(
                this,
                miniGameDefCache.get(gameId))
                .subscribe(createMiniGameDefObserver());
    }

    @NonNull protected Observer<Pair<MiniGameDefKey, MiniGameDefDTO>> createMiniGameDefObserver()
    {
        return new MiniGameDefObserver();
    }

    protected class MiniGameDefObserver implements Observer<Pair<MiniGameDefKey, MiniGameDefDTO>>
    {
        @Override public void onNext(Pair<MiniGameDefKey, MiniGameDefDTO> pair)
        {
            linkWith(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            if (miniGameDefDTO == null)
            {
                THToast.show(R.string.error_fetch_info_game);
            }
        }
    }

    protected void linkWith(@NonNull MiniGameDefDTO miniGameDefDTO)
    {
        this.miniGameDefDTO = miniGameDefDTO;
        pagerAdapter = createPagerAdapter();
        pager.setAdapter(pagerAdapter);
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick({android.R.id.button1, android.R.id.button2})
    protected void playNowClicked(View view)
    {
        dismiss();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnTouch(R.id.container)
    protected boolean containerTouched(@SuppressWarnings("UnusedParameters") View v, @SuppressWarnings("UnusedParameters") MotionEvent event)
    {
        dismiss();
        return true;
    }
}
