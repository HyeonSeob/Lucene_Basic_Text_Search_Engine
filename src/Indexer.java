import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.tartarus.snowball.ext.PorterStemmer;

public class Indexer
{
	public static void main(String[] args) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		Analyzer analyzer = new StandardAnalyzer();
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter w = new IndexWriter(index, config);
		
		int id;
		String line;
		StringBuffer title = new StringBuffer(), body = new StringBuffer();
		PorterStemmer stemmer = new PorterStemmer();
		String[] stklist;
		while((line = br.readLine()) != null){
			stklist = line.split("\\,|\\.| ");
			for(String stk : stklist)
			{
				stemmer.setCurrent(stk);
				stemmer.stem();
				System.out.println(stk+" "+stemmer.getCurrent());
			}
		}
	}
	
	private static void addDoc(IndexWriter w, int id, String title, String body) throws IOException {
		Document doc = new Document();
		doc.add(new IntField("id", id, Field.Store.YES));
		doc.add(new TextField("title", title, Field.Store.YES));
		doc.add(new StringField("body", body, Field.Store.YES));
		w.addDocument(doc);
	}
}