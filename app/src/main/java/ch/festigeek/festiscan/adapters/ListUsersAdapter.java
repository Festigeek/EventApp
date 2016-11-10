package ch.festigeek.festiscan.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.festigeek.festiscan.R;
import ch.festigeek.festiscan.activities.UserActivity;
import ch.festigeek.festiscan.interfaces.IKeys;
import ch.festigeek.festiscan.models.SimpleUser;

public class ListUsersAdapter extends ArrayAdapter<SimpleUser> implements Filterable, IKeys {

    private List<SimpleUser> mAllUsers;
    private List<SimpleUser> mFilteredUsers;

    public ListUsersAdapter(Context context, int resource, List<SimpleUser> allUsers) {
        super(context, resource, allUsers);
        mAllUsers = allUsers;
        mFilteredUsers = allUsers;
    }

    @Override
    public int getCount() {
        return mFilteredUsers.size();
    }

    @Override
    public SimpleUser getItem(int position) {
        return mFilteredUsers.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_user_list, parent, false);
        }

        final SimpleUser user = getItem(position);
        TextView username = (TextView) convertView.findViewById(R.id.item_username);
        if (username != null) {
            username.setText(user.getUsername());
        }

        LinearLayout line = (LinearLayout) convertView.findViewById(R.id.item_line);
        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                intent.putExtra(KEY_ID, user.getId());
                getContext().startActivity(intent);
            }
        });
        if (user.isCheckedIn()) {
            line.setBackgroundColor(getContext().getResources().getColor(R.color.softGreen));
        } else {
            line.setBackgroundColor(getContext().getResources().getColor(R.color.softRed));
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<SimpleUser> filteredUsers;
                if (constraint != null && constraint.length() != 0) {
                    filteredUsers = new ArrayList<>();
                    for (SimpleUser user : mAllUsers) {
                        if (user.contains(constraint.toString())) {
                            filteredUsers.add(user);
                        }
                    }
                } else {
                    filteredUsers = mAllUsers;
                }
                results.count = filteredUsers.size();
                results.values = filteredUsers;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredUsers = (List<SimpleUser>) results.values;
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    notifyDataSetChanged();
                }
            }
        };
    }
}
