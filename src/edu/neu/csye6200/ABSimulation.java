package edu.neu.csye6200;

import java.util.Observable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * @author Zongduo Li
 */
public class ABSimulation extends Observable implements Runnable {
    Thread thread = null;
    public Timer timer;
    public static final int OCEAN_LENGTH = 40; //The length of the ocean
    public static final int OCEAN_WIDTH = 40; //The width of the ocean
    OceanGrid oceanGrid;
    OceanGrid[][] ocean;
    public int time = 1;
    Boat boat = new Boat("Boat1");
    public boolean flag = true;
    private boolean done = false;
    private boolean paused = false;
    private int ctr = 0;
    private int conditionCode;
    private boolean conditionFlag = true;
    private boolean case1Flag = true;
    protected boolean coverageShowFlag = true;
    private boolean oilSpreadFlag = true;
    ABSimulation abSimulation = null;

    private static Logger log = Logger.getLogger(ABSimulation.class.getName()); //Log

    /**
     * Constructor - Initialize ocean area, initialize cleaning boat
     */
    public ABSimulation() {
        oceanGrid = new OceanGrid();
        ocean = oceanGrid.initOcean(OCEAN_LENGTH, OCEAN_WIDTH);
        boat.setBoatPositionX(boat.getAbRule().x);
        boat.setBoatPositionY(boat.getAbRule().y);
        boat.setBoatDirection(boat.getAbRule().degree);
        boat.setBoatSpeed(boat.getAbRule().speed);
    }

    //Start simulation
    public void startSim() {
        System.out.println("Starting the simulation");
        timer = new Timer();
        abSimulation = new ABSimulation();
        if (thread != null) return;
        thread = new Thread(this);
        paused = false;
        done = false;
        ctr = 0;
        thread.start();
    }

    //Pause the simulation and press again to continue
    public void pauseSim() {
        paused = !paused;
        System.out.println("Pause the simulation: " + paused);
    }

    //Stop the simulation
    public void stopSim() {
        System.out.println("Stop the simulation");
        if (thread == null) return;
        done = true;
    }

    //Simple mode
    public void simpleSim() {
        conditionCode = 1;
        conditionFlag = true;
        case1Flag = true;
    }

    //Medium mode
    public void mediumSim() {
        conditionCode = 2;
        conditionFlag = true;
    }

    //Complex mode
    public void complexSim() {
        conditionCode = 3;
        conditionFlag = true;
    }

    //Override run method of runnable interface (multithreading)
    @Override
    public void run() {
        runSimLoop();
        thread = null;
    }

    private void runSimLoop() {
        while (!done) {
            if (!paused)
                updateSim();
            sleep(500);
        }
    }

