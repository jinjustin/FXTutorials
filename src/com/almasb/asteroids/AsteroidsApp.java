package com.almasb.asteroids;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Astroid extends Application { //เขียน JavaFx ต้อง extends Application เสมอ

    private Pane root; //หน้าจอเกม
    
    private StackPane menuRoot; //หน้าจอ Menu ตอนเข้าเกม

    private List<GameObject> bullets = new ArrayList<>(); //List ของ GameObject ใช้เป็นกระสุน
    private List<GameObject> enemies = new ArrayList<>(); //List ของ GameObject ใช้เป็นศัตรู
    private List<GameObject> tanks = new ArrayList<>();

    private int score=0,power=10,HP=10;
    private int powerCount=0;
    
    private GameObject player;
    private GameObject hpBar;
    private GameObject powerBar;
    
    private Text scoreText = new Text("Score : "+ score); //Text ของคะแนน

    private Parent gameContent() { //ยังงงว่า Parent คืออะไร
        root = new Pane(); //กำหนดให้ root เป็น layout อันใหม่
        root.setPrefSize(1300, 1000);
        
        player = new Player();
        
        hpBar = new HPbar();
        powerBar = new PowerBar();
        
        player.setVelocity(new Point2D(2, 0)); //ความเร็วเริ่มต้นพอกดเปลี่ยนทิศทางความเร็วเหลือ 1   
        addGameObject(player, 300, 300); //Method อยู่ในคลาสนี้

        
        scoreText.setFont(Font.font("Sans serif",FontWeight.NORMAL,FontPosture.REGULAR,50));
        scoreText.setFill(Color.RED);
        scoreText.setX(1030.0);
        scoreText.setY(940.0);
        root.getChildren().add(scoreText);
        
        addGameObject(hpBar,20,900);
        addGameObject(powerBar,20,940);
        
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();
        return root;
    }
    
    private Parent gameMenu(){ //incompleted
        
        return menuRoot;
    }
    
    private int randomWithRange(int min, int max){ //สุ่มตัวเลข
   int range = (max - min) + 1;     
   return (int)(Math.random() * range) + min;
    }

    private void addBullet(GameObject bullet, double x, double y) {
        bullets.add(bullet); //เพิ่ม Object ลงใน ArrayList
        addGameObject(bullet, x, y);
    }

    private void addEnemy(GameObject enemy, double x, double y) {
        enemies.add(enemy);
        addGameObject(enemy, x, y);
    }
    
    private void addTank(GameObject tank, double x, double y) {
        tanks.add(tank);
        addGameObject(tank, x, y);
    }

    private void addGameObject(GameObject object, double x, double y) {
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        root.getChildren().add(object.getView()); // ไม่รู้ว่า children คืออะไร 
    }

    private void onUpdate() {
        
        for (GameObject bullet : bullets) {
            for (GameObject enemy : enemies) {
                if (bullet.isColliding(enemy)) {
                    bullet.setAlive(false);
                    enemy.setAlive(false);
                    score+=1;
                    System.out.println("Score : "+score);
                    root.getChildren().removeAll(bullet.getView(), enemy.getView());
                    GameObject.enemiesCount--;
                }
            }
            if(bullet.getView().getTranslateY()>=880){
                bullet.setAlive(false);
                root.getChildren().removeAll(bullet.getView());
            }
        }
        
            for (GameObject enemy : enemies) {
                if (player.isColliding(enemy)) {
                    HP-=1;
                    System.out.println("HP : "+HP);
                    enemy.setAlive(false);
                    System.out.println("Score : "+score);
                    root.getChildren().removeAll(enemy.getView());
                    GameObject.enemiesCount--;
                }
            }
        
            for (GameObject tank : tanks) { 
                if (player.isColliding(tank)) {
                    tank.setAlive(false);
                    if(HP<=5) HP+=5;
                    else HP=10;
                    System.out.println("HP : "+HP);
                    root.getChildren().removeAll(tank.getView());
                    GameObject.tanksCount--;
                }
            }

        bullets.removeIf(GameObject::isDead); //Method isDead อยู่ในคลาส GameObject
        enemies.removeIf(GameObject::isDead);
        tanks.removeIf(GameObject::isDead);

        bullets.forEach(GameObject::update); //update อยู่ในคลาส GameObject
        enemies.forEach(GameObject::update);
        tanks.forEach(GameObject::update);

        player.playerUpdate();

        if (randomWithRange(0,100) < 2 && GameObject.enemiesCount<20) {
            addEnemy(new Enemy(), Math.random() * root.getPrefWidth(), Math.random() * 880.0);
            GameObject.enemiesCount++;
        }
        if (randomWithRange(0,3000) < 2 && GameObject.tanksCount<2) {
            addTank(new Tank(), Math.random() * root.getPrefWidth(), Math.random() * 855.0);
            GameObject.tanksCount++;
        }
        powerCount++;
        if(player.getBoost()==false){
        if(powerCount>=60 && power<10){
            power+=1;
            powerCount=0;
        }
        }
        else{
            if(powerCount>=30 && power>0){
            power-=1;
            powerCount=0;
                System.out.println("power : "+power);
        }
            else if(powerCount>=30 && power<=0){
               player.setBoost(false);
            }
        }
        
        //คะแนน
        scoreText.setText("Score : " + score);
        
        root.getChildren().removeAll(hpBar.getView());
        hpBar.hpUpdate(HP);
        addGameObject(hpBar,20,900);
        
        root.getChildren().removeAll(powerBar.getView());
        powerBar.powerUpdate(power);
        addGameObject(powerBar,20,940);
        
    }

    private static class Player extends GameObject {
        Player() {
            super(new Rectangle(40, 20, Color.BLUE));
        }
    }

    private static class Enemy extends GameObject {
        Enemy() {
            super(new Circle(15, 15, 15, Color.RED));
        }
    }

    private static class Bullet extends GameObject {
        Bullet() {
            super(new Circle(5, 5, 5, Color.BROWN));
        }
    }
    
    private static class Tank extends GameObject {
        Tank() {
            super(new Rectangle(20, 40, Color.GREEN));
        }
    }
    
    private class HPbar extends GameObject {
        HPbar(){
            super(new Rectangle(100*HP,30, Color.RED));
        }
    }
    
    private class PowerBar extends GameObject {
        PowerBar(){
            super(new Rectangle(100*power,30, Color.YELLOW));
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(gameContent()));
        stage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                player.rotateLeft();
            } else if (e.getCode() == KeyCode.RIGHT) {
                player.rotateRight();
            } else if (e.getCode() == KeyCode.SPACE) {
                if(power>0){
                Bullet bullet = new Bullet();
                if(!player.getBoost())
                bullet.setVelocity(player.getVelocity().normalize().multiply(5));
                else
                    bullet.setVelocity(player.getVelocity().normalize().multiply(8));
                addBullet(bullet, player.getView().getTranslateX(), player.getView().getTranslateY());
                power-=1;
                System.out.println("Power : " + power);
                }
            }
            else if (e.getCode() == KeyCode.SHIFT){
                player.setBoost(!player.getBoost());
                if(player.getBoost())
                    player.setVelocity(player.getVelocity().normalize().multiply(4));
                else
                    player.setVelocity(player.getVelocity().normalize().multiply(2));
            }
        });
        stage.show();
    }
    
    public void menu(Stage stage) throws Exception{
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}
