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

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.freebase.json.JSON;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.talis.labs.crunchbase2rdf.AbstractRdfExtractor;
import com.talis.labs.crunchbase2rdf.Run;

public class FoundedYearRdfExtractor extends AbstractRdfExtractor {

	private final Calendar calendar = GregorianCalendar.getInstance();
	
	public FoundedYearRdfExtractor() {
		super("founded_year");
	}

	@Override
	public Model extract ( Resource subject, JSON json ) {
		Model model = ModelFactory.createDefaultModel();
		
		if ( json.has("founded_year") && json.get("founded_year") != null ) {
			int year = json.get("founded_year").number().intValue();
			calendar.set(Calendar.YEAR, year);
		}
		if ( json.has("founded_month") && json.get("founded_month") != null ) {
			int month = json.get("founded_month").number().intValue();
			calendar.set(Calendar.MONTH, month);
		}
		if ( json.has("founded_day") && json.get("founded_day") != null ) {
			int day = json.get("founded_day").number().intValue();
			calendar.set(Calendar.DAY_OF_MONTH, day);
		}
		
		System.out.println(calendar.getTime());
		
		Object object = json.object().get(name());
		if ( object != null ) {
			String founded_year = object.toString().trim();
			if ( founded_year.length() > 0 ) {
				Resource blog = ResourceFactory.createResource(founded_year);
				model.add(subject, ResourceFactory.createProperty(Run.CRUNCHBASE_NS_SCHEMA, name()), blog);
			}			
		}
		return model;
	}
	
}
