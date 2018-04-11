package com.example.lenovo_pc.shortstory.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo_pc.shortstory.R;
import com.example.lenovo_pc.shortstory.databases.model.StoryModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Lenovo-PC on 3/7/2018.
 */

public class StoryAdapter extends BaseAdapter {
    private List<StoryModel> storyModelList;
    Context context;

    public StoryAdapter(List<StoryModel> storyModelList, Context context) {
        this.storyModelList = storyModelList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return storyModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return storyModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        view = layoutInflater.inflate(R.layout.item_story, viewGroup, false);

        TextView txt_title = view.findViewById(R.id.txt_title);
        TextView txt_author = view.findViewById(R.id.txt_author);
        ImageView ivImage = view.findViewById(R.id.img_story);

        txt_title.setText(storyModelList.get(i).title);
        txt_author.setText(storyModelList.get(i).author);
        Picasso.with(context).load(storyModelList.get(i).image).fit().into(ivImage);
        return view;
    }
}
