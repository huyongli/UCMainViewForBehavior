package cn.ittiger.uc.view.behavior;

import cn.ittiger.uc.R;
import cn.ittiger.uc.view.behavior.base.HeaderScrollingViewBehavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Created by ylhu on 17-2-22.
 */
public class UCViewContentBehavior extends HeaderScrollingViewBehavior {
    private int mCommonHeight;

    public UCViewContentBehavior() {

        super();
    }

    public UCViewContentBehavior(Context context, AttributeSet attrs) {

        super(context, attrs);
        mCommonHeight = context.getResources().getDimensionPixelSize(R.dimen.common_height);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {

        return isDependency(dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        child.setTranslationY(-dependency.getTranslationY() / (getHeaderOffset() * 1.0f) * getScrollRange(dependency));
        return false;
    }

    @Override
    protected int getScrollRange(View v) {

        if(isDependency(v)) {
            return v.getMeasuredHeight() - getContentFinalTop();//Header Icon导航区的高度，减去最后Tab和Title的高度就是Content最终要滑动的高度
        }
        return super.getScrollRange(v);
    }

    private int getContentFinalTop() {

        int titleHeight = mCommonHeight;
        int tabHeight = mCommonHeight;
        return titleHeight + tabHeight;
    }

    private int getHeaderOffset() {

        return -mCommonHeight * 2;//Header设定允许滑动两个CommonHeight
    }

    @Override
    public View findFirstDependency(List<View> views) {

        for(int i = 0; i < views.size(); i++) {
            if(isDependency(views.get(i))) {
                return views.get(i);
            }
        }
        return null;
    }

    private boolean isDependency(View dependency) {

        return dependency != null && dependency.getId() == R.id.news_view_header_layout;
    }
}
