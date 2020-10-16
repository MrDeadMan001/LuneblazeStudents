package com.avadna.luneblaze.pojo.quiz;

import android.os.CountDownTimer;

public class PojoQuizQuestionListItem {

    public String text;
    public String img;
    public int time;
    public boolean shown=false;

    public PojoQuizQuestionListItem(String text, String img,int time){
        this.text=text;
        this.img=img;
        this.time=time;
        shown=false;
    }

}
