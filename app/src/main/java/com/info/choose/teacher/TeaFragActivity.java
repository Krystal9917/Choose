package com.info.choose.teacher;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.info.choose.R;

public class TeaFragActivity extends FragmentActivity {
    private Fragment fragments[];
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ImageButton btn_index, btn_home;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        fragments = new Fragment[2];
        fragmentManager = getSupportFragmentManager();
        fragments[0] = fragmentManager.findFragmentById(R.id.tea_home);
        fragments[1] = fragmentManager.findFragmentById(R.id.tea_index);

        fragmentTransaction = fragmentManager.beginTransaction().hide(fragments[0]).hide(fragments[1]);
        fragmentTransaction.show(fragments[0]).commit();

        btn_index = findViewById(R.id.teacher_index);
        btn_home = findViewById(R.id.teacher_home);

        btn_index.setOnClickListener(new ImgBtnListener());
        btn_home.setOnClickListener(new ImgBtnListener());
    }

    class ImgBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.teacher_index:
                    FragmentTransaction transaction1 = fragmentManager.beginTransaction().hide(fragments[0]).hide(fragments[1]);
                    transaction1.show(fragments[1]).commit();
                    btn_index.setBackgroundResource(R.mipmap.index_clicked);
                    btn_home.setBackgroundResource(R.mipmap.mine_un_clicked);
                    break;
                case R.id.teacher_home:
                    FragmentTransaction transaction2 = fragmentManager.beginTransaction().hide(fragments[0]).hide(fragments[1]);
                    transaction2.show(fragments[0]).commit();
                    btn_index.setBackgroundResource(R.mipmap.index_un_clicked);
                    btn_home.setBackgroundResource(R.mipmap.mine_clicked);
                    break;
            }
        }
    }
}
