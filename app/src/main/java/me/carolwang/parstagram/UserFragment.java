package me.carolwang.parstagram;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.carolwang.parstagram.models.Post;
import me.carolwang.parstagram.utils.BitmapScaler;

import static android.app.Activity.RESULT_OK;
import static me.carolwang.parstagram.HomeActivity.APP_TAG;
import static me.carolwang.parstagram.HomeActivity.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
import static me.carolwang.parstagram.HomeActivity.GALLERY_IMAGE_ACTIVITY_REQUEST_CODE;

public class UserFragment extends Fragment {

    private RecyclerView rvMyPosts;
    private MyPostAdapter mypostAdapter;
    private List<Post> posts;
    private TextView tvUsername;
    private TextView tvPosts;
    private TextView tvFollowers;
    private TextView tvFollowing;
    private ImageView ivProfile;
    private String photoFileName;
    private ParseFile imageFile;
    private File photoFile;

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
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());
        tvPosts = view.findViewById(R.id.tvPosts);
        tvFollowers = view.findViewById(R.id.tvFollowers);
        tvFollowing = view.findViewById(R.id.tvFollowing);
        ivProfile = view.findViewById(R.id.ivProfile);
        rvMyPosts = view.findViewById(R.id.rvMyPost);
        posts = new ArrayList<>();
        mypostAdapter = new MyPostAdapter(posts);
        rvMyPosts.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        rvMyPosts.setAdapter(mypostAdapter);
        if (ParseUser.getCurrentUser().getParseFile("profile") != null) {
            ParseFile imageFile = ParseUser.getCurrentUser().getParseFile("profile");
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(imageFile.getFile().getAbsolutePath());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            roundedBitmapDrawable.setCornerRadius(85.0f);
            roundedBitmapDrawable.setAntiAlias(true);
            ivProfile.setImageDrawable(roundedBitmapDrawable);
        }
        retrievePosts();
        tvPosts.setText(posts.size()+"\nposts");
        tvFollowers.setText("6\nfollowers");
        tvFollowing.setText("0\nfollowing");
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

    public void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    onLaunchCamera();

                } else if (items[item].equals("Choose from Library")) {
                    onLaunchGallery();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        Uri fileProvider = FileProvider.getUriForFile(getActivity(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public void onLaunchGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

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
                String imagePath = photoFile.getAbsolutePath();
                Bitmap imageBitmap = scaleCenterCrop(BitmapFactory.decodeFile(imagePath), 85, 85);
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(imageBitmap, 85);
                // Configure byte output stream
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                // Compress the image further
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(imagePath);
                    // Write the bytes of the bitmap to
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
                RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), resizedBitmap);
                roundedBitmapDrawable.setCornerRadius(85.0f);
                roundedBitmapDrawable.setAntiAlias(true);
                ivProfile.setImageDrawable(roundedBitmapDrawable);
                onProfile();
            } else { // Result was a failure
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    }
                    Uri uri = data.getData();
                    File file = new File(getPath(getContext(), uri));
                    //File file = new File(getPath(getContext(), uri));

                    Bitmap bitmap = null;
                    try {
                        bitmap = scaleCenterCrop(MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(), uri), 350, 350);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(bitmap, 300);
                    // Configure byte output stream
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    // Compress the image further
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file.getAbsolutePath());
                        // Write the bytes of the bitmap to file
                        try {
                            fos.write(bytes.toByteArray());
                            fos.close();
                            imageFile = new ParseFile(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (FileNotFoundException e) {
                        Log.i("asdf", "error");
                        e.printStackTrace();
                    }
                    RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), resizedBitmap);
                    roundedBitmapDrawable.setCornerRadius(85.0f);
                    roundedBitmapDrawable.setAntiAlias(true);
                    ivProfile.setImageDrawable(roundedBitmapDrawable);
                    onProfile();
                }

            } else { // Result was a failure
                Toast.makeText(getActivity(), "Picture wasn't selected!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onProfile() {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put("profile", imageFile);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    try {
                        currentUser.fetch();
                        Log.i("Hello", "Success");
                        Toast.makeText(getContext(), "Successfully updated profile image!", Toast.LENGTH_LONG);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.i("Parstagram", "Failed to update object, with error code: " + e.toString());
                }
            }
        });
    }


    public Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    /**
     * Method for return file path of Gallery image
     *
     * @param context
     * @param uri
     * @return path of the selected image file from gallery
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.codepath.fileprovider".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.codepath.fileprovider".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "om.codepath.fileprovider".equals(uri
                .getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.codepath.fileprovider".equals(uri
                .getAuthority());
    }

}
