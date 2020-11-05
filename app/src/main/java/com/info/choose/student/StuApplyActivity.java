package com.info.choose.student;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.info.choose.GlobalData;
import com.info.choose.HttpUtils;
import com.info.choose.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StuApplyActivity extends ListActivity {
    Handler handler;
    List<String> list;
    ListView listView;
    MyAdapter adapter;
    Button submit, cancel;
    EditText extra_content;
    String teacher_name, student_id;
    Map<String, Integer> topics_map;
    int chose_id = 0;
    String toast,have_tutor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_apply);

        Intent intent = getIntent();
        teacher_name = intent.getStringExtra("teacher_name");
        student_id = intent.getStringExtra("student_id");
        have_tutor = intent.getStringExtra("have_tutor");

        listView = findViewById(android.R.id.list);
        list = new ArrayList<>();
        topics_map = new HashMap<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(GlobalData.INFO_TAG, "Enter Run()");
                Message message = handler.obtainMessage(200);
                JSONObject json = new JSONObject();
                try {
                    json.put("name", teacher_name);
                    // http request defined in the root path
                    String response = HttpUtils.request("/topics", json);
                    JSONObject result = new JSONObject(response);
                    JSONArray topics = (JSONArray) result.get("topics");
                    for (int i = 0; i < topics.length(); i++) {
                        JSONObject topic = topics.getJSONObject(i);
                        topics_map.put(topic.get("topic").toString(), Integer.parseInt(topic.get("topic_id").toString()));
                        list.add(topic.get("topic").toString());
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
                if (list.size() != 0) {
                    adapter = new MyAdapter(list,StuApplyActivity.this);
                    setListAdapter(adapter);
                } else {
                    listView.setEmptyView(findViewById(R.id.no_data));
                }
                super.handleMessage(message);
            }
        };

        submit = findViewById(R.id.submit);
        cancel = findViewById(R.id.cancel);
        extra_content = findViewById(R.id.extra);

        submit.setOnClickListener(new BtnListener());
        cancel.setOnClickListener(new BtnListener());

    }


    class BtnListener implements View.OnClickListener {
        JSONObject json;

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.submit) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(GlobalData.INFO_TAG, "Enter Run()");
                        Message message = handler.obtainMessage(200);
                        if (chose_id != 0) {
                            if (Integer.parseInt(have_tutor) == 0) {
                                json = new JSONObject();
                                try {
                                    json.put("teacher_name", teacher_name);
                                    json.put("student_id", student_id);
                                    json.put("topic_id", chose_id);
                                    json.put("extra_content", extra_content.getText().toString());
                                    // http request defined in the root path
                                    String response = HttpUtils.request("/submit", json);
                                    JSONObject result = new JSONObject(response);
                                    if (result.getInt("status") == 200) {
                                        toast = "申请成功！";
                                    } else if (result.getInt("status") == 400) {
                                        toast = "提交失败！";
                                    } else {
                                        toast = "网络异常！";
                                    }
                                } catch (Exception e) {
                                    Log.i(GlobalData.INFO_TAG, e.toString());
                                }
                            } else {
                                toast = "你已经选过老师啦！";
                            }
                        } else {
                            toast = "你还未选题！";
                        }
                        handler.sendMessage(message);
                    }
                }).start();

                handler = new Handler(Looper.myLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Log.i(GlobalData.INFO_TAG, "Handle Successfully!");
                        Toast.makeText(StuApplyActivity.this, toast, Toast.LENGTH_LONG).show();
                        super.handleMessage(message);
                    }
                };
            }
            // finish the activity
            else {
                finish();
            }


        }
    }

    // refer from blog https://blog.csdn.net/tiger_gy/article/details/82496802
    class MyAdapter extends BaseAdapter {
        private List<String> text;
        private Context context;
        // store status
        HashMap<String, Boolean> states = new HashMap<String, Boolean>();

        public MyAdapter(List<String> listText, Context context) {
            this.text = listText;
            this.context = context;
        }

        @Override
        public int getCount() {
            return text.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = View.inflate(context, R.layout.topic_list_item, null);
            } else {
                view = convertView;
            }
            TextView radioText = view.findViewById(R.id.topic_name);
            RadioButton radioButton = view.findViewById(R.id.radio_button);
            radioText.setText(text.get(position));
            chose_id = topics_map.get(text.get(position));
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // assure only one button will be chosen every time
                    for (String key : states.keySet()) {
                        states.put(key, false);
                    }
                    states.put(String.valueOf(position), true);
                    MyAdapter.this.notifyDataSetChanged();
                }
            });
            boolean res = false;
            if (states.get(String.valueOf(position)) == null || states.get(String.valueOf(position)) == false) {
                res = false;
                states.put(String.valueOf(position), false);
            } else
                res = true;

            radioButton.setChecked(res);
            return view;
        }
    }

}
