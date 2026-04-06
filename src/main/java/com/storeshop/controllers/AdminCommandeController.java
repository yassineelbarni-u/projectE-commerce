package com.storeshop.controllers;

import com.storeshop.services.CommandeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/commandes")
@AllArgsConstructor
/**
 * Admin controller for operational order management.
 */
public class AdminCommandeController {

  private final CommandeService commandeService;

  /**
   * Displays every order for back-office monitoring.
   *
   * @param model MVC model receiving all orders
   * @return admin orders template
   */
  @GetMapping
  public String listOrders(Model model) {
    model.addAttribute("orders", commandeService.listAllOrders());
    return "admin/commandes";
  }

  /**
   * Updates workflow status for one order.
   *
   * @param id order id
   * @param status new status label (for example VALIDEE, EXPEDIEE)
   * @return redirect to admin orders list with success flag
   */
  @PostMapping("/status")
  public String updateStatus(
      @RequestParam(name = "id") Long id, @RequestParam(name = "status") String status) {
    commandeService.updateStatus(id, status);
    return "redirect:/admin/commandes?success";
  }
}
