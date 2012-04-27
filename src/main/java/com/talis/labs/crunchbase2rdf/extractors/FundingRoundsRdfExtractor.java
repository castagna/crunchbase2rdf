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

import java.util.List;

import com.freebase.json.JSON;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.talis.labs.crunchbase2rdf.AbstractRdfExtractor;

public class FundingRoundsRdfExtractor extends AbstractRdfExtractor {

	public FundingRoundsRdfExtractor() {
		super("funding_rounds");
	}

	@Override
	public Model extract ( Resource subject, JSON json ) {
		Model model = ModelFactory.createDefaultModel();
		Object object = json.object().get(name());
		if ( object != null ) {
			@SuppressWarnings("unchecked")
			List<JSON> rounds = ((JSON)object).array();
			for (JSON round : rounds) {
				extract_rounds(subject, round, model);
			}
		}
		return model;
	}
	
	private void extract_rounds ( Resource subject, JSON json, Model model ) {

		int year = json.get("funded_year").number().intValue();
		int month = json.get("funded_month").number().intValue();
		int day = json.get("funded_day").number().intValue();
		Double raised_amount = json.get("raised_amount").number().doubleValue();
		String source_url = json.get("source_url").string();
		String source_description = json.get("source_description").string();
		String raised_currency_code = json.get("raised_currency_code").string();
		String round_code = json.get("round_code").string();

		// TODO Date date = Calendar.getInstance().get
		
		extract_investments(subject, json.get("investments"), model);

	}

	private void extract_investments ( Resource subject, JSON json, Model model ) {

		System.out.println(json);
	}

}
