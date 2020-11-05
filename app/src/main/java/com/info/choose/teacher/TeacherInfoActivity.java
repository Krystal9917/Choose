package com.info.choose.teacher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.info.choose.student.StuApplyActivity;
import com.info.choose.GlobalData;
import com.info.choose.HttpUtils;
import com.info.choose.R;


import org.json.JSONArray;
import org.json.JSONObject;


public class TeacherInfoActivity extends Activity {
    WebView view;
    Button apply, show;
    Handler handler;
    String name, url, students_name, student_id;
    int have_tutor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_info_item);

        Intent intent = getIntent();
        name = intent.getStringExtra("teacher_name");
        student_id = intent.getStringExtra("student_id");
        url = intent.getStringExtra("url");
        students_name = "";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage(200);
                JSONObject json = new JSONObject();
                try {
                    json.put("name", name);
                    String list = HttpUtils.request("/matched", json);
                    JSONObject object = new JSONObject(list);
                    JSONArray students = object.getJSONArray("students");
                    if (students.length() == 0) {
                        students_name = "暂无学生选择";
                    } else {
                        for (int i = 0; i < students.length(); i++) {
                            students_name += students.getJSONObject(i).getString("student_name") + "\n";
                        }
                    }
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message message) {
                super.handleMessage(message);
            }
        };

        view = findViewById(R.id.teacher_info);
        view.setWebChromeClient(new WebChromeClient());
        view.setWebViewClient(new WebViewClient());
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl(url);

        apply = findViewById(R.id.apply);
        show = findViewById(R.id.show);

        apply.setOnClickListener(new BtnListener());
        show.setOnClickListener(new BtnListener());

    }

    class BtnListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.apply) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(GlobalData.INFO_TAG, "Enter Run()");
                        Message message = handler.obtainMessage(200);
                        JSONObject json = new JSONObject();
                        try {
                            json.put("student_id", student_id);
                            // http request defined in the root path
                            String response = HttpUtils.request("/testMatch", json);
                            JSONObject result = new JSONObject(response);
                            have_tutor= result.getInt("status");
                            Log.i(GlobalData.INFO_TAG+"---Test Matched---", String.valueOf(have_tutor));
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            Log.i(GlobalData.ERROR_TAG, e.toString());
                        }
                    }
                }).start();

                handler = new Handler(Looper.myLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        // start a new activity
                        Intent intent = new Intent(TeacherInfoActivity.this, StuApplyActivity.class);
                        intent.putExtra("teacher_name", name);
                        intent.putExtra("student_id", student_id);
                        intent.putExtra("have_tutor",String.valueOf(have_tutor));
                        startActivity(intent);
                        super.handleMessage(message);
                    }
                };

            } else if (view.getId() == R.id.show) {
                new AlertDialog.Builder(TeacherInfoActivity.this)
                        .setTitle("选择此位老师的学生")
                        .setMessage(students_name)
                        .setPositiveButton("好的", null)
                        .show();
            }
        }
    }
}
