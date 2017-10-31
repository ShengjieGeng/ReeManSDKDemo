package com.reeman.reemansdk.utils.message;

public class VideooMessage implements ReemanMessage {

	/***
	 * 视屏的路径
	 */
	private String videoPath = "";

	public void setMessagedesc(String videoPath) {
		this.videoPath = videoPath;
	}

	public int getMessagetype() {
		return ReemanMessage.MESSAGE_VIDEO;
	}

	public String getMessagedesc() {
		return videoPath;
	}

}
