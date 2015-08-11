package com.mbriot.configuration;

import com.mbriot.lucene.Indexer;
import com.mbriot.lucene.Searcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("application.properties")
public class LuceneConfiguration {

    @Autowired
    Environment env;

    @Bean
    public Indexer indexer() {
        Indexer indexer = new Indexer();
        indexer.setPathToIndex(env.getProperty("path.to.index"));
        return indexer;
    }

    @Bean
    public Searcher searcher() {
        Searcher searcher = new Searcher();
        searcher.setPathToIndex(env.getProperty("path.to.index"));
        return searcher;
    }

}
