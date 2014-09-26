package com.tradehero.th.fragments.chinabuild.fragment.competition;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.SpinnerExchangeIconAdapter;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.data.UserCompetitionDTO;
import com.tradehero.th.fragments.chinabuild.dialog.ShareSheetDialogLayout;
import com.tradehero.th.fragments.chinabuild.fragment.message.DiscoveryDiscussSendFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.competition.CompetitionCache;
import com.tradehero.th.persistence.market.ExchangeCompactListCache;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-9-9.
 * UGC 比赛创建页
 */
public class CompetitionCreateFragment extends DashboardFragment
{

    @Inject Lazy<ExchangeCompactListCache> exchangeCompactListCache;
    public DTOCacheNew.Listener<ExchangeListType, ExchangeCompactDTOList> exchangeListTypeCacheListener;

    @InjectView(R.id.edtCreateCompetitionName) EditText edtCompetitionName;
    @InjectView(R.id.edtCreateCompetitionIntro) EditText edtCompetitionIntro;
    @InjectView(R.id.spCreateCompetitionPeriod) Spinner spCompetitionPerid;
    //@InjectView(R.id.spCreateCompetitionExchange) Spinner spinnerExchange;
    @InjectView(R.id.cbCreateCompetitionInvite) CheckBox cbCompetitionInvite;

    @InjectView(R.id.cbExchangeCH) CheckBox cbExchangeCH;
    @InjectView(R.id.cbExchangeHK) CheckBox cbExchangeHK;
    @InjectView(R.id.cbExchangeAM) CheckBox cbExchangeAM;

    public SpinnerExchangeIconAdapter spinnerIconAdapter;
    public SpinnerExchangeIconAdapter spinnerIconAdapterPeriod;
    private ExchangeCompactDTOList exchangeCompactDTOs;

    @Inject Lazy<CompetitionCache> competitionCacheLazy;
    private Callback<UserCompetitionDTO> callbackcreatUGC;

    public static final int CREATE_COMPETITION_SUCCESS = 0;
    public static final int CREATE_COMPETITION_ERROR_TITLE = 1;
    public static final int CREATE_COMPETITION_ERROR_INTRO = 2;
    public static final int CREATE_COMPETITION_ERROR_EXCHANGE = 3;

