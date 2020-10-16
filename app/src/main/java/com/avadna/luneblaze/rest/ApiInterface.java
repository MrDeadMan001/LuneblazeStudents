package com.avadna.luneblaze.rest;

import com.avadna.luneblaze.pojo.PojoConductSessionResponse;
import com.avadna.luneblaze.pojo.PojoCreatePostResponse;
import com.avadna.luneblaze.pojo.PojoGetFriendRequestResponse;
import com.avadna.luneblaze.pojo.PojoGetFriendsListResponse;
import com.avadna.luneblaze.pojo.PojoGetNotificationListResponse;
import com.avadna.luneblaze.pojo.PojoGetSuggestedFriendListResponse;
import com.avadna.luneblaze.pojo.PojoGetUserOnlineStatusResponse;
import com.avadna.luneblaze.pojo.PojoInvitesListResponse;
import com.avadna.luneblaze.pojo.PojoLatestVesionResponse;
import com.avadna.luneblaze.pojo.PojoLoggedInDevicesResponse;
import com.avadna.luneblaze.pojo.PojoLoginResponse;
import com.avadna.luneblaze.pojo.PojoLogoutResponse;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoReportContentResponse;
import com.avadna.luneblaze.pojo.PojoSentRequestListResponse;
import com.avadna.luneblaze.pojo.PojoSuggestedArticlesResponse;
import com.avadna.luneblaze.pojo.PojoUpdateTokenResponse;
import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.avadna.luneblaze.pojo.PojoUserInterestsListResponse;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentListResponse;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentTypeResponse;
import com.avadna.luneblaze.pojo.assessment.PojoGetAssessmentCategoriesResponse;
import com.avadna.luneblaze.pojo.assessment.PojoInitiatePaymentResponse;
import com.avadna.luneblaze.pojo.instamojo.GatewayOrderStatus;
import com.avadna.luneblaze.pojo.instamojo.GetOrderIDRequest;
import com.avadna.luneblaze.pojo.instamojo.GetOrderIDResponse;
import com.avadna.luneblaze.pojo.pojoActiveSessions.PojoActiveSessionsResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoAddArticleCommentResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoAddArticleReplyResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoArticleCommentsResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoCreateArticleResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoGetArticleRepliesResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateChatGroupResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateMessageResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateMultiMessageResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateNewConversationResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoGetChatMessagesResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoGetAllMessageListResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoGetGroupDetailsResponse;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestDetailsResponse;
import com.avadna.luneblaze.pojo.pojoInterestHierarchy.PojoAllInterestListResponse;
import com.avadna.luneblaze.pojo.pojoInvite.PojoInviteFriendResponse;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoCampusDriveListResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoCampusDrivePriceResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoCreateOrganisationResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetAdTargetsResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetCampusBookedDatesResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetCitiesResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetCollegeListForDriveResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetOrganisationInfoResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetOrganisationTypeResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetSponsorPlansResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetSponsorSessionListResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoRegisterOrganisationResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoSponsorPlanDetailsResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoUserOrganisationListResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoGetRegisteredContactsListResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoGetUserInterestsResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoGetUserVenueResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoProfileInfoResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoUserWorkListResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.tabsData.PojoProfileTabResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoEditQaResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoGetAnswerRepliesResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoGetQuestionDetailsResponse;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoAddSessionCommentReplyResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoAddSessionDiscussionReplyResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoAddSessionDiscussionResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoGetSessionRepliesResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoGetAssessmentPaymentStatus;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUpdateAppSettingsResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserEmailPhoneResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponse;
import com.avadna.luneblaze.pojo.pojoSuggestedInterests.PojoSuggestedInterestResponse;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoGetVenueDetailsResponse;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponse;
import com.avadna.luneblaze.pojo.PojoSignUp1Response;
import com.avadna.luneblaze.pojo.PojoSignUp2Response;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoGetAllSessionsResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoGetArticleDetailsResponse;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponse;
import com.avadna.luneblaze.pojo.pojonormalpost.PojoAddPostCommentResponse;
import com.avadna.luneblaze.pojo.pojonormalpost.pojoreplylist.PojoAddNewCommentReplyResponse;
import com.avadna.luneblaze.pojo.pojonormalpost.pojoreplylist.PojoGetNormalPostReplyResponse;
import com.avadna.luneblaze.pojo.pojonormalpost.PojoNormalPostResponse;
import com.avadna.luneblaze.pojo.razorpay.PojoRazorpayGetOrder;
import com.avadna.luneblaze.pojo.registration.PojoGetInterestSiblingsResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by Sunny on 24-12-2017.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("login.php")
    Call<PojoLoginResponse> loginApi(@Field("email") String email,
                                     @Field("password") String password,
                                     @Field("user_os") String user_os,
                                     @Field("user_ip") String user_ip,
                                     @Field("session_token") String session_token,
                                     @Field("deviceid") String deviceid);

    @FormUrlEncoded
    @POST("login_two_step.php")
    Call<PojoNoDataResponse> loginTwoStepApi(@Field("user_id") String user_id,
                                             @Field("otp") String otp);

    @FormUrlEncoded
    @POST("forget_password.php")
    Call<PojoNoDataResponse> sendResetKeyToMail(@Field("email") String email,
                                                @Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("forget_password_reset.php")
    Call<PojoNoDataResponse> resetPasswordWithKey(@Field("email") String email,
                                                  @Field("mobile") String mobile,
                                                  @Field("reset_key") String reset_key,
                                                  @Field("password") String password,
                                                  @Field("confirm") String confirm);


    @FormUrlEncoded
    @POST("signup.php")
    Call<PojoSignUp1Response> registrationApi(@Field("first_name") String first_name,
                                              @Field("last_name") String last_name,
                                              @Field("email") String email,
                                              @Field("password") String password,
                                              @Field("gender") String gender,
                                              @Field("birth_day") String birth_day,
                                              @Field("birth_month") String birth_month,
                                              @Field("birth_year") String birth_year,
                                              @Field("phone") String phone,
                                              @Field("current_FOI") String current_FOI);

    @FormUrlEncoded
    @POST("signupverify.php")
    Call<PojoNoDataResponse> verifySignup(@Field("user_id") String user_id,
                                          @Field("otp") String otp);

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

    /*@FormUrlEncoded
    @POST("get_interest_siblings.php")
    Call<PojoGetInterestSiblingsResponse> getInterestSiblings(@Field("user_id") String user_id,
                                                              @Field("interest_id") String interest_id,
                                                              @Field("siblings") String siblings);

    @FormUrlEncoded
    @POST("get_user_interest.php")
    Call<PojoUserInterestsListResponse> getUserInterests(@Field("user_id") String user_id);*/

    @FormUrlEncoded
    @POST("update_user_interest.php")
    Call<PojoNoDataResponse> updateUserInterest(@Field("user_id") String user_id,
                                                @FieldMap(encoded = false) Map<String, String> interestMap);

    @FormUrlEncoded
    @POST("get_friend_request.php")
    Call<PojoGetFriendRequestResponse> getFriendRequestList(@Field("user_id") String user_id);

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


    @FormUrlEncoded
    @POST("get_organization.php")
    Call<PojoGetOrganisationInfoResponse> getOrganisationInfo(@Field("user_id") String user_id,
                                                              @Field("organization_id") String organization_id,
                                                              @Field("posttype") String posttype,
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

    //add comment on normal post
    @FormUrlEncoded
    @POST("comments.php")
    Call<PojoAddPostCommentResponse> addCommentOnPost(@Field("user_id") String user_id,
                                                      @Field("handle") String handle,
                                                      @Field("id") String id,
                                                      @Field("message") String message);


    //get normal post comment's replies
    @FormUrlEncoded
    @POST("post/comment_replies.php")
    Call<PojoGetNormalPostReplyResponse> getPostCommentReplies(@Field("user_id") String user_id,
                                                               @Field("comment_id") String comment_id,
                                                               @Field("reply_comment_id") String reply_comment_id,
                                                               @Field("order") String order,
                                                               @Field("target") String target);

    //add reply to normal post comment
    @FormUrlEncoded
    @POST("post_comment_reply.php")
    Call<PojoAddNewCommentReplyResponse> addReplyOnPostComment(@Field("user_id") String user_id,
                                                               @Field("action") String action,
                                                               @Field("text") String text,
                                                               @Field("node_type") String node_type,
                                                               @Field("user_type") String user_type,
                                                               @Field("parent_id") String parent_id);

    //edit reply on normal post comment
    @FormUrlEncoded
    @POST("post_comment_reply.php")
    Call<PojoNoDataResponse> editReplyOnPostComment(@Field("user_id") String user_id,
                                                    @Field("action") String action,
                                                    @Field("comment_id") String comment_id,
                                                    @Field("text") String text);

    //delete reply on normal post comment
    @FormUrlEncoded
    @POST("post_comment_reply.php")
    Call<PojoNoDataResponse> deleteReplyOnPostComment(@Field("user_id") String user_id,
                                                      @Field("action") String action,
                                                      @Field("comment_id") String comment_id);


    @FormUrlEncoded
    @POST("get_friend_request_sent.php")
    Call<PojoSentRequestListResponse> getSentFriendRequestList(@Field("user_id") String user_id);


    @FormUrlEncoded
    @POST("get_friends.php")
    Call<PojoGetFriendsListResponse> getFriendListApi(@Field("user_id") String user_id,
                                                      @Field("target_user_id") String target_user_id,
                                                      @Field("search") String search,
                                                      @Field("privacy_message") String privacy_message,
                                                      @Field("privacy_tag") String privacy_tag,
                                                      @Field("offset") String offset);


    //get Friends not present in group
    @FormUrlEncoded
    @POST("get_friends.php")
    Call<PojoGetFriendsListResponse> getFriendsNotInGroup(@Field("user_id") String user_id,
                                                          @Field("target_user_id") String target_user_id,
                                                          @Field("group_id") String group_id,
                                                          @Field("search") String search,
                                                          @Field("privacy_message") String privacy_message,
                                                          @Field("privacy_tag") String privacy_tag,
                                                          @Field("offset") String offset);


    @FormUrlEncoded
    @POST("notifications.php")
    Call<PojoGetNotificationListResponse> getNotificationList(@Field("user_id") String user_id,
                                                              @Field("offset") String offset,
                                                              @Field("last_notification_id") String last_notification_id);

    @FormUrlEncoded
    @POST("read_notification.php")
    Call<PojoNoDataResponse> seenNotification(@Field("user_id") String user_id,
                                              @Field("notification_id") String notification_id);

    @FormUrlEncoded
    @POST("notification_seen.php")
    Call<PojoNoDataResponse> seeAllNotification(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("friend_suggestions.php")
    Call<PojoGetSuggestedFriendListResponse> getSuggestedFriendListApi(@Field("user_id") String user_id,
                                                                       @Field("page") String page);

    @FormUrlEncoded
    @POST("suggestion.php")
    Call<PojoSuggestedArticlesResponse> getSuggestedArticles(@Field("user_id") String user_id,
                                                             @Field("type") String type);


    @FormUrlEncoded
    @POST("connect.php")
    Call<PojoUserConnectResponse> changeUserConnectionApi(@Field("user_id") String user_id,
                                                          @Field("id") String id,
                                                          @Field("connect_type") String connect_type);

    //delete email or phone number
    @FormUrlEncoded
    @POST("delete_user_extra.php")
    Call<PojoNoDataResponse> deletePhoneOrEmail(@Field("user_id") String user_id,
                                                @Field("type") String type,
                                                @Field("data") String data);

    @FormUrlEncoded
    @POST("connect.php")
    Call<PojoUserConnectResponse> addFriendApi(@Field("user_id") String user_id,
                                               @Field("id[0]") String id,
                                               @Field("connect_type") String connect_type);


    @FormUrlEncoded
    @POST("search_interest.php")
    Call<PojoSearchInterestWithTextResponse> searchInterestWithTextApi(@Field("text") String text,
                                                                       @Field("offset") String offset);


    @FormUrlEncoded
    @POST("session/venue.php")
    Call<PojoGetVenueDetailsResponse> getVenueDetailsFromIdApi(@Field("user_id") String user_id,
                                                               @Field("venue_id") String venue_id,
                                                               @Field("session_type") String session_type);

    //submit venue ratings
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

    //add venue photo
    @Multipart
    @POST("session/venue_photoadd.php")
    Call<PojoNoDataResponse> addVenueImage(@Part("user_id") RequestBody user_id,
                                           @Part("venue_id") RequestBody venue_id,
                                           @Part MultipartBody.Part file);

    //remove venue photo
    @FormUrlEncoded
    @POST("session/venue_photodelete.php")
    Call<PojoNoDataResponse> deleteVenueImage(@Field("user_id") String user_id,
                                              @Field("venue_id") String venue_id,
                                              @Field("photo_id") String photo_id);


    //remove venue photo
    @FormUrlEncoded
    @POST("session/venue_coverphoto.php")
    Call<PojoNoDataResponse> makeVenueCoverPhoto(@Field("user_id") String user_id,
                                                 @Field("venue_id") String venue_id,
                                                 @Field("photo_id") String photo_id);

   /* @FormUrlEncoded
    @POST("session/venue.php")
    Call<PojoGetVenueDetailsResponse> getVenueBasedSessions(@Field("user_id") String user_id,
                                                            @Field("venue_id") String venue_id,
                                                            @Field("session_type") String session_type,
                                                            @Field("offset") String offset,
                                                            @Field("session_roles") String session_roles,
                                                            @FieldMap(encoded = false) Map<String, String> interestMap,
                                                            @Field("my_interest") String my_interest,
                                                            @Field("time_based") String time_based,
                                                            @Field("start_date") String start_date,
                                                            @Field("end_date") String end_date);*/


    @FormUrlEncoded
    @POST("session/get_venue_list.php")
    Call<PojoGetVenueListResponse> searchVenueWithTextApi(@Field("latitude") String latitude,
                                                          @Field("longitude") String longitude,
                                                          @Field("text") String text,
                                                          @Field("offset") String offset);

    @FormUrlEncoded
    @POST("session/set_session_status.php")
    Call<PojoNoDataResponse> startSession(@Field("user_id") String user_id,
                                          @Field("session_id") String session_id,
                                          @Field("is_live") String is_live);

    @FormUrlEncoded
    @POST("session/suggest_poll_venue.php")
    Call<PojoGetVenueListResponse> searchVenueForPoll(@Field("user_id") String user_id,
                                                      @Field("session_id") String session_id,
                                                      @Field("search") String text);


    //create interest
    @FormUrlEncoded
    @POST("send_interest_request.php")
    Call<PojoNoDataResponse> createInterest(@Field("user_id") String user_id,
                                            @Field("interest_name") String interest_name,
                                            @Field("remarks") String remarks,
                                            @Field("parent_interest_name") String parent_interest_name);

    //create_artcle
    @Multipart
    @POST("articles.php")
    Call<PojoCreateArticleResponse> createArticle(@Part("user_id") RequestBody user_id,
                                                  @Part("title") RequestBody title,
                                                  @Part("description") RequestBody description,
                                                  @Part MultipartBody.Part file,
                                                  @PartMap Map<String, RequestBody> interests);

   /* @FormUrlEncoded
    @POST("get_posts.php")
    Call<PojoUserConnectResponse> getPostsApi(@Field("user_id") String user_id,
                                              @Field("get") String get,
                                              @Field("filter") String filter,
                                              @Field("offset") String offset);*/

    @FormUrlEncoded
    @POST("update_token.php")
    Call<PojoUpdateTokenResponse> updateFcmToken(@Field("user_id") String user_id,
                                                 @Field("device_id") String device_id,
                                                 @Field("device_type") String device_type,
                                                 @Field("device_token") String device_token);

   /* //interest suggestions
    @FormUrlEncoded
    @POST("suggestion.php")
    Call<PojoSuggestedInterestResponse> getSuggestedInterests(@Field("user_id") String user_id,
                                                              @Field("type") String type,
                                                              @Field("parent_id") String parent_id,
                                                              @Field("interest_id") String interest_id,
                                                              @Field("offset") String offset);*/

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


    //edit article comments
    @FormUrlEncoded
    @POST("session/edit_article_discussion.php")
    Call<PojoNoDataResponse> editArticleComment(@Field("user_id") String user_id,
                                                @Field("articles_discussion_id") String articles_discussion_id,
                                                @Field("post") String post);

    //delete article comments
    @FormUrlEncoded
    @POST("session/delete_article_discussion.php")
    Call<PojoNoDataResponse> deleteArticleComment(@Field("user_id") String user_id,
                                                  @Field("articles_discussion_id") String articles_discussion_id);


    //add reply on article comment
    @FormUrlEncoded
    @POST("article_comment_reply.php")
    Call<PojoAddArticleReplyResponse> addReplyOnArticleComment(@Field("user_id") String user_id,
                                                               @Field("article_id") String article_id,
                                                               @Field("article_comment_id") String article_comment_id,
                                                               @Field("message") String message);

    /*@FormUrlEncoded
    @POST("get_article_likes.php")
    Call<PojoGetSuggestedFriendListResponse> articleUpvoter(@Field("user_id") String user_id,
                                                            @Field("article_id") String article_id,
                                                            @Field("offset") String offset);*/

    @FormUrlEncoded
    @POST("get_article_shares.php")
    Call<PojoGetSuggestedFriendListResponse> articleSharer(@Field("user_id") String user_id,
                                                           @Field("article_id") String article_id,
                                                           @Field("offset") String offset);


    @FormUrlEncoded
    @POST("article_reaction.php")
    Call<PojoReportContentResponse> articleReaction(@Field("user_id") String user_id,
                                                    @Field("article_id") String article_id,
                                                    @Field("reaction") String reaction);

    //edit article photo
    @Multipart
    @POST("edit_type.php")
    Call<PojoNoDataResponse> editArticlephoto(@Part("user_id") RequestBody user_id,
                                              @Part("type") RequestBody type,
                                              @Part("id") RequestBody article_id,
                                              @Part("message") RequestBody message,
                                              @Part("description") RequestBody description,
                                              @Part MultipartBody.Part file);

 /*   // edit article content
    @FormUrlEncoded
    @POST("edit_type.php")
    Call<PojoNoDataResponse> editArticleContent(@Field("user_id") String user_id,
                                                @Field("type") String type,
                                                @Field("id") String article_id,
                                                @Field("message") String message,
                                                @Field("description") String description);*/

    @FormUrlEncoded
    @POST("edit_type.php")
    Call<PojoNoDataResponse> editPostComment(@Field("user_id") String user_id,
                                             @Field("type") String type,
                                             @Field("id") String article_id,
                                             @Field("message") String message);

    @FormUrlEncoded
    @POST("article_comment.php")
    Call<PojoAddArticleCommentResponse> addCommentOnArticle(@Field("user_id") String user_id,
                                                            @Field("article_id") String article_id,
                                                            @Field("message") String message);

    //all sessions api old
    @FormUrlEncoded
    @POST("session/all_sessions.php")
    Call<PojoGetAllSessionsResponse> getAllSessionsApiOld(@Field("user_id") String user_id,
                                                          @Field("sort_by") String sort_by,
                                                          @Field("order_by") String order_by,
                                                          @Field("offset") String offset,
                                                          @Field("lat") String latitude,
                                                          @Field("long") String longitude,
                                                          @Field("session_roles") String session_roles,
                                                          @Field("location_based") String location_based,
                                                          @FieldMap(encoded = false) Map<String, String> venueMap,
                                                          @Field("my_venue") String my_venue,
                                                          @FieldMap(encoded = false) Map<String, String> interestMap,
                                                          @Field("my_interest") String my_interest,
                                                          @Field("time_based") String time_based,
                                                          @Field("start_date") String start_date,
                                                          @Field("end_date") String end_date);


    //get college list with number of eligible students
    @FormUrlEncoded
    @POST("get_college.php")
    Call<PojoGetCollegeListForDriveResponse> getStudentsPerCollege(@Field("user_id") String user_id,
                                                                   @Field("noofsession") String noofsession,
                                                                   @Field("session_roles") String session_roles,
                                                                   @FieldMap(encoded = false) Map<String, String> interestMap,
                                                                   @FieldMap(encoded = false) Map<String, String> categoryMap,
                                                                   @FieldMap(encoded = false) Map<String, String> dependencyMap,
                                                                   @FieldMap(encoded = false) Map<String, String> and_or_Map);

    //all sessions api new
    @FormUrlEncoded
    @POST("session/all_sessions_v1.php")
    Call<PojoGetAllSessionsResponse> getAllSessionsApiNew(@Field("user_id") String user_id,
                                                          @Field("session_type") String session_type,
                                                          //  @Field("sort_by") String sort_by,
                                                          @Field("order_by") String order_by,
                                                          @Field("last_session_time") String lastSessionTime,
                                                          @Field("lat") String latitude,
                                                          @Field("long") String longitude,
                                                          @Field("session_roles") String session_roles,
                                                          @Field("location_based") String location_based,
                                                          @FieldMap(encoded = false) Map<String, String> venueMap,
                                                          @Field("my_venue") String my_venue,
                                                          @FieldMap(encoded = false) Map<String, String> interestMap,
                                                          @Field("my_interest") String my_interest,
                                                          @Field("time_based") String time_based,
                                                          @Field("start_date") String start_date,
                                                          @Field("end_date") String end_date);


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


    //mark user attend status
    @FormUrlEncoded
    @POST("session/mark_user_attend_status.php")
    Call<PojoNoDataResponse> markAttendStatus(@Field("user_id") String user_id,
                                              @Field("session_id") String session_id,
                                              @Field("i_attend") String i_attend);

    //like unlike session discussion,comment, article comment/reply
    @FormUrlEncoded
    @POST("posts_reaction.php")
    Call<PojoNoDataResponse> likeUnlikeSessionDiscussion(@Field("user_id") String user_id,
                                                         @Field("id") String id,
                                                         @Field("reaction") String reaction);


    //delete session discussion
    @FormUrlEncoded
    @POST("session/delete_session_discussion.php")
    Call<PojoNoDataResponse> deleteSessionDiscussionAndReply(@Field("user_id") String user_id,
                                                             @Field("sessions_discussion_id") String sessions_discussion_id);

    //delete session comment
    @FormUrlEncoded
    @POST("session/delete_session_comment.php")
    Call<PojoNoDataResponse> deleteSessionCommentAndReply(@Field("user_id") String user_id,
                                                          @Field("sessions_comment_id") String sessions_comment_id);


    //get replies on session comments
    @FormUrlEncoded
    @POST("session/get_comment_replies.php")
    Call<PojoGetSessionRepliesResponse> getSessionCommentReplies(@Field("user_id") String user_id,
                                                                 @Field("sessions_comment_id") String sessions_comment_id,
                                                                 @Field("comment_id") String comment_id,
                                                                 @Field("order") String order,
                                                                 @Field("target") String target);

    //edit session comment and comment replies
    @FormUrlEncoded
    @POST("session/edit_session_comment.php")
    Call<PojoNoDataResponse> editSessionCommentAndRepliy(@Field("user_id") String user_id,
                                                         @Field("sessions_comment_id") String sessions_comment_id,
                                                         @Field("post") String post);


    //get replies on session discussions
    @FormUrlEncoded
    @POST("session/get_discussion_replies.php")
    Call<PojoGetSessionRepliesResponse> getSessionDiscussionReplies(@Field("user_id") String user_id,
                                                                    @Field("sessions_discussion_id") String sessions_discussion_id,
                                                                    @Field("comment_id") String comment_id,
                                                                    @Field("order") String order,
                                                                    @Field("target") String target);

    //edit session discussions and discussion replies
    @FormUrlEncoded
    @POST("session/edit_session_discussion.php")
    Call<PojoNoDataResponse> editSessionDiscussionAndReply(@Field("user_id") String user_id,
                                                           @Field("sessions_discussion_id") String sessions_discussion_id,
                                                           @Field("post") String post);


    //add reply on session comment
    @FormUrlEncoded
    @POST("session/post_comment_reply.php")
    Call<PojoAddSessionCommentReplyResponse> addSessionCommentReply(@Field("user_id") String user_id,
                                                                    @Field("sessions_id") String sessions_id,
                                                                    @Field("post") String post,
                                                                    @Field("parent_comment_id") String parent_comment_id);

    //add reply on session discussion
    @FormUrlEncoded
    @POST("session/post_discussion_reply.php")
    Call<PojoAddSessionDiscussionReplyResponse> addSessionDiscussionReply(@Field("user_id") String user_id,
                                                                          @Field("sessions_id") String sessions_id,
                                                                          @Field("post") String post,
                                                                          @Field("parent_discussion_id") String parent_discussion_id);

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

    //upload session file
    @Multipart
    @POST("session/upload_video.php")
    Call<PojoNoDataResponse> uploadSessionFile(@Part("user_id") RequestBody user_id,
                                               @Part("session_id") RequestBody group_id,
                                               @Part("type") RequestBody group_name,
                                               @Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST(" session/session_photodelete.php")
    Call<PojoNoDataResponse> sessionPhotoDelete(@Field("user_id") String user_id,
                                                @Field("session_id") String sessions_id,
                                                @Field("photo_name") String photo_name);

    @FormUrlEncoded
    @POST("session/suggest_new_venues.php")
    Call<PojoNoDataResponse> suggestVenue(@Field("user_id") String user_id,
                                          @Field("session_id") String sessions_id,
                                          @Field("venue_id") String venue_id,
                                          @FieldMap(encoded = false) Map<String, String> dateMap);

    @FormUrlEncoded
    @POST("session/suggest_new_dates.php")
    Call<PojoNoDataResponse> suggestDate(@Field("user_id") String user_id,
                                         @Field("session_id") String sessions_id,
                                         @Field("venue") String venue,
                                         @Field("dates[0]") String dates);

    @FormUrlEncoded
    @POST("session/poll.php")
    Call<PojoNoDataResponse> votePoll(@Field("user_id") String user_id,
                                      @Field("session_id") String sessions_id,
                                      @Field("option_ids[0]") String option_id);

    @FormUrlEncoded
    @POST("session/delete_poll.php")
    Call<PojoNoDataResponse> uncastPollOption(@Field("user_id") String user_id,
                                              @Field("session_id") String sessions_id,
                                              @Field("option_id") String option_id);

    //get poll upvoter list
    @FormUrlEncoded
    @POST("session/get_poll_voting_users.php")
    Call<PojoGetSuggestedFriendListResponse> getPollUpvoterList(@Field("user_id") String user_id,
                                                                @Field("option_id") String option_id,
                                                                @Field("offset") String offset);

    //accept session
    @FormUrlEncoded
    @POST("session/edit.php")
    Call<PojoNoDataResponse> acceptSession(@Field("handle") String handle,
                                           @Field("user_id") String user_id,
                                           @Field("session_id") String session_id,
                                           @Field("ans1") String ans1,
                                           @Field("ans2") String ans2,
                                           @Field("total_allowed_members") String total_allowed_members);

    //finalise session
    @FormUrlEncoded
    @POST("session/edit.php")
    Call<PojoNoDataResponse> finaliseSession(@Field("handle") String handle,
                                             @Field("user_id") String user_id,
                                             @Field("session_id") String session_id,
                                             @Field("event_date") String event_date,
                                             @Field("event_time") String event_time,
                                             @Field("event_duration") String event_duration,
                                             @Field("venue_id") String venue_id);

    //finalise session
    @FormUrlEncoded
    @POST("session/edit.php")
    Call<PojoNoDataResponse> editSessionTopics(@Field("handle") String handle,
                                               @Field("user_id") String user_id,
                                               @Field("session_id") String session_id,
                                               @FieldMap(encoded = false) Map<String, String> topics,
                                               @FieldMap(encoded = false) Map<String, String> prerequisite);

    @FormUrlEncoded
    @POST("session/assign_coordinators.php")
    Call<PojoNoDataResponse> assignSessionCoordinators(@Field("session_id") String session_id,
                                                       @FieldMap(encoded = false) Map<String, String> fields);

    @FormUrlEncoded
    @POST("users_status.php")
    Call<PojoGetUserOnlineStatusResponse> getUserOnlineStatus(@FieldMap(encoded = false) Map<String,
            String> fields);


    @FormUrlEncoded
    @POST("session/assign_room.php")
    Call<PojoNoDataResponse> assignSessionRoom(@Field("session_id") String session_id,
                                               @Field("final_place") String final_place);

    //this is used to fetch old message while scrolling message list
    @FormUrlEncoded
    @POST("chat/get_messagesnew.php")
    Call<PojoGetChatMessagesResponse> getUserChatMessages(@Field("user_id") String user_id,
                                                          @Field("cid") String cid,
                                                          @Field("last_message_id") String last_message_id,
                                                          @Field("limit") String limit);

    @FormUrlEncoded
    @POST("chat/delete.php")
    Call<PojoNoDataResponse> deleteConversation(@Field("user_id") String user_id,
                                                @Field("conversation_id") String conversation_id);

    //this is used to fetch new messages after a given message id
    @FormUrlEncoded
    @POST("chat/get_messagesnew.php")
    Call<PojoGetChatMessagesResponse> getUserChatNewMessages(@Field("user_id") String user_id,
                                                             @Field("cid") String cid,
                                                             @Field("last_message_id") String last_message_id,
                                                             @Field("order") String order);

    @FormUrlEncoded
    @POST("chat/get_messagesnew.php")
    Call<PojoGetAllMessageListResponse> getUserAllMsgList(@Field("user_id") String user_id,
                                                          @Field("last_message_id") String last_message_id,
                                                          @Field("query") String query);


    //create chat group
    @Multipart
    @POST("chat/create_group.php")
    Call<PojoCreateChatGroupResponse> createChatGroup(@Part("user_id") RequestBody user_id,
                                                      @Part("group_name") RequestBody group_name,
                                                      @Part MultipartBody.Part file,
                                                      @PartMap Map<String, RequestBody> recipients);

    //edit chat group
    @Multipart
    @POST("chat/edit_group.php")
    Call<PojoNoDataResponse> editChatGroup(@Part("user_id") RequestBody user_id,
                                           @Part("group_id") RequestBody group_id,
                                           @Part("group_name") RequestBody group_name,
                                           @Part MultipartBody.Part file);

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

    //all sessions api old
    @FormUrlEncoded
    @POST("adtarget_user.php")
    Call<PojoGetAdTargetsResponse> getAdsTargetCount(@FieldMap(encoded = false) Map<String, String> venueMap,
                                                     @Field("minage") String minage,
                                                     @Field("maxage") String maxage,
                                                     @Field("latitude") String latitude,
                                                     @Field("longitude") String longitude,
                                                     @Field("radius") String radius,
                                                     @Field("gender") String gender);

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

    //delete post photo
    @FormUrlEncoded
    @POST("delete_post_photo.php")
    Call<PojoNoDataResponse> deletePostPhoto(@Field("user_id") String user_id,
                                             @Field("post_id") String post_id,
                                             @Field("photo_id") String photo_id);

    //edit poll post
    @FormUrlEncoded
    @POST("edit_type.php")
    Call<PojoNoDataResponse> editPollPost(@Field("user_id") String user_id,
                                          @Field("type") String type,
                                          @Field("id") String id,
                                          @Field("message") String message,
                                          @FieldMap(encoded = false) Map<String, String> optionMap);

    //create poll post
    @FormUrlEncoded
    @POST("post.php")
    Call<PojoCreatePostResponse> createPollPost(@Field("user_id") String user_id,
                                                @Field("handle") String handle,
                                                @Field("message") String message,
                                                @Field("privacy") String latitude,
                                                @FieldMap(encoded = false) Map<String, String> optionMap);

    //cast vote on poll post
    @FormUrlEncoded
    @POST("posts_reaction.php")
    Call<PojoNoDataResponse> castVoteOnPollPost(@Field("user_id") String user_id,
                                                @Field("reaction") String reaction,
                                                @Field("id") String id);

    //Create conversation
    @FormUrlEncoded
    @POST("chat/create_get_conversation.php")
    Call<PojoCreateNewConversationResponse> createConversation(@Field("user_id") String user_id,
                                                               @Field("recipient_id") String recipient_id);


    //send txt msg
    @FormUrlEncoded
    @POST("chat/create_message.php")
    Call<PojoCreateMessageResponse> replyToMessage(@Field("user_id") String user_id,
                                                   @Field("message") String message,
                                                   @Field("temp_msg_id") String temp_msg_id,
                                                   @Field("recipients[0]") String recipient,
                                                   @Field("conversation_id") String conversation_id);

    //send image with msg
    @Multipart
    @POST("chat/create_message.php")
    Call<PojoCreateMessageResponse> uploadChatImage(@Part MultipartBody.Part file,
                                                    @Part("user_id") RequestBody user_id,
                                                    @Part("message") RequestBody message,
                                                    @Part("temp_msg_id") RequestBody temp_msg_id,
                                                    @Part("recipients[0]") RequestBody recipients,
                                                    @Part("conversation_id") RequestBody conversation_id);


    //forward chat message
    @FormUrlEncoded
    @POST("chat/create_multimessage.php")
    Call<PojoCreateMultiMessageResponse> forwardChatMessage(@Field("user_id") String user_id,
                                                            @FieldMap(encoded = false) Map<String, String> message_id,
                                                            @FieldMap(encoded = false) Map<String, String> conversation_id,
                                                            @FieldMap(encoded = false) Map<String, String> recipients);

    //chat seen
    @FormUrlEncoded
    @POST("chat/message_seen.php")
    Call<PojoNoDataResponse> conversationSeen(@Field("user_id") String user_id,
                                              @Field("conversation_id") String conversation_id,
                                              @Field("message_id") String message_id);

    //leave group

    @DELETE("chat/leave_group.php")
    Call<PojoNoDataResponse> leaveGroup(@Query("user_id") String user_id,
                                        @Query("group_id") String group_id);

    //add  member to group
    @FormUrlEncoded
    @POST("chat/add_group_members.php")
    Call<PojoNoDataResponse> addMemberToGroup(@Field("user_id") String user_id,
                                              @Field("group_id") String group_id,
                                              @Field("recipients[0]") String recipient);

    //remove  member from group
    @FormUrlEncoded
    @POST("chat/remove_members.php")
    Call<PojoNoDataResponse> removeGroupMember(@Field("user_id") String user_id,
                                               @Field("group_id") String group_id,
                                               @Field("members[0]") String member);

    //make group admin
    @FormUrlEncoded
    @POST("chat/add_admin.php")
    Call<PojoNoDataResponse> makeUserAdmin(@Field("user_id") String user_id,
                                           @Field("group_id") String group_id,
                                           @Field("member_id") String member_id);

    //revoke admin status
    @FormUrlEncoded
    @POST("chat/remove_admin.php")
    Call<PojoNoDataResponse> revokeAdminStatus(@Field("user_id") String user_id,
                                               @Field("group_id") String group_id,
                                               @Field("member_id") String member_id);

    //group info
    @GET("chat/get_group.php")
    Call<PojoGetGroupDetailsResponse> getGroupInfo(@Query("group_id") String group_id);

    //interest related apis

    @FormUrlEncoded
    @POST("interest.php")
    Call<PojoInterestDetailsResponse> getInterestDetails(@Field("user_id") String user_id,
                                                         @Field("interest_id") String interest_id,
                                                         @Field("type") String type,
                                                         @Field("session_type") String session_type);

    @FormUrlEncoded
    @POST("suggest_interest_edit.php")
    Call<PojoNoDataResponse> suggestInterestCorrection(@Field("user_id") String user_id,
                                                       @Field("interest_id") String interest_id,
                                                       @Field("remark") String remark);

    /*@FormUrlEncoded
    @POST("interest.php")
    Call<PojoInterestDetailsResponse> getInterestSessions(@Field("user_id") String user_id,
                                                          @Field("interest_id") String interest_id,
                                                          @Field("type") String type,
                                                          @Field("lat") String latitude,
                                                          @Field("long") String longitude,
                                                          @Field("session_roles") String session_roles,
                                                          @Field("location_based") String location_based,
                                                          @FieldMap(encoded = false) Map<String, String> venueMap,
                                                          @Field("my_venue") String my_venue,
                                                          @Field("time_based") String time_based,
                                                          @Field("start_date") String start_date,
                                                          @Field("end_date") String end_date,
                                                          @Field("offset") String offset);*/

    @FormUrlEncoded
    @POST("interest.php")
    Call<PojoInterestDetailsResponse> getInterestArticles(@Field("user_id") String user_id,
                                                          @Field("interest_id") String interest_id,
                                                          @Field("type") String type,
                                                          @Field("my_roles") String my_roles,
                                                          @Field("offset") String offset);

    @FormUrlEncoded
    @POST("interest.php")
    Call<PojoInterestDetailsResponse> getInterestQuestionList(@Field("user_id") String user_id,
                                                              @Field("interest_id") String interest_id,
                                                              @Field("type") String type,
                                                              @Field("relevance") String relevance,
                                                              @Field("my_roles") String my_roles,
                                                              @Field("offset") String offset);

    //Question api
    @FormUrlEncoded
    @POST("question.php")
    Call<PojoGetQuestionDetailsResponse> getQuestionDetails(@Field("user_id") String user_id,
                                                            @Field("question_id") String question_id,
                                                            @Field("type") String type,
                                                            @Field("sortby") String sortby,
                                                            @Field("order") String order,
                                                            @Field("sessions_qa_id") String sessions_qa_id,
                                                            @Field("target") String target);

    //get replies on answers
    @FormUrlEncoded
    @POST("session/get_answer_replies.php")
    Call<PojoGetAnswerRepliesResponse> getRepliesOnAnswer(@Field("user_id") String user_id,
                                                          @Field("sessions_qa_id") String sessions_qa_id,
                                                          @Field("comment_id") String comment_id,
                                                          @Field("order") String order,
                                                          @Field("target") String target);


    //edit question, answer, reply
    @FormUrlEncoded
    @POST("session/edit_session_answer.php")
    Call<PojoNoDataResponse> editQuesAnsReply(@Field("user_id") String user_id,
                                              @Field("sessions_qa_id") String sessions_qa_id,
                                              @Field("post") String post);


    //edit question, answer, reply
    @FormUrlEncoded
    @POST("posts_reaction.php")
    Call<PojoNoDataResponse> shareContent(@Field("user_id") String user_id,
                                          @Field("id") String id,
                                          @Field("reaction") String reaction,
                                          @Field("post_title") String post_title);

    //delete question, answer, reply
    @FormUrlEncoded
    @POST("session/delete_session_answer.php")
    Call<PojoNoDataResponse> deleteQuesAnsReply(@Field("user_id") String user_id,
                                                @Field("sessions_qa_id") String sessions_qa_id,
                                                @Field("post") String post);


    //edit question and answers
    @FormUrlEncoded
    @POST("question/post_edit_data.php")
    Call<PojoEditQaResponse> editQa(@Field("user_id") String user_id,
                                    @Field("type") String type,
                                    @Field("sessions_qa_id") String sessions_qa_id,
                                    @Field("post") String post);


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


    //global search api
    @FormUrlEncoded
    @POST("search.php")
    Call<PojoSearchResultResponse> globalSearch(@Field("user_id") String user_id,
                                                @Field("query") String query,
                                                @Field("offset") String offset,
                                                @Field("search_type") String search_type);

    //search post api
    @FormUrlEncoded
    @POST("search.php")
    Call<PojoSearchResultResponse> searchPosts(@Field("user_id") String user_id,
                                               @Field("query") String query,
                                               @Field("search_type") String search_type,
                                               @Field("last_postid") String last_postid,
                                               @Field("last_score") String last_score);

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

    //qa search api
    @FormUrlEncoded
    @POST("campus_drive_price.php")
    Call<PojoCampusDrivePriceResponse> getCampusDrivePrice(@Field("user_id") String user_id,
                                                           @Field("organization_id") String query);


    //initiate campus drive payment api
    @FormUrlEncoded
    @POST("schedule_campus.php")
    Call<PojoInitiatePaymentResponse>
    initiateCampusDrivePayment(@FieldMap(encoded = false) Map<String, String> positionMap,
                               @FieldMap(encoded = false) Map<String, String> descriptionMap,
                               @FieldMap(encoded = false) Map<String, String> countMap,
                               @Field("user_id") String user_id,
                               @Field("amount") String amount,
                               @Field("organization_id") String organization_id,
                               @Field("venuetype") String venuetype,
                               @Field("venue") String venue,
                               @Field("companyaddress") String companyaddress,
                               @Field("campusdate") String campusdate,
                               @FieldMap(encoded = false) Map<String, String> category,
                               @FieldMap(encoded = false) Map<String, String> marksDependencyMap,
                               @FieldMap(encoded = false) Map<String, String> andOrMap,
                               @Field("noofsession") String noofsession,
                               @Field("sessionroles") String sessionroles,
                               @Field("noofsessions") String noofsessions,
                               @Field("interests") String interests);


    //schedule campus drive api
    @FormUrlEncoded
    @POST("schedule_campus.php")
    Call<PojoNoDataResponse> updateCampusDrivePayment(@FieldMap(encoded = false) Map<String, String> positionMap,
                                                      @FieldMap(encoded = false) Map<String, String> descriptionMap,
                                                      @FieldMap(encoded = false) Map<String, String> countMap,
                                                      @Field("user_id") String user_id,
                                                      @Field("amount") String amount,
                                                      @Field("organization_id") String organization_id,
                                                      @Field("venuetype") String venuetype,
                                                      @Field("venue") String venue,
                                                      @Field("companyaddress") String companyaddress,
                                                      @Field("campusdate") String campusdate,
                                                      @FieldMap(encoded = false) Map<String, String> category,
                                                      @FieldMap(encoded = false) Map<String, String> marksDependencyMap,
                                                      @FieldMap(encoded = false) Map<String, String> andOrMap,
                                                      @Field("noofsession") String noofsession,
                                                      @Field("session_roles") String session_roles,
                                                      @Field("order_id") String order_id,
                                                      @Field("transaction_id") String transaction_id,
                                                      @Field("paymentid") String payment_id,
                                                      @Field("status") String status,
                                                      @Field("sessionroles") String sessionroles,
                                                      @Field("noofsessions") String noofsessions,
                                                      @Field("interests") String interests);


    //create instamojoorder
    @FormUrlEncoded
    @POST("payment_request1.php")
    Call<PojoCreatInstamojoOrderResponse> createInstaMojoOrder(@Field("user_id") String user_id,
                                                               @Field("name") String buyer_name,
                                                               @Field("email") String email,
                                                               @Field("phone") String phone,
                                                               @Field("amount") String amount,
                                                               @Field("purpose") String purpose,
                                                               @Field("transaction_id") String transaction_id,
                                                               @Field("currency") String currency);

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
    @POST("campus_booked.php")
    Call<PojoGetCampusBookedDatesResponse> getCampusBookedDate(@Field("venue_id") String venue_id);

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

    //get campus drive list api
    @FormUrlEncoded
    @POST("get_campusdrive.php")
    Call<PojoCampusDriveListResponse> getCampusDriveList(@Field("organization_id") String user_id,
                                                         @Field("offset") String offset);


    //set drive apply status
    @FormUrlEncoded
    @POST("user_campus_interest.php")
    Call<PojoNoDataResponse> applyCampusDrive(@Field("user_id") String user_id,
                                              @Field("campusschedule_id") String offset,
                                              @Field("status") String status);


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


    //report content api
    @FormUrlEncoded
    @POST("report.php")
    Call<PojoNoDataResponse> reportContent(@Field("user_id") String user_id,
                                           @Field("report_type") String report_type,
                                           @Field("id") String id,
                                           @Field("reason") String reason);

    //post related apis


    @FormUrlEncoded
    @POST("posts_reaction.php")
    Call<PojoReportContentResponse> postReaction(@Field("user_id") String user_id,
                                                 @Field("id") String id,
                                                 @Field("reaction") String reaction);

    //Like unlike answer/reply
    @FormUrlEncoded
    @POST("session/connect.php")
    Call<PojoNoDataResponse> likeUnlikeAnswerReply(@Field("user_id") String user_id,
                                                   @Field("type") String type,
                                                   @Field("session_id") String session_id,
                                                   @Field("question_id") String question_id);

    @FormUrlEncoded
    @POST("posts_reaction.php")
    Call<PojoNoDataResponse> likeNormalPostComment(@Field("user_id") String user_id,
                                                   @Field("comment_id") String comment_id,
                                                   @Field("reaction") String reaction);

    //delete post comment
    @FormUrlEncoded
    @POST("post_comment_delete.php")
    Call<PojoNoDataResponse> deletePostComment(@Field("user_id") String user_id,
                                               @Field("comment_id") String comment_id);


    //user profile
    @FormUrlEncoded
    @POST("profile.php")
    Call<PojoProfileInfoResponse> getUserProfile(@Field("user_id") String user_id,
                                                 @Field("target_user_id") String target_user_id,
                                                 @Field("type") String type);

    //get user interests
    @FormUrlEncoded
    @POST("profile.php")
    Call<PojoGetUserInterestsResponse> getUserInterests(@Field("user_id") String user_id,
                                                        @Field("target_user_id") String target_user_id,
                                                        @Field("type") String type);

    //user profile
    @FormUrlEncoded
    @POST("profile.php")
    Call<PojoProfileTabResponse> getUserProfileTabData(@Field("user_id") String user_id,
                                                       @Field("target_user_id") String target_user_id,
                                                       @Field("type") String type,
                                                       @Field("offset") String offset);

    //user profile Venue
    @FormUrlEncoded
    @POST("profile.php")
    Call<PojoGetUserVenueResponse> getUserProfileVenue(@Field("user_id") String user_id,
                                                       @Field("target_user_id") String targetUserId,
                                                       @Field("type") String type);


    @Multipart
    @POST("profilepic.php")
    Call<PojoNoDataResponse> updateProfileDp(@Part MultipartBody.Part file,
                                             @Part("user_id") RequestBody user_id);

    //get assessment types
    @FormUrlEncoded
    @POST("get_assessmenttype.php")
    Call<PojoAssessmentTypeResponse> getAssessmentTypes(@Field("user_id") String user_id);


    //get assessment categories
    @FormUrlEncoded
    @POST("get_allcategory.php")
    Call<PojoGetAssessmentCategoriesResponse> getAssessmentCategories(@Field("user_id") String user_id);

    //get assessment list of user
    @FormUrlEncoded
    @POST("assessment_list.php")
    Call<PojoAssessmentListResponse> getAssessmentList(@Field("user_id") String user_id);

    //check assessment payment status
    @FormUrlEncoded
    @POST("assessment_payment_status.php")
    Call<PojoGetAssessmentPaymentStatus> getAssessmentPaymentStatus(@Field("user_id") String user_id,
                                                                    @Field("payment_id") String payment_id);

    //initiate assessment payment
    @FormUrlEncoded
    @POST("assessment_payment.php")
    Call<PojoInitiatePaymentResponse> initiateAssessmentPayment(@Field("user_id") String user_id,
                                                                @Field("amount") String amount,
                                                                @Field("assessment_type_id") String assessment_type_id);

    //make assessment payment
    @FormUrlEncoded
    @POST("assessment_payment.php")
    Call<PojoNoDataResponse> makeAssessmentPayment(@Field("user_id") String user_id,
                                                   @Field("amount") String amount,
                                                   @Field("status") String status,
                                                   @Field("assessment_type_id") String assessment_type_id,
                                                   @Field("transaction_id") String transaction_id,
                                                   @Field("order_id") String order_id,
                                                   @Field("payment_id") String payment_id);

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

    //delete work history
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoNoDataResponse> deleteWorkHistory(@Field("user_id") String user_id,
                                               @Field("type") String type,
                                               @Field("id") String id);


    //get user workList
    @FormUrlEncoded
    @POST("settings.php")
    Call<PojoUserWorkListResponse> getUserWorkList(@Field("user_id") String user_id,
                                                   @Field("target_user_id") String target_user_id,
                                                   @Field("type") String type);

    //user settings
    @FormUrlEncoded
    @POST("settings.php")
    Call<PojoUserSettingsResponse> getUserSettings(@Field("user_id") String user_id,
                                                   @Field("type") String type);


    //user organisations
    @FormUrlEncoded
    @POST("settings.php")
    Call<PojoUserOrganisationListResponse> getUserOrganisations(@Field("user_id") String user_id,
                                                                @Field("type") String type);

    //logout
    @FormUrlEncoded
    @POST("logout.php")
    Call<PojoLogoutResponse> logout(@Field("user_id") String user_id,
                                    @Field("device_id") String device_id,
                                    @Field("session_id") String session_id);

    //get logged in devices id
    @FormUrlEncoded
    @POST("logged_in_devices.php")
    Call<PojoLoggedInDevicesResponse> getLoggedInDevices(@Field("email") String email);

    //get latest app version
    @FormUrlEncoded
    @POST("get_current_version.php")
    Call<PojoLatestVesionResponse> getCurrentVersion(@Field("version") String version);

    //user settings
    @FormUrlEncoded
    @POST("settings.php")
    Call<PojoActiveSessionsResponse> getActiveSessions(@Field("user_id") String user_id,
                                                       @Field("type") String type);

    @FormUrlEncoded
    @POST("settings.php")
    Call<PojoUserSettingsResponse> getBlockingList(@Field("user_id") String user_id,
                                                   @Field("type") String type,
                                                   @Field("offset") String offset);

    //update user settings
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> updateUserSettings(@Field("user_id") String user_id,
                                                           @Field("type") String type,
                                                           @Field("update_field") String update_field,
                                                           @Field("update_value") String update_value);

    //add email
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> addEmail(@Field("user_id") String user_id,
                                                 @Field("type") String type,
                                                 @Field("email") String email);

    //send otp to email
    @FormUrlEncoded
    @POST("user/send_verification_email.php")
    Call<PojoNoDataResponse> sendOtpToEmail(@Field("user_id") String user_id,
                                            @Field("users_extra_info_id") String users_extra_info_id);

    //send otp to email
    @FormUrlEncoded
    @POST("user/verify_email.php")
    Call<PojoNoDataResponse> verifyEmailWithOtp(@Field("user_id") String user_id,
                                                @Field("email") String email,
                                                @Field("otp") String otp);


    //make email primary
    @FormUrlEncoded
    @POST("user/make_email_primary.php")
    Call<PojoNoDataResponse> makeEmailPrimary(@Field("user_id") String user_id,
                                              @Field("users_extra_info_id") String users_extra_info_id);

    //add phone number
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> addPhone(@Field("user_id") String user_id,
                                                 @Field("type") String type,
                                                 @Field("mobile") String mobile);

    //add phone number
    @FormUrlEncoded
    @POST("user/user_extra_info.php")
    Call<PojoUserEmailPhoneResponse> getUserPhoneEmail(@Field("user_id") String user_id);

    //send otp to phone
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> sendOtpToPhone(@Field("user_id") String user_id,
                                                       @Field("type") String type,
                                                       @Field("mobile") String mobile);

    //verify otp on Phone
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> verifyOtp(@Field("user_id") String user_id,
                                                  @Field("type") String type,
                                                  @Field("mobile") String mobile,
                                                  @Field("otp") String otp);

    //two step verification
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> twoStepVerification(@Field("user_id") String user_id,
                                                            @Field("type") String type,
                                                            @Field("two_step_verifications") String two_step_verifications);

   /* //change username
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> updateUsername(@Field("user_id") String user_id,
                                                       @Field("type") String type,
                                                       @Field("username") String username);*/

    /*//withdraw delete request
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> withdrawDeleteAccountRequest(@Field("user_id") String user_id,
                                                                     @Field("type") String type);*/

    //change fullname
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> updateFullName(@Field("user_id") String user_id,
                                                       @Field("type") String type,
                                                       @Field("first_name") String first_name,
                                                       @Field("last_name") String last_name);

    //change location
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> updateUserCity(@Field("user_id") String user_id,
                                                       @Field("type") String type,
                                                       @Field("city") String city);

    //change foi
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> updateUserFoi(@Field("user_id") String user_id,
                                                      @Field("type") String type,
                                                      @Field("current_FOI") String current_FOI);

    //change workplace and designation
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> updateWorkInfo(@Field("user_id") String user_id,
                                                       @Field("type") String type,
                                                       @Field("work_title") String work_title,
                                                       @Field("work_place") String work_place,
                                                       @Field("venue_id") String venue_id);

    //feedback
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> submitFeedback(@Field("user_id") String user_id,
                                                       @Field("type") String type,
                                                       @Field("feedback") String feedback,
                                                       @Field("section") String section);

   /* //bug report
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> reportBug(@Field("user_id") String user_id,
                                                  @Field("type") String type,
                                                  @Field("report_bugs") String report_bugs,
                                                  @Field("section") String section);*/

    //bug report
    @Multipart
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> reportBugWithImage(@Part MultipartBody.Part file,
                                                           @Part("user_id") RequestBody user_id,
                                                           @Part("type") RequestBody type,
                                                           @Part("report_bugs") RequestBody report_bugs,
                                                           @Part("section") RequestBody section);

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


    //Not used anymore
    //create organisation
    @Multipart
    @POST("add_organization.php")
    Call<PojoCreateOrganisationResponse> createOrganisationPage(@Part MultipartBody.Part file,
                                                                @Part("user_id") RequestBody user_id,
                                                                @Part("name") RequestBody name,
                                                                @Part("type") RequestBody type,
                                                                @Part("city") RequestBody city,
                                                                @Part("description") RequestBody description,
                                                                @Part("address") RequestBody address,
                                                                @Part("noofemployee") RequestBody noofemployee,
                                                                @Part("website") RequestBody website);


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

    //get organisation types
    @FormUrlEncoded
    @POST("get_organizationtype.php")
    Call<PojoGetOrganisationTypeResponse> getOrganisationType(@Field("user_id") String user_id,
                                                              @Field("query") String query);


    //change password
    @FormUrlEncoded
    @POST("update_settings.php")
    Call<PojoUpdateAppSettingsResponse> changePassword(@Field("user_id") String user_id,
                                                       @Field("type") String type,
                                                       @Field("current") String current,
                                                       @Field("new") String new_pass,
                                                       @Field("confirm") String confirm);

    /*//get city list
    @FormUrlEncoded
    @POST("get_city.php")
    Call<PojoGetCitiesResponse> getCityList(@Field("query") String query,
                                            @Field("offset") String offset);*/

    //invite friends api
    @FormUrlEncoded
    @POST("invite.php")
    Call<PojoInviteFriendResponse> inviteFriends(@Field("user_id") String user_id,
                                                 @Field("type") String type,
                                                 @Field("friend_id") String friend_id,
                                                 @Field("id") String id);


    //get users from phone numbers api
    @FormUrlEncoded
    @POST("get_user_by_phone.php")
    Call<PojoGetRegisteredContactsListResponse> getUserFromPhone(@Field("user_id") String user_id,
                                                                 @FieldMap(encoded = false) Map<String, String> fields);

    @FormUrlEncoded
    @POST("connect.php")
    Call<PojoNoDataResponse> addAllUsers(@Field("user_id") String user_id,
                                         @Field("connect_type") String connect_type,
                                         @FieldMap(encoded = false) Map<String, String> fields);


    //get Invites list
    @FormUrlEncoded
    @POST("invitations.php")
    Call<PojoInvitesListResponse> getVenueInterestInvitesList(@Field("user_id") String user_id,
                                                              @Field("type") String type,
                                                              @Field("invitation_id") String invitation_id,
                                                              @Field("query") String query,
                                                              @Field("offset") String offset);

    //get Invites list
    @FormUrlEncoded
    @POST("invitations.php")
    Call<PojoInvitesListResponse> getAnswerInvitesList(@Field("user_id") String user_id,
                                                       @Field("type") String type,
                                                       @Field("sessions_qa_id") String sessions_qa_id,
                                                       @Field("query") String query,
                                                       @Field("offset") String offset);

    //get Invites list
    @FormUrlEncoded
    @POST("invitations.php")
    Call<PojoInvitesListResponse> getSessionInviteList(@Field("user_id") String user_id,
                                                       @Field("type") String type,
                                                       @Field("session_id") String sessions_id,
                                                       @Field("query") String query,
                                                       @Field("offset") String offset);

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

    @FormUrlEncoded
    @POST("SendMsg.aspx")
    Call<String> sendSMS(@Field("uname") String uname,
                         @Field("pass") String pass,
                         @Field("unicode") String unicode,
                         @Field("send") String send,
                         @Field("dest") String dest,
                         @Field("msg") String msg);


   /* @POST("/order")
    Call<GetOrderIDResponse> createOrder(@Body GetOrderIDRequest request);

    @GET("/status")
    Call<GatewayOrderStatus> orderStatus(@Query("env") String env,
                                         @Query("order_id") String orderID,
                                         @Query("transaction_id") String transactionID);

    @POST("/refund")
    Call<ResponseBody> refundAmount(@Query("env") String env,
                                    @Query("transaction_id") String transaction_id,
                                    @Query("amount") String amount);*/
}
