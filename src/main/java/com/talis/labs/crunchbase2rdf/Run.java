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

package com.talis.labs.crunchbase2rdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openjena.atlas.io.IO;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.freebase.json.JSON;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.vocabulary.RDF;

public class Run {

	public static final String CRUNCHBASE_BASE_API_URL = "http://api.crunchbase.com/v/1/";
	public static final String CRUNCHBASE_BASE_URL = "http://www.crunchbase.com/";
	public static final String CRUNCHBASE_NS_SCHEMA = "http://rdf.crunchbase.com/ns#";
	public static final String CRUNCHBASE_NS_DATA = "http://rdf.crunchbase.com/";
	
	public static final Map<String,String[]> endpoints = new HashMap<String,String[]>();
	public static final Map<String,String> endpoint2namespace = new HashMap<String,String>();
	public static final Map<String,Resource> namespace2RdfType = new HashMap<String,Resource>();
	static {
		endpoints.put("companies", new String[]{"name", "permalink"});
		endpoints.put("people", new String[]{"first_name", "last_name", "permalink"});
		endpoints.put("financial-organizations", new String[]{"name", "permalink"});
		endpoints.put("products", new String[]{"name", "permalink"});
		endpoints.put("service-providers", new String[]{"name", "permalink"});
		
		endpoint2namespace.put("companies", "company");
		endpoint2namespace.put("people", "person");
		endpoint2namespace.put("financial-organizations", "financial-organization");
		endpoint2namespace.put("products", "product");
		endpoint2namespace.put("service-providers", "service-provider");
		
		namespace2RdfType.put("company", ResourceFactory.createResource(CRUNCHBASE_NS_SCHEMA + "Company"));
		namespace2RdfType.put("person", FOAF.Person);
		namespace2RdfType.put("financial-organization", ResourceFactory.createResource(CRUNCHBASE_NS_SCHEMA + "FinancialOrganization"));
		namespace2RdfType.put("product", ResourceFactory.createResource(CRUNCHBASE_NS_SCHEMA + "Product"));
		namespace2RdfType.put("service-provider", ResourceFactory.createResource(CRUNCHBASE_NS_SCHEMA + "ServiceProvider"));
	}
	private static final Cache cache = new Cache(CRUNCHBASE_BASE_API_URL, new File("data/cache"));
	private static final Logger log = LoggerFactory.getLogger(Run.class);
	
	private static final Map<String,RdfExtractor> extractors = new HashMap<String,RdfExtractor>();
	static {
		
		Reflections reflections = new Reflections("com.talis.labs.crunchbase2rdf");
		Set<Class<? extends RdfExtractor>> classes = reflections.getSubTypesOf(RdfExtractor.class);
		for (Class<? extends RdfExtractor> clazz : classes) {
			try {
				RdfExtractor extractor = clazz.newInstance();
				extractors.put(extractor.name(), extractor);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Location location = new Location("target/tdb");
		Dataset dataset = TDBFactory.createDataset(location);
		Model cb = dataset.getDefaultModel();
		for (String endpoint : endpoints.keySet()) {
			String url = CRUNCHBASE_BASE_API_URL + endpoint + ".js";
			JSON json = JSON.parse(retrieve(url));
			@SuppressWarnings("unchecked")
			Iterator<JSON> iter = json.array().iterator();
			int size = json.array().size();
			int index = 1;
			while ( iter.hasNext() ) {
				try {
					JSON item = iter.next();
					String namespace = endpoint2namespace.get(endpoint);
					String permalink = item.get("permalink").string();
					String subject_uri_path = namespace + "/" + permalink;
					String subject_uri = CRUNCHBASE_NS_DATA + subject_uri_path;
					Resource subject = ResourceFactory.createResource(subject_uri);
					cb.add ( subject, RDF.type, namespace2RdfType.get(namespace) );
					cb.add ( subject, FOAF.isPrimaryTopicOf, ResourceFactory.createResource(CRUNCHBASE_BASE_URL + subject_uri_path));
					JSON js = JSON.parse( retrieve(CRUNCHBASE_BASE_API_URL + namespace + "/" + permalink + ".js") );
					for (String field : extractors.keySet()) {
						if ( js.has(field) ) {
							RdfExtractor extractor = extractors.get(field);
							Model model = extractor.extract(subject, js);
							cb.add ( model );
							// model.write(System.out, "TURTLE");
						}
					}					
					log.info("Processing " + namespace + " " + index++ + " of " + size + " " + endpoint);					
				} catch ( Exception e ) {
					log.error(e.getMessage(), e);
				}
			}
		}
		cb.close();
	}

	public static String retrieve ( String url ) throws IOException {
		if ( !cache.has(url) ) {
			URL u = new URL(url);
			InputStream in = u.openStream();
			cache.put(url, IO.readWholeFileAsUTF8(in));
		}
		return cache.get(url);
	}

}
