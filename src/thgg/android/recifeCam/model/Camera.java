package thgg.android.recifeCam.model;

import java.io.Serializable;

public class Camera implements Serializable {

	private static final long serialVersionUID = 6354699593099098933L;

	private String descricao;
	private String nome;
	private String coordenadas;
	
	public Camera() {
		super();
	}
	
	public Camera(String descricao, String nome, String coordenadas) {
		super();
		this.descricao = descricao;
		this.nome = nome;
		this.coordenadas = coordenadas;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCoordenadas() {
		return coordenadas;
	}

	public void setCoordenadas(String coordenadas) {
		this.coordenadas = coordenadas;
	}
	
	@Override
	public boolean equals(Object o) {
		return this.descricao.equals(((Camera)o).getDescricao());
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new Camera(this.descricao, this.nome, this.coordenadas);
	}
	
	@Override
	public String toString() {
		return this.descricao; 
	}
}
