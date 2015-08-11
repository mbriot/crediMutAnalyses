package com.mbriot.lucene;

import com.mbriot.indexer.Mouvement;
import com.mbriot.utils.FieldName;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Searcher {

    private String pathToIndex;

    public List<Mouvement> search(String query) throws IOException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(pathToIndex)));
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(FieldName.DESCRIPTION,new StandardAnalyzer());
        Query parsedQuery  = null;
        try {
            parsedQuery  = parser.parse(query);
        } catch (org.apache.lucene.queryparser.classic.ParseException e) {
            e.printStackTrace();
        }

        TopDocs hits = searcher.search(parsedQuery, 10000);
        List<Mouvement> mouvements = new ArrayList<Mouvement>();
        for(ScoreDoc scoreDoc : hits.scoreDocs){
            Document doc = searcher.doc(scoreDoc.doc);
            Mouvement mouvement = Mouvement.convert(doc);
            mouvements.add(mouvement);
        }
        return mouvements;
    }

    public void setPathToIndex(String pathToIndex) {
        this.pathToIndex = pathToIndex;
    }
}
