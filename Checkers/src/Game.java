import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Game extends JFrame{
	
	
	private JFrame frame;
	Draw draw = new Draw();
	private boolean currentGame;
	private boolean playerTurn;
	
	
	private static Game instance;
	
	public static Game getInstance() {
		
		if(instance == null) {
			instance = new Game();
		}
		
		return instance;
	}
	
	public void init() {
		playerTurn = true;
		currentGame = true;
		go();
		AI.getInstance().think();
	}
	
	public void newGame() {
		Board.getInstance().resetBoard();
		currentGame = true;
	}
	
	public void go() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.out.println("mouse click");
				int y = e.getX();// Y and X differ between table array and JFrame
				int x = e.getY();// so they are switched here

					int newX = (int) ((x - 9) / (Draw.SIZE / 8));
					int newY = (int) ((y - 37) / (Draw.SIZE / 8));

				if(currentGame)
					Board.getInstance().methodCaller(newX, newY);
			
				draw.repaint(0);
			}
		});
		frame.setTitle("Checkers");
		frame.getContentPane().add(new Draw());
		frame.pack();
		frame.setSize(Draw.SIZE + 18, Draw.SIZE + 47 + 50);// 47 and 18 are GUI adjustments, do not change
		frame.setVisible(true);

	}// end of go
	
	public void update() {
		draw.repaint(0);
	}
	
	
	
	public void winSequence(boolean playerTurn) {

		currentGame = false;
		
		if (playerTurn)
			JOptionPane.showMessageDialog(frame, "Player one is the winner!", "Win!", JOptionPane.PLAIN_MESSAGE);
		else
			JOptionPane.showMessageDialog(frame, "Player two is the winner!", "Win!", JOptionPane.PLAIN_MESSAGE);
		
		int choice = JOptionPane.showConfirmDialog(frame, "Would you like to start a new game?");

		if(choice == 0)
			newGame();
		else
			System.exit(0);
	}// end of winSequence()
	
    public boolean getPlayerTurn() {
        return playerTurn;
    }

	public void changeTeam() {

		playerTurn = !playerTurn;
		
		if(playerTurn)
			AI.getInstance().think();
		
	}

					
}
