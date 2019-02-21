package cn.ommiao.socketdemo.socket.message;

import android.os.Parcel;
import android.os.Parcelable;

public class WrapperBody implements Parcelable{

    private int errorcode;
    private String errortext;

    public WrapperBody() {
    }

    public WrapperBody(int errorcode, String errortext) {
        this.errorcode = errorcode;
        this.errortext = errortext;
    }

    protected WrapperBody(Parcel in) {
        errorcode = in.readInt();
        errortext = in.readString();
    }

    public static final Parcelable.Creator<WrapperBody> CREATOR = new Parcelable.Creator<WrapperBody>() {
        @Override
        public WrapperBody createFromParcel(Parcel in) {
            return new WrapperBody(in);
        }

        @Override
        public WrapperBody[] newArray(int size) {
            return new WrapperBody[size];
        }
    };

    public int getErrorcode() {
        return errorcode;
    }

    public String getErrortext() {
        return errortext;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public void setErrortext(String errortext) {
        this.errortext = errortext;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(errorcode);
        dest.writeString(errortext);
    }

}
