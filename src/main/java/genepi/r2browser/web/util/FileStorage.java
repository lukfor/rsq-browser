package genepi.r2browser.web.util;

import java.io.File;
import java.util.List;
import java.util.Vector;

import io.javalin.core.util.FileUtil;
import io.javalin.http.UploadedFile;

public class FileStorage {

	public static List<File> store(List<UploadedFile> uploadedFiles, String location) {

		List<File> files = new Vector<File>();
		for (UploadedFile uploadedFile : uploadedFiles) {
			if (uploadedFile.getSize() > 0) {
				String filename = location + "/" + uploadedFile.getFilename();
				FileUtil.streamToFile(uploadedFile.getContent(), filename);
				File file = new File(filename);
				files.add(file);
			}
		}

		return files;
	}

}
