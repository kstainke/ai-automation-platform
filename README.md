# AI Automation Platform

A cloud-native AI-powered document intelligence platform enabling semantic search and question answering across uploaded documents.

## Tech Stack

- **Backend:** Kotlin + Ktor
- **Database:** PostgreSQL + pgvector
- **AI Providers:** OpenAI, Anthropic Claude
- **Containerization:** Docker, Kubernetes, Helm
- **Cloud:** AWS
- **IaC:** Terraform
- **Observability:** OpenTelemetry, Prometheus, Grafana
- **CI/CD:** GitHub Actions

## Architecture

```
User → Ktor API → PostgreSQL/pgvector → OpenAI/Claude API
```

## Getting Started

### Prerequisites

- Kotlin 1.9+
- Java 17+
- Docker & Docker Compose
- PostgreSQL 15+ with pgvector extension

### Local Development

```bash
# Start dependencies
docker compose up -d

# Run the application
./gradlew run
```

## Project Structure

```
backend/       # Ktor application
database/      # SQL migrations
docker/        # Dockerfiles and compose
docs/          # Architecture docs
infrastructure/# Kubernetes/Helm/Terraform
scripts/       # Utility scripts
```

## Status

Day 1 of 90-day plan — starting 2026-06-05
