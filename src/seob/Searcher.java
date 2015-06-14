package seob;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Stack;
import java.util.Vector;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.tartarus.snowball.ext.PorterStemmer;


public class Searcher {
	private static Vector<String> query;
	private static IndexReader reader;
	private static IndexSearcher searcher;
	private static PorterAnalyzer analyzer;
	private static int doc_num;
	
	public static void main(String[] args) {
		createSearcher();
		
		query = new Vector<String>();
		for(String s : args[0].split(":| "))
		{
			if(s.length() == 0)
				continue;
			
			if(s.contains("(") && s.length()>1)
			{
				query.add("(");
				query.add(s.substring(1));
			}
			else if(s.contains(")") && s.length()>1)
			{
				query.add(s.substring(0,s.length()-1));
				query.add(")");
			}
			else
				query.add(s);
		}
		
		String mode = query.elementAt(0);
		query.remove(0);
		
		if(mode.equals("boolean"))
			booleanSearch();
		else if(mode.equals("tfidf"))
			tfidf();
		else
			cosineRanking();
	}
	
	private static void createSearcher()
	{
		try {
			reader = DirectoryReader.open(FSDirectory.open(Paths.get("index")));
			searcher = new IndexSearcher(reader);
			analyzer = new PorterAnalyzer();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private static void booleanSearch()
	{
		Stack<Vector<Integer>> result = new Stack<Vector<Integer>>();
		query = infixToPostfix();
		
		for(String q : query)
		{
			if(q.equals("AND") || q.equals("OR"))
				result.push(mergeVector(q, result.pop(), result.pop()));
			else
				result.push(executeQuery(q));
		}
		
		for(int i : result.pop())
			System.out.println(i);
	}
	
	private static Vector<String> infixToPostfix()
	{
		String temp;
		Stack<String> stack = new Stack<String>();
		Vector<String> temp_query = new Vector<String>();
		for(String q : query)
		{
			if(q.equals("("))
				stack.push("(");
			else if(q.equals(")"))
			{
				while(!(temp = stack.pop()).equals("("))
					temp_query.add(temp);
			}
			else if(q.equals("AND") || q.equals("OR"))
				stack.push(q);
			else
				temp_query.add(q);
		}
		
		while(stack.size() > 0)
			temp_query.add(stack.pop());
		
		return temp_query;
	}
	
	private static Vector<Integer> executeQuery(String word)
	{
		Vector<Integer> result = new Vector<Integer>();
		try {
			TopScoreDocCollector collector = TopScoreDocCollector.create(1400);
			if(word.length() >= 6 && word.substring(0, 6).equals("title="))
				searcher.search(new QueryParser("title", analyzer).parse(word.substring(6)), collector);
			else
				searcher.search(new QueryParser("body", analyzer).parse(word), collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    for(ScoreDoc hit : hits)
		    	result.add(Integer.valueOf(searcher.doc(hit.doc).get("id")));
		    result.sort(null);
		    
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return result;
	}
	
	private static Vector<Integer> mergeVector(String mode, Vector<Integer> r1, Vector<Integer> r2)
	{
		Vector<Integer> merge = new Vector<Integer>();
		int MODE = (mode.equals("AND"))? 0 : 1;
		int index1=0, index2=0;
		
		while(index1 < r1.size() && index2 < r2.size())
		{
			if(r1.elementAt(index1) == r2.elementAt(index2))
			{
				merge.add(r1.elementAt(index1));
				index1++;
				index2++;
			}
			else if(r1.elementAt(index1) < r2.elementAt(index2))
			{
				if(MODE == 1)
					merge.add(r1.elementAt(index1));
				index1++;
			}
			else
			{
				if(MODE == 1)
					merge.add(r2.elementAt(index2));
				index2++;	
			}
		}
		
		if(MODE == 1)
		{
			while(index1 < r1.size())
				merge.add(r1.elementAt(index1++));
			while(index2 < r2.size())
				merge.add(r2.elementAt(index2++));
		}
		
		return merge;
	}
	
	private static void tfidf()
	{
		PorterStemmer ps = new PorterStemmer();
		int docNum = reader.numDocs();
		int docFreq;
		for(String s : query)
		{
			try {
				ps.setCurrent(s);
				ps.stem();
				docFreq = reader.docFreq(new Term("body", ps.getCurrent()));
				
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	private static void cosineRanking()
	{
		
	}
}
