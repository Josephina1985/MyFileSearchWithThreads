package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileSearcherTest {

  private static final String FILE_NON_EXISTENT = "nonExistent.txt";
  private static final Path DIRECTORY_NON_EXISTENT = Path.of("C:","Test", "nonExistentDirectory");

  private FileSearcher fileSearcher;

  @BeforeEach
  void setUp() {
    fileSearcher = new FileSearcher(Runtime.getRuntime().availableProcessors());
  }

  @Test
  void testFileFound() {
    // Arrange
    String expectedPath1 = SearchConstants.VALID_DIRECTORY + File.separator + "subdir" + File.separator + SearchConstants.VALID_FILE_NAME;
    String expectedPath2 = SearchConstants.VALID_DIRECTORY + File.separator + "subdir2" + File.separator + SearchConstants.VALID_FILE_NAME;
    String expectedPath3 = SearchConstants.VALID_DIRECTORY + File.separator + SearchConstants.VALID_FILE_NAME;
    List<String> expectedPathList = List.of(expectedPath1, expectedPath2, expectedPath3);

    // Act
    List<String> result = fileSearcher.searchFileRecursively(SearchConstants.VALID_FILE_NAME, SearchConstants.VALID_DIRECTORY);

    // Assert
    assertEquals(result.size(), expectedPathList.size());
    assertTrue(expectedPathList.contains(result.get(0)));
    assertTrue(expectedPathList.contains(result.get(1)));
    assertTrue(expectedPathList.contains(result.get(2)));
  }

  @Test
  void testNonExistentFileName() {
    // Act
    List<String> result = fileSearcher.searchFileRecursively(FILE_NON_EXISTENT, SearchConstants.VALID_DIRECTORY);

    // Assert
    assertTrue(result.isEmpty());
  }

  @Test
  void testNonExistentDirectory() {
    // Act
    assertThrows(IllegalArgumentException.class, () -> {
      fileSearcher.searchFileRecursively(SearchConstants.VALID_FILE_NAME, DIRECTORY_NON_EXISTENT);
    });
  }

}
