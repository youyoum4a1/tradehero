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
import com.tradehero.th.rx.view.list.ListViewObservable;
import javax.inject.Inject;
import rx.Observable;

public class FollowerTypeDialogFactory
{
    //<editor-fold desc="Constructors">
    @Inject public FollowerTypeDialogFactory()
    {
        super();
    }
    //</editor-fold>

    @NonNull protected Pair<Dialog, Observable<MessageType>> showHeroTypeDialog(@NonNull Activity activity)
    {
        LayoutInflater inflater = LayoutInflater.from(activity);
        LinearLayout expanded = (LinearLayout) inflater
                .inflate(R.layout.common_dialog_layout, null, false);
        ListView list = (ListView) expanded.findViewById(R.id.content_list);
        list.setAdapter(createMessageTypeAdapter(activity));
        View header = inflater.inflate(R.layout.common_dialog_item_header_layout, null);
        TextView headerText = (TextView) header.findViewById(android.R.id.title);
        headerText.setText(R.string.broadcast_message_change_type_hint);
        list.addHeaderView(header, null, false);
        return Pair.create(
                THDialog.showUpDialog(activity, expanded, null),
                ListViewObservable.itemClicks(list)
                        .map(object -> (MessageType) object));
    }

    @NonNull protected ArrayAdapter createMessageTypeAdapter(@NonNull Activity activity)
    {
        return new MessageTypeAdapter(
                activity,
                R.layout.common_dialog_item_layout,
                R.id.popup_text,
                MessageType.getShowingTypes());
    }
}
