package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.model.CustomerDTO;
import guru.springframework.reactivemongo.services.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerHandler {

    private final CustomerService customerService;

    public Mono<ServerResponse> listCustomers(ServerRequest request){
        Flux<CustomerDTO> flux = customerService.listCustomers();
      return ServerResponse.ok()
                .body(flux, CustomerDTO.class);
    }

    public Mono<ServerResponse> deleteCustomerById(ServerRequest request){
        return customerService.getCustomerById(request.pathVariable("customerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(customerDTO -> customerService.deleteCustomerById(customerDTO.getId()))
                .then(ServerResponse.noContent().build());
    }
    public Mono<ServerResponse> getCustomerById(ServerRequest request){
        return ServerResponse
                .ok()
                .body(customerService.getCustomerById(request.pathVariable("customerId"))
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),
                        CustomerDTO.class);
    }

    public Mono<ServerResponse> createNewCustomer(ServerRequest request){
        return customerService.saveNewCustomer(request.bodyToMono(CustomerDTO.class))
                .flatMap(customerDTO -> ServerResponse
                        .created(UriComponentsBuilder
                                .fromPath(CustomerRouterConfig.CUSTOMER_PATH_ID)
                                .build(customerDTO.getId()))
                        .body(customerService.getCustomerById(customerDTO.getId() ), CustomerDTO.class));

    }

    public Mono<ServerResponse> updateCustomerById(ServerRequest request) {
        return request.bodyToMono(CustomerDTO.class)
                .flatMap(customerDTO -> customerService
                        .updateCustomer(request.pathVariable("customerId"), customerDTO))
                 .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                 .flatMap(savedDto -> ServerResponse.ok().body(customerService.getCustomerById(savedDto.getId()), CustomerDTO.class));
    }
    public Mono<ServerResponse> patchCustomerById(ServerRequest request){
        return request.bodyToMono(CustomerDTO.class)
                .flatMap(customerDTO -> customerService
                        .patchCustomer(request.pathVariable("customerId"),customerDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(savedDto -> ServerResponse.noContent().build());
    }
}

