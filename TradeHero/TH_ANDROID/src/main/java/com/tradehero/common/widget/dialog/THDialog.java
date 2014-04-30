package com.tradehero.common.widget.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.*;
import android.widget.*;
import android.view.*;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.tradehero.th.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tradehero on 14-3-6.
 */
public class THDialog {

    public interface OnDialogItemClickListener {
        void onClick(int whichButton);
    }

    public interface DialogCallback {
        void setOnDismissCallback(DialogInterface listener);
    }

    public interface DialogInterface {
        void onDialogDismiss();
    }


    public static Dialog showCenterDialog(final Context context,
                                          String title,
                                          String message,
                                          String negativeButton,
                                          String positiveButton,
                                          android.content.DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder =  new AlertDialog.Builder(context).
                setMessage(message).
                setTitle(title).
                setPositiveButton(android.R.string.ok, onClickListener
                );
        if (negativeButton != null) {
            builder.setNegativeButton(negativeButton,onClickListener);
        }
        if(positiveButton != null) {
            builder.setPositiveButton(positiveButton,onClickListener);
        }
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    } 
    
    public static Dialog showUpDialog(final Context context,final int layoutRes) {
        final Dialog dlg = createDialog(context,R.style.TH_common_up_dialog,layoutRes);
        setDialogAttribute(dlg,null);
        dlg.show();
        return dlg;
    }

    public static Dialog showUpDialog(final Context context,final View contentView) {
        final Dialog dlg = createDialog(context,R.style.TH_common_up_dialog,contentView);
        setDialogAttribute(dlg,null);
        dlg.show();
        return dlg;
    }


    public static Dialog showUpDialog(final Context context,final int layoutRes,final DialogCallback callback) {
        final Dialog dlg = createDialog(context,R.style.TH_common_up_dialog,layoutRes);
        setDialogAttribute(dlg,null);
        if (callback != null) {
            callback.setOnDismissCallback(new DialogInterface() {
                @Override
                public void onDialogDismiss() {
                    dlg.dismiss();
                }
            });
        }
        dlg.show();
        return dlg;
    }

    public static Dialog showUpDialog(final Context context,final View contentView,final DialogCallback callback) {
        final Dialog dlg = createDialog(context,R.style.TH_common_up_dialog,contentView);
        setDialogAttribute(dlg,null);
        if (callback != null) {
            callback.setOnDismissCallback(new DialogInterface() {
                @Override
                public void onDialogDismiss() {
                    dlg.dismiss();
                }
            });
        }
        dlg.show();
        return dlg;
    }


    public static Dialog showUpDialog(final Context context, final String title, final String[] items, String exit, final OnDialogItemClickListener callback, android.content.DialogInterface.OnCancelListener cancelListener) {
        String cancel = null;//context.getString(android.R.string.cancel);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.common_dialog_layout, null);
        final Dialog dlg = createDialog(context,R.style.TH_common_up_dialog,layout);

        final ListView list = (ListView) layout.findViewById(R.id.content_list);
        AlertAdapter adapter = new AlertAdapter(context, title, items, exit, cancel);
        list.setAdapter(adapter);
        list.setDividerHeight(0);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!(title == null || title.equals("")) && position - 1 >= 0) {
                    callback.onClick(position - 1);
                    dlg.dismiss();
                    list.requestFocus();
                } else {
                    callback.onClick(position);
                    dlg.dismiss();
                    list.requestFocus();
                }

            }
        });
        setDialogAttribute(dlg,cancelListener);
        dlg.show();
        return dlg;
    }

    private static Dialog createDialog(final Context context,int style,int layoutRes) {
        final Dialog dlg = new Dialog(context, style);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(layoutRes, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        dlg.setContentView(layout);
        return dlg;
    }

    private static Dialog createDialog(final Context context,int style,View contentView) {
        final Dialog dlg = new Dialog(context, style);
        final int cFullFillWidth = 10000;
        contentView.setMinimumWidth(cFullFillWidth);
        dlg.setContentView(contentView);
        return dlg;
    }

    private static void setDialogAttribute(Dialog dlg, android.content.DialogInterface.OnCancelListener cancelListener){
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        if (cancelListener != null) {
            dlg.setOnCancelListener(cancelListener);
        }
    }


    static class AlertAdapter extends BaseAdapter {

        public static final int TYPE_BUTTON = 0;
        public static final int TYPE_TITLE = 1;
        public static final int TYPE_EXIT = 2;
        public static final int TYPE_CANCEL = 3;
        private List<String> items;
        private int[] types;
        // private boolean isSpecial = false;
        private boolean isTitle = false;
        // private boolean isExit = false;
        private Context context;

        public AlertAdapter(Context context, String title, String[] items, String exit, String cancel) {
            if (items == null || items.length == 0) {
                this.items = new ArrayList<String>();
            } else {
                ArrayList<String> c = new ArrayList<String>(items.length);
                c.addAll(Arrays.asList(items));
                this.items = c;
            }
            this.types = new int[this.items.size() + 3];
            this.context = context;
            if (title != null && !title.equals("")) {
                types[0] = TYPE_TITLE;
                this.isTitle = true;
                this.items.add(0, title);
            }

            if (exit != null && !exit.equals("")) {
                // this.isExit = true;
                types[this.items.size()] = TYPE_EXIT;
                this.items.add(exit);
            }

            if (cancel != null && !cancel.equals("")) {
                // this.isSpecial = true;
                types[this.items.size()] = TYPE_CANCEL;
                this.items.add(cancel);
            }
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean isEnabled(int position) {
            if (position == 0 && isTitle) {
                return false;
            } else {
                return super.isEnabled(position);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String textString = (String) getItem(position);
            ViewHolder holder;
            int type = types[position];
            if (convertView == null || ((ViewHolder) convertView.getTag()).type != type) {
                holder = new ViewHolder();
                if (type == TYPE_CANCEL) {
                    convertView = View.inflate(context, R.layout.common_dialog_cancel_layout, null);
                } else if (type == TYPE_BUTTON) {
                    convertView = View.inflate(context, R.layout.common_dialog_item_layout, null);
                } else if (type == TYPE_TITLE) {
                    convertView = View.inflate(context, R.layout.common_dialog_title_layout, null);
                } else if (type == TYPE_EXIT) {
                    convertView = View.inflate(context, R.layout.common_dialog_cancel_layout, null);
                }

                // holder.view = (LinearLayout) convertView.findViewById(R.id.popup_layout);
                holder.text = (TextView) convertView.findViewById(R.id.popup_text);
                holder.type = type;

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(textString);
            return convertView;
        }

        static class ViewHolder {
            // LinearLayout view;
            TextView text;
            int type;
        }
}
}
