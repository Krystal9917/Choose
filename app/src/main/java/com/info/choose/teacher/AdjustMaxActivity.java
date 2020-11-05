package com.info.choose.teacher;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.info.choose.GlobalData;
import com.info.choose.HttpUtils;
import com.info.choose.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdjustMaxActivity extends AppCompatActivity {

    Button submit, cancel;
    TextView now_view, left_view;
    EditText max_view;
    Handler handler;
    String id;
    int max, now, status, max_temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adjust_max);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        max_view = findViewById(R.id.max_number);
        now_view = findViewById(R.id.now_number);
        left_view = findViewById(R.id.left_number);

        submit = findViewById(R.id.submit);
        cancel = findViewById(R.id.cancel);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage(200);
                // http request
                JSONObject json = new JSONObject();
                try {
                    json.put("id", id);
                    JSONObject response = new JSONObject(HttpUtils.request("/myMax", json));
                    max = response.getInt("max");
                    now = response.getInt("now");
                    handler.sendMessage(message);
                } catch (Exception e) {
                    Log.i(GlobalData.ERROR_TAG, e.toString());
                }
            }
        }).start();

        handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message message) {
                max_view.setText(String.valueOf(max));
                now_view.setText(String.valueOf(now));
                left_view.setText(String.valueOf(max - now));
                super.handleMessage(message);
            }
        };

        submit.setOnClickListener(new BtnListener());
        cancel.setOnClickListener(new BtnListener());


        max_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str;
                if (!charSequence.toString().isEmpty()) {
                    boolean flag = isNumeric(charSequence.toString());
                    if (flag) {
                        max_temp = Integer.parseInt(charSequence.toString());
                        if (max_temp < now) {
                            str = "非法：最大数不能小于当前已选学生数！";
                        } else {
                            str = "合法！";
                            left_view.setText(String.valueOf(max_temp - now));
                        }
                    } else {
                        str = "非法：输入字符非数字！";
                    }
                } else {
                    str = "非法：未输入字符！";
                }
                Toast.makeText(AdjustMaxActivity.this, str, Toast.LENGTH_LONG).show();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    class BtnListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.submit) {
                // submit new data
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = handler.obtainMessage(200);
                        // http request
                        JSONObject json = new JSONObject();
                        try {
                            json.put("id", id);
                            json.put("max", max_temp);
                            JSONObject response = new JSONObject(HttpUtils.request("/updateMax", json));
                            status = response.getInt("status");
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            Log.i(GlobalData.ERROR_TAG, e.toString());
                        }
                    }
                }).start();

                handler = new Handler(Looper.myLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        String str;
                        if (status == 200) {
                            str = "更新成功！";
                        } else if (status == 400) {
                            str = "更新失败！";
                        } else {
                            str = "网络异常！";
                        }
                        Toast.makeText(AdjustMaxActivity.this, str, Toast.LENGTH_LONG).show();
                        super.handleMessage(message);
                    }
                };

            } else if (view.getId() == R.id.cancel) {
                // quit
                finish();
            }
        }
    }
}
