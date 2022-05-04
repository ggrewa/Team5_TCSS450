/*
 * TCSS450
 * Mobile Application Programming
 * Spring 2022
 */
package edu.uw.tcss450.team5.holochat.io;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/*
 * Class that builds a request queue for http calls.
 *
 * @author Charles Bryan
 * @version Spring 2022
 */
public class RequestQueueSingleton {

    /** Instance of the class. */
    private static RequestQueueSingleton instance;

    /** Context of the class. */
    private static Context context;

    /** Queue of the class. */
    private RequestQueue mRequestQueue;

    /** ImageLoader for the class. */
    private ImageLoader mImageLoader;

    /**
     * Constructor that initializes the context to the one that is specified.
     *
     * @param context context of the class.
     */
    private RequestQueueSingleton(Context context) {
        RequestQueueSingleton.context = context;
        mRequestQueue = getmRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * Getter for the instance of the request queue.
     *
     * @param context the context whose queue is to be fetched
     * @return the instance of the request queue
     */
    public static synchronized RequestQueueSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new RequestQueueSingleton(context);
        }
        return instance;
    }

    /**
     * Getter for the request queue.
     *
     * @return the request queue
     */
    public RequestQueue getmRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Adds a request  to the request queue.
     *
     * @param req request that is to be added to the queue
     * @param <T> request type
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getmRequestQueue().add(req);
    }

    /**
     * Getter for the image loader.
     * @return the Image loader.
     */
    public ImageLoader getmImageLoader() {
        return mImageLoader;
    }
}
