import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
public class naivebayes {

	String log_content="";
	String log_file;
	String[] cats;
	double[] prob_cat;
	Corpus corpus;
	ArrayList<String> words;
	String document = "";
	double[] posterior_prioris;
	//String mathstuff;
	
	// word counts
	int[][] training;
	
	/*public naivebayes(String[] categories, String document){
		this.corpus = new Corpus();
		
		this.words = new ArrayList<String>();
		this.cats = categories;
		this.prob_cat = new double[cats.length];
		
		// CALL AN OPERATION TO THE CORPUS TO GET ALL THE WORDS IN THE DOCUMENT
		
	}*/
	public naivebayes(String[] categories, String[] to_classify){
		
		this.corpus = new Corpus();
		CorpusDB.merge_plurals();
		
		//CorpusDB.printCorpus();
		this.words = new ArrayList<String>();
		this.cats = categories;
		this.prob_cat = new double[cats.length];
		this.posterior_prioris = new double[cats.length];
		
		for(int i = 0; i < to_classify.length; i++){
			this.words.add(to_classify[i]);
		}
		this.training = new int[cats.length][words.size()];
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date date = new Date();
		this.log_file = "src/docs/naivebayes-logs/"+dateFormat.format(date)+".txt";
		
		
		init_probabilities();
		classify();
		writeToLogFile();
	}
	public naivebayes(String[] categories, String document){
		this.document = document;
		this.corpus = new Corpus();
		CorpusDB.merge_plurals();
		
		//CorpusDB.printCorpus();
		this.words = new ArrayList<String>();
		this.cats = categories;
		this.prob_cat = new double[cats.length];
		this.posterior_prioris = new double[cats.length];
		
		
		ArrayList<String> to_classify = CorpusDB.getWordsInDoc(document, 100);
		for(int i = 0; i < to_classify.size(); i++){
			words.add(to_classify.get(i));
		}
		this.training = new int[cats.length][words.size()];
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date date = new Date();
		this.log_file = "src/docs/naivebayes-logs/"+dateFormat.format(date)+".txt";
		
		init_probabilities();
		classify();
		writeToLogFile();
	}
	public naivebayes(String[] categories, ArrayList<String> to_classify){
		this.corpus = new Corpus();
		CorpusDB.merge_plurals();
		
		//CorpusDB.printCorpus();
		this.words = new ArrayList<String>();
		this.cats = categories;
		this.prob_cat = new double[cats.length];
		this.posterior_prioris = new double[cats.length];
		
		
		for(int i = 0; i < to_classify.size(); i++){
			words.add(to_classify.get(i));
		}
		this.training = new int[cats.length][words.size()];
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date date = new Date();
		this.log_file = "src/docs/naivebayes-logs/"+dateFormat.format(date)+".txt";
		
		init_probabilities();
		classify();
		writeToLogFile();
	}
	private void classify(){
		train();
		no_docs_in_cats();
		word_counts();
		word_likelyhoods();
		calculate_posterior_prioris();
		//complete_classification();
		
	}
	private void init_probabilities(){
		for(int i =0; i < prob_cat.length; i++){
			prob_cat[i] = CorpusDB.getNumDocs(cats[i]);
		}
	}
	
	private void train(){
		for(int i=0; i < cats.length; i++){
			for(int j =0; j < words.size();j++){
				training[i][j] = CorpusDB.getOccurencesInCat(words.get(j), cats[i]);
				if(training[i][j] == -1 ) training[i][j] =0;
			}
		}
	}
	
