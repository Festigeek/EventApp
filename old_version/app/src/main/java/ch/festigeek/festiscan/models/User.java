package ch.festigeek.festiscan.models;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.StringEscapeUtils;
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
    private int mInscriptionId;
    private final Context mContext;
    private final String mUsername;
    private final String mFirstName;
    private final String mLastName;
    private final String mBirthdate;
    private String mTeamName;
    private String mPassword;
    private boolean mIsCheckedIn;
    private final List<Order> mOrders = new LinkedList<>();
    private final Map<String, String> map1 = new HashMap<>();
    private final Map<String, Integer> map = new HashMap<>();

    public User(Context context, JSONObject obj, UserActivity activity) throws JSONException {
        mContext = context;
        mId = obj.getInt(KEY_ID);
        mActivity = activity;
        mUsername = StringEscapeUtils.unescapeJava(obj.getString(KEY_USERNAME));
        mFirstName = StringEscapeUtils.unescapeJava(obj.getString(KEY_FIRSTNAME));
        mLastName = StringEscapeUtils.unescapeJava(obj.getString(KEY_LASTNAME));
        mBirthdate = obj.getString(KEY_BIRTHDATE);
        getOrders();
    }

    public int getId() {
        return mId;
    }

    public boolean isUnderage() {
        int year = Integer.parseInt(mBirthdate.split("-")[0]);
        int month = Integer.parseInt(mBirthdate.split("-")[1]);
        if (year >= 1999) {
            return true;
        } else if (year == 1998 && month >= 5) {
            return true;
        }
        return false;
    }

    public boolean isCheckedIn() {
        return mIsCheckedIn;
    }

    public int getInscriptionId() {
        return mInscriptionId;
    }

    public Map<String, Integer> getInscriptionMap() {
        return map;
    }

    public String getTeamName() {
        return mTeamName;
    }

    public String getPassword() {
        return mPassword;
    }

    public List<Order> getOrderedProducts() {
        return mOrders;
    }

    public String toString() {
        return mUsername + ((mFirstName == "" || mLastName == "") ? "" : " (" + mFirstName + " " + mLastName + ")");
    }

    private void getOrders() {
        new RequestGET(new ICallback<String>() {
            @Override
            public void success(String result) {
                try {
                    JSONObject obj = new JSONArray(result).getJSONObject(0);
                    JSONArray products = obj.getJSONArray(KEY_PRODUCTS);
                    setOrders(products);
                    JSONArray abstractProduct = obj.getJSONArray("abstract_products");
                    int abstractId = -1;
                    for (int i = 0; i < abstractProduct.length(); ++i) {
                        JSONObject o = abstractProduct.getJSONObject(i);
                        if (o.getString("abstract_products_type").contains("Inscription")) {
                            abstractId = o.getInt("id");
                        }
                    }
                    setInscriptions(abstractId, obj.getJSONArray(KEY_INSCRIPTIONS).getJSONObject(0));
                } catch (JSONException ex) {
                    Log.e(LOG_NAME, ex.getMessage());
                }
            }

            @Override
            public void failure(Exception ex) {
                Log.e(LOG_NAME, ex.getMessage());
            }
        }, mContext.getString(R.string.token), BASE_URL + USER_ORDERS + mId).execute();
    }

    private void setInscriptions(int abstractId, JSONObject inscriptions) throws JSONException {
        //mInscriptionId = inscriptions.getJSONObject(KEY_PIVOT).getInt(KEY_ABSTRACT_ID);
        mInscriptionId = abstractId;
        mTeamName = inscriptions.getString(KEY_TEAM_NAME);
        mIsCheckedIn = inscriptions.getJSONObject(KEY_PIVOT).getInt(KEY_USED) != 0;
        mPassword = inscriptions.getString(KEY_PASSWORD);
        map.put(KEY_USED, inscriptions.getJSONObject(KEY_PIVOT).getInt(KEY_USED));

        mActivity.displayUser();
    }

    private void setOrders(JSONArray products) throws JSONException{
        for (int i = 0; i < products.length(); ++i) {
            mOrders.add(new Order(products.getJSONObject(i)));
        }
    }
}
