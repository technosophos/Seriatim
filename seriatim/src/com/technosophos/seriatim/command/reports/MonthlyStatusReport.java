package com.technosophos.seriatim.command.reports;

import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.*;
import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.rhizome.repository.SearchResults;
import com.technosophos.seriatim.report.ReportList;
import com.technosophos.seriatim.util.DateTimeUtils;
//import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import static com.technosophos.seriatim.SeriatimConstants.SERIATIM_REPO_NAME;

public class MonthlyStatusReport extends AbstractCommand {
	protected static final String REPORT_TITLE = "Monthly Status Report";
	protected static final String REPORT_SUBTITLE = "Monthly Activity for %s";
	protected static final String DEFAULT_REPORT_SUBTITLE = "Project Activity This Month";
	
	protected void execute() throws ReRouteRequest {
		try {
			ReportList docs = this.getReport();
			this.results.add(this.createCommandResult(docs));
		} catch (RhizomeInitializationException e) {
			String err = "Failed to Initialize Storage";
			String ferr = "Could not find projects.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		} catch (RepositoryAccessException e) {
			String err = "Failed to access documents";
			String ferr = "An error occured while getting your documents.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		} catch(RhizomeException e) {
			String err = "Failed to getDocumentList(): " + e.getMessage();
			String ferr = "We could not retrieve a listing at this time. Please try again later.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		}
	}
	
	protected ReportList getReport() throws RhizomeException {
		//DocumentList dl = this.getDocumentList();
		ReportList report = new ReportList();
		report.setTitle(REPORT_TITLE);
		
		
	/*
		 * What we want to do: Search for all of the records from the given month.
		 */
		String moStr = this.getFirstParam("month","").toString();
		String yrStr = this.getFirstParam("year", "").toString();
		String project = this.getFirstParam("project", "").toString();
		Calendar now = new GregorianCalendar();
		
		int yr;
		if(yrStr.length() == 0) {
			yr = now.get(Calendar.YEAR);
		} else {
			try {
				yr = Integer.parseInt(yrStr);
				// It's arbitrary... so what?
				if(yr < 0 || yr > now.get(Calendar.YEAR)) yr = now.get(Calendar.YEAR);
			} catch (NumberFormatException e) {
				yr = now.get(Calendar.YEAR);
			}
		}
		
		int mo;
		if(moStr.length() == 0) {
			mo = now.get(Calendar.MONTH);
		} else {
			try {
				mo = Integer.parseInt(moStr);
				// FIXME: Make this more lenient.
				if(mo <= 0) {
					int cmo = now.get(Calendar.MONTH);
					if(cmo == 0) mo = 11;
					else mo = cmo - 1;
				} else {
					--mo; // decrement to get it to zero index.
				}
				/*
				if(mo > 11) mo = 12;
				else if(mo < 0) {
					if(mo < -11) mo = 0;
					else {
						int cmo = now.get(Calendar.MONTH);
						mo = 0;
					}
				}
				*/
			} catch (NumberFormatException e) {
				mo = now.get(Calendar.MONTH);
			}
		}
		
		// Now we can build a date:
		GregorianCalendar reportStartDate = new GregorianCalendar(yr, mo, 1);
		
		int lastDayOfMo = reportStartDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		//System.out.format("# Last day of month is %d\n", lastDayOfMo);
		GregorianCalendar reportEndDate = new GregorianCalendar(yr, mo, lastDayOfMo, 23, 59, 59);
		long utsStart = reportStartDate.getTimeInMillis();
		long utsEnd = reportEndDate.getTimeInMillis();
		
		report.setPeriod(reportStartDate.getTime(), reportEndDate.getTime());
		
		//System.out.format("# Searching for %d-%d\n", mo, yr);
		RepositorySearcher searcher = this.repoman.getSearcher(SERIATIM_REPO_NAME);
		
		String format = "date:[%d TO %d]";
		String q = String.format(format, utsStart, utsEnd);
		
		
		
		//System.out.println("# Search filter: " + q);
		
		String [] names = {"title", "time", "date"};
		Map<String,String> args = new HashMap<String,String>();
		args.put("fields", "date"); // Fields to search. We want to only search date fields.
		args.put("search_body", "n"); // Do NOT search body. Save us some time.
		
		// Now we are ready to do the search:
		
		
		SearchResults results;
		try {
			results = searcher.simpleSearch(
					q, 
					names, 
					args, 
					this.repoman.getRepository(SERIATIM_REPO_NAME)
			);
		} catch (RepositoryAccessException e) {
			String err = "MonthlyStatusReport: Search failed.";
			String ferr = "An error occured while generating the report.";
			e.printStackTrace(System.err);
			this.results.add(this.createErrorCommandResult(err, ferr, e));
			return null;
		}
		DocumentList dl = results.getDocumentList();
		
		if(project.length() > 0) {
			report.setSubtitle(String.format(REPORT_SUBTITLE, project));
			DocumentList dl2 = new DocumentList();
			String[] ids = searcher.getDocIDsByMetadataValue("title", project);
			if(ids.length > 0) {
				RhizomeDocument pdoc = this.repoman.getDocument(SERIATIM_REPO_NAME, ids[0]);
				for(RhizomeDocument d: dl) {
					if(pdoc.isRelatedTo(d.getDocID(), "parentOf")) {
						dl2.add(d);
					}
				}
			}
			//return dl2; // This will be empty if the project is not found.
			dl = dl2;
		} else {
			report.setSubtitle(DEFAULT_REPORT_SUBTITLE);
		}
		
		//List times = new ArrayList();
		String [] times = new String[dl.size()];
		int i = 0;
		for(RhizomeDocument d: dl) {
			times[i] = d.getMetadatum("time").getFirstValue();
			++i;
		}
		String totalTime = DateTimeUtils.addTimes(times);
		RhizomeDocument totalDoc = new RhizomeDocument("0");
		totalDoc.addMetadatum(new Metadatum("time", totalTime));
		
		report.setTotal(totalDoc);
		
		report.setReportItems(dl);
		return report;
	}

}
