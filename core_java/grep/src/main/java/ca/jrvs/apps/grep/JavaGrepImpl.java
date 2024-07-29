package ca.jrvs.apps.grep;

import java.io.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaGrepImpl implements JavaGrep {

  final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

  private String regex;
  private String rootPath;
  private String outFile;

  public static void main(String[] args) throws IOException {
    if (args.length != 3) {
      throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
    }

    BasicConfigurator.configure();

    JavaGrepImpl javaGrepImp = new JavaGrepImpl();
    javaGrepImp.setRegex(args[0]);
    javaGrepImp.setRootPath(args[1]);
    javaGrepImp.setOutFile(args[2]);

    try {
      javaGrepImp.process();
    } catch (Exception e) {
      javaGrepImp.logger.error("Error: Unable to process", e);
    }
  }

  @Override
  public void process() throws IOException {
    List<String> matchedLines = new ArrayList<String>();

    for (File file : listFiles(getRootPath())) {
      for (String line : readLines(file)) {
        if (containsPattern(line)) {
          matchedLines.add(line);
        }
      }
    }

    writeToFile(matchedLines);
  }

  @Override
  public List<File> listFiles(String rootDir) {
    List<File> result = new ArrayList<>();

    File directory = new File(rootDir);
    File[] directoryFiles = directory.listFiles();

    for (File file : directoryFiles) {
      if (file.isFile()) {
        result.add(file);
      } else if (file.isDirectory()) {
        result.addAll(listFiles(file.getAbsolutePath()));
      }
    }

    return result;
  }

  @Override
  public List<String> readLines(File inputFile) {
    List<String> lines = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
      String line = reader.readLine();

      while (line != null) {
        lines.add(line);
        line = reader.readLine();
      }
    } catch (IOException e) {
      logger.debug("Encountered an error when reading file", e);
    }

    return lines;
  }

  @Override
  public boolean containsPattern(String line) {
    Pattern pattern = Pattern.compile(getRegex());
    Matcher matcher = pattern.matcher(line);

    return matcher.find();
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {
    FileWriter file = new FileWriter(getOutFile());
    BufferedWriter output = new BufferedWriter(file);

    for (String line : lines) {
      output.write(line + "\n");
    }

    output.close();
  }

  @Override
  public String getRootPath() {
    return this.rootPath;
  }

  @Override
  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public String getRegex() {
    return this.regex;
  }

  @Override
  public void setRegex(String regex) {
    this.regex = regex;
  }

  @Override
  public String getOutFile() {
    return this.outFile;
  }

  @Override
  public void setOutFile(String outfile) {
    this.outFile = outfile;
  }
}
