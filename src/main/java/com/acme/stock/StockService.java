package com.acme.stock;

import com.acme.stock.exceptions.NotEnoughInStock;
import com.acme.stock.exceptions.StockNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {
    final StockRepository repository;

    @Transactional
    public void addToStock(StockRequest addRequest) {
        Stock stock = repository.findByBranchAndProduct(addRequest.getBranch(), addRequest.getProduct())
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setBranch(addRequest.getBranch());
                    newStock.setProduct(addRequest.getProduct());
                    return newStock;
                });
        int numberAdded = addRequest.getNumberOfItems();
        int numberOfItems = stock.getNumberOfItems();
        stock.setNumberOfItems(numberOfItems + numberAdded);
        repository.save(stock);
    }

    @Transactional
    public void removeFromStock(StockRequest removeRequest) throws StockNotFound, NotEnoughInStock {
        Stock stock = repository.findByBranchAndProduct(removeRequest.getBranch(), removeRequest.getProduct())
                .orElseThrow(() -> new StockNotFound(removeRequest.toString()));
        int numberRequested = removeRequest.getNumberOfItems();
        int numberInStock = stock.getNumberOfItems();
        if (numberInStock < numberRequested) {
            throw new NotEnoughInStock(removeRequest + " exceeds " + stock);
        }
        stock.setNumberOfItems(numberInStock - numberRequested);
        repository.save(stock);
    }
}
