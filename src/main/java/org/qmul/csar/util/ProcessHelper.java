package org.qmul.csar.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Miscellaneous utility methods for dealing with processes.
 * @see Process
 */
public final class ProcessHelper {

    /**
     * Starts a process with the description in the argument and then returns it.
     * @param command a string array containing the program and its arguments
     * @return the process started
     * @throws InterruptedException if the current thread is interrupted while waiting
     * @throws IOException if an I/O error occurs
     * @see ProcessBuilder
     */
    public static Process run(String... command) throws InterruptedException, IOException {
        ProcessBuilder pb = new ProcessBuilder(command);
        return pb.start();
    }

    /**
     * Returns the output in a {@code List<String>} for the argument.
     * @param process the process to read the output of
     * @return the output of the program
     * @throws IOException if an I/O error occurs
     */
    public static List<String> readOutput(Process process) throws IOException {
        List<String> output = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = br.readLine()) != null) {
                output.add(line);
            }
        }
        return output;
    }
}
