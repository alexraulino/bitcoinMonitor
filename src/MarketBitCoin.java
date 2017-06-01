import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MarketBitCoin {

	private String nome;
	private ArrayList<ValorBitCoin> historicoValores = new ArrayList<ValorBitCoin>();

	public MarketBitCoin(String nome) {
		super();
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Double getMedia(int qtd){
		int x = 0;
		Double media = 0.0;
		Collections.sort(historicoValores, new comparadorBitCoin());
		int t = 0;
		if (historicoValores.size() > qtd){
			t = historicoValores.size() - qtd;
		}else{
			t = 0;
		}
		
		for (int y = t; y < historicoValores.size(); y++) {		
			media = media + historicoValores.get(y).getValorVenda() + historicoValores.get(y).getValorCompra();
			x++;
		}	
		return media / (2 * x);
	}

	public ValorBitCoin addValor(Double valorVenda, Double valorCompra) {
		ValorBitCoin vb = new ValorBitCoin(
				new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime()), valorVenda,
				valorCompra);
		historicoValores.add(vb);
		return vb;
	}

	class comparadorBitCoin implements Comparator<ValorBitCoin> {

		@Override
		public int compare(ValorBitCoin o1, ValorBitCoin o2) {
			return o1.getData().compareTo(o2.getData());
		}

	}

	public void mostrarValores() {
		System.out.println("Market: " + nome);
		System.out.println("+++++++++++++++++++++++++++++++++");
		Collections.sort(historicoValores, new comparadorBitCoin());
		int t = 0;
		
		if (historicoValores.size() > 15){
			t = historicoValores.size() - 15;
		}else{
			t = 0;
		}
		
		for (int y = t; y < historicoValores.size(); y++) {			
			System.out.println(historicoValores.get(y));		
		}

	}

	private Double getValorJSON(String str) {
		Double valor1, valor2 = 0.0;
		if (str.contains("/")) {
			String[] valores = str.split("/");
			valor1 = Double.valueOf(valores[0]);
			valor2 = Double.valueOf(valores[1]);
			if (valor2 > valor1){
				return valor2 / valor1;	
			}else {
			return valor1 / valor2;
			}

		} else {
			return Double.valueOf(str);
		}

	}

	public void update(String uRL_WEBSERVICE, TrayIcon ti) throws IOException {
		URL url = new URL(uRL_WEBSERVICE);
		InputStreamReader input = new InputStreamReader(url.openStream());
		JsonObject json = new Gson().fromJson(input, JsonObject.class);
		String strVenda, strCompra;
		strCompra = json.get("best_offer").getAsJsonObject().get("xbt-brl").getAsString();
		strVenda = json.get("best_offer").getAsJsonObject().get("brl-xbt").getAsString();
		Double vVenda, vCompra;
		vVenda = getValorJSON(strVenda);
		vCompra = getValorJSON(strCompra);
		System.out.println(addValor(vVenda, vCompra) + " MP= " + String.format("%1$,.2f", getMedia(15)));
//		ti.displayMessage("Teste", addValor(vVenda, vCompra).toString(), MessageType.INFO);
	}

}
