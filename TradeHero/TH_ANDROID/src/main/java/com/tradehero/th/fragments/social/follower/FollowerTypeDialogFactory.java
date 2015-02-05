package com.tradehero.th.fragments.social.follower;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.MessageType;
import rx.Observable;
import rx.android.widget.WidgetObservable;

public class FollowerTypeDialogFactory
{
    @NonNull public static Pair<Dialog, Observable<MessageType>> showHeroTypeDialog(@NonNull Activity activity)
    {
        LayoutInflater inflater = LayoutInflater.from(activity);
        LinearLayout expanded = (LinearLayout) inflater
                .inflate(R.layout.common_dialog_layout, null, false);
        ListView list = (ListView) expanded.findViewById(R.id.content_list);
        View header = inflater.inflate(R.layout.common_dialog_item_header_layout, null);
        TextView headerText = (TextView) header.findViewById(android.R.id.title);
        headerText.setText(R.string.broadcast_message_change_type_hint);
        list.addHeaderView(header, null, false);
        list.setAdapter(createMessageTypeAdapter(activity));
        return Pair.create(
                THDialog.showUpDialog(activity, expanded, null),
                WidgetObservable.itemClicks(list)
                .map(event -> (MessageType) event.parent().getItemAtPosition(event.position())));
    }

    @NonNull public static ArrayAdapter createMessageTypeAdapter(@NonNull Activity activity)
    {
        return new MessageTypeAdapter(
                activity,
                R.layout.common_dialog_item_layout,
                R.id.popup_text,
                MessageType.getBroadcastTypes());
    }
}
