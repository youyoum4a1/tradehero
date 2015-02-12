package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.models.push.urbanairship.UrbanAirshipPushNotificationManager;
import com.urbanairship.UAirship;
import com.urbanairship.actions.ActionArguments;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class ManualPushActionView extends ScrollView
{
    @InjectView(R.id.channel_id) TextView channelIdView;
    @InjectView(R.id.situation_spinner) Spinner situationSpinner;
    @InjectView(R.id.arguments) EditText argumentView;
    @InjectView(R.id.action_name) EditText actionNameView;

    ArrayAdapter<SituationDTO> situationAdapter;
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
        ButterKnife.inject(this);
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
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        actionArgumentObservable.onNext(Pair.create(
                actionNameView.getText().toString(),
                new ActionArguments(
                        ((SituationDTO) situationSpinner.getSelectedItem()).situation,
                        argumentView.getText().toString())));
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @NonNull public Observable<Pair<String, ActionArguments>> getActionArgumentObservable()
    {
        return actionArgumentObservable.asObservable();
    }
}
