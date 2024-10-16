package com.example.demo.lucenesearch;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.junit.jupiter.api.Test;

/**
 * sourced from : Lucene Tutorial: Getting Started (with Examples) –
 * HowToDoInJava
 * https://howtodoinjava.com/lucene/lucene-index-search-examples/
 *
 * @author neon2021 on 2024/10/16
 */
public class UseLuceneInMemoryTest {

    @Test
    public void test() throws IOException, ParseException {
        ByteBuffersDirectory directory = new ByteBuffersDirectory();
        buildIndexAndSearchInIt(directory);
        rebuildIndexAndSearchInIt(directory);
    }

    /**
     * sourced from :
     * https://github.com/elastic/elasticsearch/blob/main/server/src/test/java/org/elasticsearch/common/lucene/LuceneTests.java
     * 
     * @param directory
     * @throws IOException
     */
    private void rebuildIndexAndSearchInIt(ByteBuffersDirectory directory) throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);
        Term deleteTerm = new Term("id", "1");
        indexWriter.deleteDocuments(deleteTerm);
        indexWriter.commit();
        indexWriter.close();

        DirectoryReader indexReader = DirectoryReader.open(directory);
        outputInfoAboutDocNum(indexReader);
        indexReader.close();
    }

    private void outputInfoAboutDocNum(DirectoryReader indexReader) {
        System.out.printf("indexReader.numDocs()=%s,indexReader.numDeletedDocs()=%s,indexReader.maxDoc()=%s\n\n",
                indexReader.numDocs(), indexReader.numDeletedDocs(), indexReader.maxDoc());
    }

    private void buildIndexAndSearchInIt(ByteBuffersDirectory directory) throws IOException, ParseException {
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        // Create and add documents
        Document doc1 = new Document();
        doc1.add(new StringField("id", "1", Field.Store.YES));
        doc1.add(new TextField("content", "Hello World from Apache Lucene", Field.Store.YES));
        indexWriter.addDocument(doc1);

        Document doc2 = new Document();
        doc2.add(new StringField("id", "2", Field.Store.YES));
        doc2.add(new TextField("content", "Lucene is a powerful search library", Field.Store.YES));
        indexWriter.addDocument(doc2);

        indexWriter.close();

        String searchText = "Lucene";

        // Create an IndexSearcher
        DirectoryReader directoryReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

        // Parse a query
        QueryParser queryParser = new QueryParser("content", analyzer);
        Query query = queryParser.parse(searchText);

        // Search the index
        TopDocs topDocs = indexSearcher.search(query, 10);

        // Display the results
        System.out.println("Found " + topDocs.totalHits.value + " hits.");
        for (int i = 0; i < topDocs.scoreDocs.length; i++) {
            int docId = topDocs.scoreDocs[i].doc;
            Document doc = indexSearcher.doc(docId);
            String id = doc.get("id");
            String content = doc.get("content");
            System.out.println("Doc ID: " + id);
            System.out.println("Content: " + content);
        }

        outputInfoAboutDocNum(directoryReader);
        directoryReader.close();
        // directory.close();
    }
}
