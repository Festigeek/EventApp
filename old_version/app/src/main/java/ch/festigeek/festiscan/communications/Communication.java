package ch.festigeek.festiscan.communications;

import android.os.AsyncTask;

import java.net.CookieManager;

import ch.festigeek.festiscan.interfaces.ICallback;

public abstract class Communication<T> extends AsyncTask<Void, Void, T> {
    private ICallback<T> mCallback;
    private Exception mException;

    @Override
    protected T doInBackground(Void... params) {
        mException = null;
        return communication();
    }

    @Override
    protected void onPostExecute(T ret) {
        if (mException == null) {
            mCallback.success(ret);
        } else {
            mCallback.failure(mException);
        }
    }

    protected abstract T communication();

    protected void setCallback(ICallback<T> callback) {
        mCallback = callback;
    }

    protected void setException(Exception exception) {
        mException = exception;
    }
}