package ch.festigeek.festiscan.models;

import android.content.Context;
import android.util.Log;

import ch.festigeek.festiscan.utils.Utilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.festigeek.festiscan.R;
import ch.festigeek.festiscan.activities.UserActivity;
import ch.festigeek.festiscan.communications.RequestGET;
import ch.festigeek.festiscan.interfaces.ICallback;
import ch.festigeek.festiscan.interfaces.IConstant;
import ch.festigeek.festiscan.interfaces.IKeys;
import ch.festigeek.festiscan.interfaces.IURL;

public class User implements IConstant, IKeys, IURL {

    private final int mId;
    private final UserActivity mActivity;
    private int mOrderId;
    private int mProductId;
    private final Context mContext;
    private final String mUsername;
    private final String mFirstName;
    private final String mLastName;
    private final String mBirthdate;
    private String mTeamName;
    private boolean mIsCheckedIn;
    private final List<Order> mOrders = new LinkedList<>();
    private final List<Order> mOrdersForConsume = new LinkedList<>();
    private final Map<String, String> map1 = new HashMap<>();
    private final Map<String, Integer> map = new HashMap<>();

    public User(Context context, JSONObject obj, UserActivity activity) throws JSONException {
        mContext = context;
        mId = obj.getInt(KEY_ID);
        mActivity = activity;
        mUsername = obj.getString(KEY_USERNAME);
        mFirstName = obj.getString(KEY_FIRSTNAME);
        mLastName = obj.getString(KEY_LASTNAME);
        mBirthdate = obj.getString(KEY_BIRTHDATE);
        getOrders();
    }

    public int getId() {
        return mId;
    }

    public boolean isUnderage() {
        int year = Integer.parseInt(mBirthdate.split("-")[0]);
        int month = Integer.parseInt(mBirthdate.split("-")[1]);
        if (year >= 2000) {
            return true;
        } else if (year == 1999 && month >= 5) {
            return true;
        }
        return false;
    }

    public boolean isCheckedIn() {
        return mIsCheckedIn;
    }

    public int getOrderId() {
        return mOrderId;
    }

    public int getProductId() { return mProductId; }

    public Map<String, Integer> getInscriptionMap() {
        return map;
    }

    public String getTeamName() {
        return mTeamName;
    }

    public List<Order> getOrderedProducts() {
        return mOrders;
    }

    public String toString() {
        return mUsername + ((mFirstName == "" || mLastName == "") ? "(id:" + mId + ")" : " (" + mFirstName + " " + mLastName + ", id:" + mId + ")");
    }

    private void getOrders() {
        new RequestGET(new ICallback<String>() {
            @Override
            public void success(String result) {
                Log.e(LOG_NAME, result);
                try {
                    JSONObject obj = new JSONArray(result).getJSONObject(0);
                    JSONArray products = obj.getJSONArray(KEY_PRODUCTS);
                    setOrders(products);
                    int orderId = 0;
                    int productId = 0;
                    int consume = 0;
                    for (Order o: mOrdersForConsume) {
                        if (o.getProductType() == 1) {
                            orderId = o.getOrderId();
                            productId = o.getProductId();
                            consume = o.isUsed();
                        }
                    }
                    String team;
                    try {
                        team = obj.getJSONObject(KEY_TEAM).getString(KEY_NAME);
                    } catch (Exception e) {
                        team = "";
                    }
                    setInscriptions(orderId, productId, team, consume);
                } catch (JSONException ex) {
                    Log.e(LOG_NAME, ex.getMessage());
                }
            }

            @Override
            public void failure(Exception ex) {
                Log.e(LOG_NAME, ex.getMessage());
            }
        }, Utilities.getFromSharedPreferences(mContext, "token"), BASE_URL + USER_BY_ID + mId + "/orders").execute();
    }

    private void setInscriptions(int orderId, int productId, String team, int consume) throws JSONException {
        mOrderId = orderId;
        mProductId = productId;
        mTeamName = team;
        mIsCheckedIn = consume != 0;
        map.put(KEY_USED, mIsCheckedIn ? 1 : 0);

        mActivity.displayUser();
    }

    private void setOrders(JSONArray products) throws JSONException{
        for (int i = 0; i < products.length(); ++i) {
            Order o= new Order(products.getJSONObject(i));
            if (o.getProductType() != 1) {
                mOrders.add(o);
            }
            mOrdersForConsume.add(o);
        }
    }
}
