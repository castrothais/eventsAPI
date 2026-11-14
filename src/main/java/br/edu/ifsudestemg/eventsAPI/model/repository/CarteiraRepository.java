package br.edu.ifsudestemg.eventsAPI.model.repository;

import br.edu.ifsudestemg.eventsAPI.model.entity.Carteira;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CarteiraRepository extends JpaRepository<Carteira, Long> {

    Optional<Carteira> findByUsuario_Id(Long usuarioId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Carteira c WHERE c.usuario.id = :usuarioId")
    Optional<Carteira> findByUsuarioIdComBloqueio(
            @Param("usuarioId") Long usuarioId
    );
}
