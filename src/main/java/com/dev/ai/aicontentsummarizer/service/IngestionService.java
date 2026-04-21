package com.dev.ai.aicontentsummarizer.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngestionService {

    private final VectorStore vectorStore;
    private final TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();

    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void ingest(String content) {
        System.out.println("LOG: Received text to save: " + content);
        var document = new Document(content);
        List<Document> chunks = tokenTextSplitter.apply(List.of(document));
        vectorStore.accept(chunks);
        System.out.println("LOG: Saved chunks do vector store: " + chunks.size());
    }
}
