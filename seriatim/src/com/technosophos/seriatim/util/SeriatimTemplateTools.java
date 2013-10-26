package com.technosophos.seriatim.util;

import com.technosophos.rhizome.document.*;

public class SeriatimTemplateTools {

	public RhizomeDocument getParent(DocumentList parents, String docID) {
		for(RhizomeDocument doc: parents) {
			//System.err.format("Checking %s.\n", doc.getMetadatum("title").getFirstValue());
			if(doc.isRelatedTo(docID)) {
				//System.err.println("# HIT!");
				return doc;
			}
		}
		return null;
	}
	
	public String getAppName() {
		return "Seriatim Time Tracker";
	}
}
