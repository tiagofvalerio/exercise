package br.com.tvalerio.exercice.api;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.tvalerio.exercice.api.dto.SaleDTO;
import br.com.tvalerio.exercice.api.request.SalePostRequest;
import br.com.tvalerio.exercice.model.SaleEntity;
import br.com.tvalerio.exercice.model.assembler.SaleAssembler;
import br.com.tvalerio.exercice.service.SaleService;

@RestController
@RequestMapping("/sales")
public class SaleApi {

    @Autowired
    private SaleService saleService;

    @GetMapping("/{id}")
    public ResponseEntity<SaleDTO> searchSaleById(
            @PathVariable(value = "id") String id) {
        try {
            Optional<SaleEntity> sale = saleService.findById(Long.valueOf(id));

            if (sale.isPresent()) {
                return ResponseEntity.ok().body(new SaleAssembler()
                        .fromSaleEntityToSaleDTO(sale.get()));
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<SaleDTO> registerNewSale(
            @RequestBody SalePostRequest salePostRequest) {

        Optional<SaleEntity> sale = saleService.registerSale(salePostRequest);

        if (sale.isPresent()) {
            return ResponseEntity
                    .created(ServletUriComponentsBuilder.fromCurrentRequest()
                            .path("/{id}").buildAndExpand(sale.get().getId())
                            .toUri())
                    .body(new SaleAssembler()
                            .fromSaleEntityToSaleDTO(sale.get()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @SuppressWarnings("rawtypes")
    @GetMapping(produces = "application/json")
    public ResponseEntity<Page> findSales(
            @RequestParam(name = "start-date", required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
            @RequestParam(name = "end-date", required = true) @DateTimeFormat(iso = ISO.DATE) LocalDate endDate,
            @RequestParam(name = "order", defaultValue = "createdAt") String order,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "direction", defaultValue = "DESC") String direction) {
        try {
            SaleFilter filter = SaleFilter.builder()
                    .withStartDate(startDate.atStartOfDay())
                    .withEndDate(endDate.atTime(LocalTime.MAX)).withOrder(order)
                    .withPage(page).withSize(size).withDirection(direction)
                    .build();

            Page<SaleEntity> sales = saleService.findSalesPaginated(filter);

            return ResponseEntity.ok().body(
                    new SaleAssembler().fromSaleEntityPageToSaleDTOPage(sales));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
