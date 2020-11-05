package com.info.choose;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.info.choose.entity.User;
import com.info.choose.student.StuFragActivity;
import com.info.choose.teacher.TeaFragActivity;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    EditText id, password;
    CheckBox teacher_type, student_type, policy_agree;
    Button login, forget;
    boolean type_chose, policy_chose = true;
    // 1 for teacher, 0 for student
    int type;
    User user;
    Handler handler;
    String global;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("UserInfo", Activity.MODE_PRIVATE);
        PreferenceManager.getDefaultSharedPreferences(this);
        String user_id = sharedPreferences.getString("id","");
        String name = sharedPreferences.getString("name", "");
        int type = sharedPreferences.getInt("type", 2);
        String sex = sharedPreferences.getString("sex", "");
        String major = sharedPreferences.getString("major","");
        String grade = sharedPreferences.getString("grade","");
        Log.i(GlobalData.INFO_TAG+"---Type---",String.valueOf(type));

        if(!(user_id.equals("") && name.equals("")) && type != 2){
            Intent intent;
            if(type == 0){
                intent = new Intent(MainActivity.this, StuFragActivity.class);
                intent.putExtra("id", user_id);
                intent.putExtra("name", name);
                intent.putExtra("sex", sex);
                intent.putExtra("major", major);
                intent.putExtra("grade", grade);
                startActivity(intent);
            }
            else if(type == 1){

                intent = new Intent(MainActivity.this, TeaFragActivity.class);
                intent.putExtra("id", user_id);
                intent.putExtra("name", name);
                intent.putExtra("sex", sex);
                intent.putExtra("major", major);
                startActivity(intent);
            }
        }
        else{
            // logo
            imageView = findViewById(R.id.logo);
            imageView.setImageResource(R.mipmap.first_logo);
            // input information
            id = findViewById(R.id.id_input);
            password = findViewById(R.id.pwd_input);
            // type checkbox
            teacher_type = findViewById(R.id.teacher_type);
            student_type = findViewById(R.id.student_type);
            // policy checkbox
            policy_agree = findViewById(R.id.policy);
            // button
            login = findViewById(R.id.login);
            forget = findViewById(R.id.forget);

            // add listener
            teacher_type.setOnCheckedChangeListener(new CheckBoxListener());
            student_type.setOnCheckedChangeListener(new CheckBoxListener());
            policy_agree.setOnCheckedChangeListener(new CheckBoxListener());

            login.setOnClickListener(new ButtonListener());
            forget.setOnClickListener(new ButtonListener());
        }

    }

    class CheckBoxListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                if (compoundButton.getId() == R.id.teacher_type) {
                    type_chose = true;
                    type = 1;
                    student_type.setChecked(false);
                } else if (compoundButton.getId() == R.id.student_type) {
                    type_chose = true;
                    type = 0;
                    teacher_type.setChecked(false);
                } else if (compoundButton.getId() == R.id.policy) {
                    policy_chose = true;
                }
            } else {
                if (compoundButton.getId() == R.id.teacher_type || compoundButton.getId() == R.id.student_type) {
                    type_chose = false;
                } else if (compoundButton.getId() == R.id.policy) {
                    policy_chose = false;
                }
            }

        }
    }

    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int btn_id = view.getId();
            if (btn_id == R.id.login) {
                if (id.getText() != null && password.getText() != null && type_chose && policy_chose) {
                    // start thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = handler.obtainMessage(200);
                            JSONObject json = new JSONObject();
                            try {
                                json.put("id", id.getText().toString());
                                json.put("pwd", password.getText().toString());
                                json.put("type", type);
                                global = HttpUtils.request("/login", json);
                                handler.sendMessage(message);
                            } catch (Exception e) {
                                Log.i("Thread---" + GlobalData.INFO_TAG, e.toString());
                            }
                        }
                    }).start();

                    handler = new Handler(Looper.myLooper()) {
                        @Override
                        public void handleMessage(Message message) {
                            if (global != null) {
                                // match information
                                try {
                                    JSONObject result = new JSONObject(global);
                                    JSONObject object = result.getJSONObject("user");
                                    user = new User(object.getString("id"), object.getString("name"),
                                            object.getString("sex"), object.getString("major"), object.getString("grade"));
                                    SharedPreferences sp = getSharedPreferences("UserInfo", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putInt("type", type);
                                    Log.i(GlobalData.INFO_TAG+"---Store Type---",String.valueOf(type));
                                    if (type == 0) {
                                        Intent intent = new Intent(MainActivity.this, StuFragActivity.class);
                                        intent.putExtra("id", user.getId());
                                        intent.putExtra("name", user.getName());
                                        intent.putExtra("sex", user.getSex());
                                        intent.putExtra("major", user.getMajor());
                                        intent.putExtra("grade", user.getGrade());

                                        editor.putString("grade", user.getGrade());

                                        password.setText("");
                                        student_type.setChecked(false);
                                        startActivity(intent);
                                    } else if (type == 1) {
                                        Intent intent = new Intent(MainActivity.this, TeaFragActivity.class);
                                        intent.putExtra("id", user.getId());
                                        intent.putExtra("name", user.getName());
                                        intent.putExtra("sex", user.getSex());
                                        intent.putExtra("major", user.getMajor());
                                        password.setText("");
                                        teacher_type.setChecked(false);
                                        startActivity(intent);
                                    }
                                    editor.putString("id", user.getId());
                                    editor.putString("name", user.getName());
                                    editor.putString("sex", user.getSex());
                                    editor.putString("major", user.getMajor());
                                    editor.apply();
                                    Toast.makeText(MainActivity.this, "登录成功！", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Log.i("Response---" + GlobalData.ERROR_TAG, e.getMessage());
                                }
                            } else {
                                password.setText("");
                                Toast.makeText(MainActivity.this, "登录失败，用户不存在或密码错误！", Toast.LENGTH_LONG).show();
                            }

                            super.handleMessage(message);
                        }
                    };
                } else {
                    Toast.makeText(MainActivity.this, "Null Input!", Toast.LENGTH_LONG).show();
                }
            } else if (btn_id == R.id.forget && type_chose && policy_chose) {
                if (id.getText() != null) {
                    // to reset password page
                    Intent intent = new Intent(MainActivity.this, ResetActivity.class);
                    intent.putExtra("id", id.getText().toString());
                    intent.putExtra("type",type);
                    startActivityForResult(intent, 200);
                } else {
                    Toast.makeText(MainActivity.this, "还未填写完！", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(MainActivity.this, "还未填写完！", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            Log.i(GlobalData.INFO_TAG+"---ID",data.getStringExtra("id"));
            id.setText(data.getStringExtra("id"));
        }
    }

}