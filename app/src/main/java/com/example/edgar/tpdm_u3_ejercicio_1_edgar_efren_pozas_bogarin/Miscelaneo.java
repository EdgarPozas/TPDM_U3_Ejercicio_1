package com.example.edgar.tpdm_u3_ejercicio_1_edgar_efren_pozas_bogarin;

import java.util.Random;

public class Miscelaneo {
    public static String generar_cadena(int tam) {
        String cad="";
        String letras="abcdefghijklmnopqrstuvwxyz123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random r=new Random();
        for (int i=0;i<tam;i++){
            int index=r.nextInt(letras.length()-1);
            cad+=letras.substring(index,index+1);
        }
        return cad;
    }
}
