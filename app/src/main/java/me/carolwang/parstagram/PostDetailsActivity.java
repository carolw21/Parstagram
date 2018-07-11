package me.carolwang.parstagram;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import me.carolwang.parstagram.models.Post;

public class PostDetailsActivity extends AppCompatActivity {

    TextView tvUser;
    TextView tvCaption;
    TextView tvTimestamp;
    ImageView ivPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.nav_logo_whiteout);

        tvUser = findViewById(R.id.tvUser);
        tvCaption = findViewById(R.id.tvCaption);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        ivPost = findViewById(R.id.ivPost);

        Post post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        tvUser.setText(post.getUser().getUsername());
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
