package com.mbriot.controller;

import com.mbriot.indexer.ComputedPDF;
import com.mbriot.indexer.Mouvement;
import com.mbriot.lucene.Indexer;
import com.mbriot.utils.Log;
import com.mbriot.utils.TimeCounter;
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

    @Autowired
    Indexer indexer;

    @Autowired
    Environment env;

    List<Mouvement> mouvements = new ArrayList<Mouvement>();

    @RequestMapping(value="/index", method= RequestMethod.POST)
    public @ResponseBody
    void index(){

        TimeCounter totalIndexationTime = new TimeCounter();
        TimeCounter extractBrutTextTime;
        TimeCounter computeTextTime;
        TimeCounter luceneIndexTime;
        File dirFile = new File(env.getProperty("path.to.files"));

        try {
            totalIndexationTime.start();
            for(File file : dirFile.listFiles()){
                //exclude .DS_Store file for mac OS
                if(file.getName().equals(".DS_Store")) continue;

                Log.info("process file %s", file.getName());
                extractBrutTextTime = new TimeCounter();
                extractBrutTextTime.start();
                String parsedText = extractBrutText(file);
                extractBrutTextTime.stop();
                Log.info("time to extract brut text for file %s : %d", file.getName(), extractBrutTextTime.getTime());

                computeTextTime = new TimeCounter();
                computeTextTime.start();
                ComputedPDF computedPDF = new ComputedPDF();
                computedPDF.process(parsedText);
                addMouvements(computedPDF.getMouvements());
                computeTextTime.stop();
                Log.info("time to comput file %s : %d",file.getName(),computeTextTime.getTime());
            }

            totalIndexationTime.stop();
            Log.info("all document computed in %d ms",totalIndexationTime.getTime());
            totalIndexationTime.start();

            luceneIndexTime = new TimeCounter();
            luceneIndexTime.start();
            Log.info("start indexation of %s mouvements",mouvements.size());
            indexer.indexMouvement(mouvements);
            luceneIndexTime.stop();
            Log.info("indexation done in %d ms",luceneIndexTime.getTime());

        } catch (Exception e) {
            Log.error(e,"indexation failed with error : %s", e.getMessage());
            System.out.println("You failed to upload " + " => " + e.getStackTrace()) ;
        } finally {
            totalIndexationTime.stop();
            Log.info("total indexation time : %d",totalIndexationTime.getTime());
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
