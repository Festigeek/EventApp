package ch.festigeek.festiscan.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import ch.festigeek.festiscan.R;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.MODE_WORLD_READABLE;

public final class Utilities {

    private static boolean mIsAlreadyInitiated = false;
    private static ProgressDialog mProgressDialog;

    private Utilities(){}

    public static synchronized void initProgressDialog(Context context, String message) {
        if (!mIsAlreadyInitiated) {
            mIsAlreadyInitiated = true;
            mProgressDialog = ProgressDialog.show(context, context.getString(R.string.dialog_please_wait), message, true);
        }
    }

    public static synchronized void dismissProgressDialog() {
        if (mIsAlreadyInitiated) {
            mProgressDialog.dismiss();
            mIsAlreadyInitiated = false;
        }
    }

    public static synchronized void closeKeyboardWhenNecessary(Activity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static synchronized void putToSharedPreferences(Context c, String key, String value) {
        SharedPreferences.Editor editor = c.getSharedPreferences("FestiScan", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static synchronized String getFromSharedPreferences(Context c, String key) {
        SharedPreferences prefs = c.getSharedPreferences("FestiScan", MODE_PRIVATE);
        return prefs.getString(key, null);
    }
}