    private ProgressDialog mTransactionDialog;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    private UserCompetitionDTO userCompetitionDTO;
    private Dialog mShareSheetDialog;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        exchangeListTypeCacheListener = createExchangeListTypeFetchListener();
        callbackcreatUGC = new CreateUGCCallback();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("创建比赛");
        setHeadViewRight0("提交");
    }

    @Override
    public void onClickHeadRight0()
    {
        Timber.d("点击提交");
        if (validCompetition() == CREATE_COMPETITION_SUCCESS)
        {
            Timber.d("创建比赛条件满足");
            createUGC();
        }
        else
        {
            showErrorCreateCompetition();
        }
    }

    public void createUGC()
    {
        mTransactionDialog = progressDialogUtil.show(CompetitionCreateFragment.this.getActivity(),
                R.string.processing, R.string.alert_dialog_please_wait);
        competitionCacheLazy.get()
                .creatUGCompetition(edtCompetitionName.getText().toString(),
                        edtCompetitionIntro.getText().toString(), getDurationDays(),
                        getExchangeIds(), callbackcreatUGC);
    }

    public int getDurationDays()
    {
        int position = spCompetitionPerid.getSelectedItemPosition();
        return CompetitionUtils.strDuration[position];
    }

    public int[] getExchangeIds()
    {
        //return CompetitionUtils.Exchanges[CompetitionUtils.EXCHANGE_CHINA];
        return CompetitionUtils.getExchanges(cbExchangeCH.isChecked(), cbExchangeHK.isChecked(), cbExchangeAM.isChecked());
    }

    public void showErrorCreateCompetition()
    {
        int ERROR = validCompetition();
        if (ERROR == CREATE_COMPETITION_ERROR_TITLE)
        {
            THToast.show("请输入大于4个字符的比赛名称");
        }
        else if (ERROR == CREATE_COMPETITION_ERROR_INTRO)
        {
            THToast.show("请输入大于4个字符的比赛介绍");
        }
        else if (ERROR == CREATE_COMPETITION_ERROR_EXCHANGE)
        {
            THToast.show("您还未选择交易所");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.competition_create_layout, container, false);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    private void initView()
    {
        initSpinnerViewPeriod();
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchExchangeList();
    }

    //<editor-fold desc="Exchange List Listener">
    protected DTOCacheNew.Listener<ExchangeListType, ExchangeCompactDTOList> createExchangeListTypeFetchListener()
    {
        return new TrendingExchangeListTypeFetchListener();
    }

    protected class TrendingExchangeListTypeFetchListener implements DTOCacheNew.Listener<ExchangeListType, ExchangeCompactDTOList>
    {
        @Override public void onDTOReceived(@NotNull ExchangeListType key, @NotNull ExchangeCompactDTOList value)
        {
            Timber.d("Filter exchangeListTypeCacheListener onDTOReceived");
            //linkWith(value, true);
            //initSpinnerView(value);
        }

        @Override public void onErrorThrown(@NotNull ExchangeListType key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_exchange_list));
            Timber.e("Error fetching the list of exchanges %s", key, error);
        }
    }

    private void initSpinnerViewPeriod()
    {
        spinnerIconAdapterPeriod = new SpinnerExchangeIconAdapter(getActivity(), CompetitionUtils.strPeriods);
        spCompetitionPerid.setAdapter(spinnerIconAdapterPeriod);
    }

    private void fetchExchangeList()
    {
        detachExchangeListCache();
        ExchangeListType key = new ExchangeListType();
        exchangeCompactListCache.get().register(key, exchangeListTypeCacheListener);
        exchangeCompactListCache.get().getOrFetchAsync(key);
    }

    protected void detachExchangeListCache()
    {
        if (exchangeListTypeCacheListener != null)
        {
            exchangeCompactListCache.get().unregister(exchangeListTypeCacheListener);
        }
    }

    //private void initSpinnerView(ExchangeCompactDTOList value)
    //{
    //    exchangeCompactDTOs = value;
    //    int sizeList = value.size();
    //    String[] strExchangeList = new String[sizeList + 1];
    //    int[] countryList = new int[sizeList + 1];
    //    strExchangeList[0] = "全部交易所";
    //    countryList[0] = R.drawable.default_image;
    //    for (int i = 1; i < sizeList + 1; i++)
    //    {
    //        strExchangeList[i] = value.get(i - 1).desc;
    //        countryList[i] = value.get(i - 1).getCountryCodeFlagResId();
    //    }
    //    spinnerIconAdapter = new SpinnerExchangeIconAdapter(getActivity(), strExchangeList, countryList);
    //    spinnerExchange.setAdapter(spinnerIconAdapter);
    //}

    public int validCompetition()
    {
        if (edtCompetitionName != null && edtCompetitionName.length() < 4)
        {
            return CREATE_COMPETITION_ERROR_TITLE;
        }
        else if (edtCompetitionIntro != null && edtCompetitionIntro.length() < 4)
        {
            return CREATE_COMPETITION_ERROR_INTRO;
        }
        else if ((!cbExchangeAM.isChecked()) && (!cbExchangeHK.isChecked()) && (!cbExchangeCH.isChecked()))
        {
            return CREATE_COMPETITION_ERROR_EXCHANGE;
        }
        return CREATE_COMPETITION_SUCCESS;
    }

    protected class CreateUGCCallback implements retrofit.Callback<UserCompetitionDTO>
    {

        @Override
        public void success(UserCompetitionDTO userCompetitionDTO, Response response)
        {
            onFinish();
            if (response.getStatus() == 200)
            {
                THToast.show("创建成功！");
                CompetitionCreateFragment.this.userCompetitionDTO = userCompetitionDTO;
                mShareSheetTitleCache.set(getString(R.string.share_create_contest,
                        edtCompetitionName.getText().toString()));
                ShareSheetDialogLayout contentView = (ShareSheetDialogLayout) LayoutInflater.from(getActivity())
                        .inflate(R.layout.share_sheet_local_dialog_layout, null);
                contentView.setLocalSocialClickedListener(
                        new ShareSheetDialogLayout.OnLocalSocialClickedListener()
                        {
                            @Override public void onShareRequestedClicked()
                            {
                                inviteFriendsToCompetition();
                                if (mShareSheetDialog != null)
                                {
                                    mShareSheetDialog.hide();
                                }
                            }
                        });
                mShareSheetDialog = THDialog.showUpDialog(getActivity(), contentView);
                mShareSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override public void onDismiss(DialogInterface dialogInterface)
                    {
                        popCurrentFragment();
                    }
                });
            }
        }

        private void onFinish()
        {
            if (mTransactionDialog != null)
            {
                mTransactionDialog.dismiss();
            }
            mTransactionDialog.dismiss();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            onFinish();
            if (retrofitError != null)
            {
                Timber.e(retrofitError, "Reporting the error to Crashlytics %s", retrofitError.getBody());
            }
            THException thException = new THException(retrofitError);
            THToast.show(thException);
        }
    }

    public void inviteFriendsToCompetition()
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DiscoveryDiscussSendFragment.BUNDLE_KEY_COMPETITION,userCompetitionDTO);
        pushFragment(DiscoveryDiscussSendFragment.class, bundle);
    }
}
