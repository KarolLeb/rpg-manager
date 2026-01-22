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

### 2. Grafana Extension
Direct integration with Grafana for observability and incident management.
- **Source:** Built-in Gemini Extension.
- **Capabilities:** Search dashboards, query Prometheus/Loki, manage alerts, and track incidents.
- **Connection:** Automatically configured to interact with the project's Grafana instance via tools.

### 3. Everything MCP
Provides broad project understanding and symbol access.
- **Implementation:** `@modelcontextprotocol/server-everything`
- **Status:** Configured via `settings.json`.

### 4. GitHub MCP
Automates repository management, issue tracking, and PR creation.
- **Implementation:** `@modelcontextprotocol/server-github`
- **Status:** Configured via `settings.json`.

## 📝 Configuration Location
These should be added to your local `settings.json` (e.g., in VS Code or the Gemini Desktop app configuration).
