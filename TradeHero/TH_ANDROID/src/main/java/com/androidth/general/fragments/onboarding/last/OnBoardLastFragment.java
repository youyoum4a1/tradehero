package com.androidth.general.fragments.onboarding.last;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.androidth.general.R;
import com.androidth.general.api.security.SecurityCompactDTOList;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.utils.route.THRouter;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class OnBoardLastFragment extends BaseFragment
{
    @Inject THRouter thRouter;
    @Bind(R.id.favorite_gallery) Gallery favoriteGallery;
    Observable<SecurityCompactDTOList> selectedSecuritiesObservable;

    private OnBoardFavoriteAdapter favoriteAdapter;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        favoriteAdapter = new OnBoardFavoriteAdapter(activity);
    }

    @SuppressLint("InflateParams")
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.on_board_last_fragment, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        favoriteGallery.setAdapter(favoriteAdapter);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchSecuritiesInfo();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        favoriteAdapter = null;
        super.onDetach();
    }

    public void setSelectedSecuritiesObservable(@NonNull Observable<SecurityCompactDTOList> selectedSecuritiesObservable)
    {
        this.selectedSecuritiesObservable = selectedSecuritiesObservable;
    }

    public void fetchSecuritiesInfo()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                selectedSecuritiesObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<SecurityCompactDTOList>()
                        {
                            @Override public void call(SecurityCompactDTOList list)
                            {
                                favoriteAdapter.clear();
                                favoriteAdapter.appendTail(list);
                                favoriteAdapter.notifyDataSetChanged();
                                if (list.size() > 1)
                                {
                                    favoriteGallery.setSelection(1);
                                }
                            }
                        },
                        new TimberAndToastOnErrorAction1("Failed to load exchanges")));
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(android.R.id.button1)
    protected void buySharesButtonClicked(View view)
    {
        SecurityId id = favoriteAdapter.getItem(favoriteGallery.getSelectedItemPosition()).getSecurityId();
        thRouter.open("stock-security/" +  id.getExchange() + "/" + id.getSecuritySymbol(), getActivity());
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(android.R.id.button2)
    protected void buySharesLaterButtonClicked(View view)
    {
        thRouter.open("trending-securities", getActivity());
    }
}
