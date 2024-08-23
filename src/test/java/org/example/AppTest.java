package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private Scanner scanner;
    private PrintStream out;
    private App app;

    @BeforeEach
    void setUp() {
        out = mock(PrintStream.class);
        app = new App();
    }

    @Test
    void testRunWithValidInputAndOutput() {
        // Arrange
        scanner = new Scanner(SearchConstants.VALID_FILE_NAME + "\n" + SearchConstants.VALID_DIRECTORY + "\n");
        FileSearcher fileSearcher = mock(FileSearcher.class);
        List<String> expectedResult = new ArrayList<>();
        expectedResult.add(SearchConstants.VALID_DIRECTORY + SearchConstants.VALID_FILE_NAME);

        // Act
        when(fileSearcher.searchFileRecursively(SearchConstants.VALID_FILE_NAME, SearchConstants.VALID_DIRECTORY))
          .thenReturn(expectedResult);

        app.run(scanner, fileSearcher, out);

        verify(out).println("Please enter the file name to search:");
        verify(out).println("Please enter the base directory to start the search:");
        verify(out).println("File found: " + SearchConstants.VALID_DIRECTORY + SearchConstants.VALID_FILE_NAME);
    }

    @Test
    void testGetFileName() {
        // Arrange
        scanner = new Scanner(SearchConstants.VALID_FILE_NAME + "\n");

        // Act
        String fileName = app.getFileName(scanner, out);

        // Assert
        assertEquals(SearchConstants.VALID_FILE_NAME, fileName);
    }

    @Test
    void testGetFileNameIsEmpty() {
        // Arrange
        scanner = new Scanner(SearchConstants.EMPTY_INPUT);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {app.getFileName(scanner, out);});
    }

    @Test
    void testGetDirectoryWithValidInput() {
        // Arrange
        scanner = new Scanner(SearchConstants.VALID_DIRECTORY + "\n");

        // Act
        Path directory = app.getDirectory(scanner, out);

        // Assert
        assertEquals(SearchConstants.VALID_DIRECTORY, directory);
    }

    @Test
    void testGetDirectoryWithTooMuchSpaces() {
        // Arrange
        String input = "  " + SearchConstants.VALID_DIRECTORY + "  \n";
        scanner = new Scanner(input);

        // Act
        Path directory = app.getDirectory(scanner, out);

        // Assert
        assertEquals(SearchConstants.VALID_DIRECTORY, directory);
    }

    @Test
    void testGetDirectoryIsEmpty() {
        // Arrange
        scanner = new Scanner(SearchConstants.EMPTY_INPUT);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {app.getDirectory(scanner, out);});
    }

    @Test
    void testSearchPerformance() {
        scanner = new Scanner(SearchConstants.VALID_FILE_NAME + "\n" + SearchConstants.VALID_DIRECTORY + "\n");
        FileSearcher fileSearcher = new FileSearcher(1);

        long startTime = System.nanoTime();
        app.run(scanner, fileSearcher, out);
        long endTime = System.nanoTime();

        long durationInNano = endTime - startTime;
        double durationInMilli = durationInNano / 1000000.0;

        // Überprüfen, ob die Suche unter der festgelegten Grenze bleibt, z.B. 100 ms
        assertTrue(durationInMilli < 100, "Search should complete within 100 milliseconds.");
    }

    @Test
    void testRunWithNonExistentDirectory() {
        // Arrange
        scanner = new Scanner(SearchConstants.VALID_FILE_NAME + "\n" + SearchConstants.DIRECTORY_NON_EXISTENT + "\n");
        FileSearcher fileSearcher = new FileSearcher(Runtime.getRuntime().availableProcessors());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            app.run(scanner, fileSearcher, out);
        });
    }
}
