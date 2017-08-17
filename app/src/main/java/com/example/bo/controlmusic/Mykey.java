package com.example.bo.controlmusic;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author bo.
 * @Date 2017/5/15.
 * @desc
 */

public class Mykey implements Parcelable {

    protected Mykey (Parcel in) {
    }

    public static final Creator<Mykey> CREATOR = new Creator<Mykey> () {
        @Override public Mykey createFromParcel (Parcel in) {
            return new Mykey (in);
        }

        @Override public Mykey[] newArray (int size) {
            return new Mykey[size];
        }
    };

    @Override public int describeContents () {
        return 0;
    }

    @Override public void writeToParcel (Parcel parcel, int i) {
    }
}
