# 🔌 Backend API - Quick Reference for Extension Developers

## 📡 Backend Endpoint

**Base URL:** `http://13.212.57.208:8080`

---

## 🔑 Required Manifest Permissions

Add to your `manifest.json`:

```json
{
  "manifest_version": 3,
  "host_permissions": [
    "http://13.212.57.208:8080/*"
  ]
}
```

---

## 📮 API Endpoints

### 1. Health Check

**Endpoint:** `GET /api/health`

**Example:**
```javascript
const response = await fetch('http://13.212.57.208:8080/api/health');
const text = await response.text();
console.log(text); // "Service is running"
```

### 2. Text Processing (Translation, Summarization, etc.)

**Endpoint:** `POST /api/generate`

**Request:**
```javascript
{
  "text": "Your text here",
  "prompt": "What you want AI to do"
}
```

**Response (Success):**
```javascript
{
  "result": "AI-generated response",
  "success": true,
  "error": null,
  "fromCache": false
}
```

**Response (Error):**
```javascript
{
  "result": null,
  "success": false,
  "error": "Error message",
  "fromCache": false
}
```

---

## 💻 Code Examples

### Basic Usage

```javascript
async function callAI(text, prompt) {
  try {
    const response = await fetch('http://13.212.57.208:8080/api/generate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        text: text,
        prompt: prompt
      })
    });

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    const data = await response.json();
    
    if (data.success) {
      return data.result;
    } else {
      throw new Error(data.error);
    }
  } catch (error) {
    console.error('Backend error:', error);
    throw error;
  }
}
```

### With Error Handling

```javascript
async function callAIWithRetry(text, prompt, maxRetries = 3) {
  for (let i = 0; i < maxRetries; i++) {
    try {
      const response = await fetch('http://13.212.57.208:8080/api/generate', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ text, prompt })
      });

      // Check rate limiting
      const remaining = response.headers.get('X-RateLimit-Endpoint-Remaining');
      console.log(`Rate limit remaining: ${remaining}`);

      if (response.status === 429) {
        throw new Error('Rate limit exceeded. Please wait a moment.');
      }

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      const data = await response.json();
      
      if (data.success) {
        return data.result;
      } else {
        throw new Error(data.error);
      }
      
    } catch (error) {
      if (i === maxRetries - 1) throw error;
      
      // Wait before retry
      await new Promise(resolve => setTimeout(resolve, 1000 * (i + 1)));
    }
  }
}
```

### Usage in Extension

```javascript
// In your extension's content script or popup script

// Translation example
async function translateText(text, targetLanguage) {
  const prompt = `Translate this text to ${targetLanguage}`;
  return await callAI(text, prompt);
}

// Summarization example
async function summarizeText(text) {
  const prompt = "Provide a concise summary of this text";
  return await callAI(text, prompt);
}

// Code explanation example
async function explainCode(code) {
  const prompt = "Explain what this code does in simple terms";
  return await callAI(code, prompt);
}

// Custom prompt example
async function customRequest(text, customPrompt) {
  return await callAI(text, customPrompt);
}

// Example usage:
const result = await translateText("Hello world", "Vietnamese");
console.log(result); // "Xin chào thế giới"
```

---

## 🚦 Rate Limits

**IP-based:** 100 requests per minute per IP address
**Endpoint-based:** 20 requests per minute for `/api/generate`

**Rate limit headers in response:**
- `X-RateLimit-IP-Remaining`: Remaining requests for your IP
- `X-RateLimit-Endpoint-Remaining`: Remaining requests for this endpoint
- `X-RateLimit-Reset`: Timestamp when limits reset

**Example of checking rate limits:**
```javascript
const response = await fetch('http://13.212.57.208:8080/api/generate', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ text, prompt })
});

const remaining = response.headers.get('X-RateLimit-Endpoint-Remaining');
const reset = response.headers.get('X-RateLimit-Reset');

console.log(`You have ${remaining} requests remaining`);
console.log(`Resets at: ${new Date(parseInt(reset))}`);
```

---

## 🎨 Use Cases

### 1. Translation
```javascript
const result = await callAI(
  "Hello, how are you?",
  "Translate this text to Spanish"
);
// Result: "Hola, ¿cómo estás?"
```

### 2. Summarization
```javascript
const result = await callAI(
  "Long article content here...",
  "Summarize this article in 2-3 sentences"
);
```

### 3. Code Explanation
```javascript
const result = await callAI(
  "const add = (a, b) => a + b;",
  "Explain what this JavaScript code does"
);
```

### 4. Grammar Check
```javascript
const result = await callAI(
  "Me go to store yesterday",
  "Correct the grammar in this sentence"
);
```

### 5. Style Rewriting
```javascript
const result = await callAI(
  "Your text here",
  "Rewrite this in a professional tone"
);
```

---

## ⚠️ Common Errors and Solutions

### Error: "Failed to fetch"
**Cause:** Network issue or backend not running
**Solution:** Check backend is running at http://13.212.57.208:8080

### Error: "CORS policy"
**Cause:** Missing host_permissions in manifest.json
**Solution:** Add `"http://13.212.57.208:8080/*"` to host_permissions

### Error: HTTP 400
**Cause:** Invalid request format
**Solution:** Ensure JSON has both "text" and "prompt" fields, both non-empty

### Error: HTTP 429
**Cause:** Rate limit exceeded
**Solution:** Wait a minute before making more requests

### Error: HTTP 500
**Cause:** Server error
**Solution:** Check request format, or try again later

---

## 🧪 Testing

### Test Backend Connection
```javascript
async function testBackend() {
  try {
    const response = await fetch('http://13.212.57.208:8080/api/health');
    const text = await response.text();
    console.log('✅ Backend is working:', text);
    return true;
  } catch (error) {
    console.error('❌ Backend connection failed:', error);
    return false;
  }
}
```

### Test Translation
```javascript
async function testTranslation() {
  try {
    const result = await callAI(
      "Hello, world!",
      "Translate to Vietnamese"
    );
    console.log('✅ Translation result:', result);
  } catch (error) {
    console.error('❌ Translation failed:', error);
  }
}
```

---

## 📝 Request/Response Format

### Valid Request
```json
{
  "text": "Content to process (required, min 1 char)",
  "prompt": "Instruction for AI (required, min 1 char)"
}
```

### Successful Response
```json
{
  "result": "AI-generated response",
  "success": true,
  "error": null,
  "fromCache": false
}
```

### Error Response
```json
{
  "result": null,
  "success": false,
  "error": "Detailed error message",
  "fromCache": false
}
```

---

## 🔧 Debugging Tips

1. **Check browser console** (F12) for error messages
2. **Check Network tab** to see request/response details
3. **Verify manifest.json** has correct host_permissions
4. **Test with curl** to verify backend is working:
   ```bash
   curl http://13.212.57.208:8080/api/health
   ```
5. **Check rate limits** in response headers
6. **Use try-catch** to handle errors gracefully

---

## 📞 Support

If you encounter issues:
1. Check `CORS_TROUBLESHOOTING.md` for detailed troubleshooting
2. Verify backend is running: http://13.212.57.208:8080/api/health
3. Check browser console for specific error messages
4. Verify your extension's manifest.json has correct permissions

---

## 🔐 Security Notes

- API key is managed server-side (never exposed to clients)
- All requests go through rate limiting
- CORS is configured to allow browser extensions
- Use HTTPS in production for better security
