package com.mbriot.indexer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ComputedPDF {

    private String brutText;
    private List<Mouvement> mouvements = new ArrayList<Mouvement>();

    public void process(String text) throws ParseException {
        List<String> intrestingPart = extractGoodPart(text);

        Iterator<String> stringIterator = intrestingPart.iterator();
        String line = "";
        Mouvement mouvement = new Mouvement();
        while (stringIterator.hasNext()){
            line = stringIterator.next();

            if(startWithDate(line)){
                if(mouvement.getBrutLine() != null){
                    mouvement.compute();
                    mouvements.add(mouvement);
                }
                mouvement = new Mouvement();
                mouvement.setBrutLine(line);
            } else {
                mouvement.setBrutLine(mouvement.getBrutLine() + " " + line);
            }
        }
        if(mouvement.getBrutLine() != null) {
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

    public ComputedPDF() {
        this.brutText = brutText;
    }

    public List<Mouvement> getMouvements() {
        return mouvements;
    }
}
