package com.hack.android.medassist.chatBot.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hack.android.medassist.R;
import com.hack.android.medassist.chatBot.model.ResponseMessage;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by deathcode on 26/01/18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.CustomViewHolder> {

    List<ResponseMessage> responseMessages;
    Context context;
    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;
        public CustomViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textMessage);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public MessageAdapter(List<ResponseMessage> responseMessages, Context context) {
        this.responseMessages = responseMessages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(responseMessages.get(position).isMe()){
            return R.layout.me_bubble;
        }
        return R.layout.bot_bubble;
    }

    @Override
    public int getItemCount() {
        return  responseMessages.size();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.textView.setText(responseMessages.get(position).getText());
        /*Uri uri = Uri.parse("https://s3.amazonaws.com/CAPS-SSE/echo_developer/e7bb/a4e9499cd95948a4b9f51208262b7330/APP_ICON?versionId=wCnTwWy0S7M3Mt7YrfIo3ANkW1SUbG1T&AWSAccessKeyId=AKIAJFEYRBGIHK2BBYKA&Expires=1549957358&Signature=wRpOUjq1sIgJzQm7XlMj4FK90Fw%3D");
        holder.imageView.setImageURI(uri);*/
        if(responseMessages.get(position).getImageUrl() == null ||
          responseMessages.get(position).getImageUrl().isEmpty()) {
            holder.imageView.setMaxHeight(0);
            holder.imageView.setMaxWidth(0);
            holder.imageView.setMinimumHeight(0);
            holder.imageView.setMinimumWidth(0);
        }
        Picasso.with(context).load(responseMessages.get(position).getImageUrl()).resize(200,250).into(holder.imageView);
    }
}
