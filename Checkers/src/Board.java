
public class Board {

    private String[][] table;

    private int tempX;
    private int tempY;
    private int jumpingX;
    private int jumpingY;
    private boolean jumpPossible;
    private int[][] jumpMoves;

    private static Board board;

    public static Board getInstance() {

        if (board == null) {

            return board = new Board();

        }

        return board;

    }

    private Board() {

        table = new String[][] { { " ", "1", " ", "1", " ", "1", " ", "1" }, { "1", " ", "1", " ", "1", " ", "1", " " },
                { " ", "1", " ", "1", " ", "1", " ", "1" }, { "x", " ", "x", " ", "x", " ", "x", " " },
                { " ", "x", " ", "x", " ", "x", " ", "x" }, { "2", " ", "2", " ", "2", " ", "2", " " },
                { " ", "2", " ", "2", " ", "2", " ", "2" }, { "2", " ", "2", " ", "2", " ", "2", " " } };

        jumpPossible = false;

        tempX = -1;

    }

    // sets appropriate place on table to proper character and sets the x and y of
    // object to current new coordinates
    private void regularMove(int fromX, int fromY, int toX, int toY) {

        if (table[toX][toY] != "x") {
            //Game.getInstance().throwError("Error: There is already a piece here");
            tempX = -1;
            return;
        }

        String character = table[fromX][fromY];

        boolean king;
        if (character == "1" || character == "2") {
            king = false;
        } else {
            king = true;
        }

        boolean team;
        if (character == "1" || character == "3") {
            team = true;
        } else {
            team = false;
        }

        if (!king) {
            if (team && toX - fromX == -1) {

                //Game.getInstance().throwError("Error: This piece is not a king. It can only move forwards.");
                tempX = -1;
                return;
            } else if (!team && toX - fromX == 1) {
                //Game.getInstance().throwError("Error: This piece is not a king. It can only move forwards.");
                tempX = -1;
                return;
            }
        }

        Board.getInstance().table[fromX][fromY] = "x";
        Board.getInstance().table[toX][toY] = character;

        checkForKing(toX, toY);
        checkForWin();

        Game.getInstance().changeTeam();

    }// end of regularMove()

    private void jump(int fromX, int fromY, int toX, int toY) {

        int midX = Math.abs((fromX + toX) / 2);
        int midY = Math.abs((fromY + toY) / 2);

        if (table[midX][midY] == "x") {
            //Game.getInstance().throwError("You cannot jump two spaces here. Please restart.");
            tempX = -1;
            return;
        }

        String character = table[fromX][fromY];

        boolean team;
        if (character == "1" || character == "3") {
            team = true;
        } else {
            team = false;
        }

        boolean king;
        if (character == "1" || character == "2") {
            king = false;
        } else {
            king = true;
        }

        String midChar = table[midX][midY];

        boolean midTeam;
        if (midChar == "1" || midChar == "3") {
            midTeam = true;
        } else {
            midTeam = false;
        }

        if (midTeam == team) {
            //Game.getInstance().throwError("You can't jump over your own piece. Start over.");
            tempX = -1;
            return;
        }

        if (!king) {
            if (team && toX - fromX == -2) {
                //Game.getInstance().throwError("Error: This piece is not a king. It can only move forwards.");
                tempX = -1;
                return;
            } else if (!team && toX - fromX == 2) {
                //Game.getInstance().throwError("Error: This piece is not a king. It can only move forwards.");
                tempX = -1;
                return;
            }
        }

        table[midX][midY] = "x";
        table[fromX][fromY] = "x";

        table[toX][toY] = character;

        checkForKing(toX, toY);
        checkForWin();

        jumpMoves = checkForJumpMoves(toX, toY);
        if (jumpMoves == null) {
            jumpPossible = false;
            Game.getInstance().changeTeam();
        } else {
            jumpPossible = true;
            jumpingX = toX;
            jumpingY = toY;
        }

    }// end of jump()

