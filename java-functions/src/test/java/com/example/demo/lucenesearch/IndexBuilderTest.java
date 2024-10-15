package com.example.demo.lucenesearch;

import com.google.common.collect.Lists;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.lucenesearch.Utils.getMetadataAsString;

public class IndexBuilderTest {

    static String indexPath = "/tmp/index";

    public static void buildIndex(List<String> pdfPathList, List<String> wordPathList, String indexPath) throws Exception {
        Directory directory = FSDirectory.open(Paths.get(indexPath));

        // Index PDF
        for (String pdfPath : pdfPathList) {
            PDFIndexer.indexPDF(pdfPath, directory);
        }

        // Index Word document
        for (String wordPath : wordPathList) {
            WordIndexer.indexWord(wordPath, directory);
        }

        // Optimize index
        IndexReader reader = DirectoryReader.open(directory);
//        reader.optimize();
        reader.close();
    }

    @Test
    public void test_build_index() {
        List<String> pdfPath = Lists.newArrayList(getRealPath("apple-products-cn.pdf"), getRealPath("apple-products-en.pdf"));
        List<String> wordPath = Lists.newArrayList(getRealPath("beiji-baike.docx"), getRealPath("north-pole-wiki.docx"));
//        String indexPath = "/path/to/index";

        try {
            buildIndex(pdfPath, wordPath, indexPath);
            System.out.println("Index built successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRealPath(String relativePath) {
        return IndexBuilderTest.class.getResource(relativePath).getPath();
    }

    /**
     * sourced from : Lucene Tutorial: Getting Started (with Examples) – HowToDoInJava https://howtodoinjava.com/lucene/lucene-index-search-examples/
     *
     * @throws IOException
     */
    @Test
    public void searchWithLuceneIndexFiles() throws IOException {

        try {
            // Open the directory
            FSDirectory directory = FSDirectory.open(Paths.get(indexPath));

            // Create an IndexReader
            DirectoryReader directoryReader = DirectoryReader.open(directory);

            // Create an IndexSearcher
            IndexSearcher searcher = new IndexSearcher(directoryReader);

            // Parse query
//            QueryParser parser = new QueryParser("content", new StandardAnalyzer());
            QueryParser parser = new QueryParser("content", new EnglishAnalyzer());
//            Query query = parser.parse("ipad");
            Query query = parser.parse("iPad");

            // Perform search
            TopDocs topDocs = searcher.search(query, 10); // Search for top 10 results

            ScoreDoc[] hits = topDocs.scoreDocs;

            System.out.println("Found " + topDocs.totalHits.value + " documents.");

            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document doc = searcher.doc(docId);
                String content = doc.get("content");
                System.out.println(content);
            }

            // Close document
            directoryReader.close();
            directory.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class PDFIndexer {

    public static void indexPDF(String pdfPath, Directory directory) throws IOException, SAXException, TikaException {
        File file = new File(pdfPath);
        Document doc = new Document();

        // Add metadata
        String fileName = file.getName();
        doc.add(new StringField("filename", fileName, Store.YES));

        // Extract text from PDF
        try {
            String text = extractTextFromPDF(file);

            // Add analyzed text to document
            doc.add(new StringField("content", text, Store.YES));

            try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()))) {
                writer.addDocument(doc);
            }
        } catch (Exception e) {
            System.out.println("ignore pdf file: " + pdfPath);
            e.printStackTrace();
        }

    }

    /**
     * sourced from TIKA - Extracting PDF https://www.tutorialspoint.com/tika/tika_extracting_pdf.htm
     *
     * @param file
     * @return
     * @throws IOException
     * @throws TikaException
     * @throws SAXException
     */
    private static String extractTextFromPDF(File file) throws IOException, TikaException, SAXException {
        // If you do not want tesseract to be applied to your files see: https://cwiki.apache.org/confluence/display/TIKA/TikaOCR#TikaOCR-disable-ocr
        //java.lang.IllegalArgumentException: Document contains at least one immense term in field="content" (whose UTF8 encoding is longer than the max length 32766), all of which were skipped.  Please correct the analyzer to not produce such terms.
        TesseractOCRConfig config = new TesseractOCRConfig();
        config.setSkipOcr(true);
        ParseContext context = new ParseContext();
        context.set(TesseractOCRConfig.class, config);

        PDFParser pdfParser = new PDFParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        pdfParser.parse(new FileInputStream(file),
                handler,
                metadata,
                context);
        return "metadata: " + getMetadataAsString(metadata) + "; content: " + handler;
    }
}

class Utils {
    public static String getMetadataAsString(Metadata metadata) {
        return Arrays.stream(metadata.names()).map(name -> name + ":" + metadata.get(name)).collect(Collectors.joining(","));
    }
}

class WordIndexer {

    public static void indexWord(String wordPath, Directory directory) throws IOException, TikaException, SAXException {
        File file = new File(wordPath);
        Document doc = new Document();

        // Add metadata
        String fileName = file.getName();
        doc.add(new StringField("filename", fileName, Store.YES));

        try {
            // Extract text from Word document
            String text = extractTextFromWord(file);

            // Add analyzed text to document
            doc.add(new StringField("content", text, Store.YES));

            try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()))) {
                writer.addDocument(doc);
            }
        } catch (Exception e) {
            System.out.println("ignore office file: " + wordPath);
            e.printStackTrace();
        }
    }

    /**
     * sourced from : TIKA - Extracting MS-Office Files https://www.tutorialspoint.com/tika/tika_extracting_ms_office_files.htm
     *
     * @param file
     * @return
     * @throws IOException
     * @throws TikaException
     * @throws SAXException
     */
    private static String extractTextFromWord(File file) throws IOException, TikaException, SAXException {
//        OfficeParser officeParser = new OfficeParser(); // exception: The supplied data appears to be in the Office 2007+ XML. ...
        OOXMLParser officeParser = new OOXMLParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        officeParser.parse(new FileInputStream(file), handler, metadata, new ParseContext());
        return "metadata: " + getMetadataAsString(metadata) + "; content: " + handler;
    }
}
