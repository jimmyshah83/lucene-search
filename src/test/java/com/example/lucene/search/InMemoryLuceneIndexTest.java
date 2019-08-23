package com.example.lucene.search;

import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.lucene.search.indexes.InMemoryLuceneIndex;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InMemoryLuceneIndexTest {

	@Autowired
	private InMemoryLuceneIndex inMemoryLuceneIndex;

	@Test
	public void givenSearchQueryWhenFetchedDocumentThenCorrect() {
		inMemoryLuceneIndex.indexDocument("Hello world", "How are you doing? ");
		List<Document> documents = inMemoryLuceneIndex.searchIndex("body", "you");
		Assert.assertEquals("Hello world", documents.get(0).get("title"));
	}

	@Test
	public void givenTermQueryWhenFetchedDocumentThenCorrect() {
		inMemoryLuceneIndex.indexDocument("activity", "running in track");
		inMemoryLuceneIndex.indexDocument("activity", "Cars are running on road");
		inMemoryLuceneIndex.indexDocument("activity", "Cars are on the road");

		Term term = new Term("body", "running");
		Query query = new TermQuery(term);

		List<Document> documents = inMemoryLuceneIndex.searchIndex(query);
		Assert.assertEquals(2, documents.size());
	}
}
