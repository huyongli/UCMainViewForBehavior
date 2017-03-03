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
    private int mTitleViewHeight = 0;

    public UCViewTabBehavior() {

        super();
    }

    public UCViewTabBehavior(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    @Override
    protected void layoutChild(CoordinatorLayout parent, View child, int layoutDirection) {

        mTitleViewHeight = parent.findViewById(R.id.news_view_title_layout).getMeasuredHeight();
        super.layoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {

        return isDependency(dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        //TabView要滑动的距离为Header的高度减去TitleView的高度
        float offsetRange = mTitleViewHeight - dependency.getMeasuredHeight();
        //当Header向上滑动mTitleViewHeight高度后，即滑动完成
        int headerOffsetRange = -mTitleViewHeight;

        if(dependency.getTranslationY() == headerOffsetRange) {//Header已经上滑结束
            child.setTranslationY(offsetRange);
        } else if(dependency.getTranslationY() == 0) {//下滑结束
            child.setTranslationY(0);
        } else {
            child.setTranslationY(dependency.getTranslationY() / (headerOffsetRange * 1.0f) * offsetRange);
        }
        return false;
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
