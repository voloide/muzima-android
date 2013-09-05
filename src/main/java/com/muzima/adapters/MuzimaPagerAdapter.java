package com.muzima.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.muzima.view.MuzimaListFragment;

public abstract class MuzimaPagerAdapter extends FragmentPagerAdapter{
    protected PagerView[] pagers;

    public MuzimaPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        initPagerViews(context);
    }

    @Override
    public Fragment getItem(int position) {
        return pagers[position].fragment;
    }

    @Override
    public int getCount() {
        return pagers.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pagers[position].title;
    }

    protected abstract void initPagerViews(Context context);

    public static class PagerView {
        public String title;
        public MuzimaListFragment fragment;

        public PagerView(String title, MuzimaListFragment fragment) {
            this.title = title;
            this.fragment = fragment;
        }
    }
}
