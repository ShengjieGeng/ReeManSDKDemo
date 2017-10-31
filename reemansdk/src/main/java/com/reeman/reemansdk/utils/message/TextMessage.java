package com.reeman.reemansdk.utils.message;

public class TextMessage implements ReemanMessage {

	private String desc = null;

	public int getMessagetype() {
		return ReemanMessage.MESSAGE_TEXT;
	}

	public String getMessagedesc() {
		return desc;
	}

	public void setMessagedesc(String desc) {
		this.desc = desc;
	}

}
