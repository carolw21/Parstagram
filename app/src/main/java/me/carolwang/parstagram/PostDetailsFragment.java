package me.carolwang.parstagram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import java.util.Locale;

import me.carolwang.parstagram.models.Post;

public class PostDetailsFragment extends Fragment {

    TextView tvUser;
    TextView tvCaption;
    TextView tvTimestamp;
    ImageView ivPost;
    ImageView ivProfile;
    Button likeBtn;
    Boolean liked;
    TextView tvLikes;

    public PostDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_details, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUser = view.findViewById(R.id.tvUser);
        tvCaption = view.findViewById(R.id.tvCaption);
        tvTimestamp = view.findViewById(R.id.tvTimestamp);
        ivPost = view.findViewById(R.id.ivPost);
        ivProfile = view.findViewById(R.id.ivProfile);
        likeBtn = view.findViewById(R.id.heartBtn);
        tvLikes = view.findViewById(R.id.tvLikes);

        HomeActivity activity = (HomeActivity) getActivity();
        Bundle savedData = activity.getSavedData();
        final Post post = Parcels.unwrap(savedData.getParcelable(Post.class.getSimpleName()));

        tvUser.setText(ParseUser.getCurrentUser().getUsername());
        tvCaption.setText(post.getCaption());
        tvTimestamp.setText(getRelativeTimeAgo(post.getDate().toString()));

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean remove = false;
                if (!post.getLikes().contains(ParseUser.getCurrentUser().getUsername())) {
                    post.addLikes(ParseUser.getCurrentUser().getUsername());
                }
                else {
                    post.removeLikes(ParseUser.getCurrentUser().getUsername());
                    remove = true;
                }
                if (remove) {
                    likeBtn.setBackground(getActivity().getResources().getDrawable(R.drawable.ufi_heart));
                    likeBtn.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.black));
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvCaption.getLayoutParams();

                    // Left Top Right Bottom Margin
                    lp.setMargins(25,0,0,10);

                    // Apply the updated layout parameters to TextView
                    tvCaption.setLayoutParams(lp);
                } else {
                    likeBtn.setBackground(getActivity().getResources().getDrawable(R.drawable.ufi_heart_active));
                    likeBtn.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.red_5));
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvCaption.getLayoutParams();

                    // Left Top Right Bottom Margin
                    lp.setMargins(25,30,0,10);

                    // Apply the updated layout parameters to TextView
                    tvCaption.setLayoutParams(lp);
                }
                String likes = post.getLikes();
                if (likes.length() > 3) {
                    tvLikes.setText(likes + " liked this");
                } else {
                    tvLikes.setText("");
                }
            }
        });

        Glide.with(this)
                .load(post.getImage().getUrl())
                .into(ivPost);

        try {
            if (post.getUser().getParseFile("profile") != null) {
                ParseFile imageFile = post.getUser().getParseFile("profile");
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeFile(imageFile.getFile().getAbsolutePath());
                } catch (com.parse.ParseException e) {
                    e.printStackTrace();
                }
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), bitmap);
                roundedBitmapDrawable.setCornerRadius(40.0f);
                roundedBitmapDrawable.setAntiAlias(true);
                ivProfile.setImageDrawable(roundedBitmapDrawable);
            }
        } catch(IllegalStateException e) {
            if (ParseUser.getCurrentUser().getParseFile("profile") != null) {
                ParseFile imageFile = ParseUser.getCurrentUser().getParseFile("profile");
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeFile(imageFile.getFile().getAbsolutePath());
                } catch (com.parse.ParseException j) {
                    j.printStackTrace();
                }
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), bitmap);
                roundedBitmapDrawable.setCornerRadius(40.0f);
                roundedBitmapDrawable.setAntiAlias(true);
                ivProfile.setImageDrawable(roundedBitmapDrawable);
            }
        }
        liked = post.getLikes().contains(ParseUser.getCurrentUser().getUsername());
        if (liked) {
            likeBtn.setBackground(getActivity().getResources().getDrawable(R.drawable.ufi_heart_active));
            likeBtn.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.red_5));
        }
        String likes = post.getLikes();
        if (likes.length() > 3) {
            tvLikes.setText(likes + " liked this");
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvCaption.getLayoutParams();

            // Left Top Right Bottom Margin
            lp.setMargins(25,30,0,10);

            // Apply the updated layout parameters to TextView
            tvCaption.setLayoutParams(lp);
        } else {
            tvLikes.setText("");
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tvCaption.getLayoutParams();

            // Left Top Right Bottom Margin
            lp.setMargins(25,0,0,10);

            // Apply the updated layout parameters to TextView
            tvCaption.setLayoutParams(lp);
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
