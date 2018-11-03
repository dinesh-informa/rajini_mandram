package com.dinesh.androidapp.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.dinesh.androidapp.R;
import com.dinesh.androidapp.model.Users;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;

public class UserListAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context mContext;
    List<Users> users = new ArrayList<>();

    public UserListAdapter(Context mContext, PaginatedList<Users> users) {
        this.mContext = mContext;
        this.users = users;
        inflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        Users user=users.get(i);
        if (view == null) {
            view = inflater.inflate(R.layout.user_list_item, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();
        holder.phNo.setText(user.getPhoneNumber());
        holder.txtLocality.setText(user.getLocality());
        holder.txtName.setText(user.getName());
        if(user.getImageUrl()==null){
            Picasso.get().load("https://s3.amazonaws.com/app-user-pic/"+user.getName()).into(holder.imageView);
        }else
        Picasso.get().load(user.getImageUrl())
                .transform(new CropCircleTransformation()).into(holder.imageView);


        return view;
    }

    static class ViewHolder {
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.phNo)
        TextView phNo;
        @BindView(R.id.txtLocality)
        TextView txtLocality;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
