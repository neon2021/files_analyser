package com.example.demo.lucenesearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.AutoDetectParserConfig;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Utils {
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

    public static class FileIndexer {

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
                ParsedFileInfo parsedFileInfo = null;
                String text = "";
                if (StringUtils.equals(fileType, "docx")) {
                    // Extract text from Word document
                    parsedFileInfo = extractTextFromWord(file);
                    text = "metadata: " + getMetadataAsString(parsedFileInfo.getMetadata()) + "; content: "
                            + parsedFileInfo.getHandler();
                } else if (StringUtils.equals(fileType, "pdf")) {
                    // Extract text from PDF document
                    parsedFileInfo = extractTextFromPDF(file);
                    text = "metadata: " + getMetadataAsString(parsedFileInfo.getMetadata()) + "; content: "
                            + parsedFileInfo.getHandler();
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
        public static ParsedFileInfo extractTextFromWord(File file) throws IOException, TikaException, SAXException {
            // OfficeParser officeParser = new OfficeParser(); // exception: The supplied
            // data appears to be in the Office 2007+ XML. ...
            OOXMLParser officeParser = new OOXMLParser();
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            officeParser.parse(new FileInputStream(file), handler, metadata, new ParseContext());
            // return "metadata: " + getMetadataAsString(metadata) + "; content: " +
            // handler;

            return ParsedFileInfo.builder().metadata(metadata).handler(handler).build();
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
        public static ParsedFileInfo extractTextFromPDF(File file) throws IOException, TikaException, SAXException {
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
            return ParsedFileInfo.builder().metadata(metadata).handler(handler).build();
            // return "metadata: " + getMetadataAsString(metadata) + "; content: " +
            // handler;
        }

        public static Metadata extractMetaInfoFrom(File file) throws IOException, TikaException, SAXException {
            // If you do not want tesseract to be applied to your files see:
            // https://cwiki.apache.org/confluence/display/TIKA/TikaOCR#TikaOCR-disable-ocr
            // java.lang.IllegalArgumentException: Document contains at least one immense
            // term in field="content" (whose UTF8 encoding is longer than the max length
            // 32766), all of which were skipped. Please correct the analyzer to not produce
            // such terms.
            AutoDetectParserConfig config = new AutoDetectParserConfig();
            ParseContext context = new ParseContext();
            context.set(AutoDetectParserConfig.class, config);

            AutoDetectParser pdfParser = new AutoDetectParser();
            ContentHandler handler = new BodyContentHandler();
            handler = new DummyContentHandler();
            Metadata metadata = new Metadata();
            pdfParser.parse(new FileInputStream(file), // FIXME: bug: Caused by: org.apache.tika.exception.ZeroByteFileException: InputStream must have > 0 bytes
                    handler,
                    metadata,
                    context);
            return metadata;
//            return ParsedFileInfo.builder().metadata(metadata).handler(handler).build();
            // return "metadata: " + getMetadataAsString(metadata) + "; content: " +
            // handler;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParsedFileInfo {
        BodyContentHandler handler;
        Metadata metadata;
    }
}
