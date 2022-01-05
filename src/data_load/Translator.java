package data_load;


import static constants.Constants.*;

public class Translator {

    private final double minLat;
    private final double maxLat;
    private final double minLon;
    private final double maxLon;
    private final double transLon;
    private final double transLat;

    /*
    minLat = max latitude
    maxLat = max latitude

    transLat = latitude transposition(maxLat-minLat)
    transLon = longitude transposition(maxLon-minLon)

     minLon = min longitude
     maxLon = max longitude
     */

    public Translator(double minLat, double maxLat, double minLon, double maxLon) {
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLon = minLon;
        this.maxLon = maxLon;

        this.transLat = this.maxLat - this.minLat;
        this.transLon = this.maxLon - this.minLon;
    }

    public int getLat(double latitude) {
        return (int) (WINDOW_WIDTH / (transLat / (latitude - minLat)));
    }

    public int getLon(double longitude) {
        return (int) (WINDOW_HEIGHT / (transLon / (longitude - minLon)));
    }

}
