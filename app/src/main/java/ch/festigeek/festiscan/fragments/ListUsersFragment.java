package ch.festigeek.festiscan.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ch.festigeek.festiscan.R;
import ch.festigeek.festiscan.adapters.ListUsersAdapter;
import ch.festigeek.festiscan.communications.RequestGET;
import ch.festigeek.festiscan.interfaces.ICallback;
import ch.festigeek.festiscan.interfaces.IConstant;
import ch.festigeek.festiscan.interfaces.IKeys;
import ch.festigeek.festiscan.interfaces.IURL;
import ch.festigeek.festiscan.models.SimpleUser;

public class ListUsersFragment extends Fragment implements IConstant, IKeys, IURL {

    private ListUsersAdapter mAdapter;
    private List<SimpleUser> mList;
    private EditText mFilter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list_users, container, false);
        mFilter = (EditText) v.findViewById(R.id.filter);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mFilter.setText("");
        init();
    }

    private void init() {
        mList = new LinkedList<>();
        mAdapter = new ListUsersAdapter(getActivity(), R.layout.simple_user_list, mList);
        mFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* Nothing to do here ! */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* Nothing to do here ! */
            }

            @Override
            public void afterTextChanged(Editable s) {
                mAdapter.getFilter().filter(mFilter.getText().toString());
            }
        });
        ListView listView = (ListView) getActivity().findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
        listView.setEmptyView(getActivity().findViewById(R.id.placeholder));
        loadSimpleUsers();
    }

    private void loadSimpleUsers() {
        new RequestGET(new ICallback<String>() {
            @Override
            public void success(String result) {
                try {
                    JSONArray arr = new JSONArray(result);
                    for (int i = 0; i < arr.length(); ++i) {
                        JSONObject o = arr.getJSONObject(i);
                        if (o.getJSONArray(KEY_INSCRIPTIONS).length() > 0) {
                            JSONArray inscriptions = o.getJSONArray(KEY_INSCRIPTIONS);
                            int used = inscriptions.getJSONObject(0).getInt(KEY_USED);
                            SimpleUser user = new SimpleUser(o.getInt(KEY_ID), o.getString(KEY_USERNAME), used);
                            mList.add(user);
                        }
                    }
                    Collections.sort(mList);
                } catch (JSONException ex) {
                    Log.e(LOG_NAME, ex.getMessage());
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(Exception ex) {
                Log.e(LOG_NAME, ex.getMessage());
            }
        }, getActivity().getString(R.string.token), BASE_URL + ALL_USERS).execute();
    }
}
