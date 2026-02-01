# AI Translator Extension - Backend

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Spring Boot REST API for AI-powered text translation using Google Gemini AI.

## 🔗 Links

- **Frontend**: [AI Extension UI](https://github.com/HaMinhDung/AI-Extension-UI)
- **Developer**: [Ha Minh Dung](https://github.com/HaMinhDung)

## ✨ Features

- AI-powered translation via Google Gemini
- Rate limiting (100 req/min per IP)
- CORS-enabled for browser extensions
- Secure API key management

## 🛠️ Tech Stack

Java 17 • Spring Boot 4.0.2 • Maven • Google Gemini AI • Bucket4j

## 🚀 Quick Start

**Prerequisites**: Java 17+, Maven 3.6+, [Gemini API Key](https://makersuite.google.com/app/apikey)

```bash
# Clone
git clone https://github.com/HaMinhDung/AI_Extension.git
cd AI_Extension

# Set API key (Windows)
$env:GEMINI_API_KEY="your_api_key_here"

# Build & Run
mvn spring-boot:run
```

3. **Run the application**
```bash
# Development
./mvnw spring-boot:run

# Production
./mvnw clean package
java -jar target/AITranslatorExtension-0.0.1-SNAPSHOT.jar
```


Server runs at `http://localhost:8080`

## 📡 API

**POST** `/api/generate`
```json
{
  "text": "Hello, world!",
  "prompt": "Translate to Spanish"
}
```

**Response:**
```json
{
  "result": "¡Hola, mundo!",
  "success": true
}
```

## 📄 License

MIT © [Ha Minh Dung](https://github.com/HaMinhDung)


**Code Explanation:**
```bash
curl -X POST http://localhost:8080/api/generate \
  -H "Content-Type: application/json" \
  -d '{"text": "const x = () => {}", "prompt": "Explain this code"}'
```

## 📁 Project Structure

```
src/main/java/com/extension/AITranslatorExtension/
├── client/          # Gemini API HTTP client
├── config/          # CORS and filter configuration
├── controller/      # REST API endpoints
├── dto/             # Request/Response models
├── exception/       # Error handling
├── filter/          # Rate limiting filter
└── service/         # Business logic
```

## ⚙️ Configuration

Environment variables (see [AWS_ENV_VARIABLES.md](AWS_ENV_VARIABLES.md) for details):
- `GEMINI_API_KEY` (required)
