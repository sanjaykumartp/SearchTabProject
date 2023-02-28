package com.assessment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class SearchTab {
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter directory location: ");
		String directoryPath = scanner.nextLine();
		System.out.print("Enter search string: ");
		String searchString = scanner.nextLine();
		scanner.close();
		searchDirectory(directoryPath, searchString);
	}

	private static void searchDirectory(String directoryPath, String searchString) throws IOException {
		File directory = new File(directoryPath);
		if (!directory.exists() || !directory.isDirectory()) {
		    System.out.println("Invalid directory path: " + directoryPath);
		    return;
		}
		File[] files = directory.listFiles(new FilenameFilter() {
		    public boolean accept(File directory, String name) {
		        return name.toLowerCase().endsWith(".docx");
		    }
		});
		System.out.println("Number of files present in the directory " + files.length);
		if (files.length == 0) {
			System.out.println("No .docx files found in " + directoryPath);
			return;
		}
		if (!directory.isDirectory()) {
			System.out.println(directoryPath + " is not a directory.");
			return;
		}
		String[] searchWords = searchString.split(",");
		for (String searchWord : searchWords) {
			System.out.println('\n' + "Results for Keyword: " + searchWord);

			for (File file : directory.listFiles()) {
				if (file.isFile() && file.getName().endsWith(".docx")) {
					int count = searchFile(file, searchWord.trim());
					if (count > 0) {
						File subDir = new File(directoryPath + File.separator + searchWord);//File.separator:is a system-dependent file separator character, which is used to concatenate the directory path and the search word in a platform-independent way.
						if (!subDir.exists()) {
							subDir.mkdirs();
						}
						System.out.println("File Name is : " + file.getName() + "    " + " Keyword : "
								+ searchWord.trim() + "    " + " No of Occurence : " + count + "    "
								+ " Directory : " + directoryPath);
						Path sourcePath = Paths.get(file.getAbsolutePath());//get Absolute path of main directory
						Path targetPath = Paths.get(subDir.getAbsolutePath() + File.separator + file.getName());
						Files.copy(sourcePath, targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);//this line copies the original file to the destination file
						System.out.println(file.getName()  +"  document copied from the source path "+directoryPath +" to the target path "+ subDir+'\n');																						//which specifies that if the target file already exists, it should be replaced by the source file
					} else {																				
						System.out.println(searchWord.trim() + " keyword is not present in word document " + file.getName());
					}
				}
			}
		}
	}

	private static int searchFile(File file, String searchString) throws IOException {
		int count = 0;
		//The try block uses a try-with-resources statement, which automatically closes both the FileInputStream and XWPFDocument objects when the block is exited.
		try (FileInputStream fis = new FileInputStream(file);XWPFDocument document = new XWPFDocument(fis)) // XWPFDocument it is class of Apache POI library which represent microsoft word document
		{
			for (XWPFParagraph paragraph : document.getParagraphs()) {
				if (paragraph.getText().toLowerCase().contains(searchString.toLowerCase())) {
					count++;
				}
			}
		}
		return count;
	}
}