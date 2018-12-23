import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

public class Draw extends JPanel {

	private final static int WIDTH = 100;
	private final Color B_COLOR1 = Color.white;
	private final Color B_COLOR2 = Color.darkGray;
	private final Color C_COLOR1 = Color.orange;
	private final Color C_COLOR2 = Color.red;
	public static final int  SIZE = WIDTH * 8;
	private String turnName;
	private final int TURN_NAME_SIZE = WIDTH / 2;
	private final Color TRIANGLE_COLOR = Color.black;
	private final int NUM = (WIDTH / 12);

	
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		for (int col = 0; col < 8; col++) {
			for (int row = 0; row < 8; row++) {

				if ((row + col) % 2 == 0) {
					g.setColor(B_COLOR1);
				} else {
					g.setColor(B_COLOR2);
				}

				g.fillRect(col * WIDTH, row * WIDTH, WIDTH, WIDTH);

			}
		}
		

		for (int x = 0; x < 8; x++) {

			for (int y = 0; y < 8; y++) {

				if (Board.getInstance().getTable()[x][y].equals("1")) {
					
					g.setColor(C_COLOR1);
					g.fillOval((WIDTH * y) + (WIDTH/10), (WIDTH * x) + (WIDTH/10), (WIDTH/10) * 8, (WIDTH/10) * 8);

				} else if (Board.getInstance().getTable()[x][y].equals("2")) {
					
					g.setColor(C_COLOR2);
					g.fillOval((WIDTH * y) + (WIDTH/10), (WIDTH * x) + (WIDTH/10), (WIDTH/10) * 8, (WIDTH/10) * 8);

				} else if (Board.getInstance().getTable()[x][y].equals("3")) {
					
					g.setColor(C_COLOR1);
					g.fillOval((WIDTH * y) + (WIDTH/10), (WIDTH * x) + (WIDTH/10), (WIDTH/10) * 8, (WIDTH/10) * 8);
					
					g.setColor(TRIANGLE_COLOR);
					g.fillPolygon(new int[]{(WIDTH  / 2) + (WIDTH * y), (WIDTH / 4) + (WIDTH * y), ((WIDTH / 4) * 3) + (WIDTH * y)}, new int[] {(WIDTH / 4) + (WIDTH * x) - NUM, ((WIDTH / 4) * 3) + (WIDTH * x) - NUM, ((WIDTH / 4) * 3) + (WIDTH * x) - NUM}, 3);
					g.fillPolygon(new int[]{(WIDTH  / 2) + (WIDTH * y), (WIDTH / 4) + (WIDTH * y), ((WIDTH / 4) * 3) + (WIDTH * y)}, new int[] {((WIDTH / 4) * 3) + (WIDTH * x) + NUM, (WIDTH / 4) + (WIDTH * x) + NUM, (WIDTH / 4) + (WIDTH * x) + NUM}, 3);

				} else if (Board.getInstance().getTable()[x][y].equals("4")) {
					
					g.setColor(C_COLOR2);
					g.fillOval((WIDTH * y) + (WIDTH/10), (WIDTH * x) + (WIDTH/10), (WIDTH/10) * 8, (WIDTH/10) * 8);
					
					g.setColor(TRIANGLE_COLOR);
					g.fillPolygon(new int[]{(WIDTH  / 2) + (WIDTH * y), (WIDTH / 4) + (WIDTH * y), ((WIDTH / 4) * 3) + (WIDTH * y)}, new int[] {(WIDTH / 4) + (WIDTH * x) - NUM, ((WIDTH / 4) * 3) + (WIDTH * x) - NUM, ((WIDTH / 4) * 3) + (WIDTH * x) - NUM}, 3);
					g.fillPolygon(new int[]{(WIDTH  / 2) + (WIDTH * y), (WIDTH / 4) + (WIDTH * y), ((WIDTH / 4) * 3) + (WIDTH * y)}, new int[] {((WIDTH / 4) * 3) + (WIDTH * x) + NUM, (WIDTH / 4) + (WIDTH * x) + NUM, (WIDTH / 4) + (WIDTH * x) + NUM}, 3);

				}
				
			}//end of second for loop
		}//end of first for loop
		
		
		g.setFont(new Font("TimesRoman", Font.PLAIN, TURN_NAME_SIZE));
		
		if(Game.getInstance().getPlayerTurn()) {
			g.setColor(C_COLOR1);
			turnName = "PLAYER ONE'S TURN";
		}else {
			g.setColor(C_COLOR2);
			turnName = "PLAYER TWO'S TURN";
		}
		
		g.drawString(turnName, (SIZE / 2) - ((turnName.length() / 2) * 33), SIZE + 40);
		
		repaint();
	} //end of paintComponent
	
}//end of Draw class

