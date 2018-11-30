package com.example.lactoriaus.todoapp;

public class Priority {
    String lstLocation;
    String lvlPriority;

    public Priority (String lstLocation, String lvlPriority){
        this.lstLocation = lstLocation;
        this.lvlPriority = lvlPriority;
    }

    public Priority() {

    }

    public String getLstLocation(){
        return lstLocation;
    }

    public String getLvlPriority(){
        return  lvlPriority;
    }

    public void setLstLocation(String lstLocation){
        this.lstLocation = lstLocation;
    }

    public  void setLvlPriority(String lvlPriority){
        this.lvlPriority = lvlPriority;
    }


}
