## SER 516 - Software Agility | Group 6 Project

## Team:
Arvind Tadasad (atadasad)
Dikshant Meshram (dmeshram)
Deepanshu Saxena (dsaxen14)
Prince Rwamatwara (irwamatw)
Ethan Freeman (ehfreema) 

## Working Metric:
Fan-In | Fan-Out

## Project Overview

This project is developed as part of SER 516 – Software Agility.  
The team follows agile principles and tracks all development activities in Taiga.

The primary architectural metric monitored in this project is:

- **Fan-In**: Number of modules depending on a given module.
- **Fan-Out**: Number of modules a given module depends on.

These metrics help evaluate coupling and maintainability of the system.

## Tech Stack

- Java 17
- Maven
- JUnit
- GitHub Actions (CI)
- Jenkins (External CI)
- Taiga (Agile Project Tracking)

## Project Structure

SER516-Group6-Project/
│── src/
│   ├── main/java/
│   └── test/java/
│── pom.xml
│── Jenkinsfile
│── .github/workflows/ci.yml
│── README.md

## Usage

After building the project with `mvn clean package`, you can run the metrics tool using the generated JAR file in the `target/` directory.

### Command Line Interface

```bash
java -jar target/ser516-group6-metrics-1.0.0.jar <input-path> [options]
```

**Arguments:**
- `<input-path>`: Path to the root directory of the Java project to analyze.

**Options:**
- `-o, --output <file>`: Specify the output file path. If omitted, results are printed to stdout.
- `-f, --format <fmt>`: Specify the output format. Supported formats: `text` (default), `csv`, `json`.
- `-h, --help`: Show help message.

### Examples

**Analyze a project and print results to console:**
```bash
java -jar target/ser516-group6-metrics-1.0.0.jar /path/to/project
```

**Save results as CSV:**
```bash
java -jar target/ser516-group6-metrics-1.0.0.jar /path/to/project -o metrics.csv -f csv
```

**Save results as JSON:**
```bash
java -jar target/ser516-group6-metrics-1.0.0.jar /path/to/project -o metrics.json -f json
```