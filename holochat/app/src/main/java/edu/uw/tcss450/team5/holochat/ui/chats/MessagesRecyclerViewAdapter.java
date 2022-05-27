package edu.uw.tcss450.team5.holochat.ui.chats;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.team5.holochat.R;
import edu.uw.tcss450.team5.holochat.databinding.FragmentHomeBinding;
import edu.uw.tcss450.team5.holochat.ui.HomeFragmentDirections;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.MyViewHolder> {

    public String s1[],s2[];
    public int images[];
    public Context ctx;
    private FragmentHomeBinding binding;

    public MessagesRecyclerViewAdapter(Context ct, String s1[], String s2[], int[] images)
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
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(s1[position]);
        holder.preview.setText(s2[position]);
        holder.title.setText(images[position]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToChat(view);

            }
        });
    }

    private void navigateToChat(View view){
        //note this needs to be passed with user info
        int chatID = 0;
        String name = "Chatroom";
        @NonNull NavDirections directions = HomeFragmentDirections.actionNavigationHomeToChatRoomFragment(chatID, name);
        Navigation.findNavController(view).navigate(directions);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title,preview;
        ImageView image;
        ConstraintLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.chat_title);
            preview = itemView.findViewById(R.id.chat_preview);
            image = itemView.findViewById(R.id.chat_icon);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
