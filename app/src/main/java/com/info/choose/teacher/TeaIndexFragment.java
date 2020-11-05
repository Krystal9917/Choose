package com.info.choose.teacher;


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
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.info.choose.GlobalData;
import com.info.choose.HttpUtils;
import com.info.choose.R;
import com.info.choose.student.StuFeedbackActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TeaIndexFragment extends Fragment {
    Handler handler;
    ListView list_view;
    String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_index, container, false);
        Intent intent = getActivity().getIntent();
        id = intent.getStringExtra("id");

        list_view = (ListView) view.findViewById(android.R.id.list);
        SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                getData(),
                R.layout.student_list_item,
                new String[]{"name", "major", "topic"},
                new int[]{R.id.name, R.id.major, R.id.topic});
        list_view.setAdapter(adapter);
        // add listener
        list_view.setOnItemClickListener(new MyListClick());
        return view;
    }

    private ArrayList getData() {
        ArrayList listItems = new ArrayList();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(GlobalData.INFO_TAG, "Enter getData()");
                Message message = handler.obtainMessage(200);
                JSONObject json = new JSONObject();
                try {
                    json.put("id", id);
                    // http request defined in the root path
                    String response = HttpUtils.request("/newReceive", json);
                    JSONObject result = new JSONObject(response);
                    JSONArray students = (JSONArray) result.get("students");
                    for (int i = 0; i < students.length(); i++) {
                        JSONObject student = students.getJSONObject(i);
                        Map<String, Object> results = new HashMap<>();
                        results.put("name", student.get("student_name"));
                        results.put("major", student.get("student_major"));
                        results.put("topic", student.get("topic"));
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
                if(listItems.size()!=0){
                    TextView view = getActivity().findViewById(R.id.no_data);
                    view.setText("");
                }
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
            String major = map.get("major");
            String topic = map.get("topic");
            Intent intent = new Intent(getActivity(), StuFeedbackActivity.class);
            // put data
            intent.putExtra("id",id);
            intent.putExtra("name",name);
            intent.putExtra("major",major);
            intent.putExtra("topic",topic);
            startActivity(intent);

        }
    }

}
