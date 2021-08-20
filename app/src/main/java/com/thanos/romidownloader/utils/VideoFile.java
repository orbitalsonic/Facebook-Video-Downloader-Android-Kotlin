package com.thanos.romidownloader.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoFile implements Parcelable {
    private long id = -1;
    private String filename = "";
    private String path = "";
    private int progress = 0;
    private String totalSize = "";
    private String completeSize = "";
    private String addedDate="";
    private String duration="";
    private boolean isSelected = false;
    public VideoFile() {

    }
    protected VideoFile(Parcel in) {
        id = in.readInt();
        filename = in.readString();
        path = in.readString();
        progress = in.readInt();
        totalSize = in.readString();
        completeSize = in.readString();
        duration = in.readString();
        addedDate = in.readString();
    }

    public static final Creator<VideoFile> CREATOR = new Creator<VideoFile>() {
        @Override
        public VideoFile createFromParcel(Parcel in) {
            return new VideoFile(in);
        }

        @Override
        public VideoFile[] newArray(int size) {
            return new VideoFile[size];
        }
    };

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    public String getCompleteSize() {
        return completeSize;
    }

    public void setCompleteSize(String completeSize) {
        this.completeSize = completeSize;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(id);
        dest.writeString(filename);
        dest.writeString(path);
        dest.writeInt(progress);
        dest.writeString(totalSize);
        dest.writeString(completeSize);
        dest.writeString(addedDate);
        dest.writeString(duration);
    }
}
