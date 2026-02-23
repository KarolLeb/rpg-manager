package com.rpgmanager.backend.activitylog;

/** Custom exception for embedding-related errors. */
public class EmbeddingException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public EmbeddingException(String message) {
    super(message);
  }

  public EmbeddingException(String message, Throwable cause) {
    super(message, cause);
  }
}
