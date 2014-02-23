

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;


public class Search {
	private Similarity smfn;
	private IndexSearcher searcher;
	public Search(String indexPath_) {
		try {
			searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(indexPath_))));
			smfn=new DefaultSimilarity()
			{
				public float coord(int q,int d) {
					return 1;
				}
				public float queryNorm(float s) {
					return 1;
				}
				public float tf(float f) {
					return ((float)Math.log(f)+1);
				}
			};
			searcher.setSimilarity(smfn);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	public TreeMap<String,Float> search(Query query) throws IOException{
		System.out.println(query.toString());
		Map<String,Float> result=new HashMap<String,Float>();
		Map<String,Float> t=new HashMap<String,Float>();
		TopDocs results=searcher.search(query, 2000);
		ScoreDoc[] hits = results.scoreDocs;
		for (int i=0;i<Math.min(results.totalHits, 1000);i++) {
			Document doc=searcher.doc(hits[i].doc);
			String imgName=doc.get("docno");
			
			if (t.containsKey(imgName)) {
				continue;
			}
			t.put(imgName, hits[i].score);
		}
		TreeMap<String,Float> sorted = new TreeMap<String,Float>(new Comp(t));
		sorted.putAll(t);
		return sorted;
	}
}
class Comp implements Comparator<String> {
	Map<String,Float> d;
	public Comp(Map<String,Float> b) {
		d=b;
	}
	@Override
	public int compare(String arg0, String arg1) {
		if (d.get(arg0) >= d.get(arg1)) {
			return -1;
		} else {
			return 1;
		}
	}
	
}