	private void no_docs_in_cats(){
		
		for(int x = 0; x < prob_cat.length; x++){
			log_content+= "\nNum docs in the "+cats[x]+" category : " +prob_cat[x];
			
		}
	}
	private void word_counts(){
		int col = 0;
		for(int i =0; i < cats.length; i++){
			log_content += "\n\nCategory: "+ cats[i]+"\n   ";
			for(int j =0; j < words.size(); j++){
				if(training[i][j] == -1)
					log_content += String.format("%-20s",words.get(j)+":      ");
				else log_content += String.format("%-20s",words.get(j)+":"+training[i][j]+"      ");
				col ++;
				if(col==4){ log_content += "\n   "; col =0;}
				
			}
		}
	}
	private void word_likelyhoods(){
		log_content += ("\n\nWord likely-hoods: ");
		for(int i=0; i < cats.length; i++){
			for(int j =0; j < words.size();j++){
				double result;
				if(training[i][j] == 0) result = 0;
				else result = (double)training[i][j]/prob_cat[i];
				log_content += ("\n");
				log_content += String.format("%-30s","   P("+words.get(j)+" | "+cats[i]+") : ");//+" = "+ result);
				log_content += training[i][j]+"/"+prob_cat[i] ;
			}
			log_content +=("\n");
		}
		log_content +=("\n\n");
	}
	private void calculate_posterior_prioris(){
		log_content += "\n\nPOSTERIOR PRIORI FOR EACH CATEGORY\n\n";
		String content="Stuff: ";
		log_content += "   Note: if a word does not appear in a category, the 0/x is instead represented as 1 here, to avoid multiplication by 0.";
		for(int i=0; i < cats.length; i++){
			//content+="/n category: "+cats[i];
			long denom_accum=1;
			long nom_accum=1;
			if(this.document == "") log_content += String.format("%-60s","   P("+cats[i]+" | "+words.toString()+") \n= ");
			else log_content += String.format("%-60s","\n\nP("+cats[i]+" | words from "+document+") \n= ");
			
			log_content += CorpusDB.getNumDocs(cats[i])+"/"+CorpusDB.getNumDocs()+"    x    (";
			nom_accum*=CorpusDB.getNumDocs(cats[i]);
			denom_accum*=CorpusDB.getNumDocs();
			for(int j =0; j < words.size()-1;j++){
				if(training[i][j] != 0){ //log_content += String.format("%-7s"," (1)");
				//else {
					log_content += String.format("%-7s"," "+training[i][j]+"/"+(int)prob_cat[i] +"");
					log_content +=" x ";
					nom_accum*= training[i][j];
					content+=  training[i][j]+" * ";
					denom_accum*=(int)prob_cat[i];
					}
				
				
			}
			if(training[i][words.size()-1] == 0) log_content += String.format("%-7s"," (1)");
			else {
				log_content += String.format("%-7s"," ("+training[i][words.size()-1]+"/"+(int)prob_cat[i] +")");
				nom_accum*= training[i][words.size()-1];
				
				denom_accum*=(int)prob_cat[i];
				}
			
			double result = Math.abs((double)nom_accum / (double)denom_accum);
			log_content += ")     \n=     "+Math.abs(nom_accum)+" / "+ Math.abs(denom_accum)+"    =    "+ result+ "\n";
			this.posterior_prioris[i] = result;
		}
		log_content += "\n\n";
		
	}
	private String complete_classification(){
		int max = 0;
	    for (int i = 0; i < posterior_prioris.length; i++) {
	        if (posterior_prioris[i] > posterior_prioris[max]) {
	            max = i;
	        }
	    }
	    log_content +="\n\nClass resulting form naive bayes classification for 100 words from "+ document+" is:";
	    log_content += "\n" +cats[max];
	    return cats[max];
	}
	
	public String getResults(){
		String results = "";
		//compute_category_vectors();
		String winning_class = complete_classification();
		results+="\n\n"+ "-----------------------------------------------\n"+
				"Naive bayes classification complete! \n"+
				"-----------------------------------------------\n"+
				"The category classified for your selection is...\n\n"+winning_class+
				"!\n\n... A full log of the classification process can be found here:\n"+this.log_file;
		return results;
	}
	
