package com.info.choose.teacher;

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

import org.json.JSONArray;
import org.json.JSONObject;

public class TeaHomeFragment extends Fragment {
    Button btn_students, btn_logs, btn_adjust, btn_logout;
    TextView name_view, major_view;
    ImageView image;
    String id, sex, major, name, str;
    Handler handler;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_home, container, false);
        Intent intent = getActivity().getIntent();
        id = intent.getStringExtra("id");
        sex = intent.getStringExtra("sex");
        major = intent.getStringExtra("major");
        name = intent.getStringExtra("name");

        btn_students = view.findViewById(R.id.my_students);
        btn_logs = view.findViewById(R.id.my_logs);
        btn_adjust = view.findViewById(R.id.my_max);
        btn_logout = view.findViewById(R.id.logout);

        image = view.findViewById(R.id.avatar);
        name_view = view.findViewById(R.id.teacher_name);
        major_view = view.findViewById(R.id.major);
        // set image type
        if (sex.equals("男")) {
            image.setImageResource(R.mipmap.sir);
        } else {
            image.setImageResource(R.mipmap.madam);
        }
        name_view.setText(name+" 老师");
        major_view.setText(major+" 系");

        btn_students.setOnClickListener(new BtnListener());
        btn_logs.setOnClickListener(new BtnListener());
        btn_adjust.setOnClickListener(new BtnListener());
        btn_logout.setOnClickListener(new BtnListener());

        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    class BtnListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.my_students) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        str = "";
                        Message message = handler.obtainMessage(200);
                        // http request
                        JSONObject json = new JSONObject();
                        try {
                            json.put("id", id);
                            JSONObject response = new JSONObject(HttpUtils.request("/myStudents", json));
                            JSONArray students = response.getJSONArray("students");
                            if (students.length() != 0) {
                                for (int i = 0; i < students.length(); i++) {
                                    JSONObject student = students.getJSONObject(i);
                                    str += student.getString("student_name") + " " +
                                            student.getString("student_major") + " " +
                                            student.getString("topic") + "\n";
                                }
                            } else {
                                str = "I don't choose any student!";
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
                        new AlertDialog.Builder(getContext())
                                .setTitle("My Students Information")
                                .setMessage(str)
                                .setPositiveButton("Got it", null)
                                .show();
                        super.handleMessage(message);
                    }
                };

            } else if (view.getId() == R.id.my_logs) {
                // logs page
                Intent intent = new Intent(getContext(), TeaReceiveLogActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            } else if (view.getId() == R.id.my_max) {
                // adjust page
                Intent intent = new Intent(getContext(), AdjustMaxActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            } else if (view.getId() == R.id.logout) {
                getActivity().finish();
            }
        }
    }
}
