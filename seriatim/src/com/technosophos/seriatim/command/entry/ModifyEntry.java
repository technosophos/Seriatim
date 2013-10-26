package com.technosophos.seriatim.command.entry;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.web.command.base.BaseModifyDocument;
//import com.technosophos.rhizome.web.helper.RelatedDocumentsHelper;
import com.technosophos.rhizome.web.util.Scrubby;
import com.technosophos.seriatim.util.DateTimeUtils;

import static com.technosophos.seriatim.SeriatimConstants.*;

public class ModifyEntry extends BaseModifyDocument {

	protected RhizomeDocument getDocument() throws RhizomeException {
		String docID = this.getFirstParam("doc", "").toString();
		RhizomeDocument d = this.repoman.getDocument(SERIATIM_REPO_NAME, docID);
		if(!SERIATIM_TYPE_ENTRY.equals(d.getMetadatum("type").getFirstValue())) {
			throw new RhizomeException("RhizomeDocument is the wrong type.");
		}
		return d;
	}

	@Override
	protected RhizomeDocument modifyDocument(RhizomeDocument doc)
			throws RhizomeException {
		
		// Get all params, setting defaults where necessary.
		String title = this.getFirstParam("title", "Untitled").toString();
		String body = this.getFirstParam("body", "").toString();
		String note = this.getFirstParam("note", "").toString();
		String time = this.getFirstParam("time", "0").toString();
		String date = this.getFirstParam("date", "Untitled").toString();
		//String project = this.getFirstParam("project", "").toString();
		
		// Don't let title be empty
		if(title.trim().length() == 0) {
			title = doc.getMetadatum("title").getFirstValue();
		}
		
		// Prepare fields, escaping where necessary.
		title = Scrubby.cleanText(title);
		time = DateTimeUtils.canonicalTimeSliceString(time);
		date = DateTimeUtils.canonicalDateString(date);	
		body = Scrubby.trySafeButFallbackToPlain(body);
		note = Scrubby.trySafeButFallbackToPlain(note);
		
		// Build the new document.
		doc.replaceMetadatum(new Metadatum("title", title));
		doc.replaceMetadatum(new Metadatum("time", time));
		doc.replaceMetadatum(new Metadatum("date", date));
		doc.replaceMetadatum(new Metadatum("note", note));
		doc.setBody(body);
		
		// Add some automatically-generated fields.
		String ts = Long.toString(com.technosophos.seriatim.util.DateTimeUtils.now());
		doc.replaceMetadatum(new Metadatum("last_update", ts));
		
		// These two might throw exceptions. Let the base class handle them.
		//RelatedDocumentsHelper h = new RelatedDocumentsHelper(this.repoman, "parentOf");
		//h.relateDocuments(project, doc.getDocID(), SERIATIM_REPO_NAME);
		
		return doc;
	}


	protected void writeDocument(RhizomeDocument doc) throws RhizomeException {
		this.repoman.storeDocument(SERIATIM_REPO_NAME, doc);
	}

}
