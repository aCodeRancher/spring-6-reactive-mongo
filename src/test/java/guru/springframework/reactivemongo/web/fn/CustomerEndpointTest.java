package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.model.CustomerDTO;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import java.util.List;
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
        String inputID = "1";
        getSavedTestCustomer(inputID);
        webTestClient.get().uri(CustomerRouterConfig.CUSTOMER_PATH_ID, inputID)
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
          Customer customer = Customer.builder().customerName("Mary").build();
         webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(customer), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location")
                .expectBody(CustomerDTO.class)
                 .consumeWith(consumer -> assertTrue(consumer.getResponseBody().getCustomerName().equals(customer.getCustomerName())));
     }

    @Test
     void testUpdateCustomer() {
        String inputID = "190";
        getSavedTestCustomer(inputID);
        Customer customer = Customer.builder().customerName("Mary").build();

        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, inputID)
                .body(Mono.just(customer), CustomerDTO.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDTO.class)
                .consumeWith(result-> assertTrue(result.getResponseBody().getCustomerName().equals("Mary")));
    }


    @Test
    void testUpdateCustomerNotFound() {

        Customer customer  = Customer.builder().customerName("Not").build();
        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, 999)
                .body(Mono.just(customer), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void testPatchId() {
        String inputID = "200";
        getSavedTestCustomer(inputID);
        Customer customer = Customer.builder().customerName("U").build();
        webTestClient.patch()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, inputID)
                .body(Mono.just(customer), CustomerDTO.class)
                .exchange()
                .expectStatus().isNoContent();

    }


    @Test
    void testPatchIdNotFound() {
         Customer customer = Customer.builder().customerName("T").build();

        webTestClient.patch()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, "999")
                .body(Mono.just(customer), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    public void getSavedTestCustomer(String inputID){
       Customer tester = Customer.builder().id(inputID).customerName("Customer T").build();
        FluxExchangeResult<CustomerDTO> customerDTOFluxExchangeResult =
                webTestClient.post().uri(CustomerRouterConfig.CUSTOMER_PATH)
                .body(Mono.just(tester), CustomerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .returnResult(CustomerDTO.class);

        List<String> location = customerDTOFluxExchangeResult.getResponseHeaders().get("Location");

    }

    @Test
    @Order(999)
    void testDeleteCustomer() {

        webTestClient.delete()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, "190")
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void testUpdateIdNotFound() {
        Customer ctest = Customer.builder().customerName("T").build();
        webTestClient.put()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, "999")
                .body(Mono.just(ctest), CustomerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteCustomerNotFound() {
        webTestClient.delete()
                .uri(CustomerRouterConfig.CUSTOMER_PATH_ID, "999")
                .exchange()
                .expectStatus()
                .isNotFound();
    }


}
