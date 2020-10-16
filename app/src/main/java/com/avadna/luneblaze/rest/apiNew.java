package com.avadna.luneblaze.rest;

import com.avadna.luneblaze.pojo.PojoConductSessionResponse;
import com.avadna.luneblaze.pojo.PojoCreatePostResponse;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponse;
import com.avadna.luneblaze.pojo.PojoLoggedInDevicesResponse;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoReportContentResponse;
import com.avadna.luneblaze.pojo.PojoSignUp1Response;
import com.avadna.luneblaze.pojo.PojoSignUp2Response;
import com.avadna.luneblaze.pojo.PojoSuggestedArticlesResponse;
import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.avadna.luneblaze.pojo.assessment.PojoInitiatePaymentResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoArticleCommentsResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoCreateArticleResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoGetArticleDetailsResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoGetArticleRepliesResponse;
import com.avadna.luneblaze.pojo.pojoInterestHierarchy.PojoAllInterestListResponse;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetSponsorPlansResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetSponsorSessionListResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoRegisterOrganisationResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoSponsorPlanDetailsResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoProfileInfoResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoUserWorkListResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.tabsData.PojoProfileTabResponse;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoAddSessionDiscussionResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoGetSessionRepliesResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponse;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoGetVenueDetailsResponse;
import com.avadna.luneblaze.pojo.pojonormalpost.PojoNormalPostResponse;
import com.avadna.luneblaze.pojo.pojonormalpost.pojoreplylist.PojoGetNormalPostReplyResponse;
import com.avadna.luneblaze.pojo.razorpay.PojoRazorpayGetOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface apiNew {

    @FormUrlEncoded
    @POST("signup.php")
    Call<PojoSignUp1Response> registrationApi(@Field("first_name") String first_name,
                                              @Field("last_name") String last_name,
                                              @Field("email") String email,
                                              @Field("password") String password,
                                              @Field("phone") String phone);

    @Multipart
    @POST("signup2.php")
    Call<PojoNoDataResponse> addVenueImage(@Part("user_id") String user_id,
                                           @Part("gender") String gender,
                                           @Part("birth_day") String birth_day,
                                           @Part("birth_month") String birth_month,
                                           @Part("birth_year") String birth_year,
                                           @Part MultipartBody.Part file);//filename=photo

    @FormUrlEncoded
    @POST("signup_current_status.php")
    Call<PojoSignUp2Response> registrationCurrentStatusApi(@Field("user_id") String user_id,
                                                           @Field("user_work") String user_work,
                                                           @Field("work_place") String work_place,
                                                           @Field("work_title") String work_title,
                                                           @Field("venue_id") String venue_id);

    @FormUrlEncoded
    @POST("get_sub_interest_list.php")
    Call<PojoGetInterestListResponse> getInterestListApi(@Field("user_id") String user_id,
                                                         @Field("interest_id") String interest_id);


    @FormUrlEncoded
    @POST("get_allinterest.php")
    Call<PojoAllInterestListResponse> getAllInterestsHierarchy(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("get_posts.php")
    Call<PojoGetNewsFeedResponse> getNewsFeed(@Field("user_id") String user_id,
                                              @Field("get") String get,
                                              @Field("filter") String filter,
                                              @Field("last_score") String lastScore);


    @FormUrlEncoded
    @POST("myactivity.php")
    Call<PojoGetNewsFeedResponse> getUserActivityFeed(@Field("user_id") String user_id,
                                                      @Field("target_user_id") String target_user_id,
                                                      @Field("last_post_time") String last_post_time);

    @FormUrlEncoded
    @POST("my_posts.php")
    Call<PojoGetNewsFeedResponse> hitGetMyPostApi(@Field("user_id") String user_id,
                                                  @Field("target_user_id") String target_user_id,
                                                  @Field("lastpostid") String lastpostid);

    @FormUrlEncoded
    @POST("user_polls.php")
    Call<PojoGetNewsFeedResponse> hitGetUserPollsApi(@Field("user_id") String user_id,
                                                     @Field("target_user_id") String target_user_id,
                                                     @Field("lastpostid") String lastpostid);

    //get normal post data
    @FormUrlEncoded
    @POST("get_post_comments.php")
    Call<PojoNormalPostResponse> getNormalPostData(@Field("user_id") String user_id,
                                                   @Field("post_id") String post_id,
                                                   @Field("comment_id") String comment_id,
                                                   @Field("order") String order,
                                                   @Field("target") String target);

    //get upvoters and attendees
    @FormUrlEncoded
    @POST("alllikes.php")
    Call<PojoUpvotersAndAttendeesListResponse> getUpvotersAndAttendees(@Field("user_id") String user_id,
                                                                       @Field("id") String id,
                                                                       @Field("reaction") String reaction,
                                                                       @Field("offset") String offset);


    //get sponsor plan details
    @FormUrlEncoded
    @POST("sponsor_detail.php")
    Call<PojoSponsorPlanDetailsResponse> getSponsorPlanDetails(@Field("sponsorid") String sponsorid);


    //payment status update for ad
    @FormUrlEncoded
    @POST("update_adplan.php")
    Call<PojoInitiatePaymentResponse> initiateAdPayment(@Field("organisation_id") String organisation_id,
                                                        @Field("user_id") String user_id,
                                                        @Field("amount") String amount,
                                                        @Field("ad_id") String ad_id);

    //payment status update for ad
    @FormUrlEncoded
    @POST("update_adplan.php")
    Call<PojoNoDataResponse> updateAdPaymentStatus(@Field("organisation_id") String organisation_id,
                                                   @Field("user_id") String user_id,
                                                   @Field("status") String status,
                                                   @Field("transaction_id") String transaction_id,
                                                   @Field("amount") String amount,
                                                   @Field("ad_id") String ad_id,
                                                   @Field("payment_mode") String payment_mode,
                                                   @Field("order_id") String order_id,
                                                   @Field("payment_id") String payment_id);

    //get normal post comment's replies
    @FormUrlEncoded
    @POST("post/comment_replies.php")
    Call<PojoGetNormalPostReplyResponse> getPostCommentReplies(@Field("user_id") String user_id,
                                                               @Field("comment_id") String comment_id,
                                                               @Field("reply_comment_id") String reply_comment_id,
                                                               @Field("order") String order,
                                                               @Field("target") String target);


    //fetch suggested articles list based on the interests followed by user or on basis of common interests with an article
    @FormUrlEncoded
    @POST("suggested_articles.php")
    Call<PojoSuggestedArticlesResponse> getSuggestedArticles(@Field("user_id") String user_id,
                                                             @Field("article_id") String article_id); //optional




    @FormUrlEncoded
    @POST("session/venue.php")
    Call<PojoGetVenueDetailsResponse> getVenueDetailsFromIdApi(@Field("user_id") String user_id,
                                                               @Field("venue_id") String venue_id,
                                                               @Field("session_type") String session_type);


    //submit venue/session ratings
    @FormUrlEncoded
    @POST("session/venue_rating.php")
    Call<PojoNoDataResponse> submitVenueRating(@Field("user_id") String user_id,
                                               @Field("session_id") String sessions_id,
                                               @Field("venue_id") String venue_id,
                                               @Field("rating1") String rating1,
                                               @Field("rating2") String rating2,
                                               @Field("rating3") String rating3,
                                               @Field("rating4") String rating4,
                                               @Field("rating5") String rating5);

    @FormUrlEncoded
    @POST("session/venue_update.php")
    Call<PojoNoDataResponse> updateVenueDetails(@Field("user_id") String user_id,
                                                @Field("venue_id") String venue_id,
                                                @Field("description") String description,
                                                @Field("website") String website,
                                                @Field("name") String name);

    //create_article
    @Multipart
    @POST("articles.php")
    Call<PojoCreateArticleResponse> createArticle(@Part("user_id") RequestBody user_id,
                                                  @Part("title") RequestBody title,
                                                  @Part("description") RequestBody description,
                                                  @Part MultipartBody.Part file,
                                                  @PartMap Map<String, RequestBody> interests);

    //fetch article details
    @FormUrlEncoded
    @POST("get_article.php")
    Call<PojoGetArticleDetailsResponse> getArticleDetailsApi(@Field("user_id") String user_id,
                                                             @Field("article_id") String article_id);



    @FormUrlEncoded
    @POST("get_article_comments.php")
    Call<PojoArticleCommentsResponse> getArticleComments(@Field("user_id") String user_id,
                                                         @Field("article_id") String article_id,
                                                         @Field("order") String order,
                                                         @Field("comment_id") String comment_id,
                                                         @Field("target") String target);

    @FormUrlEncoded
    @POST("get_article_comment_reply.php")
    Call<PojoGetArticleRepliesResponse> getArticleCommentsReplies(@Field("user_id") String user_id,
                                                                  @Field("article_discussion_id") String article_discussion_id,
                                                                  @Field("order") String order,
                                                                  @Field("comment_id") String reply_id,
                                                                  @Field("target") String target);


    @FormUrlEncoded
    @POST("article_reaction.php")
    Call<PojoReportContentResponse> articleReaction(@Field("user_id") String user_id,
                                                    @Field("article_id") String article_id,
                                                    @Field("reaction") String reaction);


    //edit article created by me
    @Multipart
    @POST("edit_type.php")
    Call<PojoNoDataResponse> editArticlephoto(@Part("user_id") RequestBody user_id,
                                              @Part("type") RequestBody type,
                                              @Part("id") RequestBody article_id,
                                              @Part("message") RequestBody message,
                                              @Part("description") RequestBody description,
                                              @Part MultipartBody.Part file);


    //get session details
    @FormUrlEncoded
    @POST("session/sessions.php")
    Call<PojoSessionDetailsResponse> getSessionDetails(@Field("user_id") String user_id,
                                                       @Field("sessions_id") String sessions_id,
                                                       @Field("view") String view,
                                                       @Field("order") String order,
                                                       @Field("sessions_discussion_id") String sessions_discussion_id,
                                                       @Field("sessions_comment_id") String sessions_comment_id,
                                                       @Field("target") String target);

    //like unlike session discussion,comment, article comment/reply
    @FormUrlEncoded
    @POST("posts_reaction.php")
    Call<PojoNoDataResponse> likeUnlikeSessionDiscussion(@Field("user_id") String user_id,
                                                         @Field("id") String id,
                                                         @Field("reaction") String reaction);


    //get replies on session comments
    @FormUrlEncoded
    @POST("session/get_comment_replies.php")
    Call<PojoGetSessionRepliesResponse> getSessionCommentReplies(@Field("user_id") String user_id,
                                                                 @Field("sessions_comment_id") String sessions_comment_id,
                                                                 @Field("comment_id") String comment_id,
                                                                 @Field("order") String order,
                                                                 @Field("target") String target);

    //get replies on session discussions
    @FormUrlEncoded
    @POST("session/get_discussion_replies.php")
    Call<PojoGetSessionRepliesResponse> getSessionDiscussionReplies(@Field("user_id") String user_id,
                                                                    @Field("sessions_discussion_id") String sessions_discussion_id,
                                                                    @Field("comment_id") String comment_id,
                                                                    @Field("order") String order,
                                                                    @Field("target") String target);



    //get session questions
    @FormUrlEncoded
    @POST("session/sessions.php")
    Call<PojoSessionDetailsResponse> getSessionQuestions(@Field("user_id") String user_id,
                                                         @Field("sessions_id") String sessions_id,
                                                         @Field("qoffset") String offset,
                                                         @Field("view") String view,
                                                         @Field("relevance") String relevance,
                                                         @Field("my_roles") String my_roles,
                                                         @FieldMap(encoded = false) Map<String, String> interestMap,
                                                         @Field("my_interest") String my_interest);


    //assign coordinators to a session
    @FormUrlEncoded
    @POST("session/assign_coordinators.php")
    Call<PojoNoDataResponse> assignSessionCoordinators(@Field("session_id") String session_id,
                                                       @FieldMap(encoded = false) Map<String, String> fields);


    //create post with photos
    @Multipart
    @POST("post.php")
    Call<PojoCreatePostResponse> createPost(@Part("user_id") RequestBody user_id,
                                            @Part("handle") RequestBody handle,
                                            @Part("message") RequestBody message,
                                            @Part("multiple") RequestBody multiple,
                                            @Part("privacy") RequestBody privacy,
                                            @Part("tagged") RequestBody tagged,
                                            @PartMap Map<String, RequestBody> tagged_users,
                                            @PartMap Map<String, RequestBody> pos,
                                            @Part List<MultipartBody.Part> images);

    //create organisation post with photos
    @Multipart
    @POST("organizationpost.php")
    Call<PojoCreatePostResponse> createOrganisationPost(@Part("user_id") RequestBody user_id,
                                                        @Part("id") RequestBody id,
                                                        @Part("handle") RequestBody handle,
                                                        @Part("message") RequestBody message,
                                                        @Part("multiple") RequestBody multiple,
                                                        @Part("privacy") RequestBody privacy,
                                                        @Part("tagged") RequestBody tagged,
                                                        @PartMap Map<String, RequestBody> tagged_users,
                                                        @PartMap Map<String, RequestBody> pos,
                                                        @Part List<MultipartBody.Part> images);

    //Edit post
    @Multipart
    @POST("edit_type.php")
    Call<PojoCreatePostResponse> editPost(@Part("user_id") RequestBody user_id,
                                          @Part("type") RequestBody type,
                                          @Part("id") RequestBody id,
                                          @Part("message") RequestBody message,
                                          @Part("tagged") RequestBody tagged,
                                          @PartMap Map<String, RequestBody> tagged_users,
                                          @PartMap Map<String, RequestBody> pos,
                                          @Part List<MultipartBody.Part> images);


    //conduct session api
    @Multipart
    @POST("session/conduct.php")
    Call<PojoConductSessionResponse> conductSession(@Part("user_id") RequestBody user_id,
                                                    @Part("title") RequestBody title,
                                                    @PartMap HashMap<String, RequestBody> topics,
                                                    @PartMap HashMap<String, RequestBody> prerequisite,
                                                    @Part("description") RequestBody description,
                                                    @Part MultipartBody.Part cover_photo,
                                                    @PartMap HashMap<String, RequestBody> event_date,
                                                    @PartMap HashMap<String, RequestBody> interest_ids,
                                                    @PartMap HashMap<String, RequestBody> venues,
                                                    @Part("ans1") RequestBody ans1,
                                                    @Part("ans2") RequestBody ans2,
                                                    @Part("people_allowed") RequestBody people_allowed);

    //initiate session api
    @Multipart
    @POST("session/create.php")
    Call<PojoConductSessionResponse> initiateSession(@Part("user_id") RequestBody user_id,
                                                     @Part("title") RequestBody title,
                                                     @PartMap HashMap<String, RequestBody> topics,
                                                     @PartMap HashMap<String, RequestBody> prerequisite,
                                                     @Part("description") RequestBody description,
                                                     @Part MultipartBody.Part cover_photo,
                                                     @PartMap HashMap<String, RequestBody> event_date,
                                                     @PartMap HashMap<String, RequestBody> interest_ids,
                                                     @PartMap HashMap<String, RequestBody> venues);

    //search post api
    @FormUrlEncoded
    @POST("search.php")
    Call<PojoSearchResultResponse> searchPosts(@Field("user_id") String user_id,
                                               @Field("query") String query,
                                               @Field("search_type") String search_type,
                                               @Field("last_postid") String last_postid,
                                               @Field("last_score") String last_score);

    //global search api
    @FormUrlEncoded
    @POST("search.php")
    Call<PojoSearchResultResponse> globalSearch(@Field("user_id") String user_id,
                                                @Field("query") String query,
                                                @Field("offset") String offset,
                                                @Field("search_type") String search_type);

    //qa search api
    @FormUrlEncoded
    @POST("search.php")
    Call<PojoSearchResultResponse> searchQa(@Field("user_id") String user_id,
                                            @Field("query") String query,
                                            @Field("offset") String offset,
                                            @Field("search_type") String search_type,
                                            @Field("relevance") String relevance,
                                            @Field("my_roles") String my_roles,
                                            @FieldMap(encoded = false) Map<String, String> interestMap,
                                            @Field("my_interest") String my_interest);


    //article search api
    @FormUrlEncoded
    @POST("search.php")
    Call<PojoSearchResultResponse> searchArticle(@Field("user_id") String user_id,
                                                 @Field("query") String query,
                                                 @Field("offset") String offset,
                                                 @Field("search_type") String search_type,
                                                 @Field("my_roles") String my_roles,
                                                 @FieldMap(encoded = false) Map<String, String> interestMap,
                                                 @Field("my_interest") String my_interest);


    //session search api
    @FormUrlEncoded
    @POST("search.php")
    Call<PojoSearchResultResponse> searchSessions(@Field("user_id") String user_id,
                                                  @Field("query") String query,
                                                  @Field("last_session_id") String last_session_id,
                                                  @Field("search_type") String search_type,
                                                  @Field("lat") String latitude,
                                                  @Field("long") String longitude,
                                                  @Field("session_roles") String session_roles,
                                                  @Field("time_based") String time_based,
                                                  @Field("start_date") String start_date,
                                                  @Field("end_date") String end_date,
                                                  @FieldMap(encoded = false) Map<String, String> interestMap,
                                                  @Field("location_based") String location_based,
                                                  @FieldMap(encoded = false) Map<String, String> venueMap,
                                                  @Field("my_venue") String my_venue,
                                                  @Field("my_interest") String my_interest);


    //create razorpay order
    @FormUrlEncoded
    @POST("orders")
    Call<PojoRazorpayGetOrder> createRazorpayOrder(@Field("amount") String amount,
                                                   @Field("currency") String currency,
                                                   @Field("payment_capture") String payment_capture);


    @FormUrlEncoded
    @POST("get_plan.php")
    Call<PojoGetSponsorPlansResponse> getSponsorPlanList(@Field("offset") String offset);

    @FormUrlEncoded
    @POST("update_plan.php")
    Call<PojoInitiatePaymentResponse> initiateSponsorPayment(@Field("sponsorid") String sponsorid,
                                                             @Field("planid") String planid,
                                                             @Field("amount") String amount);

    @FormUrlEncoded
    @POST("update_plan.php")
    Call<PojoNoDataResponse> setSponsorPlan(@Field("sponsorid") String sponsorid,
                                            @Field("transactionid") String transactionid,
                                            @Field("status") String status,
                                            @Field("orderid") String orderid,
                                            @Field("paymentid") String paymentid,
                                            @Field("planid") String planid,
                                            @Field("amount") String amount);



    @FormUrlEncoded
    @POST("session/get_sessions.php")
    Call<PojoGetSponsorSessionListResponse> getSponsorSessionList(@Field("user_id") String user_id,
                                                                  @Field("offset") String offset);

    @FormUrlEncoded
    @POST("session/session_booking.php")
    Call<PojoNoDataResponse> sponsorSession(@Field("sponsorid") String sponsorid,
                                            @Field("sessionid") String sessionid);

    //post related apis
    @FormUrlEncoded
    @POST("posts_reaction.php")
    Call<PojoReportContentResponse> postReaction(@Field("user_id") String user_id,
                                                 @Field("id") String id,
                                                 @Field("reaction") String reaction);

    @FormUrlEncoded
    @POST("posts_reaction.php")
    Call<PojoNoDataResponse> likeNormalPostComment(@Field("user_id") String user_id,
                                                   @Field("comment_id") String comment_id,
                                                   @Field("reaction") String reaction);


    //user profile
    @FormUrlEncoded
    @POST("profile.php")
    Call<PojoProfileInfoResponse> getUserProfile(@Field("user_id") String user_id,
                                                 @Field("target_user_id") String target_user_id,
                                                 @Field("type") String type);

    //user profile
    @FormUrlEncoded
    @POST("profile.php")
    Call<PojoProfileTabResponse> getUserProfileTabData(@Field("user_id") String user_id,
                                                       @Field("target_user_id") String target_user_id,
                                                       @Field("type") String type,
                                                       @Field("offset") String offset);

    //add work history
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoNoDataResponse> addWorkHistory(@Field("user_id") String user_id,
                                            @Field("type") String type,
                                            @Field("work_type") String work_type,
                                            @Field("subject") String subject,
                                            @Field("institution") String institution,
                                            @Field("start_date") String start_date,
                                            @Field("end_date") String end_date,
                                            @Field("venue_id") String venue_id);

    //edit work history
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoNoDataResponse> editWorkHistory(@Field("user_id") String user_id,
                                             @Field("type") String type,
                                             @Field("work_type") String work_type,
                                             @Field("subject") String subject,
                                             @Field("institution") String institution,
                                             @Field("start_date") String start_date,
                                             @Field("end_date") String end_date,
                                             @Field("id") String id,
                                             @Field("venue_id") String venue_id);

    //get user workList
    @FormUrlEncoded
    @POST("settings.php")
    Call<PojoUserWorkListResponse> getUserWorkList(@Field("user_id") String user_id,
                                                   @Field("target_user_id") String target_user_id,
                                                   @Field("type") String type);

    //get logged in devices id
    @FormUrlEncoded
    @POST("logged_in_devices.php")
    Call<PojoLoggedInDevicesResponse> getLoggedInDevices(@Field("email") String email);


    //register organisation
    @FormUrlEncoded
    @POST("organizationadd.php")
    Call<PojoRegisterOrganisationResponse> registerOrganisation(@Field("user_id") String user_id,
                                                                @Field("companyname") String companyname,
                                                                @Field("legalname") String legalname,
                                                                @Field("type") String type,
                                                                @Field("companywebsite") String companywebsite,
                                                                @Field("companycontactmail") String companycontactmail,
                                                                @Field("companycontact") String companycontact);

    @FormUrlEncoded
    @POST("organizationotpverify.php")
    Call<PojoNoDataResponse> verifyOrganisation(@Field("user_id") String user_id,
                                                @Field("companyid") String companyid,
                                                @Field("otp") String otp);


    //complete organisation registration
    @Multipart
    @POST("edit_organization.php")
    Call<PojoNoDataResponse> completeCompanyRegistration(@Part MultipartBody.Part file,
                                                         @Part("user_id") RequestBody user_id,
                                                         @Part("id") RequestBody id,
                                                         @Part("city") RequestBody city,
                                                         @Part("description") RequestBody description,
                                                         @Part("address") RequestBody address,
                                                         @Part("noofemployee") RequestBody noofemployee);

    //edit organisation
    @Multipart
    @POST("edit_organization.php")
    Call<PojoNoDataResponse> editOrganisation(@Part MultipartBody.Part file,
                                              @Part("user_id") RequestBody user_id,
                                              @Part("id") RequestBody id,
                                              @Part("name") RequestBody name,
                                              @Part("type") RequestBody type,
                                              @Part("city") RequestBody city,
                                              @Part("description") RequestBody description,
                                              @Part("address") RequestBody address,
                                              @Part("noofemployee") RequestBody noofemployee,
                                              @Part("website") RequestBody website);


    //session connects
    @FormUrlEncoded
    @POST("session/connect.php")
    Call<PojoAddSessionDiscussionResponse> sessionConnects(@Field("user_id") String user_id,
                                                           @Field("type") String type,
                                                           @Field("session_id") String session_id,
                                                           @Field("discussion_post") String discussion_post,
                                                           @Field("comment_post") String comment_post,
                                                           @Field("question_post") String question_post,
                                                           @Field("question_id") String question_id);

}

