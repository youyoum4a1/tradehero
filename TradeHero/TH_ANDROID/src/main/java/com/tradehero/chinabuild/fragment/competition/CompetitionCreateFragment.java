package com.tradehero.chinabuild.fragment.competition;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.chinabuild.dialog.ShareSheetDialogLayout;
import com.tradehero.chinabuild.fragment.message.DiscoveryDiscussSendFragment;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.SpinnerExchangeIconAdapter;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.competition.CompetitionCache;
import com.tradehero.th.persistence.market.ExchangeCompactListCache;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.StringUtils;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;

/**
 * Created by huhaiping on 14-9-9. UGC 比赛创建页
 */
public class CompetitionCreateFragment extends DashboardFragment
{

    @Inject Lazy<ExchangeCompactListCache> exchangeCompactListCache;
    public DTOCacheNew.Listener<ExchangeListType, ExchangeCompactDTOList> exchangeListTypeCacheListener;

    @InjectView(R.id.edtCreateCompetitionName) EditText edtCompetitionName;
    @InjectView(R.id.edtCreateCompetitionIntro) EditText edtCompetitionIntro;
    @InjectView(R.id.spCreateCompetitionPeriod) Spinner spCompetitionPerid;
    @InjectView(R.id.cbCreateCompetitionInvite) CheckBox cbCompetitionInvite;

    @InjectView(R.id.cbExchangeAM) CheckBox cbExchangeAM;

    public SpinnerExchangeIconAdapter spinnerIconAdapterPeriod;

    @Inject Lazy<CompetitionCache> competitionCacheLazy;
    private Callback<UserCompetitionDTO> callbackcreatUGC;
    @Inject CurrentUserId currentUserId;

    public static final int CREATE_COMPETITION_SUCCESS = 0;
    public static final int CREATE_COMPETITION_ERROR_TITLE = 1;
    public static final int CREATE_COMPETITION_ERROR_INTRO = 2;
    public static final int CREATE_COMPETITION_ERROR_EXCHANGE = 3;
    public static final int CREATE_COMPETITION_ERROR_TITLE_SPECIAL_CHAR = 4;

    private ProgressDialog mTransactionDialog;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    private UserCompetitionDTO userCompetitionDTO;
    private Dialog mShareSheetDialog;

    private boolean bSuccessed = false;

    private int tradehero_blue;

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
        if (validCompetition() == CREATE_COMPETITION_SUCCESS)
        {
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
        return CompetitionUtils.getExchanges(false, false, cbExchangeAM.isChecked());
    }

    public void showErrorCreateCompetition()
    {
        int ERROR = validCompetition();
        if (ERROR == CREATE_COMPETITION_ERROR_TITLE)
        {
            THToast.show("请输入大于4个字符的比赛名称");
            return;
        }
        if (ERROR == CREATE_COMPETITION_ERROR_TITLE_SPECIAL_CHAR)
        {
            THToast.show("比赛名称不能包含特殊字符");
            return;
        }
        if (ERROR == CREATE_COMPETITION_ERROR_INTRO)
        {
            THToast.show("请输入大于4个字符的比赛介绍");
            return;
        }
        if (ERROR == CREATE_COMPETITION_ERROR_EXCHANGE)
        {
            THToast.show("您还未选择交易所");
            return;
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
        tradehero_blue = getActivity().getResources().getColor(R.color.tradehero_blue);
        initSpinnerViewPeriod();
        cbCompetitionInvite.setChecked(true);
        cbExchangeAM.setChecked(true);
    }
    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchExchangeList();

        if (bSuccessed)
        {
            popCurrentFragment();
        }
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
        }

        @Override public void onErrorThrown(@NotNull ExchangeListType key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_exchange_list));
        }
    }

    private void initSpinnerViewPeriod()
    {
        spinnerIconAdapterPeriod = new SpinnerExchangeIconAdapter(getActivity(), CompetitionUtils.strPeriods);
        spCompetitionPerid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (view != null)
                {
                    TextView selectedTV = (TextView) view.findViewById(R.id.tvSpinnerItemName);
                    selectedTV.setTextColor(tradehero_blue);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

    public int validCompetition()
    {
        if (edtCompetitionName != null && edtCompetitionName.length() < 4)
        {
            return CREATE_COMPETITION_ERROR_TITLE;
        }
        String inputStr = edtCompetitionName.getText().toString();
        if(StringUtils.containSpecialChars(inputStr)){
            return CREATE_COMPETITION_ERROR_TITLE_SPECIAL_CHAR;
        }
        if (edtCompetitionIntro != null && edtCompetitionIntro.length() < 4)
        {
            return CREATE_COMPETITION_ERROR_INTRO;
        }
        if (!cbExchangeAM.isChecked())
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
                if(userCompetitionDTO==null){
                    return;
                }
                THToast.show("创建成功！");
                CompetitionCreateFragment.this.userCompetitionDTO = userCompetitionDTO;
                if (cbCompetitionInvite.isChecked())
                {
                    if(getActivity()==null){
                        return;
                    }
                    String endPoint = THSharePreferenceManager.getShareEndPoint(getActivity());
                    mShareSheetTitleCache.set(getString(R.string.share_create_contest, currentUserId.get().toString(),
                            userCompetitionDTO.id, userCompetitionDTO.name, endPoint));
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
                                    bSuccessed = true;
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
                else
                {
                    popCurrentFragment();
                }
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
            THException thException = new THException(retrofitError);
            THToast.show(thException);
        }
    }

    public void inviteFriendsToCompetition()
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DiscoveryDiscussSendFragment.BUNDLE_KEY_COMPETITION, userCompetitionDTO);
        pushFragment(DiscoveryDiscussSendFragment.class, bundle);
    }
}
