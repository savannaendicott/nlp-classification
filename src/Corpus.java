import java.util.*;
import java.io.*;

public class Corpus{

	File[] files;
	String[] filenames = {
			"src/docs/sports-article1.txt","src/docs/sports-article2.txt","src/docs/sports-article3.txt",
			"src/docs/travel-article1.txt","src/docs/travel-article2.txt","src/docs/travel-article3.txt",
  			"src/docs/entertainment-article1.txt","src/docs/entertainment-article2.txt","src/docs/entertainment-article3.txt",
  			"src/docs/world-article1.txt", "src/docs/world-article2.txt", "src/docs/world-article3.txt"
  		};
	static String[] categories = {"Sports","Travel","Entertainment","World News"};
	int catsize = 3;
	
	public Corpus(){
		this.files = new File[filenames.length];
		CorpusDB.connect();
        CorpusDB.createTables();
	}
	
	public static ArrayList<String> prepare_file(String filename){
		ArrayList<String> stopwords = CorpusDB.getStopWords();
		File stopwords_file = new File(filename);
        ArrayList<String> words = new ArrayList<String>();
        
        try(Scanner sc = new Scanner(new FileInputStream(stopwords_file))){
            while(sc.hasNext()){
                String word = sc.next().replaceAll("\\W+", " ").replaceAll("\\d+", " ").replaceAll("\\s","").toLowerCase();
                if(!stopwords.contains(word) && !words.contains(word)) words.add(word);
            }
            sc.close();
        }catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems reading file.");
          System.exit(0);
        }
        return words;
		
	}
	
	private void writeToDB(){
		CorpusDB.writeDBToFile();
	}
	private void printStuff(){
		CorpusDB.printCorpus();
		CorpusDB.printDocuments();
		
	}
	private ArrayList<String> getWords(){
		ArrayList<String> words = CorpusDB.getWords();
		return words;
	}
	
	public void bag_of_words(){
		ArrayList<String> words = CorpusDB.getWords();
		System.out.print("\n\nBag of Words for first 10 in the corpus: (category vs. word) \n\n");
		
		int[] random_locations = uniqueRandomInts(10, words.size());
		
		
		System.out.printf("%-14s","  ");
		for(int i=0; i < random_locations.length; i ++){
			System.out.printf("%-14s", words.get(random_locations[i]));
		}
		for(int c=0; c< categories.length; c++){
			System.out.printf("\n%-14s",categories[c]);
			for(int n=0; n<10;n++){
				int occurences = CorpusDB.getOccurencesInCat(words.get(random_locations[n]), categories[c]);
				if(occurences ==-1) occurences =0;
				System.out.printf("%-14s", occurences);
			}
		}
		System.out.println("\n");
	}
	public static int[] uniqueRandomInts(int amount, int size) {

	    int[] a = new int[amount];
	    System.out.println(size);
	    for (int i = 0; i < amount; i++) {
	        a[i] = (int)(Math.random()*size);

	        for (int j = 0; j < i; j++) {
	            if (a[i] == a[j]) {
	                i--; //if a[i] is a duplicate of a[j], then run the outer loop on i again
	                break;
	            }
	        }  
	    }

	    for (int i = 0; i < a.length; i++) {
	        System.out.print(a[i]+" ");
	    }
	    System.out.println();
	    return a;
	}

	public Corpus(String[] f){
		this.files = new File[f.length];
		this.filenames = f;
		
		CorpusDB.connect();
        CorpusDB.createTables();
        addFiles();
	}

	public void addFiles(){
		String[] f = this.filenames;
		 System.out.println("Adding words to the corpus...");
		CorpusDB.start_fresh();
        int category = 0;

		for (int i =0; i < f.length; i++){
			this.files[i] = new File(f[i]);
			//System.out.println(");
			CorpusDB.addDocument(f[i],categories[category]);
			System.out.println("Added document "+ f[i]+" to "+ categories[category]);
			
			try(Scanner sc = new Scanner(new FileInputStream(f[i]))){
			    while(sc.hasNext()){
			    	// TAKES ALL NON ALPHA CHARACTERS OUT OF THE STRING AND MAKES IT LOWER CASE!!!
			        CorpusDB.addWord(sc.next().replaceAll("\\W+", " ").replaceAll("\\d+", " ").replaceAll("\\s","").toLowerCase(), f[i]);
			        
			    }
				//System.out.println("Number of words: " + count);
			}catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() +". Problems excluding word.");
		      System.exit(0);
		    }
		    if((i+1)%3 == 0){
		    	category++;
		    }
		}

	}
	public void setup(){
		CorpusDB.connect();
		CorpusDB.createTables();
	}
}