package ms_mensajeria.mensajeriaService.controller;

import ms_mensajeria.mensajeriaService.dto.MensajeDTO;
import ms_mensajeria.mensajeriaService.model.Mensaje;
import ms_mensajeria.mensajeriaService.service.MensajeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mensajes")
public class MensajeController {

    private final MensajeService service;

    public MensajeController(MensajeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Mensaje> enviarMensaje(@RequestHeader("X-User-Id") Long idUsuarioActual,
                                                  @RequestBody MensajeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enviarMensaje(idUsuarioActual, dto));
    }

    @GetMapping("/conversacion")
    public List<Mensaje> listarConversacion(@RequestHeader("X-User-Id") Long idUsuarioActual,
                                             @RequestParam Long usuario1, @RequestParam Long usuario2) {
        return service.listarConversacion(idUsuarioActual, usuario1, usuario2);
    }

    @GetMapping("/bandeja/{idUsuario}")
    public List<Mensaje> listarBandejaEntrada(@RequestHeader("X-User-Id") Long idUsuarioActual,
                                               @PathVariable Long idUsuario) {
        return service.listarBandejaEntrada(idUsuarioActual, idUsuario);
    }

    @GetMapping("/enviados/{idUsuario}")
    public List<Mensaje> listarEnviados(@RequestHeader("X-User-Id") Long idUsuarioActual,
                                         @PathVariable Long idUsuario) {
        return service.listarEnviados(idUsuarioActual, idUsuario);
    }

    @GetMapping("/bandeja/{idUsuario}/no-leidos/count")
    public long contarNoLeidos(@RequestHeader("X-User-Id") Long idUsuarioActual,
                                @PathVariable Long idUsuario) {
        return service.contarNoLeidos(idUsuarioActual, idUsuario);
    }

    @PutMapping("/{id}/marcar-leido")
    public Mensaje marcarLeido(@RequestHeader("X-User-Id") Long idUsuarioActual,
                                @PathVariable Long id) {
        return service.marcarLeido(idUsuarioActual, id);
    }
}
