package com.github.programmerr47.imageviewer.util;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.github.programmerr47.imageviewer.R;
import com.github.programmerr47.imageviewer.representation.ImageViewerApplication;

import static com.github.programmerr47.imageviewer.representation.ImageViewerApplication.getAppContext;
import static com.github.programmerr47.imageviewer.util.AndroidUtils.dimen;

/**
 * @author Michael Spitsin
 * @since 2015-08-06
 */
public class AnimationUtils {
    public static Animator showAndHideSuccessNetworkState(final View container, float offsetHeight) {
        int toolbarHeight = (int) offsetHeight;
        int containerHeight = container.getMeasuredHeight();
        if (containerHeight == 0) {
            containerHeight = (int) dimen(R.dimen.connection_state_height);
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
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                container.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                container.setVisibility(View.INVISIBLE);
            }
        });
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
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                container.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                container.setVisibility(View.INVISIBLE);
            }
        });
        animation.start();
        return animation;
    }

    public static Animator getShowViewAnimation(@NonNull final View view) {
        Animator showingViewAnim = AnimatorInflater.loadAnimator(getAppContext(), R.animator.fade_in);

        showingViewAnim.setTarget(view);

        AnimatorSet animation = new AnimatorSet();
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });

        animation.play(showingViewAnim);
        return animation;
    }

    public static Animator getHideViewAnimation(@NonNull final View view) {
        Animator hidingViewAnim = AnimatorInflater.loadAnimator(getAppContext(), R.animator.fade_out);

        hidingViewAnim.setTarget(view);

        AnimatorSet animation = new AnimatorSet();
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });

        animation.play(hidingViewAnim);
        return animation;
    }

    public static Animator showView(@NonNull View view) {
        Animator animation = getShowViewAnimation(view);
        animation.start();
        return animation;
    }

    public static Animator hideView(@NonNull View view) {
        Animator animation = getHideViewAnimation(view);
        animation.start();
        return animation;
    }

    public static void swapViews(View hideView, View showView) {
        Animator showingAnim = AnimationUtils.getShowViewAnimation(showView);
        Animator hidingAnim = AnimationUtils.getHideViewAnimation(hideView);
        AnimationUtils.playTogether(showingAnim, hidingAnim);
    }

    public static void playTogether(Animator... animators) {
        if (animators.length > 0) {
            AnimatorSet animatorSet = new AnimatorSet();
            AnimatorSet.Builder animatorBuilder = animatorSet.play(animators[0]);

            if (animators.length > 1) {
                for (int i = 1; i < animators.length; i++) {
                    animatorBuilder.with(animators[i]);
                }
            }

            animatorSet.start();
        }
    }
}
