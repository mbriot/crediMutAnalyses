package com.mbriot.lucene;


import com.mbriot.indexer.Mouvement;
import com.mbriot.utils.FieldName;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Indexer {

    private String pathToIndex;

    public void indexMouvement(List<Mouvement> mouvements) throws IOException {

        IndexWriter writer = null;
        try{
            Directory dir = FSDirectory.open(Paths.get(pathToIndex));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            writer = new IndexWriter(dir, iwc);
            for(Mouvement mouvement : mouvements){
                indexDocs(writer, mouvement);
            }
        } catch (IOException io){
            throw new IOException(io.getMessage(),io.getCause());
        }finally {
            writer.close();
        }
    }

    public void setPathToIndex(String pathToIndex) {
        this.pathToIndex = pathToIndex;
    }

    private void indexDocs(IndexWriter writer, Mouvement mouvement) throws IOException {

        Document doc = new Document();
        FieldType fieldType = new FieldType();
        fieldType.setIndexOptions(IndexOptions.DOCS);
        fieldType.setStored(true);


        if(mouvement.getMouvementType() != null){
            Field mvtType = new Field(FieldName.TYPE,parse(mouvement.getMouvementType()),fieldType);
            doc.add(mvtType);

            if(mouvement.getMouvementDescription() != null){
                String description = mouvement.getMouvementType() + " " + mouvement.getMouvementDescription();
                Field mvtDesc = new Field(FieldName.DESCRIPTION,description,fieldType);
                doc.add(mvtDesc);
            }
        }
        if(mouvement.getDate() != null){
            Field date = new Field(FieldName.DATE,mouvement.getDate(),fieldType);
            doc.add(date);
        }
        if(mouvement.getMontant() > 0 || mouvement.getMontant() < 0){
            Field montant = new Field(FieldName.MONTANT,String.valueOf(mouvement.getMontant()),fieldType);
            doc.add(montant);
        }

        writer.addDocument(doc);
    }

    private String parse(String mouvementType) {
        if(mouvementType.contains("PAIEMENT PSC")) return "paiement_cb_sans_contact";
        if(mouvementType.contains("PAIEMENT CB")) return "paiement_cb";
        if(mouvementType.contains("RETRAIT DAB")) return "retrait";
        if(mouvementType.contains("PRLV")) return "prelevement";
        if(mouvementType.contains("VIR")) return "virement";
        return null;
    }
}
