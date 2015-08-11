package com.mbriot.controller;

import com.mbriot.indexer.ComputedPDF;
import com.mbriot.indexer.Mouvement;
import com.mbriot.lucene.Indexer;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;

@Controller
@PropertySource("application.properties")
public class IndexerController {

    @Autowired
    Indexer indexer;

    @Autowired
    Environment env;

    private boolean alreadyIndexed = false;

    @RequestMapping(value="/index", method= RequestMethod.POST)
    public @ResponseBody
    void handleFileUpload(){

        if(!alreadyIndexed){
            File dirFile = new File(env.getProperty("path.to.files"));

            try {
                File file = dirFile.listFiles()[0];
                String parsedText = extractBrutText(file);

                ComputedPDF computedPDF = new ComputedPDF();
                computedPDF.process(parsedText);

                indexer.indexMouvement(computedPDF.getMouvements());

                alreadyIndexed = true;

            } catch (Exception e) {
                System.out.println("You failed to upload " + " => " + e.getMessage()) ;
            }
        }

    }

    private static String extractBrutText(File file) throws IOException {
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        PDFTextStripper pdfStripper = null;
        String extractedText = "";
        try {
            PDFParser parser = new PDFParser(new FileInputStream(file));
            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdDoc = new PDDocument(cosDoc);
            int numberOfPages = pdDoc.getNumberOfPages();
            pdfStripper.setStartPage(1);
            pdfStripper.setEndPage(numberOfPages);
            extractedText = pdfStripper.getText(pdDoc);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            pdDoc.close();
            cosDoc.close();
        }
        return extractedText;
    }
}
