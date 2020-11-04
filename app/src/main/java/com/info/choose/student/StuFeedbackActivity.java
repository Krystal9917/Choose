package com.info.choose.student;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.info.choose.GlobalData;
import com.info.choose.HttpUtils;
import com.info.choose.R;

import org.json.JSONObject;


public class StuFeedbackActivity extends AppCompatActivity {

    Button approve, reject;
    EditText response_extra;
    TextView student_name, student_major, student_topic, request_extra;
    Handler handler;
    String id, student_name_str, topic, major;
    int handle, result;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_receive);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        student_name_str = intent.getStringExtra("name");
        topic = intent.getStringExtra("topic");
        major = intent.getStringExtra("major");

        approve = findViewById(R.id.approve);
        reject = findViewById(R.id.reject);

        response_extra = findViewById(R.id.response_extra);

        student_name = findViewById(R.id.student_name);
        student_major = findViewById(R.id.student_major);
        student_topic = findViewById(R.id.student_topic);
        request_extra = findViewById(R.id.request_extra);

        student_name.setText(student_name_str);
        student_major.setText(major);
        student_topic.setText(topic);
        request_extra.setText(getData());

        approve.setOnClickListener(new BtnListener());
        reject.setOnClickListener(new BtnListener());

    }

    public String getData() {
        final String[] str = {""};
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage(200);
                JSONObject json = new JSONObject();
                try {
                    json.put("teacher_id", id);
                    json.put("student_name", student_name_str);
                    // http request defined in the root path
                    JSONObject response = new JSONObject(HttpUtils.request("/getExtra", json));
                    String result = response.getString("extra");
                    if (result != null || !result.equals("")) {
                        str[0] = result;
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
        return str[0];
    }

    class BtnListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.approve) {
                handle = 1;

            } else {
                handle = -1;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("teacher_id", id);
                        json.put("student_name", student_name_str);
                        json.put("response_extra", response_extra.getText().toString());
                        json.put("status", handle);
                        JSONObject response = new JSONObject(HttpUtils.request("/updateStatus", json));
                        result = response.getInt("status");
                    } catch (Exception e) {
                        Log.i(GlobalData.ERROR_TAG, e.getMessage());
                    }
                }
            }).start();

            handler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message message) {
                    String str;
                    if (result == 200) {
                        str = "Submit Successfully!";
                    } else {
                        str = "Network Error!";
                    }
                    Toast.makeText(StuFeedbackActivity.this, str, Toast.LENGTH_LONG).show();
                    super.handleMessage(message);
                }
            };
            finish();
        }
    }
}
