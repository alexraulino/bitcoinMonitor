import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.util.Arrays;

import poloniex.Portifolio;

public class BitCoinMonitor {

	private static String MARKET_POLONIEX = "-POLONIEX";

	public static void main(String[] args) {
		try {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage("bitcoin.png");
			TrayIcon trayIcon = new TrayIcon(image, "BitCoin Monitor");
			trayIcon.setImageAutoSize(true);
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println("Não pode adicionar a tray");
			}

			if (Arrays.asList(args).contains(MARKET_POLONIEX)) {
				Portifolio portifolioBitCoin = null;
				try {

					portifolioBitCoin = new Portifolio("Poliniex", args[1], args[2]);
					portifolioBitCoin.updateDepositsWithdrawals();
					portifolioBitCoin.updatePortifolio();

				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				boolean xAtualizaQuantidade = true;
				while (true) {
					portifolioBitCoin.updateValoresMoedas(xAtualizaQuantidade);
					portifolioBitCoin.mostrarPortilofio();
					xAtualizaQuantidade = false;
					Thread.sleep(10000);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
