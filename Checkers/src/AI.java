

import java.util.ArrayList;
import java.util.Arrays;

public class AI {

	private final int KING_VALUE = 6;
	private final int OPP_KING_VALUE = 5;
	private final int VALUE = 4;
	private final int OPP_VALUE = 1;
	private final int JUMP_MOVE_VALUE = 7;
	
	private int turn;
	private static AI instance;

	public static AI getInstance() {

		if (instance == null)
			instance = new AI();
		return instance;
	}

	AI() {

		turn = 0;

	}

	public void think() {
		// getBestEnemyMove screws up jumps when looking two steps ahead sometimes
		// add way to change pieces when selecting and when clicking blank square
		
		turn++;
		String[][] board = copy(Board.getInstance().getTable());

		ArrayList<String> moves = getBoardMoves(board, true);
		if(moves.size() == 0) return;
						
		ArrayList<String[][]> finalBoards = new ArrayList<String[][]>();
		ArrayList<String> finalMoves = getBoardMoves(board, true);

		for (int i = 0; i < moves.size(); i++) {
			System.out.println("----------------------------------");
			
			String[][] temp = transformBoard(board, moves.get(i));
			print(temp);
			System.out.println("****");
			
			String[][] enemyBoard = transformBoard(temp, getBestEnemyMove(temp));
			print(enemyBoard);
			System.out.println("****");
			
			ArrayList<String> listOfMoves = getBoardMoves(enemyBoard, true);
			ArrayList<Integer> moveValues = new ArrayList<Integer>();
			for(String m : listOfMoves) {
				
				moveValues.add(getBoardValue(transformBoard(enemyBoard, m)));
				
			}
			
			int index = getHighest(moveValues);
			
			finalMoves.add(moves.get(i));
			if(listOfMoves.size() == 0) {
				finalBoards.add(enemyBoard);
				print(enemyBoard);
			}else {
				finalBoards.add(transformBoard(enemyBoard, listOfMoves.get(index)));
				print(transformBoard(enemyBoard, listOfMoves.get(index)));
			}
			
			System.out.println("Value: " + moveValues.get(index));
			System.out.println("Base move: " + moves.get(i));
			System.out.println("-----------------------------------------");
		}//end of for
		
		ArrayList<Integer> boardValues = new ArrayList<Integer>();
		for(String[][] b : finalBoards) {
			
			boardValues.add(getBoardValue(b));
			
		}
		
		
		int index = getHighest(boardValues);

		String selectedMove = (finalMoves.get(index));

		System.out.println("SelectedMove: " + selectedMove);
		System.out.println("Value of Selected Move: " + boardValues.get(index));

		for (int i = 0; i < (selectedMove.length() - 2); i += 2) {
			Game.getInstance().update();
			
			Board.getInstance().AImethodCaller(Integer.parseInt(selectedMove.substring(i, i + 1)),
					Integer.parseInt(selectedMove.substring(i + 1, i + 2)),
					Integer.parseInt(selectedMove.substring(i + 2, i + 3)),
					Integer.parseInt(selectedMove.substring(i + 3, i + 4)));
			
			wait(2);

		} // end of for

	}// end of think()

	private String getBestEnemyMove(String[][] b) {
		//most people only think two steps ahead so AI predicting player's move 
		//by looking at board state two steps ahead should be good

		ArrayList<String> moves = getBoardMoves(b, false);
		ArrayList<Integer> boardValues = new ArrayList<Integer>();
		if(moves.size() == 0) {
			return "0000";
		}
		for (int i = 0; i < moves.size(); i++) {

			String[][] temp = transformBoard(b, moves.get(i));
			
			temp = transformBoard(temp, getBestMove(temp, true));
			
			boardValues.add(getBoardValue(temp));
			
		}


		return moves.get(getLowest(boardValues));//return move that has lowest value, meaning it is best move for player and worst move for AI

	}// end of getBestEnemyMove
	
	private String getBestMove(String[][] b, boolean team) {

		ArrayList<String> moves = getBoardMoves(b, team);
		ArrayList<Integer> boardValues = new ArrayList<Integer>();
		
		if(moves.size() == 0) {
			return "0000";
		}
		for (int i = 0; i < moves.size(); i++) {
						
			boardValues.add(getBoardValue(transformBoard(b, moves.get(i))));
			
		}

		int index = getLowest(boardValues);

		String bestMove = moves.get(index);

		return bestMove;
	}// end of getBestEnemyMove

