package ch.festigeek.festiscan.models;

import org.json.JSONException;
import org.json.JSONObject;

import ch.festigeek.festiscan.interfaces.IKeys;

public class Order implements IKeys {

    private final String mDescription;
    private final int mId;
    private final int mOrderId;
    private final int mProductId;
    private final int mProductType;
    private final int mAmount;
    private final int mIsUsed;

    public Order(JSONObject order) throws JSONException {
        mDescription = order.getString(KEY_NAME);
        mId = order.getInt(KEY_ID);
        mOrderId = order.getJSONObject(KEY_PIVOT).getInt(KEY_ORDER_ID);
        mProductId = order.getInt(KEY_ID);
        mAmount = order.getJSONObject(KEY_PIVOT).getInt(KEY_AMOUNT);
        mProductType = order.getInt(KEY_PRODUCT_TYPE_ID);
        mIsUsed = order.getJSONObject(KEY_PIVOT).getInt(KEY_CONSUME);
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

    public int isUsed() {
        return mIsUsed;
    }

    public int getId() { return mId; }

    public int getProductType() { return mProductType; }

    public int getAmount() { return mAmount; }
}
