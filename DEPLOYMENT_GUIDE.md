# 🚀 Deployment Guide - Updated CORS Configuration

## Changes Made

### Enhanced CORS Configuration
- ✅ Added logging to CORS initialization
- ✅ Added HEAD method support
- ✅ Exposed rate-limiting headers to clients
- ✅ Added comprehensive JavaDoc comments
- ✅ Improved error messages

### New Files Created
1. **test-cors.html** - HTML file to test CORS from browser
2. **CORS_TROUBLESHOOTING.md** - Comprehensive troubleshooting guide

---

## 📦 Deploy to AWS EC2

### Option 1: Deploy New JAR (Recommended)

**1. Build is already complete:**
```bash
# JAR location:
target/AITranslatorExtension-0.0.1-SNAPSHOT.jar
```

**2. Copy JAR to EC2:**

**Windows PowerShell:**
```powershell
# Using SCP (replace with your key path and EC2 details)
scp -i "path/to/your-key.pem" `
  "target/AITranslatorExtension-0.0.1-SNAPSHOT.jar" `
  ec2-user@13.212.57.208:/home/ec2-user/app.jar
```

**Or using WinSCP / FileZilla:**
- Connect to: `13.212.57.208`
- Upload: `target/AITranslatorExtension-0.0.1-SNAPSHOT.jar`
- Rename to: `app.jar`

**3. SSH into EC2:**
```bash
ssh -i "your-key.pem" ec2-user@13.212.57.208
```

**4. Stop current application:**
```bash
# If using systemd
sudo systemctl stop ai-translator

# If running with nohup
pkill -f AITranslatorExtension

# If running in screen/tmux
screen -r  # then Ctrl+C
```

**5. Backup old JAR (optional):**
```bash
mv app.jar app.jar.backup.$(date +%Y%m%d-%H%M%S)
```

**6. Start new application:**

**Option A: Using systemd (Recommended):**
```bash
sudo systemctl start ai-translator
sudo systemctl status ai-translator

# Check logs
sudo journalctl -u ai-translator -f
```

**Option B: Using nohup:**
```bash
nohup java -jar app.jar > app.log 2>&1 &

# Check logs
tail -f app.log
```

**7. Verify deployment:**
```bash
# Check if app is running
ps aux | grep java

# Test health endpoint
curl http://localhost:8080/api/health

# Test from outside
curl http://13.212.57.208:8080/api/health
```

---

### Option 2: No Deployment Needed (If Backend Already Working)

**The CORS configuration improvements are enhancements, not fixes.**

If your backend is already running and accessible at `http://13.212.57.208:8080`, the current CORS setup is already working correctly. The issue is likely in your browser extension.

**To verify:**
```bash
# Test from your local machine
curl -H "Origin: chrome-extension://test" http://13.212.57.208:8080/api/health
```

You should see `Access-Control-Allow-Origin` in the response.

---

## 🧪 Testing After Deployment

### 1. Test from Command Line

**Windows PowerShell:**
```powershell
# Test health endpoint
Invoke-WebRequest -Uri "http://13.212.57.208:8080/api/health" -UseBasicParsing

# Test CORS headers
$headers = @{'Origin' = 'chrome-extension://test'}
Invoke-WebRequest -Uri "http://13.212.57.208:8080/api/health" -Headers $headers -UseBasicParsing | Select-Object -ExpandProperty Headers

# Test generate endpoint
$body = @{
    text = "Hello, world!"
    prompt = "Translate to Vietnamese"
} | ConvertTo-Json

Invoke-WebRequest -Uri "http://13.212.57.208:8080/api/generate" -Method POST -ContentType "application/json" -Body $body -UseBasicParsing
```

### 2. Test from Browser

Open `test-cors.html` in your browser and click the test buttons.

### 3. Test from Extension

Update your extension and test the connection.

---

## 📝 Systemd Service Configuration (Reference)

If you need to create/update systemd service:

**/etc/systemd/system/ai-translator.service:**
```ini
[Unit]
Description=AI Translator Extension Backend
After=network.target

[Service]
Type=simple
User=ec2-user
WorkingDirectory=/home/ec2-user
ExecStart=/usr/bin/java -jar /home/ec2-user/app.jar
Restart=on-failure
RestartSec=10

# Environment variables
Environment="GEMINI_API_KEY=your_api_key_here"
Environment="SERVER_PORT=8080"
Environment="LOG_LEVEL_APP=INFO"

# Logging
StandardOutput=journal
StandardError=journal
SyslogIdentifier=ai-translator

[Install]
WantedBy=multi-user.target
```

**Commands:**
```bash
# Reload systemd
sudo systemctl daemon-reload

# Enable on boot
sudo systemctl enable ai-translator

# Start service
sudo systemctl start ai-translator

# Check status
sudo systemctl status ai-translator

# View logs
sudo journalctl -u ai-translator -f
```

---

## 🔍 Troubleshooting After Deployment

### Application Won't Start

**Check Java version:**
```bash
java -version  # Should be 17 or higher
```

**Check port availability:**
```bash
netstat -tlnp | grep 8080
```

**Check environment variables:**
```bash
# If using systemd
systemctl show ai-translator | grep Environment
```

**Check logs for errors:**
```bash
sudo journalctl -u ai-translator -n 100
```

### Application Starts but Can't Connect

**Check firewall:**
```bash
# AWS Security Group - must allow inbound on port 8080
# Check in AWS Console > EC2 > Security Groups
```

**Check application is listening:**
```bash
netstat -tlnp | grep 8080
# Should show: 0.0.0.0:8080 or :::8080
```

**Test locally on EC2:**
```bash
curl http://localhost:8080/api/health
```

**Test from outside:**
```bash
curl http://13.212.57.208:8080/api/health
```

### CORS Still Not Working

**Verify CORS headers in response:**
```bash
curl -v -H "Origin: chrome-extension://test" http://13.212.57.208:8080/api/health
```

Look for these headers:
```
Access-Control-Allow-Origin: chrome-extension://test
Access-Control-Allow-Credentials: true
Access-Control-Expose-Headers: Authorization, Content-Type, X-RateLimit-IP-Remaining, X-RateLimit-Endpoint-Remaining, X-RateLimit-Reset
```

If headers are present, the issue is in your extension code or manifest.

---

## 🎯 Next Steps

1. **If backend is already running:** Don't redeploy yet. First follow the troubleshooting guide in `CORS_TROUBLESHOOTING.md` to identify if the issue is in your extension.

2. **If you want the enhanced logging:** Deploy the new JAR following Option 1 above.

3. **For production:** Consider setting up HTTPS with a domain name and SSL certificate.

---

## 📋 Deployment Checklist

- [ ] JAR file built successfully
- [ ] Environment variables configured (especially GEMINI_API_KEY)
- [ ] Old application stopped
- [ ] New JAR uploaded to EC2
- [ ] New application started
- [ ] Health endpoint responds (curl test)
- [ ] CORS headers present (curl with Origin header)
- [ ] Extension manifest.json has correct host_permissions
- [ ] Extension code updated with correct backend URL
- [ ] Test from browser extension

---

## 🆘 If You Need Help

**Information to provide:**
1. Error message from EC2 logs
2. Output of: `curl -v http://13.212.57.208:8080/api/health`
3. Output of: `ps aux | grep java`
4. Output of: `netstat -tlnp | grep 8080`
5. Browser console error from extension (F12)
