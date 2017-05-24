package ch.festigeek.festiscan.models;

import org.apache.commons.lang3.StringEscapeUtils;

public class SimpleUser implements Comparable<SimpleUser> {

    private final int mId;
    private final String mUsername;
    private boolean mIsCheckedIn;

    public SimpleUser(final int id, final String username, final int used) {
        mId = id;
        mUsername = StringEscapeUtils.unescapeJava(username);
        mIsCheckedIn = used != 0;
    }

    public int getId() {
        return mId;
    }

    public String getUsername() {
        return mUsername;
    }

    public boolean isCheckedIn() {
        return mIsCheckedIn;
    }

    public boolean contains(String s) {
        return s == null || s.isEmpty() || mUsername.toLowerCase().contains(s.toLowerCase());
    }

    public int compareTo(SimpleUser su) {
        return mUsername.compareTo(su.getUsername());
    }
}
