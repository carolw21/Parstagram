package me.carolwang.parstagram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

public class HomeActivity extends AppCompatActivity {

    public final static String APP_TAG = "Parstagram";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 134;
    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new PostFragment();
    final Fragment fragment3 = new UserFragment();
    final Fragment fragment4 = new PostDetailsFragment();
    FragmentTransaction transaction;
    SwipeRefreshLayout swipeContainer;
    BottomNavigationView bottomNavigationView;
    Bundle post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flContainer, fragment1).commit();

        bottomNavigationView.getMenu().findItem(R.id.home).setIcon(R.drawable.instagram_home_filled_24);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.home:
                        selectedFragment = fragment1;
                        bottomNavigationView.getMenu().findItem(R.id.home).setIcon(R.drawable.instagram_home_filled_24);
                        bottomNavigationView.getMenu().findItem(R.id.user).setIcon(R.drawable.instagram_user_outline_24);
                        bottomNavigationView.getMenu().findItem(R.id.post).setIcon(R.drawable.instagram_new_post_outline_24);
                        break;
                    case R.id.post:
                        selectedFragment = fragment2;
                        bottomNavigationView.getMenu().findItem(R.id.post).setIcon(R.drawable.instagram_new_post_filled_24);
                        bottomNavigationView.getMenu().findItem(R.id.home).setIcon(R.drawable.instagram_home_outline_24);
                        bottomNavigationView.getMenu().findItem(R.id.user).setIcon(R.drawable.instagram_user_outline_24);
                        break;
                    case R.id.user:
                        selectedFragment = fragment3;
                        bottomNavigationView.getMenu().findItem(R.id.user).setIcon(R.drawable.instagram_user_filled_24);
                        bottomNavigationView.getMenu().findItem(R.id.home).setIcon(R.drawable.instagram_home_outline_24);
                        bottomNavigationView.getMenu().findItem(R.id.post).setIcon(R.drawable.instagram_new_post_outline_24);
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.flContainer, selectedFragment);
                transaction.commit();
                return true;
            }
        });
    }

    public void logout(View v) {
        ((UserFragment) fragment3).logout();
    }

    public void onPost(View v) {
        ((PostFragment) fragment2).onPost();
    }

    public void saveData(int id, Bundle data) {
        post = data;
    }

    public Bundle getSavedData() {
        // here you'll save the data previously retrieved from the fragments and
        // return it in a Bundle
        return post;
    }

    public void transition() {
        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
        transaction2.replace(R.id.flContainer, fragment4);
        transaction2.commit();
    }

    public void onPhoto(View v) {
        ((UserFragment) fragment3).selectImage();
    }

    public void onProfile(View v) {
        bottomNavigationView.setSelectedItemId(R.id.user);
    }

}