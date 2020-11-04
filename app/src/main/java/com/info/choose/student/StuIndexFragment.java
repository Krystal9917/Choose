package com.info.choose.student;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.fragment.app.Fragment;

import com.info.choose.GlobalData;
import com.info.choose.HttpUtils;
import com.info.choose.R;
import com.info.choose.teacher.TeacherInfoActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StuIndexFragment extends Fragment {
    Handler handler;
    ListView list_view;
    String url,student_id,major;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.student_index, container, false);
        Intent intent = getActivity().getIntent();
        student_id = intent.getStringExtra("id");
        major = intent.getStringExtra("major");

        list_view = (ListView) view.findViewById(android.R.id.list);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                getData(),
                R.layout.teacher_list_item,
                new String[]{"name", "level", "left"},
                new int[]{R.id.name, R.id.level, R.id.left});
        list_view.setAdapter(adapter);
        // add listener
        list_view.setOnItemClickListener(new MyListClick());
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private ArrayList getData() {
        ArrayList listItems = new ArrayList();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(GlobalData.INFO_TAG, "Enter Run()");
                Message message = handler.obtainMessage(200);
                JSONObject json = new JSONObject();
                try {
                    json.put("major", major);
                    // http request defined in the root path
                    String response = HttpUtils.request("/teachers", json);
                    JSONObject result = new JSONObject(response);
                    JSONArray teachers = (JSONArray) result.get("teachers");
                    for (int i = 0; i < teachers.length(); i++) {
                        JSONObject teacher = teachers.getJSONObject(i);
                        Map<String, Object> results = new HashMap<>();
                        results.put("name", teacher.get("name"));
                        results.put("level", teacher.get("title"));
                        results.put("left", teacher.getInt("max") - teacher.getInt("now"));
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
                Log.i(GlobalData.INFO_TAG, "Finish!");
                super.handleMessage(message);
            }
        };

        return listItems;
    }

    class MyListClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            HashMap<String, String> map = (HashMap<String, String>) list_view.getItemAtPosition(i);
            String name = map.get("name");

            final int[] have_tutor = {0};
            final int[] flag = new int[1];
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message message = handler.obtainMessage(200);
                    JSONObject json = new JSONObject();
                    try {
                        json.put("name", name);
                        // http request defined in the root path
                        String response = HttpUtils.request("/url",json);
                        JSONObject result = new JSONObject(response);
                        url = result.getString("url");
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            handler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message message) {
                    Intent intent = new Intent(getActivity(), TeacherInfoActivity.class);
                    intent.putExtra("teacher_name",name);
                    intent.putExtra("student_id",student_id);
                    intent.putExtra("url",url);
                    startActivity(intent);
                    super.handleMessage(message);
                }
            };
        }
    }
}
