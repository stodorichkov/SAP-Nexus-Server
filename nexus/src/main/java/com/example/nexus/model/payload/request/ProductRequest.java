package com.example.nexus.model.payload.request;
import com.example.nexus.constant.MessageConstants;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.multipart.MultipartFile;

public record ProductRequest(
//        @Pattern()
        String name,
        @NotBlank
        String brand,
        @NotBlank
        String category,
        String description,

        @PositiveOrZero
        Float price,

        @PositiveOrZero
        Float minPrice,

        @PositiveOrZero
        int availability,

        MultipartFile image
) {
    @AssertTrue(message = MessageConstants.PASSWORD_MISMATCH)
    public boolean isPriceGreaterOrEqualToMinPrice() {
        return price >= minPrice;
    }

}