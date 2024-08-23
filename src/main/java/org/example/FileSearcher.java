package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FileSearcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(FileSearcher.class);
  final ExecutorService executorService;

  public FileSearcher(int numberOfThreads) {
    this.executorService = Executors.newFixedThreadPool(numberOfThreads);
  }

  /**
   * Searches for a file recursively in the given directory.
   *
   * @param fileName the name of the file to search for
   * @param directory the directory in which to start the search
   * @return the path of the found file, or null if the file was not found
   * @throws IllegalArgumentException if the fileName or directory is null or empty
   */
  public List<String> searchFileRecursively(String fileName, Path directory) throws IllegalArgumentException {
    File dir = directory.toFile();
    if (!dir.exists() || !dir.isDirectory()) {
      throw new IllegalArgumentException("Directory does not exist");
    }

    List<String> results = new ArrayList<>();
    List<Future<List<String>>> futures = new ArrayList<>();

    searchFileInDirectory(fileName, dir, results, futures);

    // Wait for all threads and collect the results
    for (Future<List<String>> future : futures) {
      try {
        results.addAll(future.get());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); // Re-set the interrupt flag
        LOGGER.error("Thread was interrupted during execution", e);
      } catch (ExecutionException e) {
        LOGGER.error("Error occurred during the file search execution", e);
      }
    }

    return results;
  }

  /**
   * Shuts down the executor service, stopping all active threads.
   */
  public void shutdown() {
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
        if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
          LOGGER.error("ExecutorService did not terminate");
        }
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  private void searchFileInDirectory(String fileName, File directory, List<String> results, List<Future<List<String>>> futures) {
    File[] files = directory.listFiles();
    if (files == null) {
      return;
    }

    for (File file : files) {
      if (file.isDirectory()) {
        // Recurse in a new thread
        futures.add(executorService.submit(() -> {
          List<String> localResults = new ArrayList<>();
          searchFileInDirectory(fileName, file, localResults, new ArrayList<>());
          return localResults;
        }));
      } else if (fileName.equals(file.getName())) {
        results.add(file.getAbsolutePath());
      }
    }
  }
}
