package com.technosophos.seriatim.command.project;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.web.command.base.BaseAddDocument;
import com.technosophos.rhizome.document.*;
import com.technosophos.rhizome.web.util.Scrubby;

import static com.technosophos.seriatim.SeriatimConstants.*;

/**
 * Add a new project.
 * @author mbutcher
 *
 */
public class AddProject extends BaseAddDocument {
	protected RhizomeDocument buildDocument() throws RhizomeException {
		String title = this.getFirstParam("title", "Untitled").toString();
		String body = this.getFirstParam("body", "").toString();
		String enabled = this.getFirstParam("enabled", "yes").toString();
		
		title = Scrubby.cleanText(title);
		body = Scrubby.trySafeButFallbackToPlain(body);
		enabled = "yes".equalsIgnoreCase(enabled) ? "yes" : "no";
		
		RhizomeDocument doc = new RhizomeDocument(DocumentID.generateDocumentID());
		
		doc.addMetadatum(new Metadatum("title", title));
		doc.addMetadatum(new Metadatum("enabled", enabled));
		doc.setBody(body);
		
		String time = Long.toString(com.technosophos.seriatim.util.DateTimeUtils.now());
		doc.addMetadatum(new Metadatum("type", SERIATIM_TYPE_PROJECT));
		doc.addMetadatum(new Metadatum("created_on", time));
		doc.addMetadatum(new Metadatum("last_updated", time));
		return doc;
	}
	
	protected void writeDocument(RhizomeDocument doc) throws RhizomeException {
		this.repoman.storeDocument(SERIATIM_REPO_NAME, doc);
	}
	
}
