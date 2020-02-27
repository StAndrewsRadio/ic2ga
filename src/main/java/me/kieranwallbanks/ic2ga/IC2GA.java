package me.kieranwallbanks.ic2ga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import me.kieranwallbanks.ic2ga.config.Config;
import me.kieranwallbanks.ic2ga.config.Mountpoint;
import me.kieranwallbanks.ic2ga.config.Server;
import me.kieranwallbanks.ic2ga.stats.Icestats;
import me.kieranwallbanks.ic2ga.stats.Listener;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class IC2GA extends TimerTask {
    private final ObjectMapper xmlMapper;
    private final Config config;
    private final HttpClient httpClient;
    private final StatusChecker statusChecker;

    private Timer timer =  null;

    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("EEEE",Locale.ENGLISH);
    private static final SimpleDateFormat HOUR_FORMAT = new SimpleDateFormat("HH", Locale.ENGLISH);

    public IC2GA(File configFile) throws IOException {
        xmlMapper = new XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        config = xmlMapper.readValue(configFile, Config.class);
        httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
        statusChecker = config.getStatus().isEnabled() ? new StatusChecker(config.getStatus()) : null;

        System.out.println("Created IC2GA instance...");
    }

    public void start() {
        if (statusChecker != null) {
            statusChecker.start();
            System.out.println("Started status checker!");
        }

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(this, 0, config.getRefreshRate() * 1000);
        System.out.println("Updates scheduled...");
    }

    @Override
    public void run() {
        long time = Calendar.getInstance().getTimeInMillis();

        System.out.println("Running update at " + Instant.ofEpochMilli(time) + "...");

        // loop through servers
        for (Server server : config.getServers()) {
            System.out.println("  Trying server " + server.getListMountsUrl() + "...");

            String show = null;

            // get the current show name
            if (server.getShowLogUrl() != null && !server.getShowLogUrl().isBlank()) {
                String showUrl = server.getShowLogUrl().replace("$DAY$", DAY_FORMAT.format(time))
                        .replace("$HOUR$", HOUR_FORMAT.format(time));

                try {
                    System.out.println("    Getting show name from " + showUrl + "...");

                    HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder(URI.create(showUrl)).GET().build(),
                            HttpResponse.BodyHandlers.ofString());

                    show = response.body().replace("\"", "").replace(' ', '_')
                            .toLowerCase().trim().replaceAll("[^a-zA-Z0-9_-]", "");

                    System.out.println("    Reading current show as " + show + "...");
                } catch (InterruptedException | IOException e) {
                    System.out.println("    Couldn't read the show name! Setting it to \"autofade\"...");
                    e.printStackTrace();
                }
            }

            // if it didn't work, or isn't set, leave it as autofade
            if (show == null || show.isBlank()) {
                show = "autofade";
            }

            // setup auth info
            String authString = null;

            if (server.getUsername() != null && !server.getUsername().isBlank()) {
                authString = "Basic " + Base64.getEncoder().encodeToString((server.getUsername() + ":" +
                        server.getPassword()).getBytes());
            }

            // loop through mountpoints
            for (Mountpoint mountpoint : server.getMountpoints()) {
                System.out.println("    Reading from mountpoint " + mountpoint.getName() + "...");

                HttpResponse<String> icestatsXml;
                try {
                    HttpRequest.Builder icestatsRequest = HttpRequest.newBuilder().GET()
                            .uri(URI.create(server.getListMountsUrl() + mountpoint.getName()));

                    if (authString != null) {
                        icestatsRequest.setHeader("Authorization", authString);
                    }

                    icestatsXml = httpClient.send(icestatsRequest.build(), HttpResponse.BodyHandlers.ofString());

                    if (icestatsXml.statusCode() >= 200 && icestatsXml.statusCode() < 300) {
                        System.out.println("      Show name read successfully!");
                    } else {
                        System.out.println("      Received invalid response code " + icestatsXml.statusCode() + "...");
                        System.out.println("      Body of response is: " + icestatsXml.body());
                        System.out.println("      Setting this show name to \"autofade\"...");
                    }
                } catch (IOException | InterruptedException e) {
                    System.out.println("      Couldn't get this mountpoint's listener stats!");
                    e.printStackTrace();
                    break;
                }

                Icestats icestats;
                try {
                    icestats = xmlMapper.readValue(icestatsXml.body(), Icestats.class);
                } catch (JsonProcessingException e) {
                    System.out.println("      Couldn't parse this mountpoint's listener stats!");
                    e.printStackTrace();
                    break;
                }

                Set<String> payloads = new HashSet<>();

                System.out.println("      Reading listeners...");

                // loop through listeners
                for (int i = 0; i < icestats.getSource().getListeners(); i++) {
                    System.out.println("      Checking listener " + i + "...");

                    if (icestats.getSource().getListener().size() <= i) {
                        System.out.println("      Icecast is reporting a number of listeners that doesn't add up...");
                        break;
                    }

                    Listener listener = icestats.getSource().getListener().get(i);

                    System.out.println("        " + listener);

                    if (listener.getIp() == null || listener.getIp().trim().equals("127.0.0.1")) {
                        System.out.println("        Skipping local listener...");
                    } else {
                        System.out.println("        Creating listener payload...");
                        payloads.add(GoogleAnalytics.getPayloadData(mountpoint.getTrackingId(), listener.getId(),
                                listener.getIp(), listener.getUserAgent(), server.getListenUrl(),
                                mountpoint.getName(), mountpoint.getFriendlyName(), show, listener.getLag(),
                                listener.getReferrer()));
                    }
                }

                if (payloads.isEmpty()) {
                    System.out.println("    No payloads to send!");
                } else {
                    Set<String> filteredPayloads = new HashSet<>();
                    int currentNumber = 1;
                    StringBuilder currentString = new StringBuilder();

                    // loop through payloads
                    for (String payload : payloads) {
                        // ga only supports 20 per post request
                        if (currentNumber > 20) {
                            filteredPayloads.add(currentString.toString().trim());
                            currentNumber = 1;
                            currentString = new StringBuilder();
                        }

                        currentString.append(payload).append("\n");
                        currentNumber++;
                    }

                    // make sure we add the remaining payloads
                    if (currentString.length() != 0) {
                        filteredPayloads.add(currentString.toString().trim());
                    }

                    // loop through filtered, bulky payloads
                    for (String payload : filteredPayloads) {
                        System.out.println("    Sending payload with body " + payload);

                        HttpResponse<String> response;
                        try {
                            response = httpClient.send(HttpRequest.newBuilder()
                                    .uri(GoogleAnalytics.SUBMISSION_URL)
                                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                                    .setHeader("User-Agent", GoogleAnalytics.USER_AGENT)
                                    .setHeader("Content-Type", "text/plain").build(),
                                    HttpResponse.BodyHandlers.ofString());
                        } catch (IOException | InterruptedException e) {
                            System.out.println("    Couldn't send this payload!");
                            e.printStackTrace();
                            continue;
                        }

                        // check response
                        if (response.statusCode() >= 200 && response.statusCode() < 300) {
                            System.out.println("    Payload submitted successfully!");
                        } else {
                            System.out.println("    Received invalid response code " + response.statusCode() + "...");
                            System.out.println("    Body of response is: " + response.body());
                        }
                    }
                }
            }

            System.out.println("Update done!");
        }
    }
}
