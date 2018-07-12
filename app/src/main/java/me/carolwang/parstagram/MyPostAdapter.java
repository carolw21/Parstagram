package me.carolwang.parstagram;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.List;

import me.carolwang.parstagram.models.Post;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.ViewHolder> {

    private final List<Post> posts;
    Context context;

    // pass in the Tweets array in the constructor
    public MyPostAdapter(List<Post> posts) {
        this.posts = posts;
    }



    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(
                inflater.inflate(R.layout.item_mypost, parent, false)
        );
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the data according to position
        Post post = posts.get(position);

        Glide.with(context)
                .load(post.getImage().getUrl())
                .into(holder.ivPost);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivPost;

        public ViewHolder(View itemView) {
            super(itemView);

            ivPost = itemView.findViewById(R.id.ivPost);

            itemView.setOnClickListener(this);
        }

        // when the user clicks on a row, show PostDetailsActivity for the selected movie
        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Post post = posts.get(position);

                /*// create intent for the new activity
                Intent intent = new Intent(context, PostDetailsActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                // show the activity
                context.startActivity(intent);*/
                Bundle bundle = new Bundle();
                bundle.putParcelable(Post.class.getSimpleName(), Parcels.wrap(post)); //any string to be sent
                HomeActivity activity = (HomeActivity) context;
                activity.saveData(2, bundle);
                activity.transition();
            }
        }
    }

}
