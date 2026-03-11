import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.demo.service.AdminService;
import com.example.demo.service.ProductService;
import com.example.demo.domain.Coupon;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(AdminService adminService, ProductService productService) {
        return args -> {
            adminService.addAdmin("admin1", "password123");
            productService.addCoupon(
                    "admin1",
                    "Amazon Gift Card",
                    "Amazon $100 Gift Card",
                    "https://img.com/amazon",
                    80.0,
                    20.0,
                    Coupon.ValueType.STRING,
                    "100"
            );
            productService.addCoupon(
                    "admin1",
                    "Steam Wallet",
                    "Steam Wallet Code",
                    "https://img.com/steam",
                    40.0,
                    25.0,
                    Coupon.ValueType.STRING,
                    "50"
            );
            System.out.println("Initial data loaded successfully.");
        };
    }
}