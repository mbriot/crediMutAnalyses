package com.mbriot.pojo;

import com.mbriot.indexer.Mouvement;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {

    private List<Mouvement> mouvements =  new ArrayList<Mouvement>();
    private int totalHits;
    private long searchTime;
    private double totalAmount;

    public SearchResponse() {
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setMouvements(List<Mouvement> mouvements) {
        this.mouvements = mouvements;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
    }

    public void setSearchTime(long searchTime) {
        this.searchTime = searchTime;
    }

    public List<Mouvement> getMouvements() {
        return mouvements;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public long getSearchTime() {
        return searchTime;
    }
}
