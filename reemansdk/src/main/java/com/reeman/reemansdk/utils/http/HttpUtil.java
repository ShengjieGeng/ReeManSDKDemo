package com.reeman.reemansdk.utils.http;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求工具类
 * 
 * @author David
 * 
 */
public class HttpUtil {

	/**
	 * 请求Json字符串，成功返回Json字符串，失败返回null
	 *
	 * @param path
	 * @return
	 */
//	public static String getData(String path) {
//		String ret = null;
//		InputStream in = null;
//		ByteArrayOutputStream bos = null;
//		// 访问的基本地址
//		try {
//			URL u = new URL(path);
//			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
//			conn.setRequestMethod("GET");
//			conn.setReadTimeout(2000);
//			// conn.setReadTimeout(5000);
//			conn.setDoInput(true);
//			Log.i("AVProgress", "开始请求");
//			conn.connect();
//			if (conn.getResponseCode() == 200) {
//				in = conn.getInputStream();
//				bos = new ByteArrayOutputStream();
//				byte[] b = new byte[1024 * 1024];
//				int len = 0;
//				while ((len = in.read(b)) != -1) {
//					bos.write(b, 0, len);
//				}
//				b = null;
//				Log.i("AVProgress", "请求结束");
//				ret = new String(bos.toByteArray(), "UTF-8");
//			} else {
//				Log.e("AVProgress", "Stroy Request Error:" + conn.getResponseCode());
//			}
//
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (in != null) {
//				try {
//					in.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			if (bos != null) {
//				try {
//					bos.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return ret;
//	}

	/**
	 * 返回字节数组
	 * 
//	 * @param path
	 * @return
	 */
//	public static byte[] getByteArray(String path) {
//		byte[] ret = null;
//		InputStream in = null;
//		ByteArrayOutputStream bos = null;
//		// 访问的基本地址
//		try {
//			URL u = new URL(path);
//			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
//			conn.setRequestMethod("GET");
//			conn.setReadTimeout(3000);
//			conn.setDoInput(true);
//			conn.connect();
//
//			if (conn.getResponseCode() == 200) {
//				in = conn.getInputStream();
//				bos = new ByteArrayOutputStream();
//				byte[] b = new byte[1024 * 1024];
//				int len = 0;
//				while ((len = in.read(b)) != -1) {
//					bos.write(b, 0, len);
//				}
//				b = null;
//				ret = bos.toByteArray();
//			} else {
//				Log.e("AVProgress", "Image Request Error:" + conn.getResponseCode());
//			}
//
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (in != null) {
//				try {
//					in.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			if (bos != null) {
//				try {
//					bos.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return ret;
//	}

//	public static void OkHttpGet(String url, final HttpRequest.PostListener postListener) {
//		//创建okHttpClient对象
//		OkHttpClient mOkHttpClient = new OkHttpClient();
//		//创建一个Request
//		final Request request = new Request.Builder()
//				.url(url)
//				.build();
//		//new call
//		Call call = mOkHttpClient.newCall(request);
//		//请求加入调度
//		call.enqueue(new Callback() {
//			@Override
//			public void onFailure(Call call, IOException e) {
//
//			}
//
//			@Override
//			public void onResponse(Call call, Response response) throws IOException {
//				String familyJson = response.body().string();
//				System.out.println("response:"+familyJson);
//				try {
//					JSONTokener jsonParser = new JSONTokener(familyJson);
//					JSONObject jsonResult = (JSONObject) jsonParser.nextValue();
//					int retCode = jsonResult.getInt("statusCode");
//					postListener.code(retCode, familyJson);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	public static String getLossAndDelay(String pingCMD) {
		String lost = new String();
		String delay = new String();
		String str = new String();
		try {
			Process p = Runtime.getRuntime().exec(pingCMD);
			BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while((str=buf.readLine())!=null){
				if(str.contains("packet loss")){
					int i= str.indexOf("received");
					int j= str.indexOf("%");
					lost = str.substring(i+10, j+1);
				}
				if(str.contains("avg")){
					int i=str.indexOf("/", 20);
					int j=str.indexOf(".", i);
					delay =str.substring(i+1, j);
				}
			}
			System.out.println("丢包率:"+lost);
			System.out.println("延迟:"+delay);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return delay;
	}

}
