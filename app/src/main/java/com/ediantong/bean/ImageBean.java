package com.ediantong.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageBean implements Parcelable {
    public long id;
    public String name;
    public String path;
    public boolean isSelected;

    public ImageBean(long id, String name, String path, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isSelected = isSelected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(path);
    }

    public static final Parcelable.Creator<ImageBean> CREATOR = new Parcelable.Creator<ImageBean>() {
        @Override
        public ImageBean createFromParcel(Parcel source) {
            return new ImageBean(source);
        }

        @Override
        public ImageBean[] newArray(int size) {
            return new ImageBean[size];
        }
    };

    private ImageBean(Parcel in) {
        id = in.readLong();
        name = in.readString();
        path = in.readString();
    }
}
