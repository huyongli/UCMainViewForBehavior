package cn.ittiger.uc.view.behavior;

import cn.ittiger.uc.R;
import cn.ittiger.uc.view.behavior.base.ViewOffsetBehavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ylhu on 17-2-23.
 */
public class UCViewTitleBehavior extends ViewOffsetBehavior<View> {
    private int mCommonHeight;

    public UCViewTitleBehavior() {

        super();
    }

    public UCViewTitleBehavior(Context context, AttributeSet attrs) {

        super(context, attrs);
        mCommonHeight = context.getResources().getDimensionPixelSize(R.dimen.common_height);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {

        ((CoordinatorLayout.LayoutParams) child.getLayoutParams()).topMargin = -child.getMeasuredHeight();
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {

        return isDependOn(dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        offsetChildAsNeeded(parent, child, dependency);
        return false;
    }

    private void offsetChildAsNeeded(CoordinatorLayout parent, View child, View dependency) {
        int headerOffsetRange = getHeaderOffset();
        int titleOffsetRange = getTitleHeight();
        if (dependency.getTranslationY() == headerOffsetRange) {
            child.setTranslationY(titleOffsetRange);
        } else if (dependency.getTranslationY() == 0) {
            child.setTranslationY(0);
        } else {
            child.setTranslationY((int) (dependency.getTranslationY() / (headerOffsetRange * 1.0f) * titleOffsetRange));
        }

    }

    private int getHeaderOffset() {

        return -mCommonHeight * 2;
    }

    private int getTitleHeight() {

        return mCommonHeight;
    }

    private boolean isDependOn(View dependency) {
        return dependency != null && dependency.getId() == R.id.news_view_header_layout;
    }
}
