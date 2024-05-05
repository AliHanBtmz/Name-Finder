package com.alihanbatmazoglu;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.util.Span;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class Main {
    public static void main(String[] args) throws IOException {
        // Getting URL from command line
        if (args.length == 0) {
            System.out.println("Lutfen URL'yi girin");
            return;
        }
        String URL = args[0];
        // Fetching text from the specified URL
        Document document = Jsoup.connect(URL).get();
        Element body = document.body();
        String text = body.text();

        // Loading sentence detection model
        InputStream sentenceInputStream = new FileInputStream("src/main/resources/en-sent.bin");
        SentenceModel Model = new SentenceModel(sentenceInputStream);
        SentenceDetectorME Detector = new SentenceDetectorME(Model);

        // Loading tokenizer model
        InputStream tokenizerInput = new FileInputStream("src/main/resources/en-token.bin");
        TokenizerModel TokenizerM = new TokenizerModel(tokenizerInput);
        Tokenizer tokenizer = new TokenizerME(TokenizerM);

        // Loading name finder model
        InputStream nameInput = new FileInputStream("src/main/resources/en-ner-person.bin");
        TokenNameFinderModel nameModel = new TokenNameFinderModel(nameInput);
        NameFinderME nameFinder = new NameFinderME(nameModel);

        // Splitting text into sentences
        String[] cumle = Detector.sentDetect(text);

        // Finding names in each sentence and printing them
        for (String sentence : cumle) {
            String[] tokens = tokenizer.tokenize(sentence);
            Span[] nameSpans = nameFinder.find(tokens);
            String[] names = Span.spansToStrings(nameSpans, tokens);
            for (String name : names) {
                System.out.println(name);
            }
        }
        // Closing the used resource files
        sentenceInputStream.close();
        tokenizerInput.close();
        nameInput.close();
    }
}

