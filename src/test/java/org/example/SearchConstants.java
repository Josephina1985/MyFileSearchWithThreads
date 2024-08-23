package org.example;

import java.nio.file.Path;

public class SearchConstants {

  static final String VALID_FILE_NAME = "test.txt";

  static final Path VALID_DIRECTORY = Path.of("C:", "Test");
  static final Path DIRECTORY_NON_EXISTENT = Path.of("C:", "Test", "nonExistentDirectory");

  static final String EMPTY_INPUT = "\n";

}
