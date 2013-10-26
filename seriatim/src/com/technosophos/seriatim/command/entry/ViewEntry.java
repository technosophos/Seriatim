package com.technosophos.seriatim.command.entry;

import static com.technosophos.seriatim.SeriatimConstants.*;

import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.web.command.base.BaseViewDocument;
import com.technosophos.rhizome.RhizomeException;

public class ViewEntry extends BaseViewDocument {
	protected RhizomeDocument getDocument() throws RhizomeException {
		if(!this.params.containsKey("doc")) {
			String err = "No entry was specified. I need the entry ID.";
			this.results.add(this.createErrorCommandResult(err, err));
			throw new RhizomeException("No document ID specified");
		}
		String docID = this.getFirstParam("doc", "").toString();
		
		DocumentRepository repo = this.repoman.getRepository(SERIATIM_REPO_NAME);
		RhizomeDocument doc = repo.getDocument(docID);
		if(!SERIATIM_TYPE_ENTRY.equals(doc.getMetadatum("type").getFirstValue())) {
			String err = "An entry with this ID was not found.";
			this.results.add(this.createErrorCommandResult(err, err));
			throw new RhizomeException("Type mismatch for ViewEntry.");
		}
		return doc;
	}
}
