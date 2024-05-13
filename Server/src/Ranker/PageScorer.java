package Ranker;

import java.util.ArrayList;

import static java.lang.Math.log;

public class PageScorer {

    public static long N;
    private final static double HEADER_FACTOR = 1.2;
    private final static double TITLE_FACTOR = 1.1;

    public ArrayList<Integer[]> TF;
    public ArrayList<Integer> DF;
    public Double pageRank;
    public Double score;
    public ArrayList<String> words;
    public PageScorer() {
        words = new ArrayList<>();
        DF = new ArrayList<>();;
        pageRank = 1.0;
        score = 1.0;
        TF = new ArrayList<>();
    }

    public void addWord (String word) {
        words.add(word);
    }

    public void addTF (Integer[] TF) {
        this.TF.add(TF);
    }

    public void addDF (Integer DF) {
        this.DF.add(DF);
    }

    public double updateScore() {
        score = (double) 0;
        for (int i = 0; i < DF.size(); i++) {
            score += log((double) N / DF.get(i)) *
                    (TF.get(i)[0] * HEADER_FACTOR + TF.get(i)[1] * TITLE_FACTOR + TF.get(i)[2]);
        }
        return score;
    }

    public Double finalScore() {
        score = updateScore() * pageRank;
        return score;
    }

    public void append(PageScorer currScorer) {
        this.TF.addAll(currScorer.TF);
        this.DF.addAll(currScorer.DF);
        this.words.addAll(currScorer.words);
    }
}