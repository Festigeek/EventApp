package ch.festigeek.festiscan.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ch.festigeek.festiscan.R;
import ch.festigeek.festiscan.activities.UserActivity;
import ch.festigeek.festiscan.communications.RequestPATCH;
import ch.festigeek.festiscan.interfaces.ICallback;
import ch.festigeek.festiscan.interfaces.IConstant;
import ch.festigeek.festiscan.interfaces.IURL;
import ch.festigeek.festiscan.models.Order;
import ch.festigeek.festiscan.utils.Utilities;

public class ListProductAdapter extends ArrayAdapter<Order> implements IConstant, IURL {

    private UserActivity mActivity;
    private List<Order> mList;

    public ListProductAdapter(UserActivity a, Context context, int resource, List<Order> list) {
        super(context, resource, list);
        mActivity = a;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Order getItem(int position) {
        return mList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_list, parent, false);
        }

        final Order order = mList.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.product_text);
        String str = order.getDescription() + " (" + order.isUsed() + "/" + order.getAmount() + ")";
        name.setText(str);

        final Button consume = (Button) convertView.findViewById(R.id.button_consume);
        consume.setEnabled(order.isUsed() != order.getAmount());
        final Button cancel = (Button) convertView.findViewById(R.id.button_cancel);
        cancel.setEnabled(order.isUsed() != 0);
        consume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = BASE_URL + ORDERS + order.getOrderId() + PRODUCTS + order.getProductId();
                Log.e(LOG_NAME, "value " + order.isUsed());
                new RequestPATCH(new ICallback<String>() {
                    @Override
                    public void success(String result) {
                        Log.e(LOG_NAME, result);
                        order.use(order.isUsed() + 1);
                        mActivity.init();
                    }

                    @Override
                    public void failure(Exception ex) {
                        Log.e(LOG_NAME, ex.getMessage());
                    }
                }, Utilities.getFromSharedPreferences(getContext(), "token"), url, "consume", order.isUsed() + 1).execute();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = BASE_URL + ORDERS + order.getOrderId() + PRODUCTS + order.getProductId();
                Log.e(LOG_NAME, "value " + order.isUsed());
                new RequestPATCH(new ICallback<String>() {
                    @Override
                    public void success(String result) {
                        Log.e(LOG_NAME, result);
                        order.use(order.isUsed() - 1);
                        mActivity.init();
                    }

                    @Override
                    public void failure(Exception ex) {
                        Log.e(LOG_NAME, ex.getMessage());
                    }
                }, Utilities.getFromSharedPreferences(getContext(), "token"), url, "consume", order.isUsed() - 1).execute();
            }
        });

        return convertView;
    }
}
