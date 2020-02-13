package me.kieranwallbanks.ic2ga.config;

import java.util.List;

public class Server {
    private String listenUrl, listMountsUrl, username, password, showLogUrl;
    private List<Mountpoint> mountpoints;

    public String getListenUrl() {
        return listenUrl;
    }

    public void setListenUrl(String listenUrl) {
        this.listenUrl = listenUrl;
    }

    public String getListMountsUrl() {
        return listMountsUrl;
    }

    public void setListMountsUrl(String listMountsUrl) {
        this.listMountsUrl = listMountsUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getShowLogUrl() {
        return showLogUrl;
    }

    public void setShowLogUrl(String showLogUrl) {
        this.showLogUrl = showLogUrl;
    }

    public List<Mountpoint> getMountpoints() {
        return mountpoints;
    }

    public void setMountpoints(List<Mountpoint> mountpoints) {
        this.mountpoints = mountpoints;
    }
}
