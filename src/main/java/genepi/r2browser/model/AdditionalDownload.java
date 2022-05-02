package genepi.r2browser.model;

import java.io.File;

import org.apache.commons.io.FileUtils;

import genepi.io.FileUtil;

public class AdditionalDownload {

	private String filename;

	private String name;

	private String description;

	private String size;

	public String getFilename() {
		return filename;
	}

	public String getFilenameOnly() {
		return FileUtil.getFilename(filename);
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
		this.size = FileUtils.byteCountToDisplaySize(new File(filename).length());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSize() {
		return size;
	}

}
