package com.acme.reserve;

import com.acme.reserve.exceptions.ReservedStockNotFound;
import com.acme.stock.exceptions.NotEnoughInStock;
import com.acme.stock.exceptions.StockNotFound;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reserved-stock")
class ReserveController {
    final ReservedStockRepository repository;
    final ReservedStockService service;

    @PostMapping("/reserve")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ReservedStockResponse reserveStock(@RequestBody ReserveStockRequest request) throws StockNotFound, NotEnoughInStock {
        ReservedStock reserved = service.reserve(request);
        return new ReservedStockResponse(reserved);
    }

    @PostMapping("/sell")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void reserveStock(@RequestBody SellRequest request) throws ReservedStockNotFound {
        service.sellReservedStock(request);
    }

    @GetMapping("/list")
    public List<ReservedStockResponse> listAll() {
        return service.findAll().stream().map(ReservedStockResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/find")
    public ReservedStockResponse find(@RequestParam UUID branch, @RequestParam UUID product) throws ReservedStockNotFound {
        return repository.findByBranchAndProduct(branch, product).map(ReservedStockResponse::new)
                .orElseThrow(() -> new ReservedStockNotFound("branch: " + branch + ", product: " + product));
    }

    /**
     * Allows employees to see the reservations they created.
     *
     * @param createdBy
     * @return
     */
    @GetMapping(value = "/findCreatedBy")
    public List<ReservedStockResponse> findCreatedBy(@RequestParam String createdBy) { // XXX Use active principal(?)
        return repository.findByCreatedBy(createdBy).stream().map(ReservedStockResponse::new).collect(Collectors.toList());
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
class ReserveStockRequest {
    @NotNull
    UUID branch;
    @NotNull
    UUID product;
    @Positive
    int numberOfItems;
}

@Value
// TODO HATEOAS through ResourceSupport
class ReservedStockResponse {
    UUID id;
    UUID branch;
    UUID product;
    int numberOfItems;
    String createdBy;
    Date createdDate;

    ReservedStockResponse(ReservedStock reserved) {
        this.id = reserved.getId();
        this.branch = reserved.getBranch();
        this.product = reserved.getProduct();
        this.numberOfItems = reserved.getNumberOfItems();
        this.createdBy = reserved.getCreatedBy();
        this.createdDate = reserved.getCreatedDate();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
class SellRequest {
    UUID id;
}
