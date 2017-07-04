
public class ValorBitCoin {

	private String data;
	private Double valorVenda;
	private Double valorCompra;

	public ValorBitCoin(String data, Double valorVenda, Double valorCompra) {
		super();
		this.data = data;
		this.valorVenda = valorVenda;
		this.valorCompra = valorCompra;
	}

	@Override
	public String toString() {
		return "Data: " + data + ", V=" + String.format("%1$,.2f", valorVenda) + ", C="
				+ String.format("%1$,.2f", valorCompra) + ", M= "
				+ String.format("%1$,.2f", (valorCompra + valorVenda) / 2);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
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
		ValorBitCoin other = (ValorBitCoin) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}

	public Double getValorVenda() {
		return valorVenda;
	}

	public void setValorVenda(Double valorVenda) {
		this.valorVenda = valorVenda;
	}

	public Double getValorCompra() {
		return valorCompra;
	}

	public void setValorCompra(Double valorCompra) {
		this.valorCompra = valorCompra;
	}

}
