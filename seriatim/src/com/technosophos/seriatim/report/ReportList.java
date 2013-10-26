package com.technosophos.seriatim.report;

import com.technosophos.rhizome.document.DocumentList;
import com.technosophos.rhizome.document.RhizomeDocument;
import java.util.Date;

/**
 * List of reporting items, along with a title and period information.
 * @author mbutcher
 *
 */
public class ReportList {
	String reportTitle = "";
	String subtitle = "";
	Date start, end;
	RhizomeDocument total;
	DocumentList dl;
	
	public ReportList setTitle(String title) {
		this.reportTitle = title;
		return this;
	}
	
	public ReportList setSubtitle(String subtitle) {
		this.subtitle = subtitle;
		return this;
	}
	
	public String getSubtitle() {
		return this.subtitle;
	}
	
	public String getTitle() {
		return this.reportTitle;
	}
	
	public ReportList setPeriod(Date start) {
		this.start = start; this.end = start;
		return this;
	}
	
	public ReportList setPeriod(Date start, Date end) {
		this.start = start; this.end = end;
		return this;
	}
	
	public Date [] getPeriod() {
		return new Date[] {this.start, this.end};
	}
	
	public Date getPeriodStart() { return this.start; }
	public Date getPeriodEnd() { return this.end; }
	
	public long getPeriodStartTS() { return this.getPeriodStart().getTime(); }
	public long getPeriodEndTS() { return this.getPeriodEnd().getTime(); }
	
	public ReportList setTotal(RhizomeDocument doc) {
		this.total = doc;
		return this;
	}
	
	public RhizomeDocument getTotal() {
		return this.total;
	}
	
	public ReportList setReportItems(DocumentList dl) {
		this.dl = dl;
		return this;
	}
	
	public DocumentList getReportItems() {
		return this.dl;
	}
	
}
