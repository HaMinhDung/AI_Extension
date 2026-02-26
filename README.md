# AI Translator Extension – Backend

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen)
![AWS](https://img.shields.io/badge/Deployed%20on-AWS-blue)
![License](https://img.shields.io/badge/License-MIT-blue)

Production-grade backend service powering an AI-enabled browser extension for real-time text translation.

The service is designed with a strong emphasis on scalability, security, and real-world cloud deployment practices.

---

## 🔗 Related Links

- **Frontend Repository**: https://github.com/HaMinhDung/AI-Extension-UI
- **Live Backend (Production)**: https://ai-extension-api.duckdns.org
- **Author**: https://github.com/HaMinhDung

---

##  System Overview

This backend serves as a centralized AI inference layer for a browser-based translation extension.

All AI requests are routed through the backend to prevent client-side API key exposure, enforce rate limiting, and enable centralized monitoring.

The system follows a stateless REST architecture, allowing horizontal scaling and seamless cloud deployment.

---

##  System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                             │
│  ┌────────────────────────────────────────────────────────┐     │
│  │  Browser Extension (Frontend - React)                  │     │
│  │  - User interaction                                    │     │
│  │  - No API keys stored                                  │     │
│  └────────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ HTTPS (443)
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                     INFRASTRUCTURE LAYER                        │
│  ┌────────────────────────────────────────────────────────┐     │
│  │  Nginx Reverse Proxy                                   │     │
│  │  - TLS termination (Let's Encrypt)                     │     │
│  │  - Request forwarding                                  │     │
│  └────────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP (8080)
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      APPLICATION LAYER                          │
│  ┌────────────────────────────────────────────────────────┐     │
│  │  Spring Boot Backend (AWS EC2)                         │     │
│  │  ┌──────────────────────────────────────────────────┐  │     │
│  │  │  Rate Limiting Filter (100 req/min per IP)       │  │     │
│  │  └──────────────────────────────────────────────────┘  │     │
│  │  ┌──────────────────────────────────────────────────┐  │     │
│  │  │  REST Controller (/api/generate)                 │  │     │
│  │  └──────────────────────────────────────────────────┘  │     │
│  │  ┌──────────────────────────────────────────────────┐  │     │
│  │  │  Translation Service (Business Logic)            │  │     │
│  │  └──────────────────────────────────────────────────┘  │     │
│  │  ┌──────────────────────────────────────────────────┐  │     │
│  │  │  Gemini Client (HTTP Client)                     │  │     │
│  │  └──────────────────────────────────────────────────┘  │     │
│  │                                                        │     │
│  │   Stateless → Horizontally Scalable                  │     │
│  │   API Key injected via Environment Variables         │     │
│  └────────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ HTTPS API Call
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      EXTERNAL SERVICE                           │
│  ┌────────────────────────────────────────────────────────┐     │
│  │  Google Gemini API                                     │     │
│  │  - AI model inference                                  │     │
│  │  - Text generation & translation                       │     │
│  └────────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────────┘

Key Design Principles:
✓ API key only lives on server (never exposed to frontend)
✓ Rate limiting enforced at application layer
✓ Stateless architecture enables horizontal scaling
✓ Trust boundary: All AI requests proxied through backend
✓ HTTPS enforced end-to-end for secure communication
```

---

##  Key Features

- AI-powered text translation using Google Gemini
- Stateless REST API design
- Per-IP rate limiting (100 requests/min)
- Centralized exception handling with consistent error responses
- Secure environment-based API key management
- CORS configuration optimized for browser extensions

---

## 🏗 Architecture & Tech Stack

- **Language**: Java 17
- **Framework**: Spring Boot 4.0.2
- **Build Tool**: Maven
- **AI Provider**: Google Gemini
- **Rate Limiting**: Bucket4j
- **Web Server**: Embedded Apache Tomcat
- **Cloud Platform**: AWS EC2 (Linux)
- **Process Manager**: systemd
- **Reverse Proxy**: Nginx
- **TLS / HTTPS**: Let’s Encrypt

---

##  Deployment & Operations

The application is deployed on AWS EC2 and runs as a long-lived system service managed by `systemd`.

Operational highlights:

- Automatic service restarts on failure
- Secure injection of secrets via OS-level environment variables
- HTTPS termination at the reverse proxy layer
- Centralized logging via `journalctl`

This deployment setup mirrors real-world production environments.

---

##  Security Considerations

- No secrets committed to source control
- API keys stored exclusively as environment variables
- HTTPS enforced for all external traffic
- AI credentials never exposed to the frontend

---

##  Performance & Reliability

- IP-based rate limiting (100 requests per minute)
- Stateless request handling for horizontal scalability
- Optimized request lifecycle for low-latency AI responses
- Graceful error handling with consistent API contracts

---

##  Live Demo (Production)

Base URL:
```
https://ai-extension-api.duckdns.org
```

> Public endpoint for demonstration and testing purposes only.  
> Requests are rate-limited.

### Example Request

```bash
curl -X POST https://ai-extension-api.duckdns.org/api/generate \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Hello, world!",
    "prompt": "Translate to Vietnamese"
  }'
```

---

## 📡 API Reference

### POST `/api/generate`

**Request**
```json
{
  "text": "Hello, world!",
  "prompt": "Translate to Spanish"
}
```

**Response**
```json
{
  "result": "¡Hola, mundo!",
  "success": true
}
```

---

##  Project Structure

```
src/main/java/com/extension/AITranslatorExtension/
├── client/          # Gemini API HTTP client
├── config/          # CORS and global configuration
├── controller/      # REST API endpoints
├── dto/             # Request / response models
├── exception/       # Centralized error handling
├── filter/          # Rate limiting filter
└── service/         # Business logic
```

---

##  License

MIT © Ha Minh Dung
