package cn.ittiger.uc.view.behavior;

import cn.ittiger.uc.R;
import cn.ittiger.uc.view.behavior.base.ViewOffsetBehavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import java.lang.ref.WeakReference;

/**
 * Created by ylhu on 17-2-23.
 */
public class UCViewHeaderBehavior extends ViewOffsetBehavior<View> {
    private int mCommonHeight;
    private OverScroller mOverScroller;
    private WeakReference<View> mChild;

    public static final int STATE_OPENED = 0;
    public static final int STATE_CLOSED = 1;
    public static final int DURATION_SHORT = 300;
    public static final int DURATION_LONG = 600;

    private int mCurState = STATE_OPENED;

    public UCViewHeaderBehavior() {

        super();
    }

    public UCViewHeaderBehavior(Context context, AttributeSet attrs) {

        super(context, attrs);
        mCommonHeight = context.getResources().getDimensionPixelSize(R.dimen.common_height);
        mOverScroller = new OverScroller(context);
    }

    @Override
    protected void layoutChild(CoordinatorLayout parent, View child, int layoutDirection) {

        super.layoutChild(parent, child, layoutDirection);
        mChild = new WeakReference<>(child);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {

        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && canScroll(child, 0) && !isClosed(child);
    }

    private boolean canScroll(View child, float pendingDy) {

        int pendingTranslationY = (int) (child.getTranslationY() - pendingDy);
        if (pendingTranslationY >= getHeaderOffset() && pendingTranslationY <= 0) {
            return true;
        }
        return false;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {

        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        //dy>0 scroll up;dy<0,scroll down
        float halfOfDis = dy / 4.0f;
        if (!canScroll(child, halfOfDis)) {
            if(halfOfDis > 0) {
                child.setVisibility(View.GONE);//滑动结束后，隐藏此视图
                child.setTranslationY(getHeaderOffset());
            } else {
                child.setTranslationY(0);
            }
        } else {
            if(halfOfDis <= 0) {
                child.setVisibility(View.VISIBLE);
            }
            child.setTranslationY(child.getTranslationY() - halfOfDis);
        }
        //consumed all scroll behavior after we started Nested Scrolling
        consumed[1] = dy;
    }

    private int getHeaderOffset() {

        return -mCommonHeight * 2;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {

        if(ev.getAction() == MotionEvent.ACTION_UP) {
            handlerActionUp(child);
        }
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    private void handlerActionUp(View child) {
        if (mFlingRunnable != null) {
            child.removeCallbacks(mFlingRunnable);
            mFlingRunnable = null;
        }
        mFlingRunnable = new FlingRunnable(child);
        if (child.getTranslationY() < getHeaderOffset() / 4.0f) {
            mFlingRunnable.scrollToClosed(DURATION_SHORT);
        } else {
            mFlingRunnable.scrollToOpen(DURATION_SHORT);
        }
    }

    private void onFlingFinished(View layout) {

        boolean isClosed = isClosed(layout);
        mCurState = isClosed ? STATE_CLOSED : STATE_OPENED;
        if(isClosed) {
            layout.setVisibility(View.GONE);
        }
    }

    private boolean isClosed(View child) {

        return child.getTranslationY() == getHeaderOffset();
    }

    public boolean isClosed() {
        return mCurState == STATE_CLOSED;
    }

    public void openPager() {
        openPager(DURATION_LONG);
    }

    /**
     * @param duration open animation duration
     */
    public void openPager(int duration) {
        View child = mChild.get();
        if (isClosed() && child != null) {
            if(child.getVisibility() == View.GONE) {
                child.setVisibility(View.VISIBLE);
            }
            if (mFlingRunnable != null) {
                child.removeCallbacks(mFlingRunnable);
                mFlingRunnable = null;
            }
            mFlingRunnable = new FlingRunnable(child);
            mFlingRunnable.scrollToOpen(duration);
        }
    }

    public void closePager() {
        closePager(DURATION_LONG);
    }

    /**
     * @param duration close animation duration
     */
    public void closePager(int duration) {
        View child = mChild.get();
        if (!isClosed()) {
            if (mFlingRunnable != null) {
                child.removeCallbacks(mFlingRunnable);
                mFlingRunnable = null;
            }
            mFlingRunnable = new FlingRunnable(child);
            mFlingRunnable.scrollToClosed(duration);
        }
    }


    private FlingRunnable mFlingRunnable;

    /**
     * For animation , Why not use {@link android.view.ViewPropertyAnimator } to play animation is of the
     * other {@link android.support.design.widget.CoordinatorLayout.Behavior} that depend on this could not receiving the correct result of
     * {@link View#getTranslationY()} after animation finished for whatever reason that i don't know
     */
    private class FlingRunnable implements Runnable {
        private final View mLayout;

        FlingRunnable(View layout) {
            mLayout = layout;
        }

        public void scrollToClosed(int duration) {
            float curTranslationY = ViewCompat.getTranslationY(mLayout);
            float dy = getHeaderOffset() - curTranslationY;
            mOverScroller.startScroll(0, Math.round(curTranslationY - 0.1f), 0, Math.round(dy + 0.1f), duration);
            start();
        }

        public void scrollToOpen(int duration) {
            float curTranslationY = ViewCompat.getTranslationY(mLayout);
            mOverScroller.startScroll(0, (int) curTranslationY, 0, (int) -curTranslationY, duration);
            start();
        }

        private void start() {
            if (mOverScroller.computeScrollOffset()) {
                mFlingRunnable = new FlingRunnable(mLayout);
                ViewCompat.postOnAnimation(mLayout, mFlingRunnable);
            } else {
                onFlingFinished(mLayout);
            }
        }


        @Override
        public void run() {
            if (mLayout != null && mOverScroller != null) {
                if (mOverScroller.computeScrollOffset()) {
                    ViewCompat.setTranslationY(mLayout, mOverScroller.getCurrY());
                    ViewCompat.postOnAnimation(mLayout, this);
                } else {
                    onFlingFinished(mLayout);
                }
            }
        }
    }
}
