package com.reeman.reemansdk.utils.message;

public class PicMessage implements ReemanMessage {

    String picPath = "";

    public void setMessagedesc(String picPath) {
        this.picPath = picPath;
    }

    public int getMessagetype() {
        return ReemanMessage.MESSAGE_PICTURE;
    }

    public String getMessagedesc() {
        return picPath;
    }

}
