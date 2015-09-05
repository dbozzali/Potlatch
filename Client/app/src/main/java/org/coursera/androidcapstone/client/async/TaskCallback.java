/*
 **
 ** Copyright 2014, Jules White
 **
 **
 */

package org.coursera.androidcapstone.client.async;

public interface TaskCallback<T> {
    public void success(T result);
    public void error(Exception e);
}
