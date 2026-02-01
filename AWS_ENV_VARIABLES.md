# AWS Environment Variables Configuration

This document lists all required and optional environment variables for deploying this Spring Boot application to AWS.

## Required Environment Variables

### Gemini API Configuration
```bash
GEMINI_API_KEY=your_gemini_api_key_here
```
**Description**: Google Gemini API key for AI text processing.  
**Where to get**: https://makersuite.google.com/app/apikey

> ⚠️ **IMPORTANT**: Replace `your_gemini_api_key_here` with your actual API key. Never commit real API keys to Git.

---

## Optional Environment Variables (with defaults)

### Application Configuration
```bash
APP_NAME=AITranslatorExtension
```
**Description**: Spring application name.  
**Default**: `AITranslatorExtension`

### Server Configuration
```bash
SERVER_PORT=8080
```
**Description**: Server port for the application.  
**Default**: `8080`  
**AWS Note**: For Elastic Beanstalk, use `PORT` or `SERVER_PORT=5000`

---

## Rate Limiting Configuration

### IP-based Rate Limiting
```bash
RATE_LIMIT_IP_REQUESTS=100
RATE_LIMIT_IP_PERIOD=60
```
**Description**: Maximum requests per IP within the time period (in seconds).  
**Defaults**: 100 requests per 60 seconds

### Translation Endpoint Rate Limiting
```bash
RATE_LIMIT_TRANSLATE_REQUESTS=20
RATE_LIMIT_TRANSLATE_PERIOD=60
```
**Description**: Maximum requests to `/api/generate` endpoint per IP.  
**Defaults**: 20 requests per 60 seconds

### Health Check Endpoint Rate Limiting
```bash
RATE_LIMIT_HEALTH_REQUESTS=60
RATE_LIMIT_HEALTH_PERIOD=60
```
**Description**: Maximum requests to `/api/health` endpoint per IP.  
**Defaults**: 60 requests per 60 seconds

---

## Logging Configuration

### Root Logging Level
```bash
LOG_LEVEL_ROOT=INFO
```
**Description**: Root logger level.  
**Options**: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`  
**Default**: `INFO`

### Application Logging Level
```bash
LOG_LEVEL_APP=INFO
```
**Description**: Application-specific logger level.  
**Options**: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`  
**Default**: `INFO`

### Spring Web Logging Level
```bash
LOG_LEVEL_SPRING_WEB=INFO
```
**Description**: Spring Web framework logger level.  
**Options**: `TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`  
**Default**: `INFO`

---

## AWS Deployment Examples

### AWS Elastic Beanstalk
Set environment variables in the Elastic Beanstalk console under **Configuration > Software > Environment properties**.

```properties
GEMINI_API_KEY=your_gemini_api_key_here
SERVER_PORT=5000
LOG_LEVEL_APP=INFO
```

### AWS ECS / Fargate
Define environment variables in the task definition JSON:

```json
{
  "containerDefinitions": [
    {
      "name": "ai-translator",
      "environment": [
        {
          "name": "GEMINI_API_KEY",
          "value": "your_gemini_api_key_here"
        },
        {
          "name": "SERVER_PORT",
          "value": "8080"
        },
        {
          "name": "LOG_LEVEL_APP",
          "value": "INFO"
        }
      ]
    }
  ]
}
```

### AWS Lambda (with SnapStart)
Set environment variables in the Lambda console under **Configuration > Environment variables**.

```bash
GEMINI_API_KEY=your_gemini_api_key_here
```

---

## Local Development

Create a `.env` file (do not commit to Git):

```bash
# Required
GEMINI_API_KEY=your_gemini_api_key_here

# Optional overrides
SERVER_PORT=8080
LOG_LEVEL_APP=DEBUG
RATE_LIMIT_IP_REQUESTS=50
```

Then run with:
```bash
export $(cat .env | xargs) && ./mvnw spring-boot:run
```

Or on Windows PowerShell:
```powershell
Get-Content .env | ForEach-Object {
    $name, $value = $_.split('=')
    Set-Item -Path "env:$name" -Value $value
}
./mvnw.cmd spring-boot:run
```

---

## Security Notes

- ⚠️ **NEVER** commit `.env` files or `application.properties` with real API keys
- ✅ Use AWS Secrets Manager or Parameter Store for sensitive values in production
- ✅ Rotate API keys regularly
- ✅ Use IAM roles for AWS service-to-service communication when possible
