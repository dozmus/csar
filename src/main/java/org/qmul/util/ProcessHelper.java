package org.qmul.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class ProcessHelper {

    public static Process runAndWait(String... command) throws InterruptedException, IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        Process p = pb.start();
        p.waitFor();
        return p;
    }

    public static List<String> readOutput(Process p) throws IOException {
        List<String> output = new ArrayList<>();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

        while ((line = br.readLine()) != null) {
            output.add(line);
        }
        return output;
    }
}
