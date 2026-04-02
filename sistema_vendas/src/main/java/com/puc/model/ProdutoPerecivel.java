package com.puc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "produto_perecivel")
@Data
public class ProdutoPerecivel extends Produto {
    @Temporal(TemporalType.DATE)
    private java.util.Date dataValidade;
}