	private void writeToLogFile(){
		try{
			DateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			
            PrintWriter writer = new PrintWriter(this.log_file, "UTF-8");
            
            writer.println("\nSavanna Endicott- statistical Natural Language Processing");
            writer.println("\nLog for naive bayes classification attempt on: "+dateTimeFormat.format(date)); 
			
            writer.println(log_content);
            writer.close();   
        }
        catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems writing to file.");
          System.exit(0);
        } 
	}
	
	public static void run()
  	{
		
		String[] words = {"set","around","hike","hotel","just"};
		String[] filenames = {
				"src/docs/sports-article1.txt","src/docs/sports-article2.txt","src/docs/sports-article3.txt",
				"src/docs/travel-article1.txt","src/docs/travel-article2.txt","src/docs/travel-article3.txt",
	  			"src/docs/entertainment-article1.txt","src/docs/entertainment-article2.txt","src/docs/entertainment-article3.txt",
	  			"src/docs/world-article1.txt", "src/docs/world-article2.txt", "src/docs/world-article3.txt"
	  		};
		String[] files_not_in_corpus = {
				"src/docs/tester-sports.txt","src/docs/tester-travel.txt","src/docs/tester-entertainment.txt","src/docs/tester-world.txt"
	  		};
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		String main_menu= "-----------------------------------------------\n"+
				"Statistical Natural Language Processing System\n"+
				"-----------------------------------------------\n"+
				"\nAuthor: Savanna Endicott"+
				"\nCOMP 4106 Artifical Intelligence Final Project"+
				"\n\nSystem contents:\n"+
				"4 Categories of news articles from CBC.ca : Sports, Travel, World News, and Entertainment\n\n"+
				"3 documents in each category as follows:\n"+
				Corpus.categories[0]+": "+filenames[0]+", "+filenames[1]+", and "+filenames[2]+"\n"+
				Corpus.categories[1]+": "+filenames[3]+", "+filenames[4]+", and "+filenames[5]+"\n"+
				Corpus.categories[2]+": "+filenames[6]+", "+filenames[7]+", and "+filenames[8]+"\n"+
				Corpus.categories[3]+": "+filenames[9]+", "+filenames[10]+", and "+filenames[11]+"\n"+
				"\n\n\nUsage Options:\n   1. Bag of Words Classification\n   2. Naive Bayes Classification\n\nEnter your choice (1 or 2) : ";
		String nb = "\n\nYou've selected naive bayes classification!";
		String bow = "\n\nYou've selected bag of words classification!";
		String classification_menu = "\n\nYou have the following options:\n   1. Classify a set of words\n   2. Classify a document from the corpus\n   3. Classify a document not in the corpus\n\nEnter your choice now : "; 
		
		
		
		
		System.out.println(main_menu);
		int n = reader.nextInt();
		while(true){
			if(n==1){
				System.out.println(bow + classification_menu);
				break;
			}else if(n==2){
				System.out.println(nb + classification_menu);
				break;
			}
			else{
				System.out.println("please try again! Enter 1 for bag of words classification, or 2 for naive bayes classification.");
				n = reader.nextInt();
				continue;
			}
		}
		
		//CorpusDB.close();
		
		int n2 = reader.nextInt();
		if(n2==1){
			ArrayList<String> to_classify = new ArrayList<String>();
			String word = "";
			while(!word.equals("stop")){
				System.out.println("Enter a new word or \"stop\"");
				word  = reader.next();
				to_classify.add(word);
			}
			if(to_classify.size()==0) System.out.println("Sorry, no content entered.");
			else{
				String[] wordlist_toarr = new String[to_classify.size()];
				for(int i =0; i < to_classify.size(); i++){
					wordlist_toarr[i] = to_classify.get(i);
				}
				System.out.println("\n... hold on ...\n\n");
				if(n==1) {
					bagOfWords bw = new bagOfWords(Corpus.categories,wordlist_toarr);
					System.out.println(bw.getResults());
				}else if (n==2) {
					naivebayes newnb = new naivebayes(Corpus.categories, wordlist_toarr);
					System.out.println(newnb.getResults());
				}
			}
			
		}
		else if(n2==2){
			System.out.println("Which file would you like to classify? Select from the following.\n"+
					"   1. sports-article1.txt\n   2. sports-article2.txt\n   3. sports-article3.txt\n"+
				"   4. travel-article1.txt\n   5. travel-article2.txt\n   6. travel-article3.txt\n"+
	  			"   7. entertainment-article1.txt/n   8. entertainment-article2.txt\n   9. entertainment-article3.txt\n"+
	  			"   10. world-article1.txt\n   11. world-article2.txt\n   12. world-article3.txt"+
				"\n\nPlease enter your choice: ");
				int n3 = reader.nextInt();
				System.out.println("\n... hold on ...\n\n");
				if(n3> 0 && n3 < 13){
					if(n==1) {
						bagOfWords bw = new bagOfWords(Corpus.categories,filenames[n3-1]);
						System.out.println(bw.getResults());
					}else if (n==2) {
						naivebayes newnb = new naivebayes(Corpus.categories, filenames[n3-1]);
						System.out.println(newnb.getResults());
					}
				}
		}
		else if(n2==3){
			System.out.println("Which category would you like to classify? Select from the following.\n\n   1. "+ Corpus.categories[0]+
					   "   2. "+ Corpus.categories[1]+"   3. "+Corpus.categories[2]+"   4. "+Corpus.categories[3]+"\n\n" +
						"\n\nPlease enter your choice: ");
			int n3 = reader.nextInt();
			System.out.println("\n... hold on ...\n\n");
			if(n3> 0 && n3 < 4){
				if(n==1) {
					
					bagOfWords bw = new bagOfWords(Corpus.categories,Corpus.prepare_file(files_not_in_corpus[n3-1]));
					System.out.println(bw.getResults());
				}else if (n==2) {
					naivebayes newnb = new naivebayes(Corpus.categories, Corpus.prepare_file(files_not_in_corpus[n3-1]));
					System.out.println(newnb.getResults());
				}
			}
					
			
		}
		else{
			System.out.println("Your input was incorrect, Sorry. Returning to main menu now.");
		}
		
		
		
  	}
	
	public static void main( String args[] )
  	{
		while(true){
			run();
			System.out.println("...continue?...");
			Scanner reader = new Scanner(System.in);  // Reading from System.in
			reader.next();
			
			System.out.println("\n\n\n\n\n\n\n\n");
		}
		
  	}
}
