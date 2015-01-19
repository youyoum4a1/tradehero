package com.tradehero.th.fragments.games;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.games.ViralMiniGameDefDTO;
import com.tradehero.th.api.games.ViralMiniGameDefKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.BaseDialogSupportFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.games.ViralMiniGameDefCache;
import com.tradehero.th.persistence.prefs.AutoShowViralGameDialogTimes;
import com.tradehero.th.persistence.prefs.ShowViralGameDialog;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;
import com.tradehero.th.utils.StringUtils;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

public class ViralGamePopupDialogFragment extends BaseDialogSupportFragment
{
    private static final String BUNDLE_KEY_VIRAL_MINI_GAME = ViralGamePopupDialogFragment.class + ".viralMiniGameId";
    private static final String BUNDLE_KEY_FROM_BROADCAST = ViralGamePopupDialogFragment.class + ".fromBroadcast";

    private static final String LINE_SCHEME_URI = "line://msg/";
    private static final String SRC_KEY = "src";
    private static final String SRC_FB = "fb";

    @InjectView(R.id.viral_game_banner) ImageView viralGameBanner;
    @InjectView(R.id.viral_game_selector) View viralGameSelector;
    @InjectView(R.id.progress) View progress;

    @Inject ViralMiniGameDefCache viralMiniGameDefCache;
    @Inject Picasso picasso;
    @Inject DashboardNavigator navigator;
    @Inject @ShowViralGameDialog TimingIntervalPreference showViralGameTimingIntervalPreference;
    @Inject @AutoShowViralGameDialogTimes IntPreference autoShowViralGameTimes;

    private ViralMiniGameDefDTO viralMiniGameDefDTO;
    private ViralMiniGameDefKey viralMiniGameDefKey;
    private Subscription subscription;
    private boolean isAutoPopup;

    public static ViralGamePopupDialogFragment newInstance(@NonNull ViralMiniGameDefKey viralMiniGameDefKey, boolean isFromBroadcast)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(BUNDLE_KEY_VIRAL_MINI_GAME, viralMiniGameDefKey.getArgs());
        bundle.putBoolean(BUNDLE_KEY_FROM_BROADCAST, isFromBroadcast);
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
            isAutoPopup = b.getBoolean(BUNDLE_KEY_FROM_BROADCAST);
        }

        if (isAutoPopup)
        {
            autoShowViralGameTimes.set(autoShowViralGameTimes.get() + 1);
        }
    }

    @OnClick({R.id.viral_game_banner, R.id.viral_game_selector})
    public void onBannerClick(View v)
    {
        if (viralMiniGameDefDTO != null && viralMiniGameDefDTO.gameUrl != null)
        {
            Bundle b = new Bundle();

            Uri url = Uri.parse(LINE_SCHEME_URI);
            Intent intent = new Intent(Intent.ACTION_VIEW, url);
            PackageManager packageManager = getActivity().getPackageManager();
            List<ResolveInfo> handlerActivities = packageManager.queryIntentActivities(intent, 0);
            String gameUrl = viralMiniGameDefDTO.gameUrl;
            if (handlerActivities.isEmpty())
            {
                Uri parsedGameUrl = Uri.parse(gameUrl);
                gameUrl = parsedGameUrl.buildUpon().appendQueryParameter(SRC_KEY, SRC_FB).build().toString();
            }
            ViralGameWebFragment.putUrl(b, gameUrl);
            dismiss();
            navigator.pushFragment(ViralGameWebFragment.class, b);
        }
    }

    @OnClick(R.id.close)
    public void onClose()
    {
        getDialog().cancel();
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
                            handleError(new Exception(getString(R.string.image_load_failed)));
                        }
                    });
        }
    }

    private void handleError(@Nullable Throwable e)
    {
        if (e != null)
        {
            THToast.show(new THException(e));
        }
        dismiss();
    }

    @Override public void onDismiss(DialogInterface dialog)
    {
        if (isAutoPopup)
        {
            //Update the next popup timing.
            long interval;
            switch (autoShowViralGameTimes.get())
            {
                case 1:
                    interval = TimingIntervalPreference.HOUR;
                    break;
                case 2:
                    interval = TimingIntervalPreference.DAY;
                    break;
                case 3:
                    interval = 2 * TimingIntervalPreference.DAY;
                    break;
                default:
                    interval = TimingIntervalPreference.WEEK;
            }
            showViralGameTimingIntervalPreference.pushInFuture(interval);
        }
        super.onDismiss(dialog);
    }
}
