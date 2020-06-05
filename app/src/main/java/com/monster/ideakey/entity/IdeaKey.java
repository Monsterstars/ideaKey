package com.monster.ideakey.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class IdeaKey {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @ColumnInfo(name = "key_name")
    private String keyName;

    @ColumnInfo(name = "user_name")
    private String userName;

    @ColumnInfo(name = "user_password_one")
    private String userPasswordOne;

    @ColumnInfo(name = "user_password_two")
    private String userPasswordTwo;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "detail")
    private String detail;

    public IdeaKey(String keyName, String userName, String userPasswordOne, String userPasswordTwo, String url, String detail) {
        this.keyName = keyName;
        this.userName = userName;
        this.userPasswordOne = userPasswordOne;
        this.userPasswordTwo = userPasswordTwo;
        this.url = url;
        this.detail = detail;
    }



    @Override
    public String toString() {
        return "IdeaKey{" +
                "id=" + id +
                ", keyName='" + keyName + '\'' +
                ", userName='" + userName + '\'' +
                ", userPasswordOne='" + userPasswordOne + '\'' +
                ", userPasswordTwo='" + userPasswordTwo + '\'' +
                ", url='" + url + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPasswordOne() {
        return userPasswordOne;
    }

    public void setUserPasswordOne(String userPasswordOne) {
        this.userPasswordOne = userPasswordOne;
    }

    public String getUserPasswordTwo() {
        return userPasswordTwo;
    }

    public void setUserPasswordTwo(String userPasswordTwo) {
        this.userPasswordTwo = userPasswordTwo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
