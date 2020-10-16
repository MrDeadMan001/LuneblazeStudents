package com.avadna.luneblaze.pojo.quiz;

import java.util.ArrayList;

public class CreateQuestionItem {
        public String imagePath;
        public String text;
        public ArrayList<String> answers;
        public int duration;
        public int correctAns;

        public CreateQuestionItem(){
                answers=new ArrayList<>();
                imagePath="";
        }

}
