package com.ladeologun.aws_s3.miscellaneous;

public class Solution {

    private String color;
    public Integer age;

    public Solution(){

    }

    public Solution(String color, Integer age) {
        this.color = color;
        this.age = age;
    }

    public  void getGreetings(){
        System.out.println("printing from outer class");

    }

    public  void getGreeting(){
        System.out.println("printing from outer class");

    }

}
