package com.technosophos.seriatim.command.entry;

import static com.technosophos.seriatim.SeriatimConstants.*;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.web.command.base.BaseAddDocument;
import com.technosophos.rhizome.document.DocumentID;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.web.util.Scrubby;
import com.technosophos.rhizome.web.helper.RelatedDocumentsHelper;
import com.technosophos.seriatim.util.DateTimeUtils;

import java.util.HashMap;

/**
 * Add a new entry to a project.
 * @author mbutcher
 *
 */
public class AddEntry extends BaseAddDocument {

	/**
	 * Build the document from parameters.
	 * @return The new document.
	 */
	protected RhizomeDocument buildDocument() throws RhizomeException {
		
		// Make sure required fields are set.
		HashMap<String, String> required = new HashMap<String, String>();
		required.put("title", "Title");
		required.put("time", "Work Time");
		if(!this.requireFields(required)) {
			throw new RhizomeException("One or more required fields were absent.");
		}
		
		// Get all params, setting defaults where necessary.
		String title = this.getFirstParam("title", "Untitled").toString();
		String body = this.getFirstParam("body", "").toString();
		String note = this.getFirstParam("note", "").toString();
		String time = this.getFirstParam("time", "0").toString();
		String date = this.getFirstParam("date", "").toString();
		String project = this.getFirstParam("project", "").toString();
		
		if(project.trim().length() == 0)
			throw new RhizomeException("Project ID not set.");
		
		// Prepare fields, escaping where necessary.
		title = Scrubby.cleanText(title);
		time = DateTimeUtils.canonicalTimeSliceString(time);
		date = DateTimeUtils.canonicalDateString(date);	
		body = Scrubby.trySafeButFallbackToPlain(body);
		note = Scrubby.trySafeButFallbackToPlain(note);
		
		// Build the new document.
		RhizomeDocument doc = new RhizomeDocument(DocumentID.generateDocumentID());
		doc.addMetadatum(new Metadatum("title", title));
		doc.addMetadatum(new Metadatum("time", time));
		doc.addMetadatum(new Metadatum("date", date));
		doc.addMetadatum(new Metadatum("note", note));
		doc.setBody(body);
		
		// Add some automatically-generated fields.
		String ts = Long.toString(com.technosophos.seriatim.util.DateTimeUtils.now());
		doc.addMetadatum(new Metadatum("type", SERIATIM_TYPE_ENTRY));
		doc.addMetadatum(new Metadatum("created_on", ts));
		doc.addMetadatum(new Metadatum("last_update", ts));
		
		// These two might throw exceptions. Let the base class handle them.
		System.err.println("### Adding Relation.");
		RelatedDocumentsHelper h = new RelatedDocumentsHelper(this.repoman, "parentOf");
		h.relateDocuments(project, doc.getDocID(), SERIATIM_REPO_NAME);
		System.err.println("### Relation Added.");
		
		return doc;
	}
	
	protected void writeDocument(RhizomeDocument doc) throws RhizomeException {
		this.repoman.storeDocument(SERIATIM_REPO_NAME, doc);
	}
	


}
