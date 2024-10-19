package org.fintech.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fintech.client.parser.PriceDeserializer;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "Event")
public class EventDto {
    private Long id;
    private String title;
    @JsonDeserialize(using= PriceDeserializer.class)
    private Integer price;
    private String description;
    @JsonProperty("is_free")
    private Boolean isFree;

}
