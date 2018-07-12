package me.carolwang.parstagram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.carolwang.parstagram.models.Post;
import me.carolwang.parstagram.utils.BitmapScaler;

public class PostActivity extends AppCompatActivity {

    EditText etCaption;
    ParseFile imageFile;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.nav_logo_whiteout);

        pb = findViewById(R.id.pbLoading);

        String imagePath = getIntent().getStringExtra("bitmap_path");
        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(imageBitmap, 300);
        // Configure byte output stream
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Compress the image further
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            // Write the bytes of the bitmap to file
            try {
                fos.write(bytes.toByteArray());
                fos.close();
                imageFile = new ParseFile(new File(imagePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            Log.i("asdf", "error");
            e.printStackTrace();
        }
        ImageView ivPreview = findViewById(R.id.ivPreview);
        ivPreview.setImageBitmap(resizedBitmap);

        etCaption = findViewById(R.id.etCaption);
    }

    public void onPost(View v) {
        pb.setVisibility(ProgressBar.VISIBLE);
        Post post = new Post(imageFile, etCaption.getText().toString(), ParseUser.getCurrentUser());
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //HomeActivity.fetchTimelineAsync(0);
                    pb.setVisibility(ProgressBar.INVISIBLE);
                    finish();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}
