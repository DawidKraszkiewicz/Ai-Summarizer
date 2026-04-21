package com.dev.ai.aicontentsummarizer.controller;

import com.dev.ai.aicontentsummarizer.service.IngestionService;
import com.dev.ai.aicontentsummarizer.service.SummarizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {

    private final IngestionService ingestionService;
    private final SummarizationService summarizationService;

    public ContentController(IngestionService ingestionService, SummarizationService summarizationService) {
        this.ingestionService = ingestionService;
        this.summarizationService = summarizationService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> uploadContent(@RequestBody String text) {
        ingestionService.ingest(text);
        return ResponseEntity.ok("Saved in memory! Char count: " + text.length() + "text" + text);
    }

    @GetMapping("/summarize")
    public ResponseEntity<String> getSummary(@RequestParam String topic) {
        String summary = summarizationService.summarizeTopic(topic);
        return ResponseEntity.ok(summary);
    }

}