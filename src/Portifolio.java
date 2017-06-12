import java.awt.TrayIcon;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.print.attribute.standard.RequestingUserName;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
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
import org.omg.CORBA.RepositoryIdHelper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.twelvemonkeys.lang.StringUtil;

public class Portifolio {

	private String API_KEY = "HI4CNKKM-5UHFWW2E-F1EQFY4H-UP0ZZ5Q7";
	private String SECRETE_KEY = "37c6ff8d9f9fe70dcec35495ff57383b5ec3ac1f67aef6175749d613e7bfbe844e8720540943f2c91409319ad2fd7321817bc4e951639f1589deb74589a42023";

	private String nomeMarket;
	private HashMap<String, Moeda> moedas = new HashMap<>();
	private Double BTC_entrada = 0.0;
	private Double BTC_atual = 0.0;

	public Portifolio(String nomeMarket) {
		super();
		this.nomeMarket = nomeMarket;
	}

	@Override
	public String toString() {
		return "Portifolio [nomeMarket=" + nomeMarket + "]";
	}

	public void mostrarPortilofio() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date hora = Calendar.getInstance().getTime();
		System.out.println("      E=" + String.format("|%-15.8f", BTC_entrada) + " A="
				+ String.format("|%-15.8f", BTC_atual) + "%="
				+ String.format("|%-15.2f", ((100 / BTC_entrada) * (BTC_atual)) - 100) + "  " + sdf.format(hora));
		System.out.println(
				"-------------------------------------------------------------------------------------");
		for (Moeda md : moedas.values()) {
			System.out.println(md);
		}
		System.out.println(
				"-------------------------------------------------------------------------------------");
	}

	public HttpEntity returnCommand(String command, ArrayList<SimpleEntry<String, String>> extraParams) {
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

	public HttpEntity returnPublic(String command) {
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

	public void updateMoeda() {
		BTC_atual = 0.0;
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
					atualizarValores(md);

				} else {
					moedas.remove(md.getNome());
				}
			}

		} catch (JsonSyntaxException | ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateValoresMoedas() {
		BTC_atual = 0.0;
		for (Moeda md : moedas.values()) {
			atualizarValores(md);
		}
	}

	private void atualizarValores(Moeda md) {

		if (md.getNome().equalsIgnoreCase("BTC")) {
			md.setValor(md.getQuantidade());
			md.setValorCompra(md.getQuantidade());
			BTC_atual += md.getQtdBTC();
			return;
		}
		try {
			String dateString = "09 Nov 2012 23:40:18";
			DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
			Date date = dateFormat.parse(dateString);

			long start = (long) date.getTime() / 1000;

			String dateString2 = "09 Nov 2018 23:40:18";
			DateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
			Date date2 = dateFormat2.parse(dateString2);
			long stop = (long) date2.getTime() / 1000;

			ArrayList<SimpleEntry<String, String>> extraParams = new ArrayList<>();
			extraParams.add(new SimpleEntry<String, String>("currencyPair", "BTC_" + md.getNome().toUpperCase()));
			extraParams.add(new SimpleEntry<String, String>("start", "" + start));
			extraParams.add(new SimpleEntry<String, String>("end", "" + stop));
			HttpEntity responseEntity = returnCommand("returnTradeHistory", extraParams);
			if (responseEntity == null) {
				return;
			}
			String aux = EntityUtils.toString(responseEntity);
			JsonArray json = new Gson().fromJson(aux, JsonArray.class);
			Iterator<JsonElement> ite = json.getAsJsonArray().iterator();
			JsonObject eleUti = null;
			while (ite.hasNext()) {
				JsonObject ele = ite.next().getAsJsonObject();
				if (ele.get("type").getAsString().equalsIgnoreCase("buy")) {
					if (eleUti == null) {
						md.setValorCompra(ele.get("rate").getAsDouble());
						eleUti = ele;
					} else if (ele.get("date").getAsString()
							.compareToIgnoreCase(eleUti.get("date").getAsString()) >= 0) {
						md.setValorCompra(ele.get("rate").getAsDouble());
						eleUti = ele;
					}

				}
			}

			HttpEntity responseEntity2 = returnPublic("returnTicker");
			if (responseEntity2 == null) {
				return;
			}
			String aux1 = EntityUtils.toString(responseEntity2);
			JsonObject json2 = new Gson().fromJson(aux1, JsonObject.class);
			md.setValor(json2.get("BTC_" + md.getNome().toUpperCase()).getAsJsonObject().get("last").getAsDouble());
			BTC_atual += md.getQtdBTC();
		} catch (java.text.ParseException | JsonSyntaxException | ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void update() {
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
}
