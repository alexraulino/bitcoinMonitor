package poloniex;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class Portifolio {

	private static String API_KEY = "";
	private static String SECRETE_KEY = "";

	private String nomeMarket;
	private HashMap<String, Moeda> moedas = new HashMap<>();
	private Double BTC_entrada = 0.0;
	private Double BTC_atual = 0.0;
	// private Connection conn;

	public Portifolio(String nomeMarket, String API_KEY, String SECRETE_KEY, Connection conection) throws SQLException {
		super();
		this.nomeMarket = nomeMarket;
		this.API_KEY = API_KEY.substring(1, API_KEY.length());
		this.SECRETE_KEY = SECRETE_KEY.substring(1, SECRETE_KEY.length());

	}

	@Override
	public String toString() {
		return "Portifolio [nomeMarket=" + nomeMarket + "]";
	}

	public void mostrarPortilofio() throws SQLException {

		Double percentual = ((100 / BTC_entrada) * (BTC_atual)) - 100;

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date hora = Calendar.getInstance().getTime();
		System.out.println(
				"      E=" + String.format("|%-15.8f", BTC_entrada) + " A=" + String.format("|%-15.8f", BTC_atual)
						+ "%=" + String.format("|%-15.2f", percentual) + "  " + sdf.format(hora));
		System.out.println("-------------------------------------------------------------------------------------");
		for (Moeda md : moedas.values()) {

			System.out.println(md);
		}
		System.out.println("-------------------------------------------------------------------------------------");
	}

	public static HttpEntity returnCommand(String command, ArrayList<SimpleEntry<String, String>> extraParams) {
		String url = "https://poloniex.com/tradingApi";
		String nonce = String.valueOf(System.currentTimeMillis());
		String queryArgs = "command=" + command + "&nonce=" + nonce;

		for (SimpleEntry<String, String> entry : extraParams) {
			queryArgs = queryArgs + "&" + entry.getKey() + "=" + entry.getValue();
		}

		Mac shaMac;
		try {
			shaMac = Mac.getInstance("HmacSHA512");

			SecretKeySpec keySpec = new SecretKeySpec(SECRETE_KEY.getBytes(), "HmacSHA512");
			shaMac.init(keySpec);
			final byte[] macData = shaMac.doFinal(queryArgs.getBytes());
			String sign = Hex.encodeHexString(macData);

			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			post.addHeader("Key", API_KEY);
			post.addHeader("Sign", sign);

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("command", command));
			params.add(new BasicNameValuePair("nonce", nonce));
			for (SimpleEntry<String, String> entry : extraParams) {
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			post.setEntity(new UrlEncodedFormEntity(params));

			CloseableHttpResponse response = httpClient.execute(post);
			return response.getEntity();
		} catch (NoSuchAlgorithmException | IOException | InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static HttpEntity returnPublic(String command) {
		String url = "https://poloniex.com/public?command=" + command;
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost post = new HttpPost(url);
			CloseableHttpResponse response = httpClient.execute(post);
			return response.getEntity();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public void updatePortifolio() {
		HttpEntity responseEntity = returnCommand("returnCompleteBalances", new ArrayList<>());
		if (responseEntity == null) {
			return;
		}
		try {
			JsonObject json = new Gson().fromJson(EntityUtils.toString(responseEntity), JsonObject.class);

			for (Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {

				Moeda md = new Moeda(entry.getKey(), (JsonObject) entry.getValue());
				if (md.getQuantidade() != 0) {
					moedas.put(entry.getKey(), md);
					taskUpdateMoeda tk = new taskUpdateMoeda(md, true);
					tk.run();
					md = tk.getMoeda();

				} else {
					
//					String query = " inset into historicoPortifolio (vendido, nome)" + " values (?, ?)";
//
//					// create the mysql insert preparedstatement
//					PreparedStatement preparedStmt = conn.prepareStatement(query);
//					preparedStmt.setString(1, "S");
//					preparedStmt.setString(2, md.getNome());
//					
//					// execute the preparedstatement			
//					preparedStmt.execute();
//					
//					preparedStmt.close();
					
					moedas.remove(md.getNome());
				}
			}

		} catch (JsonSyntaxException | ParseException | IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateValoresMoedas(Boolean xAtualizaQuantidade) {
		ArrayList<taskUpdateMoeda> tasks = new ArrayList<>();
		for (Moeda md : moedas.values()) {
			taskUpdateMoeda tk = new taskUpdateMoeda(md, xAtualizaQuantidade);
			tasks.add(tk);
			tk.run();
		}

		BTC_atual = 0.0;
		for (taskUpdateMoeda tk : tasks) {
			Moeda md = tk.getMoeda();
			BTC_atual += md.getQtdBTC();
			moedas.put(md.getNome(), md);
		}
	}

	public void updateDepositsWithdrawals() {
		try {
			ArrayList<SimpleEntry<String, String>> extraParams = new ArrayList<>();
			String dateString = "09 Nov 2012 23:40:18";
			DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
			Date date = dateFormat.parse(dateString);

			long start = (long) date.getTime() / 1000;

			String dateString2 = "09 Nov 2018 23:40:18";
			DateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
			Date date2 = dateFormat2.parse(dateString2);
			long stop = (long) date2.getTime() / 1000;
			extraParams.add(new SimpleEntry<String, String>("start", "" + start));
			extraParams.add(new SimpleEntry<String, String>("end", "" + stop));
			HttpEntity responseEntity2 = returnCommand("returnDepositsWithdrawals", extraParams);
			if (responseEntity2 == null) {
				return;
			}
			String aux = EntityUtils.toString(responseEntity2);

			JsonObject json2 = new Gson().fromJson(aux, JsonObject.class);
			Iterator<JsonElement> ite = json2.get("deposits").getAsJsonArray().iterator();
			BTC_entrada = 0.0;
			while (ite.hasNext()) {
				JsonElement ele = ite.next();
				BTC_entrada += ele.getAsJsonObject().get("amount").getAsDouble();
			}

			ite = json2.get("withdrawals").getAsJsonArray().iterator();
			while (ite.hasNext()) {
				JsonElement ele = ite.next();
				BTC_entrada -= ele.getAsJsonObject().get("amount").getAsDouble();
			}

		} catch (ParseException | IOException | java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void registrarHistorico() {
		// TODO Auto-generated method stub

	}
}
