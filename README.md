Project name  : nlp-classification
Author        : Savanna Endicott
Date          : April 16th, 2017

Description:
This project was used as my final project for COMP4106 - Artificial Intelligence at Carleton University. 
It's purpose is to compare Naive bayes and Bag of Words classification techniques using a corpus of language from news articles from the following categories: Sports, Entertainment, World News, and Travel. All of these articles are from CBC.ca.

Usage: 
1.	Download from https://github.com/savannaendicott/nlp-classification
2.	Import project to Eclipse
      - You will need to import existing project into eclipse (file->import->General->Existing Projects Into Workspace)
      - In the files from the project manager on the left you will see the sqlite-jdbc-3.8.11.2.jar file for SQLite. Right click this and click build-> add to build path
3.	Run (there is only one main – in naivebayes.java)
4.	Follow the menu options
      - Select classification method (Naïve Bayes or Bag of Words)
      - Select testing set for the classification
          A file from the corpus, a set of words input by the user on the go, or a sample test document I’ve uploaded to the project.
      - Select files if appropriate (will be prompted by menu)
      - Classification results are returned to the screen as is
      *IMPORTANT: LOG OF ALL STEPS OF THE PROCEDURE ARE FORMATTED AND PRINTED TO A LOG FILE FOR EACH CLASSIFICATION *
      - The log file’s name will be printed in the menu along with the result
          Format “/src/docs/<classification-type>/<date/time>.txt”
5.	At the end of the program, type anything and hit enter to restart.

Important Files:
- CorpusDB.java
- Corpus.java
- naivebayes.java
- bagOfWords.java

Note:
The text files in this project are important - used to create the corpus of information. These cannot be removed without consequences.

References:
- Standford CoreNLP list of stopwords   
  https://github.com/stanfordnlp/CoreNLP/blob/master/data/edu/stanford/nlp/patterns/surface/stopwords.txt

- Shimodaira, Hiroshi,  “Text Classification using Naïve Bayes”, Informatics 2B(2015)
  http://www.inf.ed.ac.uk/teaching/courses/inf2b/learnnotes/inf2b-learn-note07-2up.pdf

- B. J. Oommen, R. Khoury and A. Schmidt, “Text Classification Using Novel Anti-Bayesian Techniques”, ICCCI-7 (2015).

- D. Shen, R. Pan, J.-T. Sun, J.J. Pan, K. Wu, J. Yin, and Q. Yang. “Q2C@UST: our winning solution to query classification in KDDCUP      2005”, ACM SIGKDD, vol. 7, no. 2, pp. 100-110 (2005).
