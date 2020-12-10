package com.jfontes.minhasfinancas.service;

import java.util.List;

import com.jfontes.minhasfinancas.model.entity.Lancamento;
import com.jfontes.minhasfinancas.model.enums.StatusLancamento;


public interface LancamentoService {

	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamentoFiltro);
	
	void ataulzarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);
	
}
