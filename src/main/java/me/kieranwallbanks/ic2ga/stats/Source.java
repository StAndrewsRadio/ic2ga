package me.kieranwallbanks.ic2ga.stats;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class Source {
    private int listeners;
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Listener> listener;

    public int getListeners() {
        return listeners;
    }

    public void setListeners(int listeners) {
        this.listeners = listeners;
    }

    public List<Listener> getListener() {
        return listener;
    }

    public void setListener(List<Listener> listener) {
        this.listener = listener;
    }
}
