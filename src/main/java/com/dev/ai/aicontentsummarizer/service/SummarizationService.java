package com.dev.ai.aicontentsummarizer.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummarizationService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public SummarizationService(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    public String summarizeTopic(String topic) {
        List<Document> matches = vectorStore.similaritySearch(SearchRequest.builder()
                .query(topic)
                .topK(5)
                .similarityThreshold(0.0)
                .build());

        System.out.println("LOG: NUmber of docs relevant to the topic '" + topic + "': " + matches.size());

        if (matches.isEmpty()) {
            return "No data in database relevant to this topic";
        }

        String context = matches.stream()
                .map(Document::getText)
                .distinct()
                .reduce((left, right) -> left + "\n\n---\n\n" + right)
                .orElse("");

        return this.chatClient.prompt()
                .user(u -> u.text("""
                        Topic: {topic}

                        Context from knowledge base:
                        {context}

                        Task:
                         Summarize key information based solely on context from the knowledge base.
                         If the context doesn't provide information about the topic, answer precisely:
                         There is no data in the database on this topic.
                        """)
                        .param("topic", topic)
                        .param("context", context))
                .call()
                .content();
    }
}
