package edu.hsh.favs.project.escqrs.services.customerservice.service;

import edu.hsh.favs.project.escqrs.domains.customers.Customer;
import edu.hsh.favs.project.escqrs.services.customerservice.repository.CustomerRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.logging.Logger;

@Service
public class CustomerService {

    private final Logger log = Logger.getLogger(CustomerService.class.getName());
    private final CustomerRepository repo;
    private final R2dbcEntityTemplate template;
    private final TransactionalOperator txOperator;

    public CustomerService(
            CustomerRepository repo,
            R2dbcEntityTemplate template,
            TransactionalOperator txOperator) {
        this.repo = repo;
        this.template = template;
        this.txOperator = txOperator;
    }

    public Mono<Customer> find(Long id) {
    return repo.findById(id);
    }

    public Mono<Customer> createCustomer(
            Customer customer,
            Consumer<Customer> callback
    ) {
        return template.insert(Customer.class)
                .using(customer)
                .as(txOperator::transactional)
                .doOnSuccess(callback);
    }
}
