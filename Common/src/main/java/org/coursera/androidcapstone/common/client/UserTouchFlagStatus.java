package org.coursera.androidcapstone.common.client;

public enum UserTouchFlagStatus {
    USER_NONE_STATUS(0),
    USER_TOUCHED_STATUS(1),
    USER_FLAGGED_STATUS(2);
    
    private int value;
    private UserTouchFlagStatus(int value) {
        this.value = value;
    }
    public int value() {
        return this.value;
    }

    private static UserTouchFlagStatus[] allValues = values();
    public static UserTouchFlagStatus fromOrdinal(int n) {
    	return allValues[n];
    }
};