package com.lundu.demo_rag;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader {

    @Value("classpath:/pdfs/memoire.pdf")
    private Resource pdfFile;

    @Autowired
    private VectorStore vectorStore;

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @PostConstruct
    public void initDb(){
        PagePdfDocumentReader documentReader = new PagePdfDocumentReader(pdfFile);
        List<Document> documents = documentReader.get();
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> chunks = textSplitter.split(documents);
        vectorStore.add(chunks);
    }

}
