package ms_mensajeria.mensajeriaService.dto;

public class MensajeDTO {

    private String asuntoMensaje;
    private String cuerpoMensaje;
    private Long idUsuarioEmisor;
    private Long idUsuarioReceptor;

    public MensajeDTO() {
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
