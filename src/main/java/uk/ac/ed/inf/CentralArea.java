package uk.ac.ed.inf;

public class CentralArea {
    private static CentralArea instance;
    public static double[] lng;
    public static double[] lat;
    private CentralArea()
    {
        //defines the bounds of central area
        lng = new double[2];
        lng[0] = -3.192473;
        lng[1] = -3.184319;

        lat = new double[2];
        lat[0] = 55.946233;
        lat[1] = 55.942617;
    }
    synchronized public static CentralArea getInstance()
    {
        if (instance == null)
        {
            instance = new CentralArea();
        }
        return instance;
    }
}
