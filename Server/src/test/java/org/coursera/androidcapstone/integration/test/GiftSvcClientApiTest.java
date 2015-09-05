package org.coursera.androidcapstone.integration.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.coursera.androidcapstone.common.oauth.SecuredRestBuilder;
import org.coursera.androidcapstone.common.unsafe.EasyHttpClient;
import org.coursera.androidcapstone.common.client.GiftSvcApi;

import retrofit.RestAdapter.LogLevel;
import retrofit.client.ApacheClient;

public class GiftSvcClientApiTest {

	private final String USERNAME = "alice";
	private final String PASSWORD = "alicepass";
	private final String CLIENT_ID = "mobile";

	private final String TEST_URL = "https://10.0.0.14:8443";

	private GiftSvcApi giftService = new SecuredRestBuilder()
			.setLoginEndpoint(TEST_URL + GiftSvcApi.TOKEN_PATH)
			.setUsername(USERNAME)
			.setPassword(PASSWORD)
			.setClientId(CLIENT_ID)
			.setClient(new ApacheClient(new EasyHttpClient()))
			.setEndpoint(TEST_URL)
            .setLogLevel(LogLevel.FULL)
            .build()
			.create(GiftSvcApi.class);

	@Test
	public void testCheckUsername() throws Exception {
		String username = giftService.checkUsername();
		assertEquals(username, USERNAME);
	}
}
