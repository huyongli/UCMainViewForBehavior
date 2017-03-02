package cn.ittiger.uc.view.behavior;

import cn.ittiger.uc.R;
import cn.ittiger.uc.view.behavior.base.HeaderScrollingViewBehavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * Created by ylhu on 17-2-23.
 */
public class UCViewTabBehavior extends HeaderScrollingViewBehavior {
    private int mCommonHeight;

    public UCViewTabBehavior() {

        super();
    }

    public UCViewTabBehavior(Context context, AttributeSet attrs) {

        super(context, attrs);
        mCommonHeight = context.getResources().getDimensionPixelSize(R.dimen.common_height);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {

        return isDependency(dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        float offsetRange = getTabFinalTop() - dependency.getMeasuredHeight();
        if(dependency.getTranslationY() == getHeaderOffset()) {//Header已经上滑结束
            child.setTranslationY(offsetRange);
        } else if(dependency.getTranslationY() == 0) {//下滑结束
            child.setTranslationY(0);
        } else {
            child.setTranslationY(dependency.getTranslationY() / (getHeaderOffset() * 1.0f) * offsetRange);
        }
        return false;
    }

    private int getTabFinalTop() {

        int titleHeight = mCommonHeight;
        return titleHeight;
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
