package br.edu.ifsudestemg.eventsAPI.model.repository;

import br.edu.ifsudestemg.eventsAPI.model.entity.UsuarioPessoaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioPessoaJuridicaRepository extends JpaRepository<UsuarioPessoaJuridica, Long> {
    public boolean existsByRazaoSocialIgnoreCase(String razaoSocial);
    public boolean existsByCnpj(String cnpj);
    public boolean existsByInscricaoEstadualMunicipal(String inscricaoEstadualMunicipal);
}
