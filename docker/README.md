# Docker Setup

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## Usage

From the `docker/` directory:

```bash
# Build and run
docker compose up --build

# Run after building
docker compose up

# Scan a specific directory
docker compose run --rm metrics-scanner /project/src
```
