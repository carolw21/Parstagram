package me.carolwang.parstagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // get the data according to position
        final Post post = posts.get(position);

        holder.tvUser.setText(post.getUser().getUsername());
        holder.tvCaption.setText(post.getCaption());
        holder.tvTimestamp.setText(getRelativeTimeAgo(post.getDate().toString()));

        String likes = post.getLikes();
        Boolean liked = likes.contains(ParseUser.getCurrentUser().getUsername());
        if (liked) {
            holder.likeBtn.setBackground(context.getResources().getDrawable(R.drawable.ufi_heart_active));
            holder.likeBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.red_5));
        }
        else {
            holder.likeBtn.setBackground(context.getResources().getDrawable(R.drawable.ufi_heart));
            holder.likeBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.black));
        }
        if (likes.length() > 3) {
            holder.tvLikes.setText(likes + " liked this");
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.tvCaption.getLayoutParams();

            // Left Top Right Bottom Margin
            lp.setMargins(25,60,0,10);

            // Apply the updated layout parameters to TextView
            holder.tvCaption.setLayoutParams(lp);
        } else {
            holder.tvLikes.setText("");
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.tvCaption.getLayoutParams();

            // Left Top Right Bottom Margin
            lp.setMargins(25,20,0,10);

            // Apply the updated layout parameters to TextView
            holder.tvCaption.setLayoutParams(lp);
        }

        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean remove = false;
                if (!post.getLikes().contains(ParseUser.getCurrentUser().getUsername())) {
                    post.addLikes(ParseUser.getCurrentUser().getUsername());
                } else {
                    post.removeLikes(ParseUser.getCurrentUser().getUsername());
                    remove = true;
                }
                if (remove) {
                    holder.likeBtn.setBackground(context.getResources().getDrawable(R.drawable.ufi_heart));
                    holder.likeBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.black));
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.tvCaption.getLayoutParams();

                    // Left Top Right Bottom Margin
                    lp.setMargins(25,20,0,10);

                    // Apply the updated layout parameters to TextView
                    holder.tvCaption.setLayoutParams(lp);
                } else {
                    holder.likeBtn.setBackground(context.getResources().getDrawable(R.drawable.ufi_heart_active));
                    holder.likeBtn.setBackgroundTintList(context.getResources().getColorStateList(R.color.red_5));
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.tvCaption.getLayoutParams();

                    // Left Top Right Bottom Margin
                    lp.setMargins(25,60,0,10);

                    // Apply the updated layout parameters to TextView
                    holder.tvCaption.setLayoutParams(lp);
                }
                String likes = post.getLikes();
                if (likes.length() > 3) {
                    holder.tvLikes.setText(likes + " liked this");
                } else {
                    holder.tvLikes.setText("");
                }
            }
        });

        Glide.with(context)
                .load(post.getImage().getUrl())
                .into(holder.ivPost);

        if (post.getUser().getParseFile("profile") != null) {
            ParseFile imageFile = post.getUser().getParseFile("profile");
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(imageFile.getFile().getAbsolutePath());
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
            roundedBitmapDrawable.setCornerRadius(40.0f);
            roundedBitmapDrawable.setAntiAlias(true);
            holder.ivProfile.setImageDrawable(roundedBitmapDrawable);
        }
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
        ImageView ivProfile;
        Button likeBtn;
        TextView tvLikes;

        public ViewHolder(View itemView) {
            super(itemView);

            tvUser = itemView.findViewById(R.id.tvUser);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivPost = itemView.findViewById(R.id.ivPost);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            likeBtn = itemView.findViewById(R.id.heartBtn);
            tvLikes = itemView.findViewById(R.id.tvLikes);

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
