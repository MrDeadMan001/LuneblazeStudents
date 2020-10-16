package com.avadna.luneblaze.pojo;

import com.avadna.luneblaze.pojo.assessment.PojoAssessmentCategory;

import java.util.ArrayList;

public class PojoEligibilityFilters {
    public String no_of_sessions;
    public String session_role;
    public ArrayList<PojoAssessmentCategory> selectedCategoryList;
    public ArrayList<PojoAssessmentCategory> nonDomainCategoryList;
    public ArrayList<PojoAssessmentCategory> domainCategoryList;

}
