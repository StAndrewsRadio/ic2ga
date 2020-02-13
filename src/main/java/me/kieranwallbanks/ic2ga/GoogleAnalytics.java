package me.kieranwallbanks.ic2ga;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class GoogleAnalytics {
    public static final URI SUBMISSION_URL = URI.create("https://www.google-analytics.com/batch");
    public static final String PAYLOAD_DATA = "v=1&tid=%s&ds=ic2ga&cid=%s&uip=%s&ua=%s&t=pageview&dh=%s&dp=%s&dt=%s&" +
            "cd1=%s";
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)" +
            " Chrome/79.0.3945.130 Safari/537.36";

    public static String getPayloadData(String trackingId, String clientId, String clientIp, String userAgent,
                                        String host, String page, String title, String cd1, String cd2,
                                        String referrer) {

        String result = String.format(PAYLOAD_DATA, trackingId, UUID.nameUUIDFromBytes(clientId.getBytes()), clientIp,
                encode(userAgent), encode(host), encode(page), encode(title), encode(cd1));

        if (referrer != null) {
            result += "&dr=" + encode(referrer);
        }

        if (cd2 != null) {
            result += "&cd2=" + encode(cd2);
        }

        return result;
    }

    private static String encode(String string) {
        try {
            return URLEncoder.encode(string, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
