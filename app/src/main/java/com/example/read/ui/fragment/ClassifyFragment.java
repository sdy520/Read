package com.example.read.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.read.Entity.BookClassify;
import com.example.read.R;
import com.example.read.databinding.FragmentClassifyBinding;
import com.example.read.ui.activity.RankingActivity;
import com.example.read.ui.adapter.ClassifyBookAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClassifyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClassifyFragment extends Fragment {
    private FragmentClassifyBinding classifyBinding;
    private Activity mactivity;
    private List<BookClassify> bookClassifies = new ArrayList<>();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ClassifyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClassifyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClassifyFragment newInstance(String param1, String param2) {
        ClassifyFragment fragment = new ClassifyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mactivity=(Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        classifyBinding = FragmentClassifyBinding.inflate(inflater,container,false);
        ininbookclassfify();
        //LinearLayoutManager layoutManager = new LinearLayoutManager(mactivity);
        GridLayoutManager layoutManager = new GridLayoutManager(mactivity,2);
        classifyBinding.classify.setLayoutManager(layoutManager);
        ClassifyBookAdapter classifyBookAdapter = new ClassifyBookAdapter(mactivity,bookClassifies);
        classifyBinding.classify.setAdapter(classifyBookAdapter);
        classifyBookAdapter.setOnItemClickListener(new ClassifyBookAdapter.OnItemClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onItemClick(View view, int position) {
                /*Intent intent1;
                if(position==0) {
                    intent1 = new Intent(mactivity, RankingActivity.class);
                    intent1.putExtra("hang", "1");
                    intent1.putExtra("lie", 0);
                    startActivity(intent1);
                }
                if(position==1) {
                    intent1 = new Intent(mactivity, RankingActivity.class);
                    intent1.putExtra("hang", "2");
                    intent1.putExtra("lie", 0);
                    startActivity(intent1);
                }
                if(position==2) {
                    intent1 = new Intent(mactivity, RankingActivity.class);
                    intent1.putExtra("hang", "3");
                    intent1.putExtra("lie", 0);
                    startActivity(intent1);
                }
                if(position==3) {
                    intent1 = new Intent(mactivity, RankingActivity.class);
                    intent1.putExtra("hang", "4");
                    intent1.putExtra("lie", 0);
                    startActivity(intent1);
                }
                if(position==4) {
                    intent1 = new Intent(mactivity, RankingActivity.class);
                    intent1.putExtra("hang", "1");
                    intent1.putExtra("lie", 1);
                    startActivity(intent1);
                }
                if(position==5) {
                    intent1 = new Intent(mactivity, RankingActivity.class);
                    intent1.putExtra("hang", "2");
                    intent1.putExtra("lie", 1);
                    startActivity(intent1);
                }
                if(position==6) {
                    intent1 = new Intent(mactivity, RankingActivity.class);
                    intent1.putExtra("hang", "3");
                    intent1.putExtra("lie", 1);
                    startActivity(intent1);
                }
                if(position==7) {
                    intent1 = new Intent(mactivity, RankingActivity.class);
                    intent1.putExtra("hang", "4");
                    intent1.putExtra("lie", 1);
                    startActivity(intent1);
                }
                //Toast.makeText(mactivity,bookClassifies.get(position).getClassifytext(),Toast.LENGTH_SHORT).show();
                Log.e("aa",bookClassifies.get(position).getClassifytext());
                Log.e("aa", String.valueOf(position));*/
            }
        });
        return classifyBinding.getRoot();
    }
    private void ininbookclassfify(){
        BookClassify bookClassify = new BookClassify();
        bookClassify.setClassifyimg(R.drawable.xuanhuan);
        bookClassify.setClassifytext("玄幻排行榜");
        bookClassifies.add(bookClassify);
        BookClassify bookClassify1 = new BookClassify();
        bookClassify1.setClassifyimg(R.drawable.xiuzhen);
        bookClassify1.setClassifytext("修真排行榜");
        bookClassifies.add(bookClassify1);
        BookClassify bookClassify2 = new BookClassify();
        bookClassify2.setClassifyimg(R.drawable.dushi);
        bookClassify2.setClassifytext("都市排行榜");
        bookClassifies.add(bookClassify2);
        BookClassify bookClassify3 = new BookClassify();
        bookClassify3.setClassifyimg(R.drawable.lishi);
        bookClassify3.setClassifytext("历史排行榜");
        bookClassifies.add(bookClassify3);
        BookClassify bookClassify4 = new BookClassify();
        bookClassify4.setClassifyimg(R.drawable.wangyou);
        bookClassify4.setClassifytext("网游排行榜");
        bookClassifies.add(bookClassify4);
        BookClassify bookClassify5 = new BookClassify();
        bookClassify5.setClassifyimg(R.drawable.kehuan);
        bookClassify5.setClassifytext("科幻排行榜");
        bookClassifies.add(bookClassify5);
        BookClassify bookClassify6 = new BookClassify();
        bookClassify6.setClassifyimg(R.drawable.yanqing);
        bookClassify6.setClassifytext("完本排行榜");
        bookClassifies.add(bookClassify6);
        BookClassify bookClassify7 = new BookClassify();
        bookClassify7.setClassifyimg(R.drawable.qita);
        bookClassify7.setClassifytext("全部排行榜");
        bookClassifies.add(bookClassify7);
    }
}