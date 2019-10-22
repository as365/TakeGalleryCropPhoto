package com.ediantong.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;


public class CropBean implements Parcelable {
    public Uri originUri;
    public int width;
    public int height;
    public String folder_name;
    public boolean isSaveRectangle;//是否保存矩形区域内的图片


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.originUri, flags);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.folder_name);
        dest.writeByte(this.isSaveRectangle ? (byte) 1 : (byte) 0);
    }

    public CropBean() {
    }

    protected CropBean(Parcel in) {
        this.originUri = in.readParcelable(Uri.class.getClassLoader());
        this.width = in.readInt();
        this.height = in.readInt();
        this.folder_name = in.readString();
        this.isSaveRectangle = in.readByte() != 0;
    }

    public static final Creator<CropBean> CREATOR = new Creator<CropBean>() {
        @Override
        public CropBean createFromParcel(Parcel source) {
            return new CropBean(source);
        }

        @Override
        public CropBean[] newArray(int size) {
            return new CropBean[size];
        }
    };
}
