import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class GradingSummary {
	// Location of input CSV file with grades
	private static String INPUT_FILE = "";
	// Desired location of output file with LaTeX
	private static String OUTPUT_FILE = "";
	// Delimiter of tokens in the CSV file
	private static final String ITEM_DELIMITER = ",";
	// Delimiter of lines in the CSV file
	private static final String LINE_DELIMITER = "\n";
	// Contains all headers (the first row) of the CSV file
	private static ArrayList<String> headers;
	// URL of LaTeX template file
	private static File LATEX_TEMPLATE_FILE = new File("");
	// Place holder for values in LaTeX file
	private static final char LATEX_ESCAPE_CHAR='@';
	// String containing LaTeX_Template
	private static String LATEX_TEMPLATE_STRING;
	
	
    public static void main(String[] args) throws IOException{
    	if(args.length == 1) {
    		throw new IllegalArgumentException("Need to specify input and output files");
    	}
    	if(args.length >= 2)
    	{
    		INPUT_FILE = args[0];
    		OUTPUT_FILE = args[1];
    	} if( args.length == 3) {
    		LATEX_TEMPLATE_FILE = new File(args[2]);
    	}
    	
    	BufferedReader reader = null;
    	PrintWriter writer = null;
    	BufferedReader latexReader = null;
    	try {
    		reader = new BufferedReader(new FileReader(INPUT_FILE));
    		writer = new PrintWriter(new FileWriter(OUTPUT_FILE));
    		latexReader = new BufferedReader(new FileReader(LATEX_TEMPLATE_FILE));
    		
    		LATEX_TEMPLATE_STRING = readFileIntoString(latexReader);
    		
    		String nextLine = reader.readLine();
    		if(nextLine != null) {
    			headers = tokenizeLine(nextLine);
    		}
    		while((nextLine = reader.readLine()) != null) {
    			ArrayList<String> lineTokens = tokenizeLine(nextLine);
    			printNextPage(lineTokens, writer);
    		}
    		
    	} finally {
    		if(reader != null) {
    			reader.close();
    		}
    		if(writer != null) {
    			writer.close();
    		}
    		if(latexReader != null) {
    			latexReader.close();
    		}
    	}
    }
    
    // input: a String containing one line of a csv file
    // output: an ArrayList<String> containing each token of a single line
    public static ArrayList<String> tokenizeLine(String nextLine) {
    	return new ArrayList<String>(Arrays.asList(nextLine.split(ITEM_DELIMITER)));
    }
    
    // Takes in an ArrayList<String> containing each token from one line of the CSV file,
    // wraps it in LaTeX, then prints it all out to OUTPUT_FILE. Notably, only one file is
    // written to, but the delimiting of pages is done by LaTeX
    public static void printNextPage(ArrayList<String> lineTokens, PrintWriter writer) {
    	StringBuilder nextPage = new StringBuilder(LATEX_TEMPLATE_STRING);
    	// current column of csv that is being subbed in
    	int col = 0;
        boolean isHeaderFound = true;
    	for(int i = 0; i < nextPage.length(); ++i) {
    		char c = nextPage.charAt(i);
    		if(c == LATEX_ESCAPE_CHAR) {
    			// Alternate between headers and actual CSV data (i.e. "p1", then "5")
    			//System.out.println(col);
                if(isHeaderFound) {
    				nextPage.replace(i, i + 1, headers.get(col));
    			     isHeaderFound = false;
                } else {
    				nextPage.replace(i, i + 1, lineTokens.get(col));
                    isHeaderFound = true;
    			}
                if (isHeaderFound) col++;
    			

    		}
    	}
    	writer.print(nextPage);
    }
    
    // Reads in an entire file and stores/returns String representation of it
    public static String readFileIntoString(BufferedReader reader) throws IOException {
    	StringBuilder builder = new StringBuilder();
    	String nextLine = null;
    	while((nextLine = reader.readLine()) != null) {
    		builder.append(nextLine + "\n");
    	}
    	return builder.toString();
    }
}

        

