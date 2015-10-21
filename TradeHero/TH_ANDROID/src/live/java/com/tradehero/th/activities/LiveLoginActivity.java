package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.live.ayondo.AyondoLiveLoginFormDTO;
import com.tradehero.th.api.live.ayondo.AyondoUserProfileDTO;
import com.tradehero.th.network.service.DummyAyondoLiveServiceWrapper;
import com.tradehero.th.persistence.prefs.IsLiveLogIn;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class LiveLoginActivity extends BaseActivity
{
    @Inject DummyAyondoLiveServiceWrapper ayondoLiveServiceWrapper;
    @Inject @IsLiveLogIn BooleanPreference isLiveLogIn;

    @Bind(R.id.my_toolbar) Toolbar myToolbar;
    @Bind(R.id.live_id) EditText liveId;
    @Bind(R.id.live_password) EditText livePassword;
    @Bind(R.id.progress) ProgressBar progressBar;

    private boolean isDisabledTouchEvent = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_login);
        ButterKnife.bind(LiveLoginActivity.this);
        setSupportActionBar(myToolbar);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.live_prompt_menu, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.close)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean dispatchTouchEvent(MotionEvent ev)
    {
        return isDisabledTouchEvent || super.dispatchTouchEvent(ev);
    }

    @Override public void onBackPressed()
    {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_login)
    public void loginBtnOnClicked()
    {
        AyondoLiveLoginFormDTO liveLoginFormDTO = new AyondoLiveLoginFormDTO(liveId.getText().toString(), livePassword.getText().toString());

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(livePassword.getWindowToken(), 0);

        isDisabledTouchEvent = true;
        progressBar.bringToFront();
        progressBar.setVisibility(View.VISIBLE);

        ayondoLiveServiceWrapper.loginAyondo(liveLoginFormDTO)
                .delay(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AyondoUserProfileDTO>()
                {
                    @Override public void call(AyondoUserProfileDTO ayondoUserProfileDTO)
                    {
                        isDisabledTouchEvent = false;
                        progressBar.setVisibility(View.GONE);

                        if (ayondoUserProfileDTO != null)
                        {
                            setResult(RESULT_OK);
                            isLiveLogIn.set(true);
                            finish();
                        }
                        else
                        {
                            THToast.show("Account Id or Password is incorrect.");
                        }
                    }
                }, new TimberAndToastOnErrorAction1("Account Id or Password is incorrect.", "Account Id or Password is incorrect."));
    }
}
