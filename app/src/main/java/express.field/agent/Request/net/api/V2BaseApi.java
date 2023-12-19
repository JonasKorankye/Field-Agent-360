
package express.field.agent.Request.net.api;

import java.util.List;
import java.util.Map;

import express.field.agent.Request.net.JsonRpcCall;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface V2BaseApi {

    @POST("rpc/custom/identity/check")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> login(@Body RequestBody body);


    @POST("/rpc/login/identity/exchange")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> loginCrypto(@Body RequestBody body);


    @POST("/rpc/mobile/device/activation")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> activateDevice(@Body RequestBody body);

    @POST("rpc/custom/token")
    @Headers("Content-Type: application/json")
    Call<Map<String, Object>> refreshToken(@Body Map<String, Object> body);

    @POST("/rpc/custom/password/edit")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> editPassword(@Body RequestBody body);

    @POST("/rpc/custom/identity/changePassword")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> changePassword(@Body RequestBody body);

    @POST("/rpc/custom/secretQuestion/answer")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> validateAnswer(@Body RequestBody body);

    @POST("rpc/custom/question/set")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> setQuestion(@Body RequestBody body);

    @POST("rpc/custom/secretQuestion/get")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> getQuestion(@Body RequestBody body);

    @POST("rpc/custom/secretQuestionAnswer/edit")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> editQuestionAnswer(@Body RequestBody body);

    @POST("rpc/verifyOTP")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> verifyOTP(@Body RequestBody body);

    @POST("rpc/mobile/dictionary/get")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> downloadDictionaries(@Body RequestBody body);

    @POST("rpc/mobile/translation/get")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> getTranslations(@Body RequestBody body);

    @POST("rpc/dfa/customer/upload")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> uploadCustomer(@Body RequestBody body);

    @POST("rpc/dfa/group/upload")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> uploadGroup(@Body RequestBody body);

    @POST("rpc/dfa/account/upload")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> uploadAccount(@Body RequestBody body);

    @POST("rpc/mcustomer/customer/fetch")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> downloadCustomers(@Body RequestBody body);

    @POST("rpc/mgroup/group/fetch")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> downloadGroups(@Body RequestBody body);

    @POST("rpc/mledger/account/fetch")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> downloadAccounts(@Body RequestBody body);

    @POST("rpc/mobile/settings/download")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> downloadSettings(@Body RequestBody body);

    @Multipart
    @POST("rpc/document/document/upload")
    Call<ResponseBody> uploadDocuments(@Part List<MultipartBody.Part> attachments, @Part("metaData") RequestBody metadata);

    @GET("/report/customerReport")
    Call<Object> getIScoreReport();

    @GET("rpc/login/.well-known/mle")
    Call<Object> getMlePublicKeys();

    @POST("rpc/mcustomer/customer/validateBio")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> validateCustomerBio(@Body RequestBody body);

    @POST("/rpc/dfa/task/fetch")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> downloadTasks(@Body RequestBody body);

    @POST("/rpc/dfa/task/complete")
    Call<JsonRpcCall> completeTask(@Body RequestBody criteria);


    @POST("rpc/custom/identity/verify")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> verifyPin(@Body RequestBody body);

    @Streaming
    @GET("rpc/document/repository/{fileName}")
    Call<ResponseBody> downloadAttachment(@Path("fileName") String fileName);

    @POST("rpc/dfa/loanApplication/upload")
    Call<JsonRpcCall> createLoanApplication(@Body RequestBody loanApplication);

    @POST("rpc/mloan/loanapp/fetch")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> downloadLoanApplications(@Body RequestBody body);

    @POST("rpc/mloan/loan/fetch")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> downloadLoans(@Body RequestBody body);

    @POST("rpc/document/document/fetch")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> downloadDocuments(@Body RequestBody criteria);

    @POST("rpc/dfa/parameter/fetch")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> downloadConfigurableParameters(@Body RequestBody body);

    @POST("rpc/vfz/customerVisit/upload")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> uploadVisit(@Body RequestBody body);

    @POST("rpc/vfz/customerVisit/fetch")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> downloadVisits(@Body RequestBody body);

    @POST("rpc/dfa/performance/fetchReport")
    @Headers("Content-Type: application/json")
    Call<JsonRpcCall> downloadTargetReport(@Body RequestBody body);

    @POST("rpc/mobile/float/balance")
    Call<JsonRpcCall> fetchAgentFloatAccountBalance(@Body RequestBody body);

    @POST("/demoTransaction")
    Call<JsonRpcCall> doTransaction(@Body RequestBody body);

    @POST("/confirmPesafasta")
    Call<JsonRpcCall> confirmPesafasta(@Body RequestBody body);

    @POST("rpc/mobile/agent/ministatement")
    Call<JsonRpcCall> agentMinistatement(@Body RequestBody body);

    @POST("rpc/mobile/sms/resend")
    Call<JsonRpcCall> resendSms(@Body RequestBody body);

    @POST("rpc/mobile/transactions/post")
    Call<JsonRpcCall> performTransaction(@Body RequestBody body);

    @POST("/rpc/mobile/report/commission")
    Call<JsonRpcCall> commissionReport(@Body RequestBody body);

    @POST("/rpc/mobile/report/daily")
    Call<JsonRpcCall> dailyReport(@Body RequestBody body);

    @POST("/rpc/mobile/report/performance")
    Call<JsonRpcCall> performanceReport(@Body RequestBody body);

    @POST("/rpc/mobile/report/dashboard")
    Call<JsonRpcCall> dashboardReport(@Body RequestBody body);

    @POST("rpc/mobile/cashOut/post")
    Call<JsonRpcCall> performCashOutTransaction(@Body RequestBody body);

    @POST("rpc/mobile/otp/validation")
    Call<JsonRpcCall> validateOtp(@Body RequestBody body);

    @POST("rpc/mobile/devices/fetch")
    Call<JsonRpcCall> fetchBoundDevices(@Body RequestBody body);

    @POST("rpc/mobile/report/daily")
    Call<JsonRpcCall> dailyReportSummary(@Body RequestBody body);

    @POST("rpc/mobile/check/loanLimit")
    Call<JsonRpcCall> checkLoanLimit(@Body RequestBody body);

    @POST("rpc/mobile/operator/fetch")
    Call<JsonRpcCall> fetchOperatorList(@Body RequestBody body);

    @POST("rpc/mobile/operatorBalances/fetch")
    Call<JsonRpcCall> fetchOperatorBalances(@Body RequestBody body);

    @POST("rpc/telpo/mpos/initialize")
    Call<JsonRpcCall> initializeMposDevice(@Body RequestBody body);

    @POST("rpc/mobile/report/performance")
    Call<JsonRpcCall> monthlyPerformance(@Body RequestBody body);

    @POST("rpc/mobile/report/dashboard")
    Call<JsonRpcCall> agentTransactions(@Body RequestBody body);

    @POST("rpc/mobile/operator/suspend")
    Call<JsonRpcCall> suspendOperator(@Body RequestBody body);

    @POST("rpc/mobile/operator/remove")
    Call<JsonRpcCall> terminateOperator(@Body RequestBody body);

    @POST("rpc/mobile/operator/resetPassword")
    Call<JsonRpcCall> resetOperatorPassword(@Body RequestBody body);

    @POST("rpc/mobile/float/pull")
    Call<JsonRpcCall> floatPushPull(@Body RequestBody body);

    @POST("rpc/mobile/float/holdRelease")
    Call<JsonRpcCall> floatHoldRelease(@Body RequestBody body);

    @POST("rpc/mobile/receiptDetails/store")
    Call<JsonRpcCall> storeReceipt(@Body RequestBody body);

    @POST("rpc/telpo/mpos/authorization")
    Call<JsonRpcCall> cardAuthorization(@Body RequestBody body);

    @POST("rpc/custom/floatAccount/check")
    Call<JsonRpcCall> floatAccountCheck(@Body RequestBody body);


    @POST("rpc/mobile/device/add")
    Call<JsonRpcCall> storeDeviceData(@Body RequestBody body);

    @POST("rpc/mobile/assignOperator/device")
    Call<JsonRpcCall> assignDevice(@Body RequestBody body);

    @POST("rpc/mobile/cashStatement/close")
    Call<JsonRpcCall> closeCashStatement(@Body RequestBody body);

    @POST("rpc/mobile/operator/create")
    Call<JsonRpcCall> createOperator(@Body RequestBody body);

    @POST("rpc/custom/loginOtp/request")
    Call<JsonRpcCall> retryLoginOtpRequest(@Body RequestBody body);

    @POST("rpc/login/identity/closeSession")
    Call<JsonRpcCall> sessionLogout(@Body RequestBody body);

    @Streaming
    @GET
    Call<ResponseBody> downloadNewAppVersion(@Url String fileUrl);
}
