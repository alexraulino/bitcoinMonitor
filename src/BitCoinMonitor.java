import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.Arrays;

import org.jfree.ui.RefineryUtilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import banco.Banco;
import chart.BitCoinMonitorChart;
import chart.taskUpdateChart;
import poloniex.Portifolio;

public class BitCoinMonitor {

	private static String URL_WEBSERVICE = "https://s3.amazonaws.com/data-production-walltime-info/production/dynamic/meta.json?now=1496234005679.1160540.0100000002";
	private static String NOME_ARQUIVO_HISTORICO = "BITCOIN_HISTORICO.bitCoin";
	private static String NOME_ARQUIVO_PORTIFOLIO = "BITCOIN_PORTIFOLIO.bitCoin";

	private static String MARKET_WALLTIME = "-WALLTIME";
	private static String MARKET_POLONIEX = "-POLONIEX";
	private static String MARKET_POLONIEX_CHART = "-POLONIEXCHART";

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

				}
			}

			if (Arrays.asList(args).contains(MARKET_POLONIEX)) {

				Portifolio portifolioBitCoin = null;
				try {

					portifolioBitCoin = new Portifolio("Poliniex", args[1], args[2], Banco.abrir());

					portifolioBitCoin.updateDepositsWithdrawals();
					portifolioBitCoin.updatePortifolio();

				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				boolean xAtualizaQuantidade = true;
				while (true) {
					portifolioBitCoin.updateValoresMoedas(xAtualizaQuantidade);
					portifolioBitCoin.mostrarPortilofio();
					portifolioBitCoin.registrarHistorico();
					xAtualizaQuantidade = false;
					Thread.sleep(6000);
				}

			}

			if (Arrays.asList(args).contains(MARKET_POLONIEX_CHART)) {
				final String title = "BitCoinMonitor";
				final BitCoinMonitorChart demo = new BitCoinMonitorChart(title);
				demo.pack();
				RefineryUtilities.positionFrameRandomly(demo);
				demo.setVisible(true);
				taskUpdateChart tk = new taskUpdateChart(demo.tm);
				tk.run();
			}

			System.out.println("Não foi selecionado a market");

		} catch (IOException | SQLException | InterruptedException e) {
			e.printStackTrace();
		}

	}

}
