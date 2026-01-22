# MCP Setup Guide

This document outlines the Model Context Protocol (MCP) servers recommended for the RPG Manager project to enhance the AI Agent's capabilities.

## 🛠️ Configured MCPs

### 1. SonarQube MCP
Used for static code analysis, quality gate monitoring, and issue tracking.
- **Capabilities:** Check system health, list projects, search issues, get quality gate status.
- **Setup:**
  - Ensure SonarQube is running via `dev-tools/docker-compose.yml`.
  - Configured to connect to `http://localhost:9000`.
  - Requires `SONAR_TOKEN` from `.env` for authenticated operations.

## 🚀 Recommended MCPs (To Be Configured)

### 2. Everything MCP (Recommended)
Provides broad project understanding and symbol access without the complexity of a Java LSP bridge.
- **Implementation:** [@modelcontextprotocol/server-everything](https://github.com/modelcontextprotocol/servers/tree/main/src/everything)
- **Setup In `settings.json`:**
  ```json
  "everything": {
    "command": "npx",
    "args": ["-y", "@modelcontextprotocol/server-everything"]
  }
  ```

### 3. Docker MCP
Enables direct management of project infrastructure containers.
- **Implementation:** [mcp-server-docker](https://github.com/modelcontextprotocol/servers/tree/main/src/docker).
- **Setup In `settings.json`:**
  ```json
  "mcpServers": {
    "docker": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-docker"]
    }
  }
  ```

### 4. GitHub MCP
Automates repository management, issue tracking, and PR creation.
- **Implementation:** [mcp-server-github](https://github.com/modelcontextprotocol/servers/tree/main/src/github).
- **Setup:** Requires a GitHub Personal Access Token (PAT).

## 📝 Configuration Location
These should be added to your local `settings.json` (e.g., in VS Code or the Gemini Desktop app configuration).
