/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucebe_bd_1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.pdfbox.cos.COSDocument;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

/**
 *
 * @author Patricio Jaramillo
 */
public class LucenePDF {

    //Archivo donde buscar
    public static final String FILES_TO_INDEX_DIRECTORY = "C:\\tmpPDF";
    public static final String INDEX_DIRECTORY = "indexDirectory";

    public static final String FIELD_PATH = "C:\\tmpPDF";
    public static final String FIELD_CONTENTS = "contents";

    public static void main(String[] args) throws Exception {

        createIndex();
        searchIndex("APA");
        searchIndex("Bertrand");
        searchIndex("aplicaciones");
        searchIndex("Russell");
    }

    public static void createIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
        Analyzer analyzer = new StandardAnalyzer();
        boolean recreateIndexIfExists = true;
        IndexWriter indexWriter = new IndexWriter(INDEX_DIRECTORY, analyzer, recreateIndexIfExists);
        File dir = new File(FILES_TO_INDEX_DIRECTORY);
        File[] files = dir.listFiles();
        System.out.println(files);
        for (File file : files) {

            Document document = new Document();

            String path = file.getCanonicalPath();
            document.add(new Field(FIELD_PATH, path, Field.Store.YES, Field.Index.UN_TOKENIZED));
            //System.out.println(path);
            /*Reader reader = new FileReader(file);
            document.add(new Field(FIELD_CONTENTS, reader));*/
            //System.out.println(reader);
            /**/
            FileInputStream fi = new FileInputStream(file);
             PDFParser parser = new PDFParser(fi);
             parser.parse();
             COSDocument cd = parser.getDocument();
             PDFTextStripper stripper = new PDFTextStripper();
             String text="";
             try {
             text = stripper.getText(new PDDocument(cd));
             } finally {
             cd.close();
             }
            document.add(new Field(FIELD_CONTENTS, text, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
            //document.add(new Field("C:\\tmpPDF\\RussellKnowAcquaint.pdf", text, Field.Store.YES, Field.Index.UN_TOKENIZED));
            //System.out.println(text);
            /**/

            indexWriter.addDocument(document);
        }
        indexWriter.optimize();
        indexWriter.close();
    }

    public static void searchIndex(String searchString) throws IOException, ParseException {
        System.out.println("Searching for '" + searchString + "'");
        Directory directory = FSDirectory.getDirectory(INDEX_DIRECTORY);
        IndexReader indexReader = IndexReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Analyzer analyzer = new StandardAnalyzer();
        QueryParser queryParser = new QueryParser(FIELD_CONTENTS, analyzer);
        Query query = queryParser.parse(searchString);
        Hits hits = indexSearcher.search(query);
        System.out.println("Number of hits: " + hits.length());

        Iterator<Hit> it = hits.iterator();
        while (it.hasNext()) {
            Hit hit = it.next();
            Document document = hit.getDocument();
            String path = document.get(FIELD_PATH);
            System.out.println("Hit: " + path);
        }

    }

}
