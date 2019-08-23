package com.example.lucene.search.indexes;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.springframework.stereotype.Component;

@Component
public class LuceneFileIndex {

	private final StandardAnalyzer analyzer = new StandardAnalyzer();
	private Directory indexDirectory;
	
	public void setDirectory(Directory indexDirectory) {
		this.indexDirectory = indexDirectory;
	}

	public void addFileToIndex(String filepath) throws IOException {
		File file = new File(filepath);
		FileReader fileReader = new FileReader(file);
		
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);
		Document document = new Document();

		document.add(new TextField("contents", fileReader));
		document.add(new StringField("path", file.getPath(), Store.YES));
		document.add(new StringField("filename", file.getName(), Store.YES));

		indexWriter.addDocument(document);
		indexWriter.close();
	}

	public List<Document> searchFiles(String inField, String queryString) {
		try {
			Query query = new QueryParser(inField, analyzer).parse(queryString);
			IndexReader indexReader = DirectoryReader.open(indexDirectory);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			TopDocs topDocs = searcher.search(query, 10);
			List<Document> documents = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				documents.add(searcher.doc(scoreDoc.doc));
			}
			return documents;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return null;

	}

}
