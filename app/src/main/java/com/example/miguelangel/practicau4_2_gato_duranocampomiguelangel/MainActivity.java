package com.example.miguelangel.practicau4_2_gato_duranocampomiguelangel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView[][] posiciones;
    TextView resul;
    Button reiniciar;
    boolean presionado[][];
    int quien [][];
    boolean jugando;
    int posGane[];
    boolean iniciado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        posiciones=new ImageView[3][3];
        posiciones[0][0]=findViewById(R.id.p00);
        posiciones[0][1]=findViewById(R.id.p01);
        posiciones[0][2]=findViewById(R.id.p02);
        posiciones[1][0]=findViewById(R.id.p10);
        posiciones[1][1]=findViewById(R.id.p11);
        posiciones[1][2]=findViewById(R.id.p12);
        posiciones[2][0]=findViewById(R.id.p20);
        posiciones[2][1]=findViewById(R.id.p21);
        posiciones[2][2]=findViewById(R.id.p22);
        posGane=new int[2];
        resul=findViewById(R.id.resul);
        presionado=new boolean[3][3];
        quien=new int[3][3];
        jugando=false;
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++) {
                presionado[i][j]=false;
                quien[i][j]=0;
            }
        }
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                final int i1=i,j1=j;
                posiciones[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        posiciones[i1][j1].setImageResource(R.drawable.x);
                        posiciones[i1][j1].setEnabled(false);
                        quien[i1][j1]=1;
                        presionado[i1][j1]=true;
                        jugando=true;
                    }
                });
            }
        }
        iniciado=true;
        reiniciar=findViewById(R.id.reiniciar);
        reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int r=0;r<3;r++){
                    for (int c=0;c<3;c++){
                        posiciones[r][c].setImageResource(R.drawable.cuadro);
                        posiciones[r][c].setEnabled(true);
                        quien[r][c]=0;
                        presionado[r][c]=false;
                    }
                }
                posGane=new int[2];
                jugando=false;
                resul.setText("Jugando");
                iniciado=true;
            }
        });
        jugar();
    }
    public void jugar(){
        new Thread(new Runnable() {
            @Override
            public void run() {
               while(iniciado){
                   if (!jugando)continue;
                   tablero(false);
                   determinarJugada();
                   try {
                       Thread.sleep(1000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           posiciones[posGane[0]][posGane[1]].setImageResource(R.drawable.circulo);
                       }
                   });
                   presionado[posGane[0]][posGane[1]]=true;
                   quien[posGane[0]][posGane[1]]=4;
                   if (!ganador().equals("")){
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               resul.setText("Gana "+ganador());
                               tablero(false);
                           }
                       });
                       iniciado=false;
                   }
                   else{
                       int cont=0;
                       for (int i=0;i<3;i++){
                           for (int j=0;j<3;j++){
                               if (!presionado[i][j])cont++;
                           }
                       }
                       if (cont==0){
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   resul.setText("Empate");
                                   iniciado=false;
                               }
                           });
                       }
                   }
                   try {
                       Thread.sleep(2000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                    tablero(true);
                   jugando=false;
               }
            }
        }).start();
    }
    private void determinarJugada(){
        for (int i=0;i<3;i++){
            if (jugadaEnFila(i,1))return;
            if (jugadaEnColumna(i,1))return;
        }
        if (jugadaEnDiagonal(1))return;
        if (jugadaEnDiagonalInvertida(1))return;
        for (int i=0;i<3;i++){
            if (jugadaEnFila(i,0))return;
            if (jugadaEnColumna(i,0))return;
        }
        if (jugadaEnDiagonal(0))return;
        if (jugadaEnDiagonalInvertida(0))return;

        if (primerasJugadas())return;
    }
    private int sumaFila(int r){
        int suma=sumar(quien[r][0],quien[r][1],quien[r][2]);
        return suma;
    }
    private int sumaColumna(int c){
        int suma=sumar(quien[0][c],quien[1][c],quien[2][c]);
        return suma;
    }
    private int sumaDiagonal(){
        int suma=sumar(quien[0][0],quien[1][1],quien[2][2]);
        return suma;
    }
    private int sumaDiagonalInvertida(){
        int suma=sumar(quien[0][2],quien[1][1],quien[2][0]);
        return suma;
    }
    private boolean jugadaEnFila(int r,int op){
        int opcion=0;
        switch (op){
            case 0:
                opcion=2;break;
            case 1:
                opcion=8;break;
        }
        if (sumaFila(r)==opcion){
            for (int i=0;i<3;i++){
                if (!presionado[r][i]){
                    posGane[0]=r;
                    posGane[1]=i;
                    return true;
                }
            }
        }
        return false;
    }
    private boolean primerasJugadas(){
        if (!presionado[0][0]){
            posGane[0]=0;
            posGane[1]=0;
            return true;
        }
        else if (!presionado[0][2]){
            posGane[0]=0;
            posGane[1]=2;
            return true;
        }
        else if (!presionado[2][0]){
            posGane[0]=2;
            posGane[1]=0;
            return true;
        }
        else if (!presionado[2][2]){
            posGane[0]=2;
            posGane[1]=2;
            return true;
        }
        return false;
    }
    private boolean jugadaEnColumna(int c,int op){
        int opcion=0;
        switch (op){
            case 0:
                opcion=2;break;
            case 1:
                opcion=8;break;
        }
        if (sumaColumna(c)==opcion){
            for (int i=0;i<3;i++){
                if (!presionado[i][c]){
                    posGane[0]=i;
                    posGane[1]=c;
                    return true;
                }
            }
        }
        return false;
    }
    private boolean jugadaEnDiagonal(int op){
        int opcion=0;
        switch (op){
            case 0:
                opcion=2;break;
            case 1:
                opcion=8;break;
        }
        if (sumaDiagonal()==opcion){
            if (!presionado[0][0]){
                posGane[0]=0;
                posGane[1]=0;
                return true;
            }
            else if (!presionado[1][1]){
                posGane[0]=1;
                posGane[1]=1;
                return true;
            }
            else if (!presionado[2][2]){
                posGane[0]=2;
                posGane[1]=2;
                return true;
            }
        }
        return false;
    }
    private boolean jugadaEnDiagonalInvertida(int op){
        int opcion=0;
        switch (op){
            case 0:
                opcion=2;break;
            case 1:
                opcion=8;break;
        }
        if (sumaDiagonalInvertida()==opcion){
            if (!presionado[0][2]){
                posGane[0]=0;
                posGane[1]=2;
                return true;
            }
            else if (!presionado[1][1]){
                posGane[0]=1;
                posGane[1]=1;
                return true;
            }
            else if (!presionado[2][0]){
                posGane[0]=2;
                posGane[1]=0;
                return true;
            }
        }
        return false;
    }
    //----------------------------------------------------------3
    private void tablero(boolean estado){
        for (int r=0;r<3;r++){
            for (int c=0;c<3;c++){
                posiciones[r][c].setEnabled(estado);
            }
        }
    }
    //-----------------------------------------------------------

    public String ganador(){
        int suma1=sumar(quien[0][0],quien[0][1],quien[0][2]);
        int suma2=sumar(quien[1][0],quien[1][1],quien[1][2]);
        int suma3=sumar(quien[2][0],quien[2][1],quien[2][2]);
        int suma4=sumar(quien[0][0],quien[1][0],quien[2][0]);
        int suma5=sumar(quien[0][1],quien[1][1],quien[2][1]);
        int suma6=sumar(quien[0][2],quien[1][2],quien[2][2]);
        int suma7=sumar(quien[0][0],quien[1][1],quien[2][2]);
        int suma8=sumar(quien[0][2],quien[1][1],quien[2][0]);
        if (suma1==3||suma2==3||suma3==3||suma4==3||suma5==3||suma6==3||suma7==3||suma8==3){
            return "Jugador";
        }
        else if(suma1==12||suma2==12||suma3==12||suma4==12||suma5==12||suma6==12||suma7==12||suma8==12){
            return "Computadora";
        }
        return "";

    }
    public int sumar(int v1, int v2,int v3){
        return v1+v2+v3;
    }


}
