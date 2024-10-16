package com.example.demo.lucenesearch;

import static com.example.demo.lucenesearch.Utils.getMetadataAsString;
import static com.example.demo.lucenesearch.Utils.getRealPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
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
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

public class IndexBuilderTest {

    static String indexPath = "/tmp/index";

    public static void buildIndex(List<String> filePathList, String indexPath)
            throws Exception {
        try (Directory directory = FSDirectory.open(Paths.get(indexPath));) {
            // Index PDF or Word document
            for (String filePath : filePathList) {
                FileIndexer.createIndex(filePath, directory);
            }

            // Optimize index
            IndexReader reader = DirectoryReader.open(directory);
            // reader.optimize();
            reader.close();
        }
    }

    @Test
    public void testScanFilesInOneFolder() {
        Collection<File> listFiles = FileUtils.listFiles(new File(Utils.class.getResource(".").getPath()), null, false);
        listFiles.forEach(file -> System.out.println(file.getAbsolutePath()));
    }

    @Test
    public void testBuidingAndSearchWithIndex() {
        for (int i = 0; i < 3; i++) {
            try {
                testBuidingIndex();
                searchWithLuceneIndex();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileUtils.deleteDirectory(new File(indexPath));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void testBuidingIndex() {
        List<String> filePathList = Lists.newArrayList(getRealPath("apple-products-cn.pdf"),
                getRealPath("apple-products-en.pdf"), getRealPath("beiji-baike.docx"),
                getRealPath("north-pole-wiki.docx"),
                getRealPath("北极-百科.docx") // error encoding for this file name
        );
        // String indexPath = "/path/to/index";
        System.out.println("wrong way");
        filePathList.forEach(file -> System.out.println(file));

        System.out.println("correct way");
        filePathList = FileUtils
                .listFiles(new File(Utils.class.getResource(".").getPath()),
                        new String[] { "pdf", "docx" },
                        false)
                .stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
        filePathList.forEach(file -> System.out.println(file));

        try {
            buildIndex(filePathList, indexPath);
            System.out.println("Index built successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * sourced from : Lucene Tutorial: Getting Started (with Examples) –
     * HowToDoInJava https://howtodoinjava.com/lucene/lucene-index-search-examples/
     *
     * @throws IOException
     */

    public void searchWithLuceneIndex() throws IOException {
        searchWithKeyword("ipad");
        searchWithKeyword("北极");
    }

    private void searchWithKeyword(String keyword) {
        try {
            // Open the directory
            FSDirectory directory = FSDirectory.open(Paths.get(indexPath));

            // Create an IndexReader
            DirectoryReader directoryReader = DirectoryReader.open(directory);

            // Create an IndexSearcher
            IndexSearcher searcher = new IndexSearcher(directoryReader);

            // Parse query
            QueryParser parser = new QueryParser("content", new StandardAnalyzer());
            // QueryParser parser = new QueryParser("content", new EnglishAnalyzer());
            // Query query = parser.parse("ipad");
            Query query = parser.parse(keyword);

            // Perform search
            TopDocs topDocs = searcher.search(query, 10); // Search for top 10 results

            ScoreDoc[] hits = topDocs.scoreDocs;

            System.out.println("Found " + topDocs.totalHits.value + " documents.");

            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document doc = searcher.doc(docId);
                String filename = doc.get("filename");
                String content = doc.get("content");
                System.out.println("docId:" + docId + ", filename: " + filename + ", content: "
                        + StringUtils.substring(content, 0, 20));
            }

            // Close document
            directoryReader.close();
            directory.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Utils {
    public static String getRealPath(String relativePath) {
        return Utils.class.getResource(relativePath).getPath();
    }

    public static String getMetadataAsString(Metadata metadata) {
        String metadataStr = Arrays.stream(metadata.names()).map(name -> name + ":" + metadata.get(name))
                .collect(Collectors.joining(","));
        // System.out.println("metadataStr: " + metadataStr);
        return metadataStr;
    }

    public static String getFileMD5(File file) {
        try (FileInputStream inputStream = new FileInputStream(file);) {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] fileBytes = IOUtils.toByteArray(inputStream);
            messageDigest.update(fileBytes);
            return DigestUtils.md2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

class FileIndexer {

    public static void createIndex(String filePath, Directory directory)
            throws IOException, TikaException, SAXException {
        File file = new File(filePath);
        Document doc = new Document();

        // Add metadata
        String fileName = file.getName();
        System.out.println("building index for file: " + fileName);
        String fileType = null;
        if (StringUtils.endsWith(fileName, "docx")) {
            fileType = "docx";
        } else if (StringUtils.endsWith(fileName, "pdf")) {
            fileType = "pdf";
        }
        String fileMD5 = Utils.getFileMD5(file);
        doc.add(new StringField("id", fileMD5, Store.YES));
        doc.add(new StringField("filename", fileName, Store.YES));

        try {
            String text = "";
            if (StringUtils.equals(fileType, "docx")) {
                // Extract text from Word document
                text = extractTextFromWord(file);
            } else if (StringUtils.equals(fileType, "pdf")) {
                // Extract text from PDF document
                text = extractTextFromPDF(file);
            }

            // Add analyzed text to document
            doc.add(new TextField("content", text, Store.YES));

            try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()))) {
                writer.deleteDocuments(new Term("id", fileMD5));
                writer.flush();
                writer.commit();

                writer.addDocument(doc);
            }
        } catch (Exception e) {
            System.out.printf("ignore %s file: %s", fileType, filePath);
            e.printStackTrace();
        }
    }

    /**
     * sourced from : TIKA - Extracting MS-Office Files
     * https://www.tutorialspoint.com/tika/tika_extracting_ms_office_files.htm
     *
     * @param file
     * @return
     * @throws IOException
     * @throws TikaException
     * @throws SAXException
     */
    private static String extractTextFromWord(File file) throws IOException, TikaException, SAXException {
        // OfficeParser officeParser = new OfficeParser(); // exception: The supplied
        // data appears to be in the Office 2007+ XML. ...
        OOXMLParser officeParser = new OOXMLParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        officeParser.parse(new FileInputStream(file), handler, metadata, new ParseContext());
        return "metadata: " + getMetadataAsString(metadata) + "; content: " + handler;
    }

    /**
     * sourced from TIKA - Extracting PDF
     * https://www.tutorialspoint.com/tika/tika_extracting_pdf.htm
     *
     * @param file
     * @return
     * @throws IOException
     * @throws TikaException
     * @throws SAXException
     */
    private static String extractTextFromPDF(File file) throws IOException, TikaException, SAXException {
        // If you do not want tesseract to be applied to your files see:
        // https://cwiki.apache.org/confluence/display/TIKA/TikaOCR#TikaOCR-disable-ocr
        // java.lang.IllegalArgumentException: Document contains at least one immense
        // term in field="content" (whose UTF8 encoding is longer than the max length
        // 32766), all of which were skipped. Please correct the analyzer to not produce
        // such terms.
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
