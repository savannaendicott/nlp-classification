Project name  : nlp-classification
Author        : Savanna Endicott
Stu#          : 100933116
Date          : April 16th, 2017

Description:
This project was used as my independent final project in COMP4106 - Artificial Intelligence at Carleton University. 
It's purpose is to compare Naive bayes and Bag of Words classification techniques using a corpus of language from CBC.ca: 
news articles from the following categories: Sports, Entertainment, World News, and Travel.

Usage: 
1.	Download from https://github.com/savannaendicott/nlp-classification
2.	Import project to Eclipse
      Only way I can seem to get the SQLite jar file to sync properly
      You will need to import existing project into eclipse (file->import->General->Existing Projects Into Workspace)
      In the files from the project manager on the left you will see the sqlite-jdbc-3.8.11.2.jar file for SQLite. Right click this and click build-> add to build path
3.	Run (there is only one main – in naivebayes.java)
4.	Follow the menu options
      - Select Naïve Bayes or Bag of words
      - Select testing word set for the classification
          A file from the corpus, a set of words input by the user on the go, or a sample tester document I’ve uploaded to the project.
      - Select files if appropriate (will be prompted by menu)
      - Classification results are returned to the screen as is
      *IMPORTANT: LOG OF ALL STEPS OF THE PROCEDURE ARE FORMATTED AND PRINTED TO A LOG FILE FOR EACH CLASSIFICATION *
      - The log file’s name will be printed in the menu along with the result
          Format “/src/docs/<classification-type>/<date/time>.txt”
5.	At the end of the program, type anything and hit enter to restart.

Important Files:
CorpusDB.java
Corpus.java
naivebayes.java
bagOfWords.java

Note:
The text files in this project are important - used to create the corpus of information. These cannot be removed without consequences.
