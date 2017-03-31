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
    private int mTitleViewHeight = 0;
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
        mOverScroller = new OverScroller(context);
    }

    @Override
    protected void layoutChild(CoordinatorLayout parent, View child, int layoutDirection) {

        super.layoutChild(parent, child, layoutDirection);
        mTitleViewHeight = parent.findViewById(R.id.news_view_title_layout).getMeasuredHeight();
        mChild = new WeakReference<>(child);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {

        //开始滑动的条件，垂直方向滑动，滑动未结束
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && canScroll(child, 0) && !isClosed(child);
    }

    /**
     * 当前是否可以滑动
     * @param child
     * @param pendingDy     Y轴方向滑动的translationY
     * @return
     */
    private boolean canScroll(View child, float pendingDy) {

        int pendingTranslationY = (int) (child.getTranslationY() - pendingDy);
        if (pendingTranslationY >= getHeaderOffsetRange() && pendingTranslationY <= 0) {
            return true;
        }
        return false;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {

        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        //开始滑动之前的逻辑处理

        //dy>0 向上滑
        //dy<0 向下滑
        float halfOfDis = dy / 4.0f;
        if (!canScroll(child, halfOfDis)) {//滑动结束
            if(halfOfDis > 0) {
                child.setVisibility(View.GONE);//滑动结束后，隐藏此视图
                child.setTranslationY(getHeaderOffsetRange());
            } else {
                child.setTranslationY(0);
            }
        } else {//滑动未结束
            if(halfOfDis <= 0) {
                child.setVisibility(View.VISIBLE);
            }
            //滑动
            child.setTranslationY(child.getTranslationY() - halfOfDis);
        }
        //消耗掉当前垂直方向上的滑动距离
        consumed[1] = dy;
    }

    /**
     * 向上滑动过程时translationY的最小值
     * @return
     */
    private int getHeaderOffsetRange() {

        return -mTitleViewHeight;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {

        if(ev.getAction() == MotionEvent.ACTION_UP) {
            //对松开手指时进行处理，如果松开时滑动滑动了1/4则自动滑动到结束，否则则回归原位
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
        if (child.getTranslationY() < getHeaderOffsetRange() / 4.0f) {
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

    /**
     * 是否滑动结束
     * @param child
     * @return
     */
    private boolean isClosed(View child) {

        return child.getTranslationY() == getHeaderOffsetRange();
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
    private class FlingRunnable implements Runnable {
        private final View mLayout;

        FlingRunnable(View layout) {
            mLayout = layout;
        }

        public void scrollToClosed(int duration) {
            float curTranslationY = ViewCompat.getTranslationY(mLayout);
            float dy = getHeaderOffsetRange() - curTranslationY;
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
