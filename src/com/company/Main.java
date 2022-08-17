package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends JPanel implements KeyListener {
    public static final int CELL_SIZE=20;
    public static int width=400;
    public static int height=400;
    public static int row=height/CELL_SIZE;
    public static int column=width/CELL_SIZE;
    private Snake snake;
    private Fruit fruit;
    private Timer t;
    private int speed=100;
    private static String direction;
    private boolean allowKeyPress;
    private int score;
    private int highest_score;

    public Main(){
        read_highest_score();
        reset();
        addKeyListener(this);
    }
    public void setTimer(){
        t=new Timer();
        t.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                repaint(); //會執行paintComponent
            }
        },0,speed);
    }
    public void reset(){
        score=0;
        if(snake!=null){
            snake.getSnakeBody().clear();
        }
        allowKeyPress=true; //遊戲剛開始時可以用 keyPressed
        direction="Right";
        snake= new Snake();
        fruit= new Fruit();
        setTimer();
    }

    @Override
    public void paintComponent(Graphics g){
        // check if the snake bites itself
        ArrayList<Node> snake_body =  snake.getSnakeBody();
        Node head = snake_body.get(0);
        for (int i = 1; i < snake_body.size(); i++) {
            if (snake_body.get(i).x == head.x && snake_body.get(i).y == head.y) {
                allowKeyPress = false;
                t.cancel();
                t.purge();
                int response = JOptionPane.showOptionDialog(this, "Game Over!!Your score is"+score+"And your high score is"+ highest_score + ". Would you like to start over?", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, JOptionPane.YES_OPTION);
                write_a_file(score);
                switch (response) {
                    case JOptionPane.CLOSED_OPTION:
                        System.exit(0);
                        break;
                    case JOptionPane.NO_OPTION:
                        System.exit(0);
                        break;
                    case JOptionPane.YES_OPTION:
                        reset();
                        return;
                }
            }
        }
        // draw a background

        g.fillRect(0,0,width,height );
        snake.drawSnake(g);
        fruit.drawFruit(g);

        //remove snake tail and put it in head
        int snakeX=snake.getSnakeBody().get(0).x;
        int snakeY=snake.getSnakeBody().get(0).y;
        if(direction.equals("Left")){
            snakeX-=CELL_SIZE;// If turn left,  x-=cell_size
        }else if(direction.equals("Up")){
            snakeY-=CELL_SIZE;// If turn up, y-=cell_size
        }else if (direction.equals("Right")){
            snakeX+=CELL_SIZE;// If turn right, x+=cell_size
        }else if (direction.equals("Down")){
            snakeY+=CELL_SIZE;// If turn down, y+=cell_size
        }
        Node newHead=new Node(snakeX,snakeY);

        //check if the snake eats the fruit
        if(snake.getSnakeBody().get(0).x==fruit.getX() && snake.getSnakeBody().get(0).y==fruit.getY()){
            fruit.setNewLocation(snake);
            fruit.drawFruit(g);
            score++;
        }else{
            snake.getSnakeBody().remove(snake.getSnakeBody().size()-1);
        }

        snake.getSnakeBody().add(0,newHead);
        allowKeyPress=true;
        requestFocusInWindow();

    }
    @Override
    public Dimension getPreferredSize(){
        return new Dimension(width,height);
    }
    public static void main(String[] args) {
        JFrame window=new JFrame("Snake Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new Main());
        window.pack();//調整框架的大小
        window.setLocationRelativeTo(null);//設定視窗在屏幕中間
        window.setVisible(true);
        window.setResizable(false);//使用者無法調整視窗大小
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(allowKeyPress){
            if(e.getKeyCode()==37 && !direction.equals("Right")){
                direction="Left";
            }else if(e.getKeyCode()==38 && !direction.equals("Down")){
                direction="Up";
            }else if(e.getKeyCode()==39 && !direction.equals("Left")){
                direction="Right";
            }else if(e.getKeyCode()==40 && !direction.equals("Up")){
                direction="Down";
            }
            allowKeyPress=false;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void read_highest_score(){
        try{
            File myObj =new File("filename.txt"); //create the file
            Scanner myReader=new Scanner(myObj);
            highest_score=myReader.nextInt();
            myReader.close();
        }catch(FileNotFoundException e){
            highest_score=0;
            try{
                File myObj=new File("filename.txt");
                if(myObj.createNewFile()){
                    System.out.println("File created"+ myObj.getName());
                }
                FileWriter myWriter=new FileWriter(myObj.getName());
                myWriter.write(""+0);
            }catch (IOException err){
                err.printStackTrace();
            }
        }
    }
    //update the high score
    public void write_a_file(int score){
        try{
            FileWriter myWriter=new FileWriter("filename.txt");
            if(score > highest_score){
                myWriter.write(""+score);
                highest_score=score;
            }else{
                myWriter.write(""+highest_score);
            }
            myWriter.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
