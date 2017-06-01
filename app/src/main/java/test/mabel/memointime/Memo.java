package test.mabel.memointime;

import java.util.Date;
import java.util.UUID;


public class Memo {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mContacts;


    public Memo() {
        this(UUID.randomUUID());
    }

    public Memo(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public UUID getId() {

        return mId;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getContacts() {
        return mContacts;
    }

    public void setContacts(String contacts) {
        mContacts = contacts;
    }

    // 获取文件名
    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
