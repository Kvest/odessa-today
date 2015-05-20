package com.kvest.odessatoday.ui.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Kvest on 03.05.2015.
 */
public abstract class FragmentPagerAdapter extends PagerAdapter {
    private static final String TAG = "FragmentPagerAdapter";
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private Fragment mCurrentPrimaryItem = null;

    public FragmentPagerAdapter(FragmentManager fm) {
        this.mFragmentManager = fm;
    }

    public abstract Fragment getItem(int var1);

    public void startUpdate(ViewGroup container) {
    }

    public Object instantiateItem(ViewGroup container, int position) {
        if(this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }

        long itemId = this.getItemId(position);
        String name = makeFragmentName(container.getId(), itemId);
        Fragment fragment = this.mFragmentManager.findFragmentByTag(name);
        if(fragment != null) {
            this.mCurTransaction.attach(fragment);
        } else {
            fragment = this.getItem(position);
            this.mCurTransaction.add(container.getId(), fragment, makeFragmentName(container.getId(), itemId));
        }

        if(fragment != this.mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            setUserVisibleHint(fragment, false);
        }

        return fragment;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        if(this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }

        this.mCurTransaction.detach((Fragment)object);
    }

    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if(fragment != this.mCurrentPrimaryItem) {
            if(this.mCurrentPrimaryItem != null) {
                this.mCurrentPrimaryItem.setMenuVisibility(false);
                setUserVisibleHint(this.mCurrentPrimaryItem, false);
            }

            if(fragment != null) {
                fragment.setMenuVisibility(true);
                setUserVisibleHint(fragment, true);
            }

            this.mCurrentPrimaryItem = fragment;
        }

    }

    public void finishUpdate(ViewGroup container) {
        if(this.mCurTransaction != null) {
            this.mCurTransaction.commitAllowingStateLoss();
            this.mCurTransaction = null;
            this.mFragmentManager.executePendingTransactions();
        }

    }

    private void setUserVisibleHint(Fragment fragment, boolean value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            fragment.setUserVisibleHint(value);
        }
    }

    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    public Parcelable saveState() {
        return null;
    }

    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    public long getItemId(int position) {
        return (long)position;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
