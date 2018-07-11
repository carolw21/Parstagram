package me.carolwang.parstagram;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.carolwang.parstagram.models.Post;

public class HomeActivity extends AppCompatActivity {

    public final static String APP_TAG = "Parstagram";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "newphoto.jpg";
    File photoFile;
    RecyclerView rvPosts;
    static PostAdapter postAdapter;
    ArrayList<Post> posts;
    static SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.nav_logo_whiteout);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment fragment1 = new HomeFragment();
        final Fragment fragment2 = new PostFragment();
        final Fragment fragment3 = new UserFragment();

        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        //fragmentTransaction.replace(R.id.flContainer, fragment1).commit();
                        bottomNavigationView.getMenu().findItem(R.id.home).setIcon(R.drawable.instagram_home_filled_24);
                        bottomNavigationView.getMenu().findItem(R.id.user).setIcon(R.drawable.instagram_user_outline_24);
                        bottomNavigationView.getMenu().findItem(R.id.post).setIcon(R.drawable.instagram_new_post_outline_24);
                        return true;
                    case R.id.post:
                        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        //fragmentTransaction.replace(R.id.flContainer, fragment2).commit();
                        bottomNavigationView.getMenu().findItem(R.id.post).setIcon(R.drawable.instagram_new_post_filled_24);
                        bottomNavigationView.getMenu().findItem(R.id.home).setIcon(R.drawable.instagram_home_outline_24);
                        bottomNavigationView.getMenu().findItem(R.id.user).setIcon(R.drawable.instagram_user_outline_24);
                        return true;
                    case R.id.user:
                        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        //fragmentTransaction.replace(R.id.flContainer, fragment3).commit();
                        bottomNavigationView.getMenu().findItem(R.id.user).setIcon(R.drawable.instagram_user_filled_24);
                        bottomNavigationView.getMenu().findItem(R.id.home).setIcon(R.drawable.instagram_home_outline_24);
                        bottomNavigationView.getMenu().findItem(R.id.post).setIcon(R.drawable.instagram_new_post_outline_24);
                        return true;
                }
                return false;
            }
        });

        swipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rvPosts = findViewById(R.id.rvPost);
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        rvPosts.setAdapter(postAdapter);
        populateFeed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    public static void fetchTimelineAsync(int page) {
        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    postAdapter.clear();
                    // ...the data has come back, add new items to your adapter...
                    List<Post> list = new ArrayList<>();
                    for (int i=0; i<objects.size(); i++) {
                        list.add(objects.get(i));
                    }
                    postAdapter.addAll(list);
                    // Now we call setRefreshing(false) to signal refresh has finished
                    swipeContainer.setRefreshing(false);
                } else {
                    Log.i(APP_TAG, "Sorry, can't load feed.");
                    e.printStackTrace();
                }
            }
        });
    }

    public void logout(MenuItem item) {
        ParseUser.logOut();
        finish();
        Toast.makeText(this, "Successfully logged out.", Toast.LENGTH_LONG);
    }

    public void populateFeed() {
        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); ++i) {
                        posts.add(objects.get(i));
                        postAdapter.notifyItemInserted(posts.size() - 1);

                        //Log.d(APP_TAG, objects.get(i).getCaption() + " " + objects.get(i).getUser().getUsername());
                    }
                } else {
                    Log.i(APP_TAG, "Sorry, can't load feed.");
                    e.printStackTrace();
                }
            }
        });
    }

    public void onLaunchCamera(MenuItem m) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Intent intent = new Intent(HomeActivity.this, PostActivity.class);
                intent.putExtra("bitmap_path", photoFile.getAbsolutePath());
                HomeActivity.this.startActivity(intent);

            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
