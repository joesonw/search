
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.FieldInfo.IndexOptions;

public class TrecDocIterator implements Iterator<Document> {

	protected BufferedReader rdr;
	protected boolean at_eof = false;
	
	public TrecDocIterator(File file) throws FileNotFoundException {
		rdr = new BufferedReader(new FileReader(file));
		System.out.println("Reading " + file.toString());
	}
	
	@Override
	public boolean hasNext() {
		return !at_eof;
	}

	public Document next(int doc_index_id) {
		Document doc = new Document();
		StringBuffer sb = new StringBuffer();
		try {
			String line;
			Pattern docno_tag = Pattern.compile("<DOCNO>\\s*(\\S+)\\s*<");
			boolean in_doc = false;
			while (true) {
				line = rdr.readLine();
				if (line == null) {
					at_eof = true;
					break;
				}
				if (!in_doc) {
					if (line.startsWith("<DOC>"))
						in_doc = true;
					else
						continue;
				}
				if (line.startsWith("</DOC>")) {
					in_doc = false;
					sb.append(line);
					break;
				}

				Matcher m = docno_tag.matcher(line);
				if (m.find()) {
					String docno = m.group(1);
					doc.add(new StringField("docno", docno, Field.Store.YES));
					doc.add(new StringField("docid", Integer.toString(doc_index_id), Field.Store.YES));
					
				}
				
				if (line.startsWith("<FILEID>") || line.startsWith("<1ST_LINE>") || line.startsWith("<2ND_LINE>") 
						|| line.startsWith("<DOCNO>") || line.startsWith("<DATELINE>") || line.startsWith("<BYLINE>") )
					continue;

				sb.append(line);
			}
			if (sb.length() > 0)
			{
				String sbstring = sb.toString().replace("<DOC>", "");
				sbstring = sbstring.replace("</DOC>", "");
				sbstring = sbstring.replace("</HEAD>", "");
				sbstring = sbstring.replace("<HEAD>", "");
				sbstring = sbstring.replace("</BYLINE>", "");
				sbstring = sbstring.replace("<BYLINE>", " ");
				sbstring = sbstring.replace("</DATELINE>", "");
				sbstring = sbstring.replace("<DATELINE>", " ");
				sbstring = sbstring.replace("</TEXT>", "");
				sbstring = sbstring.replace("<TEXT>", " ");			
				
//				doc.add(new Field("contents", sb.toString(), Field.Store.NO, Field.Index.ANALYZED));
				FieldType fieldType = new FieldType();
			    fieldType.setStoreTermVectors(true);
//			    fieldType.setStoreTermVectorPositions(true);
			    fieldType.setIndexed(true);
			    fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
			    fieldType.setStored(true);
			    doc.add(new Field("contents", sbstring, fieldType));
			}
		} catch (IOException e) {
			doc = null;
		}
		return doc;
	}

	@Override
	public void remove() {
		// Do nothing, but don't complain
	}

	@Override
	public Document next() {
		// TODO Auto-generated method stub
		return null;
	}

}