	private int getBoardValue(String[][] b) {

		//possible adjustment ideas:
		// - give more value to pieces close to becoming king
		// - subtract value if opponent can jump a piece(possibly make so it doesn't do this when evaluating opponent board)

		int value = 0;

		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {

				if (b[x][y] == "1")
					value += VALUE;
				if (b[x][y] == "2")
					value -= OPP_VALUE;
				if (b[x][y] == "3")
					value += KING_VALUE;
				if (b[x][y] == "4")
					value -= OPP_KING_VALUE;

			}
		}
		
		/*ArrayList<String> oppMoves = getBoardMoves(b, false);
		
		for(String move : oppMoves) {
			for (int i = 0; i < (move.length() - 2); i += 2) {
				int startX = Integer.parseInt(move.substring(0 + i, 1 + i));
				int endX = Integer.parseInt(move.substring(2 + i, 3 + i));
						
				
				if(Math.abs(startX - endX) == 2) {
					value -= JUMP_MOVE_VALUE;
				}
			}
		}*/
		
		/*
		if(b[3][0] != "x" && turn < 10) {

			if(b[3][0] == "1") {
				value += 3;
			}else if(b[3][0] == "2") {
				value -= 3;
			}
			
			
		}
		
		if(b[4][7] != "x" && turn < 10) {

			if(b[4][7] == "1") {
				value += 3;
			}else if(b[4][7] == "2") {
				value -= 3;
			}
			
		}*/
		
