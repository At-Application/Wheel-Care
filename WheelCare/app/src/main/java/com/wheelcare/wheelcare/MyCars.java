package com.wheelcare.wheelcare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.wheelcare.wheelcare.R;

import java.util.ArrayList;
import java.util.List;

public class MyCars extends Fragment {

    ListView listView;
    ArrayList<Vehicle> rowItems;
    View view;
    CustomListViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        view = inflater.inflate(R.layout.my_cars, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rowItems = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.list);
        adapter = new CustomListViewAdapter(getActivity().getApplicationContext(), R.layout.mycars_list_item, rowItems);
        listView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.plus);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(), CarRegistration.class);
                i.putExtra("MyCars", true);
                startActivity(i);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        rowItems = ((GlobalClass)getActivity().getApplicationContext()).getCarList();
        adapter.notifyDataSetChanged();
    }

    public class CustomListViewAdapter extends BaseSwipeAdapter {

        Context context;
        ArrayList<Vehicle> items;
        int resourceId;

        public CustomListViewAdapter(Context context, int resourceId,
                                     ArrayList<Vehicle> items) {
            //super(context, resourceId, items);
            this.context = context;
            this.items = items;
            this.resourceId = resourceId;
        }

        /*private view holder class*/
        private class ViewHolder {
            ImageView imageView;
            TextView txtTitle;
            TextView txtDesc;
        }

        @Override
        public int getSwipeLayoutResourceId(int pos) {
            return R.id.swipe;
        }

        @Override
        public View generateView(final int position, ViewGroup viewGroup) {
            ViewHolder holder = null;
            View convertView=null;

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.mycars_list_item, null);
                SwipeLayout swipeLayout = (SwipeLayout)convertView.findViewById(getSwipeLayoutResourceId(position));
                swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                    @Override
                    public void onOpen(SwipeLayout layout) {
                        YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                    }
                });
                convertView.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity().getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                        rowItems.remove(position);
                        notifyDataSetChanged();
                    }
                });
                holder = new ViewHolder();
                holder.txtDesc = (TextView) convertView.findViewById(R.id.registrationnumber);
                holder.txtTitle = (TextView) convertView.findViewById(R.id.carvariant);
                holder.imageView = (ImageView) convertView.findViewById(R.id.carimage);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            holder.txtDesc.setText(rowItems.get(position).type);
            holder.txtTitle.setText(rowItems.get(position).registration_number);
            holder.imageView = rowItems.get(position).image;

            return convertView;
        }

        @Override
        public void fillValues(int i, View view) {

        }

        @Override
        public int getCount() {
            if(rowItems.size() > 0)
            return rowItems.size();
            else
                return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}

