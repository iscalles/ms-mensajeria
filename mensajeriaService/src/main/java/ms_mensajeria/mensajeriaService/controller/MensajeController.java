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
    public ResponseEntity<Mensaje> enviarMensaje(@RequestBody MensajeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enviarMensaje(dto));
    }

    @GetMapping("/conversacion")
    public List<Mensaje> listarConversacion(@RequestParam Long usuario1, @RequestParam Long usuario2) {
        return service.listarConversacion(usuario1, usuario2);
    }

    @GetMapping("/bandeja/{idUsuario}")
    public List<Mensaje> listarBandejaEntrada(@PathVariable Long idUsuario) {
        return service.listarBandejaEntrada(idUsuario);
    }

    @GetMapping("/enviados/{idUsuario}")
    public List<Mensaje> listarEnviados(@PathVariable Long idUsuario) {
        return service.listarEnviados(idUsuario);
    }

    @GetMapping("/bandeja/{idUsuario}/no-leidos/count")
    public long contarNoLeidos(@PathVariable Long idUsuario) {
        return service.contarNoLeidos(idUsuario);
    }

    @PutMapping("/{id}/marcar-leido")
    public Mensaje marcarLeido(@PathVariable Long id) {
        return service.marcarLeido(id);
    }
}
