/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hsmannheim.ss15.alr.searchengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StoredDocument;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Herbe_000
 */
public class DefaultLuceneController extends LuceneController {

//---------------------------------------------------------object attributes---------------------------------------------------------
//---------------------------------------------------------constructors---------------------------------------------------------
    public DefaultLuceneController(String indexDir, String docsDir) {
        this.indexDir = indexDir;
        this.docsDir = docsDir;
        LOGGER = LoggerFactory.getLogger(DefaultLuceneController.class);
        LOGGER.info("created");
    }

//---------------------------------------------------------public methods---------------------------------------------------------
    public void refreshIndex() {
        boolean create = true;
        final Path docDir = Paths.get(docsDir);
        IndexWriter writer = null;
        try {

            Directory dir = FSDirectory.open(Paths.get(indexDir));
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

            // Add new documents to an existing index:
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            // Optional: for better indexing performance, if you
            // are indexing many documents, increase the RAM
            // buffer.  But if you do this, increase the max heap
            // size to the JVM (eg add -Xmx512m or -Xmx1g):
            //
            // iwc.setRAMBufferSizeMB(256.0);
            writer = new IndexWriter(dir, iwc);
            indexDocs(writer, docDir);

            // NOTE: if you want to maximize search performance,
            // you can optionally call forceMerge here.  This can be
            // a terribly costly operation, so generally it's only
            // worth it when your index is relatively static (ie
            // you're done adding documents to it):
            //
            // writer.forceMerge(1);
            writer.close();

        } catch (IOException e) {
            LOGGER.warn("Exception while indexing", e);

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    java.util.logging.Logger.getLogger(DefaultLuceneController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public List<StoredDocument> doSearch(String queryString) throws IOException, ParseException {
        String field = "contents";
        String queries = null;
        boolean raw = false;
        int hitsPerPage = 10;

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();

        BufferedReader in = null;
        if (queries != null) {
            in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
        } else {
            in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        }
        QueryParser parser = new QueryParser(field, analyzer);

        Query query = parser.parse(queryString);

        Highlighter highlighter = new Highlighter(new QueryScorer(query));

        TotalHitCountCollector collector = new TotalHitCountCollector();
        searcher.search(query, collector);
        TopDocs topDocs = searcher.search(query, Math.max(1, collector.getTotalHits()));

        List<StoredDocument> results = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            StoredDocument doc = searcher.doc(scoreDoc.doc);
            try {
                File file = new File(doc.get("path"));
                BufferedReader docReader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8));

                List<String> lines = new ArrayList<>();
                while (docReader.ready()) {
                    lines.add(docReader.readLine());
                }
                lines.remove(0);
                lines.remove(0);
                lines.remove(0);

                String content = "";

                for (String s : lines) {
                    content = content + s;
                }
                String highLight = highlighter.getBestFragment(analyzer, null, content);
                if (highLight == null) {
                    LOGGER.warn("No Highlight found");
                } else {
                    doc.add(new TextField("highlight", highLight, Field.Store.YES));
                }
            } catch (InvalidTokenOffsetsException ex) {
                LOGGER.warn("No Highlight found");
            }

            results.add(doc);
        }

        reader.close();
        return results;

    }

//---------------------------------------------------------private methods---------------------------------------------------------
    /**
     * Indexes the given file using the given writer, or if a directory is
     * given, recurses over files and directories found under the given
     * directory.
     *
     * NOTE: This method indexes one document per input file. This is slow. For
     * good throughput, put multiple documents into your input file(s). An
     * example of this is in the benchmark module, which can create "line doc"
     * files, one document per line, using the
     * <a href="../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
     * >WriteLineDocTask</a>.
     *
     * @param writer Writer to the index where the given file/dir info will be
     * stored
     * @param path The file to index, or the directory to recurse into to find
     * files to index
     * @throws IOException If there is a low-level I/O error
     */
    static void indexDocs(final IndexWriter writer, Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    } catch (IOException ignore) {
                        // don't index files that can't be read.
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }
    }

    /**
     * Indexes a single document
     */
    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
        try (InputStream stream = Files.newInputStream(file)) {
            // make a new, empty document
            org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();

            // Add the path of the file as a field named "path".  Use a
            // field that is indexed (i.e. searchable), but don't tokenize 
            // the field into separate words and don't index term frequency
            // or positional information:
            Field pathField = new StringField("path", file.toString(), Field.Store.YES);
            doc.add(pathField);

            // Add the last modified date of the file a field named "modified".
            // Use a LongField that is indexed (i.e. efficiently filterable with
            // NumericRangeFilter).  This indexes to milli-second resolution, which
            // is often too fine.  You could instead create a number based on
            // year/month/day/hour/minutes/seconds, down the resolution you require.
            // For example the long value 2011021714 would mean
            // February 17, 2011, 2-3 PM.
            doc.add(new LongField("modified", lastModified, Field.Store.NO));

            // Add the contents of the file to a field named "contents".  Specify a Reader,
            // so that the text of the file is tokenized and indexed, but not stored.
            // Note that FileReader expects the file to be in UTF-8 encoding.
            // If that's not the case searching for special characters will fail.
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

            List<String> lines = new ArrayList<>();
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
            if (lines.size() > 0) {
                String urlLine = lines.remove(0);
                if (urlLine != null && urlLine.startsWith("URL:")) {
                    urlLine = urlLine.substring(4);
                    doc.add(new TextField("URL", urlLine, Field.Store.YES));
                }
            }
            if (lines.size() > 0) {
                String dataType = lines.remove(0);
                if (dataType != null && dataType.startsWith("DataType:")) {
                    dataType = dataType.substring(9);
                    doc.add(new TextField("DataType", dataType, Field.Store.YES));
                }
            }
            if (lines.size() > 0) {
                String title = lines.remove(0);
                if (title != null && title.startsWith("Title:")) {
                    title = title.substring(6);
                    doc.add(new TextField("title", title, Field.Store.YES));
                }
            }
            String content = "";
            for (String s : lines) {
                content = content + s;
            }
             doc.add(new TextField("contents", content, Field.Store.NO));

            if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                writer.addDocument(doc);
            } else {
                // Existing index (an old copy of this document may have been indexed) so 
                // we use updateDocument instead to replace the old one matching the exact 
                // path, if present:
                writer.updateDocument(new Term("path", file.toString()), doc);
            }
        }
    }
}
