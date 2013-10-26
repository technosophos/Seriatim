package com.technosophos.seriatim.command.project;

import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.document.DocumentList;
import com.technosophos.rhizome.web.command.base.BaseListDocuments;
import com.technosophos.rhizome.RhizomeException;

import static com.technosophos.seriatim.SeriatimConstants.*;
import java.util.HashMap;

public class ListProjects extends BaseListDocuments {
	
	public DocumentList getDocumentList() throws RhizomeException {
		HashMap<String,String> narrower = new HashMap<String, String>();
		narrower.put("type", SERIATIM_TYPE_PROJECT);
		narrower.put("enabled", "yes");
		String [] additional_md = { "title","created_on","last_updated" };
		
		RepositorySearcher searcher = this.repoman.getSearcher(SERIATIM_REPO_NAME);
		DocumentRepository r = this.repoman.getRepository(SERIATIM_REPO_NAME);
		DocumentList docs = searcher.fetchDocumentList(narrower, additional_md, r);
		return docs;
	}

}
