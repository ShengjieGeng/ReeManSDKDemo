package com.reeman.reemansdk.utils.message;

/***
 * 封装消息类型
 * 
 * @author Administrator
 *
 */
public interface ReemanMessage {

	public static final int MESSAGE_TEXT = 0; // 文字消息
	public static final int MESSAGE_VIDEO = 1; // 音频消息
	public static final int MESSAGE_SOUND = 2; // 声音消息
	public static final int MESSAGE_FILE = 3; // 文件消息
	public static final int MESSAGE_PICTURE = 4; // 图片消息
	public int MESSAGE_STYLE = 0;

	/***
	 * 把消息内容传递进去
	 * 
	 * @param desc
	 */
	public void setMessagedesc(String desc);

	/***
	 * 消息类型
	 * 
	 * @return
	 */
	int getMessagetype();

	/***
	 * 获取消息秒速 文字消息传递文字,图片.音频传递路径.
	 */
	String getMessagedesc();
}
