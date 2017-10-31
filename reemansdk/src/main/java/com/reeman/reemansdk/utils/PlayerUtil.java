package com.reeman.reemansdk.utils;

import android.app.Service;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.os.Vibrator;

import com.reeman.reemansdk.R;

public class PlayerUtil {

	private MediaPlayer mPlayer;
	Context context;

	public PlayerUtil(Context context) {
		this.context = context;
		if (mPlayer == null) {
			mPlayer = MediaPlayer.create(context,R.raw.incoming);
		}
	}

	private static int touchSoundId, speechId;
	private static SoundPool notificationMediaplayer;
	private static Vibrator notificationVibrator;

	public PlayerUtil(Context context, String pull) {
		this.context = context;
		if (notificationMediaplayer == null) {
			notificationMediaplayer = new SoundPool(3, AudioManager.STREAM_SYSTEM, 5);
			touchSoundId = notificationMediaplayer.load(context, R.raw.down, 1);
			speechId = notificationMediaplayer.load(context, R.raw.beep, 1);
		}
		if (notificationVibrator == null) {
			notificationVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
		}
	}

	/**
	 * 点击按键声效
	 */
	public void playNotification() {
		notificationMediaplayer.play(touchSoundId, 1, 1, 0, 0, 1);
		notificationVibrator.vibrate(50);
	}

	/**
	 * 点击按键声效
	 */
	public void playSpeechSound() {
		notificationMediaplayer.play(speechId, 1, 1, 0, 0, 1);
		notificationVibrator.vibrate(50);
	}

	private AssetManager mAssetManager;

	public void playAssetsFile(String file, boolean repeat) {
		if (mPlayer == null) {
			mPlayer = MediaPlayer.create(context,R.raw.incoming);
		}
		try {
			mPlayer.start();
			if (!repeat) {
				mPlayer.setOnCompletionListener(myComPlistener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		try {
			if (mPlayer == null) {
				return;
			}
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
			mPlayer.release();
			mPlayer = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mPlayer = null;
		}
	}

	OnCompletionListener myComPlistener = new OnCompletionListener() {

		public void onCompletion(MediaPlayer mp) {
//			listener.playOver();
			mPlayer.pause();
			mPlayer.stop();
			mPlayer.release();
		}
	};

	public void setPlayerListener(PlayerListener listener) {
		this.listener = listener;
	}

	PlayerListener listener;

	public interface PlayerListener {
		/** 播放完毕回调 */
		void playOver();
	}

}
