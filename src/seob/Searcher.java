package seob;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Vector;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {
	private static String[] query;
	private static Directory dir;
	private static IndexReader reader;
	private static IndexSearcher searcher;
	
	public static void main(String[] args) {
		createSearcher();
		
		query = args[0].split(":| ");
		if(query[0].equals("boolean"))
			booleanSearch();
		else if(query[0].equals("tfidf"))
			tfidf();
		else
			cosineRanking();
	}
	
	private static void createSearcher()
	{
		try {
			dir = FSDirectory.open(Paths.get("index"));
			reader = DirectoryReader.open(dir);
			searcher = new IndexSearcher(reader);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	private static void booleanSearch()
	{
		int index = 1;
		Vector<Integer> q1, q2;
		
		while(query[index] != null)
		{
			
		}
	}
	
	private static Vector<Integer> executeQuery(int index)
	{
		
		return null;
	}
	
	private static void tfidf()
	{
		
	}
	
	private static void cosineRanking()
	{
		
	}
}
