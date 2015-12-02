package com.github.programmerr47.imageviewer.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.github.programmerr47.imageviewer.R;

import static com.github.programmerr47.imageviewer.representation.ImageViewerApplication.getAppContext;

/**
 * @author Michael Spitsin
 * @since 2015-08-06
 */
public class AnimationUtil {

    public static Animator showAndHideSuccessNetworkState(final View container, float offsetHeight) {
        int toolbarHeight = (int) offsetHeight;
        int containerHeight = container.getMeasuredHeight();
        if (containerHeight == 0) {
            containerHeight = (int) getAppContext().getResources().getDimension(R.dimen.connection_state_height);
        }

        container.setY(toolbarHeight - containerHeight);

        final ValueAnimator yAnim = ValueAnimator.ofInt(toolbarHeight - containerHeight, toolbarHeight);
        yAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                container.setY(val);
            }
        });

        final ValueAnimator yAnimReverse = ValueAnimator.ofInt(toolbarHeight, toolbarHeight - containerHeight);
        yAnimReverse.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                container.setY(val);
            }
        });

        yAnim.setDuration(Constants.DEFAULT_ANIMATION_DURATION);
        yAnimReverse.setDuration(Constants.DEFAULT_ANIMATION_DURATION);

        yAnim.setInterpolator(new DecelerateInterpolator());
        yAnimReverse.setInterpolator(new AccelerateInterpolator());

        AnimatorSet animation = new AnimatorSet();
        animation
                .play(yAnimReverse)
                .after(3000)
                .after(yAnim);
        animation.start();
        return animation;
    }

    public static Animator showNetworkState(final View container, float offsetHeight) {
        int toolbarHeight = (int) offsetHeight;
        int containerHeight = container.getMeasuredHeight();
        container.setY(toolbarHeight - containerHeight);

        final ValueAnimator yAnim = ValueAnimator.ofInt(toolbarHeight - containerHeight, toolbarHeight);
        yAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                container.setY(val);
            }
        });

        yAnim.setDuration(Constants.DEFAULT_ANIMATION_DURATION);

        AnimatorSet animation = new AnimatorSet();
        animation.setInterpolator(new DecelerateInterpolator());

        animation.play(yAnim);
        animation.start();
        return animation;
    }

    public static Animator showProgress(final View progressView, final View hidingView, final View container) {
        final ValueAnimator alphaAnim = ValueAnimator.ofFloat(0, 1);
        final int progressWidth = progressView.getMeasuredWidth();
        final int containerWidth = container.getMeasuredWidth();
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (Float) animation.getAnimatedValue();
                progressView.setAlpha(val);
                hidingView.setAlpha(1 - val);

                if (AndroidUtils.hasLollipop()) {
                    container.setElevation(AndroidUtils.dpToPx((int)(val * 2)));
                } else {
                    ViewCompat.setElevation(container, AndroidUtils.dpToPx((int)(val * 2)));
                }

                ViewGroup.LayoutParams params = container.getLayoutParams();
                params.width = (int)(containerWidth * (1 - val) + progressWidth * val);
                container.setLayoutParams(params);
            }
        });
        alphaAnim.setDuration(Constants.DEFAULT_ANIMATION_DURATION);

        AnimatorSet animation = new AnimatorSet();
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                progressView.setVisibility(View.VISIBLE);
                hidingView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hidingView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                hidingView.setVisibility(View.VISIBLE);
                progressView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animation.play(alphaAnim);
        animation.start();
        return animation;
    }

    public static Animator hideProgress(final View progressView, final View revealView, final View container, final int originalContainerWidth) {
        ValueAnimator alphaAnim = ValueAnimator.ofFloat(1, 0);
        final int progressWidth = progressView.getMeasuredWidth();
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (Float) animation.getAnimatedValue();
                progressView.setAlpha(val);
                revealView.setAlpha(1 - val);

                if (AndroidUtils.hasLollipop()) {
                    container.setElevation(AndroidUtils.dpToPx((int)(val * 2)));
                } else {
                    ViewCompat.setElevation(container, AndroidUtils.dpToPx((int)(val * 2)));
                }

                ViewGroup.LayoutParams params = container.getLayoutParams();
                params.width = (int)(originalContainerWidth * (1 - val) + progressWidth * val);
                container.setLayoutParams(params);
            }
        });
        alphaAnim.setDuration(Constants.DEFAULT_ANIMATION_DURATION);

        AnimatorSet animation = new AnimatorSet();
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                progressView.setVisibility(View.VISIBLE);
                revealView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                revealView.setVisibility(View.INVISIBLE);
                progressView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });

        animation.play(alphaAnim);
        animation.start();
        return animation;
    }

    public static void changeColorOfView(View view, int startColor, int endColor) {
        ColorDrawable[] color = {new ColorDrawable(startColor), new ColorDrawable(endColor)};
        TransitionDrawable trans = new TransitionDrawable(color);
        view.setBackgroundDrawable(trans);
        trans.startTransition(Constants.DEFAULT_ANIMATION_DURATION);
    }
}
