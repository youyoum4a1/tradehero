package android.tradehero.Utills;


public class Constants {	


	public static final int FB_OPERATION=10000;
	public static final int LINKEDIN_OPERATION=10001;
	public static final int TWITTER_OPERATION=10002;
	public static final int EMAIL_OPERATION=10003;
	public static final int LOGIN_OPERATION=10004;
	public static final int SIGNUP_OPERATION=10004;


	public static final String BASE_API_URL= "https://www.tradehero.mobi/";
	public static final String SIGN_UP_WITH_EMAIL_URL=BASE_API_URL+"api/SignupWithEmail";
	public static final String SIGN_UP_WITH_SOCIAL_MEDIA_USER_URL=BASE_API_URL+"api/users";
	public static final String LOGIN_URL=BASE_API_URL+"api/login";
	public static final String PRIVACY_WEB_URL=BASE_API_URL+"privacy";
	public static final String PRIVACY_TERMS_OF_SERVICE=BASE_API_URL+"privacy";
	//header
	public static final String CONTENT_TYPE="Content-Type";
	public static final String CONTENT_TYPE_VALUE="application/x-www-form-urlencoded";
	public static final String CHARSET="charset" ;
	public static final String CHARSET_VALUE="utf-8";	
	//SingUp 
	public static final String PASSWORD_CONFIRMATION ="passwordConfirmation";
	public static final String PASSWORD="password";
	public static final String DISPLAY_NAME="displayName";
	public static final String FIRST_NAME="firstName";
	public static final String EMAIL="email";
	public static final String LAST_NAME="lastName";
	

	//SignUp FaceBook
	public static final String FB_ACCESS_TOKEN="facebook_access_token";

	//SignUp Twitter
	public static final String TWITTER_ACCESS_TOKEN="twitter_access_token";
	public static final String TWITTER_ACCESS_TOKEN_SCERET="twitter_access_token_secret";
	//LINKEDiN
	public static final String LINKED_ACCESS_TOKEN="linkedin_access_token";
	public static final String LINKED_ACCESS_TOKEN_SCERET="linkedin_access_token_secret";

	//User
	public static final String ADDRESS="address";
	public static final String ALERT_COUNT="alertCount";
	public static final String BIOGRAPHY="biography";
	public static final String CCBALANCE="ccbalance";
	public static final String CCPERMONTHBALANCE="ccPerMonthBalance";
	//public static final String DISPLAY_NAME="displayName";
	//public static final String EMAIL="email" ;
	public static final String EMAIL_NOTIFICATION="emailNotificationsEnabled";
	public static final String ENROLLED_PROVIDERS="enrolledProviders" ;
	public static final String FB_LINKED="fbLinked";
	public static final String FIRST_FOLLOW_ALL_TIME="firstFollowAllTime";
	//public static final String FIRST_NAME="firstName";
	public static final String FOLLOWERS_COUNT="followerCount";
	public static final String HEROIDS="heroIds";
	public static final String ID="id"; 
	public static final String LARGE_PIC="largePicture" ;
	//public static final String LAST_NAME="lastName";
	public static final String LEADERSHIP_BOARD_RANKING="leaderboardRankings ";
	public static final String LINKEDIN_LINKED="liLinked";
	public static final String LOCATION="location";
	public static final String MEMBER_SINCE="memberSince";
	public static final String PAYPAL_EMAIL_ADD="paypalEmailAddress";
	public static final String PICTURE="picture";
	
	public static final String PORTFOLIO_JSON_OBJ ="portfolio";     
	public static final String PORTFOLIO_CURRENCY="Currency";
	public static final String PORTFOLIO_CASH_BAL="cashBalance";
	public static final String PORTFOLIO_COUNT_EXCHANGE="countExchanges";
	public static final String PORTFOLIO_COUNT_TRADE="countTrades";
	public static final String PORTFOLIO_CREATION_DATE="creationDate";
	public static final String PORTFOLIO_DESCRIPTION="description";
	public static final String PORTFOLIO_ID="id";
	public static final String PORTFOLIO_INTIAL_CASH="initialCash";
	public static final String PORTFOLIO_MARKING_AS_OF_UTC="markingAsOfUtc";
	public static final String PORTFOLIO_PI_M2D="plM2D";
	public static final String PORTFOLIO_PI_Q2D="plQ2D"; 
	public static final String PORTFOLIO_PI_SINCE_INCEPTION="plSinceInception";
	public static final String PORTFOLIO_PI_Y2D	="plY2D";
	public static final String PORTFOLIO_ROI_M2D="roiM2D";
	public static final String PORTFOLIO_ROI_M2D_ANNUALIZES="roiM2DAnnualized";
	public static final String PORTFOLIO_ROI_Q2D="roiQ2D";
	public static final String PORTFOLIO_ROI_Q2D_ANNUALIZED	="roiQ2DAnnualized" ;
	public static final String PORTFOLIO_ROI_SINCE_INCEPTION="roiSinceInception";
	public static final String PORTFOLIO_ROI_SINCE_INCEPTION_ANNUALIZED	="roiSinceInceptionAnnualized";
	public static final String PORTFOLIO_ROI_Y2D="roiY2D";
	public static final String PORTFOLIO_ROI_Y2D_ANNUALIZED	="roiY2DAnnualized";
	public static final String TITLE="title";
	public static final String TOTAL_EXTRA_CASH_GIVEN="totalExtraCashGiven";
	public static final String TOTAL_EXTRA_CASH_PURCHASED="totalExtraCashPurchased";
	public static final String TOTAL_VALUE="totalValue";
	public static final String YAHOO_SYMBOLS="yahooSymbols";        
	public static final String PUSH_NOTIFICATION_IS_ENABLED="pushNotificationsEnabled" ;
	public static final String RANK="rank";     
	public static final String TH_LINKED="thLinked";
	public static final String TRADE_SHARED_COUNT ="tradesSharedCount_FB" ;
	public static final String TWITTER_LINK="twLinked" ;
	public static final String UNREAD_COUNT="unreadCount";
	public static final String USE_TH_PRICE="useTHPrice";
	public static final String USER_ALERT_PLAN="userAlertPlans";
	public static final String WEBSITE="website";

}
