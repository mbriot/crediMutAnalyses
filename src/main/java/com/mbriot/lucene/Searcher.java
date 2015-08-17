package com.mbriot.lucene;

import com.mbriot.indexer.Mouvement;
import com.mbriot.pojo.SearchResponse;
import com.mbriot.utils.FieldName;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.thymeleaf.expression.Sets;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Searcher {

    private String pathToIndex;

    public SearchResponse search(String query) throws IOException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(pathToIndex)));
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(FieldName.DESCRIPTION,new StandardAnalyzer());
        Query parsedQuery  = null;
        try {
            parsedQuery  = parser.parse(query);
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            e.printStackTrace();
        }

        if(parsedQuery instanceof TermQuery){
            parsedQuery = rewriteTermQuery(parsedQuery);
        } else if(parsedQuery instanceof TermRangeQuery){
            parsedQuery = rewritTermRangeQuery(parsedQuery);

        } else if(parsedQuery instanceof BooleanQuery){
            for(BooleanClause clause : ((BooleanQuery) parsedQuery).getClauses()){
                if(clause.getQuery() instanceof TermQuery){
                    Query q = rewriteTermQuery(clause.getQuery());
                    clause.setQuery(q);
                } else if(clause.getQuery() instanceof TermRangeQuery){
                    Query q = rewritTermRangeQuery(clause.getQuery());
                    clause.setQuery(q);
                }
            }
        }

        double totalAmount = 0.0;
        TopDocs hits = searcher.search(parsedQuery, 10000);
        List<Mouvement> mouvements = new ArrayList<Mouvement>();
        for(ScoreDoc scoreDoc : hits.scoreDocs){
            Document doc = searcher.doc(scoreDoc.doc);
            Mouvement mouvement = Mouvement.convert(doc);
            totalAmount = totalAmount + mouvement.getMontant();
            mouvements.add(mouvement);
        }

        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setMouvements(mouvements);
        searchResponse.setTotalHits(hits.totalHits);
        searchResponse.setTotalAmount(Math.floor(totalAmount * 100) / 100);

        return searchResponse;
    }

    private Query rewriteTermQuery(Query q) {
        TermQuery tq = (TermQuery) q;
        if(tq.getTerm().field().equals(FieldName.MONTANT)){
            return NumericRangeQuery.newDoubleRange(FieldName.MONTANT,
                    Double.parseDouble(tq.getTerm().text()),
                    Double.parseDouble(tq.getTerm().text()),
                    true, true);
        }
        return tq;
    }

    private Query rewritTermRangeQuery(Query query) {
        TermRangeQuery trq = (TermRangeQuery) query;
        if(trq.getField().equals(FieldName.MONTANT)){
            String lowerTerm = trq.getLowerTerm().utf8ToString();
            String upperTerm = trq.getUpperTerm().utf8ToString();
            return NumericRangeQuery.newDoubleRange(FieldName.MONTANT,
                    Double.parseDouble(lowerTerm),
                    Double.parseDouble(upperTerm),
                    true, true);
        }
        return query;
    }

    public void setPathToIndex(String pathToIndex) {
        this.pathToIndex = pathToIndex;
    }
}
