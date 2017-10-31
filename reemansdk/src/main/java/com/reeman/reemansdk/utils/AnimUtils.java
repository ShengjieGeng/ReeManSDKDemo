package com.reeman.reemansdk.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * Created by reeman on 2017/8/23.
 */

public class AnimUtils {
    public static void transAnim(boolean animated, View view, int transX) {
        ObjectAnimator choiceViewAnim = animated? ObjectAnimator.ofFloat(view, "translationX",  transX,0): ObjectAnimator.ofFloat(view, "translationX", 0, transX);
        choiceViewAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                System.out.println("choiceViewAnim:::" + value);
            }
        });
        choiceViewAnim.setDuration(500);
        choiceViewAnim.start();
    }
}
