package uk.ac.ed.inf;

public record LngLat(double lng, double lat){

    public boolean inCentralArea(){
        return false;
    }

    public double distanceTo(LngLat a, LngLat b){
        return Math.sqrt((a.lng - b.lng)*(a.lng - b.lng) + (a.lat - b.lat)*(a.lat - b.lat));
    }

    public boolean closeTo(LngLat a, LngLat b){
        return false;
    }

    public LngLat nextPosition(int direction){
        return null;
    }
}
