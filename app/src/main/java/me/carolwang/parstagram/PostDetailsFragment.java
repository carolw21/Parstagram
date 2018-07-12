package me.carolwang.parstagram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

        //Post post = Parcels.unwrap(getArguments().getIntent().getParcelableExtra(Post.class.getSimpleName()));

        HomeActivity activity = (HomeActivity) getActivity();
        Bundle savedData = activity.getSavedData();
        Post post = Parcels.unwrap(savedData.getParcelable(Post.class.getSimpleName()));

        tvUser.setText(ParseUser.getCurrentUser().getUsername());
        tvCaption.setText(post.getCaption());
        tvTimestamp.setText(getRelativeTimeAgo(post.getDate().toString()));

        Glide.with(this)
                .load(post.getImage().getUrl())
                .into(ivPost);
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