    private void updateSim() {
        System.out.println("Updating the simualtion " + ctr++);

        // Update the simulation state
        // Reposition moving items
        // Calculate the effects of actions
        // Measure any statistics or collect other needed information

        // Notify any observers that the state of this simulation has changed

        //If the ship is on the boundary at this time,
        // it will not turn to prevent the array from crossing the boundary
        if (boat.getBoatPositionY() == OCEAN_LENGTH / oceanGrid.getGridWidth() - 1 || boat.getBoatPositionX() == 0 || boat.getBoatPositionX() == OCEAN_WIDTH / oceanGrid.getGridLength() - 1 || boat.getBoatPositionY() == 0) {
            conditionFlag = false;
        }


        if (conditionFlag == true) {

            switch (conditionCode) {
                case 1:
                    if (case1Flag == true) {

                        boat.getAbRule().degree = 3.14 / 4;
                        case1Flag = false;
                    }
                    conditionFlag = false;
                    break;
                case 2:
                    double[] j = {0, 3.14 / 4, 3.14 * 0.75, 3.14, 1.25 * 3.14, 1.75 * 3.14, 2 * 3.14};
                    Random ran = new Random();
                    int ran2 = ran.nextInt(j.length);
                    double degree2 = j[ran2];
                    boat.getAbRule().degree = degree2;
                    break;
                case 3:
                    double[] i = {0, 3.14 / 4, 3.14 / 2, 3.14 * 0.75, 3.14, 1.25 * 3.14, 1.5 * 3.14, 1.75 * 3.14, 2 * 3.14};
                    Random r = new Random();
                    int random = r.nextInt(i.length);
                    double degree = i[random];
                    boat.getAbRule().degree = degree;
                    break;
            }
        }


        boatMove();
        conditionFlag = true;
        calcOilSpread();
        oilSpreadFlag = true;
        log.info("BOAT NAME: " + boat.getBoatName() + " POSITION: [" + boat.getBoatPositionX() + "," + boat.getBoatPositionY() + "] TOTAL GRIDS: "
                + (OCEAN_LENGTH / oceanGrid.getGridLength()) * (OCEAN_WIDTH / oceanGrid.getGridWidth()) + " TOTAL POLLUTED GRIDS: " + calcPollutedGrids());

        setChanged();
        notifyObservers(this); // Send a copy of the simulation
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Calculate how many grids on the sea surface have been contaminated by fuel
    public int calcPollutedGrids() {
        int counter = 0;
        for (int i = 0; i < OCEAN_WIDTH / oceanGrid.getGridWidth(); i++) {
            for (int j = 0; j < OCEAN_LENGTH / oceanGrid.getGridLength(); j++) {
                if (ocean[i][j].getOilCoverage() > 0)
                    counter++;
            }
        }
        return counter;
    }

    //Calculate the total fuel coverage of the current ocean
    public double calcFractionOfOilCoverage() {
        double counter = 0;
        for (int i = 0; i < OCEAN_WIDTH / oceanGrid.getGridWidth(); i++) {
            for (int j = 0; j < OCEAN_LENGTH / oceanGrid.getGridLength(); j++) {
                if (ocean[i][j].getOilCoverage() > 0)
                    counter += ocean[i][j].getOilCoverage();
            }
        }
        return counter / (OCEAN_WIDTH * OCEAN_LENGTH);
    }

    //The ship travels at an angle of 45 ° and rebounds against the boundary
    //If there is oil pollution on the grid where the current ship is located, clean up the pollution
    public void boatMove() {

        if (flag == true) {


            //右上角
            if (boat.getBoatPositionX() == 0 && boat.getBoatPositionY() == OCEAN_LENGTH / oceanGrid.getGridWidth() - 1) {
                boat.getAbRule().degree = 0.75 * 3.14;
            }
            //右下角
            else if (boat.getBoatPositionX() == OCEAN_WIDTH / oceanGrid.getGridLength() - 1 && boat.getBoatPositionY() == OCEAN_LENGTH / oceanGrid.getGridWidth() - 1) {
                boat.getAbRule().degree = 1.25 * 3.14;
            }
            //左上角
            else if (boat.getBoatPositionX() == 0 && boat.getBoatPositionY() == 0) {
                boat.getAbRule().degree = 0.25 * 3.14;
            }
            //左下角
            else if (boat.getBoatPositionX() == OCEAN_WIDTH / oceanGrid.getGridLength() - 1 && boat.getBoatPositionY() == 0) {
                boat.getAbRule().degree = 1.75 * 3.14;

            } else if (boat.getBoatPositionY() == OCEAN_LENGTH / oceanGrid.getGridWidth() - 1 || boat.getBoatPositionY() == 0) {
                boat.getAbRule().changeDirectionY();
            } else if (boat.getBoatPositionX() == OCEAN_WIDTH / oceanGrid.getGridLength() - 1 || boat.getBoatPositionX() == 0) {
                boat.getAbRule().changeDirectionX();
            }

            if (boat.getAbRule().degree == 3.14 / 4) {
                boat.setBoatPositionX(boat.getBoatPositionX() + (int) Math.rint(Math.sqrt(2) * boat.getBoatSpeed() * Math.sin(boat.getAbRule().degree)));
                boat.setBoatPositionY(boat.getBoatPositionY() + (int) Math.rint(Math.sqrt(2) * boat.getBoatSpeed() * Math.cos(boat.getAbRule().degree)));
            } else {
                boat.setBoatPositionX(boat.getBoatPositionX() + (int) Math.rint(boat.getBoatSpeed() * Math.sin(boat.getAbRule().degree)));
                boat.setBoatPositionY(boat.getBoatPositionY() + (int) Math.rint(boat.getBoatSpeed() * Math.cos(boat.getAbRule().degree)));
            }

        }

        if (ocean[boat.getBoatPositionX()][boat.getBoatPositionY()].getOilCoverage() > 0) {
            //Carry out the cleaning work
            flag = false;
            ocean[boat.getBoatPositionX()][boat.getBoatPositionY()].setOilCoverage(ocean[boat.getBoatPositionX()][boat.getBoatPositionY()].getOilCoverage() - 0.5);
            if (boat.getBoatPositionX() != 0 && boat.getBoatPositionX() != OCEAN_WIDTH / oceanGrid.getGridWidth() - 1 && boat.getBoatPositionY() != 0 && boat.getBoatPositionY() != OCEAN_LENGTH / oceanGrid.getGridLength() - 1) {

                ocean[boat.getBoatPositionX() - 1][boat.getBoatPositionY() - 1].setOilCoverage(ocean[boat.getBoatPositionX() - 1][boat.getBoatPositionY() - 1].getOilCoverage() - 0.5);
                ocean[boat.getBoatPositionX() - 1][boat.getBoatPositionY()].setOilCoverage(ocean[boat.getBoatPositionX() - 1][boat.getBoatPositionY()].getOilCoverage() - 0.5);
                ocean[boat.getBoatPositionX() - 1][boat.getBoatPositionY() + 1].setOilCoverage(ocean[boat.getBoatPositionX() - 1][boat.getBoatPositionY() + 1].getOilCoverage() - 0.5);
                ocean[boat.getBoatPositionX()][boat.getBoatPositionY() - 1].setOilCoverage(ocean[boat.getBoatPositionX()][boat.getBoatPositionY() - 1].getOilCoverage() - 0.5);
                ocean[boat.getBoatPositionX()][boat.getBoatPositionY() + 1].setOilCoverage(ocean[boat.getBoatPositionX()][boat.getBoatPositionY() + 1].getOilCoverage() - 0.5);
                ocean[boat.getBoatPositionX() + 1][boat.getBoatPositionY() - 1].setOilCoverage(ocean[boat.getBoatPositionX() + 1][boat.getBoatPositionY() - 1].getOilCoverage() - 0.5);
                ocean[boat.getBoatPositionX() + 1][boat.getBoatPositionY()].setOilCoverage(ocean[boat.getBoatPositionX() + 1][boat.getBoatPositionY()].getOilCoverage() - 0.5);
                ocean[boat.getBoatPositionX() + 1][boat.getBoatPositionY() + 1].setOilCoverage(ocean[boat.getBoatPositionX() + 1][boat.getBoatPositionY() + 1].getOilCoverage() - 0.5);
            }
            if (ocean[boat.getBoatPositionX()][boat.getBoatPositionY()].getOilCoverage() < 0)
                ocean[boat.getBoatPositionX()][boat.getBoatPositionY()].setOilCoverage(0);
        }
        if (ocean[boat.getBoatPositionX()][boat.getBoatPositionY()].getOilCoverage() == 0)
            flag = true;
    }

    //Calculate the fuel diffusion in the upper left corner
    public void calcOilSpreadAtTopLeftCorner(int i, int j) {
        boolean girdStateChanged = false;
        if (ocean[i][j].getOilCoverage() > ocean[i + 1][j].getOilCoverage()) {

            if ((ocean[i][j].getOilCoverage() - ocean[i + 1][j].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i + 1][j].getOilCoverage()) / 2);
                ocean[i + 1][j].setOilCoverage(ocean[i + 1][j].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i + 1][j].getOilCoverage()) / 2);
            } else {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                ocean[i + 1][j].setOilCoverage(ocean[i + 1][j].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                girdStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() < ocean[i + 1][j].getOilCoverage()) {
            if ((ocean[i + 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i + 1][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i + 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                ocean[i + 1][j].setOilCoverage(ocean[i + 1][j].getOilCoverage() - (ocean[i + 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i + 1][j].getOilFlowRate() * time);
                ocean[i + 1][j].setOilCoverage(ocean[i + 1][j].getOilCoverage() - ocean[i + 1][j].getOilFlowRate() * time);
                girdStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() > ocean[i][j + 1].getOilCoverage()) {
            if ((ocean[i][j].getOilCoverage() - ocean[i][j + 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i][j + 1].getOilCoverage()) / 2);
                ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i][j + 1].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                girdStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() < ocean[i][j + 1].getOilCoverage()) {
            if ((ocean[i][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i][j + 1].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() - (ocean[i][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i][j + 1].getOilFlowRate() * time);
                ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() - ocean[i][j + 1].getOilFlowRate() * time);
                girdStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() > ocean[i + 1][j + 1].getOilCoverage()) {
            if ((ocean[i][j].getOilCoverage() - ocean[i + 1][j + 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i + 1][j + 1].getOilCoverage()) / 2);
                ocean[i + 1][j + 1].setOilCoverage(ocean[i + 1][j + 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i + 1][j + 1].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                ocean[i + 1][j + 1].setOilCoverage(ocean[i + 1][j + 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                girdStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() < ocean[i + 1][j + 1].getOilCoverage()) {
            if ((ocean[i + 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i + 1][j + 1].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i + 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                ocean[i + 1][j + 1].setOilCoverage(ocean[i + 1][j + 1].getOilCoverage() - (ocean[i + 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i + 1][j + 1].getOilFlowRate() * time);
                ocean[i + 1][j + 1].setOilCoverage(ocean[i + 1][j + 1].getOilCoverage() - ocean[i + 1][j + 1].getOilFlowRate() * time);
                girdStateChanged = true;
            }
        }
        if (girdStateChanged)
            oilSpreadFlag = false;
    }

    //Calculate the fuel diffusion in the lower left corner
    public void calcOilSpreadAtLeftBottom(int i, int j) {
        boolean gridStateChanged = false;
        if (ocean[i][j].getOilCoverage() > ocean[i + 1][j].getOilCoverage()) {
            if ((ocean[i][j].getOilCoverage() - ocean[i + 1][j].getOilCoverage()) == ocean[i][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i + 1][j].getOilCoverage()) / 2);
                ocean[i + 1][j].setOilCoverage(ocean[i + 1][j].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i + 1][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                ocean[i + 1][j].setOilCoverage(ocean[i + 1][j].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() < ocean[i + 1][j].getOilCoverage()) {
            if ((ocean[i + 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i + 1][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i + 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                ocean[i + 1][j].setOilCoverage(ocean[i + 1][j].getOilCoverage() - (ocean[i + 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i + 1][j].getOilFlowRate() * time);
                ocean[i + 1][j].setOilCoverage(ocean[i + 1][j].getOilCoverage() - ocean[i + 1][j].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() > ocean[i][j - 1].getOilCoverage()) {
            if ((ocean[i][j].getOilCoverage() - ocean[i][j - 1].getOilCoverage()) == ocean[i][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i][j - 1].getOilCoverage()) / 2);
                ocean[i][j - 1].setOilCoverage(ocean[i][j - 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i][j - 1].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                ocean[i][j - 1].setOilCoverage(ocean[i][j - 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() < ocean[i][j - 1].getOilCoverage()) {
            if ((ocean[i][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) == ocean[i][j - 1].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                ocean[i][j - 1].setOilCoverage(ocean[i][j - 1].getOilCoverage() - (ocean[i][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i][j - 1].getOilFlowRate() * time);
                ocean[i][j - 1].setOilCoverage(ocean[i][j - 1].getOilCoverage() - ocean[i][j - 1].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() > ocean[i + 1][j - 1].getOilCoverage()) {
            if ((ocean[i][j].getOilCoverage() - ocean[i + 1][j - 1].getOilCoverage()) == ocean[i][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i + 1][j - 1].getOilCoverage()) / 2);
                ocean[i + 1][j - 1].setOilCoverage(ocean[i + 1][j - 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i + 1][j - 1].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                ocean[i + 1][j - 1].setOilCoverage(ocean[i + 1][j - 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() < ocean[i + 1][j - 1].getOilCoverage()) {
            if ((ocean[i + 1][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) == ocean[i + 1][j - 1].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i + 1][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                ocean[i + 1][j - 1].setOilCoverage(ocean[i + 1][j - 1].getOilCoverage() - (ocean[i + 1][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i + 1][j - 1].getOilFlowRate() * time);
                ocean[i + 1][j - 1].setOilCoverage(ocean[i + 1][j - 1].getOilCoverage() - ocean[i + 1][j - 1].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (gridStateChanged)
            oilSpreadFlag = false;
    }

    //Calculate the fuel diffusion in the upper right corner
    public void calcOilSpreadAtTopRightCorner(int i, int j) {
        boolean gridStateChanged = false;
        if (ocean[i][j].getOilCoverage() > ocean[i - 1][j].getOilCoverage()) {
            if ((ocean[i][j].getOilCoverage() - ocean[i - 1][j].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i - 1][j].getOilCoverage()) / 2);
                ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i - 1][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() < ocean[i - 1][j].getOilCoverage()) {
            if ((ocean[i - 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i - 1][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i - 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() - (ocean[i - 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i - 1][j].getOilFlowRate() * time);
                ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() - ocean[i - 1][j].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() > ocean[i][j + 1].getOilCoverage()) {
            if ((ocean[i][j].getOilCoverage() - ocean[i][j + 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i][j + 1].getOilCoverage()) / 2);
                ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i][j + 1].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() < ocean[i][j + 1].getOilCoverage()) {
            if ((ocean[i][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i][j + 1].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() - (ocean[i][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i][j + 1].getOilFlowRate() * time);
                ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() - ocean[i][j + 1].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() > ocean[i - 1][j + 1].getOilCoverage()) {
            if ((ocean[i][j].getOilCoverage() - ocean[i - 1][j + 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i - 1][j + 1].getOilCoverage()) / 2);
                ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i - 1][j + 1].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() < ocean[i - 1][j + 1].getOilCoverage()) {
            if ((ocean[i - 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i - 1][j + 1].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i - 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() - (ocean[i - 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i - 1][j + 1].getOilFlowRate() * time);
                ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() - ocean[i - 1][j + 1].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (gridStateChanged)
            oilSpreadFlag = false;
    }

    //Calculate the fuel diffusion in the lower right corner
    public void calcOilSpreadAtRightBottom(int i, int j) {
        boolean gridStateChanged = false;
        if (ocean[i][j].getOilCoverage() > ocean[i - 1][j].getOilCoverage()) {
            if ((ocean[i][j].getOilCoverage() - ocean[i - 1][j].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i - 1][j].getOilCoverage()) / 2);
                ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i - 1][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() < ocean[i - 1][j].getOilCoverage()) {
            if ((ocean[i - 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i - 1][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i - 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() - (ocean[i - 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i - 1][j].getOilFlowRate() * time);
                ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() - ocean[i - 1][j].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() > ocean[i][j - 1].getOilCoverage()) {
            if ((ocean[i][j].getOilCoverage() - ocean[i][j - 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i][j - 1].getOilCoverage()) / 2);
                ocean[i][j - 1].setOilCoverage(ocean[i][j - 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i][j - 1].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                ocean[i][j - 1].setOilCoverage(ocean[i][j - 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() < ocean[i][j - 1].getOilCoverage()) {
            if ((ocean[i][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i][j - 1].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                ocean[i][j - 1].setOilCoverage(ocean[i][j - 1].getOilCoverage() - (ocean[i][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i][j - 1].getOilFlowRate() * time);
                ocean[i][j - 1].setOilCoverage(ocean[i][j - 1].getOilCoverage() - ocean[i][j - 1].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() > ocean[i - 1][j - 1].getOilCoverage()) {
            if ((ocean[i][j].getOilCoverage() - ocean[i - 1][j - 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i - 1][j - 1].getOilCoverage()) / 2);
                ocean[i - 1][j - 1].setOilCoverage(ocean[i - 1][j - 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i - 1][j - 1].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                ocean[i - 1][j - 1].setOilCoverage(ocean[i - 1][j - 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (ocean[i][j].getOilCoverage() < ocean[i - 1][j - 1].getOilCoverage()) {
            if ((ocean[i - 1][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i - 1][j - 1].getOilFlowRate()) {
                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i - 1][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                ocean[i - 1][j - 1].setOilCoverage(ocean[i - 1][j - 1].getOilCoverage() - (ocean[i - 1][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
            } else {

                ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i - 1][j - 1].getOilFlowRate() * time);
                ocean[i - 1][j - 1].setOilCoverage(ocean[i - 1][j - 1].getOilCoverage() - ocean[i - 1][j - 1].getOilFlowRate() * time);
                gridStateChanged = true;
            }
        }
        if (gridStateChanged)
            oilSpreadFlag = false;
    }

    //Calculation of fuel diffusion on the ocean surface
    public void calcOilSpread() {
        for (int i = 0; i < OCEAN_LENGTH / oceanGrid.getGridLength() - 1; i++) {

            for (int j = 0; j < OCEAN_WIDTH / oceanGrid.getGridWidth() - 1; j++) {
                if (i == 0 && j == 0) {
                    calcOilSpreadAtTopLeftCorner(i, j);
                        /*if(!oilSpreadFlag)
                            break;*/

                } else if (i == 0 && j == OCEAN_WIDTH / oceanGrid.getGridWidth() - 1) {
                    calcOilSpreadAtLeftBottom(i, j);
                        /*if(!oilSpreadFlag)
                            break;*/

                } else if (i == OCEAN_LENGTH / oceanGrid.getGridLength() - 1 && j == 0) {
                    calcOilSpreadAtTopRightCorner(i, j);
                        /*if(!oilSpreadFlag)
                            break;*/


                } else if (i == OCEAN_LENGTH / oceanGrid.getGridLength() - 1 && j == OCEAN_WIDTH / oceanGrid.getGridWidth() - 1) {
                    calcOilSpreadAtRightBottom(i, j);
                        /*if(!oilSpreadFlag)
                            break;*/


                } else if (j == 0) {
                    boolean gridStateChanged = false;
                    calcOilSpreadAtTopLeftCorner(i, j);
                    if (ocean[i][j].getOilCoverage() > ocean[i - 1][j].getOilCoverage()) {
                        if ((ocean[i][j].getOilCoverage() - ocean[i - 1][j].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i - 1][j].getOilCoverage()) / 2);
                            ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i - 1][j].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                            ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() < ocean[i - 1][j].getOilCoverage()) {
                        if ((ocean[i - 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i - 1][j].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i - 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                            ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() - (ocean[i - 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i - 1][j].getOilFlowRate() * time);
                            ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() - ocean[i - 1][j].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() > ocean[i - 1][j + 1].getOilCoverage()) {
                        if ((ocean[i][j].getOilCoverage() - ocean[i - 1][j + 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i - 1][j + 1].getOilCoverage()) / 2);
                            ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i - 1][j + 1].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                            ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() < ocean[i - 1][j + 1].getOilCoverage()) {
                        if ((ocean[i - 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i - 1][j + 1].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i - 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                            ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() - (ocean[i - 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i - 1][j + 1].getOilFlowRate() * time);
                            ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() - ocean[i - 1][j + 1].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (gridStateChanged) {
                        oilSpreadFlag = false;

                    }


                } else if (i == 0) {
                    boolean gridStateChanged = false;
                    calcOilSpreadAtLeftBottom(i, j);
                    if (ocean[i][j].getOilCoverage() > ocean[i][j + 1].getOilCoverage()) {
                        if ((ocean[i][j].getOilCoverage() - ocean[i][j + 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i][j + 1].getOilCoverage()) / 2);
                            ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i][j + 1].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                            ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() < ocean[i][j + 1].getOilCoverage()) {
                        if ((ocean[i][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i][j + 1].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                            ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() - (ocean[i][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i][j + 1].getOilFlowRate() * time);
                            ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() - ocean[i][j + 1].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() > ocean[i + 1][j + 1].getOilCoverage()) {
                        if ((ocean[i][j].getOilCoverage() - ocean[i + 1][j + 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i + 1][j + 1].getOilCoverage()) / 2);
                            ocean[i + 1][j + 1].setOilCoverage(ocean[i + 1][j + 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i + 1][j + 1].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                            ocean[i + 1][j + 1].setOilCoverage(ocean[i + 1][j + 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() < ocean[i + 1][j + 1].getOilCoverage()) {
                        if ((ocean[i + 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i + 1][j + 1].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i + 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                            ocean[i + 1][j + 1].setOilCoverage(ocean[i + 1][j + 1].getOilCoverage() - (ocean[i + 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i + 1][j + 1].getOilFlowRate() * time);
                            ocean[i + 1][j + 1].setOilCoverage(ocean[i + 1][j + 1].getOilCoverage() - ocean[i + 1][j + 1].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (gridStateChanged) {

                        oilSpreadFlag = false;

                    }


                } else if (j == OCEAN_WIDTH / oceanGrid.getGridWidth() - 1) {
                    boolean gridStateChanged = false;
                    calcOilSpreadAtLeftBottom(i, j);
                    if (ocean[i][j].getOilCoverage() > ocean[i - 1][j].getOilCoverage()) {
                        if ((ocean[i][j].getOilCoverage() - ocean[i - 1][j].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i - 1][j].getOilCoverage()) / 2);
                            ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i - 1][j].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                            ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() < ocean[i - 1][j].getOilCoverage()) {
                        if ((ocean[i - 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i - 1][j].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i - 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                            ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() - (ocean[i - 1][j].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i - 1][j].getOilFlowRate() * time);
                            ocean[i - 1][j].setOilCoverage(ocean[i - 1][j].getOilCoverage() - ocean[i - 1][j].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() > ocean[i - 1][j - 1].getOilCoverage()) {
                        if ((ocean[i][j].getOilCoverage() - ocean[i - 1][j - 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i - 1][j - 1].getOilCoverage()) / 2);
                            ocean[i - 1][j - 1].setOilCoverage(ocean[i - 1][j - 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i - 1][j - 1].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                            ocean[i - 1][j - 1].setOilCoverage(ocean[i - 1][j - 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() < ocean[i - 1][j - 1].getOilCoverage()) {
                        if ((ocean[i - 1][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i - 1][j - 1].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i - 1][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                            ocean[i - 1][j - 1].setOilCoverage(ocean[i - 1][j - 1].getOilCoverage() - (ocean[i - 1][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i - 1][j - 1].getOilFlowRate() * time);
                            ocean[i - 1][j - 1].setOilCoverage(ocean[i - 1][j - 1].getOilCoverage() - ocean[i - 1][j - 1].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (gridStateChanged) {

                        oilSpreadFlag = false;

                    }


                } else if (i == OCEAN_LENGTH / oceanGrid.getGridLength() - 1) {
                    boolean gridStateChanged = false;
                    calcOilSpreadAtRightBottom(i, j);
                    if (ocean[i][j].getOilCoverage() > ocean[i][j + 1].getOilCoverage()) {
                        if ((ocean[i][j].getOilCoverage() - ocean[i][j + 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i][j + 1].getOilCoverage()) / 2);
                            ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i][j + 1].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                            ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() < ocean[i][j + 1].getOilCoverage()) {
                        if ((ocean[i][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i][j + 1].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                            ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() - (ocean[i][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i][j + 1].getOilFlowRate() * time);
                            ocean[i][j + 1].setOilCoverage(ocean[i][j + 1].getOilCoverage() - ocean[i][j + 1].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() > ocean[i - 1][j + 1].getOilCoverage()) {
                        if ((ocean[i][j].getOilCoverage() - ocean[i - 1][j + 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i - 1][j + 1].getOilCoverage()) / 2);
                            ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i - 1][j + 1].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                            ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() < ocean[i - 1][j + 1].getOilCoverage()) {
                        if ((ocean[i - 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i - 1][j + 1].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i - 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                            ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() - (ocean[i - 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i - 1][j + 1].getOilFlowRate() * time);
                            ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() - ocean[i - 1][j + 1].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (gridStateChanged) {

                        oilSpreadFlag = false;

                    }

                } else { //顺序：下，右，右下，左，上，左上，右上，左下
                    boolean gridStateChanged = false;
                    calcOilSpreadAtTopLeftCorner(i, j);
                    calcOilSpreadAtRightBottom(i, j);
                    if (ocean[i][j].getOilCoverage() > ocean[i + 1][j - 1].getOilCoverage()) {
                        if ((ocean[i][j].getOilCoverage() - ocean[i + 1][j - 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i + 1][j - 1].getOilCoverage()) / 2);
                            ocean[i + 1][j - 1].setOilCoverage(ocean[i + 1][j - 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i + 1][j - 1].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                            ocean[i + 1][j - 1].setOilCoverage(ocean[i + 1][j - 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() < ocean[i + 1][j - 1].getOilCoverage()) {
                        if ((ocean[i + 1][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i + 1][j - 1].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i + 1][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                            ocean[i + 1][j - 1].setOilCoverage(ocean[i + 1][j - 1].getOilCoverage() - (ocean[i + 1][j - 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i + 1][j - 1].getOilFlowRate() * time);
                            ocean[i + 1][j - 1].setOilCoverage(ocean[i + 1][j - 1].getOilCoverage() - ocean[i + 1][j - 1].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() > ocean[i - 1][j + 1].getOilCoverage()) {
                        if ((ocean[i][j].getOilCoverage() - ocean[i - 1][j + 1].getOilCoverage()) < ocean[i][j].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - (ocean[i][j].getOilCoverage() - ocean[i - 1][j + 1].getOilCoverage()) / 2);
                            ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() + (ocean[i][j].getOilCoverage() - ocean[i - 1][j + 1].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() - ocean[i][j].getOilFlowRate() * time);
                            ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() + ocean[i][j].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (ocean[i][j].getOilCoverage() < ocean[i - 1][j + 1].getOilCoverage()) {
                        if ((ocean[i - 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) < ocean[i - 1][j + 1].getOilFlowRate()) {
                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + (ocean[i - 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                            ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() - (ocean[i - 1][j + 1].getOilCoverage() - ocean[i][j].getOilCoverage()) / 2);
                        } else {

                            ocean[i][j].setOilCoverage(ocean[i][j].getOilCoverage() + ocean[i - 1][j + 1].getOilFlowRate() * time);
                            ocean[i - 1][j + 1].setOilCoverage(ocean[i - 1][j + 1].getOilCoverage() - ocean[i - 1][j + 1].getOilFlowRate() * time);
                            gridStateChanged = true;
                        }
                    }
                    if (gridStateChanged) {
                        oilSpreadFlag = false;
                    }
                }

                if (ocean[i][j].getOilCoverage() < 0)
                    ocean[i][j].setOilCoverage(0);
            }


        }
    }
}
