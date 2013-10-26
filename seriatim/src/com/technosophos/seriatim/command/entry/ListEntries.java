package com.technosophos.seriatim.command.entry;

import static com.technosophos.seriatim.SeriatimConstants.SERIATIM_REPO_NAME;
import static com.technosophos.seriatim.SeriatimConstants.SERIATIM_TYPE_ENTRY;

import java.util.Collections;
import java.util.HashMap;
import java.util.Comparator;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.document.DocumentList;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.web.command.base.BaseListDocuments;
/**
 * List all entries.
 * 
 * If the command configuration parameter "sort_on" is set, then this will attempt to 
 * sort the resulting documents, sorting on the value given to sort_on.
 * 
 * If the command configuration parameter "max_docs" is set, then this will attempt to 
 * limit the number of documents returned to no more than max_docs. If max_docs is 0, 
 * then this will assume that no maximum should be applied.
 * 
 * @author mbutcher
 *
 */
public class ListEntries extends BaseListDocuments implements Comparator<RhizomeDocument> {
	
	private String compareKey = "last_updated";

	protected DocumentList getDocumentList() throws RhizomeException {
		HashMap<String,String> narrower = new HashMap<String, String>();
		narrower.put("type", SERIATIM_TYPE_ENTRY);
		String [] additional_md = { "title","time", "date", "created_on","last_updated" };
		
		RepositorySearcher searcher = this.repoman.getSearcher(SERIATIM_REPO_NAME);
		DocumentRepository r = this.repoman.getRepository(SERIATIM_REPO_NAME);
		DocumentList docs = searcher.fetchDocumentList(narrower, additional_md, r);
		
		
		if(this.comConf.hasDirective("sort_on")) {
			System.err.println("Sorting enabled.");
			String sortOn = this.comConf.getDirective("sort_on")[0];
			this.sortDocuments(docs, sortOn);
			
			Collections.reverse(docs);
		}
		
		if(this.comConf.hasDirective("max_docs")) {
			System.err.println("Limiting enabled.");
 
			int maxDocs = 0;
			String [] sMaxDocs = this.comConf.getDirective("max_docs");
			try {
				maxDocs = Integer.parseInt(sMaxDocs[0]);
			} catch (Exception e) {
				// Do nothing.
				System.err.format(
						"ListEntries: max_docs is %s, which is invalid.\n",
						sMaxDocs[0]
				);
			}
			
			// If we have to trim the list, do so and return the revised list.
			if(maxDocs > 0 && docs.size() > maxDocs) {
				//docs.subList(0, maxDocs);
				DocumentList d2 = new DocumentList();
				for(int i = 0; i < maxDocs; ++i) {
					d2.add(docs.get(i));
				}
				return d2;
			}
		}
		
		return docs;	
	}
	
	protected void sortDocuments(DocumentList docs) {
		Collections.sort(docs, this);
	}
	
	protected void sortDocuments(DocumentList docs, String key) {

		this.compareKey = key;
		Collections.sort(docs, this);
	}
	
	public int compare(RhizomeDocument r1, RhizomeDocument r2) {
		String t1 = r1.getMetadatum(this.compareKey).getFirstValue();
		String t2 = r2.getMetadatum(this.compareKey).getFirstValue();
		return t1.compareToIgnoreCase(t2);
	}
}
