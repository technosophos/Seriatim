package com.technosophos.seriatim.command.admin;

import com.technosophos.rhizome.command.AbstractCommand;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RhizomeInitializationException;

import static com.technosophos.seriatim.SeriatimConstants.*;

/**
 * Create the main Seriatim repository.
 * @author mbutcher
 *
 */
public class CreateSeriatimRepository extends AbstractCommand {

	/**
	 * Create the main repository.
	 */
	protected void execute() throws ReRouteRequest {
		
		String repoName = SERIATIM_REPO_NAME;
		
		if( this.repoman.hasRepository(repoName) ) {
			// We assume a naming collision from generateRepoID, and we try again:
			String err = String.format("A repository named %s already exists.", repoName);
			String ferr = err;
			results.add( this.createErrorCommandResult(err, ferr));
			return;	
		}
		// Create repository
		try {
			this.repoman.createRepository(repoName);
		} catch ( RepositoryAccessException e) {
			String errMsg = String.format("Access problem when creating %s: %s", repoName, e.getMessage());
			String ferr = "We could not create the repository for " + repoName;
			results.add(this.createErrorCommandResult(errMsg, ferr, e));
			return;
		} catch (RhizomeInitializationException e) {
			String errMsg = String.format("Problem initializing repository when creating %s: %s", repoName, e.getMessage() );
			String ferr = "We encountered a problem while trying to set up your repository. Try again later.";
			results.add(this.createErrorCommandResult(errMsg, ferr, e));
			return;
		}
	}

}
