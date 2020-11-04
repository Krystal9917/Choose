package com.info.choose.student;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.info.choose.GlobalData;
import com.info.choose.HttpUtils;
import com.info.choose.R;

import org.json.JSONObject;

public class StuHomeFragment extends Fragment {
    Button btn_request, btn_logout;
    TextView name_view, major_view, grade_view, my_tutor;
    ImageView image;
    String id, grade, sex, major, name, str;
    Handler handler;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.student_home, container, false);
        Intent intent = getActivity().getIntent();
        id = intent.getStringExtra("id");
        grade = intent.getStringExtra("grade");
        sex = intent.getStringExtra("sex");
        major = intent.getStringExtra("major");
        name = intent.getStringExtra("name");

        my_tutor = view.findViewById(R.id.my_tutor);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage(200);
                // http request
                JSONObject json = new JSONObject();
                try {
                    json.put("id", id);
                    JSONObject response = new JSONObject(HttpUtils.request("/myTutor", json));
                    String teacher = response.getString("teacher");
                    if (!teacher.equals(null) && !teacher.equals("null")) {
                        str = "我的导师是：" + teacher;
                    }
                    handler.sendMessage(message);
                } catch (Exception e) {
                    Log.i(GlobalData.ERROR_TAG, e.toString());
                }
            }
        }).start();

        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message message) {
                Log.i(GlobalData.INFO_TAG, "Finish!");
                my_tutor.setText(str);
                super.handleMessage(message);
            }
        };

        btn_request = view.findViewById(R.id.my_request);
        btn_logout = view.findViewById(R.id.logout);

        image = view.findViewById(R.id.avatar);
        name_view = view.findViewById(R.id.student_name);
        major_view = view.findViewById(R.id.major);
        grade_view = view.findViewById(R.id.grade);
        // set image type
        if (sex.equals("男")) {
            image.setImageResource(R.mipmap.boy);
        } else {
            image.setImageResource(R.mipmap.girl);
        }
        name_view.setText(name);
        major_view.setText(major);
        grade_view.setText(grade);

        btn_request.setOnClickListener(new BtnListener());
        btn_logout.setOnClickListener(new BtnListener());

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    class BtnListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.my_request) {
                // another page
                Intent intent = new Intent(getContext(), StuApplyLogActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            } else if (view.getId() == R.id.logout) {
                getActivity().finish();
            }
        }
    }
}
