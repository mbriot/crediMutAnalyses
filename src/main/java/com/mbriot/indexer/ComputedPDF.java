package com.mbriot.indexer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by manu on 28/07/2015.
 */
public class ComputedPDF {

    private String brutText;
    private List<Mouvement> mouvements = new ArrayList<Mouvement>();

    public ComputedPDF() {
        this.brutText = brutText;
    }

    public List<Mouvement> getMouvements() {
        return mouvements;
    }

    public void process(String text) throws ParseException {
        List<String> intrestingPart = extractGoodPart(text);

        Iterator<String> stringIterator = intrestingPart.iterator();
        String actualLine = "";
        String nextLine = "";
        while (stringIterator.hasNext()){
            Mouvement mouvement = new Mouvement();

            if(nextLine.isEmpty()){
                actualLine = stringIterator.next();
            } else {
                actualLine = nextLine;
            }

            if(stringIterator.hasNext()){
                nextLine = stringIterator.next();
            }

            if(!startWithDate(nextLine)){
                mouvement.setBrutLine(actualLine + " " + nextLine);
                nextLine = "";
            } else{
                mouvement.setBrutLine(actualLine);
            }

            mouvement.compute();
            mouvements.add(mouvement);
        }
    }

    private static List<String> extractGoodPart(String text) {
        List<String> extractedPart = new ArrayList<String>();

        boolean intrestingPart = false;
        for(String line : text.split("\n")){

            if(line.contains("LIVRET BLEU N° 00041500304 en euros")){
                break;
            }
            if(line.contains("Date Date valeur Opération Débit euros Crédit euros")){
                intrestingPart = false;
            }

            if(intrestingPart){
                extractedPart.add(line);
            }

            if(line.contains("TITULAIRE(S) : M MANUEL BRIOT")){
                intrestingPart = true;
            }
        }
        return extractedPart;
    }

    private static boolean startWithDate(String line) {
        String[] splittedLine = line.split("/");
        if(splittedLine.length > 1){
            try{
                Integer.parseInt(splittedLine[0]);
                Integer.parseInt(splittedLine[1]);
                return true;
            } catch (NumberFormatException nfe){
                return false;
            }
        }
        return false;
    }
}
