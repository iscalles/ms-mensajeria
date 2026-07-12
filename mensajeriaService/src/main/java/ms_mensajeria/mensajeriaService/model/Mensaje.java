package ms_mensajeria.mensajeriaService.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "MENSAJE")
public class Mensaje {

    public static final String ESTADO_NO_LEIDO = "NO_LEIDO";
    public static final String ESTADO_LEIDO = "LEIDO";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "mensaje_seq")
    @SequenceGenerator(name = "mensaje_seq", sequenceName = "seq_mensaje", allocationSize = 1)
    @Column(name = "ID_MENSAJE")
    private Long idMensaje;

    @Column(name = "ASUNTO_MENSAJE", length = 200)
    private String asuntoMensaje;

    @Lob
    @Column(name = "CUERPO_MENSAJE", nullable = false)
    private String cuerpoMensaje;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm", timezone = "America/Santiago")
    @Column(name = "FECHA_ENVIO_MENSAJE", nullable = false)
    private LocalDateTime fechaEnvioMensaje;

    @Column(name = "ESTADO_MENSAJE", length = 20, nullable = false)
    private String estadoMensaje = ESTADO_NO_LEIDO;

    @Column(name = "ID_USUARIO_EMISOR", nullable = false)
    private Long idUsuarioEmisor;

    @Column(name = "ID_USUARIO_RECEPTOR", nullable = false)
    private Long idUsuarioReceptor;

    public Mensaje() {
    }

    public Long getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(Long idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getAsuntoMensaje() {
        return asuntoMensaje;
    }

    public void setAsuntoMensaje(String asuntoMensaje) {
        this.asuntoMensaje = asuntoMensaje;
    }

    public String getCuerpoMensaje() {
        return cuerpoMensaje;
    }

    public void setCuerpoMensaje(String cuerpoMensaje) {
        this.cuerpoMensaje = cuerpoMensaje;
    }

    public LocalDateTime getFechaEnvioMensaje() {
        return fechaEnvioMensaje;
    }

    public void setFechaEnvioMensaje(LocalDateTime fechaEnvioMensaje) {
        this.fechaEnvioMensaje = fechaEnvioMensaje;
    }

    public String getEstadoMensaje() {
        return estadoMensaje;
    }

    public void setEstadoMensaje(String estadoMensaje) {
        this.estadoMensaje = estadoMensaje;
    }

    public Long getIdUsuarioEmisor() {
        return idUsuarioEmisor;
    }

    public void setIdUsuarioEmisor(Long idUsuarioEmisor) {
        this.idUsuarioEmisor = idUsuarioEmisor;
    }

    public Long getIdUsuarioReceptor() {
        return idUsuarioReceptor;
    }

    public void setIdUsuarioReceptor(Long idUsuarioReceptor) {
        this.idUsuarioReceptor = idUsuarioReceptor;
    }
}
