package com.puc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "produto_eletronico")
@Data
public class ProdutoEletronico extends Produto {
    private String voltagem;
}