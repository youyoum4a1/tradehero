package com.tradehero.common.widget.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.*;
import com.tradehero.th.R;

public class THDialog
{

    public interface OnDialogItemClickListener
    {
        void onClick(int whichButton);
    }

    public interface DialogCallback
    {
        void setOnDismissCallback(DialogInterface listener);
    }

    public interface DialogInterface
    {
        void onDialogDismiss();
    }


    public static Dialog showCenterDialog(final Context context,
            String title,
            String message,
            String negativeButton,
            String positiveButton,
            android.content.DialogInterface.OnClickListener onClickListener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).
                setMessage(message).
                setTitle(title).
                setPositiveButton(android.R.string.ok, onClickListener
                );
        if (negativeButton != null)
        {
            builder.setNegativeButton(negativeButton, onClickListener);
        }
        if (positiveButton != null)
        {
            builder.setPositiveButton(positiveButton, onClickListener);
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static Dialog showUpDialog(final Context context, final int layoutRes)
    {
        final Dialog dlg = createDialog(context, R.style.TH_common_up_dialog, layoutRes);
        setDialogAttribute(dlg, null);
        dlg.show();
        return dlg;
    }

    public static Dialog showUpDialog(final Context context, final View contentView)
    {
        final Dialog dlg = createDialog(context, R.style.TH_common_up_dialog, contentView);
        setDialogAttribute(dlg, null);
        dlg.show();
        return dlg;
    }


    public static Dialog showUpDialog(final Context context, final View contentView, final DialogCallback callback)
    {
        final Dialog dlg = createDialog(context, R.style.TH_common_up_dialog, contentView);
        setDialogAttribute(dlg, null);
        if (callback != null)
        {
            callback.setOnDismissCallback(new DialogInterface()
            {
                @Override
                public void onDialogDismiss()
                {
                    dlg.dismiss();
                }
            });
        }
        dlg.show();
        return dlg;
    }

    private static Dialog createDialog(final Context context, int style, int layoutRes)
    {
        final Dialog dlg = new Dialog(context, style);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(layoutRes, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        dlg.setContentView(layout);
        return dlg;
    }

    private static Dialog createDialog(final Context context, int style, View contentView)
    {
        final Dialog dlg = new Dialog(context, style);
        final int cFullFillWidth = 10000;
        contentView.setMinimumWidth(cFullFillWidth);
        dlg.setContentView(contentView);
        return dlg;
    }

    private static void setDialogAttribute(Dialog dlg, android.content.DialogInterface.OnCancelListener cancelListener)
    {
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        if (cancelListener != null)
        {
            dlg.setOnCancelListener(cancelListener);
        }
    }
}
