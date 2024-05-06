package QueryProcessing;

import org.bson.Document;

public class Pair implements Comparable{
    Document document;
    double geometricMean;
    public Pair(Document document , double geometricMean){
        this.document = document;
        this.geometricMean = geometricMean;
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(this.geometricMean, ((Pair) o).geometricMean);
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public double getGeometricMean() {
        return geometricMean;
    }

    public void setGeometricMean(double geometricMean) {
        this.geometricMean = geometricMean;
    }
}