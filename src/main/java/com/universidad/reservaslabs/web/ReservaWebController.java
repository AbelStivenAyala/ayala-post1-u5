package com.universidad.reservaslabs.web;

import com.universidad.reservaslabs.model.Reserva;
import com.universidad.reservaslabs.repository.LaboratorioRepository;
import com.universidad.reservaslabs.service.ReservaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reservas")
public class ReservaWebController {

    private final ReservaService service;             // misma instancia que usa ReservaController (REST)
    private final LaboratorioRepository laboratorioRepo;

    public ReservaWebController(ReservaService service, LaboratorioRepository laboratorioRepo) {
        this.service = service;
        this.laboratorioRepo = laboratorioRepo;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("reservas", service.findAll());
        return "reservas/lista";
    }

    @GetMapping("/nueva")
    public String formularioNueva(Model model) {
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("laboratorios", laboratorioRepo.findAll());
        return "reservas/nueva";
    }

    @PostMapping
    public String crear(@ModelAttribute Reserva reserva, RedirectAttributes redirect) {
        service.crear(reserva);                        // MISMA regla de solapamiento y horario que la API REST
        redirect.addFlashAttribute("mensaje", "Reserva creada correctamente");
        return "redirect:/reservas";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirect) {
        service.cancelar(id);
        redirect.addFlashAttribute("mensaje", "Reserva cancelada correctamente");
        return "redirect:/reservas";
    }
}
