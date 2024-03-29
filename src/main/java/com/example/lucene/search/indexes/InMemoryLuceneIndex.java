package com.example.lucene.search.indexes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.SortedDocValuesField;
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
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.util.BytesRef;
import org.springframework.stereotype.Component;

@Component
public class InMemoryLuceneIndex {

	private final StandardAnalyzer analyzer = new StandardAnalyzer();
	private final ByteBuffersDirectory memoryIndex = new ByteBuffersDirectory();

	public void indexDocument(String field, String value) {
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		try {
			IndexWriter writter = new IndexWriter(memoryIndex, indexWriterConfig);
			Document document = new Document();

			document.add(new TextField("title", field, Store.YES));
			document.add(new TextField("body", value, Store.YES));
			document.add(new SortedDocValuesField("title", new BytesRef(field)));

			writter.addDocument(document);
			writter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Document> searchIndex(String inField, String queryString) {
		try {
			Query query = new QueryParser(inField, analyzer).parse(queryString);
			IndexReader indexReader = DirectoryReader.open(memoryIndex);
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

	public List<Document> searchIndex(Query query) {
		try {
			IndexReader indexReader = DirectoryReader.open(memoryIndex);
			IndexSearcher searcher = new IndexSearcher(indexReader);
			TopDocs topDocs = searcher.search(query, 10);
			List<Document> documents = new ArrayList<>();
			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				documents.add(searcher.doc(scoreDoc.doc));
			}
			return documents;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

}
