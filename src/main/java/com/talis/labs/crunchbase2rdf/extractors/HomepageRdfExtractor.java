/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.talis.labs.crunchbase2rdf.extractors;

import com.freebase.json.JSON;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.talis.labs.crunchbase2rdf.AbstractRdfExtractor;

public class HomepageRdfExtractor extends AbstractRdfExtractor {

	public HomepageRdfExtractor() {
		super("homepage_url");
	}

	@Override
	public Model extract ( Resource subject, JSON json ) {
		Model model = ModelFactory.createDefaultModel();
		Object object = json.object().get(name());
		if ( object != null ) {
			String homepage_url = object.toString().trim();
			if ( homepage_url.length() > 0 ) {
				Resource homepage = ResourceFactory.createResource(homepage_url);
				model.add(subject, FOAF.homepage, homepage);			
			}			
		}
		return model;
	}
	
}
