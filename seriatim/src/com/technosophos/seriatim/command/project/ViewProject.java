package com.technosophos.seriatim.command.project;

import com.technosophos.rhizome.web.command.base.BaseViewDocument;
import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.DocumentRepository;

import static com.technosophos.seriatim.SeriatimConstants.*;
/**
 * Get a project for viewing.
 * @author mbutcher
 *
 */
public class ViewProject extends BaseViewDocument {

	protected RhizomeDocument getDocument() throws RhizomeException {
		if(!this.params.containsKey("doc")) {
			String err = "No project was specified. I need the project ID.";
			this.results.add(this.createErrorCommandResult(err, err));
			throw new RhizomeException("No document ID specified");
		}
		String docID = this.getFirstParam("doc", "").toString();
		
		DocumentRepository repo = this.repoman.getRepository(SERIATIM_REPO_NAME);
		RhizomeDocument doc = repo.getDocument(docID);
		if(!SERIATIM_TYPE_PROJECT.equals(doc.getMetadatum("type").getFirstValue())) {
			String err = "A project with this ID was not found.";
			this.results.add(this.createErrorCommandResult(err, err));
			throw new RhizomeException("Type mismatch for ViewProject.");
		}
		return doc;
	}

}
