package at.kurumi.main;

import at.kurumi.json.InstructionsJSON;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
