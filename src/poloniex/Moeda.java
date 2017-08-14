package poloniex;

import com.google.gson.JsonObject;

public class Moeda extends Object {

	private String nome;
	private Double quantidade;
	private Double valor;
	private Double valorCompra;
	private Double quantidadeBTC;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Double getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Double quantidade) {
		this.quantidade = quantidade;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Double getValorCompra() {
		if (valorCompra == null){
			valorCompra = 0.0;
		}
		return valorCompra;
	}

	public void setValorCompra(Double valorCompra) {
		this.valorCompra = valorCompra;
	}

	public Double getQuantidadeBTC() {
		return quantidadeBTC;
	}

	public void setQuantidadeBTC(Double quantidadeBTC) {
		this.quantidadeBTC = quantidadeBTC;
	}

	public Moeda(String nome, JsonObject jsonObject) {
		super();
		this.nome = nome;
		this.quantidade = jsonObject.get("available").getAsDouble() + jsonObject.get("onOrders").getAsDouble();
		this.quantidadeBTC = jsonObject.get("btcValue").getAsDouble();
	}

	public Moeda(String nome, Double quantidade, Double valor, Double valorCompra, Double quantidadeBTC) {
		super();
		this.nome = nome;
		this.quantidade = quantidade;
		this.valor = valor;
		this.valorCompra = valorCompra;
		this.quantidadeBTC = quantidadeBTC;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((valorCompra == null) ? 0 : valorCompra.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + ((quantidade == null) ? 0 : quantidade.hashCode());
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Moeda other = (Moeda) obj;
		if (valorCompra == null) {
			if (other.valorCompra != null)
				return false;
		} else if (!valorCompra.equals(other.valorCompra))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (quantidade == null) {
			if (other.quantidade != null)
				return false;
		} else if (!quantidade.equals(other.quantidade))
			return false;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("%5s", nome) + " C=" + String.format("|%-15.8f", valorCompra) + " V="
				+ String.format("|%-15.8f", valor) + "D=" + String.format("|%-+15.8f", valor - valorCompra) + "P="
				+ String.format("|%-+12.2f", ((100 / valorCompra) * valor) - 100);
	}

	public Double getQtdBTC() {
		// TODO Auto-generated method stub
		if (nome.equalsIgnoreCase("BTC")) {
			return quantidade;
		}
		return quantidade * valor;
	}

	@Override
	public Moeda clone() {
		return new Moeda(nome, quantidade, valor, valorCompra, quantidadeBTC);
	}

}
