package cn.ittiger.uc.adapter;

import cn.ittiger.uc.fragment.ContentFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class ContentFragmentAdapter extends FragmentPagerAdapter {
    List<ContentFragment> mFragments;


    public ContentFragmentAdapter(List<ContentFragment> fragments, FragmentManager fm) {
        super(fm);
        this.mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return mFragments.get(position).getName();
    }
}
