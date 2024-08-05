package com.lundu.demo_rag;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MultiSearchToPDFWithoutAPI {
    public static void main(String[] args) throws DocumentException, IOException {
        String query = "c'est quoi le college de paris";
        query = query.replace(" ", "+");

        // Perform Google Search
        List<String> googleResults = googleSearch(query);
        for (String url : googleResults) {
            boolean contientUrl = url.contains("https://maps.google.com/maps");
            if(!contientUrl){
                try {
                    String content = fetchContent(url);
                    generatePDF("google_" + url.hashCode() + ".pdf", content);
                } catch (IOException i){

                }
            }
        }

        // Perform Bing Search
        List<String> bingResults = bingSearch(query);
        for (String url : bingResults) {
            boolean contientUrl = url.contains("https://maps.google.com/maps");
            if(!contientUrl){
                String content = fetchContent(url);
                generatePDF("bing_" + url.hashCode() + ".pdf", content);
            }
        }
    }

    private static List<String> googleSearch(String query) throws IOException {
        String url = "https://www.google.com/search?q=" + query;
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
        Elements results = doc.select("a[href]");
        List<String> urls = new ArrayList<>();
        for (Element result : results) {
            String link = result.attr("href");
            if (link.startsWith("/url?q=")) {
                urls.add(link.substring(7, link.indexOf("&")));
            }
        }
        return urls;
    }

    private static List<String> bingSearch(String query) throws IOException {
        String url = "https://www.bing.com/search?q=" + query;
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
        Elements results = doc.select("a[href]");
        List<String> urls = new ArrayList<>();
        for (Element result : results) {
            String link = result.attr("href");
            if (link.startsWith("http")) {
                urls.add(link);
            }
        }
        return urls;
    }

    private static String fetchContent(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements paragraphs = doc.select("p");
        StringBuilder content = new StringBuilder();
        for (Element paragraph : paragraphs) {
            content.append(paragraph.text()).append("\n");
        }
        return content.toString();
    }

    public static void generatePDF(String filename, String content) throws FileNotFoundException{
        // Création d'un writer pour écrire dans le fichier PDF

        // Création d'un document PDF
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            document.open();
            // Ajout d'un paragraphe au document
            document.add(new Paragraph(content));
            // Fermeture du document
            document.close();
            writer.close();
        }catch (Exception e){

        }
    }

}

