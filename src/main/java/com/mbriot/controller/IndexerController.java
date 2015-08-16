package com.mbriot.controller;

import com.mbriot.indexer.ComputedPDF;
import com.mbriot.indexer.Mouvement;
import com.mbriot.lucene.Indexer;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@PropertySource("application.properties")
public class IndexerController {

    static final Logger LOG = LoggerFactory.getLogger(IndexerController.class);

    @Autowired
    Indexer indexer;

    @Autowired
    Environment env;

    List<Mouvement> mouvements = new ArrayList<Mouvement>();

    @RequestMapping(value="/index", method= RequestMethod.POST)
    public @ResponseBody
    void index(){

        File dirFile = new File(env.getProperty("path.to.files"));

        try {
            for(File file : dirFile.listFiles()){

                LOG.debug("process file " + file.getName());

                String parsedText = extractBrutText(file);

                ComputedPDF computedPDF = new ComputedPDF();
                computedPDF.process(parsedText);
                addMouvements(computedPDF.getMouvements());
            }

            LOG.debug("start indexation of " + mouvements.size() + " mouvements");
            indexer.indexMouvement(mouvements);

        } catch (Exception e) {
            LOG.error("indexation failed ", e);
            System.out.println("You failed to upload " + " => " + e.getMessage()) ;
        }

    }

    private void addMouvements(List<Mouvement> mvts) {
        for(Mouvement mouvement : mvts){
            mouvements.add(mouvement);
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
