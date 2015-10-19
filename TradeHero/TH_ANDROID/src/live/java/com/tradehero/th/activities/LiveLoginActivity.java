package com.tradehero.th.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.live.ayondo.AyondoLiveLoginFormDTO;
import com.tradehero.th.api.live.ayondo.AyondoUserProfileDTO;
import com.tradehero.th.network.service.DummyAyondoLiveServiceWrapper;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import javax.inject.Inject;
import rx.functions.Action1;
import timber.log.Timber;

public class LiveLoginActivity extends BaseActivity
{
    @Inject DummyAyondoLiveServiceWrapper ayondoLiveServiceWrapper;

    @Bind(R.id.my_toolbar) Toolbar myToolbar;
    @Bind(R.id.live_id) EditText liveId;
    @Bind(R.id.live_password) EditText livePassword;

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

        ayondoLiveServiceWrapper.loginAyondo(liveLoginFormDTO)
                .subscribe(new Action1<AyondoUserProfileDTO>()
                {
                    @Override public void call(AyondoUserProfileDTO ayondoUserProfileDTO)
                    {
                        Timber.d(ayondoUserProfileDTO.toString());
                    }
                }, new TimberAndToastOnErrorAction1("Account Id or Password is incorrect.", "Account Id or Password is incorrect."));
    }
}
