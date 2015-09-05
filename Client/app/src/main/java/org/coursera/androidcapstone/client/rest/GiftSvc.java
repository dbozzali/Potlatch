package org.coursera.androidcapstone.client.rest;

import org.coursera.androidcapstone.common.json.GsonHelper;
import org.coursera.androidcapstone.common.client.GiftSvcApi;
import org.coursera.androidcapstone.common.unsafe.EasyHttpClient;
//import org.coursera.androidcapstone.common.unsafe.UnsafeHttpsClient;
import org.coursera.androidcapstone.common.oauth.SecuredRestBuilder;

import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;
import retrofit.converter.GsonConverter;

public class GiftSvc {
	public static final String CLIENT_ID = "mobile";
    public static final String READ_ONLY_CLIENT_ID = "mobileReader";

	private static GiftSvcApi giftSvc;
    private static GiftSvcApi readOnlyGiftSvc;

	public static synchronized GiftSvcApi getSecuredRestBuilder() {
        return giftSvc;
	}

    public static synchronized GiftSvcApi getReadOnlySecuredRestBuilder() {
        return readOnlyGiftSvc;
    }

	public static synchronized void logout() {
		giftSvc = null;
        readOnlyGiftSvc = null;
	}

	public static synchronized void init(String server, String username, String password) {
        giftSvc =
            new SecuredRestBuilder().setLoginEndpoint(server + GiftSvcApi.TOKEN_PATH)
                                    .setUsername(username)
                                    .setPassword(password)
                                    .setClientId(CLIENT_ID)
                                    //.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
                                    //.setClient(new ApacheClient(UnsafeHttpsClient.getHttpsClient()))
                                    .setClient(new ApacheClient(new EasyHttpClient()))
                                    .setEndpoint(server)
                                    .setLogLevel(LogLevel.FULL)
                                    .setConverter(new GsonConverter(GsonHelper.getCustomGson()))
                                    .build()
                                    .create(GiftSvcApi.class);
    }

    public static synchronized void initReadOnly(String server) {
        readOnlyGiftSvc =
            new SecuredRestBuilder().setLoginEndpoint(server + GiftSvcApi.TOKEN_PATH)
                                    .setUsername("readonlyuser")
                                    .setPassword("readonlyuserpass")
                                    .setClientId(READ_ONLY_CLIENT_ID)
                                    //.setClient(new ApacheClient(UnsafeHttpsClient.createUnsafeClient()))
                                    //.setClient(new ApacheClient(UnsafeHttpsClient.getHttpsClient()))
                                    .setClient(new ApacheClient(new EasyHttpClient()))
                                    .setEndpoint(server)
                                    .setLogLevel(LogLevel.FULL)
                                    .setConverter(new GsonConverter(GsonHelper.getCustomGson()))
                                    .build()
                                    .create(GiftSvcApi.class);
    }

    public static synchronized boolean loggedIn() {
        return (giftSvc != null);
    }
}
