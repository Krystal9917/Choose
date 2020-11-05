package com.info.choose.student;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.info.choose.HttpUtils;
import com.info.choose.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StuApplyLogActivity extends ListActivity {
    ListView listView;
    ArrayList listItems;
    SimpleAdapter adapter;
    Handler handler;
    String student_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_log);

        Intent intent = getIntent();
        student_id = intent.getStringExtra("id");

        listItems = new ArrayList();
        listView = findViewById(android.R.id.list);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage(200);
                JSONObject json = new JSONObject();
                try {
                    json.put("student_id", student_id);
                    // http request defined in the root path
                    String response = HttpUtils.request("/requestLogs", json);
                    JSONObject result = new JSONObject(response);
                    JSONArray logs = (JSONArray) result.get("logs");
                    for (int i = 0; i < logs.length(); i++) {
                        JSONObject log = logs.getJSONObject(i);
                        Map<String, Object> results = new HashMap<>();
                        results.put("name", log.get("teacher_name"));
                        results.put("topic", log.get("topic"));
                        results.put("status", log.get("status"));
                        results.put("request_extra", log.get("request_extra"));
                        results.put("response_extra", log.get("response_extra"));
                        listItems.add(results);
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
                if(listItems.size() != 0){
                    adapter = new SimpleAdapter(StuApplyLogActivity.this,
                            listItems,
                            R.layout.apply_list_item,
                            new String[]{"name", "topic", "status", "request_extra", "response_extra"},
                            new int[]{R.id.name, R.id.topic, R.id.status, R.id.request_extra, R.id.response_extra});
                    setListAdapter(adapter);
                    TextView text = findViewById(R.id.no_data);
                    text.setText("");
                }
                else{
                    TextView text = findViewById(R.id.no_data);
                    TextView view = findViewById(R.id.notice);
                    view.setText("");
                    text.setText("我还未发起过申请！");
                }
                super.handleMessage(message);
            }
        };

    }
}