    // transforms cords into ints that correspond to squares and calls appropriate
    // method

    public void AImethodCaller(int oldX, int oldY, int newX, int newY) {

        // System.out.println(oldX + ", " + oldY + "\t" + newX + ", " + newY);
        if ((Math.abs(newX - oldX)) == 2) {

            jump(oldX, oldY, newX, newY);

        } else if (Math.abs((newX - oldX)) == 1) {

            regularMove(oldX, oldY, newX, newY);

        } // end of if

    }// end of AImethodCaller

    public void methodCaller(int newX, int newY) {

        // trapping for selection of empty square
        if (tempX == -1 && Board.getInstance().table[newX][newY].equals("x")) {
            tempX = -1;
            return;
        }
        if(tempX != -1 && Board.getInstance().table[newX][newY] != "x"){
            tempX = -1;
            return;
        }
        // trapping for NoMan'sLand squares
        if ((newX + newY) % 2 == 0) {
            tempX = -1;
            return;
        }

        boolean team;
        if(table[newX][newY] == "1" || table[newX][newY] == "3") {
            team = true;
        }else {
            team = false;
        }


        if (team != Game.getInstance().getPlayerTurn() && tempX == -1) {

            tempX = -1;
            return;
        }

        if ((newX != jumpingX || newY != jumpingY) && tempX == -1 && jumpPossible) {
            tempX = -1;
            return;

        }

        if (tempX == -1 && jumpPossible) {

            tempX = newX;
            tempY = newY;
            return;
        }

        if (tempX == -1) {

            tempX = newX;
            tempY = newY;

            return;
        } // end of temp change

        if (jumpPossible) {
            boolean goodMove = false;
            for (int i = 0; i <= 3; i++) {

                if (newX == jumpMoves[0][i] && newY == jumpMoves[1][i]) {

                    goodMove = true;

                }
            } // end of for

            if (goodMove) {

                jump(tempX, tempY, newX, newY);
                tempX = -1;

            } else {
                tempX = -1;
            }

            return;
        } // end of if

        if ((Math.abs(newX - tempX)) == 2) {

            jump(tempX, tempY, newX, newY);
            tempX = -1;

        } else if (Math.abs((newX - tempX)) == 1 && Math.abs(newY - tempY) == 1) {

            regularMove(tempX, tempY, newX, newY);
            tempX = -1;

        } else {

            tempX = -1;

        }


    }// end of methodCaller()

    public void checkForWin() {

        if (!checkForBoardMoves(true) && checkForBoardMoves(false)) {
            Game.getInstance().winSequence(true);

        } else if (!checkForBoardMoves(false) && checkForBoardMoves(true)) {
            Game.getInstance().winSequence(true);

        }else if(!checkForBoardMoves(true) && !checkForBoardMoves((false))){
            Game.getInstance().winSequence(false);
        }

    }// end of checkForWin

    // coords in para can be used to check that specific spot for king instead of
    // using for loop
    private void checkForKing(int pieceX, int pieceY) {

        if (table[pieceX][pieceY] == "2" && pieceX == 0) {

            table[pieceX][pieceY] = "4";

        }

        if (table[pieceX][pieceY] == "1" && pieceX == 7) {

            table[pieceX][pieceY] = "3";

        }

    }// end of checkForKing()

