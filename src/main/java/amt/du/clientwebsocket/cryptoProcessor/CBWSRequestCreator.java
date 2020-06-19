package amt.du.clientwebsocket.cryptoProcessor;

import amt.du.clientwebsocket.socketConstants.WebSocketConstants;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CBWSRequestCreator {
	Logger lg = Logger.getLogger(CBWSRequestCreator.class);
	
	@Value("${cbwsi.request.bankid}")
	private String bankId;
	
	@Value("${cbwsi.request.username}")
	private String userName;
	
	@Value("${cbwsi.request.password}")
	private String password;
	
	@Value("${adcb.rsa.privatekey}")
	private String privateKey;
	
	@Value("${cbwsi.request.publickey}")
	private String cbPublicKey;
	
	
	
	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getCbPublicKey() {
		return cbPublicKey;
	}

	public void setCbPublicKey(String cbPublicKey) {
		this.cbPublicKey = cbPublicKey;
	}
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
/*	public static void main(String[] args) throws Exception {
		CBWSRequestCreator c = new CBWSRequestCreator();
		c.setBankId("003");
		c.setCbPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqaM25DoEkoWP0z1E8D/5p1lTgIqLBbVdcqzXtYJGCOiNU2tHK80qMHSV7HkQ4Lj0KPpD2J1Li27zAVYFXRYhqiceqLV7BcXSAFbbL2Cg09eN2pSoJCcq2AvVcvNK0Oz5FcF3BOzPv4sAuIeQxJQpZTTldG1lUZf8EdhXRnLKqwIAXO8xJajxFmbJbU1guCt+2IpW/Mv6xWspXXCeNjk+CChnPY2mPWcz2M4uy71PcoCqE2CBli5/Qr6iTNU6+Z94doIdVx81P8Qffnz8BvR/A79EtC1yYC70QjNIuewYOamVXUgnIAWMPQnZoaP9gUq9WbLSDIoHfAXtoBCJs8Cy0QIDAQAB");
		c.setPassword("5096267040959198221044104872286650397073226495406468217960992687");
		c.setPrivateKey("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC+XRbBjPZbLt/79b233wZxoaZ17QvIOVzMRq9kWARtc8s1ZeqE0mX4iykIELzH+9TF8Eecvr0Ps35iI6PUNioHPolpkBQFnxy/xrglgyN3kT/9Heqtj4EMh+7oEa8Q9tjuyTi4nPdDRB+7gpSWAOQrDaTfY2hC1ZQx+yYjXLFWi7F9rCPtyanfhJdlaIHnvgVe4EATIy8Avu4bZfpVtIw4m7Mc03OBRuTJt92C2DWQ0bLiahUDUpaYc6BwNnkLyezojARKeZFPrZQtdF7r5WT9MYib9M4eAP7bET+CJBloNJYoZjD2CO0ls34q15gTv3MA8dFiyGtDCVpupOCX5CqzAgMBAAECggEAV6JPCYc1GkD4B+vXDMB1HPaHxR39II2XtMqNj6odT63QzKJAVijtnH+jwfzCi2XZS84TiYsdSg8djVxO3TCaJH+bjqcvMG9RMs/ukgZO24pYZNPS51d61yP0zYzmhO6+ax7jJ8nK4oKIATBZ6rw6Tb+YOOZwZuQftuqhYgIX+XjEh2ARRGcjRDTHn9gt6zJjgtC8seW/rXayfxU/+m901KkHGK3z0b2UOCGsfSRoBAP8krOM0jJu8ZG/SNeYP8QeAqPuDniJY2HI679aU6BVCIjJc4U63T58IeMWKG2MW4r9PrF9PfLaxC4cAYyhpSt7Wo8PPGlc9lTaH4SrFliqcQKBgQD8XcH6zLPSiPRO6LfuqIEiDJPzr7uW3D6tkGgtJDwMxDqQNce3rykQ4RU9ofgF1FUg1xnbnHawAYvZpcdg1c64vVkzKtU07OxjJ6N7HZk7d+qPmWnZNSK08UwOkzjxohRlp9XorEonK5uIU50F1Aojij2E9OHb5GySfYh9NK+KNwKBgQDBGsjXlP2KzocWKguKAN5fjWMgvBsPmsg9x1LdmzxTosLSExNMXHK7RSAZYVoPDCm/29Qw3ialZ9/PY8n2XDIa8bP03C3kGXupKS4gCvYiFIbZi0SyDw61+mhybTLVBDnxqcMB++rsrYhApkwkgJgIJz6x2Xi3HfkueXZPTYT1ZQKBgCPJ8A5vJTlALtVZ72uq/dUkT38JduLoDQN0LTHpp5Jv6ZY16kLEVmCMawS4iYHaINBtcL9SKwq+zWRqPCIhcBWG1LWK531/8+4D2w1En0xtrxf6O6aQAIIEjjKq8jJiN95KtO0wMbvoVUA6C1SeQ2nS2vKV/8m9VSBZKRgIdrR1AoGAA1CXZAP9ujVDYmEEY7ESICfILNatzD3G9DCIvp8ckP75Qyvqp+PqYEaWf3kJV7rIuK9oXBJkLXC0CdZXsC/y+D1v7mMdMo4xbjzzur8VbI5XpkJawoe5o+wRgO1Y8wkoiXp1i4IsjTWJYjq4kePXOEScMedeyB3umhqhqkjVYHUCgYBBlNDwF7hhYvVPxc3YCUYtBkWGDxn6mI22aPHXKNkFXxk/gcCOuI8L5hgSFBuTjguWo8ElMRqA7lA7wePGyWehsR2HGN0TVFlpKXy+RwwF00bqJM+Z6z46Db9FSUwBScuN/N+x0+PQg2zy7Zv5yw4WK6Ff3azD4Yk4nz3uHPqU5Q==");
		c.setUserName("P003DIGID2F333BA0D8F6CF6F77EC43243B83E1BDAE4BD537FEEF83C61DCD50E");
		String requestParams=c.createWSConnectionRequest();
		URI endpointURI = new URI("wss", "131.5.15.13", "/CBWSO/wso/svc?" + requestParams, null, null);
		System.out.println(endpointURI.toURL());
//		WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(endpointURI);
	}
*/
	 public static String getHexa(int a){
	     	String str = Integer.toHexString(a);
	     	return str;
	     }
	public String createWSConnectionRequest() throws Exception{
		try{
			System.out.println("CBWSRequestCreator creating ws connection request");
			lg.info("creating ws connection request");
			String datepattern="yyMMdd";
			String timepattern="HHmmss";
			SimpleDateFormat simpleDateOnlyFormat = new SimpleDateFormat(datepattern);
			SimpleDateFormat simpletimeFormat = new SimpleDateFormat(timepattern);
			String requestParam = null;
			JSONObject jsonObject = new JSONObject();
			jsonObject.put(WebSocketConstants.bankId, bankId);
			jsonObject.put(WebSocketConstants.password, password);
			Date dat=new Date();
			System.out.println("CBWSRequestCreator before rew");
			String rew="WSO"+WebSocketConstants.ADCB_ENTITYID+simpleDateOnlyFormat.format(dat)+getHexa(0)+"1"+simpletimeFormat.format(dat).substring(2,6);
			jsonObject.put(WebSocketConstants.requesterWsoRef, rew);
			System.out.println("CBWSRequestCreator before signing password");
			String signedPassword = CyrptoProcessor.sign(password, privateKey);
			System.out.println("CBWSRequestCreator after signing password");
			jsonObject.put(WebSocketConstants.signature, signedPassword);
			String timeStamp  = dateFormat.format(new Date());
			jsonObject.put(WebSocketConstants.timeStamp, timeStamp);
			jsonObject.put(WebSocketConstants.userName, userName);
			JSONObject entityObject = new JSONObject();
			entityObject.put(WebSocketConstants.entityDetails, jsonObject);
			
		
			String data = entityObject.toString();
			
			System.out.println("CBWSRequestCreator before generateaeskey");
			String aesKey = CyrptoProcessor.generateAesKey();
			System.out.println("CBWSRequestCreator after generateaeskey");
			String encryptedData = CyrptoProcessor.aesEncrypt(data, aesKey);
			System.out.println("CBWSRequestCreator after aesencrypt");
			String encrytedAESKey = CyrptoProcessor.rsaEncrypt(aesKey, cbPublicKey);
			System.out.println("CBWSRequestCreator after rsaencrypt");
			JSONObject requestDetails = new JSONObject();
			requestDetails.put(WebSocketConstants.requestData, encryptedData);
			requestDetails.put(WebSocketConstants.requestkData, encrytedAESKey);
			requestParam = requestDetails.toJSONString();
			     
			String urlParameters = null;
			try {
				urlParameters = URLEncoder.encode(WebSocketConstants.entityDetails, "UTF-8") + "=" + URLEncoder.encode(requestParam, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("CBWSRequestCreator after urlparameters");
			try {
				urlParameters = urlParameters+"&"+URLEncoder.encode(WebSocketConstants.ENTITYID, "UTF-8") + "=" + URLEncoder.encode(WebSocketConstants.ADCB_ENTITYID, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("CBWSRequestCreator after append url + parameters");
			return urlParameters;
		}catch (Exception e){
			lg.error("error creating request",e);
			e.printStackTrace();
		}
		return null;
	}
	
	
}
