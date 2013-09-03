package com.tradehero.th.fragments.authentication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.form.ForgotPasswordFormDTO;
import com.tradehero.th.api.users.ForgotPasswordDTO;
import com.tradehero.th.application.App;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.base.Application;
import com.tradehero.th.http.RequestTaskCompleteListener;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.ProfileDTO;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.utills.Logger;
import com.tradehero.th.utills.Logger.LogLevel;
import com.tradehero.th.utills.PUtills;
import com.tradehero.th.utills.Util;
import com.tradehero.th.widget.ServerValidatedEmailText;
import org.json.JSONObject;

public class EmailSignInFragment extends AuthenticationFragment
        implements View.OnClickListener, RequestTaskCompleteListener
{

    private final static String TAG = EmailSignInFragment.class.getName();
    private ProgressDialog mProgressDialog;
    private View forgotDialogView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        //View view = null;
        View view = inflater.inflate(R.layout.authentication_email_sign_in, container, false);

        view.findViewById(R.id.authentication_sign_in_email).setOnClickListener(onClickListener);
        view.findViewById(R.id.authentication_sign_in_forgot_password).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.authentication_sign_in_forgot_password:
                showForgotPasswordUI();
                break;

            default:
                break;
        }
    }

    private void showForgotPasswordUI()
    {
        if (forgotDialogView == null)
        {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            forgotDialogView = inflater.inflate(R.layout.forgot_password_dialog, (ViewGroup)getView(), false);
        }

        String message = getActivity().getString(R.string.authentication_forgot_password_ask_for_email);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(message)
                .setView(forgotDialogView)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int which)
                    {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int which)
                    {

                        ServerValidatedEmailText serverValidatedEmailText =
                                (ServerValidatedEmailText) forgotDialogView.findViewById(R.id.authentication_forgot_password_validated_email);
                        if (serverValidatedEmailText == null)
                        {
                            return;
                        }
                        serverValidatedEmailText.forceValidate();

                        if (!serverValidatedEmailText.isValid())
                        {
                            THToast.show(R.string.forgot_email_incorrect_input_email);
                        }
                        else
                        {
                            String email = serverValidatedEmailText.getText().toString();
                            doForgotPassword(email);
                            dialogInterface.dismiss();
                        }
                    }
                }).create().show();
    }

    @Override
    public void onTaskComplete(JSONObject pResponseObject)
    {

        if (mProgressDialog.isShowing())
        {
            mProgressDialog.dismiss();
        }

        if (pResponseObject != null)
        {

            try
            {
                Logger.log(TAG, pResponseObject.toString(), LogLevel.LOGGING_LEVEL_INFO);
                if (pResponseObject.has("Message"))
                {
                    THToast.show(pResponseObject.optString("Message"));
                    //startActivity(new Intent(getActivity(),DashboardActivity.class));
                }
                else
                {
                    JSONObject obj = pResponseObject.getJSONObject("profileDTO");

                    ProfileDTO prof = new PUtills(getActivity())._parseJson(obj);

                    ((App) getActivity().getApplication()).setProfileDTO(prof);
                    startActivity(new Intent(getActivity(), DashboardActivity.class));
                    getActivity().finish();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override public void onErrorOccured(int pErrorCode, String pErrorMessage)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void doForgotPassword(String email)
    {
        ForgotPasswordFormDTO forgotPasswordFormDTO = new ForgotPasswordFormDTO();
        forgotPasswordFormDTO.userEmail = email;

        mProgressDialog = ProgressDialog.show(
                getActivity(),
                Application.getResourceString(R.string.please_wait),
                Application.getResourceString(R.string.connecting_tradehero_only),
                true);

        NetworkEngine.createService(UserService.class)
                .forgotPassword(forgotPasswordFormDTO, createForgotPasswordCallback());
    }

    private THCallback<ForgotPasswordDTO> createForgotPasswordCallback()
    {
        return new THCallback<ForgotPasswordDTO>()
        {
            @Override protected void success(ForgotPasswordDTO forgotPasswordDTO, THResponse thResponse)
            {
                THToast.show(R.string.thank_you_message_email);
            }

            @Override public void failure(THException ex)
            {
                THToast.show(ex);
            }

            @Override protected void finish()
            {
                mProgressDialog.dismiss();
            }
        };
    }

    @Override public AuthenticationMode getAuthenticationMode()
    {
        return AuthenticationMode.SignIn;
    }
}
