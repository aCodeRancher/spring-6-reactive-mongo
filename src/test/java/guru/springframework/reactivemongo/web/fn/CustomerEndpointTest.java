package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.model.CustomerDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureWebTestClient
public class CustomerEndpointTest {

    @Autowired
    WebTestClient webTestClient;


    @Test
    void testListCustomers() {
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody().jsonPath("$.size()").value(greaterThan(1));
    }

    @Test
    void testGetById() {
        CustomerDTO customerDTO = getSavedTestCustomer();

        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody(CustomerDTO.class);
    }

    @Test
    void testGetByIdNotFound() {
         webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
     }

    @Test
    void testCreateCustomer() {
        CustomerDTO testCustomer = getSavedTestCustomer();
         String name = testCustomer.getCustomerName();
         webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(testCustomer), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location")
                .expectBody(CustomerDTO.class)
                 .consumeWith(consumer -> assertTrue(consumer.getResponseBody().getCustomerName().equals(name)));
     }

    @Test
     void testUpdateCustomer() {
        String updatedName = "Customer U";
        CustomerDTO customerDTO = getSavedTestCustomer();
        customerDTO.setCustomerName(updatedName);

        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTO.class)
                .consumeWith(result-> assertTrue(result.getResponseBody().getCustomerName().equals(updatedName)));
    }


    @Test
    void testUpdateCustomerNotFound() {

        CustomerDTO customerDTO = getSavedTestCustomer();
        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testPatchId() {
        CustomerDTO customerDTO = getSavedTestCustomer();
        String updatedName = "Customer U";
        customerDTO.setCustomerName(updatedName);
        webTestClient.patch()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, customerDTO.getId())
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTO.class)
                .consumeWith(result->assertTrue(result.getResponseBody().getCustomerName().equals(updatedName)));
    }

    @Test
    void testPatchIdNotFound() {
        CustomerDTO customerDTO = getSavedTestCustomer();
        String updatedName = "Customer U";
        customerDTO.setCustomerName(updatedName);
        webTestClient.patch()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .body(Mono.just(customerDTO), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    public CustomerDTO getSavedTestCustomer(){

        AtomicReference<CustomerDTO> customerTested = new AtomicReference<>();
        Customer tester = Customer.builder().customerName("Customer T").build();
        FluxExchangeResult<CustomerDTO> customerDTOFluxExchangeResult =
                webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(tester), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .returnResult(CustomerDTO.class);

        List<String> location = customerDTOFluxExchangeResult.getResponseHeaders().get("Location");

        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .exchange().returnResult(CustomerDTO.class).getResponseBody()
                .subscribe(customerDTO -> customerTested.set(customerDTO));
        return customerTested.get();
    }
}
