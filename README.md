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

## Build & Test (Without IDE)

This project builds and runs tests using Maven.

### Run Tests
```bash
mvn clean test

Compile:
mvn clean compile

Package:
mvn clean package