package com.technosophos.seriatim.command.project;

import static com.technosophos.seriatim.SeriatimConstants.SERIATIM_REPO_NAME;
import static com.technosophos.seriatim.SeriatimConstants.SERIATIM_TYPE_PROJECT;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.web.command.base.BaseModifyDocument;
import com.technosophos.rhizome.web.util.Scrubby;
/**
 * Modify a project document.
 * @author mbutcher
 *
 */
public class ModifyProject extends BaseModifyDocument {

	protected RhizomeDocument getDocument() throws RhizomeException {
		String docID = this.getFirstParam("doc", "").toString();
		RhizomeDocument d = this.repoman.getDocument(SERIATIM_REPO_NAME, docID);
		if(!SERIATIM_TYPE_PROJECT.equals(d.getMetadatum("type").getFirstValue())) {
			throw new RhizomeException("RhizomeDocument is the wrong type.");
		}
		return d;
	}

	protected void writeDocument(RhizomeDocument doc) throws RhizomeException {
		this.repoman.storeDocument(SERIATIM_REPO_NAME, doc);
	}

	protected RhizomeDocument modifyDocument(RhizomeDocument doc)
			throws RhizomeException {
		String title = this.getFirstParam("title", "").toString();
		String body = this.getFirstParam("body", "").toString();
		String enabled = this.getFirstParam("enabled", "yes").toString();
		
		if(title.trim().length() == 0) {
			title = doc.getMetadatum("title").getFirstValue();
		} else {
			title = Scrubby.cleanText(title);
		}
		body = Scrubby.trySafeButFallbackToPlain(body);
		enabled = "yes".equalsIgnoreCase(enabled) ? "yes" : "no";
		
		doc.replaceMetadatum(new Metadatum("title", title));
		doc.replaceMetadatum(new Metadatum("enabled", enabled));
		doc.setBody(body);
		
		String time = Long.toString(com.technosophos.seriatim.util.DateTimeUtils.now());
		doc.replaceMetadatum(new Metadatum("last_updated", time));
		return doc;
	}


}
