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

    public UCViewTitleBehavior() {

        super();
    }

    public UCViewTitleBehavior(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {

        //因为UCViewTitle默认是在屏幕外不可见，所以在UCViewTitle进行布局的时候设置其topMargin让其不可见
        ((CoordinatorLayout.LayoutParams) child.getLayoutParams()).topMargin = -child.getMeasuredHeight();
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {

        return isDependOn(dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        //因为UCViewTitle与UCViewHeader的滑动方向相反
        //所以当依赖UCViewHeader发生变化时，只需要时设置反向的translationY即可
        child.setTranslationY(-dependency.getTranslationY());
        return false;
    }

    private boolean isDependOn(View dependency) {
        return dependency != null && dependency.getId() == R.id.news_view_header_layout;
    }
}
