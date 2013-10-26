package com.technosophos.seriatim.command.entry;

import static com.technosophos.seriatim.SeriatimConstants.SERIATIM_REPO_NAME;
import static com.technosophos.seriatim.SeriatimConstants.SERIATIM_TYPE_ENTRY;

import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.document.DocumentList;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.web.command.base.BaseListDocuments;

public class ListRecentEntries extends BaseListDocuments implements Comparator<RhizomeDocument>{

	private String compareKey = "date";
	
	/**
	 * List recent entries.
	 */
	protected DocumentList getDocumentList() throws RhizomeException {
		HashMap<String,String> narrower = new HashMap<String, String>();
		narrower.put("type", SERIATIM_TYPE_ENTRY);
		String [] additional_md = { "title","time", "date", "created_on","last_updated" };
		
		RepositorySearcher searcher = this.repoman.getSearcher(SERIATIM_REPO_NAME);
		DocumentRepository r = this.repoman.getRepository(SERIATIM_REPO_NAME);
		DocumentList docs = searcher.fetchDocumentList(narrower, additional_md, r);
		
		this.sortDocuments(docs);
		
		int maxDocs = 25;
		if(this.comConf.hasDirective("max_docs")) {
			String [] sMaxDocs = this.comConf.getDirective("max_docs");
			try {
				maxDocs = Integer.parseInt(sMaxDocs[0]);
			} catch (Exception e) {
				// Do nothing.
			}
		}
		
		if(maxDocs > 0 && docs.size() > maxDocs) {
			docs.subList(0, maxDocs);
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
