package com.lundu.demo_rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class RagRestController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:/prompts/prompt.st")
    private Resource promptResource;

    public RagRestController(ChatClient.Builder builder, VectorStore vectorStore){
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    @PostMapping(value = "/ask", produces = MediaType.TEXT_PLAIN_VALUE)
    public String ask(String question){
        PromptTemplate promptTemplate = new PromptTemplate(promptResource);
        List<Document> documents = vectorStore.similaritySearch(question);
        List<String> context = documents.stream().map(Document::getContent).toList();
        Prompt prompt = promptTemplate.create(Map.of("context", context, "question", question));
        return chatClient.prompt(prompt).call().content();
    }
}
