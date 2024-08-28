# Introduction
This project is a Java implementation of the Linux grep command. It accepts a regex pattern, directory path,
an out file path, and recursively searches through all files in the given directory to find all lines that match the given
regex pattern. The lines are stored in the out file specified.

This app was initially implemented using Lists, and then later updated to utilize Streams and the Lambda
function to improve performance and readability.The dependencies were managed using Maven through the pom.xml file. The application was tested manually using a sample text file and a regex pattern. A Docker image was created and is available on  DockerHub.

# Quick Start
1. Download the docker image using `docker pull jasdhillon152/grep`
2. Start the container using: **docker run --rm \ -v \`pwd\`/data:/data -v \`pwd\`/log:/log \
   ${docker_user}/grep {regex pattern} {search directory} {out file path}**

# Implemenation
## Pseudocode
```
matchedLines = [] 
for file in listFilesRecursively(rootDir)
   for line in readLines(file)
      if containsPattern(line)
         matchedLines.add(line)
writeToFile(matchedLines)
```

## Performance Issue
When reading large files (greater than the machines physical memory), we will run into the `OutOfMemoryError` exception.
The solution to this is to load the file into memory lazily, using the Streams API.

# Test
I used the shakespeare.txt alongside the `.*Romeo.*Juliet.*` pattern and compared it with the output of the 
equiavalent linux grep command to verify that my solution was correct.

# Deployment
A Dockerfile was created which built upon the openjdk:8-alpine image.\
A docker image was created using the Dockerfile and uploaded to Dockhub, available at the `/jasdhillon152/grep` repository.

# Improvement
- Implement the option to support all the various options with the grep command.
- Implement unit tests using JUnit to automate the testing process.
- Although streams have been implemented for the function, the methods still return lists. To improve the memory usage, the return types should be updated to return streams.