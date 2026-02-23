package com.rpgmanager.backend.activitylog;

/**
 * Service for generating vector embeddings from text. This interface allows
 * swapping the embedding
 * implementation (e.g., local hashing → OpenAI → Ollama) without changing
 * consumers.
 */
public interface EmbeddingService {

    /**
     * Generates a vector embedding for the given text.
     *
     * @param text the text to embed
     * @return a float array of dimension 384
     */
    float[] embed(String text);

    /** Returns the dimension of the embedding vectors produced by this service. */
    int getDimension();
}
