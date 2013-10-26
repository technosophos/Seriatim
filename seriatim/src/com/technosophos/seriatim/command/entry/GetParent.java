package com.technosophos.seriatim.command.entry;

import static com.technosophos.seriatim.SeriatimConstants.SERIATIM_REPO_NAME;
import static com.technosophos.seriatim.SeriatimConstants.SERIATIM_TYPE_ENTRY;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.DocumentList;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.web.helper.RelatedDocumentsHelper;

/**
 * Get the <i>parent(s) of</i> the last entry in the CommandResults list.
 * 
 * Since Rhizome does not impose single-parent restrictions, a document <i>can</i> have
 * multiple parents. But this is not likely in Seriatim.
 * @author mbutcher
 *
 */
public class GetParent extends AbstractCommand {

	protected void execute() throws ReRouteRequest {
		RelatedDocumentsHelper h = new RelatedDocumentsHelper(this.repoman, "parentOf");
		
		int index = this.results.size();
		if(index < 1) {
			// Exit silently
			//System.err.println("##ListEntriesByProject: No Preceding Results");
			return;
		}
		Object o = this.results.get(index - 1).getResult();
		if(! (o instanceof RhizomeDocument)) {
			// exit silently:
			//System.err.println("##ListEntriesByProject: Object is not RhizomeDocument");
			return;
		}
		
		RhizomeDocument doc = (RhizomeDocument)o; 
		
		if(!SERIATIM_TYPE_ENTRY.equals(doc.getMetadatum("type").getFirstValue())) {
			// Exit silently
			return;
		}
		
		String docID = doc.getDocID();//this.getFirstParam("doc", "").toString();
		
		String[] mdNames = {
			"title"
		};
		
		try {
			DocumentList docs = h.getReverseRelatedDocuments(docID, mdNames, SERIATIM_REPO_NAME);
			this.results.add(this.createCommandResult(docs));
			//System.err.format("##ListEntriesByProject: Added doc list. Doc count: %d", docs.size() );
		} catch (RhizomeException e) {
			this.results.add(this.createErrorCommandResult(
				"Could not retrieve any relationships", 
				"No related items were found", 
				e
			));
		}

	}

}
