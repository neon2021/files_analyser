package com.neon.file.analyser.lucenesearch;

import static com.neon.file.analyser.lucenesearch.Utils.getRealPath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

public class IndexBuilderTest {

    static String indexPath = "/tmp/index";

    public static void buildIndex(List<String> filePathList, String indexPath)
            throws Exception {
        try (Directory directory = FSDirectory.open(Paths.get(indexPath));) {
            // Index PDF or Word document
            for (String filePath : filePathList) {
                Utils.FileInfoIndexer.createIndex(filePath, directory);
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

