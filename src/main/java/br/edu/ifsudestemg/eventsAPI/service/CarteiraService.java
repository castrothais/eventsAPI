
package br.edu.ifsudestemg.eventsAPI.service;

import br.edu.ifsudestemg.eventsAPI.api.exeption.RegraNegocioException;
import br.edu.ifsudestemg.eventsAPI.model.entity.*;
import br.edu.ifsudestemg.eventsAPI.model.repository.CarteiraRepository;
import br.edu.ifsudestemg.eventsAPI.model.repository.MovimentacaoCarteiraRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CarteiraService {

    private final CarteiraRepository carteiraRepository;
    private final MovimentacaoCarteiraRepository movimentacaoRepository;

    public CarteiraService(
            CarteiraRepository carteiraRepository,
            MovimentacaoCarteiraRepository movimentacaoRepository) {
        this.carteiraRepository = carteiraRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    @Transactional
    public Carteira obterOuCriarCarteira(Usuario usuario) {

        return carteiraRepository.findByUsuario_Id(usuario.getId())
                .orElseGet(() -> {
                    Carteira carteira = new Carteira();
                    carteira.setUsuario(usuario);
                    carteira.setSaldo(BigDecimal.ZERO);

                    return carteiraRepository.save(carteira);
                });
    }

    @Transactional(readOnly = true)
    public BigDecimal consultarSaldo(Long usuarioId) {

        return carteiraRepository.findByUsuario_Id(usuarioId)
                .map(Carteira::getSaldo)
                .orElse(BigDecimal.ZERO);
    }

    @Transactional
    public void creditar(
            Usuario usuario,
            BigDecimal valor,
            Ingresso ingresso) {

        validarValor(valor);

        if (ingresso != null &&
                movimentacaoRepository.existsByIngresso_IdAndTipo(
                        ingresso.getId(), TipoMovimentacao.CREDITO)) {
            throw new RegraNegocioException(
                    "Este ingresso já gerou um crédito na carteira."
            );
        }

        Carteira carteira = obterOuCriarCarteira(usuario);

        carteira.setSaldo(carteira.getSaldo().add(valor));
        carteiraRepository.save(carteira);

        registrarMovimentacao(
                carteira, ingresso, TipoMovimentacao.CREDITO, valor
        );
    }

    @Transactional(noRollbackFor = RegraNegocioException.class)
    public void debitar(
            Usuario usuario,
            BigDecimal valor,
            Ingresso ingresso) {

        validarValor(valor);

        if (ingresso != null &&
                movimentacaoRepository.existsByIngresso_IdAndTipo(
                        ingresso.getId(), TipoMovimentacao.DEBITO)) {
            throw new RegraNegocioException(
                    "Este ingresso já possui um débito registrado na carteira."
            );
        }

        Carteira carteira = carteiraRepository
                .findByUsuarioIdComBloqueio(usuario.getId())
                .orElseThrow(() -> new RegraNegocioException(
                        "Carteira não encontrada."
                ));

        if (carteira.getSaldo().compareTo(valor) < 0) {
            throw new RegraNegocioException(
                    "Saldo insuficiente na carteira."
            );
        }

        carteira.setSaldo(carteira.getSaldo().subtract(valor));
        carteiraRepository.save(carteira);

        registrarMovimentacao(
                carteira, ingresso, TipoMovimentacao.DEBITO, valor
        );
    }

    private void registrarMovimentacao(
            Carteira carteira,
            Ingresso ingresso,
            TipoMovimentacao tipo,
            BigDecimal valor) {

        MovimentacaoCarteira movimentacao = new MovimentacaoCarteira();

        movimentacao.setCarteira(carteira);
        movimentacao.setIngresso(ingresso);
        movimentacao.setTipo(tipo);
        movimentacao.setValor(valor);

        movimentacaoRepository.save(movimentacao);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoCarteira> consultarMovimentacoes(Long carteiraId) {
        return movimentacaoRepository
                .findByCarteira_IdOrderByDataHoraDesc(carteiraId);
    }

    private void validarValor(BigDecimal valor) {

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException(
                    "O valor da movimentação deve ser maior que zero."
            );
        }
    }
}
