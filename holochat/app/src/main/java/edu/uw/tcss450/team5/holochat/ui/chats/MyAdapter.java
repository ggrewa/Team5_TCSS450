package edu.uw.tcss450.team5.holochat.ui.chats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.team5.holochat.R;
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public String s1[],s2[];
    public int images[];
    public Context ctx;

    public MyAdapter(Context ct, String s1[], String s2[], int[] images)
    {
        this.ctx=ct;
        this.images = images;
        this.s1=s1;
        this.s2=s2;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.ctx);
        View view = inflater.inflate(R.layout.layout_chatroom_row,parent,false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(s1[position]);
        holder.preview.setText(s2[position]);
        holder.title.setText(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title,preview;
        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.chat_title);
            preview = itemView.findViewById(R.id.chat_preview);
            image = itemView.findViewById(R.id.chat_icon);
        }
    }
}
