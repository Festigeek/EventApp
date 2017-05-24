package ch.festigeek.festiscan.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import ch.festigeek.festiscan.communications.RequestPATCH;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.festigeek.festiscan.R;
import ch.festigeek.festiscan.adapters.ListProductAdapter;
import ch.festigeek.festiscan.communications.RequestGET;
import ch.festigeek.festiscan.communications.RequestPUT;
import ch.festigeek.festiscan.interfaces.ICallback;
import ch.festigeek.festiscan.interfaces.IConstant;
import ch.festigeek.festiscan.interfaces.IKeys;
import ch.festigeek.festiscan.interfaces.IURL;
import ch.festigeek.festiscan.models.Order;
import ch.festigeek.festiscan.models.User;
import ch.festigeek.festiscan.utils.Utilities;

public class UserActivity extends AppCompatActivity implements IConstant, IKeys, IURL {

    private int mId;
    private User mUser;

    private CheckBox mCheckedIn;
    private TextView mParentalAuthorization;
    private TextView mUsername;
    private TextView mTeamName;

    private ListProductAdapter mAdapter;
    private List<Order> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mCheckedIn = (CheckBox) findViewById(R.id.is_checked_in);
        mCheckedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckedIn.isChecked()) {
                    mCheckedIn.setBackgroundColor(getResources().getColor(R.color.softGreen));
                    Utilities.initProgressDialog(UserActivity.this, getString(R.string.dialog_check_in));
                    checkIn(1);
                } else {
                    mCheckedIn.setBackgroundColor(getResources().getColor(R.color.softRed));
                    Utilities.initProgressDialog(UserActivity.this, getString(R.string.dialog_check_out));
                    checkIn(0);
                }
            }
        });
        mParentalAuthorization = (TextView) findViewById(R.id.parental_authorization);
        mUsername = (TextView) findViewById(R.id.user_name);
        mTeamName = (TextView) findViewById(R.id.team_name);

        mId = getIntent().getExtras().getInt(KEY_ID);

        mList = new LinkedList<>();
        mAdapter = new ListProductAdapter(this, R.layout.product_list, mList);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
        init();
    }

    private void checkIn(int value) {
        Map<String, Integer> map = mUser.getInscriptionMap();
        map.put(KEY_USED, value);
        String url = BASE_URL + ORDERS + mUser.getOrderId() + PRODUCTS + mUser.getProductId();
        Log.e(LOG_NAME, url + "-" + value);
        new RequestPATCH(new ICallback<String>() {
            @Override
            public void success(String result) {
                Utilities.dismissProgressDialog();
                Log.e(LOG_NAME, result);
            }

            @Override
            public void failure(Exception ex) {
                Log.e(LOG_NAME, ex.getMessage());
                mCheckedIn.setChecked(false);
                mCheckedIn.setBackgroundColor(getResources().getColor(R.color.softRed));
                Utilities.dismissProgressDialog();
            }
        }, Utilities.getFromSharedPreferences(this, "token"), url, "consume", value).execute();
    }

    private void init() {
        Utilities.initProgressDialog(this, getString(R.string.dialog_loading_user));
        new RequestGET(new ICallback<String>() {
            @Override
            public void success(String result) {
                Utilities.dismissProgressDialog();
                try {
                    JSONObject obj = new JSONObject(result);
                    mUser = new User(getApplicationContext(), obj, UserActivity.this);
                } catch (JSONException ex) {
                    Log.e(LOG_NAME, ex.getMessage());
                }
            }

            @Override
            public void failure(Exception ex) {
                Log.e(LOG_NAME, ex.getMessage());
                Utilities.dismissProgressDialog();
            }
        }, Utilities.getFromSharedPreferences(this, "token"), BASE_URL + USER_BY_ID + mId).execute();
    }

    public void displayUser() {
        if (mUser.isCheckedIn()) {
            mCheckedIn.setBackgroundColor(getResources().getColor(R.color.softGreen));
            mCheckedIn.setChecked(true);
        }
        if (mUser.isUnderage()) {
            mParentalAuthorization.setText(getString(R.string.do_need_parental_authorization));
            mParentalAuthorization.setBackgroundColor(getResources().getColor(R.color.softRed));
        }
        mUsername.setText(mUser.toString());
        mTeamName.setText(mUser.getTeamName());

        for (Order o : mUser.getOrderedProducts()) {
            mList.add(o);
        }
        mAdapter.notifyDataSetChanged();

        Utilities.dismissProgressDialog();
    }

}
