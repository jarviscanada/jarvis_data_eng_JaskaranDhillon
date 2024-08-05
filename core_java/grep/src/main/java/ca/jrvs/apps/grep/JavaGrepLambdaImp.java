package ca.jrvs.apps.grep;

import java.util.Arrays;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaGrepLambdaImp extends JavaGrepImpl {

  final Logger logger = LoggerFactory.getLogger(JavaGrepLambdaImp.class);

  @Override
  public List<File> listFiles(String rootDir) {
    File directory = new File(rootDir);

    return Arrays.stream(directory.listFiles()).flatMap(file -> {
      if (file.isFile()) {
        return Stream.of(file);
      } else if (file.isDirectory()) {
        return listFiles(file.getAbsolutePath()).stream();
      } else {
        return Stream.empty();
      }
    }).collect(Collectors.toList());
  }

  @Override
  public List<String> readLines(File inputFile) {
    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
      return reader.lines().collect(Collectors.toList());
    } catch (IOException e) {
      logger.debug("Encountered an error when reading file", e);
      return Collections.emptyList();
    }
  }

  public static void main(String[] args) {
    if (args.length != 3) {
      throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
    }

    BasicConfigurator.configure();

    JavaGrepLambdaImp javaGrepLambdaImp = new JavaGrepLambdaImp();
    javaGrepLambdaImp.setRegex(args[0]);
    javaGrepLambdaImp.setRootPath(args[1]);
    javaGrepLambdaImp.setOutFile(args[2]);

    try {
      javaGrepLambdaImp.process();
    } catch (Exception ex) {
      javaGrepLambdaImp.logger.error("Error: Unable to process", ex);
    }
  }
}