package com.hinnka.devtools.ui.anr;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: zhoubinjia
 * date: 2017/1/4
 */
public class ANR implements Parcelable {
    private String pid;
    private String date;
    private String time;
    private String packageName;
    private String log = "";

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pid);
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeString(this.packageName);
        dest.writeString(this.log);
    }

    ANR() {
    }

    protected ANR(Parcel in) {
        this.pid = in.readString();
        this.date = in.readString();
        this.time = in.readString();
        this.packageName = in.readString();
        this.log = in.readString();
    }

    public static final Creator<ANR> CREATOR = new Creator<ANR>() {
        @Override
        public ANR createFromParcel(Parcel source) {
            return new ANR(source);
        }

        @Override
        public ANR[] newArray(int size) {
            return new ANR[size];
        }
    };
}
