package me.kieranwallbanks.ic2ga.config;

import java.util.List;

public class Config {
    private int refreshRate;
    private List<Server> servers;

    public int getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }

    public List<Server> getServers() {
        return servers;
    }

    public void setServers(List<Server> servers) {
        this.servers = servers;
    }
}