		return value;
	}// end of get BoardValue

	private ArrayList<String> getBoardMoves(String[][] b, boolean team) {// [pic] pieces move backwards

		ArrayList<String> moves = new ArrayList<String>();

		int z;
		boolean check = false;
		String oppPiece;
		String oppKing;
		String teamPiece;
		String teamKing;

		if (team) {
			z = 1;
			oppPiece = "2";
			oppKing = "4";
			teamPiece = "1";
			teamKing = "3";
		} else {
			z = -1;
			oppPiece = "1";
			oppKing = "3";
			teamPiece = "2";
			teamKing = "4";
		}
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {

				if (b[x][y] != teamPiece && b[x][y] != teamKing)
					continue;

				check = false;
				for (int counter = 0; counter < 1; counter++) {

					for (int i = -1; i <= 1; i += 2) {

						if (x + z < 0 || x + z > 7 || y + i < 0 || y + i > 7) {
							continue;
						}

						if (b[x + z][y + i] == "x") {

							moves.add(String.valueOf(x) + String.valueOf(y) + String.valueOf(x + z)
									+ String.valueOf(y + i));
							// add a continue here ***
						} // end of if regular move available

						if (x + (z * 2) < 0 || x + (z * 2) > 7 || y + (i * 2) < 0 || y + (i * 2) > 7) {
							continue;
						}
						if ((b[x + z][y + i] == oppPiece || b[x + z][y + i] == oppKing)) {

							if (b[x + (z * 2)][y + (i * 2)] == "x") {

								moves = getJumpMoves(b, moves, x, y, x + (z * 2), y + (i * 2),
										String.valueOf(x) + String.valueOf(y) + String.valueOf(x + (z * 2))
												+ String.valueOf(y + (i * 2)));

							} // end of if

						} // end of if jump available

					} // end of y for

					if (b[x][y] == teamKing && !check) {
						check = true;
						counter--;
						z = -(z);
					} // end of if

				} // end of counter for

			} // end of inner for
		} // end of outer for

		return moves;
	}// end of checkForBoardMoves

	private ArrayList<String> getJumpMoves(String[][] b, ArrayList<String> m, int x, int y, int newX, int newY,
			String prefix) {

		ArrayList<String> moves = m;
		String oppPiece;
		String oppKing;
		boolean atLeastOne = false;
		boolean check;
		boolean king;
		int z;

		if (b[x][y] == "3" || b[x][y] == "4") {
			king = true;
		} else {
			king = false;
		}

		if (b[x][y] == "1" || b[x][y] == "3") {
			z = 1;
			oppPiece = "2";
			oppKing = "4";
		} else {
			z = -1;
			oppPiece = "1";
			oppKing = "3";
		}

		b = transformBoard(b, String.valueOf(x) + String.valueOf(y) + String.valueOf(newX) + String.valueOf(newY));

		x = newX;
		y = newY;

		check = false;
		for (int counter = 0; counter < 1; counter++) {

			for (int i = -1; i <= 1; i += 2) {

				if (x + z < 0 || x + z > 7 || y + i < 0 || y + i > 7) {
					continue;
				}
				if (x + (z * 2) < 0 || x + (z * 2) > 7 || y + (i * 2) < 0 || y + (i * 2) > 7) {
					continue;
				}

				if ((b[x + z][y + i] == oppPiece || b[x + z][y + i] == oppKing)) {

					if (b[x + (z * 2)][y + (i * 2)] == "x") {

						// moves.add(String.valueOf(x) + String.valueOf(y) + String.valueOf(x + (z * 2))
						// + String.valueOf(y + (i * 2)));
						moves = getJumpMoves(b, moves, x, y, x + (z * 2), y + (i * 2),
								prefix + String.valueOf(x + (z * 2)) + String.valueOf(y + (i * 2)));
						atLeastOne = true;
					}

				}

			} // end of inner for
			
			if(king && !check) {
				check = true;
				z = -(z);
				counter--;
			}
		} // end of outer for

		if (!atLeastOne) {
			moves.add(prefix);
		}

		return moves;
	}// end of getJumpMoves

	private String[][] transformBoard(String[][] board, String move) {

		String[][] b = copy(board);

		for (int i = 0; i < (move.length() - 2); i += 2) {

			int oldX = Integer.parseInt(move.substring(i, i + 1));
			int oldY = Integer.parseInt(move.substring(i + 1, i + 2));
			int newX = Integer.parseInt(move.substring(i + 2, i + 3));
			int newY = Integer.parseInt(move.substring(i + 3, i + 4));

			String movingPiece = b[oldX][oldY];

			b[oldX][oldY] = "x";

			if (Math.abs(oldX - newX) == 2) {
				int deadX = (newX + oldX) / 2;
				int deadY = (newY + oldY) / 2;

				b[deadX][deadY] = "x";
			}

			if ((movingPiece == "1" && newX == 7) || (movingPiece == "2" && newX == 0)) {

				if (movingPiece == "1")
					movingPiece = "3";
				else
					movingPiece = "4";
			}

			b[newX][newY] = movingPiece;
		} // end of for

		return b;
	}// end of transformBoard()

	private int getHighest(ArrayList<Integer> a) {
		if(a.size() == 0) {
			return 0;
		}
		
		int max = a.get(0);
		ArrayList<Integer> arraySpot = new ArrayList<Integer>();
		arraySpot.add(0);
		
		for (int i = 0; i < a.size(); i++) {

			if(a.get(i) == max) {
				arraySpot.add(i);
			}

			if (a.get(i) > max) {
				max = a.get(i);
				arraySpot.clear();
				arraySpot.add(i);
			}
		} // end of for

		int index = (int) (Math.random() * arraySpot.size());

		return arraySpot.get(index);
	}// end of getHighest()

	private int getLowest(ArrayList<Integer> a) {

		int min = 0;
		int arraySpot = 0;
		boolean first = true;
		for (int i = 0; i < a.size(); i++) {

			if (first) {
				first = false;
				min = a.get(i);
				arraySpot = i;
			}
			if (a.get(i) < min) {
				min = a.get(i);
				arraySpot = i;
			}
		} // end of for

		return arraySpot;
	}// end of getLowest()

	private String[][] copy(String[][] b) {
		
		String[][] newB = new String[8][];
		for (int x = 0; x < 8; x++) {
			newB[x] = Arrays.copyOf(b[x], b[x].length);
		}

		return newB;
	}
	
	private void print(String[][] b) {
		
		for (int u = 0; u < 8; u++) {
			System.out.println();
			for (int z = 0; z < 8; z++) {
				System.out.print(b[u][z] + " ");
			} // end of for
		} // end of for
		
		System.out.println();
	}
	
	 private static void wait(int time) {

	        try {
	            Thread.sleep(time * 1000);
	            //TimeUnit.MILLISECONDS.sleep(time * 1000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }

	    }// end of wait()
	
	
}// end of class



