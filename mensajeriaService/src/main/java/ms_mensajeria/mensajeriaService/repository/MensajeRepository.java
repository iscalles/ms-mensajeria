package ms_mensajeria.mensajeriaService.repository;

import ms_mensajeria.mensajeriaService.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    @Query("SELECT m FROM Mensaje m " +
            "WHERE (m.idUsuarioEmisor = :idUsuario1 AND m.idUsuarioReceptor = :idUsuario2) " +
            "   OR (m.idUsuarioEmisor = :idUsuario2 AND m.idUsuarioReceptor = :idUsuario1) " +
            "ORDER BY m.fechaEnvioMensaje ASC")
    List<Mensaje> findConversacion(@Param("idUsuario1") Long idUsuario1, @Param("idUsuario2") Long idUsuario2);

    List<Mensaje> findByIdUsuarioReceptorOrderByFechaEnvioMensajeDesc(Long idUsuarioReceptor);

    List<Mensaje> findByIdUsuarioEmisorOrderByFechaEnvioMensajeDesc(Long idUsuarioEmisor);

    long countByIdUsuarioReceptorAndEstadoMensaje(Long idUsuarioReceptor, String estadoMensaje);
}
