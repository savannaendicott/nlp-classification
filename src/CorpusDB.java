import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;



public class CorpusDB
{
    static Connection c = null;
    static Statement stmt = null;

    static String categories[] = {"Sports","Travel","Entertainment","World News"};

    public static void connect()
    {

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:mycorpus.db;foreign keys=true;");
           
            c.setAutoCommit(false);
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        
        //System.out.println("Opened database successfully");
    }
    public static void close(){
    	 try {
	    	c.close();
	    	stmt.close();
	    } catch ( Exception e ) {
	        System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	        System.exit(0);
	    }
    }
    public static int getOccurencesInDoc(String word, String document){
    //get number of occurrences of an input word in an input document – and not returning 0.
    	try{
	        ResultSet rs;
	        rs = stmt.executeQuery("SELECT count, docname, word FROM document, corpus WHERE fileid=docid AND docname='"+document+"' AND word='"+word+"';");
	        
	        // word doesn't appear in this document - don't return 0!
	        if(!rs.next()) return 1;
	        
	        return rs.getInt("count");
    	}
	    catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems  getting occurences of a word");
	    	System.exit(0);
	    	return -1;}
    	
    }
    
    public static int getOccurencesInCat(String word, String category){
        //get number of occurrences of an input word in an input document – and not returning 0.
        	try{
        		ResultSet rs;
    	        rs = stmt.executeQuery("SELECT count, cat, word FROM categories WHERE cat='"+category+"' AND word='"+word+"';");
    	        // word doesn't appear in this document - don't return 0!
    	        if(!rs.next()) return -1;
    	        //System.out.println("found "+ rs.getInt("count") +" instances of "+ word+" in the "+category+" category.");
    	        return rs.getInt("count");
        	}
    	    catch ( Exception e ) {
    	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems  getting occurences of a word");
    	    	System.exit(0);
    	    	return -1;}
    }
    public static int getOccurences(String word){
    	try{
	        ResultSet rs;
	        rs = stmt.executeQuery("SELECT count, word FROM master_wordlist WHERE word='"+word+"';");
	        
	        // word doesn't appear in this document - don't return 0!
	        if(!rs.next()) return -1;
	        
	        return rs.getInt("count");
    	}
	    catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems  getting occurences of a word");
	    	System.exit(0);
	    	return -1;}
    }
    public static int getNumWords(){
        //get number of occurrences of an input word in an input document – and not returning 0.
    	try{
	        ResultSet rs;
	        rs = stmt.executeQuery("SELECT COUNT(word) AS total FROM master_wordlist;");
	        
	        // word doesn't appear in this document - don't return 0!
	        if(!rs.next()) System.out.println("Query returned nothing");;
	        
	        return rs.getInt("total");
    	}
	    catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems  getting occurences of a word");
	    	System.exit(0);
	    	return -1;}
        	
    }
    public static int getNumWords(String document){
        //get number of occurrences of an input word in an input document – and not returning 0.
    	try{
	        ResultSet rs;
	        rs = stmt.executeQuery("SELECT COUNT word, fileid,docid AS numwords FROM document, corpus WHERE fileid=docid AND docname='"+document+"';");
	        
	        // word doesn't appear in this document - don't return 0!
	        if(!rs.next()) System.out.println("Query returned nothing");;
	        
	        return rs.getInt("numwords");
    	}
	    catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems  getting occurences of a word");
	    	System.exit(0);
	    	return -1;}
        	
    }
    public static int getNumDocs(String category){
    	try{
	        ResultSet rs;
	        rs = stmt.executeQuery("SELECT COUNT(*) AS numdocs FROM corpus WHERE category='"+category+"';");
	        
	        // word doesn't appear in this document - don't return 0!
	        if(!rs.next()) return 0;
	        else return rs.getInt("numdocs");
    	}
	    catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems  getting occurences of a word");
	    	System.exit(0);
	    	return -1;
    	}
    	
    }
    public static int getNumDocs(){
    	try{
	        ResultSet rs;
	        rs = stmt.executeQuery("SELECT COUNT(*) AS numdocs FROM corpus;");
	        
	        // word doesn't appear in this document - don't return 0!
	        if(!rs.next()) return 0;
	        else return rs.getInt("numdocs");
    	}
	    catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems  getting occurences of a word");
	    	System.exit(0);
	    	return -1;
    	}
    	
    }
    public static ArrayList<String> getWordsInDoc(String document, int num){
    	ArrayList<String> words = new ArrayList<String>();
    	ArrayList<String> stopwords = getStopWords();
    	try{
    		ResultSet rs = stmt.executeQuery("SELECT DISTINCT document.word, document.fileid, corpus.docid, categories.word, corpus.docname FROM document, categories, corpus WHERE document.word=categories.word AND corpus.docid=document.fileid AND corpus.docname='"+document+"';");
	        
	        while(rs.next() && num >0){
	            String word = rs.getString("word");
	            if(!stopwords.contains(word)){
	            	words.add(word);
	            	num--;
	            }
	        }
	        stmt.close();
    	}
	    catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() +" problems retrieving all of the words (master list)");
	    	System.exit(0);}
    	return words;
    }
    public static ArrayList<String> getWords(){
    	ArrayList<String> words = new ArrayList<String>();
    	
    	try{
	        ResultSet rs = stmt.executeQuery("SELECT word FROM master_wordlist;");
	        
	        while(rs.next()){
	            String word = rs.getString("word");
	            words.add(word);
	        }
	        stmt.close();
    	}
	    catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() +" problems retrieving all of the words (master list)");
	    	System.exit(0);}
    	return words;
    }
    public static ArrayList<String> getWords(String category){
    	ArrayList<String> words = new ArrayList<String>();
    	
    	try{
	        ResultSet rs = stmt.executeQuery("SELECT word FROM categories WHERE cat='"+category+"';");
	        
	        while(rs.next()){
	            String word = rs.getString("word");
	            words.add(word);
	        }
	        stmt.close();
    	}
	    catch ( Exception e ) {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems printing the words in the "+ category+" category");
	    	System.exit(0);}
    	return words;
    }

    public static void createTables(){

        try {
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS corpus " +
                        "(docid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + 
                        "docname        CHAR(25), "+
                        "category       CHAR(25));"; 

            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS document " +
                            "(wordid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "word      CHAR(20) NOT NULL,"+
                            "count     INTEGER NOT NULL, " +
                            "fileid    INTEGER NOT NULL, " +
                            "FOREIGN KEY(fileid) REFERENCES corpus(docid) ON DELETE CASCADE);";

            stmt.executeUpdate(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS master_wordlist " +
	            "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
	            "word      CHAR(20) NOT NULL,"+
	            "count     INTEGER NOT NULL);" ;
	        
            stmt.executeUpdate(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS categories " +
                    "(catid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "word      CHAR(20) NOT NULL, "+
                    "count     INTEGER NOT NULL, " +
                    "cat  CHAR(25) NOT NULL);";
            stmt.executeUpdate(sql);
            stmt.close();

        } catch ( Exception e ) {
	          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems creating the tables");
	          System.exit(0);
        }
    }
    public static void addDocument(String name, String category){
        try{
        	ResultSet rs = stmt.executeQuery("SELECT * FROM corpus WHERE docname = '"+ name + "';");
            if(!rs.next()){
                String sql = "INSERT INTO corpus(docname, category) VALUES ('"+name+"', '"+category+"');"; 
                stmt.executeUpdate(sql);
            }
           
            rs.close();
        }catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems adding a document to the corpus.");
          System.exit(0);
        }

    }
    
    public static void addWord(String w, String document){
    	ArrayList<String> stopwords = getStopWords();
    	
        try{
        	ResultSet rs = stmt.executeQuery("SELECT * FROM document, corpus WHERE (docid=fileid) AND (word='"+w+"') AND (docname='"+document+"');");
            if(rs.next()){
                String sql = "UPDATE document set count="+ (rs.getInt("count")+1) +" where wordid="+ rs.getInt("wordid") +";";
                stmt.executeUpdate(sql);
                
            }
            else{
            	if( !stopwords.contains(w)){
             	   Statement ps = c.createStatement();
             	   
             	   rs.getStatement().executeQuery("SELECT * from corpus WHERE docname='"+document+"';");
                    int doc_id = rs.getInt("docid");
                    
                    String word =  w.substring(0, w.length()-1);
                    
                    // is there a singular form of this word in the same document?
                    ResultSet sing_form = ps.executeQuery("SELECT * FROM document WHERE word='"+ word +"' AND fileid='"+doc_id+"';");
                    	
                    if(sing_form.next()){
                 	   addWord(word, document);
                    }
                    else{
                    	 String sql = "INSERT INTO document(word, count, fileid) VALUES ('"+w+"', 1, "+doc_id+");"; //+doc_id +
                         stmt.executeUpdate(sql);
                    }
                    ps.close();
                    sing_form.close();
                }
                  
            }
            rs.close();
        }catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems adding the word "+"\"" + w +"\" to the document word list.");
            System.exit(0);
        }
        /*try{
            ResultSet rs;
            Statement mergingstuff = c.createStatement();
            rs = mergingstuff.executeQuery("SELECT word, count, cat FROM categories;");
            while(rs.next()){
            	String word = rs.getString("word");
            	word =  rs.getString("word").substring(0, str.length()-1);
            	String category = rs.getString("cat");
            	Statement stmt2 = c.createStatement();
            	
            	// is there a singular form of this word in the same category?
                ResultSet plural_form = stmt2.executeQuery("SELECT word, count FROM categories WHERE word='"+ word +"' AND cat='"+category+"';");
            	
                if(sing_form.next()){
                	
                }
                
            }
            rs.close();
        }*/

    }
 // add word to the master list, keeping track of frequency of the word throughout the entire corpus
    // words will (should) be unique
    public static void createMasterList(){
    	
    	clear_words();
    	System.out.println("Creating the master list....");
    	ArrayList<String> stopwords = getStopWords();
    	int x = 0;
        try{
        	//while (x < 20){
    		ResultSet rs = stmt.executeQuery("SELECT word,count FROM categories");
    		Statement stmt2 = c.createStatement();
    		while(rs.next()){
    			// check if the word is already in the msater list
    			String word = rs.getString("word");//.replaceAll("\\W+", " ").replaceAll("\\d+", " ").replaceAll("\\s","").toLowerCase();
    			int count = rs.getInt("count");
    			//System.out.println("Adding "+ word +" from the document table, seen "+count+" times.");
    			
    			ResultSet word_in_master_list = stmt2.executeQuery("SELECT * FROM master_wordlist WHERE word='"+word+"';");
    			if(word_in_master_list.next()){
    				
    				int cummulative_count = word_in_master_list.getInt("count") + count;
    			//	System.out.println("found "+word+" in the master list, seen "+count+" + "+ word_in_master_list.getInt("count") +" = "+ cummulative_count);
    				
    				String sql = "UPDATE master_wordlist set count="+ cummulative_count +" where word='"+word+"';";
	                stmt2.executeUpdate(sql);
    			}
    			else{
    				if( !stopwords.contains(word)){
    					//System.out.println("Didn't find "+word+" in the master list, adding it now with "+count+ " appearances.");
        				
	    				String sql = "INSERT INTO master_wordlist(word, count) VALUES ('"+word+"', "+count+");"; 
	                    stmt2.executeUpdate(sql);
    				}
    			}
    			word_in_master_list.close();
    			//x++;
    			//if(x > 20) break;
    		
        	}
    		rs.close();
    		stmt2.close();
        	//}
        	
    	}catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems adding to the master list.");
            System.exit(0);
        }

    }
    public static void printDocumentsTable(){
    	try{
    		stmt = c.createStatement();
    		ResultSet rs = stmt.executeQuery("SELECT document.word, document.count, corpus.category FROM document JOIN corpus on corpus.docid=document.fileid;");
    		while(rs.next()){
    			System.out.println("found a word "+ rs.getString("word") + " in "+ rs.getString("category")+ "category.");
    		}
    		rs.close();
    	
    	}catch(Exception e){
    		System.out.println("ugh.");
    	}
    }
    public static void createCorpus(){
    	clear_corpus();
    	String debug_stuff = "";
    	
    	try{
    		ResultSet rs = stmt.executeQuery("SELECT document.word, document.count, corpus.category"
    				+" FROM document JOIN corpus on corpus.docid=document.fileid;");
    		Statement stmt2 = c.createStatement();
    		while(rs.next()){
    			// check if the word and category already match a row from the category table
    			String category = rs.getString("category");
    			String word = rs.getString("word");
    			int count = rs.getInt("count");
    			
    			//System.out.println("analyzing "+ word+" seen "+count+" time(s) in "+ category);
    			ResultSet category_row = stmt2.executeQuery("SELECT * from categories WHERE (word='"+word+"') AND (cat='"+category+"')");
    			if(category_row.next()){
    				// we need to increment the instance we found
    				int cummulative_count = rs.getInt("count") + count;
    				debug_stuff +="\nincrementing "+ rs.getInt("count")+" by "+ count+" to get "+ cummulative_count+" for "+ word+" in "+category;
    				
    				
                    String sql = "UPDATE categories set count="+ cummulative_count +" where cat='"+ category +"' AND word='"+word+"';";
                    stmt2.executeUpdate(sql);
    			}
    			else{
    				// let's add this!
                    String sql = "INSERT INTO categories(word, count, cat) VALUES ('"+word+"', "+count+",'"+category+"');"; 
                    stmt2.executeUpdate(sql);
                    debug_stuff +="Added "+word+" to corpus table for the "+category+" category!";
    				
    			}
    			category_row.close();
    			
    		}
    		rs.close();
    		stmt2.close();
    		//System.out.println(debug_stuff);
    		
    	}catch(Exception e){
    		System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems creating corpus.");
            System.exit(0);
    	}
    			
    }
    public static void addToCorpus(String word, String category, int count){
    	
    	try{
    		ResultSet rs = stmt.executeQuery("SELECT * from categories WHERE (word='"+word+"') AND (cat='"+category+"')");
			if(rs.next()){
				// we need to increment the instance we found
				int cummulative_count = rs.getInt("count") + count;
				String sql = "UPDATE categories set count="+ cummulative_count +" where cat='"+ category +"' AND word='"+word+"';";
                stmt.executeUpdate(sql);
			}
			else{
				// let's add this!
                String sql = "INSERT INTO categories(word, count, cat) VALUES ('"+word+"', "+count+",'"+category+"');"; 
                stmt.executeUpdate(sql);
			}
    		rs.close();
    		stmt.close();
    		//System.out.println(debug_stuff);
    		
    	}catch(Exception e){
    		System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems adding to corpus.");
            System.exit(0);
    	}
    			
    }
    
    public static ArrayList<String> getStopWords(){
        File stopwords_file = new File("src/docs/stopwords.txt");
        ArrayList<String> stopwords = new ArrayList<String>();
        
        try(Scanner sc = new Scanner(new FileInputStream(stopwords_file))){
            while(sc.hasNext()){
                stopwords.add(sc.next().replaceAll("\\W+", " ").replaceAll("\\d+", " ").replaceAll("\\s","").toLowerCase());
            }
        }catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems reading file.");
          System.exit(0);
        }
        return stopwords;
    }
    public static void writeToTextFile(String name, String content){
    	try{
            PrintWriter writer = new PrintWriter("src/docs/"+name, "UTF-8");
            writer.println("\nSavanna Endicott- statistical Natural Language Processing");
            writer.println(content);
            writer.close();   
        }
        catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems writing to file.");
          System.exit(0);
        } 
        
    }
    public static void writeDBToFile(){
    	merge_plurals();
        try{
            PrintWriter writer = new PrintWriter("src/docs/dblog.txt", "UTF-8");
            writer.println("\nSavanna Endicott's statistical Natural Language Processing Corpus");
            
            writer.println("\nCategories Table:\n");
            for(int i =0; i < categories.length; i++){
                ResultSet rs = stmt.executeQuery("SELECT * FROM categories WHERE cat ='"+ categories[i]+"' ORDER BY count DESC;");
                writer.println("Category: "+categories[i]);
                while(rs.next()){
                    String time_str = "times";
                    if(rs.getInt("count") == 1) time_str="time";
                    writer.println("  \""+rs.getString("word") +"\" appears " + rs.getInt("count") +" "+ time_str);
                }
                rs.close();
            }
            
        	writer.println("\nDocument Table:\n");
            
            //writer.println("\n\nPRINTING FROM THE DOCUMENT TABLE NOW!!!!\n\n");
            for(int i =0; i < categories.length; i++){
                ResultSet rs = stmt.executeQuery("SELECT word, count, fileid, docname, category FROM corpus, document WHERE (docid=fileid) AND (category ='"+ categories[i]+"');");
                writer.println("\n\n\nWords from "+categories[i]+" category: ");
                while(rs.next()){
                    String time_str = "times";
                    if(rs.getInt("count") == 1) time_str="time";
                    writer.println("  \""+rs.getString("word") +"\" appears " + rs.getInt("count") +" "+ time_str +" in "+rs.getString("docname")+ " from the "+ rs.getString("category")+ " category");
                }
                rs.close();
            }
        	writer.println("\nMaster List of Words:\n");
            
           
            ResultSet rs = stmt.executeQuery("SELECT word, count FROM master_wordlist;");
            while(rs.next()){
                String time_str = "times";
                if(rs.getInt("count") == 1) time_str="time";
                writer.println("  \""+rs.getString("word") +"\" appears " + rs.getInt("count") +" "+ time_str);
            }
            rs.close();
            
            writer.close();   
        }
        catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems printing the words by category.");
          System.exit(0);
        } 
        
    }
    public static void merge_plurals(){
    	/*try{
            ResultSet rs;
            Statement mergingstuff = c.createStatement();
           
            rs = mergingstuff.executeQuery("SELECT word, count, cat FROM categories;");
            while(rs.next()){
            	//System.out.println("made it here!");
                
            	String word = rs.getString("word");
            	String category = rs.getString("cat");
            	//System.out.print("|");
            	Statement stmt2 = c.createStatement();
            	
            	// is there a plural form of this word in the same category?
                ResultSet plural_form = stmt2.executeQuery("SELECT word, count FROM categories WHERE word='"+ word +"s' AND cat='"+category+"';");
                
                
                if(plural_form.next()){
                	int count = plural_form.getInt("count");
                	// remove the plural form, add the word without the s to the corpus
                	//System.out.println("found plural verion of "+ word +" in the "+ category+ " category! Deleting it now...");
                	//String sql = "DELETE FROM categories WHERE word='"+ word +"s' AND cat='"+category+"';";    
                	
                	//stmt3.executeUpdate(sql);
                    
                	//System.out.println("Deleted it! Now updating the singular form.");
                    addToCorpus(word, category, count);
                	//stmt3.close();
                }
                plural_form.close();
                stmt2.close();
                
            }
            rs.close();
        }
        catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems merging plural form to singular form");
          System.exit(0);
        }*/
    }
    public static void delete(String word, String category){
    	try{
    		
    		String sql = "DELETE FROM categories WHERE word='"+ word +"s' AND cat='"+category+"';";    
    		stmt.executeUpdate(sql);
    	
    	}catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems merging plural form to singular form");
            System.exit(0);
          }
    	
    	
        
    }
    
    public static void printWords(){
		System.out.println("\nMaster word list: ");
        try{
            ResultSet rs;
            rs = stmt.executeQuery("SELECT word, count FROM master_wordlist;");
            while(rs.next()){
                String time_str = "times";
                if(rs.getInt("count") == 1) time_str="time";
                System.out.println("  \""+rs.getString("word") +"\" appears " + rs.getInt("count") +" "+ time_str+" in the corpus");
                
            }
        }
        catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems printing the master word list.");
          System.exit(0);
        }
    }
    
    public static void printCorpus(){
        try{
            ResultSet rs;
            for(int i =0; i < categories.length; i++){
                rs = stmt.executeQuery("SELECT * FROM categories WHERE cat ='"+ categories[i]+"';");
                System.out.println("\nWords from "+categories[i]+" category: ");
                while(rs.next()){
                    String time_str = "times";
                    if(rs.getInt("count") == 1) time_str="time";
                    System.out.println("  \""+rs.getString("word") +"\" appears " + rs.getInt("count") +" "+ time_str);
                }
            }
        }
        catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems printing the final corpus.");
          System.exit(0);
        }
    }

    public static void printWordsByCategory(){
        try{
            ResultSet rs;
            System.out.println(categories.length);
            for(int i =0; i < categories.length; i++){
                rs = stmt.executeQuery("SELECT word, count, fileid, docname, category FROM corpus, document WHERE (docid=fileid) AND (category ='"+ categories[i]+"');");
                System.out.println("\n\n\nWords from "+categories[i]+" category: ");
                while(rs.next()){
                    String time_str = "times";
                    if(rs.getInt("count") == 1) time_str="time";
                    System.out.println("  \""+rs.getString("word") +"\" appears " + rs.getInt("count") +" "+ time_str +" in "+rs.getString("docname")+ " from the "+ rs.getString("category")+ " category");
                }
            }

            
        }
        catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems printing the words by category.");
          System.exit(0);
        }
    }

    public static void printDocuments(){
        try{
            ResultSet rs = stmt.executeQuery("SELECT * FROM corpus;");
            System.out.println("\nDocs: ");
            while(rs.next())
                System.out.println("  "+rs.getString("docname") + " from "+rs.getString("category"));

        }catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems printing the documents");
          System.exit(0);
        }
    }
    
    public static void printWordsInCategory(String category){
	    try{
	        ResultSet rs;
	        rs = stmt.executeQuery("SELECT word, count, fileid, docname, category FROM corpus, document WHERE (docid=fileid) AND (category ='"+ category+"');");
	        System.out.println("\nWords from "+category+" category: ");
	        while(rs.next()){
	            String time_str = "times";
	            if(rs.getInt("count") == 1) time_str="time";
	            System.out.println("  \""+rs.getString("word") +"\" appears " + rs.getInt("count") +" "+ time_str + " in "+rs.getString("docname")+ " from the "+ rs.getString("category")+ " category");
	        }
	    }
	    catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems printing the words in the "+ category+" category");
	      System.exit(0);
	    }
    }

    public static void clear_words_by_document(){
        try{
            stmt = c.createStatement();
            System.out.println("Clearing the document table...");
            String sql = "delete from document;";    
            stmt.executeUpdate(sql);
        }catch ( Exception e ) {
              System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems deleting words.");
              System.exit(0);
        }
    }

    public static void clear_words(){
        try{
            stmt = c.createStatement();
            System.out.println("Clearing the master list...");
            String sql = "delete from master_wordlist;";    
            stmt.executeUpdate(sql);
        }catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems clearing master list.");
            System.exit(0);
        }
    }
    
    public static void clear_documents(){
        try{
            stmt = c.createStatement();
            System.out.println("\nClearing the corpus of all documents...");
            String sql = "delete from corpus;";    
            stmt.executeUpdate(sql);
        }catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems deleting documents.");
            System.exit(0);
        }
    }
    
    public static void clear_corpus(){
        try{
            //stmt.close();
        	//c.close();
        	//c = DriverManager.getConnection("jdbc:sqlite:mycorpus.db;foreign keys=true;");
        	stmt = c.createStatement();
            System.out.println("Clearing categories table");
            String sql = "delete from categories;";    
            stmt.executeUpdate(sql);
        }catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems clearing corpus");
            System.exit(0);
        }
    }
    
    
    
    public static void delete_word(String w){
        try{
            stmt = c.createStatement();
            System.out.println("Removing word "+ w +" from corpus...");
            String sql = "delete from document where word = '"+ w+ "';";    
            stmt.executeUpdate(sql);
        }
        catch ( Exception e ) {
              System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems deleting \""+ w+"\"");
              System.exit(0);
        }
    }
    public static void delete_stopwords(){
    	ArrayList<String> stopwords = getStopWords();
    	try{
    		for(int i =0; i < stopwords.size(); i++){
	            stmt = c.createStatement();
	            //System.out.println("Removing word "+ stopwords.get(i) +" from corpus...");
	            String sql = "delete from master_wordlist where word = '"+ stopwords.get(i) +"';";    
	            stmt.executeUpdate(sql);
    		}
        }
        catch ( Exception e ) {
              System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems deleting stopwords");
              System.exit(0);
        }
    	
    }

    public static void delete_document(String d){
        try{
            stmt = c.createStatement();
            System.out.println("Removing document "+ d +" from corpus...");
            String sql = "delete from document where docname == '"+ d+ "';";    
            stmt.executeUpdate(sql);
        }
        catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems deleting.");
          System.exit(0);
        }
    }
    public static void start_fresh(){
        clear_documents();
        clear_words();
        clear_corpus();
    }

}