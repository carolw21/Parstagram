package me.carolwang.parstagram.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
