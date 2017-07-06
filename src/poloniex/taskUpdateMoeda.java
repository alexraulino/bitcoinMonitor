package poloniex;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class taskUpdateMoeda extends Thread {

	private Moeda moeda;
	private boolean atualizaQuantidade = true;

	public taskUpdateMoeda(Moeda moeda, boolean atualizaQuantidade) {
		this.moeda = moeda;
		this.atualizaQuantidade = atualizaQuantidade;
	}

	public Moeda getMoeda() {
		try {
			this.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return moeda;
	}

	@Override
	public void run() {
		Moeda clone = moeda.clone();

		if (moeda.getNome().equalsIgnoreCase("BTC")) {
			moeda.setValor(moeda.getQuantidade());
			moeda.setValorCompra(moeda.getQuantidade());
			return;
		}
		try {
			if (atualizaQuantidade) {
				String dateString = "09 Nov 2012 23:40:18";
				DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
				Date date = dateFormat.parse(dateString);

				long start = (long) date.getTime() / 1000;

				String dateString2 = "09 Nov 2018 23:40:18";
				DateFormat dateFormat2 = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
				Date date2 = dateFormat2.parse(dateString2);
				long stop = (long) date2.getTime() / 1000;

				ArrayList<SimpleEntry<String, String>> extraParams = new ArrayList<>();
				extraParams
						.add(new SimpleEntry<String, String>("currencyPair", "BTC_" + moeda.getNome().toUpperCase()));
				extraParams.add(new SimpleEntry<String, String>("start", "" + start));
				extraParams.add(new SimpleEntry<String, String>("end", "" + stop));
				HttpEntity responseEntity = Portifolio.returnCommand("returnTradeHistory", extraParams);
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
							moeda.setValorCompra(ele.get("rate").getAsDouble());
							eleUti = ele;
						} else if (ele.get("date").getAsString()
								.compareToIgnoreCase(eleUti.get("date").getAsString()) >= 0) {
							moeda.setValorCompra(ele.get("rate").getAsDouble());
							eleUti = ele;
						}

					}
				}
			}

			HttpEntity responseEntity2 = Portifolio.returnPublic("returnTicker");
			if (responseEntity2 == null) {
				return;
			}
			String aux1 = EntityUtils.toString(responseEntity2);
			JsonObject json2 = new Gson().fromJson(aux1, JsonObject.class);
			moeda.setValor(
					json2.get("BTC_" + moeda.getNome().toUpperCase()).getAsJsonObject().get("last").getAsDouble());
		} catch (java.text.ParseException | JsonSyntaxException | ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			moeda = clone;
		}
	}

}
