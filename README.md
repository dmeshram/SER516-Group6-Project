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

This project implements class-level Fan-In and Fan-Out metrics for Java projects.
The tool:
- Parses Java source files
- Identifies class-to-class dependencies
- Computes:
  - Fan-Out → Number of distinct outbound dependencies from a class
  - Fan-In → Number of distinct inbound dependencies to a class
- Outputs structured results in:
  - JSON
  - CSV
  - Console output
- Runs via command line
- Integrates with Jenkins CI pipeline

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
│
├── src/
│   ├── main/java/edu/asu/ser516/metrics/
│   │   ├── FanOutComputerMain.java
│   │   ├── SourceScanner.java
│   │   ├── ...
│   │
│   └── test/
│
├── pom.xml
├── Jenkinsfile
├── docker/
├── README.md
└── target/

## Get Started with the Project
## 1. Clone the Repository.
git clone https://github.com/dmeshram/SER516-Group6-Project.git
cd SER516-Group6-Project

## Input setup
Create a folder in root directory "Input"
cd Input
git clone https://github.com/pH-7/Simple-Java-Calculator.git
cd ..

## Build the Project
mvn clean install

## Run the Application
mvn clean -DskipTests compile

mvn exec:java \
-Dexec.mainClass="edu.asu.ser516.metrics.FanOutComputerMain" \
-Dexec.args="input/Simple-Java-Calculator/src"

mvn exec:java \
-Dexec.mainClass="edu.asu.ser516.metrics.FanOutComputerMain" \
-Dexec.args="input/Simple-Java-Calculator/src both out"

## Output Results
You can check the output, in "Out" folder in root directory.

## Grafana + PostgreSQL (metrics for dashboards)

Fan-out results can be written to PostgreSQL so Grafana can visualize them (snapshot and time series).

### 1. Start PostgreSQL

```bash
docker compose up -d
```

This starts Postgres on port 5432 and runs `docker/init-fanout.sql` to create the `fan_out_metrics` table.

### 2. Run the app with DB write

Set `JDBC_URL` (and optionally `JDBC_USER`, `JDBC_PASSWORD`), then run the fan-out pipeline as usual. Each run inserts a new snapshot (one row per class with the same timestamp).

**Example (from project root):**

```bash
export JDBC_URL="jdbc:postgresql://localhost:5432/metrics"
export JDBC_USER="grafana"
export JDBC_PASSWORD="grafana"

mvn exec:java \
  -Dexec.mainClass="edu.asu.ser516.metrics.FanOutComputerMain" \
  -Dexec.args="input/Simple-Java-Calculator/src"
```

If `JDBC_URL` is not set, the app skips the database write and behaves as before.

### 3. Grafana

- Add a **PostgreSQL** data source: host `localhost:5432` (or `host.docker.internal:5432` if Grafana runs in Docker), database `metrics`, user `grafana`, password `grafana`.
- Use the `fan_out_metrics` table for panels (e.g. snapshot = latest `recorded_at`, time series = filter by `recorded_at`).

## CI/CD - Jenkins Integration
This project includes:
- Jenkinsfile
- Automated pipeline stages:
- Checkout
- Build
- Test
- Generate metrics
- Archive artifacts
## To Run in Jenkins:
- Create a new Pipeline job
- Connect GitHub repository
- Set:
  - Pipeline script from SCM
  - Branch: develop or main
- Build the project

## How Fan-Out is Calculated

For each class:
1. Parse imports and type usages
2. Identify distinct referenced classes
3. Count unique outbound dependencies
4. Store in structured format

## Summary

This project delivers a fully automated, CI-integrated tool for computing class-level Fan-In and Fan-Out metrics for Java applications, aligned with Agile development practices and sprint-based tracking.
