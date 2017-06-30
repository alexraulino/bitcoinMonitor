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
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

public class BitCoinMonitor {

	private static String URL_WEBSERVICE = "https://s3.amazonaws.com/data-production-walltime-info/production/dynamic/meta.json?now=1496234005679.1160540.0100000002";
	private static String NOME_ARQUIVO_HISTORICO = "BITCOIN_HISTORICO.bitCoin";
	private static String NOME_ARQUIVO_PORTIFOLIO = "BITCOIN_PORTIFOLIO.bitCoin";

	private static String MARKET_WALLTIME = "-WALLTIME";
	private static String MARKET_POLONIEX = "-POLONIEX";

	public static void main(String[] args) {
		try {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage("bitcoin.png");
			TrayIcon trayIcon = new TrayIcon(image, "MILHÕES");
			trayIcon.setImageAutoSize(true);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println("Não pode adicionar a tray");
			}

			if (Arrays.asList(args).contains(MARKET_WALLTIME)) {

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
			}

			if (Arrays.asList(args).contains(MARKET_POLONIEX)) {

				Portifolio portifolioBitCoin = null;
				// File file = new File(NOME_ARQUIVO_PORTIFOLIO);
				// if (file.exists()) {
				// JsonReader reader = new JsonReader(new
				// FileReader(NOME_ARQUIVO_PORTIFOLIO));
				// portifolioBitCoin = new Gson().fromJson(reader,
				// Portifolio.class);
				// } else {
				portifolioBitCoin = new Portifolio("Poliniex", args[1], args[2]);
				// }

				int x = 0;
				int y = 0;

				while (true) {
					if ((x == 30) || (x == 0)) {
						portifolioBitCoin.update();
					}
					x++;

					if ((y == 30) || (y == 0)) {
						portifolioBitCoin.updateMoeda();
					}
					y++;
					portifolioBitCoin.updateValoresMoedas();
					portifolioBitCoin.mostrarPortilofio();
					// file.delete();
					// try (Writer writer = new
					// FileWriter(NOME_ARQUIVO_PORTIFOLIO)) {
					// Gson gson = new GsonBuilder().create();
					// gson.toJson(portifolioBitCoin, writer);
					// }
				}

			}

			System.out.println("Não foi selecionado a market");

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
