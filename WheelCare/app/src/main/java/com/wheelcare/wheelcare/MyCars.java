package com.wheelcare.wheelcare;

import android.app.Activity;
import android.content.Context;
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

import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.wheelcare.wheelcare.R;

import java.util.ArrayList;
import java.util.List;


public class MyCars extends Fragment {

    public static final String[] carregistrations = new String[] { "KA04JD1234", "KA04JD1234", "KA04JD1234" };

    public static final String[] carvariants = new String[] {"Petrol","Diesel","Electric"};

    public static final Integer[] images = { R.drawable.myalto,
            R.drawable.myalto, R.drawable.myalto};

    ListView listView;
    List<RowItem> rowItems;
    View view;


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


        rowItems = new ArrayList<RowItem>();
        for (int i = 0; i < carregistrations.length; i++) {
            RowItem item = new RowItem(images[i], carregistrations[i], carvariants[i]);
            rowItems.add(item);
        }

        listView = (ListView) view.findViewById(R.id.list);
        CustomListViewAdapter adapter = new CustomListViewAdapter(getActivity().getApplicationContext(), R.layout.mycars_list_item, rowItems);
        listView.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.plus);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                Intent i = new Intent(this, Activity.class);
//                startActivity(i);
            }
        });
    }


    public class CustomListViewAdapter extends BaseSwipeAdapter {

        Context context;
        List<RowItem> items;
        int resourceId;

        public CustomListViewAdapter(Context context, int resourceId,
                                     List<RowItem> items) {
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
               // swipeLayout.setSwipeEnabled(false);
                swipeLayout.addSwipeListener(new SimpleSwipeListener() {
                    @Override
                    public void onOpen(SwipeLayout layout) {
//                        YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
                    }
                });
                convertView.findViewById(R.id.trash).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity().getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                        //call backend to delete item
                    }
                });
                holder = new ViewHolder();
                holder.txtDesc = (TextView) convertView.findViewById(R.id.registrationnumber);
                holder.txtTitle = (TextView) convertView.findViewById(R.id.carvariant);
                holder.imageView = (ImageView) convertView.findViewById(R.id.carimage);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            holder.txtDesc.setText(rowItems.get(position).getDesc());
            holder.txtTitle.setText(rowItems.get(position).getTitle());
            holder.imageView.setImageResource(rowItems.get(position).getImageId());

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

//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder holder = null;
//
//            LayoutInflater mInflater = (LayoutInflater) context
//                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//            if (convertView == null) {
//                convertView = mInflater.inflate(R.layout.mycars_list_item, null);
//                holder = new ViewHolder();
//                holder.txtDesc = (TextView) convertView.findViewById(R.id.registrationnumber);
//                holder.txtTitle = (TextView) convertView.findViewById(R.id.carvariant);
//                holder.imageView = (ImageView) convertView.findViewById(R.id.carimage);
//                convertView.setTag(holder);
//            } else
//                holder = (ViewHolder) convertView.getTag();
//
//            holder.txtDesc.setText(rowItems.());
//            holder.txtTitle.setText(rowItem.getTitle());
//            holder.imageView.setImageResource(rowItem.getImageId());
//
//            return convertView;
//        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }




    public class RowItem {
        private int carimageId;
        private String registrationNumber;
        private String carVariant;
        public RowItem(int carimageId, String registrationNumber, String carVariant) {
            this.carimageId = carimageId;
            this.registrationNumber = registrationNumber;
            this.carVariant = carVariant;
        }
        public int getImageId() {
            return carimageId;
        }
        public void setImageId(int imageId) {
            this.carimageId = carimageId;
        }
        public String getDesc() {
            return registrationNumber;
        }
        public void setDesc(String desc) {
            this.registrationNumber = registrationNumber;
        }
        public String getTitle() {
            return carVariant;
        }
        public void setTitle(String title) {
            this.carVariant = carVariant;
        }
    }


}

