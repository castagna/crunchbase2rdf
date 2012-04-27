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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.openjena.atlas.io.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cache {

	private static final Logger log = LoggerFactory.getLogger(Cache.class) ;
	
	private File path ;
	
	public Cache(String base, File path) {
		log.debug("Cache({})", path.getAbsolutePath());
		path.mkdirs();
		if ( !path.isDirectory() ) throw new IllegalArgumentException("The specified path is not a directory: " + path.getAbsolutePath());
		if ( !path.canRead() ) throw new IllegalArgumentException("Cannot read from: " + path.getAbsolutePath());
		if ( !path.canWrite() ) throw new IllegalArgumentException("Cannot write to: " + path.getAbsolutePath());
		String base_path = base.replaceAll("http://", "").replaceAll("https://", "");
		this.path  = new File(path, base_path);
		this.path.mkdirs();
	}

	public void put (String url, String content) {
		File file = new File(path, filename(url));
		log.debug("put({}, ...) --> {}", url, file.getAbsolutePath());
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(content.getBytes());
		} catch (IOException e) {
			log.error("put({}, ...): {}", url, e.getMessage());
		} finally {
			if ( out != null ) try { out.close(); } catch (IOException e) { log.error(e.getMessage(), e); }
		}
	}
	
	public boolean has ( String url ) {
		File file = new File(path, filename(url));
		boolean result = file.exists();
		log.debug("has({}) --> {}", url, result);
		return result;
	}
	
	public String get (String url) {
		String content = null;
		File file = new File(path, filename(url));
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			content = IO.readWholeFileAsUTF8(in);
		} catch (IOException e) {
			log.error("get({}): {}", url, e.getMessage());
		} finally {
			if ( in != null ) try { in.close(); } catch (IOException e) { log.error(e.getMessage(), e); }
		}
		log.debug("get({}) --> {} bytes", url, content.getBytes().length);
		return content;
	}
	
	public InputStream open (String url) {
		File file = new File(path, filename(url));
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (IOException e) {
			log.error("get({}): {}", url, e.getMessage());
		}
		log.debug("get({}) --> {}", url, in);
		return in;
	}
	
	private String filename(String url) {
		String result = String.valueOf(url.hashCode()); 
		log.debug("filename({}) --> {}", url, result);
		return result;
	}
	
}
