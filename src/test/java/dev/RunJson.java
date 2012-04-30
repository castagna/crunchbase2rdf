package dev;

import java.io.FileInputStream;

import org.openjena.atlas.io.IO;

import com.freebase.json.JSON;

public class RunJson {

	public static void main(String[] args) throws Exception {
		String content = IO.readWholeFileAsUTF8(new FileInputStream("/home/castagna/Desktop/facebook.json"));
		JSON json = JSON.parse(content);
		System.out.println(json.get("founded_year").number().intValue());
		System.out.println(json.has("founded_month"));
	}

}
