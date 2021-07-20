package at.kurumi.main;

import at.kurumi.json.InstructionsJSON;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        if(args.length < 1) {
            System.err.println("Specify path to instructions json file");
            System.exit(1);
        }
        final var instructionsJson = loadInstructions(args[0]);
        if(instructionsJson.isEmpty()) {
            System.err.println("Did not load instruction json file");
            System.exit(1);
        }
        final var instructions = instructionsJson.get().getInstructions();
    }

    private static final int BLOCK_LENGTH = 32;

    /**
     * Print a delimiter liner to System.out with length LENGTH.
     */
    private static void printSegment(int blocks) {
        final var a = new char[BLOCK_LENGTH * blocks];
        Arrays.fill(a, '-');
        System.out.println(a);
    }

    private static void printBeginEvent(String eventName, String workerName, boolean terminate) {
        // Truncate long event names to never be longer than 16 characters
        eventName = eventName.length() < 13
                ? eventName
                : eventName.substring(0, 13) + "...";
        System.out.printf("BEGIN %s\tUSING %s |%s", eventName, workerName, terminate ? "\n" : "");
    }

    private static void printEndEvent(boolean terminate) {
        System.out.printf("              END              |%s", terminate ? "\n" : "");
    }

    /**
     * Load instruction sheet from json file located in command line arguments.
     *
     * @param path path to file
     * @return instruction sheet representation
     */
    private static Optional<InstructionsJSON> loadInstructions(String path) {
        try (final var reader = new InputStreamReader(new FileInputStream(path))) {
            final var gson = new Gson();
            return Optional.ofNullable(gson.fromJson(reader, InstructionsJSON.class));
        } catch (FileNotFoundException e) {
            System.err.printf("File at %s not found", path);
            System.exit(1);
        } catch (IOException e) {
            System.err.printf("Could not read file at %s", path);
            System.exit(1);
        }
        return Optional.empty();
    }
}
