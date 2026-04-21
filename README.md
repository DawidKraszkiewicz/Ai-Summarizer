# AI Content Summarizer

A simple RAG project based on Spring Boot, Spring AI, Ollama, and PostgreSQL with the `pgvector` extension.

The application allows you to:
- load text into a vector database,
- generate embeddings for that text,
- find the most similar fragments to a given topic,
- pass the found context to the LLM model,
- return a response based solely on the data stored in the database.



## This project uses:

- Java 21
- Spring Boot 4
- Spring AI 2.0.0-M4
- Ollama
- Embedding model: `mxbai-embed-large`
- Chat model: `llama3.2`
- PostgreSQL
- `pgvector`
- Maven
- Docker Compose to launch PostgreSQL

## How it works

Current flow:

1. `POST /api/v1/content/ingest`
   receives raw text.

2. `IngestionService`
   splits text into fragments and writes them to `VectorStore`.

3. `Spring AI + Ollama`
   Generate embeddings for each fragment.

4. `PgVectorStore`
   Saves text content and embeddings `vector_store` in PostgreSQL.

5. `GET /api/v1/content/summarize?topic=...`
    Looks for similar documents in vector db for given topic

6. `SummarizationService`
    Builds context from found fragments and passes them to llama3.2

7. Model only replies based on context, if context not found model replies:
   `No data in database relevant to this topic`


## Requirements

- Java 21+
- Docker Desktop
- working instance of Ollama
- Downloaded models in Ollama:
  - `mxbai-embed-large`
  - `llama3.2`

## How to launch

### 1. Run PostgreSQL with pgvector

```powershell
docker compose up -d
```

The database will be set up with the following configuration:

- host: `localhost`
- port: `5432`
- database: `ai_db_v2`
- user: `myuser`
- password: `mypassword`

### 2. Launch Ollama

If Ollama isn't running locally, launch it

Download required models:

```powershell
ollama pull mxbai-embed-large
ollama pull llama3.2
```

Ollama waits at address:

```text
http://localhost:11434
```

### 3. Launch the application

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, with IntelliJ run:

`AiContentSummarizerApplication`

## Configuration

Most important settings are in  `src/main/resources/application.yml`.

Used are:

- PostgreSQL: `jdbc:postgresql://localhost:5432/ai_db_v2`
- embedding model: `mxbai-embed-large`
- chat model: `llama3.2`
- vector store schema init: on
- similarity search:
  - `topK = 5`
  - `similarityThreshold = 0.0`

## API

### 1. Add text to knowledge base

```http
POST /api/v1/content/ingest
Content-Type: text/plain
```

example body:

```text
In 2026, coffee became the most important raw material on Earth. Java programmers discovered that after drinking four espressos, their code compiled without errors. Meanwhile, scientists at MIT proved that artificial intelligence runs 20% faster when servers are cooled with cold brew.
```

example `curl`:

```powershell
curl -X POST "http://localhost:8080/api/v1/content/ingest" `
  -H "Content-Type: text/plain" `
  --data-raw "In 2026, coffee became the most important raw material on Earth. Java programmers discovered that after drinking four espressos, their code compiled without errors. Meanwhile, scientists at MIT proved that artificial intelligence runs 20% faster when servers are cooled with cold brew."
```

### 2. Test write of example data

```http
POST /api/v1/content/ingest-test
```

This endpoint adds an additional text embedded in code.

### 3. Get a summary

```http
GET /api/v1/content/summarize?topic=coffee
```

Przykład:

```powershell
curl "http://localhost:8080/api/v1/content/summarize?topic=cofee"
```

## How to test

Scenario:

1. Launch Postgres and Ollama.
2. Launch the app.
3. Send `POST /api/v1/content/ingest-test`.
4. Send `GET /api/v1/content/summarize?topic=kawa`.
5. Check the answer.

You could also use the file:

- `src/main/java/com/dev/ai/aicontentsummarizer/controller/api-test.http`

## Logs and debugging

Project has logging enabled:

- `org.springframework.ai: DEBUG`
- `org.springframework.jdbc: DEBUG`

Additionaly, the app logs information:

- How many fragments where inserted to vector store,
- How many documents have been found relevant to the topic.

## What's saved in the database

`PgVectorStore` creates table `vector_store`, in which there are:

- `content`
- `metadata`
- `embedding`


## Limits

This is a project in work, currently having limitations: 

- data is sent in text format using API,
- lack of module parsing docx, pdf files


## Next Steps

Next steps are:
- add a module to parse files in format: docx. pdf

## Quickstart

```powershell
docker compose up -d
ollama pull mxbai-embed-large
ollama pull llama3.2
.\mvnw.cmd spring-boot:run
```

Then:

```powershell
curl -X POST "http://localhost:8080/api/v1/content/ingest-test"
curl "http://localhost:8080/api/v1/content/summarize?topic=coffee"
```
