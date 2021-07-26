
/*
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG             #
 * #                                                   #
 */
package com.example.read.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.read.R;
import com.example.read.databinding.ActivityMainBinding;
import com.example.read.ui.adapter.MyViewPagerAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //实例化适配器
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), getLifecycle());

        //设置适配器
        binding.viewpager.setAdapter(myViewPagerAdapter);
        //TabLayout和Viewpager2进行关联
        new TabLayoutMediator(binding.tablayout, binding.viewpager, (tab, position) -> {
            switch (position){
                case 0:
                    tab.setIcon(R.drawable.ic_baseline_bookmark_border_24);
                    break;
                case 1:
                    tab.setIcon(R.drawable.ic_bookclassify);
                    break;
            }
        }).attach();

        binding.topAppBar.setOnMenuItemClickListener(item -> {
            Intent intent = null;
            switch (item.getItemId()){
                case R.id.search:
                    intent=new Intent(MainActivity.this, SearchBookActivity.class);
                    break;
                case R.id.favorite:
                case R.id.setting:
                case R.id.more:
                    Toast.makeText(MainActivity.this,item.getTitle(),Toast.LENGTH_SHORT).show();
                    break;
            }
            if (intent != null){
                startActivity(intent);
            }
            return true;
        });

    }
    public AppBarLayout getRlCommonTitle() {
        return binding.rlCommonTitle;
    }
    public RelativeLayout getRlEditTitile() {
        return binding.rlEditTitile;
    }
    public TextView getTvEditFinish() {
        return binding.tvEditFinish;
    }
    public ViewPager2 getVpContent() {
        return binding.viewpager;
    }

}