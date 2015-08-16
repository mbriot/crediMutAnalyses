package com.mbriot.indexer;

import com.mbriot.utils.FieldName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexableField;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.math.NumberUtils.isNumber;

public class Mouvement {
    private String brutLine;
    private String date;
    private String mouvementDescription;
    private String mouvementType;
    private boolean debit;
    private boolean credit;
    private double montant = 0;
    private String description;

    public void compute() throws ParseException {
        String[] splittedMouvement = brutLine.split("\\s+");

        this.setDate(format(splittedMouvement[0]));

        String operation = "";
        int montantIndex = 0;
        for(int i = 2;i<splittedMouvement.length;i++){
            String[] currentValueSplitted = splittedMouvement[i].split(",");
            if(currentValueSplitted.length == 2 &&
                    isNumber(currentValueSplitted[0]) && isNumber(currentValueSplitted[1])) {
                this.setMontant(
                        Double.valueOf(currentValueSplitted[0].replaceAll("\\.","") + "." + currentValueSplitted[1]));
                this.setMouvementType(operation);
                montantIndex = i;
                break;
            }
            operation += " " + splittedMouvement[i];
        }

        if(splittedMouvement.length > montantIndex -1){
            String mouvementDescription = "";
            for(int i = montantIndex + 1;i<splittedMouvement.length;i++){
                mouvementDescription += " " + splittedMouvement[i];
            }
            this.setMouvementDescription(operation + " " + mouvementDescription);
        }

    }

    private String format(String s) {
        String[] datePart = s.split("/");
        return datePart[2] + datePart[1] + datePart[0];
    }

    public static Mouvement convert(Document doc){
        Mouvement mouvement = new Mouvement();
        for(IndexableField field : doc.getFields()){
            if(field.name().equals(FieldName.TYPE)){
                mouvement.setMouvementType(field.stringValue());
            }
            if(field.name().equals(FieldName.DATE)){
                mouvement.setDate(field.stringValue());
            }
            if(field.name().equals(FieldName.MONTANT)){
                mouvement.setMontant(Double.valueOf(field.stringValue()));
            }
            if(field.name().equals(FieldName.DESCRIPTION)){
                mouvement.setMouvementDescription(field.stringValue());
            }

        }
        return mouvement;
    }


    public Mouvement() {
    }

    public void setBrutLine(String brutLine) {
        this.brutLine = brutLine;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMouvementDescription(String mouvementDescription) {
        this.mouvementDescription = mouvementDescription;
    }

    public void setMouvementType(String mouvementType) {
        this.mouvementType = mouvementType;
    }

    public void setDebit(boolean debit) {
        this.debit = debit;
    }

    public void setCredit(boolean credit) {
        this.credit = credit;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrutLine() {
        return brutLine;
    }

    public String getDate() {
        return date;
    }

    public String getMouvementDescription() {
        return mouvementDescription;
    }

    public String getMouvementType() {
        return mouvementType;
    }

    public boolean isDebit() {
        return debit;
    }

    public boolean isCredit() {
        return credit;
    }

    public double getMontant() {
        return montant;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Mouvement{" +
                "date=" + date +
                ", mouvementType='" + mouvementType + '\'' +
                ", montant=" + montant + '\'' +
                ", mouvementDescription=" + mouvementDescription +
                '}';
    }
}
