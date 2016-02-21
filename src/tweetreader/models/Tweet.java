package tweetreader.models;

/**
 * Classe que representa um tweet, contendo apenas três informações básicas
 * utilizadas para renderizar um tweet na view: usuario, mensagem e data de
 * envio.
 * 
 * @author Felipe Augusto
 * 
 */
public class Tweet {

	private String usuario;
	private String mensagem;
	private String dataEnvio;

	public Tweet() {
		super();
	}

	public Tweet(String usuario, String mensagem, String dataEnvio) {
		super();
		this.usuario = usuario;
		this.mensagem = mensagem;
		this.dataEnvio = dataEnvio;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(String dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	@Override
	public String toString() {
		return "Tweet [usuario=" + usuario + ", mensagem=" + mensagem
				+ ", dataEnvio=" + dataEnvio + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataEnvio == null) ? 0 : dataEnvio.hashCode());
		result = prime * result
				+ ((mensagem == null) ? 0 : mensagem.hashCode());
		result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
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
		Tweet other = (Tweet) obj;
		if (dataEnvio == null) {
			if (other.dataEnvio != null)
				return false;
		} else if (!dataEnvio.equals(other.dataEnvio))
			return false;
		if (mensagem == null) {
			if (other.mensagem != null)
				return false;
		} else if (!mensagem.equals(other.mensagem))
			return false;
		if (usuario == null) {
			if (other.usuario != null)
				return false;
		} else if (!usuario.equals(other.usuario))
			return false;
		return true;
	}

}
