
import java.util.Scanner;

public class TicTacToeGame
{

    private static final char PLAYERX = 'X';     // Helper constant for X player

    private static final char PLAYERO = 'O';     // Helper constant for O player

    private static final char SPACE = ' ';       // Helper constant for spaces

    private String playerX;

    private String playerO;

    public String getPlayerX() {
        return playerX;
    }

    public String getPlayerO() {
        return playerO;
    }

    /*
     TicTacToe Board
      0 | 1 | 2
     -----------
      3 | 4 | 5
     -----------
      6 | 7 | 8
     */

    private char[][] ar=new char[3][3];

    public TicTacToeGame() //constructor
    {
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                ar[i][j]=' ';
            }
        }
    }

    public TicTacToeGame(String player1, String player2){
        this();
        this.playerX=player1;
        this.playerO=player2;
    }

   public void printsamplebox()    //prints sample box before the game starts
    {
        System.out.println(" Sample TicTacToe Board\n" +
                "      0 | 1 | 2\n" +
                "     -----------\n" +
                "      3 | 4 | 5\n" +
                "     -----------\n" +
                "      6 | 7 | 8");
    }

    public static String getSampleBox()    //prints sample box before the game starts
    {
        return String.format(" Sample TicTacToe Board\n" +
                "      0 | 1 | 2\n" +
                "     -----------\n" +
                "      3 | 4 | 5\n" +
                "     -----------\n" +
                "      6 | 7 | 8");
    }



    public void printbox()      //prints tictactoe box after every move
    {
        System.out.println("  TicTacToe Board\n" +
                "     "+ar [0][0]+ " | " +ar[0][1]+ " | "+ ar[0][2]+"\n" +

                "     "+ ar[1][0] +" | "+ ar[1][1]+" | "+ar[1][2]+"\n" +

                "     "+ar[2][0]  + " | "+ar[2][1] +" | "+ ar[2][2]+"");
    }

    @Override
    public String toString()      //prints tictactoe box after every move
    {
        return String.format("  TicTacToe Board\n" +
                "     "+ar [0][0]+ " | " +ar[0][1]+ " | "+ ar[0][2]+"\n" +

                "     "+ ar[1][0] +" | "+ ar[1][1]+" | "+ar[1][2]+"\n" +

                "     "+ar[2][0]  + " | "+ar[2][1] +" | "+ ar[2][2]+"");
    }


    /*
    checkwinner function
    returns 1 if "0" wins
    returns 2 if "X" wins
    returns 0 if draw-game
    */
    public boolean isOver(){
        if(checkwinner()!=0){
            return true;
        }else{
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    if(ar[i][j]==' '){
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public int checkwinner()
    {
        for(int i=0;i<3;i++)
        {
            if(ar[i][0]=='0' && ar[i][1]=='0' && ar[i][2]=='0')
            {
                return 1;
            }
            if(ar[i][0]=='X' && ar[i][1]=='X' && ar[i][2]=='X')
            {
                return 2;
            }
        }
        for(int j=0;j<3;j++)
        {
            if(ar[0][j]=='0' && ar[1][j]=='0' && ar[2][j]=='0')
            {
                return 1;
            }
            if(ar[0][j]=='X' && ar[1][j]=='X' && ar[2][j]=='X')
            {
                return 2;
            }

        }

        if(ar[0][0]=='0' && ar[1][1]=='0' && ar[2][2]=='0')
        {
            return 1;
        }
        if(ar[0][0]=='X' && ar[1][1]=='X' && ar[2][2]=='X')
        {
            return 2;
        }

        if(ar[0][2]=='0' && ar[1][1]=='0' && ar[2][0]=='0')
        {
            return 1;
        }
        if(ar[0][2]=='X' && ar[1][1]=='X' && ar[2][0]=='X')
        {
            return 2;
        }

        return 0;

    }


    public  int cou=1;
    /* This is the counter variable which check whose turn it is .
      If cou is even it '0' turn
      Else it is 'x' turn
      So when you implement socket in this program and when a player takes 2 turns consequently then
      you just need to do counter minus 1 or you can make a if statement in controller() method that doesn't let to use place() method if a player takes 2 turns
       */

    public int getCou() {
        return cou;
    }

    public char getSpace(int space){
        return ar[space/3][space%3];
    }

    public boolean isTurn(String player){
        if(getCou()%2==1){
            return this.playerX.equalsIgnoreCase(player);
        }else{
            return this.playerO.equalsIgnoreCase(player);
        }
    }

    public int place(int a, String player){
        if(!isTurn(player)){
            return -1;
        }else if(ar[a/3][a%3]!=' '){
            return -2;
        }else{
            place(a);
            return 0;
        }
    }

    public void place(int a) // this function place the X or 0 to the place where player tells i.e "int a"

        {

            if(cou%2==0)
            {
                if (a == 0) {
                    ar[0][0] = '0';
                } else if (a == 1) {
                    ar[0][1] = '0';
                } else if (a == 2) {
                    ar[0][2] = '0';
                } else if (a == 3) {
                    ar[1][0] = '0';
                } else if (a == 4) {
                    ar[1][1] = '0';

                } else if (a == 5) {
                    ar[1][2] = '0';

                } else if (a == 6) {
                    ar[2][0] = '0';

                } else if (a == 7) {
                    ar[2][1] = '0';
                } else if (a == 8) {
                    ar[2][2] = '0';
                }

            }

           else
                { if (a == 0) {
                        ar[0][0] = 'X';
                    } else if (a == 1) {
                        ar[0][1] = 'X';
                    } else if (a == 2) {
                        ar[0][2] = 'X';
                    } else if (a == 3) {
                        ar[1][0] = 'X';
                    } else if (a == 4) {
                        ar[1][1] = 'X';

                    } else if (a == 5) {
                        ar[1][2] = 'X';

                    } else if (a == 6) {
                        ar[2][0] = 'X';

                    } else if (a == 7) {
                        ar[2][1] = 'X';
                    } else if (a == 8) {
                        ar[2][2] = 'X';
                    }
                }
                cou++;
        }

        public void gamecontroller() // this method basically calls all other method to make a game as whole working
        {
            TicTacToeGame o=new TicTacToeGame();
            o.printsamplebox();
            int pos;
            Scanner s= new Scanner(System.in);

            int[] p=new int[9];
            for(int h=0;h<p.length;h++)
            {p[h]=-1;}

            for(int i=0;i<9;i++)
            {

                System.out.println("Enter place where you want to place your symbol");
                pos=s.nextInt();
                boolean shouldIplace=true;
                for(int c=0;c<p.length;c++)
                {
                    if(p[c]==-1 || p[c]<0 ||pos>8)
                    {
                        shouldIplace=true;
                        p[c]=pos;
                        break;
                    }
                   else if(p[c]==pos)
                    {
                        System.out.println("This place is already been used. Enter a valid other place");
                        i--;
                        shouldIplace=false;
                        break;
                    }


                }
                if (shouldIplace)
                {
                if(pos >=0 && pos <9)
                {
                    o.place(pos);
                }

                else
                {
                    System.out.println("Please enter a valid space");
                    --i;
                }

                o.printbox();

                int  a=o.checkwinner();
                if (a==1)
                {
                    System.out.println("'0' player wins");break;
                }
                if (a==2)
                {
                    System.out.println("'X' player wins`");
                }
                if(i==8 && a==0)
                {
                    System.out.println("Draw game");
                }
            }
            }

        }


        /*
         you can apply all socket functions in the main() method or create other methods of your own and you can change p1 and p2 below to sockets

         */

    public static void main(String[] args)

    {
        TicTacToeGame c=new TicTacToeGame();


        Scanner s=new Scanner(System.in);
        String p1,p2;
        System.out.println("Enter Player 1's name: ");
        p1=s.nextLine();
        System.out.println("Enter Player 2's name: ");
        p2=s.nextLine();

        c.gamecontroller(); // calls game controller method


    }

}