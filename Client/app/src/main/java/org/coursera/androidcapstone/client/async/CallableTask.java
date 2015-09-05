/*
 **
 ** Copyright 2014, Jules White
 **
 **
 */

package org.coursera.androidcapstone.client.async;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.Callable;


/**
 * A class that is designed to provide a simpler interface to Android's
 * AsyncTask and allow exceptions to be reported back in the UI thread.
 * Rather than needing to subclass AsyncTask, you simply call:
 *
 <pre>
 * {@code
 *
 *  CallableTask.invoke(
 *      new Callable<Collection<Gift>>() {
 @Override
 public Collection<Gift> call() throws Exception {
 // Code to execute in the background
 }
 },
 new TaskCallback<Collection<Gift>>() {
 
 @Override
 public void success(Collection<Gift> result) {
 // Code to execute in the UI thread if the
 // background operation succeeds
 }
 
 @Override
 public void error(Exception e) {
 // Code to execute in the UI thread if the
 // background operation fails
 }
 }
 *  );
 *
 * }
 * @param <T>
 */
public class CallableTask<T> extends AsyncTask<Void, Double, T> {
    private static final String TAG = CallableTask.class.getName();

    private Callable<T> callable;
    private TaskCallback<T> callback;
    private Exception error;

    public CallableTask(Callable<T> callable, TaskCallback<T> callback) {
        this.callable = callable;
        this.callback = callback;
    }

    public static <V> void invoke(Callable<V> call, TaskCallback<V> callback) {
        new CallableTask<V>(call, callback).execute();
    }
    
    @Override
    protected T doInBackground(Void... ts) {
        T result = null;
        try {
            result = this.callable.call();
        }
        catch (Exception ex){
            Log.e(TAG, "Error invoking callable in AsyncTask callable: " + this.callable, ex);
            this.error = ex;
        }
        return result;
    }

    @Override
    protected void onPostExecute(T r) {
    	if (this.error != null) {
    		this.callback.error(this.error);
    	}
    	else {
    		this.callback.success(r);
    	}
    }
}

