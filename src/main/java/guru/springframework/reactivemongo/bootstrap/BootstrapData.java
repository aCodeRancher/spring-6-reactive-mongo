package guru.springframework.reactivemongo.bootstrap;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.domain.Customer;
import guru.springframework.reactivemongo.repositories.BeerRepository;
import guru.springframework.reactivemongo.repositories.CustomerRepsoitory;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by jt, Spring Framework Guru.
 */
@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final BeerRepository beerRepository;

    private final CustomerRepsoitory customerRepsoitory;

    @Override
    public void run(String... args) throws Exception {
        beerRepository.deleteAll()
                .doOnSuccess(success -> {
                    loadBeerData();
                }).subscribe();

        customerRepsoitory.deleteAll()
                .doOnSuccess(success -> {
                     loadCustomerData();
                }).subscribe();
    }

    private void loadBeerData() {

        beerRepository.count().subscribe(count -> {
            if (count == 0) {
                Beer beer1 = Beer.builder()
                        .beerName("Galaxy Cat")
                        .beerStyle("Pale Ale")
                        .upc("12356")
                        .price(new BigDecimal("12.99"))
                        .quantityOnHand(122)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build();

                Beer beer2 = Beer.builder()
                        .beerName("Crank")
                        .beerStyle("Pale Ale")
                        .upc("12356222")
                        .price(new BigDecimal("11.99"))
                        .quantityOnHand(392)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build();

                Beer beer3 = Beer.builder()
                        .beerName("Sunshine City")
                        .beerStyle("IPA")
                        .upc("12356")
                        .price(new BigDecimal("13.99"))
                        .quantityOnHand(144)
                        .createdDate(LocalDateTime.now())
                        .lastModifiedDate(LocalDateTime.now())
                        .build();

                beerRepository.save(beer1).subscribe();
                beerRepository.save(beer2).subscribe();
                beerRepository.save(beer3).subscribe();
            }
        });
    }

    private void loadCustomerData() {

        customerRepsoitory.count().subscribe(count -> {
            if (count == 0) {
                Customer c1 = Customer.builder()
                              .customerName("Customer 1")
                              .createdDate(LocalDateTime.now())
                              .lastModifiedDate(LocalDateTime.now())
                               .build();

                Customer c2 = Customer.builder()
                              .customerName("Customer 2")
                              .createdDate(LocalDateTime.now())
                              .lastModifiedDate(LocalDateTime.now())
                              .build();

                Customer c3 = Customer.builder()
                              .customerName("Customer 3")
                             .createdDate(LocalDateTime.now())
                              .lastModifiedDate(LocalDateTime.now())
                              .build();

                customerRepsoitory.save(c1).subscribe();
                customerRepsoitory.save(c2).subscribe();
                customerRepsoitory.save(c3).subscribe();
            }
        });
    }
}
