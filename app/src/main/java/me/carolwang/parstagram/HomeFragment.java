package me.carolwang.parstagram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import me.carolwang.parstagram.models.Post;

public class HomeFragment extends Fragment {

    public final static String APP_TAG = "Parstagram";
    private final List<Post> posts = new ArrayList<>();
    private RecyclerView rvPosts;
    private PostAdapter postAdapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("HomeFragment", "onViewCreated");

        rvPosts = view.findViewById(R.id.rvPost);
        swipeContainer = view.findViewById(R.id.swipeContainer);

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

        postAdapter = new PostAdapter(posts);
        rvPosts.setLayoutManager(new LinearLayoutManager(view.getContext()));
        rvPosts.setAdapter(postAdapter);
        populateFeed();
    }

    @Override
    public void onResume() {
        super.onResume();

        postAdapter.notifyDataSetChanged();
    }

    public void fetchTimelineAsync(int page) {
        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
            if (e == null) {
                postAdapter.clear();
                postAdapter.addAll(objects);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            } else {
                Log.i(APP_TAG, "Sorry, can't load feed.");
                e.printStackTrace();
            }
            }
        });
    }

    public void populateFeed() {
        final Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    Log.d("HomeFragment", "objects size: " + objects.size());
                    posts.addAll(objects);
                    postAdapter.notifyDataSetChanged();
                    Log.i("HomeFragment", "populatedFeed");
                } else {
                    Log.i(APP_TAG, "Sorry, can't load feed.");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.i("HomeFragment", "onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

}
