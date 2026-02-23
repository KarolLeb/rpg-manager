CREATE DATABASE rpg_auth;
CREATE DATABASE rpg_admin;

-- Enable pgvector extension for RAG-based activity log
\c rpg;
CREATE EXTENSION IF NOT EXISTS vector;
