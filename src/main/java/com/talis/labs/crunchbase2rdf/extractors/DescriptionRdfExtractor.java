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
import com.hp.hpl.jena.vocabulary.DC;
import com.talis.labs.crunchbase2rdf.AbstractRdfExtractor;

public class DescriptionRdfExtractor extends AbstractRdfExtractor {

	public DescriptionRdfExtractor() {
		super("description");
	}

	@Override
	public Model extract ( Resource subject, JSON json ) {
		Model model = ModelFactory.createDefaultModel();
		Object object = json.object().get(name());
		if ( object != null ) {
			String description = object.toString().trim();
			if ( description.length() > 0 ) {
				model.add(subject, DC.description, description);
			}			
		}
		return model;
	}
	
}
