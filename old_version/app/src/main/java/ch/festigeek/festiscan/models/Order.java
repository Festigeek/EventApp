package ch.festigeek.festiscan.models;

import org.json.JSONException;
import org.json.JSONObject;

import ch.festigeek.festiscan.interfaces.IKeys;

public class Order implements IKeys {

    private final String mDescription;
    private final int mOrderId;
    private final int mProductId;
    private final boolean mIsUserd;

    public Order(JSONObject order) throws JSONException {
        mDescription = order.getString(KEY_DESCRIPTION);
        mOrderId = order.getJSONObject(KEY_PIVOT).getInt(KEY_ORDER_ID);
        mProductId = order.getJSONObject(KEY_PIVOT).getInt(KEY_ABSTRACT_ID);
        mIsUserd = order.getJSONObject(KEY_PIVOT).getInt(KEY_USED) != 0;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getOrderId() {
        return mOrderId;
    }

    public int getProductId() {
        return mProductId;
    }

    public boolean isUsed() {
        return mIsUserd;
    }
}
