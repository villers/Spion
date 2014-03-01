/**
 * PostData.java
 * 
 * Value Object
 */
package com.ghota.spi0n.model;


import android.os.Parcel;
import android.os.Parcelable;

public class PostData implements Parcelable {
    public String postGuid;
    public String postSlug;
    public String postUrl;
    public String postTitle;
    public String postContent;
    public String postExcerpt;
    public String postDate;
    public String postCategory;
    public String postComment;
	public String postThumbUrl;

    public PostData(){}

    public PostData(String postGuid, String postSlug, String postUrl, String postTitle, String postContent, String postExcerpt, String postDate, String postCategory, String postComment, String postThumbUrl){
        super();
        this.postGuid = postGuid;
        this.postSlug = postSlug;
        this.postUrl = postUrl;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postExcerpt = postExcerpt;
        this.postDate = postDate;
        this.postCategory = postCategory;
        this.postComment = postComment;
        this.postThumbUrl = postThumbUrl;
    }

    public PostData(Parcel in) {
        this.postGuid = in.readString();
        this.postSlug = in.readString();
        this.postUrl = in.readString();
        this.postTitle = in.readString();
        this.postContent = in.readString();
        this.postExcerpt = in.readString();
        this.postDate = in.readString();
        this.postCategory = in.readString();
        this.postComment = in.readString();
        this.postThumbUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(postGuid);
        parcel.writeString(postSlug);
        parcel.writeString(postUrl);
        parcel.writeString(postTitle);
        parcel.writeString(postContent);
        parcel.writeString(postExcerpt);
        parcel.writeString(postDate);
        parcel.writeString(postCategory);
        parcel.writeString(postComment);
        parcel.writeString(postThumbUrl);

    }

    public static final Parcelable.Creator<PostData> CREATOR = new Parcelable.Creator<PostData>() {

        @Override
        public PostData createFromParcel(Parcel source)
        {
            return new PostData(source);
        }

        @Override
        public PostData[] newArray(int size)
        {
            return new PostData[size];
        }
    };
}