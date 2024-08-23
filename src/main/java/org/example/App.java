package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.Scanner;


public class App 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    /**
     * The main entry point of the application.
     * Initializes the application components and starts the file search process.
     *
     * @param args command-line arguments (not used)
     */
    public static void main( String[] args )
    {
        try {
            Scanner scanner = new Scanner(System.in);
            int numberOfThreads = Runtime.getRuntime()
                                         .availableProcessors();
            System.out.println("Using " + numberOfThreads + " threads based on available CPU cores.");

            App app = new App();
            boolean continueSearching;

            do {
                FileSearcher fileSearcher = new FileSearcher(numberOfThreads);
                app.run(scanner, fileSearcher, System.out);

                fileSearcher.shutdown();

                continueSearching = app.askForAnotherSearch(scanner, System.out);
            } while (continueSearching);

        } catch (IllegalStateException e) {
            LOGGER.error(MessageFormat.format("Invalid input: {0}", e.getMessage()));
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("An error occurred: ", e);
        }
    }

    /**
     * Runs the file search application. by interacting with the user to get the file name
     * and directory, and then performing the search.
     *
     * @param scanner the scanner to read user input from
     * @param fileSearcher the file searcher to use
     * @param out the output stream to print results to
     * @throws IllegalArgumentException if the file name or directory is invalid
     */
    public void run(Scanner scanner, FileSearcher fileSearcher, PrintStream out) throws IllegalArgumentException {
        String fileName = getFileName(scanner, out);
        Path directory = getDirectory(scanner, out);

        LOGGER.info("Starting search for file '{}' in directory '{}'", fileName, directory);

        long startTime = System.nanoTime();
        List<String> result = fileSearcher.searchFileRecursively(fileName, directory);
        long endTime = System.nanoTime();

        displayResult(result, out);

        long durationInNanos = endTime - startTime;
        double durationInMilli = durationInNanos / 1_000_000.0;
        out.println("Search completed in " + durationInMilli + " milliseconds.");
        LOGGER.info("Search completed in {} milliseconds.", durationInMilli);
    }

    /**
     * Asks the user if they would like to perform another search.
     *
     * @param scanner the scanner to read user input from
     * @param out     the output stream to print the prompt to
     * @return true if the user wants to search again, false otherwise
     */
    public boolean askForAnotherSearch(Scanner scanner, PrintStream out) {
        out.println("Would you like to search again? (yes/no)");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("yes");
    }

    /**
     * Prompts the user to enter the file name and returns it.
     *
     * @param scanner the scanner to read user input from
     * @param out     the output stream to print the prompt to
     * @return the file name entered by the user
     * @throws IllegalArgumentException if the file name is empty or null
     */
    public String getFileName(Scanner scanner, PrintStream out) {
        out.println("Please enter the file name to search:");

        String fileName = scanner.nextLine();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        return fileName;
    }

    /**
     * Prompts the user to enter the base directory and returns it.
     *
     * @param scanner the scanner to read user input from
     * @param out     the output stream to print the prompt to
     * @return the base directory entered by the user
     * @throws IllegalArgumentException if the directory is empty or null
     */
    public Path getDirectory(Scanner scanner, PrintStream out) {
        out.println("Please enter the base directory to start the search:");

        String directoryInput = scanner.nextLine().trim();
        if (directoryInput.isEmpty()) {
            throw new IllegalArgumentException("Directory cannot be empty");
        }

        return Paths.get(directoryInput).normalize();
    }

    /**
     * Displays the result of the file search to the user.
     *
     * @param resultList the result of the file search, which is the file path if found, or null if not found
     * @param out    the output stream to print the result to
     */
    public void displayResult(List<String> resultList, PrintStream out) {
        if (resultList.isEmpty()) {
            out.println("File not found.");
            LOGGER.info("File not found.");
        }

        for (String result : resultList) {
            out.println("File found: " + result);
            LOGGER.info("File found: {}", result);
        }
    }
}
