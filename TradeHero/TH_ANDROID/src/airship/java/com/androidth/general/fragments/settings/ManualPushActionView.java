package com.androidth.general.fragments.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidth.general.R;
import com.androidth.general.models.push.urbanairship.UrbanAirshipPushNotificationManager;
import com.urbanairship.UAirship;
import com.urbanairship.actions.Action;
import com.urbanairship.actions.ActionArguments;
import com.urbanairship.actions.ActionValue;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class ManualPushActionView extends ScrollView
{
    @BindView(R.id.channel_id) TextView channelIdView;
    @BindView(R.id.situation_spinner) Spinner situationSpinner;
    @BindView(R.id.arguments) EditText argumentView;
    @BindView(R.id.action_name) EditText actionNameView;

    ArrayAdapter<SituationDTO> situationAdapter;
    private Unbinder unbinder;
    private BehaviorSubject<Pair<String, ActionArguments>> actionArgumentObservable;

    //<editor-fold desc="Constructors">
    public ManualPushActionView(Context context)
    {
        super(context);
        actionArgumentObservable = BehaviorSubject.create();
    }

    public ManualPushActionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        actionArgumentObservable = BehaviorSubject.create();
    }

    public ManualPushActionView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        actionArgumentObservable = BehaviorSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        unbinder = ButterKnife.bind(this);
        situationAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, SituationDTO.getAll());
        situationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        situationSpinner.setAdapter(situationAdapter);

        UAirship uAirship = UrbanAirshipPushNotificationManager.getUAirship();
        if (uAirship == null)
        {
            channelIdView.setText("uAirship is null");
        }
        else
        {
            channelIdView.setText("Channel Id: " + uAirship.getPushManager().getChannelId());
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        unbinder = ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        //try
        {
            actionArgumentObservable.onNext(Pair.create(
                    actionNameView.getText().toString(),
                    new ActionArguments(Action.SITUATION_PUSH_OPENED,
                            ActionValue.wrap(argumentView.getText().toString()),
                            new Bundle())));
             }
            /*catch (ActionValueException e)
        {
            Timber.e(e, "Failed to pass on action value");
            actionArgumentObservable.onError(e);
        }*/
        unbinder.unbind();
        super.onDetachedFromWindow();
    }

    @NonNull public Observable<Pair<String, ActionArguments>> getActionArgumentObservable()
    {
        return actionArgumentObservable.asObservable();
    }
}
