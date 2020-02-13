package me.kieranwallbanks.ic2ga;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        File configFile;

        if (args == null || args.length != 1 || !(configFile = new File(args[0])).exists()) {
            throw new IllegalArgumentException("invalid file argument provided");
        }

        new IC2GA(configFile).start();
    }
}
