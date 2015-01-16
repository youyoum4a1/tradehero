package com.tradehero.th.fragments.games;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.games.ViralMiniGameDefDTO;
import com.tradehero.th.api.games.ViralMiniGameDefKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.BaseDialogSupportFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.games.ViralMiniGameDefCache;
import com.tradehero.th.utils.StringUtils;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

public class ViralGamePopupDialogFragment extends BaseDialogSupportFragment
{
    private static final String BUNDLE_KEY_VIRAL_MINI_GAME = ViralGamePopupDialogFragment.class + ".viralMiniGameId";

    @InjectView(R.id.viral_game_banner) ImageView viralGameBanner;
    @InjectView(R.id.viral_game_selector) View viralGameSelector;
    @InjectView(R.id.progress) View progress;

    @Inject ViralMiniGameDefCache viralMiniGameDefCache;
    @Inject Picasso picasso;
    @Inject DashboardNavigator navigator;

    private ViralMiniGameDefDTO viralMiniGameDefDTO;
    private ViralMiniGameDefKey viralMiniGameDefKey;
    private Subscription subscription;

    public static ViralGamePopupDialogFragment newInstance(@NonNull ViralMiniGameDefKey viralMiniGameDefKey)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(BUNDLE_KEY_VIRAL_MINI_GAME, viralMiniGameDefKey.getArgs());
        ViralGamePopupDialogFragment dialogFragment = new ViralGamePopupDialogFragment();
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.viral_game_dialog_fragment, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        Bundle b = getArguments();

        if (b != null && b.getBundle(BUNDLE_KEY_VIRAL_MINI_GAME) != null)
        {
            viralMiniGameDefKey = new ViralMiniGameDefKey(b.getBundle(BUNDLE_KEY_VIRAL_MINI_GAME));
            subscription = AndroidObservable.bindFragment(this, viralMiniGameDefCache.get(viralMiniGameDefKey))
                    .subscribe(new Observer<Pair<ViralMiniGameDefKey, ViralMiniGameDefDTO>>()
                    {
                        @Override public void onCompleted()
                        {

                        }

                        @Override public void onError(Throwable e)
                        {
                            handleError(e);
                        }

                        @Override public void onNext(Pair<ViralMiniGameDefKey, ViralMiniGameDefDTO> viralMiniGameDefKeyViralMiniGameDefDTOPair)
                        {
                            linkWith(viralMiniGameDefKeyViralMiniGameDefDTOPair.second);
                        }
                    });
        }
    }

    @OnClick({R.id.viral_game_banner, R.id.viral_game_selector})
    public void onBannerClick(View v)
    {
        if (viralMiniGameDefDTO != null && viralMiniGameDefDTO.gameUrl != null)
        {
            Bundle b = new Bundle();
            ViralGameWebFragment.putUrl(b, viralMiniGameDefDTO.gameUrl);
            navigator.pushFragment(ViralGameWebFragment.class, b);
        }
    }

    @OnClick(R.id.close)
    public void onClose()
    {
        getDialog().dismiss();
    }

    @Override public void onDestroyView()
    {
        unsubscribe(subscription);
        picasso.cancelRequest(viralGameBanner);
        super.onDestroyView();
    }

    protected void linkWith(ViralMiniGameDefDTO viralMiniGameDefDTO)
    {
        this.viralMiniGameDefDTO = viralMiniGameDefDTO;
        loadBanner();
    }

    private void loadBanner()
    {
        if (!StringUtils.isNullOrEmpty(viralMiniGameDefDTO.bannerUrl))
        {
            picasso.load(viralMiniGameDefDTO.bannerUrl)
                    .into(viralGameBanner, new Callback()
                    {
                        @Override public void onSuccess()
                        {
                            progress.setVisibility(View.GONE);
                            viralGameSelector.setClickable(true);
                            viralGameSelector.setFocusable(true);
                            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        }

                        @Override public void onError()
                        {
                            handleError(new Exception("Failed to load image for this game"));
                        }
                    });
        }
    }

    private void handleError(@Nullable Throwable e)
    {
        if(e != null)
        {
            THToast.show(new THException(e));
        }
        getDialog().dismiss();
    }
}
