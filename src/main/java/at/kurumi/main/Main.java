package at.kurumi.main;

import at.kurumi.json.InstructionsJSON;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Specify path to instructions json file");
            System.exit(1);
        }
        final var instructionsJson = loadInstructions(args[0]);
        if (instructionsJson.isEmpty()) {
            System.err.println("Did not load instruction json file");
            System.exit(1);
        }

        // build graph from json
        final var instructions = instructionsJson.get().getInstructions();
        // Exit if there are no instructions
        if(instructions.isEmpty()) {
            System.err.print("No instructions");
            System.exit(1);
        }
        final var nodes = new ArrayList<Instruction>(instructions.size());
        instructions.forEach(instruction ->
                nodes.add(new Instruction(instruction.getProcess(), instruction.getDuration())));
        for(int i = 0; i < instructions.size(); i++) {
            for(int dep : instructions.get(i).getDepends_on()) {
                // account for non-zero indexing in source
                dep--;
                System.out.printf("Linking instruction %d back to %d%n", i, dep);
                nodes.get(i).linkWithDependency(nodes.get(dep));
            }
        }
        // Calculate start times
        final var timeline = new HashMap<Integer, List<Instruction>>();
        nodes.forEach(node -> {
            final var startTime = node.getEarliestStartTime();
            System.out.printf("Setting start for %s to %d%n", node.getInstruction(), startTime);
            timeline.computeIfAbsent(startTime, k -> new ArrayList<>())
                    .add(node);
        });

        timeline.keySet().stream().sorted().forEachOrdered(timestamp -> {
            timeline.get(timestamp).forEach(event -> {
                printBeginEvent(timestamp, event.getInstruction(), "XXXX0x00");
            });
            System.out.print("\n");
        });

    }

    private static void walkPaths(Map<Integer, List<Instruction>> timeline, Instruction current, int currentBegin) {
        System.out.printf("At instruction: %s%n", current.getInstruction());
        if(current.getNext().isEmpty()) {
            System.out.println("Is terminal, returning");
            return;
        }
        // Add next set of instructions
        final var currentEnd = currentBegin + current.getDuration();
        System.out.printf("Branching at %d", currentEnd);
        final var nextInstructions = timeline.computeIfAbsent(currentEnd, k -> new ArrayList<>());
        // Make sure to not add any that already exist?
        nextInstructions.addAll(current.getNext().stream()
                /*.filter(next -> {
                    final var precursorsOfNext = next.getPrevious();
                    // Instruction chaining is ugly like this to keep the entire process as lazy as possible
                    // and hopefully avoid some of the recursive calls
                    final var longerPathExists = precursorsOfNext.size() > 1 // Could there be a longer path to this next node?
                            // If yes, check if there is a longer path
                            && current.getInclusivePathLength() < precursorsOfNext.stream()
                            .mapToInt(Instruction::getInclusivePathLength)
                            .max()
                            // Ignore "no isPresent check" warning. We wouldn't be here if it wasn't.
                            .getAsInt();
                    return !longerPathExists;
                })*/
                .filter(next -> timeline.values().stream().noneMatch(list -> list.contains(next)))
                .collect(Collectors.toList())
        );
        System.out.printf(" to %s path(s)%n", nextInstructions.size());
        // Recursive calls
        nextInstructions.forEach(next -> walkPaths(timeline, next, currentEnd));
    }

    /**
     * Format a begin event line. Line will be 32 characters long to match Main::printEndEvent() in length.
     * Event names will be truncated if they are longer than 4 characters.
     * Worker names should be named 4 characters for their skill type, followed by a 4 character hexadecimal
     * representation of the 8bit worker id
     *
     * @param timestamp  integer timestamp up to 999
     * @param eventName  event name
     * @param workerName worker name
     */
    private static void printBeginEvent(int timestamp, String eventName, String workerName) {
        System.out.printf("%02d|BEG %s   USING %s  |",
                timestamp,
                eventName.substring(0, 4),
                workerName
        );
    }

    private static void printEndEvent(int timestamp, String eventName) {
        System.out.printf("%02d|          END %s         |", timestamp, eventName);
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
