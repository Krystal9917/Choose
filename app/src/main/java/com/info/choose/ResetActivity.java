package com.info.choose;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.info.choose.entity.User;
import com.info.choose.student.StuFragActivity;
import com.info.choose.teacher.TeaFragActivity;

import org.json.JSONObject;

public class ResetActivity extends AppCompatActivity {

    ImageView imageView;

    EditText id, new_pwd, again_pwd;
    Button submit, cancel;
    Handler handler;
    int response, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        Intent intent = getIntent();


        // logo
        imageView = findViewById(R.id.logo);
        imageView.setImageResource(R.mipmap.first_logo);
        // input information
        new_pwd = findViewById(R.id.new_pwd);
        again_pwd = findViewById(R.id.again_pwd);
        id = findViewById(R.id.select_id);
        id.setText(intent.getStringExtra("id"));
        type = intent.getIntExtra("type", 0);

        // button
        submit = findViewById(R.id.submit);
        cancel = findViewById(R.id.cancel);

        // add listener
        submit.setOnClickListener(new ButtonListener());
        cancel.setOnClickListener(new ButtonListener());

    }

    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int btn_id = view.getId();
            if (btn_id == R.id.submit) {
                if (!new_pwd.getText().toString().equals("") && !again_pwd.getText().toString().equals("")) {
                    // match information
                    if (new_pwd.getText().toString().equals(again_pwd.getText().toString())) {
                        // update database
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = handler.obtainMessage(200);
                                JSONObject json = new JSONObject();
                                try {
                                    json.put("id", id.getText().toString());
                                    json.put("pwd", new_pwd.getText().toString());
                                    json.put("type", type);
                                    JSONObject object = new JSONObject(HttpUtils.request("/reset", json));
                                    response = object.getInt("status");
                                    handler.sendMessage(message);
                                } catch (Exception e) {
                                    Log.i("Thread---" + GlobalData.ERROR_TAG, e.toString());
                                }
                            }
                        }).start();

                        handler = new Handler(Looper.myLooper()) {
                            @Override
                            public void handleMessage(Message message) {
                                String str;
                                if (response == 200) {
                                    // change successfully
                                    str = "Reset password successfully!";
                                    new_pwd.setText("");
                                    again_pwd.setText("");
                                    Toast.makeText(ResetActivity.this, str, Toast.LENGTH_LONG).show();
                                    // turn back to login page
                                    Intent intent = getIntent();
                                    intent.putExtra("id", id.getText().toString());
                                    setResult(200, intent);
                                    finish();
                                } else {
                                    str = "Reset password failed!";
                                    new_pwd.setText("");
                                    again_pwd.setText("");
                                    Toast.makeText(ResetActivity.this, str, Toast.LENGTH_LONG).show();
                                }
                                super.handleMessage(message);
                            }
                        };
                    } else {
                        // passwords are not the same
                        Toast.makeText(ResetActivity.this, "Passwords are different!", Toast.LENGTH_LONG).show();
                        new_pwd.setText("");
                        again_pwd.setText("");
                    }
                } else {
                    Toast.makeText(ResetActivity.this, "Null Input!", Toast.LENGTH_LONG).show();
                }
            } else if (btn_id == R.id.cancel) {
                new_pwd.setText("");
                again_pwd.setText("");
                // turn back to login page
                Intent intent = getIntent();
                intent.putExtra("id", id.getText().toString());
                setResult(200, intent);
                finish();
            }
        }
    }


}