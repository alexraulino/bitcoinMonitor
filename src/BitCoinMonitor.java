import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class BitCoinMonitor {

	private static String URL_WEBSERVICE = "https://s3.amazonaws.com/data-production-walltime-info/production/dynamic/meta.json?now=1496234005679.1160540.0100000002";
	private static String NOME_ARQUIVO_HISTORICO = "BITCOIN_HISTORICO.bitCoin";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {

			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage("bitcoin.png");
//			PopupMenu popup = new PopupMenu();
//			MenuItem item = new MenuItem("");
//			popup.add(item);
			TrayIcon trayIcon = new TrayIcon(image, "MILHÕES");
			trayIcon.setImageAutoSize(true);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println("Não pode adicionar a tray");
			}

			MarketBitCoin marketBitCoin = null;
			File file = new File(NOME_ARQUIVO_HISTORICO);
			if (file.exists()) {
				JsonReader reader = new JsonReader(new FileReader(NOME_ARQUIVO_HISTORICO));
				marketBitCoin = new Gson().fromJson(reader, MarketBitCoin.class);
			} else {
				marketBitCoin = new MarketBitCoin("WallTime");
			}

			marketBitCoin.mostrarValores();
			while (true) {
				marketBitCoin.update(URL_WEBSERVICE, trayIcon);
				file.delete();
				try (Writer writer = new FileWriter(NOME_ARQUIVO_HISTORICO)) {
					Gson gson = new GsonBuilder().create();
					gson.toJson(marketBitCoin, writer);
				}
				Thread.sleep(60000);
			}
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
