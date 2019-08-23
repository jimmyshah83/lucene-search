package com.example.lucene.search;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.store.FSDirectory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.lucene.search.indexes.LuceneFileIndex;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LuceneFileIndexTest {
	
	private final String FILE_PATH = "<Absolute path>/luceneData/file1.txt";

	@Autowired
	private LuceneFileIndex luceneFileIndex;
	
	@Before
	public void init() throws IOException {
		luceneFileIndex.setDirectory(FSDirectory.open(Paths.get("luceneIndex")));
	}
	
	@Test
    public void givenSearchQueryWhenFetchedFileNamehenCorrect() throws IOException, URISyntaxException {
        luceneFileIndex.addFileToIndex(FILE_PATH);
        List<Document> docs = luceneFileIndex.searchFiles("contents", "FileNotFoundException");
        Assert.assertEquals("file1.txt", docs.get(0).get("filename"));
    }
}
