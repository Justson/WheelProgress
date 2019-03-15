package com.wheelloading.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wheelloading.library.WheelProgress;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        final View view = this.findViewById(R.id.wheelprogress);
        ListView listView = this.findViewById(R.id.listview);
        listView.setAdapter(new TestAdapter());
    }

    class TestAdapter extends BaseAdapter {

        @Override
        public Object getItem(int position) {
            return new Object();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(MainActivity.this, R.layout.item_layout, null);
            }
            boolean isGone = position % 3 == 0;
            View view = convertView.findViewById(R.id.wheelprogress);
            View c = convertView.findViewById(R.id.wheelContainer);
            int visibility = isGone ? View.GONE : View.VISIBLE;
            c.setVisibility(visibility);
            return convertView;
        }
    }
}
