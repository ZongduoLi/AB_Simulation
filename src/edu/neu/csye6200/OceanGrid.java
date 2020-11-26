package edu.neu.csye6200;

/**
 * @author Zongduo Li
 */
public class OceanGrid {
    private int gridLength = 1;
    private int gridWidth = 1;
    private double oilCoverage = 0;
    private double oilFlowRate = 0.3;
    private boolean isLand = false;

    public OceanGrid(){
    }

    public OceanGrid(int gridLength,int gridWidth,int oilCoverage,int oilFlowRate,boolean isLand){
        this.gridLength = gridLength;
        this.gridWidth = gridWidth;
        this.oilCoverage = oilCoverage;
        this.oilFlowRate = oilFlowRate;
        this.isLand = isLand;
    }

    public OceanGrid[][] initOcean(int oceanLength,int oceanWidth){
        OceanGrid[][] ocean = new OceanGrid[oceanLength/gridLength][oceanWidth/gridWidth];
        for(int i=0;i<oceanWidth/gridWidth;i++){
            for(int j=0;j<oceanLength/gridLength;j++){
                ocean[i][j] = new OceanGrid();
            }
        }
        return ocean;
    }

    public int getGridLength() {
        return gridLength;
    }

    public void setGridLength(int gridLength) {
        this.gridLength = gridLength;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }

    public double getOilCoverage() {
        return oilCoverage;
    }

    public void setOilCoverage(double oilCoverage) {
        this.oilCoverage = oilCoverage;
    }

    public double getOilFlowRate() {
        return oilFlowRate;
    }

    public void setOilFlowRate(double oilFlowRate) {
        this.oilFlowRate = oilFlowRate;
    }

    public boolean isLand() {
        return isLand;
    }

    public void setLand(boolean land) {
        isLand = land;
    }
}
