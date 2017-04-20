import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class bagOfWords {
	public static final String ANSI_WHITE = "\u001B[37m";
	String[] cats;
	String log_content;
	Corpus corpus;
	Map<String, Integer> words;
	String log_file;
	int[][] cat_vectors;
	
	public bagOfWords(String[] categories, String[] to_classify){
		this.corpus = new Corpus();
		//CorpusDB.close();
		//this.corpus = new Corpus();
		//CorpusDB.connect();
		//CorpusDB.createTables();
		CorpusDB.merge_plurals();
		
		this.words = new HashMap<String,Integer>();
		this.cats = categories;
		
		for(int i = 0; i < to_classify.length; i++){
			Integer freq = words.get(to_classify[i]);
			words.put(to_classify[i], (freq == null) ? 1 : freq +1);
		}
		this.cat_vectors = new int[cats.length][words.size()];
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date date = new Date();
		this.log_file = "src/docs/bagofwords-logs/"+dateFormat.format(date)+".txt";
		
		
		compute_category_vectors();
		test();
		writeToLogFile();
	}
	
	public bagOfWords(String[] categories, String document){
		this.corpus = new Corpus();
		CorpusDB.merge_plurals();
		
		this.words = new HashMap<String,Integer>();
		this.cats = categories;
		ArrayList<String> to_classify = CorpusDB.getWordsInDoc(document, 100);
		
		for(int i = 0; i < to_classify.size(); i++){
			Integer freq = words.get(to_classify.get(i));
			words.put(to_classify.get(i), (freq == null) ? 1 : freq +1);
		}
		this.cat_vectors = new int[cats.length][words.size()];
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date date = new Date();
		this.log_file = "src/docs/bagofwords-logs/"+dateFormat.format(date)+".txt";
		
		compute_category_vectors();
		test();
		writeToLogFile();
		
		
	}
	
	public bagOfWords(String[] categories, ArrayList<String> to_classify){
		this.corpus = new Corpus();
		CorpusDB.merge_plurals();
		
		this.words = new HashMap<String,Integer>();
		this.cats = categories;
		
		for(int i = 0; i < to_classify.size(); i++){
			Integer freq = words.get(to_classify.get(i));
			words.put(to_classify.get(i), (freq == null) ? 1 : freq +1);
		}
		this.cat_vectors = new int[cats.length][words.size()];
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date date = new Date();
		this.log_file = "src/docs/bagofwords-logs/"+dateFormat.format(date)+".txt";
		
		compute_category_vectors();
		test();
		writeToLogFile();
		
		
	}
	
	private void compute_category_vectors(){
		for(int i=0; i < cats.length; i++){
			log_content +="\n\nVector for category: "+ cats[i]+"\n";
			int j = 0;
			for (String word : words.keySet()){
				cat_vectors[i][j] = CorpusDB.getOccurencesInCat(word, cats[i]);
				if(cat_vectors[i][j] == -1 ) cat_vectors[i][j] =0;
				log_content += String.format("%-10s", "   "+word+" : "+cat_vectors[i][j]+"\n");
						
				j++;
			}
		}
		for(int x = 0 ; x < cats.length; x++){
			log_content += "\n\nVector for "+ cats[x]+" : [";
			for(int y =0; y < words.keySet().size()-1; y++){
				log_content+= cat_vectors[x][y]+", ";
			}
			log_content+= cat_vectors[x][words.keySet().size()-1]+" ]";
		}
		
	}
	
	private String test(){
		double[] similarities = new double[cats.length];
		log_content +="\n\nComputing Cosine Similarities\n\n";
		int max =0;
		for(int i =0; i < cats.length; i++){
			log_content+="\n\nSimilarity for "+cats[i]+"...";
			similarities[i] = computeCosineSimilarity(i);
			if(similarities[i] > similarities[max]) {
				max =i;
				log_content +="\n   category "+cats[i]+" has a cosine similarity of " + similarities[i]+" which is larger than the current maximum "+similarities[max]+"\n"+cats[i]+" now has the maximum cosine similarity.";
						
			}
			else{
				log_content +="\n   category "+cats[i]+" has a cosine similarity of " + similarities[i]+" which is smaller than the current maximum "+similarities[max];
				
			}
		}
	    //System.out.println("\nClass resulting form bag of words classification for the given wordset is:");
	    log_content +="\nThe class found through bag of words is "+ cats[max]+"\n";
		return cats[max];
	
	}
	private double computeCosineSimilarity(int category){
		int nom = 0;
		int denom_r = 0;
		int denom_l = 0;
		double denom = 0;
		int counter =0;
		
		int counter2 =0;
		
		String nomstr="";
		String denomstr="";
		String denom_lstr="";
		String denom_rstr="";
		
		for (int i =0; i < words.keySet().size(); i++){
			if(cat_vectors[category][i] !=0){
				counter2++;
				nom += 1 * cat_vectors[category][i]; 
				nomstr+=" (1 * "+cat_vectors[category][i]+") +";
				denom_r += (cat_vectors[category][i])^ 2;
				denom_rstr= "("+cat_vectors[category][i]+"^2) +";
				denom_l += 1;
				denom_lstr +="(1)^2) + ";
			}
			
		}
		nomstr+= "0";
		denom_rstr +="0";
		denom_lstr +="0";
		
		if(counter2 ==0){
			log_content+="\nThere are no words that occur from classification sample, so the similarity is 0.\n";
			
			return 0;
		}
		
		denom = Math.sqrt((double)denom_l) * Math.sqrt((double)denom_r);
		log_content+="\nThere are "+ counter2+" words that occur from classification sample\n";
		log_content+= "\n   "+nomstr+"\n/   "+"("+denom_lstr+") * ("+denom_rstr+")";
		if(counter2 == 1) return 0.25;
		log_content+="\n\n= "+ Math.sqrt((double)nom)+" / "+ denom+"\n\n=" +Math.sqrt((double)nom) / (double)denom;
		return Math.sqrt((double)nom) / (double)denom;
		
	}
	public String getResults(){
		String results = "";
		compute_category_vectors();
		String winning_class = test();
		results+="-----------------------------------------------\n"+
				"Bag of words classification complete! \n"+
				"-----------------------------------------------\n"+
				"The category classified for your selection is...\n\n"+winning_class+
				"!\n\nA full log of the classification process can be found here:\n"+this.log_file;
		return results;
	}
	
	private void writeToLogFile(){
		try{
			DateFormat dateTimeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			
			Date date = new Date();
			
            PrintWriter writer = new PrintWriter(log_file, "UTF-8");
            writer.println("\nSavanna Endicott- statistical Natural Language Processing");
            writer.println("\nLog for bag of words classification attempt on: "+dateTimeFormat.format(date)); 
			
            writer.println(log_content);
            writer.close();   
        }
        catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems writing to file.");
          System.exit(0);
        } 
	}
	
}

	
	
	
	

