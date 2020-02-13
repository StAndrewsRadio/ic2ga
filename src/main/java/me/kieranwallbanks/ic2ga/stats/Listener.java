package me.kieranwallbanks.ic2ga.stats;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Listener {
    @JsonProperty("ID") private String id;
    @JsonProperty("IP") private String ip;
    @JsonProperty("UserAgent") private String userAgent;
    @JsonProperty("Connected") private String connected;
    @JsonProperty("Referer") private String referrer;
    private String lag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getConnected() {
        return connected;
    }

    public void setConnected(String connected) {
        this.connected = connected;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getLag() {
        return lag;
    }

    public void setLag(String lag) {
        this.lag = lag;
    }

    @Override
    public String toString() {
        return "Listener{" +
                "id='" + id + '\'' +
                ", ip='" + ip + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", connected='" + connected + '\'' +
                ", referrer='" + referrer + '\'' +
                ", lag='" + lag + '\'' +
                '}';
    }
}
