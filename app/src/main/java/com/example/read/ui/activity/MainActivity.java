
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.read.R;
import com.example.read.databinding.ActivityMainBinding;
import com.example.read.ui.adapter.MyViewPagerAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    private static String message = "1、 爱读书是一款提供网络文学搜索的工具，为广大网络文学爱好者提供一种方便、快捷舒适的试读体验。\n" +
            "2、 当您搜索一本书的时，爱读书会将该书的书名以关键词的形式提交到各个第三方网络文学网站。各第三方网站返回的内容与爱读书无关，" +
            "爱读书对其概不负责，亦不承担任何法律责任。任何通过使用爱读书而链接到的第三方网页均系他人制作或提供，您可能从第三方网页上获得其他服务，" +
            "爱读书对其合法性概不负责，亦不承担任何法律责任。第三方搜索引擎结果根据您提交的书名自动搜索获得并提供试读，不代表爱读书赞成或被搜索链接到的第三方网页上的内容或立场。" +
            "您应该对使用搜索引擎的结果自行承担风险。\n" +
            "3、 爱读书致力于最大程度地减少网络文学阅读者在自行搜寻过程中的无意义的时间浪费，" +
            "通过专业搜索展示不同网站中网络文学的最新章节。爱读书在为广大小说爱好者提供方便、快捷舒适的试读体验的同时，也使优秀网络文学得以迅速、更广泛的传播，从而达到了在一定程度促进网络文学充分繁荣发展之目的。" +
            "爱读书鼓励广大小说爱好者通爱读书发现优秀网络小说及其提供商，并建议阅读正版图书。任何单位或个人认为通过爱读书搜索链接到的第三方网页内容可能涉嫌侵犯其信息网络传播权，应该及时向爱读书提出书面权力通知，并提供身份证明、权属证明及详细侵权情况证明。" +
            "爱读书在收到上述法律文件后，将会依法尽快断开相关链接内容。";

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
                case R.id.more:
                    Toast.makeText(MainActivity.this,item.getTitle(),Toast.LENGTH_SHORT).show();
                    break;
                case R.id.setting:
                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                                                    .setTitle("免责声明")
                                                    .setMessage(message)
                                                    .setIcon(R.drawable.ic_round)
                                                    .create();
                    alertDialog.show();
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