    private boolean checkForBoardMoves(boolean team) {

        int z;
        boolean check = false;
        boolean team1;
        boolean king;


        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {


                team1 = table[x][y] == "1" || table[x][y] == "3";

                if(table[x][x] == "3" || table[x][y] == "4"){
                    king = true;
                }else{
                    king = false;
                }

                if(team1 == team) {
                    continue;
                }



                if (team1) {
                    z = 1;
                } else {
                    z = -1;
                }

                for (int counter = 0; counter < 1; counter++) {
                    for (int i = -1; i <= 1; i += 2) {

                        if (x + z < 0 || x + z > 7 || y + i < 0 || y + i > 7) {
                            continue;
                        }

                        if (Board.getInstance().table[x + z][y + i] == "x") {
                            return true;
                        }

                        if (x + (z * 2) < 0 || x + (z * 2) > 7 || y + (i * 2) < 0
                                || y + (i * 2) > 7) {
                            continue;
                        }

                        if (Board.getInstance().table[x + z][y + i] != "x") {

                            if ((Board.getInstance().table[x + z][y + i] == "1"
                                    || Board.getInstance().table[x + z][y + i] == "3") && !team1) {
                                if (Board.getInstance().table[x + (z * 2)][y + (i * 2)] == "x") {

                                    return true;
                                }
                            } // end of if team 1

                            if ((Board.getInstance().table[x + z][y + i] == "2"
                                    || Board.getInstance().table[x + z][y + i] == "4") && team1) {
                                if (Board.getInstance().table[x + (z * 2)][y + (i * 2)] == "x") {

                                    return true;
                                }

                            } // end of if team 2

                        } // end of piece check

                    } // end of inner for

                    if (king && !check) {
                        check = true;
                        counter--;
                        z = -(z);
                    }
                } // end of while
            } // end of for
        }//end of for
        return false;
    }// end of checkForBoardMoves()

    private int[][] checkForJumpMoves(int x, int y) {

        int arraySpot = 0;

        int[][] xy = new int[2][4];
        int z;
        int limit = 1;
        int counter = 0;
        boolean check = false;
        String character = table[x][y];
        boolean team;
        boolean king;

        if (character == "1" || character == "3") {
            team = true;
            z = 1;
        } else {
            team = false;
            z = -1;
        }

        if (character == "1" || character == "2") {
            king = false;
        } else {
            king = true;
        }

        while (counter < limit) {
            for (int i = -1; i <= 1; i += 2) {
                if (x + z < 0 || x + z > 7 || y + i < 0 || y + i > 7) {
                    continue;
                }
                if (x + (z * 2) < 0 || x + (z * 2) > 7 || y + (i * 2) < 0 || y + (i * 2) > 7) {
                    continue;
                }

                if (table[x + z][y + i] != "x") {
                    if ((table[x + z][y + i] == "1" || table[x + z][y + i] == "3") && !team) {
                        if (table[x + (z * 2)][y + (i * 2)] == "x") {
                            xy[0][arraySpot] = x + (z * 2);
                            xy[1][arraySpot] = y + (i * 2);
                            arraySpot++;
                        }
                    } // end of if team 1

                    if ((table[x + z][y + i] == "2" || table[x + z][y + i] == "4") && team) {

                        if (table[x + (z * 2)][y + (i * 2)] == "x") {
                            xy[0][arraySpot] = x + (z * 2);
                            xy[1][arraySpot] = y + (i * 2);

                            arraySpot++;
                        }

                    } // end of if team 2

                } // end of piece check

            } // end of inner for

            if (king && !check) {
                check = true;
                limit++;
                z = -(z);
            }

            counter++;
        } // end of while

        if (arraySpot == 0) {
            return null;
        }

        // code for automatic double jumping if only one jump option
        /*
         * if (arraySpot == 1) { jump(x, y, xy[0][0], xy[1][0]); tempX = -1; return
         * null; }
         */

        return xy;

    }// end of checkForMoves


    public String[][] getTable() {
        return table;
    }

    public void resetBoard(){

        table = new String[][] { { " ", "1", " ", "1", " ", "1", " ", "1" }, { "1", " ", "1", " ", "1", " ", "1", " " },
                { " ", "1", " ", "1", " ", "1", " ", "1" }, { "x", " ", "x", " ", "x", " ", "x", " " },
                { " ", "x", " ", "x", " ", "x", " ", "x" }, { "2", " ", "2", " ", "2", " ", "2", " " },
                { " ", "2", " ", "2", " ", "2", " ", "2" }, { "2", " ", "2", " ", "2", " ", "2", " " } };

        jumpPossible = false;

        tempX = -1;

    }

}// end of Board class