/* old not good code
public class AI {

    private final int VALUE = 10;
    private final int KING_VALUE = 25;
    private final int VALUE_ONE = 20;//ADDED VALUE FOR WHEN A REGULAR PIECE IS ONE MOVE AWAY FROM BECOMING KING
    private final int VALUE_TWO = 5;//ADDED VALUE FOR WHEN A REGULAR PIECE IS TWO MOVES AWAY FROM BECOMING KING
    private final int VALUE_THREE = 0;//ADDED VALUE FOR WHEN A REGULAR PIECE IS THREE MOVES AWAY FROM BECOMING KING


    private static AI instance;

    public static AI getInstance() {

        if (instance == null)
            instance = new AI();

        return instance;
    }

    public void think() {
    	System.out.println("think called");
        String[][] board = copy(Board.getInstance().getTable());

        ArrayList<String> moves = getBoardMoves(board, true);

        ArrayList<String> movesAfterEnemy;
        ArrayList<String> movesAfterEnemy2 = new ArrayList<String>();
        ArrayList<String[][]> finalBoards = new ArrayList<String[][]>();

        if(moves.size() == 0) return;
        //TODO: MAKE WAY TO PRINT MOVE CALCULATIONS AND WITH THAT INFO IMPROVE AI
        for (int i = 0; i < moves.size(); i++) {
            System.out.println("---------------------------------------------------");

            String[][] temp = transformBoard(board, moves.get(i));
            print(temp);
            System.out.println("stop 1");
            String[][] enemyMoveBoard = transformBoard(temp, getBestEnemyMove(temp));
            print(enemyMoveBoard);

            movesAfterEnemy = getBoardMoves(enemyMoveBoard, true);
            System.out.println("*****");

            ArrayList<Integer> values = new ArrayList<Integer>();
            ArrayList<String[][]> possB = new ArrayList<String[][]>();
            for (String m : movesAfterEnemy) {
            	System.out.println("stop 2");
                movesAfterEnemy2.add(moves.get(i) + m);
                temp = transformBoard(enemyMoveBoard, m);
                possB.add(temp);
                values.add(getBoardValue(temp));
            }
            System.out.println("stop 3");
            String[][] finalBoard = possB.get(getHighest(values));
            finalBoards.add(finalBoard);

            print(finalBoard);
            System.out.println(getBoardValue(finalBoard));

            System.out.println("---------------------------------------------------");
        } // end of for

        //i
        ArrayList<Integer> boardValues = new ArrayList<Integer>();
        for (String[][] b : finalBoards) {

            boardValues.add(getBoardValue(b));

        }

        int index = getHighest(boardValues);
        if(index == -1){//problem? what if best move iss at 0 index
        	System.out.println("getHighest is 0");
            return;
        }
        
        String selectedMove = (moves.get(index));

        for (String m : moves) {

            if (selectedMove.contains(m)) {

                selectedMove = m;
                break;
            } // end of if

        } // end of for

        System.out.println("Selected Move: " + selectedMove);
        String[][] selectedBoard = transformBoard(board, selectedMove);
        System.out.println("Value: " + String.valueOf(getBoardValue(selectedBoard)));

        System.out.println("move made");
        for (int i = 0; i < (selectedMove.length() - 2); i += 2) {

            Board.getInstance().AImethodCaller(Integer.parseInt(selectedMove.substring(i, i + 1)),
                    Integer.parseInt(selectedMove.substring(i + 1, i + 2)),
                    Integer.parseInt(selectedMove.substring(i + 2, i + 3)),
                    Integer.parseInt(selectedMove.substring(i + 3, i + 4)));

        } // end of for


    }// end of think()

    private String getBestEnemyMove(String[][] b) {

        ArrayList<String> moves = getBoardMoves(b, false);
        ArrayList<Integer> boardValues = new ArrayList<Integer>();
        String[][] temp;

        for (int i = 0; i < moves.size(); i++) {

            temp = transformBoard(b, moves.get(i));
            temp = transformBoard(temp, getBestMove(temp, true));
            boardValues.add(getBoardValue(temp));

        }
        if(moves.size() == 0) return "0000";

        return moves.get(getLowest(boardValues));// return move that has lowest value, meaning it is best move for
        // player and worst move for AI

    }// end of getBestEnemyMove

    private String getBestMove(String[][] b, boolean team) {

        ArrayList<String> moves = getBoardMoves(b, team);// error

        ArrayList<Integer> boardValues = new ArrayList<Integer>();
        for (int i = 0; i < moves.size(); i++) {
            boardValues.add(getBoardValue(transformBoard(b, moves.get(i))));

        }

        if(moves.size() == 0) return "0000";

        int index = getLowest(boardValues);

        String bestMove = moves.get(index);

        return bestMove;
    }// end of getBestEnemyMove

    private int getBoardValue(String[][] b) {

        // Possible value adjustment ideas:
        //   - add extra points for special spots for first few turns

        int v1 = VALUE_ONE;
        int v2 = VALUE_TWO;
        int v3 = VALUE_THREE;
        int b1 = VALUE_ONE;
        int b2 = VALUE_TWO;
        int b3 = VALUE_THREE;

        int value = 0;

        for (int x = 0; x < 8; x++) {

            for (int y = 0; y < 8; y++) {

                String piece = b[x][y];
                if (piece == "1")
                    value += VALUE;
                if (piece == "2")
                    value -= VALUE;
                if (piece == "3")
                    value += KING_VALUE;
                if (piece == "4")
                    value -= KING_VALUE;

                if(piece == "1" && x < 4){

                    switch(x){

                        case 1:
                            value += v1;
                            v1 = (int) (v1 * (3.0 / 4.0));
                            break;
                        case 2:
                            value += v2;
                            v2 = (int) (v2 * (3.0 / 4.0));
                            break;

                            case 3:
                            value += v3;
                            v3 = (int) (v3 * (3.0 / 4.0));
                            break;

                    }
                }

                if(piece == "2" && x > 4) {

                    switch (x) {

                        case 6:
                            value -= b1;
                            b1 = (int) (b1 * (3.0 / 4.0));
                            break;

                        case 5:
                            value -= b2;
                            b2 = (int) (b2 * (3.0 / 4.0));
                            break;

                        case 4:
                            value -= b3;
                            b3 = (int) (b3 * (3.0 / 4.0));
                            break;

                    }
                }


            }//end of inner for
        }//end of outer for



        return value;
    }// end of get BoardValue

    private ArrayList<String> getBoardMoves(String[][] board, boolean team) {

        String[][] b = copy(board);
        ArrayList<String> moves = new ArrayList<String>();

        int z;
        boolean check = false;
        String oppPiece;
        String oppKing;
        String teamPiece;
        String teamKing;

        if (team) {
            z = 1;
            oppPiece = "2";
            oppKing = "4";
            teamPiece = "1";
            teamKing = "3";
        } else {
            z = -1;
            oppPiece = "1";
            oppKing = "3";
            teamPiece = "2";
            teamKing = "4";
        }

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {

                if (b[x][y] != teamPiece && b[x][y] != teamKing)
                    continue;

                check = false;
                for (int counter = 0; counter < 1; counter++) {

                    for (int i = -1; i <= 1; i += 2) {

                        if (x + z < 0 || x + z > 7 || y + i < 0 || y + i > 7) {
                            continue;
                        }

                        if (b[x + z][y + i] == "x") {
                            moves.add(String.valueOf(x) + String.valueOf(y) + String.valueOf(x + z)
                                    + String.valueOf(y + i));
                            // add a continue here ***
                        } // end of if regular move available

                        if (x + (z * 2) < 0 || x + (z * 2) > 7 || y + (i * 2) < 0 || y + (i * 2) > 7) {
                            continue;
                        }
                        if ((b[x + z][y + i] == oppPiece || b[x + z][y + i] == oppKing)) {

                            if (b[x + (z * 2)][y + (i * 2)] == "x") {
                                moves = getJumpMoves(b, moves, x, y, x + (z * 2), y + (i * 2),
                                        String.valueOf(x) + String.valueOf(y) + String.valueOf(x + (z * 2))
                                                + String.valueOf(y + (i * 2)));
                            } // end of if

                        } // end of if jump available

                    } // end of y for

                    if (b[x][y] == teamKing && !check) {
                        check = true;
                        counter--;
                        z = -(z);
                    } // end of if

                } // end of counter for

            } // end of inner for
        } // end of outer for

        return moves;
    }// end of checkForBoardMoves

    private ArrayList<String> getJumpMoves(String[][] board, ArrayList<String> m, int x, int y, int newX, int newY,
                                           String prefix) {

        String[][] b = copy(board);
        ArrayList<String> moves = m;
        String oppPiece;
        String oppKing;
        boolean atLeastOne = false;
        boolean check;
        boolean king;
        int z;

        if (b[x][y] == "3" || b[x][y] == "4") {
            king = true;
        } else {
            king = false;
        }

        if (b[x][y] == "1" || b[x][y] == "3") {
            z = 1;
            oppPiece = "2";
            oppKing = "4";
        } else {
            z = -1;
            oppPiece = "1";
            oppKing = "3";
        }

        b = transformBoard(b, String.valueOf(x) + String.valueOf(y) + String.valueOf(newX) + String.valueOf(newY));

        x = newX;
        y = newY;

        check = false;
        for (int counter = 0; counter < 1; counter++) {

            for (int i = -1; i <= 1; i += 2) {

                if (x + z < 0 || x + z > 7 || y + i < 0 || y + i > 7) {
                    continue;
                }

                if (x + (z * 2) < 0 || x + (z * 2) > 7 || y + (i * 2) < 0 || y + (i * 2) > 7) {
                    continue;
                }

                if ((b[x + z][y + i] == oppPiece || b[x + z][y + i] == oppKing)) {

                    if (b[x + (z * 2)][y + (i * 2)] == "x") {

                        // moves.add(String.valueOf(x) + String.valueOf(y) + String.valueOf(x + (z * 2))
                        // + String.valueOf(y + (i * 2)));
                        moves = getJumpMoves(b, moves, x, y, x + (z * 2), y + (i * 2),
                                prefix + String.valueOf(x + (z * 2)) + String.valueOf(y + (i * 2)));
                        atLeastOne = true;
                    }

                }

            } // end of inner for

            if (king && !check) {
                check = true;
                z = -(z);
                counter--;
            }
        } // end of outer for

        if (!atLeastOne) {
            moves.add(prefix);
        }

        return moves;
    }// end of getJumpMoves

    private String[][] transformBoard(String[][] board, String move) {

        String[][] b = copy(board);

        for (int i = 0; i < (move.length() - 2); i += 2) {

            int oldX = Integer.parseInt(move.substring(i, i + 1));
            int oldY = Integer.parseInt(move.substring(i + 1, i + 2));
            int newX = Integer.parseInt(move.substring(i + 2, i + 3));
            int newY = Integer.parseInt(move.substring(i + 3, i + 4));

            String movingPiece = b[oldX][oldY];

            b[oldX][oldY] = "x";

            if (Math.abs(oldX - newX) == 2) {
                int deadX = (newX + oldX) / 2;
                int deadY = (newY + oldY) / 2;

                b[deadX][deadY] = "x";
            }

            if ((movingPiece == "1" && newX == 7) || (movingPiece == "2" && newX == 0)) {

                if (movingPiece == "1")
                    movingPiece = "3";
                else
                    movingPiece = "4";
            }

            b[newX][newY] = movingPiece;
        } // end of for

        return b;
    }// end of transformBoard()

    private int getHighest(ArrayList<Integer> a) {
    	
    	if(a.size() == 0) {
    		return -1;
    	}

        int max = a.get(0);
        ArrayList<Integer> arraySpot = new ArrayList<Integer>();
        for (int i = 0; i < a.size(); i++) {

            if (max == a.get(i)) {
                arraySpot.add(i);
            }

            if (a.get(i) > max) {
                arraySpot.clear();
                max = a.get(i);
                arraySpot.add(i);
            }

        } // end of for

        int index = (int) (Math.random() * arraySpot.size());

        return arraySpot.get(index);
    }// end of getHighest()

    private int getLowest(ArrayList<Integer> a) {

        if(a.size() == 0){
            return -1;
        }
        int min = 0;
        int arraySpot = 0;
        boolean first = true;
        for (int i = 0; i < a.size(); i++) {

            if (first) {
                first = false;
                min = a.get(i);
                arraySpot = i;
            }
            if (a.get(i) < min) {
                min = a.get(i);
                arraySpot = i;
            }
        } // end of for

        return arraySpot;
    }// end of getLowest()

    private String[][] copy(String[][] b) {
        String[][] newB = new String[8][];
        for (int x = 0; x < 8; x++) {
            newB[x] = Arrays.copyOf(b[x], b[x].length);
        }

        return newB;
    }
    private void print(String[][] b){
        for (int x = 0; x < 8; x++) {
            System.out.println();
            for (int z = 0; z < 8; z++) {
                System.out.print(b[x][z] + " ");
            } // end of for
        } // end of for
    }

    private static void wait(int time) {

        try {
            Thread.sleep(time * 1000);
            //TimeUnit.MILLISECONDS.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }// end of wait()

}// end of class

*/