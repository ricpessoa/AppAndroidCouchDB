package mei.ricardo.pessoa.app.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by rpessoa on 06/05/14.
 */
public class DialogFragmentYesNoOk extends DialogFragment {
    private static String TAG = DialogFragmentYesNoOk.class.getName();
    private Context mContext;
    private String msg;
    private int type; //type 1 ok and type 2 = ok Cancel
    private String positive, negative;
    private boolean backToPreviousActivity = false;

    public DialogFragmentYesNoOk(Context mContext) {
        this.mContext = mContext;
    }

    public DialogFragmentYesNoOk(Context mContext, String msg) {
        this.mContext = mContext;
        this.msg = msg;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public void setType(int ntype) {
        this.type = ntype;
    }

    public void setPositiveAndNegative(String positive, String negative) {
        this.positive = positive;
        this.negative = negative;
    }

    public void setBackToPreviousActivity(boolean back) {

        this.backToPreviousActivity = back;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (this.type == 2) {
            final boolean back = this.backToPreviousActivity;

            builder.setMessage(this.msg)
                    .setPositiveButton(this.positive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(mContext, "Press positive", Toast.LENGTH_SHORT).show();
                            if (back) {
                                getActivity().finish(); // finish actual activity
                            }
                        }
                    })
                    .setNegativeButton(this.negative, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(mContext, "Press negative", Toast.LENGTH_SHORT).show();

                        }
                    });
        } else {
            builder.setMessage(this.msg)
                    .setPositiveButton(this.positive, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(mContext, "Press positive", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        // Create the AlertDialog object and return it
        return builder.create();
    }
}