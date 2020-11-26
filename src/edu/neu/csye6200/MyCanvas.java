/**
 * 
 */
package edu.neu.csye6200;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;


/**
 * @author mgmunson
 *
 */
public class MyCanvas extends JPanel implements Observer {

	private int ctr = 0;
	private ABSimulation mySimulation = new ABSimulation();

	/**
	 * 
	 */
	public MyCanvas()  {

	}

	// Swing calls when a redraw is needed
	public void paint(Graphics g) {
		drawCanvas(g);
	}

	// Draw the contents of the panel
	private void drawCanvas(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Dimension size = getSize();

		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, size.width, size.height);

		drawOceanGrid(g2d);  
	}

	private int lineSize = 20; // How big each cell should be
	private int halfLineSize = lineSize / 2;

	private void drawOceanGrid(Graphics2D g2d) {

		int maxRows = ABSimulation.OCEAN_WIDTH;
		int maxCols = ABSimulation.OCEAN_LENGTH;

		for (int j = 0; j < maxRows; j++) {
			for (int i = 0; i < maxCols; i++) {

				int startx = i * lineSize;

				if(mySimulation.ocean[j][i].getOilCoverage()>0){
					int red = validColor((int)(135 - 200*mySimulation.ocean[j][i].getOilCoverage()));
					int green = validColor((int)(206 - 200*mySimulation.ocean[j][i].getOilCoverage()));
					int blue = validColor((int)(235 - 200*mySimulation.ocean[j][i].getOilCoverage()));
					g2d.setColor(new Color(red,green,blue));
				}else{

					g2d.setColor(new Color(135,206,235));
				}


				g2d.fillRect(startx, j * lineSize, lineSize-1, lineSize-1);

				if(mySimulation.boat.getBoatPositionX()==j && mySimulation.boat.getBoatPositionY()==i) {
					Font font = new Font("Boat", Font.PLAIN, 30);
					g2d.setColor(new Color(153, 51, 250));
					g2d.setFont(font);
					g2d.drawString("\uD83D\uDEA2", startx + halfLineSize - halfLineSize * 2 / 3, j * lineSize + halfLineSize + halfLineSize / 4);
				}
				Font font = new Font("OilCoverage",Font.PLAIN,15);
				g2d.setColor(new Color(94,38,18));
				g2d.setFont(font);
				String str = String.format("%1$.2f",mySimulation.ocean[j][i].getOilCoverage());
				if(mySimulation.coverageShowFlag){
					g2d.drawString(str, startx + halfLineSize - halfLineSize * 2 / 3, j * lineSize + halfLineSize + halfLineSize / 4);
				}
			}
		}

		g2d.setColor(Color.green);
		g2d.drawString("Hello World " + ctr++ ,10 ,20 ); 

	}

	
	private int validColor(int colVal) {
		if (colVal < 0) return 0;
		if (colVal > 255) return 255;
		return colVal;

	}
	
	@Override
	public void update(Observable o, Object arg) {

		if (arg instanceof ABSimulation) {
			mySimulation = (ABSimulation) arg;
		}
		repaint(); // Tell the GUI thread that it should schedule a paint() call
	}



}
