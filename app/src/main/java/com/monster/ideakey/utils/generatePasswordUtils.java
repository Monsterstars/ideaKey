package com.monster.ideakey.utils;


import java.util.Random;

public class generatePasswordUtils {

    public static String getRandomString(){
        Random random=new Random();
        int k = random.nextInt();
        int j = Math.abs(k % 10) + 6;
        StringBuffer sb=new StringBuffer();
        for(int i = 0; i < j; i++){
            int number=random.nextInt(3);
            long result=0;
            switch(number){
                case 0:
                    result=Math.round(Math.random()*25+65);
                    sb.append(String.valueOf((char)result));
                    break;
                case 1:
                    result=Math.round(Math.random()*25+97);
                    sb.append(String.valueOf((char)result));
                    break;
                case 2:
                    sb.append(String.valueOf(new Random().nextInt(10)));
                    break;
            }
        }
        return sb.toString();
    }
}
