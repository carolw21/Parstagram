package me.carolwang.parstagram;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import me.carolwang.parstagram.models.Post;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final List<Post> posts;
    Context context;

    // pass in the Tweets array in the constructor
    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        return new ViewHolder(
                inflater.inflate(R.layout.item_post, parent, false)
        );
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the data according to position
        Post post = posts.get(position);

        holder.tvUser.setText(post.getUser().getUsername());
        holder.tvCaption.setText(post.getCaption());
        holder.tvTimestamp.setText(getRelativeTimeAgo(post.getDate().toString()));

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

        TextView tvUser;
        TextView tvCaption;
        TextView tvTimestamp;
        ImageView ivPost;

        public ViewHolder(View itemView) {
            super(itemView);

            tvUser = itemView.findViewById(R.id.tvUser);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
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
                Post post = posts.get(position);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Post.class.getSimpleName(), Parcels.wrap(post)); //any string to be sent
                HomeActivity activity = (HomeActivity) context;
                activity.saveData(1, bundle);
                activity.transition();
            }
        }
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
