package me.carolwang.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.carolwang.parstagram.models.Post;

public class UserFragment extends Fragment {

    private RecyclerView rvMyPosts;
    private MyPostAdapter mypostAdapter;
    private List<Post> posts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMyPosts = view.findViewById(R.id.rvMyPost);
        posts = new ArrayList<>();
        mypostAdapter = new MyPostAdapter(posts);
        rvMyPosts.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        rvMyPosts.setAdapter(mypostAdapter);
        retrievePosts();
    }

    public void retrievePosts() {
        // Define the class we would like to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // Define our query conditions
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.orderByDescending("createdAt");
        // Execute the find asynchronously
        query.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> itemList, ParseException e) {
                if (e == null) {
                    posts.addAll(itemList);
                    mypostAdapter.notifyDataSetChanged();
                } else {
                    Log.d("item", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void logout() {
        ParseUser.logOut();
        Toast.makeText(getActivity(), "Successfully logged out.", Toast.LENGTH_LONG);
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        getActivity().overridePendingTransition(0,0);
    }
}
