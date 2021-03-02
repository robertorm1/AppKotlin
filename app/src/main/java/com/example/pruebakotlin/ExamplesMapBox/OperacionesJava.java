package com.example.pruebakotlin.ExamplesMapBox;

public class OperacionesJava {

    public double operaciones(Double num1,Double num2,int op){

        double result=0.0;

        switch(op){
            case 1:
                result=num1+num2;
                break;
            case 2:
                result=num1-num2;
                break;
            case 3:
                result=num1/num2;
                break;
            case 4:
                result=num1*num2;
                break;
            default:
                result=0;
        }
        return result;
    }
}
