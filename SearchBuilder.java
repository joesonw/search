

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;


public class SearchBuilder {
	QueryParser parser;
	Search search;
	Map<String,String> imgTrecs;																																																																	
	public SearchBuilder(String indexPath_,String trecFile_) {
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
		parser = new QueryParser(Version.LUCENE_CURRENT,"contents",analyzer);
		search=new Search(indexPath_);
		BufferedReader in;
		imgTrecs=new HashMap<String,String>();
		try {
			in=new BufferedReader(new InputStreamReader(new FileInputStream(trecFile_),"UTF-8"));
			while (true) {
				String line=in.readLine();
				if (line==null || line.length() == -1) {
					break;
				}
				String imgName=null;
				if (line.equals("<DOC>"))  {
					line = in.readLine();
					imgName = line.replaceAll("<DOCNO>", "");
					imgName = imgName.replaceAll("</DOCNO>", "");
					line=in.readLine();
					line=in.readLine();
				} else {
					continue;
				}
				line=line.trim();
				if (line.length()==0) {
					break;
				}
				imgTrecs.put(imgName, line);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
	}
	public TreeMap<String,Float> searchByName(String imgName) {
		TreeMap<String,Float> result=new TreeMap<String,Float>();
		if (imgTrecs.containsKey(imgName)) {
			try {
				result=search.search(parser.parse(imgTrecs.get(imgName)));
			} catch (Exception e) {
				System.out.println(e.toString());
			}
		}
		return result;
	}
	public TreeMap<String,Float> searchByRaw(String raw) {
                System.out.println(raw);
		TreeMap<String,Float> result=new TreeMap<String,Float>();
		try {
			result=search.search(parser.parse(raw));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}
}
