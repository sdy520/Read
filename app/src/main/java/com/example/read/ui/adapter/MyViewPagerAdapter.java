package com.example.read.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.read.ui.fragment.BookmarkFragment;
import com.example.read.ui.fragment.ClassifyFragment;

public class MyViewPagerAdapter extends FragmentStateAdapter {


    public MyViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if(position==0)
            fragment= new BookmarkFragment();
        else
            fragment= new ClassifyFragment();
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
