package com.mbriot.controller;

import com.mbriot.indexer.Mouvement;
import com.mbriot.lucene.Searcher;
import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Controller
public class SearchController {

    @Autowired
    Searcher searcher;

    @RequestMapping(value="/search", method= RequestMethod.GET)
    public @ResponseBody
    List<Mouvement> searchDocs(@RequestParam("query") String query) throws IOException {
        List<Mouvement> mouvements= searcher.search(query);
        return mouvements;
    }
}
