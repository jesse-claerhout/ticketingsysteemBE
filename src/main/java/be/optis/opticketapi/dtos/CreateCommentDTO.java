package be.optis.opticketapi.dtos;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCommentDTO {

    @NotBlank(message = "Inhoud mag niet leeg zijn")
    private String content;
}
