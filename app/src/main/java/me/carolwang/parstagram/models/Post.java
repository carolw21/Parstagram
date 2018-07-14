package me.carolwang.parstagram.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;

@ParseClassName("Post")
public class Post extends ParseObject {

    public Post() {
        super();
    }

    public Post(ParseFile imageFile, String caption, ParseUser user) {
        super();
        setImage(imageFile);
        setCaption(caption);
        setUser(user);
    }

    public ParseFile getImage() {
        return getParseFile("media");
    }

    public String getCaption() {
        return getString("caption");
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public Date getDate() { return getCreatedAt(); }

    public void setImage(ParseFile imageFile) {
        put("media", imageFile);
    }

    public void setCaption(String caption) {
        put("caption", caption);
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public void addLikes(String username) {
        String likes = getLikes();
        likes = likes.concat(username+" ");
        put("likes",likes);
        saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    try {
                        fetch();
                        Log.i("Hello", "Success");
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.i("Parstagram", "Failed to update object, with error code: " + e.toString());
                }
            }
        });
    }

    public void removeLikes(String username) {
        String likes = getLikes();
        likes = likes.replace(username+" ", "");
        put("likes", likes);
        saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    try {
                        fetch();
                        Log.i("Hello", "Success");
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.i("Parstagram", "Failed to update object, with error code: " + e.toString());
                }
            }
        });
    }

    public String getLikes() {
        String arr = getString("likes");
        if (arr == null || arr.length() == 0) {
           return "";
        }
        return arr;
    }

    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        public Query getTop() {
            setLimit(20);
            orderByDescending("createdAt");
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }
}
