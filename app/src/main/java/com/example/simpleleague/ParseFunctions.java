package com.example.simpleleague;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.simpleleague.models.Comment;
import com.example.simpleleague.models.Follow;
import com.example.simpleleague.models.Post;
import com.example.simpleleague.models.User;
import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class ParseFunctions {

    public static final String TAG = "ParseQueries";

    public static Follow createFollow(ParseUser parseUser) {
        Follow follow = new Follow();
        follow.setUser(parseUser);
        follow.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Follow wasn't created for "+parseUser.getUsername()+".", e);
                }
            }
        });
        return follow;
    }

    public static void loadProfileImage(ImageView ivProfileImage, Context mContext, ParseUser parseUser) {
        ParseFile image = parseUser.getParseFile(User.KEY_PROFILE_IMAGE);
        if (image != null) {
            Glide.with(mContext).load(image.getUrl()).placeholder(R.drawable.default_profile_image).centerCrop().into(ivProfileImage);
        } else {
            Glide.with(mContext).load(R.drawable.default_profile_image).centerCrop().into(ivProfileImage);
        }
    }

    public static void setBio(TextView tvBio, ParseUser parseUser) {
        String bio = parseUser.getString(User.KEY_BIO);
        if (bio == null) {
            tvBio.setText(R.string.defaultBio);
        } else {
            tvBio.setText(bio);
        }
    }

    public static void setFollowing(TextView tvFollowing, ParseUser parseUser) {
        Follow follow = (Follow) parseUser.get(User.KEY_FOLLOW);
        if (follow == null) {
            Log.i(TAG, "Null Follow - Unable to setFollowing for "+parseUser.getUsername()+".");
            return;
        }
        List<String> following = follow.getFollowing();
        if (following == null) {
            tvFollowing.setText(String.valueOf(0));
        } else {
            tvFollowing.setText(String.valueOf(following.size()));
        }
    }

    public static void setFollowers(TextView tvFollowers, ParseUser parseUser) {
        Follow follow = (Follow) parseUser.get(User.KEY_FOLLOW);
        if (follow == null) {
            Log.i(TAG, "Null Follow - Unable to setFollowers for "+parseUser.getUsername()+".");
            return;
        }
        List<String> followers = follow.getFollowers();
        if (followers == null) {
            tvFollowers.setText(String.valueOf(0));
        } else {
            tvFollowers.setText(String.valueOf(followers.size()));
        }
    }

    public static void setNumberPosts(TextView tvPosts, ParseUser parseUser) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo(Post.KEY_USER, parseUser);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error getting # of posts for "+parseUser.getUsername()+".", e);
                    return;
                }
                tvPosts.setText(String.valueOf(count));
            }
        });
    }

    public static boolean userFollows(ParseUser parseUser) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        Follow follow = (Follow) currentUser.get(User.KEY_FOLLOW);
        List<String> following = follow.getFollowing();
        if (following == null) return false;
        return following.contains(parseUser.getObjectId());
    }

    public static void followUser(ParseUser parseUser) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        // Get users that current user follow
        Follow currentUserFollow = (Follow) currentUser.get(User.KEY_FOLLOW);
        currentUserFollow.addFollowing(parseUser);
        currentUserFollow.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, currentUser.getUsername()+" couldn't follow "+parseUser.getUsername()+".", e);
                }
            }
        });
        // Get users that follow passed in user
        Follow userFollow = (Follow) parseUser.get(User.KEY_FOLLOW);
        if (userFollow == null) {
            Log.i(TAG, "Null Follow - "+parseUser.getUsername()+" cannot be followed by "+currentUser.getUsername()+".");
            return;
        }
        userFollow.addFollower(currentUser);
        userFollow.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, parseUser.getUsername()+" didn't receive "+currentUser.getUsername()+" as a follower.", e);
                }
            }
        });
    }

    public static void unfollowUser(ParseUser parseUser) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        // Get users that current user follows
        Follow currentUserFollow = (Follow) currentUser.get(User.KEY_FOLLOW);
        currentUserFollow.removeFollowing(parseUser);
        currentUserFollow.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i(TAG, currentUser.getUsername()+" couldn't unfollow "+parseUser.getUsername()+".", e);
                }
            }
        });
        // Get users that follow passed in user
        Follow userFollow = (Follow) parseUser.get(User.KEY_FOLLOW);
        if (userFollow == null) {
            Log.i(TAG, "Null Follow - "+parseUser.getUsername()+" cannot be unfollowed by "+currentUser.getUsername()+".");
            return;
        }
        userFollow.removeFollower(currentUser);
        userFollow.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i(TAG, parseUser.getUsername()+" didn't have "+currentUser.getUsername()+" removed from followers.", e);
                }
            }
        });
    }

    public static void saveProfileImage(Context context, File photoFile, ImageView ivProfileImage) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put(User.KEY_PROFILE_IMAGE, new ParseFile(photoFile));
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving profile image for "+currentUser.getUsername()+".", e);
                    Toast.makeText(context, "Error while saving profile image!", Toast.LENGTH_SHORT).show();
                    // Load back previous image
                    loadProfileImage(ivProfileImage, context, currentUser);
                }
            }
        });
    }

    public static void saveUsername(Context context, String username) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put(User.KEY_USERNAME, username.trim());
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving username for "+currentUser.getUsername()+".", e);
                    Toast.makeText(context, "Error while saving username!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void saveBio(Context context, String bio) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.put(User.KEY_BIO, bio.trim());
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving bio for "+currentUser.getUsername()+".", e);
                    Toast.makeText(context, "Error while saving bio!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean userLikesPost(Post post) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> likesUserIds = post.getLikes();
        if (likesUserIds == null) return false;
        return likesUserIds.contains(currentUser.getObjectId());
    }

    public static void likePost(Post post) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        post.addLike(currentUser.getObjectId());
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, currentUser.getUsername()+" wasn't able to like "+post.getUser().getUsername()+"'s post.", e);
                    return;
                }
            }
        });
    }

    public static void removeLikePost(Post post) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        post.removeLike(currentUser.getObjectId());
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, currentUser.getUsername()+" wasn't able to unlike "+post.getUser().getUsername()+"'s post.", e);
                }
            }
        });
    }

    public static boolean userDislikesPost(Post post) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> dislikesUserIds = post.getDislikes();
        if (dislikesUserIds == null) return false;
        return dislikesUserIds.contains(currentUser.getObjectId());
    }

    public static void dislikePost(Post post) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        post.addDislike(currentUser.getObjectId());
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, currentUser.getUsername()+" wasn't able to dislike "+post.getUser().getUsername()+"'s post.", e);
                }
            }
        });
    }

    public static void removeDislikePost(Post post) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        post.removeDislike(currentUser.getObjectId());
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, currentUser.getUsername()+" wasn't able to un-dislike "+post.getUser().getUsername()+"'s post.", e);
                }
            }
        });
    }

    public static boolean userLikesComment(Comment comment) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> likesUserIds = comment.getLikes();
        if (likesUserIds == null) return false;
        return likesUserIds.contains(currentUser.getObjectId());
    }

    public static void likeComment(Comment comment) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        comment.addLike(currentUser.getObjectId());
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, currentUser.getUsername()+" wasn't able to like "+comment.getUser().getUsername()+"'s comment.", e);
                }
            }
        });
    }

    public static void removeLikeComment(Comment comment) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        comment.removeLike(currentUser.getObjectId());
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, currentUser.getUsername()+" wasn't able to unlike "+comment.getUser().getUsername()+"'s comment.", e);
                }
            }
        });
    }

    public static boolean userDislikesComment(Comment comment) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> dislikesUserIds = comment.getDislikes();
        if (dislikesUserIds == null) return false;
        return dislikesUserIds.contains(currentUser.getObjectId());
    }

    public static void dislikeComment(Comment comment) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        comment.addDislike(currentUser.getObjectId());
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, currentUser.getUsername()+" wasn't able to dislike "+comment.getUser().getUsername()+"'s post.", e);
                }
            }
        });
    }

    public static void removeDislikeComment(Comment comment) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        comment.removeDislike(currentUser.getObjectId());
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, currentUser.getUsername()+" wasn't able to un-dislike "+comment.getUser().getUsername()+"'s post.", e);
                }
            }
        });
    }

    public static void saveFollowersCount(Follow follow) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        try {
            currentUser.put(User.KEY_FOLLOWERS_COUNT, follow.getFollowers().size());
        } catch (NullPointerException e) {
            currentUser.put(User.KEY_FOLLOWERS_COUNT, 0);
        }
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't save # of followers for "+currentUser.getUsername()+".");
                }
            }
        });
    }

    public static void savePostViewsCount(Post post) {
        try {
            if (post.getViewsCount() == post.getViews().size()) return;
            post.put(Post.KEY_VIEWS_COUNT, post.getViews().size());
        } catch (NullPointerException e) {
            post.put(Post.KEY_VIEWS_COUNT, 0);
        }
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Couldn't save # of views for "+post.getUser().getUsername()+"'s post.");
                }
            }
        });
    }
}