package com.technosophos.seriatim.command.template;

import org.apache.velocity.VelocityContext;

import com.technosophos.rhizome.command.template.DoVelocityTemplate;
import com.technosophos.rhizome.web.util.TemplateTools;
import com.technosophos.seriatim.util.SeriatimTemplateTools;

/**
 * Configure Seriatim template processor.
 * Inserts {@link TemplateTools} into the context 
 * as $tpl and {@link SeriatimTemplateTools} as $ser.
 * @author mbutcher
 * @see DoVelocityTemplate
 */
public class DoSeriatimTemplate extends DoVelocityTemplate {
	protected VelocityContext createContext() {
		VelocityContext cxt = super.createContext();
		TemplateTools tt = new TemplateTools();
		SeriatimTemplateTools stt = new SeriatimTemplateTools();
		cxt.put("tpl", tt);
		cxt.put("ser", stt);
		return cxt;
	}
}
